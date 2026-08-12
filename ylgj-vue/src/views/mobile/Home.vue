<template>
  <div>
    <!-- Banner -->
    <div class="banner" style="background:linear-gradient(135deg, #2E8B57, #1e6b3e); border-radius: 12px; padding: 30px 20px; color: #fff; text-align: center; margin-bottom: 16px">
      <h2 style="font-size:22px;margin-bottom:6px">医疗管家</h2>
      <p style="opacity:.8;font-size:14px">您的专属健康管理平台</p>
    </div>

    <!-- 功能入口 -->
    <div style="display:grid;grid-template-columns:repeat(4,1fr);gap:12px;margin-bottom:20px">
      <div v-for="item in services" :key="item.label" style="text-align:center;cursor:pointer" @click="item.onClick">
        <div style="width:50px;height:50px;border-radius:14px;margin:0 auto 6px;display:flex;align-items:center;justify-content:center;font-size:24px;color:#fff"
          :style="{ background: item.color }">
          <el-icon><component :is="item.icon" /></el-icon>
        </div>
        <span style="font-size:12px;color:#666">{{ item.label }}</span>
      </div>
    </div>

    <!-- 热门套餐 -->
    <h3 style="margin-bottom:12px;font-size:16px">🔥 热门体检套餐</h3>
    <div v-for="s in setmeals" :key="s.id" class="m-card" style="cursor:pointer" @click="$router.push(`/setmeal/${s.id}`)">
      <div style="display:flex;gap:12px">
        <el-image v-if="s.img" :src="s.img" fit="cover" style="width:100px;height:80px;border-radius:8px;flex-shrink:0" />
        <div v-else style="width:100px;height:80px;border-radius:8px;flex-shrink:0;background:#e0f0e8;display:flex;align-items:center;justify-content:center;color:#2E8B57;font-size:24px">🏥</div>
        <div style="flex:1">
          <h4 style="font-size:15px;margin-bottom:4px">{{ s.name }}</h4>
          <p style="color:#999;font-size:12px;margin-bottom:6px">{{ s.remark?.slice(0, 30) || '适合各年龄段体检' }}</p>
          <span style="color:#f56c6c;font-size:18px;font-weight:bold">¥{{ s.price }}</span>
        </div>
      </div>
    </div>
    <p v-if="setmeals.length === 0" style="text-align:center;color:#ccc;padding:30px">暂无套餐</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { findAllSetmeal } from '@/api/setmeal'
import type { Setmeal } from '@/types/models'

const router = useRouter()
const setmeals = ref<Setmeal[]>([])

const services = [
  { label: '体检预约', icon: 'DishDot', color: '#409eff', onClick: () => router.push('/setmeal') },
  { label: 'AI助手', icon: 'ChatDotRound', color: '#67c23a', onClick: () => router.push('/ai') },
  { label: '我的预约', icon: 'Tickets', color: '#e6a23c', onClick: () => router.push('/orders') },
  { label: '健康档案', icon: 'Notebook', color: '#8b5cf6', onClick: () => router.push('/health') }
]

onMounted(async () => {
  const res = await findAllSetmeal()
  if (res.flag) setmeals.value = res.data.slice(0, 6)
})
</script>
