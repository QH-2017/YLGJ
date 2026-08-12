<template>
  <div>
    <div class="m-card">
      <h3 style="margin-bottom:16px">体检预约</h3>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="体检人姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" size="large" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex"><el-radio value="男">男</el-radio><el-radio value="女">女</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="telephone">
          <el-input v-model="form.telephone" placeholder="请输入11位手机号" size="large" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入18位身份证号" size="large" />
        </el-form-item>
        <el-form-item label="预约日期" prop="orderDate">
          <el-date-picker v-model="form.orderDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" size="large" />
        </el-form-item>
        <el-button type="success" size="large" style="width:100%;height:46px;font-size:16px" :loading="submitting" @click="handleSubmit">
          确认预约
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submitOrder } from '@/api/order'

const route = useRoute(); const router = useRouter()
const formRef = ref(); const submitting = ref(false)

const form = reactive({
  name: '', sex: '男', telephone: '', idCard: '',
  setmealId: String(route.params.setmealId), orderDate: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名' }],
  sex: [{ required: true, message: '请选择性别' }],
  telephone: [{ required: true, pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号' }],
  idCard: [{ required: true, pattern: /^\d{17}[\dXx]$/, message: '请输入正确的18位身份证号' }],
  orderDate: [{ required: true, message: '请选择预约日期' }]
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const res = await submitOrder({ ...form })
    if (res.flag) {
      ElMessage.success('预约成功！')
      router.push('/orders')
    }
  } catch (e) { /* handled */ }
  submitting.value = false
}
</script>
