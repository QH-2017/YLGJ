<template>
  <div>
    <!-- 用户头像区 -->
    <div class="m-card" style="text-align:center;padding:26px 20px">
      <div style="width:70px;height:70px;border-radius:50%;background:#e0f0e8;margin:0 auto 12px;display:flex;align-items:center;justify-content:center;font-size:30px">👤</div>
      <h3 style="font-size:17px">{{ member ? member.name : '健康用户' }}</h3>
      <p style="color:#999;font-size:13px">{{ member ? '📱 ' + member.phoneNumber + ' · 已绑定' : (phone ? '绑定手机号后同步健康档案' : '医疗管家 · 您的健康伙伴') }}</p>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { findMemberByPhone } from '@/api/member'
import { useMemberPhone } from '@/composables/useMemberPhone'
import type { Member } from '@/types/models'

const router = useRouter()
const { phone, bind } = useMemberPhone()
const member = ref<Member | null>(null)

async function bindPhone() {
  try {
    const { value } = await ElMessageBox.prompt('输入您的手机号，即可同步健康档案与预约记录', '绑定手机号', {
      confirmButtonText: '绑定',
      cancelButtonText: '取消',
      inputPattern: /^1[3-9]\d{9}$/,
      inputErrorMessage: '请输入正确的11位手机号',
      inputValue: phone.value || ''
    })
    bind(value)
    ElMessage.success('绑定成功')
    await loadMember()
  } catch (e) {
    // 用户取消
  }
}

async function loadMember() {
  if (!phone.value) {
    member.value = null
    return
  }
  const res = await findMemberByPhone(phone.value)
  if (res.flag) member.value = res.data as Member
}

const menuItems = [
  { label: '我的预约', icon: 'Tickets', color: '#409eff', onClick: () => router.push('/mobile/orders') },
  { label: '健康档案', icon: 'Notebook', color: '#67c23a', onClick: () => router.push('/mobile/health') },
  { label: 'AI健康助手', icon: 'ChatDotRound', color: '#8b5cf6', onClick: () => router.push('/mobile/ai') },
  { label: '体检套餐', icon: 'DishDot', color: '#e6a23c', onClick: () => router.push('/mobile/setmeal') },
  { label: phone.value ? '更换手机号' : '绑定手机号', icon: 'Iphone', color: '#2E8B57', onClick: () => bindPhone() }
]

onMounted(() => { loadMember() })
</script>
