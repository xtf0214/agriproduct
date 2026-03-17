<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">商品审核</h2>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="商品名称">
          <el-input
            v-model="queryParams.keyword"
            placeholder="请输入商品名称"
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

    <el-table v-loading="loading" :data="productList" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="商品图片" width="100">
        <template #default="{ row }">
          <el-image
            v-if="row.mainImage"
            :src="row.mainImage"
            style="width: 60px; height: 60px"
            fit="cover"
            :preview-src-list="[row.mainImage]"
          />
          <el-icon v-else :size="40" color="#c0c4cc"><Picture /></el-icon>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" />
      <el-table-column prop="merchantName" label="商家" width="150" />
      <el-table-column prop="price" label="价格" width="100">
        <template #default="{ row }">
          ¥{{ row.price.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column prop="sales" label="销量" width="100" />
      <el-table-column label="审核状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.auditStatus === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="row.auditStatus === 1" type="success">已通过</el-tag>
          <el-tag v-else-if="row.auditStatus === 2" type="danger">已拒绝</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="上架状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="info">已下架</el-tag>
          <el-tag v-else-if="row.status === 1" type="success">已上架</el-tag>
          <el-tag v-else-if="row.status === 2" type="warning">审核中</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleViewDetail(row)">
            查看详情
          </el-button>
          <el-button
            v-if="row.auditStatus === 0"
            link
            type="success"
            size="small"
            @click="handleAudit(row, 1)"
          >
            通过
          </el-button>
          <el-button
            v-if="row.auditStatus === 0"
            link
            type="danger"
            size="small"
            @click="handleAudit(row, 2)"
          >
            拒绝
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

    <!-- Detail dialog -->
    <el-dialog v-model="detailDialogVisible" title="商品详情" width="800px">
      <el-descriptions v-if="currentProduct" :column="2" border>
        <el-descriptions-item label="商品ID">{{ currentProduct.id }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">
          {{ currentProduct.name }}
        </el-descriptions-item>
        <el-descriptions-item label="价格">
          ¥{{ currentProduct.price.toFixed(2) }}
        </el-descriptions-item>
        <el-descriptions-item label="库存">{{ currentProduct.stock }}</el-descriptions-item>
        <el-descriptions-item label="销量">{{ currentProduct.sales }}</el-descriptions-item>
        <el-descriptions-item label="商家">
          {{ currentProduct.merchantName }}
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag v-if="currentProduct.auditStatus === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="currentProduct.auditStatus === 1" type="success">已通过</el-tag>
          <el-tag v-else-if="currentProduct.auditStatus === 2" type="danger">已拒绝</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="上架状态">
          <el-tag v-if="currentProduct.status === 0" type="info">已下架</el-tag>
          <el-tag v-else-if="currentProduct.status === 1" type="success">已上架</el-tag>
          <el-tag v-else-if="currentProduct.status === 2" type="warning">审核中</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ currentProduct.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="商品描述" :span="2">
          {{ currentProduct.detail || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="商品图片" :span="2">
          <el-image
            v-if="currentProduct.mainImage"
            :src="currentProduct.mainImage"
            style="width: 200px"
            fit="contain"
            :preview-src-list="[currentProduct.mainImage]"
          />
          <span v-else>暂无图片</span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="currentProduct && currentProduct.auditStatus === 0"
          type="success"
          @click="handleAudit(currentProduct, 1)"
        >
          通过
        </el-button>
        <el-button
          v-if="currentProduct && currentProduct.auditStatus === 0"
          type="danger"
          @click="handleAudit(currentProduct, 2)"
        >
          拒绝
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAuditProductListApi, auditProductApi } from '@/api/index'
import type { AuditProductItem } from '@/api/product'

const loading = ref(false)
const productList = ref<AuditProductItem[]>([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ''
})

const detailDialogVisible = ref(false)
const currentProduct = ref<AuditProductItem | null>(null)

async function getList() {
  loading.value = true
  try {
    const res = await getAuditProductListApi(queryParams)
    if (res.code === 200 && res.data) {
      productList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.pageNum = 1
  getList()
}

function handleReset() {
  queryParams.keyword = ''
  queryParams.pageNum = 1
  getList()
}

function handleViewDetail(row: AuditProductItem) {
  currentProduct.value = row
  detailDialogVisible.value = true
}

async function handleAudit(row: AuditProductItem, auditStatus: number) {
  const text = auditStatus === 1 ? '通过' : '拒绝'
  let auditReason = ''

  if (auditStatus === 2) {
    try {
      const { value } = await ElMessageBox.prompt('请输入拒绝原因', '审核拒绝', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S/,
        inputErrorMessage: '请输入拒绝原因'
      })
      auditReason = value
    } catch {
      return
    }
  } else {
    try {
      await ElMessageBox.confirm(`确定要${text}该商品吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
    } catch {
      return
    }
  }

  try {
    const res = await auditProductApi(row.id, {
      auditStatus,
      auditReason
    })

    if (res.code === 200) {
      ElMessage.success('审核成功')
      if (detailDialogVisible.value) {
        detailDialogVisible.value = false
      }
      getList()
    }
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

onMounted(() => {
  getList()
})
</script>
