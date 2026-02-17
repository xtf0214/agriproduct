package com.agriproduct.admin.service;

import com.agriproduct.admin.dto.ProductAuditRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.agriproduct.product.entity.ProdProduct;
import com.agriproduct.product.vo.ProductVO;

/**
 * 商品审核服务
 */
public interface ProductAuditService {

    /**
     * 获取待审核商品列表
     */
    IPage<ProductVO> getPendingProducts(Page<ProdProduct> page);

    /**
     * 审核商品
     * @param productId 商品ID
     * @param request 审核请求
     * @return 是否成功
     */
    Boolean auditProduct(Long productId, ProductAuditRequest request);
}
