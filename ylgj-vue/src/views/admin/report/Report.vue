<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
      <h2>运营数据报表</h2>
      <el-button type="success" @click="handleExport"><el-icon><Download /></el-icon> 导出Excel</el-button>
    </div>

    <!-- KPI -->
    <div class="stat-cards">
      <div class="stat-card">
        <div class="stat-icon blue"><el-icon><User /></el-icon></div>
        <div class="stat-info"><h4>总会员</h4><div class="num">{{ data.totalMember }}</div></div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green"><el-icon><Calendar /></el-icon></div>
        <div class="stat-info"><h4>本月预约</h4><div class="num">{{ data.thisMonthOrderNumber }}</div></div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange"><el-icon><Checked /></el-icon></div>
        <div class="stat-info"><h4>本月到诊</h4><div class="num">{{ data.thisMonthVisitsNumber }}</div></div>
      </div>
      <div class="stat-card">
        <div class="stat-icon purple"><el-icon><TrendCharts /></el-icon></div>
        <div class="stat-info"><h4>本月新增会员</h4><div class="num">{{ data.thisMonthNewMember }}</div></div>
      </div>
    </div>

    <!-- 图表 -->
    <div style="display:grid;grid-template-columns:2fr 1fr;gap:16px">
      <div class="admin-card">
        <h3 style="margin-bottom:16px">会员增长趋势（近6个月）</h3>
        <v-chart :option="memberOption" style="height:320px" autoresize />
      </div>
      <div class="admin-card">
        <h3 style="margin-bottom:16px">套餐预约占比</h3>
        <v-chart :option="setmealOption" style="height:320px" autoresize />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { getBusinessReportData, getMemberReport, getSetmealReport, exportBusinessReport } from '@/api/report'

use([CanvasRenderer, LineChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const data = ref<any>({})
const memberOption = ref({})
const setmealOption = ref({})

onMounted(async () => {
  const [biz, member, setmeal] = await Promise.all([getBusinessReportData(), getMemberReport(), getSetmealReport()])
  if (biz.flag) data.value = biz.data
  if (member.flag) {
    memberOption.value = {
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: member.data.months },
      yAxis: { type: 'value' },
      series: [{ name: '新增会员', type: 'line', data: member.data.memberCounts, smooth: true, areaStyle: { opacity: 0.3 }, itemStyle: { color: '#409eff' } }]
    }
  }
  if (setmeal.flag) {
    setmealOption.value = {
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{ name: '预约数量', type: 'pie', radius: ['45%', '70%'], data: setmeal.data.map((s: any) => ({ name: s.name, value: s.value })) }]
    }
  }
})

async function handleExport() {
  const blob = await exportBusinessReport()
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url; a.download = `运营数据报表_${new Date().toISOString().slice(0,10)}.xlsx`
  a.click(); URL.revokeObjectURL(url)
}
</script>
