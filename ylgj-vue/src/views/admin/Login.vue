<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="login-bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
      <div class="shape shape-4"></div>
    </div>

    <div class="login-card">
      <div class="login-brand">
        <div class="brand-logo">
          <span class="medical-cross">+</span>
        </div>
        <h2>医疗管家管理系统</h2>
        <p class="subtitle">Smart Health Management System</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password @keyup.enter="handleLogin">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <router-link to="/admin/register">注册账户 →</router-link>
        <span>v2.0</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const ok = await userStore.login(form.username, form.password)
    if (ok) router.push('/admin/dashboard')
  } catch (e) {
    // 登录失败提示已由 axios 拦截器统一弹出，这里兜底避免按钮一直转圈
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0a1628 0%, #0d2f50 40%, #0f4c6b 70%, #0b2d45 100%);
  position: relative;
  overflow: hidden;
}

.login-bg-shapes .shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.06;
}
.shape-1 { width: 600px; height: 600px; background: #14b8a6; top: -200px; left: -100px; }
.shape-2 { width: 400px; height: 400px; background: #3b82f6; bottom: -150px; right: -80px; }
.shape-3 { width: 200px; height: 200px; background: #8b5cf6; top: 40%; right: 15%; }
.shape-4 { width: 300px; height: 300px; background: #14b8a6; bottom: 10%; left: 5%; }

.login-card {
  width: 420px;
  background: rgba(255, 255, 255, 0.97);
  border-radius: 20px;
  padding: 44px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3), 0 0 0 1px rgba(255, 255, 255, 0.05);
  position: relative;
  z-index: 1;
  backdrop-filter: blur(10px);
}

.login-brand {
  text-align: center;
  margin-bottom: 32px;
}

.brand-logo {
  width: 56px; height: 56px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, #14b8a6, #0d9488);
  border-radius: 16px;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 6px 20px rgba(20, 184, 166, 0.4);
}

.medical-cross {
  color: #fff;
  font-size: 28px;
  font-weight: 300;
  line-height: 1;
}

.login-brand h2 {
  font-size: 20px; color: #1e293b;
  margin: 0 0 6px; letter-spacing: 1px;
}

.login-brand .subtitle {
  font-size: 12px; color: #94a3b8;
  font-weight: 400; letter-spacing: 0.5px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 4px;
  border-radius: 10px;
  background: linear-gradient(135deg, #14b8a6, #0d9488);
  border: none;
  font-weight: 600;
}

.login-btn:hover {
  background: linear-gradient(135deg, #0d9488, #0f766e);
}

.login-footer {
  display: flex; justify-content: space-between; align-items: center;
  margin-top: 24px; font-size: 12px; color: #94a3b8;
  a { color: #14b8a6; text-decoration: none; font-weight: 500; }
  a:hover { color: #0d9488; }
}
</style>
