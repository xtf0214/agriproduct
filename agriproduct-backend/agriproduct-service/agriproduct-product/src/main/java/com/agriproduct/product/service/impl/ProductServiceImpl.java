package com.agriproduct.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.agriproduct.common.core.domain.PageRequest;
import com.agriproduct.common.core.domain.PageResult;
import com.agriproduct.common.core.exception.BusinessException;
import com.agriproduct.product.dto.ProductQueryRequest;
import com.agriproduct.product.entity.ProdProduct;
import com.agriproduct.product.mapper.ProdProductMapper;
import com.agriproduct.product.service.ProductService;
import com.agriproduct.product.vo.ProductVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务实现
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProdProductMapper, ProdProduct> implements ProductService {

    @Override
    public PageResult<ProductVO> getProductList(PageRequest pageRequest, ProductQueryRequest queryRequest) {
        // 构建查询条件
        LambdaQueryWrapper<ProdProduct> wrapper = buildQueryWrapper(queryRequest);

        // 分页查询
        IPage<ProdProduct> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        IPage<ProdProduct> resultPage = page(page, wrapper);

        // 转换为VO
        List<ProductVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建分页结果
        return new PageResult<>(
                resultPage.getCurrent(),
                resultPage.getSize(),
                resultPage.getTotal(),
                voList
        );
    }

    @Override
    public ProductVO getProductDetail(Long id) {
        ProdProduct product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return convertToVO(product);
    }

    @Override
    public List<ProductVO> searchProducts(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return List.of();
        }

        LambdaQueryWrapper<ProdProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ProdProduct::getName, keyword)
                .or()
                .like(ProdProduct::getSubtitle, keyword)
                .eq(ProdProduct::getStatus, 1)  // 只查询上架商品
                .eq(ProdProduct::getAuditStatus, 1)  // 只查询审核通过的商品
                .orderByDesc(ProdProduct::getSales)
                .last("LIMIT 50");

        return list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductVO> getRecommendProducts(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        LambdaQueryWrapper<ProdProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProdProduct::getStatus, 1)  // 上架
                .eq(ProdProduct::getAuditStatus, 1)  // 审核通过
                .gt(ProdProduct::getStock, 0)  // 有库存
                .orderByDesc(ProdProduct::getSales)  // 按销量排序
                .last("LIMIT " + limit);

        return list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void increaseSales(Long productId, Integer quantity) {
        ProdProduct product = getById(productId);
        if (product != null) {
            product.setSales(product.getSales() + quantity);
            updateById(product);
        }
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<ProdProduct> buildQueryWrapper(ProductQueryRequest queryRequest) {
        LambdaQueryWrapper<ProdProduct> wrapper = new LambdaQueryWrapper<>();

        // 只查询上架且审核通过的商品
        wrapper.eq(ProdProduct::getStatus, 1)
                .eq(ProdProduct::getAuditStatus, 1);

        // 分类筛选
        if (queryRequest.getCategoryId() != null) {
            wrapper.eq(ProdProduct::getCategoryId, queryRequest.getCategoryId());
        }

        // 关键词搜索
        if (StrUtil.isNotBlank(queryRequest.getKeyword())) {
            wrapper.and(w -> w.like(ProdProduct::getName, queryRequest.getKeyword())
                    .or()
                    .like(ProdProduct::getSubtitle, queryRequest.getKeyword()));
        }

        // 价格排序
        if (StrUtil.isNotBlank(queryRequest.getPriceSort())) {
            if ("asc".equalsIgnoreCase(queryRequest.getPriceSort())) {
                wrapper.orderByAsc(ProdProduct::getPrice);
            } else if ("desc".equalsIgnoreCase(queryRequest.getPriceSort())) {
                wrapper.orderByDesc(ProdProduct::getPrice);
            }
        }

        // 销量排序
        if (StrUtil.isNotBlank(queryRequest.getSalesSort())) {
            if ("asc".equalsIgnoreCase(queryRequest.getSalesSort())) {
                wrapper.orderByAsc(ProdProduct::getSales);
            } else if ("desc".equalsIgnoreCase(queryRequest.getSalesSort())) {
                wrapper.orderByDesc(ProdProduct::getSales);
            }
        }

        // 默认按销量降序
        if (StrUtil.isBlank(queryRequest.getPriceSort()) && StrUtil.isBlank(queryRequest.getSalesSort())) {
            wrapper.orderByDesc(ProdProduct::getSales);
        }

        return wrapper;
    }

    /**
     * 转换为VO
     */
    private ProductVO convertToVO(ProdProduct product) {
        ProductVO vo = BeanUtil.copyProperties(product, ProductVO.class);

        // 解析图片JSON
        if (StrUtil.isNotBlank(product.getImages())) {
            vo.setImageList(JSONUtil.toList(product.getImages(), String.class));
        }

        // 设置是否有库存
        vo.setInStock(product.getStock() > 0);

        return vo;
    }
}
