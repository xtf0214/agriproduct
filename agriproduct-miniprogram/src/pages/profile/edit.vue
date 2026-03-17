<template>
  <view class="page-profile-edit">
    <view class="edit-section">
      <!-- 头像编辑 -->
      <view class="edit-item avatar-item">
        <text class="item-label">头像</text>
        <view class="item-value" @click="chooseAvatar">
          <image class="avatar-img" :src="formData.avatar || '/static/default-avatar.png'" mode="aspectFill" />
          <text class="arrow">›</text>
        </view>
      </view>

      <!-- 昵称编辑 -->
      <view class="edit-item">
        <text class="item-label">昵称</text>
        <view class="item-value">
          <input
            class="item-input"
            v-model="formData.nickname"
            placeholder="请输入昵称"
            maxlength="20"
          />
        </view>
      </view>

      <!-- 手机号编辑 -->
      <view class="edit-item">
        <text class="item-label">手机号</text>
        <view class="item-value">
          <input
            class="item-input"
            v-model="formData.phone"
            placeholder="请输入手机号"
            type="number"
            maxlength="11"
          />
        </view>
      </view>
    </view>

    <!-- 保存按钮 -->
    <view class="action-section">
      <button class="save-btn" @click="handleSave" :disabled="saving">
        {{ saving ? '保存中...' : '保存' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { updateProfile, uploadAvatar } from '@/api/auth'
import type { UserUpdateParams } from '@/types'

const userStore = useUserStore()

const formData = ref<UserUpdateParams>({
  nickname: '',
  avatar: '',
  phone: ''
})

const saving = ref(false)
const uploading = ref(false)

// 加载用户信息
onMounted(() => {
  if (userStore.userInfo) {
    formData.value = {
      nickname: userStore.userInfo.nickname || '',
      avatar: userStore.userInfo.avatar || '',
      phone: userStore.userInfo.phone || ''
    }
  }
})

// 选择头像
async function chooseAvatar() {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const tempFilePath = res.tempFilePaths[0]
      uploading.value = true
      try {
        // 上传图片到服务器
        const result = await uploadAvatar(tempFilePath)
        formData.value.avatar = result.url
        uni.showToast({ title: '头像上传成功', icon: 'success' })
      } catch (error: any) {
        uni.showToast({ title: error.message || '头像上传失败', icon: 'none' })
      } finally {
        uploading.value = false
      }
    }
  })
}

// 保存
async function handleSave() {
  // 验证
  if (!formData.value.nickname?.trim()) {
    uni.showToast({ title: '请输入昵称', icon: 'none' })
    return
  }

  if (!formData.value.phone?.trim()) {
    uni.showToast({ title: '请输入手机号', icon: 'none' })
    return
  }

  const phoneReg = /^1[3-9]\d{9}$/
  if (!phoneReg.test(formData.value.phone)) {
    uni.showToast({ title: '手机号格式不正确', icon: 'none' })
    return
  }

  saving.value = true
  try {
    await updateProfile(formData.value)

    // 更新本地用户信息
    await userStore.fetchUserInfo()

    uni.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error: any) {
    uni.showToast({ title: error.message || '保存失败', icon: 'none' })
  } finally {
    saving.value = false
  }
}
</script>

<style lang="scss" scoped>
.page-profile-edit {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40rpx;
}

.edit-section {
  background-color: #fff;
  margin-top: 20rpx;

  .edit-item {
    display: flex;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #eee;

    &:last-child {
      border-bottom: none;
    }

    .item-label {
      width: 150rpx;
      font-size: 30rpx;
      color: #333;
    }

    .item-value {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: flex-end;

      .item-input {
        flex: 1;
        text-align: right;
        font-size: 28rpx;
        color: #333;
      }

      .arrow {
        font-size: 36rpx;
        color: #999;
        margin-left: 10rpx;
      }
    }

    &.avatar-item {
      .item-value {
        .avatar-img {
          width: 100rpx;
          height: 100rpx;
          border-radius: 50%;
        }
      }
    }
  }
}

.action-section {
  padding: 60rpx 30rpx;

  .save-btn {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
    background-color: #4CAF50;
    color: #fff;
    border-radius: 44rpx;
    font-size: 32rpx;
    border: none;

    &:disabled {
      background-color: #a5d6a7;
    }

    &::after {
      border: none;
    }
  }
}
</style>
