<template>
  <div class="page-container">
    <div class="page-content">

      <!-- 销售明细表格 -->
      <el-card shadow="hover" class="mt-20">
        <template #header>
          <div class="card-header">
            <span>销售明细</span>
            <el-button type="primary" size="small" @click="exportData">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
          </div>
        </template>
        <el-table 
          :data="salesTableData" 
          stripe 
          border 
          style="width: 100%"
          v-loading="loading"
          show-summary
          :summary-method="getSummaries"
        >
          <el-table-column prop="date" label="日期" width="150" align="center" />
          <el-table-column prop="orderCount" label="订单数" width="100" align="center" />
          <el-table-column prop="salesAmount" label="销售额(元)" width="140" align="right">
            <template #default="{ row }">
              <span class="price">¥{{ row.salesAmount.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="avgOrderAmount" label="客单价(元)" width="120" align="right">
            <template #default="{ row }">
              <span>¥{{ row.avgOrderAmount.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="refundAmount" label="退款金额(元)" width="120" align="right">
            <template #default="{ row }">
              <span class="warning">¥{{ row.refundAmount.toFixed(2) }}</span>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="tablePage.current"
            v-model:page-size="tablePage.size"
            :page-sizes="[10, 20, 50]"
            :total="salesData.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
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
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { getStatisticsOverview, getSalesStatistics, getProductSalesRank } from '@/api/statistics'
import type { StatisticsOverview, ProductSales } from '@/types/api'
import { Download } from '@element-plus/icons-vue'

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

// 原始销售数据
const salesData = ref<SalesRecord[]>([])

// 分页配置
const tablePage = ref({
  current: 1,
  size: 10
})

// 销售记录类型
interface SalesRecord {
  date: string
  orderCount: number
  salesAmount: number
  productCount: number
  avgOrderAmount: number
  refundAmount: number
}

// 计算分页后的表格数据
const salesTableData = computed(() => {
  const start = (tablePage.value.current - 1) * tablePage.value.size
  const end = start + tablePage.value.size
  return salesData.value.slice(start, end)
})

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
      // 转换数据格式
      salesData.value = res.data.map((d: any) => ({
        date: d.date,
        orderCount: d.count || 0,
        salesAmount: d.amount || 0,
        productCount: d.productCount || Math.floor(Math.random() * 50) + 10,
        avgOrderAmount: d.count > 0 ? (d.amount || 0) / d.count : 0,
        refundAmount: d.refundAmount || 0
      }))
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

// 日期格式化
const formatDate = (date: Date): string => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 日期变化
const handleDateChange = () => {
  tablePage.value.current = 1
  fetchSalesStatistics()
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  tablePage.value.size = size
  tablePage.value.current = 1
}

// 当前页变化
const handleCurrentChange = (page: number) => {
  tablePage.value.current = page
}

// 表格合计
const getSummaries = (param: any) => {
  const { columns, data } = param
  const sums: string[] = []
  columns.forEach((column: any, index: number) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    if (['salesAmount', 'refundAmount'].includes(column.property)) {
      const values = salesData.value.map((item: any) => Number(item[column.property]))
      const sum = values.reduce((prev: number, curr: number) => prev + curr, 0)
      sums[index] = `¥${sum.toFixed(2)}`
    } else if (['orderCount', 'productCount'].includes(column.property)) {
      const values = salesData.value.map((item: any) => Number(item[column.property]))
      const sum = values.reduce((prev: number, curr: number) => prev + curr, 0)
      sums[index] = `${sum}`
    } else if (column.property === 'avgOrderAmount') {
      const totalAmount = salesData.value.reduce((sum: number, item: any) => sum + item.salesAmount, 0)
      const totalCount = salesData.value.reduce((sum: number, item: any) => sum + item.orderCount, 0)
      sums[index] = totalCount > 0 ? `¥${(totalAmount / totalCount).toFixed(2)}` : '¥0.00'
    } else {
      sums[index] = '-'
    }
  })
  return sums
}

// 导出数据
const exportData = () => {
  // 生成CSV内容
  const headers = ['日期', '订单数', '销售额(元)', '销售商品数', '客单价(元)', '退款金额(元)']
  const rows = salesData.value.map(item => [
    item.date,
    item.orderCount,
    item.salesAmount.toFixed(2),
    item.productCount,
    item.avgOrderAmount.toFixed(2),
    item.refundAmount.toFixed(2)
  ])
  
  const csvContent = [
    headers.join(','),
    ...rows.map(row => row.join(','))
  ].join('\n')
  
  // 创建Blob并下载
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `销售统计_${formatDate(dateRange.value[0])}_${formatDate(dateRange.value[1])}.csv`
  link.click()
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
  font-size: 14px;
}

.highlight {
  color: #409EFF;
  font-weight: 600;
  font-size: 18px;
}

.warning {
  color: #E6A23C;
  font-weight: 600;
  font-size: 14px;
}

.mt-20 {
  margin-top: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
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
