<template>
  <div class="orders">
    <el-card>
      <el-table :data="tableData" border stripe style="width:100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="orderNo" label="订单号" width="220" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="totalAmount" label="总金额" width="120">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDetail(row)">详情</el-button>
            <el-button link type="success" size="small" @click="handleStatus(row.id, 'PAID')" v-if="row.status === 'PENDING'">确认付款</el-button>
            <el-button link type="warning" size="small" @click="handleStatus(row.id, 'FINISHED')" v-if="row.status === 'PAID'">完成</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentOrder.userId }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTag(currentOrder.status)">{{ statusText(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentOrder.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>商品明细</el-divider>
      <el-table :data="orderItems" border stripe style="width:100%">
        <el-table-column prop="productId" label="商品ID" width="120" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="price" label="单价" width="120">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const currentOrder = ref(null)
const orderItems = ref([])

function statusTag(status) {
  const map = { PENDING: 'warning', PAID: 'success', FINISHED: 'info', CANCELLED: 'danger' }
  return map[status] || 'info'
}

function statusText(status) {
  const map = { PENDING: '待付款', PAID: '已付款', FINISHED: '已完成', CANCELLED: '已取消' }
  return map[status] || status
}

async function fetchData() {
  loading.value = true
  try {
    const res = await request.get('/orders')
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

async function openDetail(row) {
  const res = await request.get(`/orders/${row.id}`)
  currentOrder.value = res.data.order
  orderItems.value = res.data.items || []
  detailVisible.value = true
}

async function handleStatus(id, status) {
  await request.put(`/orders/${id}/status`, { status })
  ElMessage.success('状态更新成功')
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>