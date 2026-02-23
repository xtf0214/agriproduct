<template>
  <div class="page-container">
    <div class="page-content">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>销售统计</span>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="handleDateChange"
            />
          </div>
        </template>
        
        <div class="chart-container" v-loading="loading">
          <div ref="chartRef" style="height: 400px;"></div>
        </div>
      </el-card>
      
      <el-row :gutter="20" class="mt-20">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>商品销量排行</span>
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
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>数据概览</span>
            </template>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="今日销售额">
                <span class="price">¥{{ overview.todaySales }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="今日订单数">
                <span class="highlight">{{ overview.todayOrders }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="在售商品">
                <span class="highlight">{{ overview.totalProducts }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="待发货订单">
                <span class="warning">{{ overview.pendingOrders }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { getStatisticsOverview, getSalesStatistics, getProductSalesRank } from '@/api/statistics'
import type { StatisticsOverview, ProductSales } from '@/types/api'

const loading = ref(false)
const rankLoading = ref(false)
const chartRef = ref<HTMLElement>()
let chartInstance: any = null

const dateRange = ref<[Date, Date]>([
  new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
  new Date()
])

const overview = ref<StatisticsOverview>({
  todaySales: 0,
  todayOrders: 0,
  totalProducts: 0,
  pendingOrders: 0
})

const productRank = ref<ProductSales[]>([])

// 获取统计概览
const fetchOverview = async () => {
  try {
    const res = await getStatisticsOverview()
    if (res.data) {
      overview.value = res.data
    }
  } catch (error) {
    console.error(error)
  }
}

// 获取销售统计
const fetchSalesStatistics = async () => {
  loading.value = true
  try {
    const [start, end] = dateRange.value
    const res = await getSalesStatistics(
      formatDate(start),
      formatDate(end)
    )
    if (res.data) {
      renderChart(res.data)
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
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

// 渲染图表
const renderChart = (data: { date: string; amount: number; count: number }[]) => {
  // 这里简化处理，实际项目中可以使用ECharts
  if (!chartRef.value) return
  
  // 模拟图表显示
  const dates = data.map(d => d.date)
  const amounts = data.map(d => d.amount)
  const counts = data.map(d => d.count)
  
  chartRef.value.innerHTML = `
    <div style="text-align: center; color: #909399;">
      <p>日期: ${dates.join(' | ')}</p>
      <p style="margin-top: 10px;">销售额(元): ${amounts.join(' | ')}</p>
      <p style="margin-top: 10px;">订单数: ${counts.join(' | ')}</p>
      <p style="margin-top: 20px; color: #409EFF;">（可集成 ECharts 展示更美观的图表）</p>
    </div>
  `
}

// 日期格式化
const formatDate = (date: Date): string => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 日期变化
const handleDateChange = () => {
  fetchSalesStatistics()
}

onMounted(() => {
  fetchOverview()
  fetchSalesStatistics()
  fetchProductRank()
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
  }
})
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #F56C6C;
  font-weight: 600;
  font-size: 18px;
}

.highlight {
  color: #409EFF;
  font-weight: 600;
  font-size: 18px;
}

.warning {
  color: #E6A23C;
  font-weight: 600;
  font-size: 18px;
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
</style>
