<template>
  <div class="profile-page">
    <!-- 个人信息头部卡片 -->
    <div class="profile-header-card">
      <div class="profile-bg"></div>
      <div class="profile-header-content">
        <div class="profile-avatar">
          <el-icon :size="40"><UserFilled /></el-icon>
        </div>
        <div class="profile-meta">
          <h2>{{ userStore.username || '管理员' }}</h2>
          <p><span class="role-badge">系统管理员</span> · 最后登录: {{ lastLoginTime }}</p>
        </div>
        <el-button type="primary" plain size="small" @click="showEdit = true">编辑资料</el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="profile-stats">
      <div class="p-stat-card">
        <div class="p-stat-icon" style="background: linear-gradient(135deg, #14b8a6, #0d9488)">
          <el-icon :size="20"><Calendar /></el-icon>
        </div>
        <div class="p-stat-info">
          <span class="p-stat-num">{{ stats.todayOrders }}</span>
          <span class="p-stat-label">今日预约</span>
        </div>
      </div>
      <div class="p-stat-card">
        <div class="p-stat-icon" style="background: linear-gradient(135deg, #3b82f6, #2563eb)">
          <el-icon :size="20"><User /></el-icon>
        </div>
        <div class="p-stat-info">
          <span class="p-stat-num">{{ stats.totalMembers }}</span>
          <span class="p-stat-label">总会员数</span>
        </div>
      </div>
      <div class="p-stat-card">
        <div class="p-stat-icon" style="background: linear-gradient(135deg, #8b5cf6, #7c3aed)">
          <el-icon :size="20"><DishDot /></el-icon>
        </div>
        <div class="p-stat-info">
          <span class="p-stat-num">{{ stats.totalSetmeals }}</span>
          <span class="p-stat-label">体检套餐</span>
        </div>
      </div>
      <div class="p-stat-card">
        <div class="p-stat-icon" style="background: linear-gradient(135deg, #f59e0b, #d97706)">
          <el-icon :size="20"><Checked /></el-icon>
        </div>
        <div class="p-stat-info">
          <span class="p-stat-num">{{ stats.todayVisits }}</span>
          <span class="p-stat-label">今日到诊</span>
        </div>
      </div>
    </div>

    <!-- 详细信息和操作 -->
    <div class="profile-grid">
      <!-- 账号信息 -->
      <div class="profile-card">
        <h3><el-icon><InfoFilled /></el-icon> 账号信息</h3>
        <div class="info-list">
          <div class="info-item">
            <span class="info-label">用户名</span>
            <span class="info-value">{{ currentUser?.username || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">性别</span>
            <span class="info-value">{{ currentUser?.gender || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">手机号</span>
            <span class="info-value">{{ currentUser?.telephone || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">生日</span>
            <span class="info-value">{{ currentUser?.birthday || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">账号状态</span>
            <span class="info-value"><span class="status-active">正常</span></span>
          </div>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="profile-card">
        <h3><el-icon><Operation /></el-icon> 快捷操作</h3>
        <div class="quick-actions">
          <div class="action-item" @click="$router.push('/admin/password')">
            <div class="action-icon" style="background: rgba(59,130,246,.1);color:#3b82f6">
              <el-icon :size="18"><Lock /></el-icon>
            </div>
            <span>修改密码</span>
            <el-icon :size="14"><ArrowRight /></el-icon>
          </div>
          <div class="action-item" @click="$router.push('/admin/export')">
            <div class="action-icon" style="background: rgba(20,184,166,.1);color:#14b8a6">
              <el-icon :size="18"><Download /></el-icon>
            </div>
            <span>数据导出</span>
            <el-icon :size="14"><ArrowRight /></el-icon>
          </div>
          <div class="action-item" @click="$router.push('/admin/news')">
            <div class="action-icon" style="background: rgba(245,158,11,.1);color:#f59e0b">
              <el-icon :size="18"><Bell /></el-icon>
            </div>
            <span>健康资讯</span>
            <el-icon :size="14"><ArrowRight /></el-icon>
          </div>
          <div class="action-item" @click="$router.push('/admin/users')">
            <div class="action-icon" style="background: rgba(139,92,246,.1);color:#8b5cf6">
              <el-icon :size="18"><UserFilled /></el-icon>
            </div>
            <span>用户管理</span>
            <el-icon :size="14"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>

      <!-- 操作日志 -->
      <div class="profile-card full-width">
        <h3><el-icon><Document /></el-icon> 最近操作记录</h3>
        <el-table :data="activityLogs" stripe size="small">
          <el-table-column prop="time" label="时间" width="170" />
          <el-table-column prop="action" label="操作" />
          <el-table-column prop="ip" label="IP 地址" width="150" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === '成功' ? 'success' : 'danger'" size="small">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 编辑资料弹窗 -->
    <el-dialog v-model="showEdit" title="编辑个人资料" width="480px" :close-on-click-modal="false">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="手机号">
          <el-input v-model="editForm.telephone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="editForm.gender">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="生日">
          <el-date-picker v-model="editForm.birthday" type="date" placeholder="选择日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEdit = false">取消</el-button>
        <el-button type="primary" @click="handleSaveProfile">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { findAllUsers } from '@/api/user'
import { getBusinessReportData } from '@/api/report'
import { findAllSetmeal } from '@/api/setmeal'
import { ElMessage } from 'element-plus'
import type { User } from '@/types/models'

const userStore = useUserStore()
const showEdit = ref(false)
const currentUser = ref<User | null>(null)

const lastLoginTime = new Date().toLocaleString('zh-CN')

const stats = ref({ todayOrders: 0, totalMembers: 0, totalSetmeals: 0, todayVisits: 0 })

const editForm = ref({
  telephone: '',
  gender: '男',
  birthday: '',
  remark: ''
})

const activityLogs = ref([
  { time: new Date().toLocaleString(), action: '登录系统', ip: '127.0.0.1', status: '成功' },
  { time: new Date(Date.now() - 1800000).toLocaleString(), action: '查看数据概览', ip: '127.0.0.1', status: '成功' },
  { time: new Date(Date.now() - 3600000).toLocaleString(), action: '修改预约设置', ip: '127.0.0.1', status: '成功' },
  { time: new Date(Date.now() - 7200000).toLocaleString(), action: '新增体检套餐', ip: '127.0.0.1', status: '成功' },
  { time: new Date(Date.now() - 86400000).toLocaleString(), action: '导出运营报表', ip: '127.0.0.1', status: '成功' }
])

function handleSaveProfile() {
  ElMessage.success('个人资料已更新（演示）')
  showEdit.value = false
}

onMounted(async () => {
  try {
    const users = await findAllUsers()
    if (users.flag) {
      const found = users.data.find((u: User) => u.username === userStore.username)
      if (found) currentUser.value = found
    }
  } catch (e) { /* ignore */ }
  try {
    const biz = await getBusinessReportData()
    if (biz.flag) {
      stats.value.todayOrders = biz.data.todayOrderNumber || 0
      stats.value.totalMembers = biz.data.totalMember || 0
      stats.value.todayVisits = biz.data.todayVisitsNumber || 0
    }
    const sm = await findAllSetmeal()
    if (sm.flag) stats.value.totalSetmeals = sm.data?.length || 0
  } catch (e) { /* ignore */ }
})
</script>

<style lang="scss" scoped>
.profile-page { max-width: 1200px; margin: 0 auto; }

.profile-header-card {
  position: relative;
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,.05);

  .profile-bg {
    height: 80px;
    background: linear-gradient(135deg, #0d2f50 0%, #14b8a6 100%);
  }

  .profile-header-content {
    display: flex;
    align-items: center;
    padding: 0 24px 20px;
    gap: 16px;
    margin-top: -28px;

    .profile-avatar {
      width: 72px; height: 72px;
      border-radius: 16px;
      background: linear-gradient(135deg, #1e3a5f, #0d2f50);
      border: 3px solid #fff;
      display: flex; align-items: center; justify-content: center;
      color: #14b8a6;
      box-shadow: 0 4px 16px rgba(0,0,0,.15);
      flex-shrink: 0;
    }

    .profile-meta {
      flex: 1;
      h2 { font-size: 20px; color: #1e293b; margin: 0 0 4px; }
      p { font-size: 13px; color: #94a3b8; margin: 0; }
      .role-badge {
        display: inline-block;
        padding: 2px 8px;
        border-radius: 4px;
        background: rgba(20,184,166,.1);
        color: #0d9488;
        font-size: 12px;
        font-weight: 500;
      }
    }
  }
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 20px;

  .p-stat-card {
    background: #fff;
    border-radius: 12px;
    padding: 16px;
    display: flex;
    align-items: center;
    gap: 12px;
    box-shadow: 0 1px 6px rgba(0,0,0,.04);

    .p-stat-icon {
      width: 44px; height: 44px;
      border-radius: 10px;
      display: flex; align-items: center; justify-content: center;
      color: #fff;
    }

    .p-stat-info {
      display: flex; flex-direction: column;
      .p-stat-num { font-size: 20px; font-weight: 700; color: #1e293b; }
      .p-stat-label { font-size: 12px; color: #94a3b8; margin-top: 2px; }
    }
  }
}

.profile-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;

  .full-width { grid-column: 1 / -1; }
}

.profile-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 6px rgba(0,0,0,.04);

  h3 {
    display: flex; align-items: center; gap: 8px;
    font-size: 15px; color: #1e293b; margin: 0 0 16px;
    padding-bottom: 12px; border-bottom: 1px solid #f1f5f9;
  }
}

.info-list {
  .info-item {
    display: flex; justify-content: space-between; align-items: center;
    padding: 10px 0;
    border-bottom: 1px solid #f8fafc;
    &:last-child { border: none; }

    .info-label { font-size: 13px; color: #94a3b8; }
    .info-value { font-size: 13px; color: #334155; font-weight: 500; }
    .status-active { color: #10b981; font-weight: 600; }
  }
}

.quick-actions {
  .action-item {
    display: flex; align-items: center; gap: 10px;
    padding: 12px; border-radius: 8px;
    cursor: pointer; transition: all 0.2s;

    &:hover { background: #f8fafc; }

    .action-icon {
      width: 34px; height: 34px;
      border-radius: 8px;
      display: flex; align-items: center; justify-content: center;
    }
    span { flex: 1; font-size: 13px; color: #475569; font-weight: 500; }
    .el-icon { color: #cbd5e1; }
  }
}
</style>
