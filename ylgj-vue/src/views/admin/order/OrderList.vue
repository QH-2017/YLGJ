<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchPhone" placeholder="按手机号查询预约" clearable @keyup.enter="loadByPhone" />
      <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon> 全部订单</el-button>
      <el-button @click="loadByPhone">按手机号查询</el-button>
    </div>
    <div class="admin-card">
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="会员姓名" width="100">
          <template #default="{ row }">{{ row.member?.name || '未知' }}</template>
        </el-table-column>
        <el-table-column label="手机号" width="130">
          <template #default="{ row }">{{ row.member?.phoneNumber || '' }}</template>
        </el-table-column>
        <el-table-column label="套餐名称" min-width="160">
          <template #default="{ row }">{{ row.setmeal?.name || '未知' }}</template>
        </el-table-column>
        <el-table-column prop="orderDate" label="预约日期" width="120" />
        <el-table-column prop="orderType" label="预约类型" width="100" />
        <el-table-column label="状态" width="140">
          <template #default="{ row }">
            <el-select v-model="row.orderStatus" size="small" @change="(val: string) => handleStatusChange(row, val)">
              <el-option value="未到诊" label="未到诊" />
              <el-option value="已到诊" label="已到诊" />
              <el-option value="已取消" label="已取消" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleCancel(row)" :disabled="row.orderStatus === '已取消'">取消预约</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import { findAllOrders, findOrdersByPhone, updateOrder, cancelOrder } from '@/api/order'
import type { OrderEnriched } from '@/types/models'

const searchPhone = ref(''); const tableData = ref<OrderEnriched[]>([]); const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const res = await findAllOrders()
    if (res.flag) tableData.value = res.data
  } catch (e) {
    // 错误提示已由 axios 拦截器统一处理，这里仅兜底避免未捕获异常
  } finally {
    loading.value = false
  }
}

async function loadByPhone() {
  if (!searchPhone.value.trim()) { loadData(); return }
  loading.value = true
  try {
    const res = await findOrdersByPhone(searchPhone.value.trim())
    if (res.flag) tableData.value = res.data
  } catch (e) {
    // 错误提示已由 axios 拦截器统一处理，这里仅兜底避免未捕获异常
  } finally {
    loading.value = false
  }
}

async function handleStatusChange(row: OrderEnriched, val: string) {
  await updateOrder({ orderId: String(row.id), orderStatus: val })
}

async function handleCancel(row: OrderEnriched) {
  await ElMessageBox.confirm('确定取消该预约吗？', '确认', { type: 'warning' })
  await cancelOrder(row.id); loadData()
}

onMounted(loadData)
</script>
