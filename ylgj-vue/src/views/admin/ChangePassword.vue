<template>
  <div style="max-width:480px;margin:0 auto">
    <div class="admin-card">
      <h3 style="font-size:16px;color:#1e293b;margin:0 0 20px;display:flex;align-items:center;gap:8px">
        <el-icon><Lock /></el-icon> 修改密码
      </h3>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" size="large">
        <el-form-item label="当前用户名">
          <el-input :model-value="userStore.username" disabled />
        </el-form-item>
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="form.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" show-password placeholder="请输入新密码（至少6位）" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">确认修改</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 密码安全提示 -->
    <div class="admin-card" style="margin-top:16px">
      <h4 style="color:#1e293b;margin:0 0 12px">密码安全提示</h4>
      <ul style="list-style:none;padding:0">
        <li v-for="(tip, i) in tips" :key="i" style="display:flex;align-items:center;gap:6px;margin-bottom:8px;font-size:13px;color:#64748b">
          <el-icon :size="14" color="#14b8a6"><CircleCheck /></el-icon> {{ tip }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<any>(null)
const submitting = ref(false)

const form = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const validateConfirm = (_rule: any, value: string, callback: Function) => {
  if (value !== form.newPassword) callback(new Error('两次输入的密码不一致'))
  else callback()
}

const rules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '新密码至少6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认新密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }]
}

const tips = [
  '密码长度至少6位，建议使用8位以上',
  '建议包含字母、数字和特殊字符',
  '不要使用生日、手机号等容易猜到的密码',
  '定期更换密码，保障账户安全'
]

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const ok = await userStore.changePassword(form.oldPassword, form.newPassword)
    if (ok) {
      ElMessage.success('密码修改成功，请重新登录')
      userStore.logout()
      router.push('/admin/login')
    } else {
      ElMessage.error('原密码错误，请重试')
    }
  } catch (e) {
    ElMessage.error('修改失败，请稍后重试')
  }
  submitting.value = false
}
</script>
