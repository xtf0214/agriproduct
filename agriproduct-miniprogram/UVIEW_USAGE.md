# uView UI 组件库使用示例

## 已安装配置

✅ **已完成的配置：**
1. 安装了 `uview-plus` (Vue 3 版本)
2. 在 `main.ts` 中注册了 uView UI
3. 配置了 easycom 自动导入组件

## 使用示例

### 1. 收货地址页面 - 使用 uView 组件改进

将原来的原生表单组件替换为 uView UI 组件：

```vue
<template>
  <view class="page-address-edit">
    <!-- 使用 uView 表单组件 -->
    <u-form :model="form" :rules="rules" ref="uFormRef">
      <u-form-item label="收货人" prop="receiverName">
        <u-input 
          v-model="form.receiverName" 
          placeholder="请输入收货人姓名"
          border="surround"
        />
      </u-form-item>
      
      <u-form-item label="手机号" prop="receiverPhone">
        <u-input 
          v-model="form.receiverPhone" 
          placeholder="请输入手机号"
          type="number"
          maxlength="11"
          border="surround"
        />
      </u-form-item>
      
      <u-form-item label="所在地区" prop="region">
        <!-- 使用 uView 地区选择器 -->
        <u-picker 
          mode="region" 
          v-model="showRegionPicker"
          :defaultRegion="defaultRegion"
          @confirm="handleRegionConfirm"
        >
          <u-input 
            :value="regionText" 
            placeholder="请选择省市区"
            border="surround"
            @click="showRegionPicker = true"
            disabled
          />
        </u-picker>
      </u-form-item>
      
      <u-form-item label="详细地址" prop="detailAddress">
        <u-textarea 
          v-model="form.detailAddress" 
          placeholder="请输入详细地址"
          :maxlength="200"
          border="surround"
        />
      </u-form-item>
      
      <u-form-item label="设为默认">
        <u-switch 
          v-model="isDefault" 
          activeColor="#4CAF50"
          @change="handleDefaultChange"
        />
      </u-form-item>
    </u-form>
    
    <!-- 使用 uView 按钮 -->
    <u-button 
      type="primary" 
      text="保存"
      :disabled="!canSubmit"
      @click="handleSave"
      customStyle="margin-top: 40rpx"
    />
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const showRegionPicker = ref(false)
const uFormRef = ref()

const form = ref({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: 0
})

// 表单验证规则
const rules = {
  receiverName: [
    { required: true, message: '请输入收货人姓名' }
  ],
  receiverPhone: [
    { required: true, message: '请输入手机号' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' }
  ],
  region: [
    { required: true, message: '请选择地区' }
  ],
  detailAddress: [
    { required: true, message: '请输入详细地址' }
  ]
}

const defaultRegion = computed(() => {
  return [form.value.province, form.value.city, form.value.district]
})

const regionText = computed(() => {
  if (form.value.province) {
    return `${form.value.province} ${form.value.city} ${form.value.district}`
  }
  return ''
})

const canSubmit = computed(() => {
  return form.value.receiverName && 
         form.value.receiverPhone && 
         form.value.province && 
         form.value.detailAddress
})

function handleRegionConfirm(value: any) {
  form.value.province = value.province
  form.value.city = value.city
  form.value.district = value.district
  showRegionPicker.value = false
}

function handleDefaultChange(value: boolean) {
  form.value.isDefault = value ? 1 : 0
}

async function handleSave() {
  // 使用 uView 表单验证
  try {
    await uFormRef.value.validate()
    // 保存逻辑...
  } catch (e) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' })
  }
}
</script>
```

### 2. 商品卡片 - 使用 uView 卡片组件

```vue
<template>
  <u-card 
    v-for="product in products" 
    :key="product.id"
    :title="product.name"
    :subTitle="product.category"
    :thumb="product.image"
    @click="handleProductClick(product.id)"
  >
    <view class="card-content">
      <text class="price">¥{{ product.price }}</text>
      <text class="sales">已售 {{ product.sales }}</text>
    </view>
  </u-card>
</template>
```

### 3. 购物车 - 使用 uView 列表组件

```vue
<template>
  <view>
    <u-swipe-action 
      v-for="item in cartItems" 
      :key="item.id"
      :options="swipeOptions"
      @click="handleSwipeClick($event, item.id)"
    >
      <view class="cart-item">
        <u-checkbox v-model="item.selected" />
        <u-image :src="item.image" width="120rpx" height="120rpx" />
        <view class="item-info">
          <text>{{ item.name }}</text>
          <text>¥{{ item.price }}</text>
        </view>
        <!-- 使用 uView 步进器 -->
        <u-number-box 
          v-model="item.quantity" 
          :min="1" 
          :max="item.stock"
          @change="handleQuantityChange(item.id, $event)"
        />
      </view>
    </u-swipe-action>
  </view>
</template>

<script setup lang="ts">
const swipeOptions = [
  { text: '删除', style: { backgroundColor: '#dd524d' } }
]

function handleSwipeClick(index: number, itemId: number) {
  if (index === 0) {
    // 删除商品
    deleteCartItem(itemId)
  }
}
</script>
```

### 4. 订单列表 - 使用 uView 标签和状态组件

