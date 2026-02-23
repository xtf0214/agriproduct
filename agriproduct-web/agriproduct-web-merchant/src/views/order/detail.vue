<template>
  <div class="page-container">
    <div class="page-content">
      <div class="page-header">
        <el-page-header @back="$router.back()">
          <template #content>
            <span class="page-title">订单详情</span>
          </template>
        </el-page-header>
      </div>
      
      <div v-loading="loading" class="order-detail">
        <!-- 订单状态 -->
        <el-card shadow="never" class="status-card">
          <div class="status-info">
            <el-icon :size="48" :color="statusColor">
              <component :is="statusIcon" />
            </el-icon>
            <div class="status-text">
              <div class="status-title">{{ orderStatusText(order?.status || 0) }}</div>
              <div class="status-desc">{{ statusDesc }}</div>
            </div>
          </div>
        </el-card>
        
        <!-- 收货信息 -->
        <el-card shadow="never" class="info-card">
          <template #header>
            <span class="card-title">收货信息</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="收货人">{{ order?.receiverName }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ order?.receiverPhone }}</el-descriptions-item>
            <el-descriptions-item label="收货地址" :span="2">{{ order?.receiverAddress }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ order?.remark || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
        
        <!-- 订单信息 -->
        <el-card shadow="never" class="info-card">
          <template #header>
            <span class="card-title">订单信息</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">{{ order?.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="orderStatusType(order?.status || 0)">
                {{ orderStatusText(order?.status || 0) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="下单时间">{{ order?.createTime }}</el-descriptions-item>
            <el-descriptions-item label="支付时间">{{ order?.payTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="发货时间">{{ order?.shipTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="完成时间">{{ order?.finishTime || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
        
        <!-- 商品信息 -->
        <el-card shadow="never" class="info-card">
          <template #header>
            <span class="card-title">商品信息</span>
          </template>
          <el-table :data="order?.items" stripe>
            <el-table-column label="商品" min-width="300">
              <template #default="{ row }">
                <div class="product-info">
                  <el-image :src="row.productImage" class="product-image" />
                  <span class="product-name">{{ row.productName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="price" label="单价" width="120">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100" />
            <el-table-column prop="totalAmount" label="小计" width="120">
              <template #default="{ row }">
                <span class="price">¥{{ row.totalAmount }}</span>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="order-summary">
            <div class="summary-item">
              <span>商品总数：</span>
              <span>{{ itemCount }} 件</span>
            </div>
            <div class="summary-item">
              <span>订单总额：</span>
              <span class="price">¥{{ order?.totalAmount }}</span>
            </div>
            <div class="summary-item">
              <span>实付金额：</span>
              <span class="price pay-amount">¥{{ order?.payAmount }}</span>
            </div>
          </div>
        </el-card>
        
        <!-- 操作 -->
        <div class="action-bar" v-if="order?.status === 2">
          <el-button type="primary" @click="handleShip">确认发货</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, shipOrder } from '@/api/order'
import { orderStatusText, orderStatusType } from '@/utils/format'
import type { Order } from '@/types/api'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const order = ref<Order | null>(null)

const orderId = computed(() => Number(route.params.id))

const itemCount = computed(() => {
  return order.value?.items.reduce((sum, item) => sum + item.quantity, 0) || 0
})

// 状态图标
const statusIcon = computed(() => {
  const icons: Record<number, string> = {
    1: 'Clock',
    2: 'Box',
    3: 'Van',
    4: 'CircleCheck',
    5: 'CircleClose'
  }
  return icons[order.value?.status || 1] || 'Clock'
})

// 状态颜色
const statusColor = computed(() => {
  const colors: Record<number, string> = {
    1: '#E6A23C',
    2: '#409EFF',
    3: '#909399',
    4: '#67C23A',
    5: '#F56C6C'
  }
  return colors[order.value?.status || 1] || '#909399'
})

// 状态描述
const statusDesc = computed(() => {
  const descs: Record<number, string> = {
    1: '等待买家付款',
    2: '买家已付款，请尽快发货',
    3: '商品已发货，等待买家确认收货',
    4: '订单已完成',
    5: '订单已取消'
  }
  return descs[order.value?.status || 1] || ''
})

// 获取订单详情
const fetchOrderDetail = async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(orderId.value)
    if (res.data) {
      order.value = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 发货
const handleShip = () => {
  ElMessageBox.prompt('请输入快递单号', '确认发货', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /\S+/,
    inputErrorMessage: '请输入快递单号'
  }).then(async ({ value }) => {
    try {
      await shipOrder(orderId.value, '顺丰', value)
      ElMessage.success('发货成功')
      fetchOrderDetail()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchOrderDetail()
})
</script>

<style scoped lang="scss">
.order-detail {
  max-width: 1000px;
  
  .status-card {
    margin-bottom: 20px;
    
    .status-info {
      display: flex;
      align-items: center;
      
      .status-text {
        margin-left: 20px;
        
        .status-title {
          font-size: 20px;
          font-weight: 600;
          color: #303133;
        }
        
        .status-desc {
          margin-top: 5px;
          color: #909399;
        }
      }
    }
  }
  
  .info-card {
    margin-bottom: 20px;
    
    .card-title {
      font-weight: 600;
    }
    
    .product-info {
      display: flex;
      align-items: center;
      
      .product-image {
        width: 60px;
        height: 60px;
        border-radius: 4px;
        flex-shrink: 0;
      }
      
      .product-name {
        margin-left: 10px;
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
  
  .order-summary {
    margin-top: 20px;
    text-align: right;
    padding: 15px;
    background: #F5F7FA;
    border-radius: 4px;
    
    .summary-item {
      margin-bottom: 10px;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
  }
  
  .price {
    color: #F56C6C;
    font-weight: 600;
    
    &.pay-amount {
      font-size: 18px;
    }
  }
  
  .action-bar {
    text-align: center;
    padding: 20px 0;
  }
}
</style>
