package com.agriproduct.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import com.agriproduct.dto.MerchantProductRequest;
import com.agriproduct.dto.ProductQueryRequest;
import com.agriproduct.entity.ProdCategory;
import com.agriproduct.entity.ProdProduct;
import com.agriproduct.exception.BusinessException;
import com.agriproduct.mapper.ProdProductMapper;
import com.agriproduct.service.CategoryService;
import com.agriproduct.service.ProductService;
import com.agriproduct.vo.ProductVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProdProductMapper, ProdProduct> implements ProductService {

    private final CategoryService categoryService;

    @Override
    public IPage<ProductVO> getProductList(Page<ProdProduct> page, ProductQueryRequest queryRequest) {
        LambdaQueryWrapper<ProdProduct> wrapper = buildQueryWrapper(queryRequest);

        IPage<ProdProduct> resultPage = page(page, wrapper);

        List<ProductVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        Page<ProductVO> result = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        result.setRecords(voList);
        return result;
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
                .eq(ProdProduct::getStatus, 1)
                .eq(ProdProduct::getAuditStatus, 1)
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
        wrapper.eq(ProdProduct::getStatus, 1)
                .eq(ProdProduct::getAuditStatus, 1)
                .gt(ProdProduct::getStock, 0)
                .orderByDesc(ProdProduct::getSales)
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

    @Override
    public IPage<ProductVO> getMerchantProductList(Page<ProdProduct> page, Long merchantId, String keyword, Integer status) {
        LambdaQueryWrapper<ProdProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProdProduct::getMerchantId, merchantId)
                .orderByDesc(ProdProduct::getCreateTime);

        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(ProdProduct::getName, keyword)
                    .or()
                    .like(ProdProduct::getSubtitle, keyword));
        }
        if (status != null) {
            wrapper.eq(ProdProduct::getStatus, status);
        }

        IPage<ProdProduct> resultPage = page(page, wrapper);
        List<ProductVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        Page<ProductVO> result = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    public Long createMerchantProduct(Long merchantId, MerchantProductRequest request) {
        ProdProduct product = new ProdProduct();
        product.setMerchantId(merchantId);
        product.setCategoryId(request.getCategoryId());
        product.setName(request.getName());
        product.setSubtitle(request.getSubtitle());
        product.setMainImage(request.getMainImage());
        product.setImages(request.getImages());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStock(request.getStock());
        product.setDetail(request.getDetail());
        product.setSales(0);
        product.setStatus(2);
        product.setAuditStatus(0);
        product.setAuditReason(null);
        save(product);
        return product.getId();
    }

    @Override
    public Boolean updateMerchantProduct(Long productId, Long merchantId, MerchantProductRequest request) {
        ProdProduct product = getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (!merchantId.equals(product.getMerchantId())) {
            throw new BusinessException("无权操作该商品");
        }

        product.setCategoryId(request.getCategoryId());
        product.setName(request.getName());
        product.setSubtitle(request.getSubtitle());
        product.setMainImage(request.getMainImage());
        product.setImages(request.getImages());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStock(request.getStock());
        product.setDetail(request.getDetail());
        return updateById(product);
    }

    @Override
    public Boolean deleteMerchantProduct(Long productId, Long merchantId) {
        ProdProduct product = getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (!merchantId.equals(product.getMerchantId())) {
            throw new BusinessException("无权操作该商品");
        }
        return removeById(productId);
    }

    @Override
    public Boolean updateMerchantProductStock(Long productId, Long merchantId, Integer stock) {
        ProdProduct product = getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (!merchantId.equals(product.getMerchantId())) {
            throw new BusinessException("无权操作该商品");
        }
        product.setStock(stock);
        return updateById(product);
    }

    @Override
    public Boolean publishMerchantProduct(Long productId, Long merchantId) {
        ProdProduct product = getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (!merchantId.equals(product.getMerchantId())) {
            throw new BusinessException("无权操作该商品");
        }
        if (!Integer.valueOf(1).equals(product.getAuditStatus())) {
            throw new BusinessException("商品未通过审核，无法上架");
        }
        product.setStatus(1);
        return updateById(product);
    }

    @Override
    public Boolean unpublishMerchantProduct(Long productId, Long merchantId) {
        ProdProduct product = getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (!merchantId.equals(product.getMerchantId())) {
            throw new BusinessException("无权操作该商品");
        }
        product.setStatus(0);
        return updateById(product);
    }

    private LambdaQueryWrapper<ProdProduct> buildQueryWrapper(ProductQueryRequest queryRequest) {
        LambdaQueryWrapper<ProdProduct> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(ProdProduct::getStatus, 1)
                .eq(ProdProduct::getAuditStatus, 1);

        if (queryRequest != null) {
            if (queryRequest.getCategoryId() != null) {
                // 如果该分类有子分类，则查询该分类及其所有子分类的商品
                // 如果该分类没有子分类（叶子分类），则只查询该分类的商品
                if (categoryService.hasChildren(queryRequest.getCategoryId())) {
                    // 有子分类，查询该分类及其所有子分类的商品
                    List<Long> categoryIds = categoryService.getCategoryAndChildrenIds(queryRequest.getCategoryId());
                    wrapper.in(ProdProduct::getCategoryId, categoryIds);
                } else {
                    // 叶子分类，只查询该分类的商品
                    wrapper.eq(ProdProduct::getCategoryId, queryRequest.getCategoryId());
                }
            }

            if (StrUtil.isNotBlank(queryRequest.getKeyword())) {
                wrapper.and(w -> w.like(ProdProduct::getName, queryRequest.getKeyword())
                        .or()
                        .like(ProdProduct::getSubtitle, queryRequest.getKeyword()));
            }

            if (StrUtil.isNotBlank(queryRequest.getPriceSort())) {
                if ("asc".equalsIgnoreCase(queryRequest.getPriceSort())) {
                    wrapper.orderByAsc(ProdProduct::getPrice);
                } else if ("desc".equalsIgnoreCase(queryRequest.getPriceSort())) {
                    wrapper.orderByDesc(ProdProduct::getPrice);
                }
            }

            if (StrUtil.isNotBlank(queryRequest.getSalesSort())) {
                if ("asc".equalsIgnoreCase(queryRequest.getSalesSort())) {
                    wrapper.orderByAsc(ProdProduct::getSales);
                } else if ("desc".equalsIgnoreCase(queryRequest.getSalesSort())) {
                    wrapper.orderByDesc(ProdProduct::getSales);
                }
            }
        }

        wrapper.orderByDesc(ProdProduct::getSales);
        return wrapper;
    }

    private ProductVO convertToVO(ProdProduct product) {
        ProductVO vo = BeanUtil.copyProperties(product, ProductVO.class);
        if (StrUtil.isNotBlank(product.getImages())) {
            vo.setImageList(JSONUtil.toList(product.getImages(), String.class));
        }
        return vo;
    }
}
