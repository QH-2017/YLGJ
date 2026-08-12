<template>
  <div class="register-container">
    <div class="register-bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
    </div>

    <div class="register-card">
      <!-- 头部 -->
      <div class="register-header">
        <div class="brand-logo">
          <span class="medical-cross">+</span>
        </div>
        <h2>创建管理员账户</h2>
        <p>医疗管家管理系统 · 账户注册</p>
      </div>

      <!-- 步骤条 -->
      <el-steps :active="currentStep" align-center finish-status="success" style="margin: 24px 0 28px">
        <el-step title="账户设置" description="设置登录凭据" />
        <el-step title="个人信息" description="完善个人资料" />
        <el-step title="安全验证" description="确认注册信息" />
      </el-steps>

      <!-- 步骤1: 账户设置 -->
      <el-form
        v-show="currentStep === 0"
        ref="step1Ref"
        :model="form"
        :rules="step1Rules"
        label-width="0"
        size="large"
      >
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名（4-20位字母或数字）" @blur="checkUsername">
            <template #prefix><el-icon><User /></el-icon></template>
            <template #suffix>
              <el-icon v-if="usernameStatus === 'checking'" class="is-loading"><Loading /></el-icon>
              <el-icon v-else-if="usernameStatus === 'ok'" color="#10b981"><CircleCheck /></el-icon>
              <el-icon v-else-if="usernameStatus === 'taken'" color="#ef4444"><CircleClose /></el-icon>
            </template>
          </el-input>
          <div v-if="usernameMsg" class="field-hint" :class="usernameStatus">{{ usernameMsg }}</div>
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" @input="checkPasswordStrength">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
          <div v-if="form.password" class="password-strength">
            <div class="strength-bar">
              <div class="strength-fill" :style="{ width: passwordStrength.percent + '%' }" :class="passwordStrength.level"></div>
            </div>
            <span class="strength-text" :class="passwordStrength.level">{{ passwordStrength.text }}</span>
            <span class="strength-score">{{ passwordStrength.score }}/100</span>
          </div>
          <div v-if="form.password" class="password-rules">
            <span :class="{ met: pwdChecks.length }"><el-icon :size="12"><component :is="pwdChecks.length ? 'CircleCheck' : 'CircleClose'" /></el-icon> 至少8位字符</span>
            <span :class="{ met: pwdChecks.hasUpper }"><el-icon :size="12"><component :is="pwdChecks.hasUpper ? 'CircleCheck' : 'CircleClose'" /></el-icon> 包含大写字母</span>
            <span :class="{ met: pwdChecks.hasLower }"><el-icon :size="12"><component :is="pwdChecks.hasLower ? 'CircleCheck' : 'CircleClose'" /></el-icon> 包含小写字母</span>
            <span :class="{ met: pwdChecks.hasDigit }"><el-icon :size="12"><component :is="pwdChecks.hasDigit ? 'CircleCheck' : 'CircleClose'" /></el-icon> 包含数字</span>
            <span :class="{ met: pwdChecks.hasSpecial }"><el-icon :size="12"><component :is="pwdChecks.hasSpecial ? 'CircleCheck' : 'CircleClose'" /></el-icon> 包含特殊字符</span>
          </div>
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入密码">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
      </el-form>

      <!-- 步骤2: 个人信息 -->
      <el-form
        v-show="currentStep === 1"
        ref="step2Ref"
        :model="form"
        :rules="step2Rules"
        label-width="80px"
        size="large"
      >
        <el-form-item label="真实姓名" prop="realname">
          <el-input v-model="form.realname" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio-button value="男">男</el-radio-button>
            <el-radio-button value="女">女</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="telephone">
          <el-input v-model="form.telephone" placeholder="请输入手机号">
            <template #prefix><el-icon><Phone /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱地址">
            <template #prefix><el-icon><Message /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker v-model="form.birthday" type="date" placeholder="选择日期" style="width:100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注信息（选填）" />
        </el-form-item>
      </el-form>

      <!-- 步骤3: 确认 -->
      <div v-show="currentStep === 2" class="confirm-step">
        <div class="confirm-card">
          <h4><el-icon><User /></el-icon> 账户信息</h4>
          <div class="confirm-row"><span>用户名</span><strong>{{ form.username }}</strong></div>
          <div class="confirm-row"><span>密码强度</span><strong :class="passwordStrength.level">{{ passwordStrength.text }}</strong></div>
        </div>
        <div class="confirm-card">
          <h4><el-icon><InfoFilled /></el-icon> 个人信息</h4>
          <div class="confirm-row"><span>姓名</span><strong>{{ form.realname || '-' }}</strong></div>
          <div class="confirm-row"><span>性别</span><strong>{{ form.gender }}</strong></div>
          <div class="confirm-row"><span>手机号</span><strong>{{ form.telephone || '-' }}</strong></div>
          <div class="confirm-row"><span>邮箱</span><strong>{{ form.email || '-' }}</strong></div>
          <div class="confirm-row"><span>出生日期</span><strong>{{ form.birthday || '-' }}</strong></div>
        </div>
        <div class="confirm-card">
          <h4><el-icon><Select /></el-icon> 服务条款</h4>
          <el-checkbox v-model="agreed">
            我已阅读并同意 <a href="#" @click.prevent="showTerms = true">《医疗管家系统服务协议》</a> 和 <a href="#" @click.prevent="showTerms = true">《隐私政策》</a>
          </el-checkbox>
        </div>
      </div>

      <!-- 底部按钮 -->
      <div class="step-actions">
        <el-button v-if="currentStep > 0" @click="prevStep" size="large">上一步</el-button>
        <el-button v-if="currentStep < 2" type="primary" @click="nextStep" size="large">下一步</el-button>
        <el-button v-if="currentStep === 2" type="primary" :loading="submitting" size="large" class="submit-btn" @click="handleRegister">
          完成注册
        </el-button>
      </div>

      <div class="register-footer">
        已有账户？<router-link to="/admin/login">立即登录 →</router-link>
      </div>
    </div>

    <!-- 服务条款弹窗 -->
    <el-dialog v-model="showTerms" title="医疗管家系统服务协议" width="560px">
      <div style="max-height:400px;overflow-y:auto;font-size:13px;color:#475569;line-height:1.8">
        <h4>一、总则</h4>
        <p>欢迎使用医疗管家管理系统（以下简称"本系统"）。本系统是一个面向医疗机构的管理平台，提供会员管理、体检预约、数据统计等功能。</p>
        <h4>二、账户管理</h4>
        <p>1. 用户在注册时应提供真实、准确的个人信息。<br/>2. 用户应妥善保管账户密码，因密码泄露导致的后果由用户自行承担。<br/>3. 系统管理员有权对违规账户进行停用或删除处理。</p>
        <h4>三、数据安全</h4>
        <p>1. 本系统采用加密技术保护用户数据传输安全。<br/>2. 系统定期备份数据，保障数据不丢失。<br/>3. 用户不得利用本系统从事违法违规活动。</p>
        <h4>四、免责声明</h4>
        <p>本系统仅供医疗管理辅助使用，不构成医疗建议。具体诊疗请咨询专业医生。</p>
      </div>
      <template #footer>
        <el-button type="primary" @click="showTerms = false">我已了解</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { findAllUsers, register as registerApi } from '@/api/user'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'

