<template>
  <div class="app-header">
    <div class="header-left">
      <el-icon class="fold-btn" @click="toggleSidebar">
        <Fold v-if="appStore.sidebar.opened" />
        <Expand v-else />
      </el-icon>
      <span class="logo">农产品电商管理后台</span>
    </div>
    <div class="header-right">
      <el-dropdown @command="handleCommand">
        <span class="user-info">
          <el-avatar :size="32" :src="userStore.userInfo?.avatar">
            <User />
          </el-avatar>
          <span class="username">{{ userStore.userInfo?.nickname || '管理员' }}</span>
          <el-icon class="arrow-down">
            <ArrowDown />
          </el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>
              个人信息
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

function toggleSidebar() {
  appStore.toggleSideBar()
}

async function handleCommand(command: string) {
  switch (command) {
    case 'profile':
      ElMessage.info('个人信息功能开发中')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        userStore.logout()
        router.push('/login')
        ElMessage.success('已退出登录')
      } catch {
        // User cancelled
      }
      break
  }
}
</script>

<style lang="scss" scoped>
.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 50px;
  padding: 0 20px;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
}

.fold-btn {
  font-size: 20px;
  margin-right: 15px;
  cursor: pointer;
  transition: color 0.3s;

  &:hover {
    color: var(--el-color-primary);
  }
}

.logo {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;

  .username {
    margin: 0 8px;
    font-size: 14px;
    color: #606266;
  }

  .arrow-down {
    font-size: 12px;
    color: #909399;
  }
}
</style>
