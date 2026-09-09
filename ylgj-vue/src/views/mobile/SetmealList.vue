<template>
  <div>
    <h3 style="margin-bottom:12px;font-size:16px">全部体检套餐</h3>
    <div v-for="s in setmeals" :key="s.id" class="m-card" style="cursor:pointer" @click="$router.push(`/mobile/setmeal/${s.id}`)">
      <div style="display:flex;gap:12px">
        <el-image v-if="s.img" :src="s.img" fit="cover" style="width:100px;height:80px;border-radius:8px;flex-shrink:0" />
        <div v-else style="width:100px;height:80px;border-radius:8px;flex-shrink:0;background:#e0f0e8;display:flex;align-items:center;justify-content:center;color:#2E8B57;font-size:24px">🏥</div>
        <div style="flex:1">
          <h4 style="font-size:15px;margin-bottom:4px">{{ s.name }}</h4>
          <p style="color:#999;font-size:12px;margin-bottom:6px">{{ s.remark?.slice(0, 30) || '无描述' }}</p>
          <span style="color:#f56c6c;font-size:18px;font-weight:bold">¥{{ s.price }}</span>
        </div>
      </div>
    </div>
    <p v-if="setmeals.length === 0" style="text-align:center;color:#ccc;padding:40px">暂无套餐</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { findAllSetmeal } from '@/api/setmeal'
import type { Setmeal } from '@/types/models'

const setmeals = ref<Setmeal[]>([])
onMounted(async () => {
  const res = await findAllSetmeal()
  if (res.flag) setmeals.value = res.data
})
</script>