const router = useRouter()
const currentStep = ref(0)
const submitting = ref(false)
const agreed = ref(false)
const showTerms = ref(false)
const usernameStatus = ref<'idle' | 'checking' | 'ok' | 'taken'>('idle')
const usernameMsg = ref('')

const step1Ref = ref<FormInstance>()
const step2Ref = ref<FormInstance>()

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realname: '',
  gender: '男',
  telephone: '',
  email: '',
  birthday: '',
  remark: ''
})

const validateUsername = (_rule: any, value: string, callback: Function) => {
  if (!value) callback(new Error('请输入用户名'))
  else if (!/^[a-zA-Z0-9_]{4,20}$/.test(value)) callback(new Error('4-20位，只允许字母数字下划线'))
  else if (usernameStatus.value === 'taken') callback(new Error('用户名已被注册'))
  else callback()
}

const validatePassword = (_rule: any, value: string, callback: Function) => {
  if (!value) callback(new Error('请输入密码'))
  else if (value.length < 8) callback(new Error('密码至少8位'))
  else if (passwordStrength.value.score < 40) callback(new Error('密码强度不足，请按要求设置'))
  else callback()
}

const validateConfirm = (_rule: any, value: string, callback: Function) => {
  if (!value) callback(new Error('请确认密码'))
  else if (value !== form.password) callback(new Error('两次密码不一致'))
  else callback()
}

