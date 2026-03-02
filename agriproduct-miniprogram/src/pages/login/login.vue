<template>
  <view class="page-login">
    <view class="login-header">
      <image class="logo" src="/static/logo.png" mode="aspectFit" />
      <text class="title">农产品商城</text>
      <text class="subtitle">新鲜农产品 产地直供</text>
    </view>
    
    <view class="login-form">
      <view class="form-item">
        <text class="input-icon">👤</text>
        <input 
          class="input" 
          v-model="form.username" 
          placeholder="请输入用户名"
          maxlength="20"
        />
      </view>
      
      <view class="form-item">
        <text class="input-icon">🔒</text>
        <input 
          class="input" 
          v-model="form.password" 
          placeholder="请输入密码"
          :password="!showPassword"
          maxlength="20"
        />
        <text class="toggle-pwd" @click="showPassword = !showPassword">
          {{ showPassword ? '👁️' : '👁️‍🗨️' }}
        </text>
      </view>
      
      <button class="login-btn" :class="{ disabled: !canSubmit }" @click="handleLogin">
        {{ loading ? '登录中...' : '登录' }}
      </button>
      
      <view class="register-link" @click="showRegister = true">
        <text class="link-text">还没有账号？立即注册</text>
      </view>
    </view>
    
    <!-- 注册弹窗 -->
    <view class="register-popup" v-if="showRegister" @click="showRegister = false">
      <view class="popup-content" @click.stop>
        <view class="popup-header">
          <text class="popup-title">用户注册</text>
          <text class="popup-close" @click="showRegister = false">✕</text>
        </view>
        
        <view class="popup-form">
          <view class="form-item">
            <input 
              class="input" 
              v-model="registerForm.username" 
              placeholder="用户名"
              maxlength="20"
            />
          </view>
          <view class="form-item">
            <input 
              class="input" 
              v-model="registerForm.password" 
              placeholder="密码"
              password
              maxlength="20"
            />
          </view>
          <view class="form-item">
            <input 
              class="input" 
              v-model="registerForm.nickname" 
              placeholder="昵称（选填）"
              maxlength="20"
            />
          </view>
          <view class="form-item">
            <input 
              class="input" 
              v-model="registerForm.phone" 
              placeholder="手机号"
              type="number"
              maxlength="11"
            />
          </view>
          
          <button 
            class="register-btn" 
            :class="{ disabled: !canRegister }"
            @click="handleRegister"
          >
            {{ registering ? '注册中...' : '注册' }}
          </button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const form = ref({
  username: '',
  password: ''
})

const registerForm = ref({
  username: '',
  password: '',
  nickname: '',
  phone: ''
})

const loading = ref(false)
const registering = ref(false)
const showPassword = ref(false)
const showRegister = ref(false)

const canSubmit = computed(() => {
  return form.value.username && form.value.password
})

const canRegister = computed(() => {
  return registerForm.value.username && 
         registerForm.value.password && 
         /^1[3-9]\d{9}$/.test(registerForm.value.phone)
})

// 登录
async function handleLogin() {
  if (!canSubmit.value || loading.value) return
  
  loading.value = true
  try {
    await userStore.login({
      username: form.value.username,
      password: form.value.password,
      loginType: 'user'
    })
    
    uni.showToast({ title: '登录成功', icon: 'success' })
    
    setTimeout(() => {
      uni.switchTab({ url: '/pages/index/index' })
    }, 1500)
  } catch (e) {
    console.error('登录失败', e)
  } finally {
    loading.value = false
  }
}

// 注册
async function handleRegister() {
  if (!canRegister.value || registering.value) return
  
  registering.value = true
  try {
    await registerApi({
      username: registerForm.value.username,
      password: registerForm.value.password,
      nickname: registerForm.value.nickname,
      phone: registerForm.value.phone,
      registerType: 'user'
    })
    
    uni.showToast({ title: '注册成功', icon: 'success' })
    
    // 自动填充登录表单
    form.value.username = registerForm.value.username
    form.value.password = registerForm.value.password
    
    showRegister.value = false
  } catch (e) {
    console.error('注册失败', e)
  } finally {
    registering.value = false
  }
}
</script>

<style lang="scss" scoped>
.page-login {
  min-height: 100vh;
  background: linear-gradient(180deg, #4CAF50 0%, #388E3C 100%);
  padding: 0 60rpx;
}

.login-header {
  padding-top: 120rpx;
  text-align: center;
  margin-bottom: 80rpx;
  
  .logo {
    width: 160rpx;
    height: 160rpx;
    margin-bottom: 30rpx;
  }
  
  .title {
    display: block;
    font-size: 48rpx;
    color: #fff;
    font-weight: bold;
    margin-bottom: 16rpx;
  }
  
  .subtitle {
    font-size: 28rpx;
    color: rgba(255, 255, 255, 0.8);
  }
}

.login-form {
  background-color: #fff;
  border-radius: 24rpx;
  padding: 60rpx 40rpx;
  
  .form-item {
    display: flex;
    align-items: center;
    border-bottom: 1rpx solid #eee;
    padding-bottom: 24rpx;
    margin-bottom: 40rpx;
    
    .input-icon {
      font-size: 40rpx;
      margin-right: 20rpx;
    }
    
    .input {
      flex: 1;
      font-size: 32rpx;
      color: #333;
    }
    
    .toggle-pwd {
      font-size: 40rpx;
    }
  }
  
  .login-btn {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
    background-color: #4CAF50;
    color: #fff;
    border-radius: 44rpx;
    font-size: 32rpx;
    border: none;
    margin-top: 40rpx;
    
    &::after {
      border: none;
    }
    
    &.disabled {
      background-color: #ccc;
    }
  }
  
  .register-link {
    text-align: center;
    margin-top: 30rpx;
    
    .link-text {
      font-size: 28rpx;
      color: #4CAF50;
    }
  }
}

.register-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: flex;
  align-items: flex-end;
  
  .popup-content {
    width: 100%;
    background-color: #fff;
    border-radius: 24rpx 24rpx 0 0;
    padding: 40rpx 30rpx 60rpx;
  }
  
  .popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40rpx;
    
    .popup-title {
      font-size: 36rpx;
      color: #333;
      font-weight: bold;
    }
    
    .popup-close {
      font-size: 40rpx;
      color: #999;
    }
  }
  
  .popup-form {
    .form-item {
      margin-bottom: 30rpx;
      
      .input {
        width: 100%;
        height: 88rpx;
        line-height: 88rpx;
        background-color: #f5f5f5;
        border-radius: 44rpx;
        padding: 0 30rpx;
        font-size: 28rpx;
        color: #333;
      }
    }
    
    .register-btn {
      width: 100%;
      height: 88rpx;
      line-height: 88rpx;
      background-color: #4CAF50;
      color: #fff;
      border-radius: 44rpx;
      font-size: 32rpx;
      border: none;
      margin-top: 20rpx;
      
      &::after {
        border: none;
      }
      
      &.disabled {
        background-color: #ccc;
      }
    }
  }
}
</style>
