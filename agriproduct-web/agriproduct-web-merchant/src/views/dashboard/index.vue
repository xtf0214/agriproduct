<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in statisticsCards" :key="item.key">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: item.color }">
              <el-icon :size="28">
                <component :is="item.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-label">{{ item.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="mt-20">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>销售趋势</span>
            </div>
          </template>
          <div class="chart-container" v-loading="chartLoading">
            <div ref="chartRef" style="height: 300px;"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>商品销量排行</span>
            </div>
          </template>
          <div class="rank-list" v-loading="rankLoading">
            <div 
              v-for="(item, index) in productRank" 
              :key="item.productId"
              class="rank-item"
            >
              <div class="rank-num" :class="{ top: index < 3 }">{{ index + 1 }}</div>
              <div class="rank-info">
                <div class="rank-name">{{ item.productName }}</div>
                <div class="rank-sales">销量: {{ item.salesCount }}</div>
              </div>
              <div class="rank-amount">¥{{ item.salesAmount }}</div>
            </div>
            <el-empty v-if="productRank.length === 0" description="暂无数据" />
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>待处理订单</span>
              <el-button type="primary" link @click="$router.push('/order/list')">
                查看全部
              </el-button>
            </div>
          </template>
          <el-table :data="pendingOrders" v-loading="orderLoading">
            <el-table-column prop="orderNo" label="订单号" width="200" />
            <el-table-column prop="receiverName" label="收货人" width="120" />
            <el-table-column prop="receiverPhone" label="电话" width="140" />
            <el-table-column label="订单金额" width="120">
              <template #default="{ row }">
                ¥{{ row.payAmount }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="orderStatusType(row.status)">
                  {{ orderStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="下单时间" width="180" />
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button type="primary" link @click="$router.push(`/order/detail/${row.id}`)">
                  查看
                </el-button>
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
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStatisticsOverview, getProductSalesRank } from '@/api/statistics'
import { getOrderList, shipOrder } from '@/api/order'
import { orderStatusText, orderStatusType } from '@/utils/format'
import type { StatisticsOverview, ProductSales, Order } from '@/types/api'

// 统计卡片数据
const overview = ref<StatisticsOverview>({
  todaySales: 0,
  todayOrders: 0,
  totalProducts: 0,
  pendingOrders: 0
})

const statisticsCards = reactive([
  { key: 'todaySales', label: '今日销售额', value: '¥0', icon: 'Money', color: '#409EFF' },
  { key: 'todayOrders', label: '今日订单数', value: '0', icon: 'Document', color: '#67C23A' },
  { key: 'totalProducts', label: '在售商品', value: '0', icon: 'Goods', color: '#E6A23C' },
  { key: 'pendingOrders', label: '待发货订单', value: '0', icon: 'Box', color: '#F56C6C' }
])

// 商品销量排行
const productRank = ref<ProductSales[]>([])
const chartLoading = ref(false)
const rankLoading = ref(false)

// 待处理订单
const pendingOrders = ref<Order[]>([])
const orderLoading = ref(false)

const chartRef = ref<HTMLElement>()

// 获取统计概览
const fetchOverview = async () => {
  try {
    const res = await getStatisticsOverview()
    if (res.data) {
      overview.value = res.data
      statisticsCards[0].value = `¥${res.data.todaySales}`
      statisticsCards[1].value = res.data.todayOrders.toString()
      statisticsCards[2].value = res.data.totalProducts.toString()
      statisticsCards[3].value = res.data.pendingOrders.toString()
    }
  } catch (error) {
    console.error(error)
  }
}

// 获取销量排行
const fetchProductRank = async () => {
  rankLoading.value = true
  try {
    const res = await getProductSalesRank(10)
    if (res.data) {
      productRank.value = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    rankLoading.value = false
  }
}

// 获取待发货订单
const fetchPendingOrders = async () => {
  orderLoading.value = true
  try {
    const res = await getOrderList({ pageNum: 1, pageSize: 10 })
    if (res.data) {
      // 筛选待发货订单（status === 2）
      pendingOrders.value = res.data.records.filter((order: Order) => order.status === 2)
    }
  } catch (error) {
    console.error(error)
  } finally {
    orderLoading.value = false
  }
}

// 订单发货
const handleShip = (order: Order) => {
  ElMessageBox.confirm('确定要发货吗？', '订单发货', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await shipOrder(order.id)
      ElMessage.success('发货成功')
      fetchPendingOrders()
      fetchOverview()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchOverview()
  fetchProductRank()
  fetchPendingOrders()
})
</script>

<style scoped lang="scss">
.dashboard-container {
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      
      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 10px;
        display: flex;
        justify-content: center;
        align-items: center;
        color: #fff;
      }
      
      .stat-info {
        margin-left: 15px;
        
        .stat-value {
          font-size: 24px;
          font-weight: 600;
          color: #303133;
        }
        
        .stat-label {
          color: #909399;
          margin-top: 5px;
        }
      }
    }
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .rank-list {
    max-height: 300px;
    overflow-y: auto;
    
    .rank-item {
      display: flex;
      align-items: center;
      padding: 10px 0;
      border-bottom: 1px solid #EBEEF5;
      
      &:last-child {
        border-bottom: none;
      }
      
      .rank-num {
        width: 24px;
        height: 24px;
        border-radius: 4px;
        background: #E4E7ED;
        color: #909399;
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 12px;
        margin-right: 10px;
        
        &.top {
          background: #409EFF;
          color: #fff;
        }
      }
      
      .rank-info {
        flex: 1;
        
        .rank-name {
          font-size: 14px;
          color: #303133;
        }
        
        .rank-sales {
          font-size: 12px;
          color: #909399;
          margin-top: 2px;
        }
      }
      
      .rank-amount {
        color: #F56C6C;
        font-weight: 600;
      }
    }
  }
}
</style>