const validatePhone = (_rule: any, value: string, callback: Function) => {
  if (!value) callback(new Error('请输入手机号'))
  else if (!/^1[3-9]\d{9}$/.test(value)) callback(new Error('手机号格式不正确'))
  else callback()
}

const validateEmail = (_rule: any, value: string, callback: Function) => {
  if (value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) callback(new Error('邮箱格式不正确'))
  else callback()
}

const step1Rules = {
  username: [{ required: true, validator: validateUsername, trigger: 'blur' }],
  password: [{ required: true, validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirm, trigger: 'blur' }]
}

const step2Rules = {
  realname: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  telephone: [{ validator: validatePhone, trigger: 'blur' }],
  email: [{ validator: validateEmail, trigger: 'blur' }]
}

// 密码强度检查
const pwdChecks = reactive({ length: false, hasUpper: false, hasLower: false, hasDigit: false, hasSpecial: false })

const passwordStrength = computed(() => {
  const pwd = form.password
  if (!pwd) return { score: 0, percent: 0, level: '', text: '' }

  pwdChecks.length = pwd.length >= 8
  pwdChecks.hasUpper = /[A-Z]/.test(pwd)
  pwdChecks.hasLower = /[a-z]/.test(pwd)
  pwdChecks.hasDigit = /\d/.test(pwd)
  pwdChecks.hasSpecial = /[^a-zA-Z0-9]/.test(pwd)

  let score = 0
  score += Math.min(pwd.length * 4, 32)
  if (pwdChecks.hasUpper) score += 12
  if (pwdChecks.hasLower) score += 12
  if (pwdChecks.hasDigit) score += 12
  if (pwdChecks.hasSpecial) score += 18
  // 混合组合加分
  const variety = [pwdChecks.hasUpper, pwdChecks.hasLower, pwdChecks.hasDigit, pwdChecks.hasSpecial].filter(Boolean).length
  score += variety * 6

  score = Math.min(score, 100)

  let level = 'weak', text = '弱'
  if (score >= 80) { level = 'strong'; text = '强' }
  else if (score >= 50) { level = 'medium'; text = '中' }

  return { score, percent: score, level, text }
})

function checkPasswordStrength() {
  // trigger computed recalculation
}

// 异步检查用户名
let checkTimer: ReturnType<typeof setTimeout> | null = null
async function checkUsername() {
  if (!form.username || !/^[a-zA-Z0-9_]{4,20}$/.test(form.username)) {
    usernameStatus.value = 'idle'; usernameMsg.value = ''; return
  }
  usernameStatus.value = 'checking'; usernameMsg.value = '正在检查用户名...'
  if (checkTimer) clearTimeout(checkTimer)
  checkTimer = setTimeout(async () => {
    try {
      const res = await findAllUsers()
      const exists = res.flag && res.data?.some((u: any) => u.username === form.username)
      if (exists) {
        usernameStatus.value = 'taken'; usernameMsg.value = '该用户名已被注册'
      } else {
        usernameStatus.value = 'ok'; usernameMsg.value = '用户名可用'
      }
    } catch {
      usernameStatus.value = 'idle'; usernameMsg.value = ''
    }
  }, 500)
}

// 步骤导航
async function nextStep() {
  if (currentStep.value === 0) {
    const valid = await step1Ref.value?.validate().catch(() => false)
    if (!valid) return
  } else if (currentStep.value === 1) {
    const valid = await step2Ref.value?.validate().catch(() => false)
    if (!valid) return
  }
  currentStep.value++
}

