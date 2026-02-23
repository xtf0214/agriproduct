<template>
  <div class="page-container">
    <div class="page-content">
      <el-card shadow="hover">
        <template #header>
          <span>个人中心</span>
        </template>
        
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="info">
            <el-form
              ref="infoFormRef"
              :model="userInfo"
              label-width="100px"
              class="info-form"
            >
              <el-form-item label="头像">
                <el-avatar :size="80" :src="userStore.userInfo?.avatar">
                  {{ userStore.userInfo?.nickname?.charAt(0) || '商' }}
                </el-avatar>
              </el-form-item>
              <el-form-item label="用户名">
                <el-input :value="userStore.userInfo?.username" disabled />
              </el-form-item>
              <el-form-item label="昵称">
                <el-input v-model="userInfo.nickname" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="userInfo.phone" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSaveInfo" :loading="saving">保存</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          
          <el-tab-pane label="修改密码" name="password">
            <el-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="100px"
              class="info-form"
            >
              <el-form-item label="当前密码" prop="oldPassword">
                <el-input v-model="passwordForm.oldPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="passwordForm.newPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleChangePassword" :loading="changing">修改密码</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updateUserInfo, updatePassword } from '@/api/auth'
import { getUserId } from '@/utils/auth'

const userStore = useUserStore()
const activeTab = ref('info')
const saving = ref(false)
const changing = ref(false)

const infoFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

const userInfo = reactive({
  nickname: userStore.userInfo?.nickname || '',
  phone: userStore.userInfo?.phone || ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirm = (rule: any, value: string, callback: any) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

// 保存基本信息
const handleSaveInfo = async () => {
  const userId = getUserId()
  if (!userId) return
  
  saving.value = true
  try {
    await updateUserInfo(userId, {
      nickname: userInfo.nickname,
      phone: userInfo.phone
    })
    ElMessage.success('保存成功')
    userStore.fetchUserInfo()
  } catch (error) {
    console.error(error)
  } finally {
    saving.value = false
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  const userId = getUserId()
  if (!userId) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      changing.value = true
      try {
        await updatePassword(userId, passwordForm.oldPassword, passwordForm.newPassword)
        ElMessage.success('密码修改成功，请重新登录')
        userStore.logout()
        window.location.href = '/login'
      } catch (error) {
        console.error(error)
      } finally {
        changing.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.info-form {
  max-width: 500px;
  margin-top: 20px;
}
</style>
