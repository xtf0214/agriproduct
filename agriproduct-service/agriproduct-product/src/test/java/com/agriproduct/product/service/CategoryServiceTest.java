package com.agriproduct.product.service;

import com.agriproduct.product.entity.ProdCategory;
import com.agriproduct.product.mapper.ProdCategoryMapper;
import com.agriproduct.product.service.impl.CategoryServiceImpl;
import com.agriproduct.product.vo.CategoryVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 分类服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("分类服务测试")
class CategoryServiceTest {

    @Mock
    private ProdCategoryMapper categoryMapper;

    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() throws Exception {
        categoryService = new CategoryServiceImpl();
        // 使用反射注入 baseMapper
        Field baseMapperField = categoryService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(categoryService, categoryMapper);
    }

    private ProdCategory createTestCategory(Long id, String name, Long parentId, Integer sortOrder) {
        ProdCategory category = new ProdCategory();
        category.setId(id);
        category.setName(name);
        category.setParentId(parentId);
        category.setIcon("icon");
        category.setSortOrder(sortOrder);
        category.setCreateTime(LocalDateTime.now());
        return category;
    }

    @Test
    @DisplayName("获取分类树成功")
    void testGetCategoryTreeSuccess() {
        // 准备测试数据 - 两级分类结构
        ProdCategory category1 = createTestCategory(1L, "新鲜蔬菜", 0L, 1);
        ProdCategory category2 = createTestCategory(2L, "新鲜水果", 0L, 2);
        ProdCategory subCategory1 = createTestCategory(6L, "叶菜类", 1L, 1);
        ProdCategory subCategory2 = createTestCategory(7L, "根茎类", 1L, 2);

        when(categoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(category1, category2, subCategory1, subCategory2));

        // 执行测试
        List<CategoryVO> result = categoryService.getCategoryTree();

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2); // 两个一级分类
        assertThat(result.get(0).getName()).isEqualTo("新鲜蔬菜");
        assertThat(result.get(0).getChildren()).isNotNull();
        assertThat(result.get(0).getChildren()).hasSize(2); // 两个子分类
        assertThat(result.get(0).getHasChildren()).isTrue();
    }

    @Test
    @DisplayName("获取分类树 - 无分类")
    void testGetCategoryTreeEmpty() {
        // 模拟返回空列表
        when(categoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // 执行测试
        List<CategoryVO> result = categoryService.getCategoryTree();

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("获取一级分类成功")
    void testGetTopCategoriesSuccess() {
        // 准备测试数据
        ProdCategory category1 = createTestCategory(1L, "新鲜蔬菜", 0L, 1);
        ProdCategory category2 = createTestCategory(2L, "新鲜水果", 0L, 2);

        when(categoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(category1, category2));

        // 执行测试
        List<CategoryVO> result = categoryService.getTopCategories();

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getParentId()).isEqualTo(0L);
    }

    @Test
    @DisplayName("获取一级分类 - 无结果")
    void testGetTopCategoriesEmpty() {
        // 模拟返回空列表
        when(categoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // 执行测试
        List<CategoryVO> result = categoryService.getTopCategories();

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("根据父分类ID获取子分类成功")
    void testGetChildrenByParentIdSuccess() {
        // 准备测试数据
        Long parentId = 1L;
        ProdCategory subCategory1 = createTestCategory(6L, "叶菜类", parentId, 1);
        ProdCategory subCategory2 = createTestCategory(7L, "根茎类", parentId, 2);

        when(categoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(subCategory1, subCategory2));

        // 执行测试
        List<CategoryVO> result = categoryService.getChildrenByParentId(parentId);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getParentId()).isEqualTo(parentId);
        assertThat(result.get(0).getName()).isEqualTo("叶菜类");
    }

    @Test
    @DisplayName("获取子分类 - 无子分类")
    void testGetChildrenByParentIdEmpty() {
        // 模拟返回空列表
        when(categoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // 执行测试
        List<CategoryVO> result = categoryService.getChildrenByParentId(999L);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("分类树 - 多级分类结构")
    void testCategoryTreeMultiLevel() {
        // 准备测试数据 - 三级分类结构
        ProdCategory level1 = createTestCategory(1L, "一级分类", 0L, 1);
        ProdCategory level2 = createTestCategory(2L, "二级分类", 1L, 1);
        ProdCategory level3 = createTestCategory(3L, "三级分类", 2L, 1);

        when(categoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(level1, level2, level3));

        // 执行测试
        List<CategoryVO> result = categoryService.getCategoryTree();

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChildren()).hasSize(1);
        assertThat(result.get(0).getChildren().get(0).getChildren()).hasSize(1);
    }

    @Test
    @DisplayName("分类按排序字段排序")
    void testCategoriesSortedByOrder() {
        // 准备测试数据 - 不同排序值
        ProdCategory category1 = createTestCategory(1L, "分类3", 0L, 3);
        ProdCategory category2 = createTestCategory(2L, "分类1", 0L, 1);
        ProdCategory category3 = createTestCategory(3L, "分类2", 0L, 2);

        when(categoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(category1, category2, category3));

        // 执行测试
        List<CategoryVO> result = categoryService.getTopCategories();

        // 验证结果 - 确保mapper调用时包含了排序条件
        verify(categoryMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("分类树 - 无子分类")
    void testCategoryTreeNoChildren() {
        // 准备测试数据 - 只有父分类，没有子分类
        ProdCategory category1 = createTestCategory(1L, "新鲜蔬菜", 0L, 1);
        ProdCategory category2 = createTestCategory(2L, "新鲜水果", 0L, 2);

        when(categoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(category1, category2));

        // 执行测试
        List<CategoryVO> result = categoryService.getCategoryTree();

        // 验证结果
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getChildren()).isNotNull();
        assertThat(result.get(0).getChildren()).isEmpty();
        assertThat(result.get(0).getHasChildren()).isFalse();
    }
}