function prevStep() { currentStep.value-- }

async function handleRegister() {
  if (!agreed.value) { ElMessage.warning('请先同意服务协议'); return }
  submitting.value = true
  try {
    const res = await registerApi({
      username: form.username,
      password: form.password,
      gender: form.gender,
      telephone: form.telephone,
      birthday: form.birthday,
      remark: `${form.realname || ''} | ${form.email || ''} | ${form.remark || ''}`
    })
    if (res.flag) {
      ElMessage.success('注册成功！即将跳转登录页...')
      setTimeout(() => router.push('/admin/login'), 1500)
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch (e) {
    ElMessage.error('注册失败，请稍后重试')
  }
  submitting.value = false
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0a1628 0%, #0d2f50 40%, #0f4c6b 70%, #0b2d45 100%);
  position: relative;
  overflow: auto;
  padding: 40px 20px;
}

.register-bg-shapes .shape {
  position: fixed; border-radius: 50%; opacity: 0.05;
}
.shape-1 { width: 500px; height: 500px; background: #14b8a6; top: -100px; right: -100px; }
.shape-2 { width: 350px; height: 350px; background: #8b5cf6; bottom: -80px; left: -80px; }

.register-card {
  width: 540px;
  background: rgba(255,255,255,0.97);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0,0,0,.3);
  position: relative;
  z-index: 1;
}

.register-header { text-align: center; }
.brand-logo {
  width: 52px; height: 52px;
  margin: 0 auto 14px;
  background: linear-gradient(135deg, #14b8a6, #0d9488);
  border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 6px 20px rgba(20,184,166,.4);
}
.medical-cross { color: #fff; font-size: 26px; font-weight: 300; }
.register-header h2 { font-size: 20px; color: #1e293b; margin: 0 0 4px; }
.register-header p { font-size: 13px; color: #94a3b8; margin: 0; }

/* 字段提示 */
.field-hint { font-size: 12px; margin-top: 4px; }
.field-hint.ok { color: #10b981; }
.field-hint.taken { color: #ef4444; }
.field-hint.checking { color: #94a3b8; }

/* 密码强度 */
.password-strength {
  display: flex; align-items: center; gap: 10px; margin-top: 8px;

  .strength-bar {
    flex: 1; height: 4px; background: #e2e8f0; border-radius: 2px; overflow: hidden;
    .strength-fill { height: 100%; transition: width 0.3s; border-radius: 2px; }
    .strength-fill.weak { background: #ef4444; }
    .strength-fill.medium { background: #f59e0b; }
    .strength-fill.strong { background: #10b981; }
  }
  .strength-text { font-size: 12px; font-weight: 600; }
  .strength-text.weak { color: #ef4444; }
  .strength-text.medium { color: #f59e0b; }
  .strength-text.strong { color: #10b981; }
  .strength-score { font-size: 11px; color: #94a3b8; }
}

.password-rules {
  display: flex; flex-wrap: wrap; gap: 4px 14px; margin-top: 6px;
  span {
    font-size: 11px; color: #94a3b8; display: flex; align-items: center; gap: 3px;
    &.met { color: #10b981; }
  }
}

/* 步骤3确认卡片 */
.confirm-step {
  .confirm-card {
    background: #f8fafc; border-radius: 10px; padding: 16px; margin-bottom: 12px;
    h4 { font-size: 14px; color: #1e293b; margin: 0 0 10px; display: flex; align-items: center; gap: 6px; }
    .confirm-row {
      display: flex; justify-content: space-between; padding: 6px 0;
      font-size: 13px; color: #94a3b8;
      strong { color: #334155; }
      strong.weak { color: #ef4444; }
      strong.medium { color: #f59e0b; }
      strong.strong { color: #10b981; }
    }
  }
}

.step-actions {
  display: flex; justify-content: center; gap: 12px; margin-top: 24px;
  .submit-btn { min-width: 160px; }
}

.register-footer {
  text-align: center; margin-top: 20px; font-size: 13px; color: #94a3b8;
  a { color: #14b8a6; text-decoration: none; font-weight: 500; }
}
</style>
