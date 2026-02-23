package com.agriproduct.service;

import com.agriproduct.dto.MerchantProductRequest;
import com.agriproduct.dto.ProductQueryRequest;
import com.agriproduct.entity.ProdProduct;
import com.agriproduct.vo.ProductVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService extends IService<ProdProduct> {

    IPage<ProductVO> getProductList(Page<ProdProduct> page, ProductQueryRequest queryRequest);

    ProductVO getProductDetail(Long id);

    List<ProductVO> searchProducts(String keyword);

    List<ProductVO> getRecommendProducts(Integer limit);

    void increaseSales(Long productId, Integer quantity);

    IPage<ProductVO> getMerchantProductList(Page<ProdProduct> page, Long merchantId, String keyword, Integer status);

    Long createMerchantProduct(Long merchantId, MerchantProductRequest request);

    Boolean updateMerchantProduct(Long productId, Long merchantId, MerchantProductRequest request);

    Boolean deleteMerchantProduct(Long productId, Long merchantId);

    Boolean updateMerchantProductStock(Long productId, Long merchantId, Integer stock);

    Boolean publishMerchantProduct(Long productId, Long merchantId);

    Boolean unpublishMerchantProduct(Long productId, Long merchantId);
}
