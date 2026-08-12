<template>
  <div>
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #14b8a6, #0d9488); box-shadow: 0 4px 14px rgba(20,184,166,.35)">
          <el-icon :size="22"><User /></el-icon>
        </div>
        <div class="stat-info">
          <h4>总会员数</h4>
          <div class="num">{{ reportData.totalMember }}</div>
          <span class="stat-change up">较上月 ↑12%</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #3b82f6, #2563eb); box-shadow: 0 4px 14px rgba(59,130,246,.35)">
          <el-icon :size="22"><Calendar /></el-icon>
        </div>
        <div class="stat-info">
          <h4>今日预约</h4>
          <div class="num">{{ reportData.todayOrderNumber }}</div>
          <span class="stat-change up">较昨日 ↑8%</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f59e0b, #d97706); box-shadow: 0 4px 14px rgba(245,158,11,.35)">
          <el-icon :size="22"><Checked /></el-icon>
        </div>
        <div class="stat-info">
          <h4>今日到诊</h4>
          <div class="num">{{ reportData.todayVisitsNumber }}</div>
          <span class="stat-change down">到诊率 76%</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #8b5cf6, #7c3aed); box-shadow: 0 4px 14px rgba(139,92,246,.35)">
          <el-icon :size="22"><UserFilled /></el-icon>
        </div>
        <div class="stat-info">
          <h4>本月新增</h4>
          <div class="num">{{ reportData.thisMonthNewMember }}</div>
          <span class="stat-change up">较上月 ↑5%</span>
        </div>
      </div>
    </div>

    <!-- 图表区 -->
    <div style="display:grid;grid-template-columns:2fr 1fr;gap:16px">
      <div class="admin-card">
        <div class="card-header">
          <h3>会员增长趋势（近6个月）</h3>
          <el-tag size="small">月度统计</el-tag>
        </div>
        <v-chart :option="memberChartOption" style="height:320px" autoresize />
      </div>
      <div class="admin-card">
        <div class="card-header">
          <h3>套餐预约占比</h3>
          <el-tag size="small">实时数据</el-tag>
        </div>
        <v-chart :option="setmealPieOption" style="height:320px" autoresize />
      </div>
    </div>

    <!-- 热门套餐排行 + 快捷入口 -->
    <div style="display:grid;grid-template-columns:1.5fr 1fr;gap:16px;margin-top:16px">
      <div class="admin-card">
        <div class="card-header">
          <h3>热门套餐排行 (Top 5)</h3>
          <el-tag size="small" type="warning">热度排行</el-tag>
        </div>
        <el-table :data="reportData.hotSetmeal" stripe>
          <el-table-column type="index" label="排名" width="60">
            <template #default="{ $index }">
              <span class="rank-badge" :class="'rank-' + ($index + 1)">{{ $index + 1 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="套餐名称" />
          <el-table-column prop="setmeal_count" label="预约数量" width="100" align="center">
            <template #default="{ row }">
              <el-tag type="primary" effect="plain" round size="small">{{ row.setmeal_count }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="proportion" label="占比" width="80" align="center" />
        </el-table>
      </div>

      <!-- 快捷功能模块 -->
      <div class="admin-card">
        <div class="card-header">
          <h3>快捷功能</h3>
          <el-tag size="small" type="success">常用入口</el-tag>
        </div>
        <div class="quick-grid">
          <div class="quick-item" @click="$router.push('/admin/setmeal/add')">
            <div class="q-icon" style="background:rgba(20,184,166,.1);color:#14b8a6"><el-icon :size="18"><Plus /></el-icon></div>
            <span>新增套餐</span>
          </div>
          <div class="quick-item" @click="$router.push('/admin/order')">
            <div class="q-icon" style="background:rgba(59,130,246,.1);color:#3b82f6"><el-icon :size="18"><Calendar /></el-icon></div>
            <span>预约管理</span>
          </div>
          <div class="quick-item" @click="$router.push('/admin/member')">
            <div class="q-icon" style="background:rgba(139,92,246,.1);color:#8b5cf6"><el-icon :size="18"><User /></el-icon></div>
            <span>会员管理</span>
          </div>
          <div class="quick-item" @click="$router.push('/admin/export')">
            <div class="q-icon" style="background:rgba(245,158,11,.1);color:#f59e0b"><el-icon :size="18"><Download /></el-icon></div>
            <span>数据导出</span>
          </div>
          <div class="quick-item" @click="$router.push('/admin/news')">
            <div class="q-icon" style="background:rgba(239,68,68,.1);color:#ef4444"><el-icon :size="18"><Bell /></el-icon></div>
            <span>健康资讯</span>
          </div>
          <div class="quick-item" @click="$router.push('/admin/users')">
            <div class="q-icon" style="background:rgba(6,182,212,.1);color:#06b6d4"><el-icon :size="18"><UserFilled /></el-icon></div>
            <span>用户管理</span>
          </div>
        </div>
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
import { getBusinessReportData, getMemberReport, getSetmealReport } from '@/api/report'
import type { BusinessReportData } from '@/types/api'

use([CanvasRenderer, LineChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const reportData = ref<BusinessReportData>({
  reportDate: '', todayNewMember: 0, totalMember: 0, thisWeekNewMember: 0, thisMonthNewMember: 0,
  todayOrderNumber: 0, todayVisitsNumber: 0, thisWeekOrderNumber: 0, thisWeekVisitsNumber: 0,
  thisMonthOrderNumber: 0, thisMonthVisitsNumber: 0, hotSetmeal: []
})

const memberChartOption = ref({})
const setmealPieOption = ref({})

onMounted(async () => {
  try {
    const business = await getBusinessReportData()
    if (business.flag) reportData.value = business.data

    const member = await getMemberReport()
    if (member.flag) {
      memberChartOption.value = {
        tooltip: { trigger: 'axis', backgroundColor: '#fff', borderColor: '#e2e8f0', textStyle: { color: '#334155' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: member.data.months, axisLine: { lineStyle: { color: '#e2e8f0' } }, axisLabel: { color: '#94a3b8' } },
        yAxis: { type: 'value', splitLine: { lineStyle: { color: '#f1f5f9', type: 'dashed' } }, axisLabel: { color: '#94a3b8' } },
        series: [{
          name: '新增会员', type: 'line', data: member.data.memberCounts,
          smooth: true, symbol: 'circle', symbolSize: 8,
          areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(20,184,166,.3)' }, { offset: 1, color: 'rgba(20,184,166,0)' }] } },
          itemStyle: { color: '#14b8a6' }, lineStyle: { width: 3, color: '#14b8a6' }
        }]
      }
    }

    const setmeal = await getSetmealReport()
    if (setmeal.flag) {
      setmealPieOption.value = {
        tooltip: { trigger: 'item', backgroundColor: '#fff', borderColor: '#e2e8f0', textStyle: { color: '#334155' } },
        legend: { bottom: 0, textStyle: { color: '#94a3b8', fontSize: 11 } },
        color: ['#14b8a6', '#3b82f6', '#8b5cf6', '#f59e0b', '#ef4444', '#06b6d4'],
        series: [{
          name: '预约数量', type: 'pie', radius: ['50%', '75%'], center: ['50%', '48%'],
          data: setmeal.data.map((s: any) => ({ name: s.name, value: s.value })),
          emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,.3)' } },
          label: { color: '#94a3b8', fontSize: 10 }
        }]
      }
    }
  } catch (e) { /* 数据加载失败不阻塞页面 */ }
})
</script>

<style lang="scss" scoped>
.stat-change {
  font-size: 11px; margin-top: 4px; display: block;
  &.up { color: #10b981; }
  &.down { color: #f59e0b; }
}

.card-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 16px;
  h3 { font-size: 15px; color: #1e293b; margin: 0; }
}

.rank-badge {
  display: inline-flex; align-items: center; justify-content: center;
  width: 22px; height: 22px; border-radius: 6px;
  font-size: 12px; font-weight: 700;
  background: #f1f5f9; color: #94a3b8;
  &.rank-1 { background: #fef3c7; color: #d97706; }
  &.rank-2 { background: #e2e8f0; color: #64748b; }
  &.rank-3 { background: #fed7aa; color: #c2410c; }
}

.quick-grid {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px;

  .quick-item {
    display: flex; flex-direction: column; align-items: center; gap: 8px;
    padding: 16px 8px; border-radius: 10px;
    cursor: pointer; transition: all 0.2s;

    &:hover { background: #f8fafc; transform: translateY(-2px); }

    .q-icon {
      width: 40px; height: 40px;
      border-radius: 10px;
      display: flex; align-items: center; justify-content: center;
    }
    span { font-size: 12px; color: #475569; font-weight: 500; }
  }
}
</style>
