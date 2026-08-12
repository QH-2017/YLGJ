<template>
  <div class="admin-card" v-loading="loading">
    <h3 style="margin-bottom:20px">{{ isEdit ? '编辑检查组' : '新增检查组' }}</h3>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width:700px">
      <el-row :gutter="20">
        <el-col :span="12"><el-form-item label="编码" prop="code"><el-input v-model="form.code" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="名称" prop="name"><el-input v-model="form.name" /></el-form-item></el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12"><el-form-item label="助记码" prop="helpCode"><el-input v-model="form.helpCode" /></el-form-item></el-col>
        <el-col :span="12">
          <el-form-item label="适用性别"><el-select v-model="form.sex" style="width:100%">
            <el-option label="男" value="0" /><el-option label="女" value="1" /><el-option label="不限" value="2" />
          </el-select></el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="注意事项"><el-input v-model="form.attention" type="textarea" :rows="2" /></el-form-item>
      <el-form-item label="说明"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>

      <!-- 关联检查项 -->
      <el-form-item label="关联检查项">
        <el-checkbox-group v-model="form.checkitemIds">
          <el-checkbox v-for="item in checkitems" :key="item.id" :value="item.id!" :label="item.id">
            {{ item.name }} ({{ item.code }}) - ¥{{ item.price }}
          </el-checkbox>
        </el-checkbox-group>
        <span v-if="checkitems.length === 0" style="color:#ccc">暂无检查项</span>
      </el-form-item>

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
import { addCheckgroup, editCheckgroup, findCheckgroupById } from '@/api/checkgroup'
import { findAllCheckitem } from '@/api/checkitem'
import type { Checkitem } from '@/types/models'

const route = useRoute(); const router = useRouter()
const formRef = ref(); const saving = ref(false); const loading = ref(false)
const checkitems = ref<Checkitem[]>([])
const isEdit = !!route.params.id
const editId = Number(route.params.id)

const form = reactive({
  code: '', name: '', helpCode: '', sex: '2', attention: '', remark: '', checkitemIds: [] as number[]
})

const rules = {
  code: [{ required: true, message: '请输入编码' }],
  name: [{ required: true, message: '请输入名称' }],
  helpCode: [{ required: true, message: '请输入助记码' }]
}

onMounted(async () => {
  const itemRes = await findAllCheckitem()
  if (itemRes.flag) checkitems.value = itemRes.data
  if (isEdit) {
    loading.value = true
    const res = await findCheckgroupById(editId)
    if (res.flag) {
      const d = res.data
      Object.assign(form, { code: d.code, name: d.name, helpCode: d.helpCode, sex: d.sex, attention: d.attention || '', remark: d.remark || '', checkitemIds: d.checkitemIds || [] })
    }
    loading.value = false
  }
})

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  const data = { ...form, id: isEdit ? editId : undefined }
  const res = isEdit ? await editCheckgroup(data as any) : await addCheckgroup(data as any)
  if (res.flag) { ElMessage.success(isEdit ? '编辑成功' : '新增成功'); router.push('/admin/checkgroup') }
  saving.value = false
}
</script>
