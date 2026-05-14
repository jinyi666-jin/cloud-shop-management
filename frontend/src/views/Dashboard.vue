<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background:#e6f7ff">
              <el-icon :size="28" color="#1890ff"><Goods /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.productCount }}</div>
              <div class="stat-label">商品总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background:#f6ffed">
              <el-icon :size="28" color="#52c41a"><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.orderCount }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background:#fff7e6">
              <el-icon :size="28" color="#fa8c16"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.userCount }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background:#f9f0ff">
              <el-icon :size="28" color="#722ed1"><SoldOut /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.todayOrders }}</div>
              <div class="stat-label">今日订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>系统概览</span>
          </template>
          <p>欢迎使用云电商管理系统！通过左侧菜单可以管理商品、分类、订单和用户。</p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../api/request'

const stats = ref({ productCount: 0, orderCount: 0, userCount: 0, todayOrders: 0 })

onMounted(async () => {
  const res = await request.get('/dashboard')
  if (res.data) {
    stats.value = res.data
  }
})
</script>

<style scoped>
.stat-card {
  cursor: pointer;
}
.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}
.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}
</style>
