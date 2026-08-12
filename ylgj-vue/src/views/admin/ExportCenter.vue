<template>
  <div>
    <div class="page-toolbar">
      <div>
        <h2 style="font-size:18px;color:#1e293b;margin:0">数据导出中心</h2>
        <p style="color:#94a3b8;font-size:13px;margin:4px 0 0">导出运营数据、会员信息、预约记录等报表</p>
      </div>
    </div>

    <!-- 导出功能卡片 -->
    <div class="export-grid">
      <div class="export-card">
        <div class="export-icon" style="background:linear-gradient(135deg,#14b8a6,#0d9488)">
          <el-icon :size="28"><TrendCharts /></el-icon>
        </div>
        <div class="export-info">
          <h4>运营数据报表</h4>
          <p>导出完整的运营数据 Excel 报表，包含会员统计、预约到诊数据、套餐明细</p>
        </div>
        <el-button type="primary" @click="handleExportBusiness" :loading="exportLoading === 'business'">
          <el-icon><Download /></el-icon> 导出 Excel
        </el-button>
      </div>

      <div class="export-card">
        <div class="export-icon" style="background:linear-gradient(135deg,#3b82f6,#2563eb)">
          <el-icon :size="28"><User /></el-icon>
        </div>
        <div class="export-info">
          <h4>会员信息导出</h4>
          <p>导出全部会员的基本信息，包括姓名、手机号、身份证号、注册时间等</p>
        </div>
        <el-button type="primary" @click="handleExportMembers" :loading="exportLoading === 'members'">
          <el-icon><Download /></el-icon> CSV 导出
        </el-button>
      </div>

      <div class="export-card">
        <div class="export-icon" style="background:linear-gradient(135deg,#8b5cf6,#7c3aed)">
          <el-icon :size="28"><Calendar /></el-icon>
        </div>
        <div class="export-info">
          <h4>预约记录导出</h4>
          <p>导出全部预约订单明细，包含会员、套餐、预约日期、预约状态等信息</p>
        </div>
        <el-button type="primary" @click="handleExportOrders" :loading="exportLoading === 'orders'">
          <el-icon><Download /></el-icon> CSV 导出
        </el-button>
      </div>

      <div class="export-card">
        <div class="export-icon" style="background:linear-gradient(135deg,#f59e0b,#d97706)">
          <el-icon :size="28"><DishDot /></el-icon>
        </div>
        <div class="export-info">
          <h4>套餐信息导出</h4>
          <p>导出全部体检套餐信息，包含套餐名称、价格、包含的检查组等数据</p>
        </div>
        <el-button type="primary" @click="handleExportSetmeals" :loading="exportLoading === 'setmeals'">
          <el-icon><Download /></el-icon> CSV 导出
        </el-button>
      </div>
    </div>

    <!-- 导出历史 -->
    <div class="admin-card" style="margin-top:20px">
      <h3 style="font-size:15px;color:#1e293b;margin:0 0 16px;display:flex;align-items:center;gap:8px">
        <el-icon><Clock /></el-icon> 导出记录
      </h3>
      <el-table :data="exportHistory" stripe size="small">
        <el-table-column prop="time" label="导出时间" width="180" />
        <el-table-column prop="fileName" label="文件名" min-width="200" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag size="small" :type="row.tagType">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="size" label="文件大小" width="100" />
        <el-table-column label="操作" width="120" align="center">
          <template #default>
            <el-button type="primary" link size="small">重新下载</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { exportBusinessReport } from '@/api/report'
import { ElMessage } from 'element-plus'

const exportLoading = ref<string | null>(null)

async function handleExportBusiness() {
  exportLoading.value = 'business'
  try {
    const blob = await exportBusinessReport()
    downloadBlob(blob, `运营数据报表_${new Date().toISOString().slice(0, 10)}.xlsx`)
    ElMessage.success('导出成功')
    addHistory('运营数据报表', 'Excel 报表', 'success')
  } catch (e) {
    ElMessage.error('导出失败，请确认后端服务正常运行')
  }
  exportLoading.value = null
}

function downloadBlob(blob: Blob, fileName: string) {
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url; a.download = fileName; a.click()
  window.URL.revokeObjectURL(url)
}

