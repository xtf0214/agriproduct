<template>
  <div class="page-container">
    <div class="page-content">
      <!-- 搜索表单 -->
      <div class="search-form">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="订单号">
            <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
          </el-form-item>
          <el-form-item label="订单状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable>
              <el-option label="待付款" :value="1" />
              <el-option label="待发货" :value="2" />
              <el-option label="待收货" :value="3" />
              <el-option label="已完成" :value="4" />
              <el-option label="已取消" :value="5" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 订单列表 -->
      <el-table :data="orderList" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column label="商品" min-width="250">
          <template #default="{ row }">
            <div class="order-products">
              <div v-for="item in row.items" :key="item.id" class="product-item">
                <el-image :src="item.productImage" class="product-image" />
                <div class="product-info">
                  <div class="product-name">{{ item.productName }}</div>
                  <div class="product-price">¥{{ item.price }} x {{ item.quantity }}</div>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="订单金额" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.payAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="receiverName" label="收货人" width="100" />
        <el-table-column prop="receiverPhone" label="电话" width="130" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="orderStatusType(row.status)">
              {{ orderStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="170" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button 
              v-if="row.status === 2" 
              type="success" 
              link 
              @click="handleShip(row)"
            >
              发货
            </el-button>
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
    
    <!-- 发货弹窗 -->
    <el-dialog v-model="shipDialogVisible" title="订单发货" width="500">
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="快递公司">
          <el-select v-model="shipForm.expressCompany" placeholder="请选择">
            <el-option label="顺丰速运" value="顺丰" />
            <el-option label="中通快递" value="中通" />
            <el-option label="圆通速递" value="圆通" />
            <el-option label="申通快递" value="申通" />
            <el-option label="韵达快递" value="韵达" />
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="shipForm.expressNo" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmShip" :loading="shipLoading">确定发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrderList, shipOrder } from '@/api/order'
import { orderStatusText, orderStatusType } from '@/utils/format'
import type { Order } from '@/types/api'

const router = useRouter()
const loading = ref(false)
const orderList = ref<Order[]>([])

const searchForm = reactive({
  orderNo: '',
  status: undefined as number | undefined
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 发货相关
const shipDialogVisible = ref(false)
const shipLoading = ref(false)
const currentOrder = ref<Order | null>(null)
const shipForm = reactive({
  expressCompany: '顺丰',
  expressNo: ''
})

// 获取订单列表
const fetchOrderList = async () => {
  loading.value = true
  try {
    const res = await getOrderList({
      pageNum: pagination.page,
      pageSize: pagination.size
    })
    if (res.data) {
      orderList.value = res.data.records
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
  fetchOrderList()
}

// 重置
const handleReset = () => {
  searchForm.orderNo = ''
  searchForm.status = undefined
  handleSearch()
}

// 详情
const handleDetail = (row: Order) => {
  router.push(`/order/detail/${row.id}`)
}

// 发货
const handleShip = (row: Order) => {
  currentOrder.value = row
  shipForm.expressCompany = '顺丰'
  shipForm.expressNo = ''
  shipDialogVisible.value = true
}

// 确认发货
const confirmShip = async () => {
  if (!currentOrder.value) return
  
  shipLoading.value = true
  try {
    await shipOrder(currentOrder.value.id)
    ElMessage.success('发货成功')
    shipDialogVisible.value = false
    fetchOrderList()
  } catch (error) {
    console.error(error)
  } finally {
    shipLoading.value = false
  }
}

// 分页
const handleSizeChange = (size: number) => {
  pagination.size = size
  fetchOrderList()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  fetchOrderList()
}

onMounted(() => {
  fetchOrderList()
})
</script>

<style scoped lang="scss">
.order-products {
  .product-item {
    display: flex;
    align-items: center;
    padding: 5px 0;
    
    &:not(:last-child) {
      border-bottom: 1px solid #EBEEF5;
    }
    
    .product-image {
      width: 50px;
      height: 50px;
      border-radius: 4px;
      flex-shrink: 0;
    }
    
    .product-info {
      margin-left: 10px;
      flex: 1;
      
      .product-name {
        font-size: 14px;
        color: #303133;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .product-price {
        font-size: 12px;
        color: #909399;
        margin-top: 2px;
      }
    }
  }
}

.price {
  color: #F56C6C;
  font-weight: 600;
}
</style>
