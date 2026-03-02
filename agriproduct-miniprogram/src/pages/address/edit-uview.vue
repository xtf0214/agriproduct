<template>
  <view class="page-address-edit">
    <!-- 使用 uView 表单组件 -->
    <u--form :model="form" :rules="rules" ref="uForm">
      <u-form-item 
        label="收货人" 
        prop="receiverName"
        borderBottom
        required
      >
        <u--input 
          v-model="form.receiverName" 
          placeholder="请输入收货人姓名"
          border="none"
        />
      </u-form-item>
      
      <u-form-item 
        label="手机号" 
        prop="receiverPhone"
        borderBottom
        required
      >
        <u--input 
          v-model="form.receiverPhone" 
          placeholder="请输入手机号"
          type="number"
          maxlength="11"
          border="none"
        />
      </u-form-item>
      
      <u-form-item 
        label="所在地区" 
        prop="region"
        borderBottom
        required
        @click="showRegionPicker = true"
      >
        <u--input 
          :value="regionText" 
          placeholder="请选择省市区"
          border="none"
          disabled
          disabledColor="#ffffff"
        />
        <template #right>
          <u-icon name="arrow-right" />
        </template>
      </u-form-item>
      
      <u-form-item 
        label="详细地址" 
        prop="detailAddress"
        borderBottom
        required
      >
        <u--textarea 
          v-model="form.detailAddress" 
          placeholder="请输入详细地址，如街道、门牌号等"
          :maxlength="200"
          border="none"
          autoHeight
        />
      </u-form-item>
      
      <u-form-item 
        label="设为默认地址"
        borderBottom
      >
        <u-switch 
          v-model="isDefault" 
          activeColor="#4CAF50"
          size="22"
        />
      </u-form-item>
    </u--form>
    
    <!-- 保存按钮 -->
    <view class="save-btn-wrapper">
      <u-button 
        type="primary" 
        text="保存"
        :disabled="!canSubmit"
        @click="handleSave"
        shape="circle"
        customStyle="margin-top: 40rpx"
      />
    </view>
    
    <!-- 地区选择器 -->
    <u-picker 
      mode="region"
      :show="showRegionPicker"
      :defaultRegion="defaultRegion"
      @confirm="handleRegionConfirm"
      @cancel="showRegionPicker = false"
      :columns="3"
      keyName="name"
    />
    
    <!-- 加载提示 -->
    <u-loading-page :loading="pageLoading" loading-text="加载中..." />
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getAddressDetail, addAddress, updateAddress } from '@/api/address'
import type { AddressParams } from '@/types'

const addressId = ref(0)
const showRegionPicker = ref(false)
const pageLoading = ref(false)
const uForm = ref()

const form = ref<AddressParams>({
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
    { required: true, message: '请输入收货人姓名', trigger: ['blur', 'change'] }
  ],
  receiverPhone: [
    { required: true, message: '请输入手机号', trigger: ['blur', 'change'] },
    { 
      pattern: /^1[3-9]\d{9}$/, 
      message: '手机号格式不正确',
      trigger: ['blur', 'change']
    }
  ],
  region: [
    { 
      validator: (rule: any, value: any, callback: any) => {
        if (!form.value.province) {
          callback(new Error('请选择地区'))
        } else {
          callback()
        }
      },
      trigger: ['blur', 'change']
    }
  ],
  detailAddress: [
    { required: true, message: '请输入详细地址', trigger: ['blur', 'change'] }
  ]
}

const isDefault = computed({
  get: () => form.value.isDefault === 1,
  set: (val: boolean) => {
    form.value.isDefault = val ? 1 : 0
  }
})

const defaultRegion = computed(() => {
  if (!form.value.province) return []
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

// 页面加载
onLoad((options: any) => {
  if (options.id) {
    addressId.value = parseInt(options.id)
    fetchAddressDetail()
  }
})

// 获取地址详情
async function fetchAddressDetail() {
  if (!addressId.value) return
  
  pageLoading.value = true
  try {
    const detail = await getAddressDetail(addressId.value)
    form.value = {
      receiverName: detail.receiverName,
      receiverPhone: detail.receiverPhone,
      province: detail.province,
      city: detail.city,
      district: detail.district,
      detailAddress: detail.detailAddress,
      isDefault: detail.isDefault
    }
  } catch (e) {
    console.error('获取地址详情失败', e)
    uni.showToast({ title: '获取地址失败', icon: 'none' })
  } finally {
    pageLoading.value = false
  }
}

// 地区选择确认
function handleRegionConfirm(value: any) {
  console.log('选择的地区:', value)
  form.value.province = value.value[0].name
  form.value.city = value.value[1].name
  form.value.district = value.value[2].name
  showRegionPicker.value = false
  
  // 触发表单验证
  uForm.value?.validateField('region')
}

// 保存地址
async function handleSave() {
  if (!canSubmit.value) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' })
    return
  }
  
  try {
    // 使用 uView 表单验证
    await uForm.value.validate()
    
    // 验证手机号
    if (!/^1[3-9]\d{9}$/.test(form.value.receiverPhone)) {
      uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }
    
    if (addressId.value) {
      await updateAddress(addressId.value, form.value)
    } else {
      await addAddress(form.value)
    }
    
    uni.showToast({ title: '保存成功', icon: 'success' })
    uni.$emit('addressUpdated')
    
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (e) {
    console.error('保存地址失败', e)
    uni.showToast({ title: '保存失败', icon: 'none' })
  }
}
</script>

<style lang="scss" scoped>
.page-address-edit {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
  
  :deep(.u-form-item) {
    background-color: #ffffff;
    padding: 24rpx 32rpx;
  }
  
  :deep(.u-form-item__body__left__content__label) {
    font-size: 28rpx;
    color: #333;
  }
  
  :deep(.u-form-item__body__right__content__slot) {
    font-size: 28rpx;
  }
}

.save-btn-wrapper {
  padding: 40rpx 32rpx;
}
</style>