```vue
<template>
  <u-card v-for="order in orders" :key="order.id">
    <template #head>
      <view class="order-header">
        <text>订单号: {{ order.orderNo }}</text>
        <!-- 订单状态标签 -->
        <u-tag 
          :text="order.statusText" 
          :type="getOrderStatusTag(order.status)"
        />
      </view>
    </template>
    
    <view class="order-products">
      <u-image 
        v-for="product in order.products" 
        :key="product.id"
        :src="product.image"
        width="160rpx"
        height="160rpx"
      />
    </view>
    
    <template #foot>
      <view class="order-footer">
        <text>合计: ¥{{ order.totalAmount }}</text>
        <u-button 
          v-if="order.status === 1"
          type="primary" 
          size="small"
          text="去支付"
          @click="handlePay(order.id)"
        />
        <u-button 
          v-if="order.status === 3"
          type="success" 
          size="small"
          text="确认收货"
          @click="handleConfirm(order.id)"
        />
      </view>
    </template>
  </u-card>
</template>

<script setup lang="ts">
function getOrderStatusTag(status: number) {
  const statusMap = {
    1: 'warning',   // 待支付
    2: 'primary',   // 待发货
    3: 'primary',   // 待收货
    4: 'success',   // 已完成
    5: 'error'      // 已取消
  }
  return statusMap[status]
}
</script>
```

### 5. 搜索页面 - 使用 uView 搜索组件

```vue
<template>
  <view>
    <!-- 搜索栏 -->
    <u-search 
      v-model="keyword"
      placeholder="搜索商品"
      :showAction="true"
      @search="handleSearch"
      @custom="handleSearch"
    />
    
    <!-- 搜索历史 -->
    <view v-if="!keyword && searchHistory.length">
      <u-divider>搜索历史</u-divider>
      <u-tag 
        v-for="(item, index) in searchHistory" 
        :key="index"
        :text="item"
        type="info"
        @click="handleHistoryClick(item)"
      />
    </view>
    
    <!-- 热门搜索 -->
    <u-grid :col="4" v-if="!keyword">
      <u-grid-item 
        v-for="(item, index) in hotSearch" 
        :key="index"
        @click="handleHotClick(item)"
      >
        <text>{{ item }}</text>
      </u-grid-item>
    </u-grid>
  </view>
</template>
```

### 6. 加载状态 - 使用 uView 加载组件

```vue
<template>
  <view>
    <!-- 页面加载 -->
    <u-loading-page 
      :loading="pageLoading"
      loading-text="加载中..."
    />
    
    <!-- 列表加载更多 -->
    <u-loadmore 
      :status="loadStatus"
      :loading-text="loadingText"
      :loadmore-text="loadmoreText"
      :nomore-text="nomoreText"
    />
    
    <!-- 空状态 -->
    <u-empty 
      v-if="!list.length"
      mode="list"
      text="暂无数据"
    />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const pageLoading = ref(true)
const loadStatus = ref('loadmore') // loadmore / loading / nomore
const loadingText = ref('努力加载中')
const loadmoreText = ref('轻轻上拉')
const nomoreText = ref('实在没有了')
</script>
```

### 7. 倒计时 - 使用 uView 倒计时组件

```vue
<template>
  <!-- 限时秒杀倒计时 -->
  <u-count-down 
    :time="seckillEndTime - Date.now()"
    format="HH:mm:ss"
    @finish="handleSeckillEnd"
  />
</template>
```

### 8. 评分和评论 - 使用 uView 评分组件

```vue
<template>
  <view>
    <u-rate 
      v-model="rating"
      :count="5"
      activeColor="#ff9900"
      @change="handleRatingChange"
    />
    <text>{{ rating }}分</text>
  </view>
</template>
```

## 常用组件快速参考

| 组件 | 说明 | 文档链接 |
|------|------|----------|
| `u-button` | 按钮 | [官方文档](https://uviewui.com/components/button.html) |
| `u-input` | 输入框 | [官方文档](https://uviewui.com/components/input.html) |
| `u-form` | 表单 | [官方文档](https://uviewui.com/components/form.html) |
| `u-picker` | 选择器 | [官方文档](https://uviewui.com/components/picker.html) |
| `u-swipe-action` | 滑动操作 | [官方文档](https://uviewui.com/components/swipeAction.html) |
| `u-number-box` | 步进器 | [官方文档](https://uviewui.com/components/numberBox.html) |
| `u-card` | 卡片 | [官方文档](https://uviewui.com/components/card.html) |
| `u-image` | 图片 | [官方文档](https://uviewui.com/components/image.html) |
| `u-tag` | 标签 | [官方文档](https://uviewui.com/components/tag.html) |
| `u-search` | 搜索框 | [官方文档](https://uviewui.com/components/search.html) |
| `u-empty` | 空状态 | [官方文档](https://uviewui.com/components/empty.html) |
| `u-loadmore` | 加载更多 | [官方文档](https://uviewui.com/components/loadmore.html) |
| `u-rate` | 评分 | [官方文档](https://uviewui.com/components/rate.html) |
| `u-count-down` | 倒计时 | [官方文档](https://uviewui.com/components/countDown.html) |

## 优势

使用 uView UI 的好处：

1. **统一的设计语言** - 所有组件风格一致
2. **更好的交互体验** - 动画、反馈更流畅
3. **更少的代码量** - 一行代码实现复杂功能
4. **完整的类型支持** - TypeScript 类型友好
5. **持续维护更新** - 活跃的社区支持
6. **跨平台一致性** - H5、小程序表现一致

## 下一步

1. 将关键页面（如地址编辑、购物车、订单）逐步替换为 uView 组件
2. 自定义主题色和样式
3. 根据实际需求查阅官方文档添加更多组件

---

**官方文档**: https://uviewui.com/
**GitHub**: https://github.com/umicro/uView2.0
