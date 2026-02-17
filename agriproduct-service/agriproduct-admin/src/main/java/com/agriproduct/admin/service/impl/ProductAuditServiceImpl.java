package com.agriproduct.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.agriproduct.admin.dto.ProductAuditRequest;
import com.agriproduct.admin.service.ProductAuditService;
import com.agriproduct.common.core.exception.BusinessException;
import com.agriproduct.product.entity.ProdProduct;
import com.agriproduct.product.mapper.ProdProductMapper;
import com.agriproduct.product.vo.ProductVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 商品审核服务实现
 */
@Service
@RequiredArgsConstructor
public class ProductAuditServiceImpl implements ProductAuditService {

    private final ProdProductMapper productMapper;

    @Override
    public IPage<ProductVO> getPendingProducts(Page<ProdProduct> page) {
        // 查询待审核的商品
        LambdaQueryWrapper<ProdProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProdProduct::getAuditStatus, 0)
                .orderByDesc(ProdProduct::getCreateTime);

        IPage<ProdProduct> productPage = productMapper.selectPage(page, wrapper);

        // 转换为VO
        IPage<ProductVO> voPage = new Page<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());
        voPage.setRecords(productPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList()));

        return voPage;
    }

    @Override
    public Boolean auditProduct(Long productId, ProductAuditRequest request) {
        ProdProduct product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 只能审核待审核的商品
        if (product.getAuditStatus() != 0) {
            throw new BusinessException("该商品已审核，不能重复审核");
        }

        // 更新审核状态
        product.setAuditStatus(request.getAuditStatus());
        product.setAuditReason(request.getAuditRemark());

        // 如果审核通过，自动上架
        if (request.getAuditStatus() == 1) {
            product.setStatus(1); // 上架
        } else {
            product.setStatus(0); // 下架
        }

        return productMapper.updateById(product) > 0;
    }

    /**
     * 转换为VO
     */
    private ProductVO convertToVO(ProdProduct product) {
        return BeanUtil.copyProperties(product, ProductVO.class);
    }
}
