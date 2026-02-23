<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">商家管理</h2>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="店铺名称">
          <el-input
            v-model="queryParams.shopName"
            placeholder="请输入店铺名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="正常" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
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

    <el-table v-loading="loading" :data="merchantList" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="shopName" label="店铺名称" />
      <el-table-column prop="shopDesc" label="店铺描述" show-overflow-tooltip />
      <el-table-column prop="contactName" label="联系人" />
      <el-table-column prop="contactPhone" label="联系电话" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 1" type="success">正常</el-tag>
          <el-tag v-else-if="row.status === 0" type="warning">待审核</el-tag>
          <el-tag v-else type="danger">已拒绝</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 0"
            link
            type="primary"
            size="small"
            @click="handleAudit(row)"
          >
            审核
          </el-button>
          <el-button link type="info" size="small">查看详情</el-button>
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

    <!-- Audit dialog -->
    <el-dialog v-model="auditDialogVisible" title="商家审核" width="500px">
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="店铺名称">
          <el-input v-model="auditForm.shopName" disabled />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="auditForm.contactName" disabled />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="auditForm.contactPhone" disabled />
        </el-form-item>
        <el-form-item label="营业执照">
          <el-image
            v-if="auditForm.businessLicense"
            :src="auditForm.businessLicense"
            style="width: 100%; max-height: 200px"
            fit="contain"
            :preview-src-list="[auditForm.businessLicense]"
          />
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.auditStatus">
            <el-radio :value="1">通过</el-radio>
            <el-radio :value="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input
            v-model="auditForm.auditRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入审核备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="auditLoading" @click="handleAuditSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMerchantListApi, auditMerchantApi } from '@/api/index'
import type { MerchantItem } from '@/api/merchant'

const loading = ref(false)
const merchantList = ref<MerchantItem[]>([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  shopName: '',
  status: undefined as number | undefined
})

const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const currentAuditMerchant = ref<MerchantItem | null>(null)

const auditForm = reactive({
  shopName: '',
  contactName: '',
  contactPhone: '',
  businessLicense: '',
  auditStatus: 1,
  auditRemark: ''
})

async function getList() {
  loading.value = true
  try {
    const res = await getMerchantListApi(queryParams)
    if (res.code === 200 && res.data) {
      merchantList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取商家列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.pageNum = 1
  getList()
}

function handleReset() {
  queryParams.shopName = ''
  queryParams.status = undefined
  queryParams.pageNum = 1
  getList()
}

function handleAudit(row: MerchantItem) {
  currentAuditMerchant.value = row
  auditForm.shopName = row.shopName
  auditForm.contactName = row.contactName || ''
  auditForm.contactPhone = row.contactPhone
  auditForm.businessLicense = row.businessLicense || ''
  auditForm.auditStatus = 1
  auditForm.auditRemark = ''
  auditDialogVisible.value = true
}

async function handleAuditSubmit() {
  if (!currentAuditMerchant.value) return

  auditLoading.value = true
  try {
    const res = await auditMerchantApi(currentAuditMerchant.value.id, {
      auditStatus: auditForm.auditStatus,
      auditRemark: auditForm.auditRemark
    })

    if (res.code === 200) {
      ElMessage.success('审核成功')
      auditDialogVisible.value = false
      getList()
    }
  } catch (error) {
    ElMessage.error('审核失败')
  } finally {
    auditLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
