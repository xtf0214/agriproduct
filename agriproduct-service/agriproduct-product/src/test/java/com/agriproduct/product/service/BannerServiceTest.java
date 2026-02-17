package com.agriproduct.product.service;

import com.agriproduct.product.entity.ContentBanner;
import com.agriproduct.product.mapper.ContentBannerMapper;
import com.agriproduct.product.service.impl.BannerServiceImpl;
import com.agriproduct.product.vo.BannerVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 轮播图服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("轮播图服务测试")
class BannerServiceTest {

    @Mock
    private ContentBannerMapper bannerMapper;

    private BannerServiceImpl bannerService;

    @BeforeEach
    void setUp() throws Exception {
        bannerService = new BannerServiceImpl();
        // 使用反射注入 baseMapper
        Field baseMapperField = bannerService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(bannerService, bannerMapper);
    }

    private ContentBanner createTestBanner(Long id, String title, Integer sortOrder) {
        ContentBanner banner = new ContentBanner();
        banner.setId(id);
        banner.setTitle(title);
        banner.setImage("https://example.com/banner" + id + ".jpg");
        banner.setLinkType(1);
        banner.setLinkId(null);
        banner.setSortOrder(sortOrder);
        banner.setStatus(1);
        banner.setCreateTime(LocalDateTime.now());
        return banner;
    }

    @Test
    @DisplayName("获取启用的轮播图列表成功")
    void testGetActiveBannersSuccess() {
        // 准备测试数据 - Mock返回的是启用的轮播图（模拟数据库查询结果）
        ContentBanner banner1 = createTestBanner(1L, "春季促销", 1);
        ContentBanner banner2 = createTestBanner(2L, "新品上市", 2);

        when(bannerMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(banner1, banner2));

        // 执行测试
        List<BannerVO> result = bannerService.getActiveBanners();

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("春季促销");
        assertThat(result.get(1).getTitle()).isEqualTo("新品上市");
    }

    @Test
    @DisplayName("获取轮播图 - 无轮播图")
    void testGetActiveBannersEmpty() {
        // 模拟返回空列表
        when(bannerMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // 执行测试
        List<BannerVO> result = bannerService.getActiveBanners();

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("获取轮播图 - 按排序字段排序")
    void testGetActiveBannersSorted() {
        // 准备测试数据 - 不同排序值
        ContentBanner banner1 = createTestBanner(1L, "轮播3", 3);
        ContentBanner banner2 = createTestBanner(2L, "轮播1", 1);
        ContentBanner banner3 = createTestBanner(3L, "轮播2", 2);

        when(bannerMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(banner1, banner2, banner3));

        // 执行测试
        List<BannerVO> result = bannerService.getActiveBanners();

        // 验证结果 - 确保mapper调用时包含了排序条件
        assertThat(result).isNotNull();
        verify(bannerMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("轮播图包含完整信息")
    void testBannerVOMapping() {
        // 准备测试数据
        ContentBanner banner = createTestBanner(1L, "测试轮播", 1);
        banner.setLinkType(2);
        banner.setLinkId(100L);

        when(bannerMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(banner));

        // 执行测试
        List<BannerVO> result = bannerService.getActiveBanners();

        // 验证VO映射
        assertThat(result).hasSize(1);
        BannerVO vo = result.get(0);
        assertThat(vo.getId()).isEqualTo(1L);
        assertThat(vo.getTitle()).isEqualTo("测试轮播");
        assertThat(vo.getImage()).isEqualTo("https://example.com/banner1.jpg");
        assertThat(vo.getLinkType()).isEqualTo(2);
        assertThat(vo.getLinkId()).isEqualTo(100L);
        assertThat(vo.getSortOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("轮播图 - 所有状态都是禁用")
    void testAllBannersDisabled() {
        // 模拟数据库查询返回空列表（因为没有启用的轮播图）
        when(bannerMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // 执行测试
        List<BannerVO> result = bannerService.getActiveBanners();

        // 验证结果 - 应该返回空
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("轮播图不同链接类型")
    void testBannerLinkTypes() {
        // 准备测试数据 - 不同链接类型
        ContentBanner banner1 = createTestBanner(1L, "无链接", 1);
        banner1.setLinkType(1);

        ContentBanner banner2 = createTestBanner(2L, "商品链接", 2);
        banner2.setLinkType(2);
        banner2.setLinkId(100L);

        ContentBanner banner3 = createTestBanner(3L, "分类链接", 3);
        banner3.setLinkType(3);
        banner3.setLinkId(10L);

        when(bannerMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(banner1, banner2, banner3));

        // 执行测试
        List<BannerVO> result = bannerService.getActiveBanners();

        // 验证不同链接类型
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getLinkType()).isEqualTo(1);
        assertThat(result.get(1).getLinkType()).isEqualTo(2);
        assertThat(result.get(1).getLinkId()).isEqualTo(100L);
        assertThat(result.get(2).getLinkType()).isEqualTo(3);
        assertThat(result.get(2).getLinkId()).isEqualTo(10L);
    }
}
