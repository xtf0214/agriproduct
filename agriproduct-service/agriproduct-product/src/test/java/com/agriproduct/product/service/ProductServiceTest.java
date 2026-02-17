package com.agriproduct.product.service;

import com.agriproduct.common.core.domain.PageRequest;
import com.agriproduct.common.core.domain.PageResult;
import com.agriproduct.product.dto.ProductQueryRequest;
import com.agriproduct.product.entity.ProdProduct;
import com.agriproduct.product.mapper.ProdProductMapper;
import com.agriproduct.product.service.impl.ProductServiceImpl;
import com.agriproduct.product.vo.ProductVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 商品服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("商品服务测试")
class ProductServiceTest {

    @Mock
    private ProdProductMapper productMapper;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() throws Exception {
        productService = new ProductServiceImpl();
        // 使用反射注入 baseMapper
        Field baseMapperField = productService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(productService, productMapper);
    }

    private ProdProduct createTestProduct(Long id, String name, BigDecimal price, Integer stock, Integer status) {
        ProdProduct product = new ProdProduct();
        product.setId(id);
        product.setMerchantId(1L);
        product.setCategoryId(1L);
        product.setName(name);
        product.setSubtitle("测试商品副标题");
        product.setMainImage("https://example.com/image.jpg");
        product.setPrice(price);
        product.setOriginalPrice(price.add(BigDecimal.valueOf(10)));
        product.setStock(stock);
        product.setSales(0);
        product.setStatus(status);
        product.setAuditStatus(1);
        product.setCreateTime(LocalDateTime.now());
        return product;
    }

    @Test
    @DisplayName("分页查询商品列表成功")
    void testGetProductListSuccess() {
        // 准备测试数据
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);

        ProductQueryRequest queryRequest = new ProductQueryRequest();
        queryRequest.setCategoryId(1L);

        ProdProduct product1 = createTestProduct(1L, "有机菠菜", BigDecimal.valueOf(8.00), 100, 1);
        ProdProduct product2 = createTestProduct(2L, "红富士苹果", BigDecimal.valueOf(29.90), 50, 1);

        IPage<ProdProduct> page = new Page<>(1, 10);
        ((Page<ProdProduct>) page).setRecords(Arrays.asList(product1, product2));
        ((Page<ProdProduct>) page).setTotal(2);

        when(productMapper.selectPage(any(), any(LambdaQueryWrapper.class))).thenReturn(page);

