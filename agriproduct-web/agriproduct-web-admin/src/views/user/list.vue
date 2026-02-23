<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="用户名">
          <el-input
            v-model="queryParams.username"
            placeholder="请输入用户名"
            clearable
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="queryParams.phone"
            placeholder="请输入手机号"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table v-loading="loading" :data="userList" style="width: 100%">
      <el-table-column prop="userId" label="ID" width="80" />
      <el-table-column label="头像" width="80">
        <template #default="{ row }">
          <el-avatar :size="40" :src="row.avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 1" type="success">正常</el-tag>
          <el-tag v-else type="danger">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 1"
            link
            type="danger"
            size="small"
            @click="handleUpdateStatus(row, 0)"
          >
            禁用
          </el-button>
          <el-button
            v-else
            link
            type="primary"
            size="small"
            @click="handleUpdateStatus(row, 1)"
          >
            启用
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserListApi, updateUserStatusApi } from '@/api/index'
import type { UserItem } from '@/api/user'

const loading = ref(false)
const userList = ref<UserItem[]>([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  phone: ''
})

async function getList() {
  loading.value = true
  try {
    const res = await getUserListApi(queryParams)
    if (res.code === 200 && res.data) {
      userList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.pageNum = 1
  getList()
}

function handleReset() {
  queryParams.username = ''
  queryParams.phone = ''
  queryParams.pageNum = 1
  getList()
}

async function handleUpdateStatus(row: UserItem, status: number) {
  const text = status === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确定要${text}该用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await updateUserStatusApi(row.userId, status)
    if (res.code === 200) {
      ElMessage.success(`${text}成功`)
      getList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${text}失败`)
    }
  }
}

onMounted(() => {
  getList()
})
</script>
