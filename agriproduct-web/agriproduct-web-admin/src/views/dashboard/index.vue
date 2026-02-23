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
              <div class="stat-value">1,234</div>
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
              <div class="stat-value">56</div>
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
              <div class="stat-value">3,456</div>
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
              <div class="stat-value">8,901</div>
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
          <div class="chart-placeholder">
            <el-empty description="图表功能开发中" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>商品分类占比</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <el-empty description="图表功能开发中" />
          </div>
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
import { ref } from 'vue'

const recentMerchants = ref([
  {
    id: 1,
    shopName: '有机农场',
    contactName: '张三',
    contactPhone: '13800138000',
    status: 1,
    createTime: '2026-02-20 10:00:00'
  },
  {
    id: 2,
    shopName: '绿色果蔬',
    contactName: '李四',
    contactPhone: '13800138001',
    status: 0,
    createTime: '2026-02-19 15:30:00'
  },
  {
    id: 3,
    shopName: '田园鲜果',
    contactName: '王五',
    contactPhone: '13800138002',
    status: 1,
    createTime: '2026-02-18 09:15:00'
  }
])
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
