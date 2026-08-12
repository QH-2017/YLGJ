<template>
  <div>
    <div style="display:flex;gap:8px;margin-bottom:16px">
      <el-input v-model="phone" placeholder="输入手机号查询预约" size="large" clearable @keyup.enter="loadOrders" />
      <el-button type="primary" size="large" @click="loadOrders">查询</el-button>
    </div>

    <div v-if="!phone" style="text-align:center;padding:60px 20px">
      <el-icon style="font-size:48px;color:#ccc"><Search /></el-icon>
      <p style="color:#999;margin-top:12px">请输入手机号查询您的预约记录</p>
    </div>

    <div v-else-if="loading" style="text-align:center;padding:30px">
      <el-icon class="is-loading" style="font-size:24px"><Loading /></el-icon>
    </div>

    <p v-else-if="orders.length === 0" style="text-align:center;color:#ccc;padding:40px">暂无预约记录</p>

    <div v-for="order in orders" :key="order.id" class="m-card">
      <div style="display:flex;justify-content:space-between;align-items:start;margin-bottom:8px">
        <h4>{{ order.setmealName || '体检套餐' }}</h4>
        <el-tag :type="statusType(order.orderStatus)" size="small">{{ order.orderStatus }}</el-tag>
      </div>
      <div style="font-size:13px;color:#666;line-height:1.8">
        <div>📅 预约日期：{{ order.orderDate }}</div>
        <div>👤 体检人：{{ order.memberName }}</div>
        <div>📱 手机号：{{ order.phone }}</div>
        <div>💰 套餐价：¥{{ order.setmealPrice }}</div>
        <div>📋 预约类型：{{ order.orderType }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { findOrdersByPhone } from '@/api/order'
import type { OrderEnriched } from '@/types/models'

const phone = ref(''); const orders = ref<any[]>([]); const loading = ref(false)

function statusType(status: string) {
  if (status === '已到诊') return 'success'
  if (status === '已取消') return 'info'
  return 'warning'
}

async function loadOrders() {
  if (!phone.value.trim()) return
  loading.value = true
  const res = await findOrdersByPhone(phone.value.trim())
  if (res.flag) orders.value = res.data
  loading.value = false
}
</script>
