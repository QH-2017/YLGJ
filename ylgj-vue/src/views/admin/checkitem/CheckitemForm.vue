<template>
  <div class="admin-card" v-loading="loading">
    <h3 style="margin-bottom:20px">{{ isEdit ? '编辑检查项' : '新增检查项' }}</h3>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width:700px">
      <el-row :gutter="20">
        <el-col :span="12"><el-form-item label="编码" prop="code"><el-input v-model="form.code" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="名称" prop="name"><el-input v-model="form.name" /></el-form-item></el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="类型" prop="type">
            <el-radio-group v-model="form.type"><el-radio value="检查">检查</el-radio><el-radio value="检验">检验</el-radio></el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="价格(元)"><el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" /></el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="适用性别"><el-select v-model="form.sex" style="width:100%"><el-option label="男" value="0" /><el-option label="女" value="1" /><el-option label="不限" value="2" /></el-select></el-form-item>
        </el-col>
        <el-col :span="12"><el-form-item label="适用年龄"><el-input v-model="form.age" placeholder="如：18-60" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="注意事项"><el-input v-model="form.attention" type="textarea" :rows="2" /></el-form-item>
      <el-form-item label="说明"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="saving" @click="handleSubmit">保存</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addCheckitem, editCheckitem, findCheckitemById } from '@/api/checkitem'

const route = useRoute(); const router = useRouter()
const formRef = ref(); const saving = ref(false); const loading = ref(false)
const isEdit = !!route.params.id; const editId = Number(route.params.id)

const form = reactive({ code: '', name: '', type: '检查', price: 0, sex: '2', age: '', attention: '', remark: '' })
const rules = {
  code: [{ required: true, message: '请输入编码' }],
  name: [{ required: true, message: '请输入名称' }],
  type: [{ required: true, message: '请选择类型' }]
}

onMounted(async () => {
  if (isEdit) {
    loading.value = true
    const res = await findCheckitemById(editId)
    if (res.flag) Object.assign(form, res.data)
    loading.value = false
  }
})

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  const data = { ...form, id: isEdit ? editId : undefined }
  const res = isEdit ? await editCheckitem(data as any) : await addCheckitem(data as any)
  if (res.flag) { ElMessage.success(isEdit ? '编辑成功' : '新增成功'); router.push('/admin/checkitem') }
  saving.value = false
}
</script>
