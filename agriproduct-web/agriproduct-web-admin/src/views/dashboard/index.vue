<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon user">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalUsers }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon merchant">
              <el-icon><Shop /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalMerchants }}</div>
              <div class="stat-label">商家总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon product">
              <el-icon><Goods /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalProducts }}</div>
              <div class="stat-label">商品总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon order">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalOrders }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近7天订单趋势</span>
            </div>
          </template>
          <div ref="orderChartRef" style="height: 300px;" v-loading="orderChartLoading"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>商品分类占比</span>
            </div>
          </template>
          <div ref="categoryChartRef" style="height: 300px;" v-loading="categoryChartLoading"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新入驻商家</span>
              <el-button link type="primary" @click="$router.push('/merchant')"
                >查看更多</el-button
              >
            </div>
          </template>
          <el-table :data="recentMerchants" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="shopName" label="店铺名称" />
            <el-table-column prop="contactName" label="联系人" />
            <el-table-column prop="contactPhone" label="联系电话" />
            <el-table-column label="状态">
              <template #default="{ row }">
                <el-tag v-if="row.status === 1" type="success">正常</el-tag>
                <el-tag v-else-if="row.status === 0" type="warning">待审核</el-tag>
                <el-tag v-else type="danger">已拒绝</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getStatisticsOverviewApi, getDailyOrderStatisticsApi, getCategorySalesApi } from '@/api/statistics'
import { getMerchantListApi } from '@/api/merchant'

interface MerchantInfo {
  id: number
  shopName: string
  contactName: string
  contactPhone: string
  status: number
  createTime: string
}

// 统计数据
const statistics = reactive({
  totalUsers: 0,
  totalMerchants: 0,
  totalProducts: 0,
  totalOrders: 0
})

// 图表引用
const orderChartRef = ref<HTMLElement>()
const categoryChartRef = ref<HTMLElement>()
let orderChart: echarts.ECharts | null = null
let categoryChart: echarts.ECharts | null = null

// 加载状态
const orderChartLoading = ref(false)
const categoryChartLoading = ref(false)

// 最新商家
const recentMerchants = ref<MerchantInfo[]>([])

// 获取统计概览
const fetchStatistics = async () => {
  try {
    const res = await getStatisticsOverviewApi()
    if (res.data) {
      statistics.totalUsers = res.data.totalUsers
      statistics.totalMerchants = res.data.totalMerchants
      statistics.totalProducts = res.data.totalProducts
      statistics.totalOrders = res.data.totalOrders
    }
  } catch (error) {
    console.error(error)
  }
}

// 获取最新商家
const fetchRecentMerchants = async () => {
  try {
    const res = await getMerchantListApi({ pageNum: 1, pageSize: 5 })
    if (res.data) {
      recentMerchants.value = res.data.records
    }
  } catch (error) {
    console.error(error)
  }
}

// 初始化订单趋势图表
const initOrderChart = async () => {
  orderChartLoading.value = true
  try {
    // 获取最近7天的日期范围
    const endDate = new Date()
    const startDate = new Date()
    startDate.setDate(startDate.getDate() - 6)
    
    const formatDate = (date: Date) => {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }
    
    const res = await getDailyOrderStatisticsApi(formatDate(startDate), formatDate(endDate))
    
    await nextTick()
    
    if (orderChartRef.value) {
      orderChart = echarts.init(orderChartRef.value)
      
      const dates: string[] = []
      const counts: number[] = []
      const amounts: number[] = []
      
      // 生成最近7天的日期
      for (let i = 6; i >= 0; i--) {
        const date = new Date()
        date.setDate(date.getDate() - i)
        dates.push(formatDate(date).slice(5)) // 只显示月-日
      }
      
      // 填充数据
      if (res.data) {
        const dataMap = new Map(res.data.map(item => [item.date, item]))
        for (let i = 6; i >= 0; i--) {
          const date = new Date()
          date.setDate(date.getDate() - i)
          const dateStr = formatDate(date)
          const item = dataMap.get(dateStr)
          counts.push(item?.orderCount || 0)
          amounts.push(item?.totalAmount || 0)
        }
      }
      
      const option: echarts.EChartsOption = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: ['订单数', '销售额']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates
        },
        yAxis: [
          {
            type: 'value',
            name: '订单数',
            position: 'left',
            minInterval: 1
          },
          {
            type: 'value',
            name: '销售额(元)',
            position: 'right'
          }
        ],
        series: [
          {
            name: '订单数',
            type: 'line',
            smooth: true,
            data: counts,
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
              ])
            }
          },
          {
            name: '销售额',
            type: 'bar',
            yAxisIndex: 1,
            data: amounts,
            itemStyle: {
              color: '#67C23A',
              borderRadius: [4, 4, 0, 0]
            }
          }
        ]
      }
      
      orderChart.setOption(option)
    }
  } catch (error) {
    console.error(error)
  } finally {
    orderChartLoading.value = false
  }
}

// 初始化分类占比图表
const initCategoryChart = async () => {
  categoryChartLoading.value = true
  try {
    const res = await getCategorySalesApi()
    
    await nextTick()
    
    if (categoryChartRef.value) {
      categoryChart = echarts.init(categoryChartRef.value)
      
      const data: { name: string; value: number }[] = []
      
      if (res.data) {
        res.data.forEach(item => {
          data.push({
            name: item.categoryName,
            value: item.productCount
          })
        })
      }
      
      const option: echarts.EChartsOption = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: '5%',
          top: 'center'
        },
        series: [
          {
            name: '商品数量',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['35%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 16,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: data.length > 0 ? data : [{ name: '暂无数据', value: 1 }]
          }
        ]
      }
      
      categoryChart.setOption(option)
    }
  } catch (error) {
    console.error(error)
  } finally {
    categoryChartLoading.value = false
  }
}

// 窗口大小变化时重新调整图表
const handleResize = () => {
  orderChart?.resize()
  categoryChart?.resize()
}

onMounted(() => {
  fetchStatistics()
  fetchRecentMerchants()
  initOrderChart()
  initCategoryChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  orderChart?.dispose()
  categoryChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  .stat-card {
    margin-bottom: 20px;

    .stat-content {
      display: flex;
      align-items: center;

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
        margin-right: 15px;
        color: #fff;

        &.user {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        &.merchant {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        &.product {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        &.order {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
      }

      .stat-info {
        flex: 1;

        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: #303133;
          line-height: 1;
          margin-bottom: 8px;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
  }

  .chart-placeholder {
    padding: 40px 0;
  }
}
</style>
