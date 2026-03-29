<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">订单管理</h2>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="订单号">
          <el-input
            v-model="queryParams.orderNo"
            placeholder="请输入订单号"
            clearable
          />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="待付款" :value="1" />
            <el-option label="待发货" :value="2" />
            <el-option label="待收货" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已取消" :value="5" />
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

    <el-table v-loading="loading" :data="orderList" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column label="订单金额" width="120">
        <template #default="{ row }">
          <span class="price">¥{{ row.payAmount?.toFixed(2) || '0.00' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ row.statusDesc }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="receiverName" label="收货人" width="100" />
      <el-table-column prop="receiverPhone" label="联系电话" width="130" />
      <el-table-column prop="receiverAddress" label="收货地址" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleViewDetail(row)">
            查看详情
          </el-button>
          <el-button
            v-if="row.status === 2"
            link
            type="success"
            size="small"
            @click="handleShip(row)"
          >
            发货
          </el-button>
          <el-button
            v-if="row.status === 1 || row.status === 2"
            link
            type="danger"
            size="small"
            @click="handleCancel(row)"
          >
            取消
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

    <!-- Order detail dialog -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="800px">
      <el-descriptions v-if="currentOrder" :column="2" border>
        <el-descriptions-item label="订单ID">{{ currentOrder.id }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentOrder.userId }}</el-descriptions-item>
        <el-descriptions-item label="商家ID">{{ currentOrder.merchantId }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">
          <span class="price">¥{{ currentOrder.totalAmount?.toFixed(2) || '0.00' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="实付金额">
          <span class="price">¥{{ currentOrder.payAmount?.toFixed(2) || '0.00' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ currentOrder.statusDesc }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="支付状态">
          <el-tag :type="currentOrder.payStatus === 1 ? 'success' : 'warning'">
            {{ currentOrder.payStatus === 1 ? '已支付' : '未支付' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="收货人">{{ currentOrder.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.receiverAddress }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ currentOrder.payTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发货时间">{{ currentOrder.shipTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ currentOrder.finishTime || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div v-if="currentOrder?.items?.length" class="order-items">
        <h4>订单商品</h4>
        <el-table :data="currentOrder.items" border>
          <el-table-column label="商品图片" width="100">
            <template #default="{ row }">
              <el-image
                v-if="row.productImage"
                :src="row.productImage"
                style="width: 60px; height: 60px"
                fit="cover"
                :preview-src-list="[row.productImage]"
              />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column label="单价" width="120">
            <template #default="{ row }">
              <span class="price">¥{{ row.price?.toFixed(2) || '0.00' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column label="小计" width="120">
            <template #default="{ row }">
              <span class="price">¥{{ (row.price * row.quantity)?.toFixed(2) || '0.00' }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="currentOrder && currentOrder.status === 2"
          type="success"
          @click="handleShipFromDetail"
        >
          发货
        </el-button>
        <el-button
          v-if="currentOrder && (currentOrder.status === 1 || currentOrder.status === 2)"
          type="danger"
          @click="handleCancelFromDetail"
        >
          取消订单
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderListApi, getOrderDetailApi, shipOrderApi, cancelOrderApi } from '@/api/index'
import type { OrderItem } from '@/api/order'

const loading = ref(false)
const orderList = ref<OrderItem[]>([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  status: undefined as number | undefined
})

const detailDialogVisible = ref(false)
const currentOrder = ref<OrderItem | null>(null)

function getStatusType(status: number) {
  switch (status) {
    case 1:
      return 'warning'
    case 2:
      return 'primary'
    case 3:
      return 'info'
    case 4:
      return 'success'
    case 5:
      return 'danger'
    default:
      return 'info'
  }
}

async function getList() {
  loading.value = true
  try {
    const res = await getOrderListApi(queryParams)
    if (res.code === 200 && res.data) {
      orderList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.pageNum = 1
  getList()
}

function handleReset() {
  queryParams.orderNo = ''
  queryParams.status = undefined
  queryParams.pageNum = 1
  getList()
}

async function handleViewDetail(row: OrderItem) {
  try {
    const res = await getOrderDetailApi(row.id)
    if (res.code === 200 && res.data) {
      currentOrder.value = res.data
      detailDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

async function handleShip(row: OrderItem) {
  try {
    await ElMessageBox.confirm('确定要发货吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await shipOrderApi(row.id)
    if (res.code === 200) {
      ElMessage.success('发货成功')
      getList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发货失败')
    }
  }
}

async function handleCancel(row: OrderItem) {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await cancelOrderApi(row.id)
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      getList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

function handleShipFromDetail() {
  if (!currentOrder.value) return
  detailDialogVisible.value = false
  handleShip(currentOrder.value)
}

function handleCancelFromDetail() {
  if (!currentOrder.value) return
  detailDialogVisible.value = false
  handleCancel(currentOrder.value)
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.price {
  color: #f56c6c;
  font-weight: 500;
}

.order-items {
  margin-top: 20px;

  h4 {
    margin-bottom: 10px;
    font-size: 16px;
    font-weight: 500;
  }
}
</style>
