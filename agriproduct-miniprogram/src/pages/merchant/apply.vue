<template>
  <view class="page-merchant-apply">
    <view class="apply-container">
      <!-- 说明 -->
      <view class="apply-notice">
        <text class="notice-title">商家入驻说明</text>
        <text class="notice-text">欢迎申请成为商家，提交申请后我们将在1-3个工作日内完成审核，请耐心等待。</text>
      </view>

      <!-- 表单 -->
      <view class="apply-form">
        <!-- 店铺名称 -->
        <view class="form-item">
          <text class="item-label">店铺名称 <text class="required">*</text></text>
          <input
            class="item-input"
            v-model="formData.shopName"
            placeholder="请输入店铺名称"
            maxlength="50"
          />
        </view>

        <!-- 店铺简介 -->
        <view class="form-item">
          <text class="item-label">店铺简介 <text class="required">*</text></text>
          <textarea
            class="item-textarea"
            v-model="formData.shopDesc"
            placeholder="请输入店铺简介（10-200字）"
            maxlength="200"
          />
        </view>

        <!-- 联系人姓名 -->
        <view class="form-item">
          <text class="item-label">联系人 <text class="required">*</text></text>
          <input
            class="item-input"
            v-model="formData.contactName"
            placeholder="请输入联系人姓名"
            maxlength="20"
          />
        </view>

        <!-- 联系电话 -->
        <view class="form-item">
          <text class="item-label">联系电话 <text class="required">*</text></text>
          <input
            class="item-input"
            v-model="formData.contactPhone"
            placeholder="请输入联系电话"
            type="number"
            maxlength="11"
          />
        </view>

        <!-- 营业执照 -->
        <view class="form-item upload-item">
          <text class="item-label">营业执照 <text class="required">*</text></text>
          <view class="upload-area" @click="chooseLicense">
            <image v-if="formData.businessLicense" :src="formData.businessLicense" class="license-image" mode="aspectFill" />
            <view v-else class="upload-placeholder">
              <text class="upload-icon">+</text>
              <text class="upload-text">点击上传营业执照</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 提交按钮 -->
      <view class="action-section">
        <button class="submit-btn" @click="handleSubmit" :disabled="submitting">
          {{ submitting ? '提交中...' : '提交申请' }}
        </button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { applyMerchant, uploadLicense } from '@/api/merchant'
import type { MerchantApplyParams } from '@/types'

// 表单数据
const formData = reactive<MerchantApplyParams>({
  shopName: '',
  shopDesc: '',
  contactName: '',
  contactPhone: '',
  businessLicense: ''
})

// 提交中状态
const submitting = ref(false)

// 选择营业执照
function chooseLicense() {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const filePath = res.tempFilePaths[0]
      try {
        const result = await uploadLicense(filePath)
        formData.businessLicense = result.url
        uni.showToast({ title: '上传成功', icon: 'success' })
      } catch (e) {
        uni.showToast({ title: '上传失败', icon: 'none' })
      }
    }
  })
}

// 表单验证
function validateForm(): boolean {
  if (!formData.shopName?.trim()) {
    uni.showToast({ title: '请输入店铺名称', icon: 'none' })
    return false
  }

  if (formData.shopName.trim().length < 2 || formData.shopName.trim().length > 50) {
    uni.showToast({ title: '店铺名称长度在2-50个字符', icon: 'none' })
    return false
  }

  if (!formData.shopDesc?.trim()) {
    uni.showToast({ title: '请输入店铺简介', icon: 'none' })
    return false
  }

  if (formData.shopDesc.trim().length < 10 || formData.shopDesc.trim().length > 200) {
    uni.showToast({ title: '店铺简介长度在10-200个字符', icon: 'none' })
    return false
  }

  if (!formData.contactName?.trim()) {
    uni.showToast({ title: '请输入联系人姓名', icon: 'none' })
    return false
  }

  if (!formData.contactPhone?.trim()) {
    uni.showToast({ title: '请输入联系电话', icon: 'none' })
    return false
  }

  const phoneReg = /^1[3-9]\d{9}$/
  if (!phoneReg.test(formData.contactPhone)) {
    uni.showToast({ title: '请输入正确的手机号码', icon: 'none' })
    return false
  }

  if (!formData.businessLicense) {
    uni.showToast({ title: '请上传营业执照', icon: 'none' })
    return false
  }

  return true
}

// 提交申请
async function handleSubmit() {
  // 表单验证
  if (!validateForm()) return

  submitting.value = true
  try {
    await applyMerchant(formData)
    uni.showToast({ title: '申请提交成功，请等待审核', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (e) {
    // 错误已在请求拦截中处理
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.page-merchant-apply {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.apply-container {
  padding-bottom: 40rpx;
}

.apply-notice {
  background-color: #fff3e0;
  padding: 30rpx;
  margin-bottom: 20rpx;

  .notice-title {
    display: block;
    font-size: 30rpx;
    color: #e65100;
    font-weight: bold;
    margin-bottom: 16rpx;
  }

  .notice-text {
    display: block;
    font-size: 26rpx;
    color: #ef6c00;
    line-height: 1.6;
  }
}

.apply-form {
  background-color: #fff;

  .form-item {
    padding: 30rpx;
    border-bottom: 1rpx solid #eee;

    &:last-child {
      border-bottom: none;
    }

    .item-label {
      display: block;
      font-size: 30rpx;
      color: #333;
      margin-bottom: 20rpx;

      .required {
        color: #f44336;
      }
    }

    .item-input {
      width: 100%;
      height: 80rpx;
      padding: 0 20rpx;
      font-size: 28rpx;
      color: #333;
      background-color: #f5f5f5;
      border-radius: 8rpx;
    }

    .item-textarea {
      width: 100%;
      min-height: 160rpx;
      padding: 20rpx;
      font-size: 28rpx;
      color: #333;
      background-color: #f5f5f5;
      border-radius: 8rpx;
      line-height: 1.6;
    }

    &.upload-item {
      .upload-area {
        width: 100%;
        height: 300rpx;
        border-radius: 12rpx;
        overflow: hidden;
        background-color: #f5f5f5;

        .license-image {
          width: 100%;
          height: 100%;
        }

        .upload-placeholder {
          width: 100%;
          height: 100%;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;

          .upload-icon {
            font-size: 80rpx;
            color: #999;
            margin-bottom: 16rpx;
          }

          .upload-text {
            font-size: 26rpx;
            color: #999;
          }
        }
      }
    }
  }
}

.action-section {
  padding: 60rpx 30rpx 0;

  .submit-btn {
    width: 100%;
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

    &:disabled {
      background-color: #a5d6a7;
      color: rgba(255, 255, 255, 0.8);
    }
  }
}
</style>
