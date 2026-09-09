<template>
  <div>
    <!-- 手机号查询 -->
    <div style="display:flex;gap:8px;margin-bottom:14px">
      <el-input v-model="phoneInput" placeholder="输入手机号查看健康档案" size="large" clearable maxlength="11" @keyup.enter="load" />
      <el-button type="primary" size="large" @click="bindAndLoad">查询</el-button>
    </div>

    <div v-if="!member && !records.length && !loading" style="text-align:center;padding:50px 20px;color:#999">
      <div style="font-size:48px;margin-bottom:12px">📋</div>
      <p>输入手机号查看体检档案与记录</p>
      <p style="color:#ccc;font-size:13px;margin-top:6px">档案在完成预约后自动建立</p>
    </div>

    <div v-if="loading" style="text-align:center;padding:40px;color:#999">
      <el-icon class="is-loading" style="font-size:24px"><Loading /></el-icon>
    </div>

    <template v-else-if="member || records.length">
      <!-- 会员档案卡 -->
      <div class="m-card" style="background:linear-gradient(135deg,#2E8B57,#1e6b3e);color:#fff;border:none">
        <div style="display:flex;align-items:center;gap:14px">
          <div style="width:58px;height:58px;border-radius:50%;background:rgba(255,255,255,.22);display:flex;align-items:center;justify-content:center;font-size:28px;flex-shrink:0">👤</div>
          <div style="flex:1">
            <div style="display:flex;align-items:center;gap:8px">
              <h3 style="font-size:18px;margin:0">{{ member ? member.name : '健康用户' }}</h3>
              <span v-if="member && sexLabel(member.sex)" style="font-size:12px;background:rgba(255,255,255,.2);padding:2px 8px;border-radius:10px">{{ sexLabel(member.sex) }}</span>
            </div>
            <p style="opacity:.85;font-size:13px;margin-top:6px">📱 {{ displayPhone }} · 医疗管家健康档案</p>
          </div>
        </div>
        <div v-if="member" style="display:grid;grid-template-columns:1fr 1fr;gap:8px 14px;margin-top:16px;font-size:13px;opacity:.92;line-height:1.7">
          <div>档案编号：{{ member.fileNumber || '待分配' }}</div>
          <div>注册时间：{{ member.regTime || '-' }}</div>
          <div>出生日期：{{ member.birthday || '-' }}</div>
          <div>备注：{{ member.remark || '-' }}</div>
        </div>
      </div>

      <!-- 数据统计 -->
      <div style="display:flex;gap:10px;margin:12px 0">
        <div class="m-card" style="flex:1;text-align:center;padding:14px 8px">
          <div style="font-size:22px;font-weight:bold;color:#2E8B57">{{ records.length }}</div>
          <div style="font-size:12px;color:#999;margin-top:4px">档案记录</div>
        </div>
        <div class="m-card" style="flex:1;text-align:center;padding:14px 8px">
          <div style="font-size:22px;font-weight:bold;color:#409eff">{{ completedCount }}</div>
          <div style="font-size:12px;color:#999;margin-top:4px">已完成体检</div>
        </div>
        <div class="m-card" style="flex:1;text-align:center;padding:14px 8px">
          <div style="font-size:22px;font-weight:bold;color:#e6a23c">{{ pendingCount }}</div>
          <div style="font-size:12px;color:#999;margin-top:4px">待进行</div>
        </div>
      </div>

      <!-- 档案记录 -->
      <h3 style="font-size:16px;margin:4px 0 12px">📄 档案记录</h3>
      <p v-if="!records.length" class="m-card" style="text-align:center;color:#999;padding:30px 16px">暂无体检记录，去挑选套餐完成一次预约吧</p>
      <div v-for="o in records" :key="o.id" class="m-card">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
          <h4 style="font-size:15px;margin:0">{{ o.setmeal?.name || '体检套餐' }}</h4>
          <el-tag :type="statusType(o.orderStatus)" size="small">{{ statusLabel(o.orderStatus) }}</el-tag>
        </div>
        <div style="font-size:13px;color:#666;line-height:1.8">
          <div>📅 预约日期：{{ o.orderDate }}</div>
          <div>👤 体检人：{{ o.member?.name || '-' }}</div>
          <div>💰 套餐价：¥{{ o.setmeal?.price ?? '-' }}</div>
          <div>📋 预约方式：{{ o.orderType || '-' }}</div>
        </div>
      </div>

      <p style="text-align:center;color:#bbb;font-size:12px;margin:24px 0 6px">— 完 —</p>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { findMemberByPhone } from '@/api/member'
import { findOrdersByPhone } from '@/api/order'
import { useMemberPhone } from '@/composables/useMemberPhone'
import type { Member, OrderEnriched } from '@/types/models'

const { phone, bind } = useMemberPhone()
const phoneInput = ref(phone.value)
const member = ref<Member | null>(null)
const records = ref<OrderEnriched[]>([])
const loading = ref(false)

const displayPhone = computed(() => member.value?.phoneNumber || phone.value || phoneInput.value || '-')
const completedCount = computed(() => records.value.filter(o => o.orderStatus === '已到诊').length)
const pendingCount = computed(() => records.value.length - completedCount.value)

function sexLabel(sex: string | undefined) {
  if (sex === '1' || sex === '男') return '男'
  if (sex === '2' || sex === '女') return '女'
  return sex || ''
}

function statusLabel(s: string) {
  if (s === '已到诊') return '已完成'
  if (s === '已取消') return '已取消'
  if (s === '未到诊') return '未到诊'
  if (s === '已过期') return '已过期'
  return s || '已预约'
}

function statusType(s: string) {
  if (s === '已到诊') return 'success'
  if (s === '已取消' || s === '已过期') return 'info'
  if (s === '未到诊') return 'danger'
  return 'warning'
}

async function load() {
  const p = phoneInput.value.trim()
  if (!p) {
    ElMessage.warning('请输入手机号')
    return
  }
  loading.value = true
  member.value = null
  records.value = []
  try {
    const mRes = await findMemberByPhone(p)
    if (mRes.flag) member.value = mRes.data as Member
    const oRes = await findOrdersByPhone(p)
    if (oRes.flag) {
      const list = (oRes.data || []) as OrderEnriched[]
      // 若已精确定位会员，仅保留该会员的记录；否则全部展示
      records.value = member.value
        ? list.filter(o => o.member?.id === member.value!.id)
        : list
    }
    if (!member.value && !records.value.length) ElMessage.warning(mRes.message || '未找到该手机号对应的健康档案')
  } finally {
    loading.value = false
  }
}

function bindAndLoad() {
  bind(phoneInput.value)
  load()
}

onMounted(() => {
  if (phone.value) load()
})
</script>