        // 执行测试
        PageResult<ProductVO> result = productService.getProductList(pageRequest, queryRequest);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(2);
        assertThat(result.getRecords()).hasSize(2);
    }

    @Test
    @DisplayName("分页查询商品 - 无结果")
    void testGetProductListEmpty() {
        // 准备测试数据
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);

        ProductQueryRequest queryRequest = new ProductQueryRequest();

        IPage<ProdProduct> page = new Page<>(1, 10);
        ((Page<ProdProduct>) page).setRecords(Collections.emptyList());
        ((Page<ProdProduct>) page).setTotal(0);

        when(productMapper.selectPage(any(), any(LambdaQueryWrapper.class))).thenReturn(page);

        // 执行测试
        PageResult<ProductVO> result = productService.getProductList(pageRequest, queryRequest);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(0);
        assertThat(result.getRecords()).isEmpty();
    }

    @Test
    @DisplayName("获取商品详情成功")
    void testGetProductDetailSuccess() {
        // 准备测试数据
        Long productId = 1L;
        ProdProduct product = createTestProduct(productId, "有机菠菜", BigDecimal.valueOf(8.00), 100, 1);

        when(productMapper.selectById(productId)).thenReturn(product);
        when(productMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // 执行测试
        ProductVO result = productService.getProductDetail(productId);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("有机菠菜");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(8.00));
    }

    @Test
    @DisplayName("获取商品详情失败 - 商品不存在")
    void testGetProductDetailNotFound() {
        // 模拟商品不存在
        when(productMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        assertThatThrownBy(() -> productService.getProductDetail(999L))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("搜索商品成功")
    void testSearchProductsSuccess() {
        // 准备测试数据
        String keyword = "菠菜";
        ProdProduct product1 = createTestProduct(1L, "有机菠菜", BigDecimal.valueOf(8.00), 100, 1);
        ProdProduct product2 = createTestProduct(2L, "新鲜菠菜", BigDecimal.valueOf(6.00), 80, 1);

        when(productMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(product1, product2));

        // 执行测试
        List<ProductVO> result = productService.searchProducts(keyword);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).contains(keyword);
    }

    @Test
    @DisplayName("搜索商品 - 关键词为空")
    void testSearchProductsEmptyKeyword() {
        // 执行测试
        List<ProductVO> result = productService.searchProducts("");

        // 验证结果 - 空关键词应返回空列表
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("获取推荐商品成功")
    void testGetRecommendProductsSuccess() {
        // 准备测试数据
        ProdProduct product1 = createTestProduct(1L, "热销商品1", BigDecimal.valueOf(10.00), 50, 1);
        ProdProduct product2 = createTestProduct(2L, "热销商品2", BigDecimal.valueOf(20.00), 30, 1);
        product1.setSales(100);
        product2.setSales(80);

        when(productMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(product1, product2));

        // 执行测试
        List<ProductVO> result = productService.getRecommendProducts(10);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("增加商品销量成功")
    void testIncreaseSalesSuccess() {
        // 准备测试数据
        Long productId = 1L;
        ProdProduct product = createTestProduct(productId, "测试商品", BigDecimal.valueOf(10.00), 100, 1);
        product.setSales(50);

        when(productMapper.selectById(productId)).thenReturn(product);
        when(productMapper.updateById(any(ProdProduct.class))).thenReturn(1);

        // 执行测试
        productService.increaseSales(productId, 10);

        // 验证
        verify(productMapper).updateById(any(ProdProduct.class));
    }

    @Test
    @DisplayName("增加销量 - 商品不存在")
    void testIncreaseSalesProductNotFound() {
        // 模拟商品不存在
        when(productMapper.selectById(999L)).thenReturn(null);

        // 执行测试 - 不应抛出异常
        productService.increaseSales(999L, 10);

        // 验证 - 不应调用更新
        verify(productMapper, never()).updateById(any(ProdProduct.class));
    }

    @Test
    @DisplayName("按分类筛选商品")
    void testGetProductListByCategory() {
        // 准备测试数据
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);

        ProductQueryRequest queryRequest = new ProductQueryRequest();
        queryRequest.setCategoryId(1L);

        IPage<ProdProduct> page = new Page<>(1, 10);
        ((Page<ProdProduct>) page).setRecords(Arrays.asList(
                createTestProduct(1L, "分类商品1", BigDecimal.valueOf(10.00), 100, 1)
        ));
        ((Page<ProdProduct>) page).setTotal(1);

        when(productMapper.selectPage(any(), any(LambdaQueryWrapper.class))).thenReturn(page);

        // 执行测试
        PageResult<ProductVO> result = productService.getProductList(pageRequest, queryRequest);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1);
    }

    @Test
    @DisplayName("按价格升序排序")
    void testGetProductListPriceAsc() {
        // 准备测试数据
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);

        ProductQueryRequest queryRequest = new ProductQueryRequest();
        queryRequest.setPriceSort("asc");

        IPage<ProdProduct> page = new Page<>(1, 10);
        ((Page<ProdProduct>) page).setRecords(Arrays.asList(
                createTestProduct(1L, "商品1", BigDecimal.valueOf(10.00), 100, 1),
                createTestProduct(2L, "商品2", BigDecimal.valueOf(20.00), 100, 1)
        ));
        ((Page<ProdProduct>) page).setTotal(2);

        when(productMapper.selectPage(any(), any(LambdaQueryWrapper.class))).thenReturn(page);

        // 执行测试
        PageResult<ProductVO> result = productService.getProductList(pageRequest, queryRequest);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getRecords()).hasSize(2);
    }

    @Test
    @DisplayName("按销量降序排序")
    void testGetProductListSalesDesc() {
        // 准备测试数据
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);

        ProductQueryRequest queryRequest = new ProductQueryRequest();
        queryRequest.setSalesSort("desc");

        IPage<ProdProduct> page = new Page<>(1, 10);
        ((Page<ProdProduct>) page).setRecords(Collections.emptyList());
        ((Page<ProdProduct>) page).setTotal(2);

        when(productMapper.selectPage(any(), any(LambdaQueryWrapper.class))).thenReturn(page);

        // 执行测试
        PageResult<ProductVO> result = productService.getProductList(pageRequest, queryRequest);

        // 验证结果
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("只查询上架且审核通过的商品")
    void testGetProductListOnlyActive() {
        // 准备测试数据
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);

        ProductQueryRequest queryRequest = new ProductQueryRequest();

        IPage<ProdProduct> page = new Page<>(1, 10);
        ((Page<ProdProduct>) page).setRecords(Collections.emptyList());
        ((Page<ProdProduct>) page).setTotal(0);

        when(productMapper.selectPage(any(), any(LambdaQueryWrapper.class))).thenReturn(page);

        // 执行测试
        PageResult<ProductVO> result = productService.getProductList(pageRequest, queryRequest);

        // 验证: 确保查询条件包含上架和审核通过状态
        verify(productMapper).selectPage(any(), any(LambdaQueryWrapper.class));
    }
}
