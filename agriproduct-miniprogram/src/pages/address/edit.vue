<template>
  <view class="page-address-edit">
    <view class="form-section">
      <view class="form-item">
        <text class="form-label">收货人</text>
        <input class="form-input" v-model="form.receiverName" placeholder="请输入收货人姓名" />
      </view>
      
      <view class="form-item">
        <text class="form-label">手机号</text>
        <input class="form-input" v-model="form.receiverPhone" placeholder="请输入手机号" type="number" maxlength="11" />
      </view>
      
      <picker 
        mode="region" 
        :value="regionValue" 
        @change="handleRegionChange"
      >
        <view class="form-item">
          <text class="form-label">所在地区</text>
          <view class="form-picker">
            <text class="picker-value" :class="{ placeholder: !regionText }">
              {{ regionText || '请选择省市区' }}
            </text>
            <text class="picker-arrow">›</text>
          </view>
        </view>
      </picker>
      
      <view class="form-item">
        <text class="form-label">详细地址</text>
        <textarea 
          class="form-textarea" 
          v-model="form.detailAddress" 
          placeholder="请输入详细地址，如街道、门牌号等"
          :maxlength="200"
        />
      </view>
      
      <view class="form-item switch-item">
        <text class="form-label">设为默认地址</text>
        <switch 
          :checked="form.isDefault === 1" 
          @change="handleDefaultChange"
          color="#4CAF50"
        />
      </view>
    </view>
    
    <button class="save-btn" :class="{ disabled: !canSubmit }" @click="handleSave">
      保存
    </button>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getAddressDetail, addAddress, updateAddress } from '@/api/address'
import type { AddressParams } from '@/types'

const addressId = ref(0)

const form = ref<AddressParams>({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: 0
})

const regionValue = computed(() => {
  if (!form.value.province) return []
  return [form.value.province, form.value.city, form.value.district]
})

// 地区文本
const regionText = computed(() => {
  if (form.value.province) {
    return `${form.value.province} ${form.value.city} ${form.value.district}`
  }
  return ''
})

// 是否可以提交
const canSubmit = computed(() => {
  return form.value.receiverName && 
         form.value.receiverPhone && 
         form.value.province && 
         form.value.detailAddress
})

// 获取地址详情
async function fetchAddressDetail() {
  if (!addressId.value) return
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
  }
}

// 地区选择
function handleRegionChange(e: any) {
  const value = e.detail.value
  form.value.province = value[0]
  form.value.city = value[1]
  form.value.district = value[2]
}

// 默认地址开关
function handleDefaultChange(e: any) {
  form.value.isDefault = e.detail.value ? 1 : 0
}

// 保存地址
async function handleSave() {
  if (!canSubmit.value) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' })
    return
  }
  
  // 验证手机号
  if (!/^1[3-9]\d{9}$/.test(form.value.receiverPhone)) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
    return
  }
  
  try {
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

onLoad((options) => {
  if (options?.id) {
    addressId.value = Number(options.id)
    uni.setNavigationBarTitle({ title: '编辑地址' })
    fetchAddressDetail()
  }
})

onMounted(() => {
  // 设置地区选择器初始值
  regionValue.value = []
})
</script>

<style lang="scss" scoped>
.page-address-edit {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.form-section {
  background-color: #fff;
  margin-bottom: 20rpx;
  
  .form-item {
    display: flex;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #eee;
    
    &:last-child {
      border-bottom: none;
    }
    
    .form-label {
      width: 160rpx;
      font-size: 28rpx;
      color: #333;
    }
    
    .form-input {
      flex: 1;
      font-size: 28rpx;
      color: #333;
    }
    
    .form-picker {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: space-between;
      
      .picker-value {
        font-size: 28rpx;
        color: #333;
        
        &.placeholder {
          color: #999;
        }
      }
      
      .picker-arrow {
        font-size: 36rpx;
        color: #999;
      }
    }
    
    &.switch-item {
      justify-content: space-between;
    }
  }
  
  .form-textarea {
    flex: 1;
    font-size: 28rpx;
    color: #333;
    min-height: 120rpx;
    padding: 20rpx 0;
  }
}

.save-btn {
  margin: 40rpx 30rpx;
  height: 88rpx;
  line-height: 88rpx;
  background-color: #4CAF50;
  color: #fff;
  border-radius: 44rpx;
  font-size: 32rpx;
  border: none;
  
  &::after {
    border: none;
  }
  
  &.disabled {
    background-color: #ccc;
  }
}
</style>
