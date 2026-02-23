package com.agriproduct.controller;

import com.agriproduct.domain.Result;
import com.agriproduct.dto.ProductQueryRequest;
import com.agriproduct.entity.ProdProduct;
import com.agriproduct.service.ProductService;
import com.agriproduct.vo.ProductVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    @Operation(summary = "分页查询商品列表")
    @GetMapping("/list")
    public Result<IPage<ProductVO>> getProductList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            ProductQueryRequest queryRequest) {
        Page<ProdProduct> page = new Page<>(pageNum, pageSize);
        IPage<ProductVO> result = productService.getProductList(page, queryRequest);
        return Result.success(result);
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public Result<ProductVO> getProductDetail(@PathVariable Long id) {
        ProductVO product = productService.getProductDetail(id);
        return Result.success(product);
    }

    @Operation(summary = "搜索商品")
    @GetMapping("/search")
    public Result<List<ProductVO>> searchProducts(@RequestParam String keyword) {
        List<ProductVO> products = productService.searchProducts(keyword);
        return Result.success(products);
    }
}
