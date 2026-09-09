<template>
  <div>
    <div class="page-toolbar">
      <div>
        <h2 style="font-size:18px;color:#1e293b;margin:0">系统用户管理</h2>
        <p style="color:#94a3b8;font-size:13px;margin:4px 0 0">管理系统后台登录账户</p>
      </div>
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon> 新增用户
      </el-button>
    </div>

    <div class="admin-card">
      <!-- 搜索和统计 -->
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
        <div style="display:flex;gap:10px">
          <el-input v-model="searchKey" placeholder="搜索用户名/手机号" style="width:220px" clearable @clear="loadUsers">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-button @click="loadUsers">搜索</el-button>
        </div>
        <span style="font-size:13px;color:#94a3b8">共 {{ filteredUsers.length }} 个账户</span>
      </div>

      <el-table :data="paginatedUsers" stripe v-loading="loading">
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="username" label="用户名" min-width="120">
          <template #default="{ row }">
            <div style="display:flex;align-items:center;gap:8px">
              <span class="user-table-avatar">
                <el-icon :size="14"><UserFilled /></el-icon>
              </span>
              <span style="font-weight:500">{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="telephone" label="手机号" width="140" />
        <el-table-column prop="gender" label="性别" width="70" align="center" />
        <el-table-column prop="station" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.station === '1' ? 'success' : 'warning'" size="small">
              {{ row.station === '1' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button :type="row.station === '1' ? 'warning' : 'success'" link size="small" @click="toggleStation(row)">
              {{ row.station === '1' ? '停用' : '启用' }}
            </el-button>
            <el-popconfirm title="确定删除此用户？" @confirm="handleDelete(row.id!)">
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div style="display:flex;justify-content:flex-end;margin-top:12px">
        <el-pagination
          v-model:current-page="userPage"
          :page-size="pageSize"
          :total="filteredUsers.length"
          layout="prev, pager, next"
          small
          background
        />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="460px" :close-on-click-modal="false">
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input v-model="form.password" :placeholder="isEdit ? '留空则不修改' : '请输入密码'" show-password />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.telephone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="生日">
          <el-date-picker v-model="form.birthday" type="date" placeholder="选择日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { findAllUsers, addUser, updateUser, deleteUser } from '@/api/user'
import { ElMessage } from 'element-plus'
import type { User } from '@/types/models'

const users = ref<User[]>([])
const loading = ref(false)
const searchKey = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const userPage = ref(1)
const pageSize = 12
const formRef = ref<any>(null)

const form = ref<User>({ username: '', password: '', gender: '男', telephone: '', remark: '', birthday: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }]
}

const filteredUsers = computed(() => {
  if (!searchKey.value) return users.value
  const kw = searchKey.value.toLowerCase()
  return users.value.filter(u =>
    u.username.toLowerCase().includes(kw) ||
    (u.telephone || '').includes(kw)
  )
})

const paginatedUsers = computed(() => {
  const start = (userPage.value - 1) * pageSize
  return filteredUsers.value.slice(start, start + pageSize)
})

async function loadUsers() {
  loading.value = true
  try {
    const res = await findAllUsers()
    if (res.flag) users.value = res.data || []
  } catch (e) { /* ignore */ }
  finally { loading.value = false }
}

function openAddDialog() {
  isEdit.value = false
  form.value = { username: '', password: '', gender: '男', telephone: '', remark: '', birthday: '' }
  dialogVisible.value = true
}

function openEditDialog(row: User) {
  isEdit.value = true
  form.value = { ...row, password: '' }
  dialogVisible.value = true
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (isEdit.value) {
    const payload: User = { ...form.value }
    // 密码留空则不修改密码
    if (!payload.password) delete payload.password
    try {
      const res = await updateUser(payload)
      if (res.flag) { ElMessage.success('更新成功'); dialogVisible.value = false; loadUsers() }
      else ElMessage.error(res.message || '更新失败')
    } catch (e) { /* 拦截器已提示 */ }
  } else {
    try {
      const res = await addUser(form.value)
      if (res.flag) { ElMessage.success('新增成功'); dialogVisible.value = false; loadUsers() }
      else ElMessage.error(res.message || '新增失败')
    } catch (e) { /* 拦截器已提示 */ }
  }
}

/** 账号状态切换：正常(1) <-> 停用(0) */
async function toggleStation(row: User) {
  const target = row.station === '1' ? '0' : '1'
  const action = target === '0' ? '停用' : '启用'
  try {
    const res = await updateUser({ id: row.id, username: row.username, station: target })
    if (res.flag) {
      ElMessage.success(`账号已${action}`)
      loadUsers()
    } else {
      ElMessage.error(res.message || `${action}失败`)
    }
  } catch (e) { /* 拦截器已提示 */ }
}

async function handleDelete(id: number) {
  try {
    const res = await deleteUser(id)
    if (res.flag) { ElMessage.success('删除成功'); loadUsers() }
    else ElMessage.error(res.message || '删除失败')
  } catch (e) { /* 拦截器已提示 */ }
}

onMounted(loadUsers)
</script>

<style lang="scss" scoped>
.page-toolbar {
  display: flex; justify-content: space-between; align-items: flex-start;
  margin-bottom: 16px;
}
.user-table-avatar {
  width: 28px; height: 28px;
  border-radius: 6px;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  display: inline-flex; align-items: center; justify-content: center;
  color: #fff;
}
</style>
