package com.agriproduct.product.controller;

import com.agriproduct.common.core.domain.PageRequest;
import com.agriproduct.common.core.domain.PageResult;
import com.agriproduct.product.dto.ProductQueryRequest;
import com.agriproduct.product.service.ProductService;
import com.agriproduct.product.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 */
@Tag(name = "商品管理")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 商品列表（分页）
     */
    @Operation(summary = "商品列表")
    @GetMapping("/list")
    public PageResult<ProductVO> getProductList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            ProductQueryRequest queryRequest) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        return productService.getProductList(pageRequest, queryRequest);
    }

    /**
     * 商品详情
     */
    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public ProductVO getProductDetail(@PathVariable Long id) {
        return productService.getProductDetail(id);
    }

    /**
     * 搜索商品
     */
    @Operation(summary = "搜索商品")
    @GetMapping("/search")
    public List<ProductVO> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }

    /**
     * 推荐商品
     */
    @Operation(summary = "推荐商品")
    @GetMapping("/recommend")
    public List<ProductVO> getRecommendProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        return productService.getRecommendProducts(limit);
    }
}
