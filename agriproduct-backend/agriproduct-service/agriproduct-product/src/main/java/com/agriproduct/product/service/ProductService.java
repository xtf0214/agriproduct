package com.agriproduct.product.service;

import com.agriproduct.common.core.domain.PageRequest;
import com.agriproduct.common.core.domain.PageResult;
import com.agriproduct.product.dto.ProductQueryRequest;
import com.agriproduct.product.entity.ProdProduct;
import com.agriproduct.product.vo.ProductVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService extends IService<ProdProduct> {

    /**
     * 分页查询商品列表
     */
    PageResult<ProductVO> getProductList(PageRequest pageRequest, ProductQueryRequest queryRequest);

    /**
     * 获取商品详情
     */
    ProductVO getProductDetail(Long id);

    /**
     * 搜索商品
     */
    List<ProductVO> searchProducts(String keyword);

    /**
     * 获取推荐商品
     */
    List<ProductVO> getRecommendProducts(Integer limit);

    /**
     * 增加商品销量
     */
    void increaseSales(Long productId, Integer quantity);
}
