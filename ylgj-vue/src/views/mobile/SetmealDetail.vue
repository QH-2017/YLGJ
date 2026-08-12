<template>
  <div v-loading="loading">
    <el-image v-if="detail.img" :src="detail.img" fit="cover" style="width:100%;height:200px;border-radius:10px;margin-bottom:14px" />
    <div v-else style="width:100%;height:200px;border-radius:10px;background:#e0f0e8;display:flex;align-items:center;justify-content:center;margin-bottom:14px">
      <span style="color:#2E8B57;font-size:40px">🏥</span>
    </div>

    <div class="m-card">
      <h3 style="font-size:18px;margin-bottom:8px">{{ detail.name }}</h3>
      <div style="display:flex;gap:20px;margin-bottom:10px;font-size:13px;color:#666">
        <span>编码：{{ detail.code }}</span>
        <span>适用性别：{{ sexMap[detail.sex] || '不限' }}</span>
        <span>年龄：{{ detail.age || '不限' }}</span>
      </div>
      <div style="color:#f56c6c;font-size:24px;font-weight:bold;margin-bottom:10px">¥{{ detail.price }}</div>
      <p style="color:#999;font-size:13px;line-height:1.6">{{ detail.remark }}</p>
      <p v-if="detail.attention" style="color:#e6a23c;font-size:13px;margin-top:8px">⚠️ {{ detail.attention }}</p>
    </div>

    <!-- 包含的检查组 -->
    <div class="m-card" v-if="checkgroups.length > 0">
      <h4 style="margin-bottom:12px">包含的检查项目：</h4>
      <el-collapse>
        <el-collapse-item v-for="cg in checkgroups" :key="cg.id" :title="`${cg.name} (${cg.code})`">
          <div v-if="cg.items?.length">
            <div v-for="item in cg.items" :key="item.id" style="padding:6px 0;border-bottom:1px solid #f5f5f5;font-size:13px">
              <span>{{ item.name }}</span>
              <span style="float:right;color:#999">¥{{ item.price }}</span>
            </div>
          </div>
          <p v-else style="color:#ccc">暂无检查项</p>
        </el-collapse-item>
      </el-collapse>
    </div>

    <!-- 立即预约按钮 -->
    <div style="position:sticky;bottom:60px;padding:12px 0">
      <el-button type="success" size="large" style="width:100%;height:48px;font-size:16px" @click="$router.push(`/order/${detail.id}`)">
        立即预约
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { findSetmealById } from '@/api/setmeal'
import { findCheckgroupById } from '@/api/checkgroup'
import { findCheckitemIdsByCheckgroupId, findCheckitemById } from '@/api/checkitem'

const route = useRoute()
const loading = ref(true)
const detail = ref<any>({})
const checkgroups = ref<any[]>([])
const sexMap: Record<string, string> = { '0': '男', '1': '女', '2': '不限' }

onMounted(async () => {
  const id = Number(route.params.id)
  const res = await findSetmealById(id)
  if (res.flag) {
    detail.value = res.data
    const cgList = res.data.setmealCheckGroups || []
    const cgDetails = []
    for (const sc of cgList) {
      const cgRes = await findCheckgroupById(sc.checkgroupId)
      if (cgRes.flag) {
        const cg = cgRes.data
        // 获取关联的检查项
        const idsRes = await findCheckitemIdsByCheckgroupId(cg.id!)
        const items = []
        if (idsRes.flag && idsRes.data) {
          for (const itemId of idsRes.data) {
            const itemRes = await findCheckitemById(itemId)
            if (itemRes.flag) items.push(itemRes.data)
          }
        }
        cgDetails.push({ ...cg, items })
      }
    }
    checkgroups.value = cgDetails
  }
  loading.value = false
})
</script>
