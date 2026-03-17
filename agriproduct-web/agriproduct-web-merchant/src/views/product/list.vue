<template>
  <div class="page-container">
    <div class="page-content">
      <!-- 搜索表单 -->
      <div class="search-form">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="商品名称">
            <el-input v-model="searchForm.keyword" placeholder="请输入商品名称" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable>
              <el-option label="已下架" :value="0" />
              <el-option label="已上架" :value="1" />
              <el-option label="审核中" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 操作按钮 -->
      <div class="action-bar">
        <el-button type="primary" @click="$router.push('/product/add')">
          <el-icon><Plus /></el-icon>
          添加商品
        </el-button>
      </div>
      
      <!-- 商品列表 -->
      <el-table :data="productList" v-loading="loading" stripe>
        <el-table-column label="商品信息" min-width="300">
          <template #default="{ row }">
            <div class="product-info">
              <el-image 
                :src="row.mainImage" 
                :preview-src-list="[row.mainImage]"
                fit="cover"
                class="product-image"
              />
              <div class="product-detail">
                <div class="product-name">{{ row.name }}</div>
                <div class="product-subtitle" v-if="row.subtitle">{{ row.subtitle }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.price }}</span>
            <span class="original-price" v-if="row.originalPrice">¥{{ row.originalPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ productStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getAuditStatusType(row.auditStatus)">
              {{ auditStatusText(row.auditStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button 
              v-if="row.status === 0 && row.auditStatus === 1" 
              type="success" 
              link 
              @click="handlePublish(row)"
            >
              上架
            </el-button>
            <el-button 
              v-if="row.status === 1" 
              type="warning" 
              link 
              @click="handleUnpublish(row)"
            >
              下架
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getMerchantProductList,
  deleteProduct,
  publishProduct,
  unpublishProduct
} from '@/api/product'
import { productStatusText, auditStatusText } from '@/utils/format'
import type { Product } from '@/types/api'

const router = useRouter()
const loading = ref(false)
const productList = ref<Product[]>([])

const searchForm = reactive({
  keyword: '',
  status: undefined as number | undefined
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 获取商品列表
const fetchProductList = async () => {
  loading.value = true
  try {
    const res = await getMerchantProductList({
      pageNum: pagination.page,
      pageSize: pagination.size,
      ...searchForm
    })
    if (res.data) {
      productList.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchProductList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = undefined
  handleSearch()
}

// 编辑商品
const handleEdit = (row: Product) => {
  router.push(`/product/edit/${row.id}`)
}

// 上架商品
const handlePublish = (row: Product) => {
  ElMessageBox.confirm('确定要上架该商品吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await publishProduct(row.id)
      ElMessage.success('上架成功')
      fetchProductList()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

// 下架商品
const handleUnpublish = (row: Product) => {
  ElMessageBox.confirm('确定要下架该商品吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await unpublishProduct(row.id)
      ElMessage.success('下架成功')
      fetchProductList()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

// 删除商品
const handleDelete = (row: Product) => {
  ElMessageBox.confirm('确定要删除该商品吗？删除后不可恢复！', '警告', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteProduct(row.id)
      ElMessage.success('删除成功')
      fetchProductList()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

// 分页
const handleSizeChange = (size: number) => {
  pagination.size = size
  fetchProductList()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  fetchProductList()
}

// 状态颜色
const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    0: 'info',
    1: 'success',
    2: 'warning'
  }
  return types[status] || 'info'
}

const getAuditStatusType = (status: number) => {
  const types: Record<number, string> = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return types[status] || 'info'
}

onMounted(() => {
  fetchProductList()
})
</script>

<style scoped lang="scss">
.product-info {
  display: flex;
  align-items: center;
  
  .product-image {
    width: 60px;
    height: 60px;
    border-radius: 4px;
    flex-shrink: 0;
  }
  
  .product-detail {
    margin-left: 10px;
    flex: 1;
    overflow: hidden;
    
    .product-name {
      font-weight: 500;
      color: #303133;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .product-subtitle {
      margin-top: 5px;
      font-size: 12px;
      color: #909399;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

.price {
  color: #F56C6C;
  font-weight: 600;
  
  & ~ .original-price {
    margin-left: 5px;
    font-size: 12px;
    color: #909399;
    text-decoration: line-through;
  }
}
</style>