function csvExport(data: any[], columns: string[], fileName: string) {
  const BOM = '﻿'
  const header = columns.join(',') + '\n'
  const rows = data.map(row => columns.map(col => `"${(row[col] ?? '')}"`).join(',')).join('\n')
  const blob = new Blob([BOM + header + rows], { type: 'text/csv;charset=utf-8' })
  downloadBlob(blob, fileName)
  ElMessage.success('导出成功')
}

async function handleExportMembers() {
  exportLoading.value = 'members'
  try {
    const { findAllMember } = await import('@/api/member')
    const res = await findAllMember()
    if (res.flag && res.data) {
      csvExport(res.data, ['name', 'sex', 'phoneNumber', 'idCard', 'regTime', 'email'], `会员信息_${new Date().toISOString().slice(0, 10)}.csv`)
      addHistory('会员信息', 'CSV 文件', 'info')
    }
  } catch (e) { ElMessage.error('导出失败') }
  exportLoading.value = null
}

async function handleExportOrders() {
  exportLoading.value = 'orders'
  try {
    const { findAllOrders } = await import('@/api/order')
    const res = await findAllOrders()
    if (res.flag && res.data) {
      const flat = res.data.map((o: any) => ({
        orderId: o.id,
        memberName: o.member?.name || '',
        phone: o.member?.phoneNumber || '',
        setmealName: o.setmeal?.name || '',
        orderDate: o.orderDate,
        orderType: o.orderType,
        orderStatus: o.orderStatus
      }))
      csvExport(flat, ['orderId', 'memberName', 'phone', 'setmealName', 'orderDate', 'orderType', 'orderStatus'],
        `预约记录_${new Date().toISOString().slice(0, 10)}.csv`)
      addHistory('预约记录', 'CSV 文件', 'info')
    }
  } catch (e) { ElMessage.error('导出失败') }
  exportLoading.value = null
}

async function handleExportSetmeals() {
  exportLoading.value = 'setmeals'
  try {
    const { findAllSetmeal } = await import('@/api/setmeal')
    const res = await findAllSetmeal()
    if (res.flag && res.data) {
      csvExport(res.data, ['name', 'code', 'sex', 'age', 'price', 'remark'],
        `套餐信息_${new Date().toISOString().slice(0, 10)}.csv`)
      addHistory('套餐信息', 'CSV 文件', 'warning')
    }
  } catch (e) { ElMessage.error('导出失败') }
  exportLoading.value = null
}

const exportHistory = ref([
  { time: new Date(Date.now() - 86400000).toLocaleString(), fileName: '运营数据报表_2026-07-30.xlsx', type: 'Excel 报表', tagType: 'success', size: '48KB' },
  { time: new Date(Date.now() - 172800000).toLocaleString(), fileName: '预约记录_2026-07-29.csv', type: 'CSV 文件', tagType: 'info', size: '12KB' }
])

function addHistory(type: string, tagType: string, label: string) {
  exportHistory.value.unshift({
    time: new Date().toLocaleString(),
    fileName: `${type}_${new Date().toISOString().slice(0, 10)}.${tagType === 'Excel 报表' ? 'xlsx' : 'csv'}`,
    type, tagType: label, size: '-'
  })
}
</script>

<style lang="scss" scoped>
.page-toolbar {
  display: flex; justify-content: space-between; align-items: flex-start;
  margin-bottom: 16px;
}
.export-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;

  .export-card {
    background: #fff;
    border-radius: 12px;
    padding: 24px;
    display: flex;
    align-items: center;
    gap: 18px;
    box-shadow: 0 1px 6px rgba(0,0,0,.04);
    transition: box-shadow 0.25s;

    &:hover { box-shadow: 0 4px 16px rgba(0,0,0,.08); }

    .export-icon {
      width: 60px; height: 60px;
      border-radius: 14px;
      display: flex; align-items: center; justify-content: center;
      color: #fff;
      flex-shrink: 0;
    }

    .export-info {
      flex: 1;
      h4 { font-size: 15px; color: #1e293b; margin: 0 0 6px; }
      p { font-size: 12px; color: #94a3b8; margin: 0; line-height: 1.5; }
    }
  }
}
</style>
