<template>
  <div>
    <!-- 用户头像区 -->
    <div class="m-card" style="text-align:center;padding:30px 20px">
      <div style="width:70px;height:70px;border-radius:50%;background:#e0f0e8;margin:0 auto 12px;display:flex;align-items:center;justify-content:center;font-size:30px">👤</div>
      <h3 style="font-size:17px">健康用户</h3>
      <p style="color:#999;font-size:13px">医疗管家 · 您的健康伙伴</p>
    </div>

    <!-- 菜单列表 -->
    <div class="m-card" style="padding:0">
      <div v-for="item in menuItems" :key="item.label" style="padding:14px 16px;border-bottom:1px solid #f5f5f5;display:flex;align-items:center;justify-content:space-between;cursor:pointer" @click="item.onClick">
        <div style="display:flex;align-items:center;gap:10px">
          <el-icon :size="18" :color="item.color"><component :is="item.icon" /></el-icon>
          <span style="font-size:14px">{{ item.label }}</span>
        </div>
        <el-icon color="#ccc"><ArrowRight /></el-icon>
      </div>
    </div>

    <p style="text-align:center;color:#ccc;margin-top:30px;font-size:12px">医疗管家 v1.0</p>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
const menuItems = [
  { label: '我的预约', icon: 'Tickets', color: '#409eff', onClick: () => router.push('/orders') },
  { label: '健康档案', icon: 'Notebook', color: '#67c23a', onClick: () => router.push('/health') },
  { label: 'AI健康助手', icon: 'ChatDotRound', color: '#8b5cf6', onClick: () => router.push('/ai') },
  { label: '体检套餐', icon: 'DishDot', color: '#e6a23c', onClick: () => router.push('/setmeal') }
]
</script>
