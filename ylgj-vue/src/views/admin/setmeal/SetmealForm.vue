<template>
  <div class="admin-card" v-loading="loading">
    <h3 style="margin-bottom:20px">{{ isEdit ? '编辑套餐' : '新增套餐' }}</h3>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width:700px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="套餐编码" prop="code">
            <el-input v-model="form.code" placeholder="如：0001" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="套餐名称" prop="name">
            <el-input v-model="form.name" placeholder="如：入职体检套餐" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="助记码" prop="helpCode">
            <el-input v-model="form.helpCode" placeholder="拼音首字母" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="价格(元)" prop="price">
            <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="适用性别" prop="sex">
            <el-select v-model="form.sex" style="width:100%">
              <el-option label="男" value="0" />
              <el-option label="女" value="1" />
              <el-option label="不限" value="2" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="适用年龄" prop="age">
            <el-input v-model="form.age" placeholder="如：18-60" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="注意事项">
        <el-input v-model="form.attention" type="textarea" :rows="2" />
      </el-form-item>
      <el-form-item label="套餐说明">
        <el-input v-model="form.remark" type="textarea" :rows="3" />
      </el-form-item>

      <!-- 图片上传 -->
      <el-form-item label="套餐图片">
        <el-upload
          :action="uploadUrl"
          :before-upload="beforeUpload"
          :on-success="onUploadSuccess"
          :on-error="onUploadError"
          :show-file-list="false"
          accept="image/*"
        >
          <template v-if="form.img">
            <el-image :src="form.img" fit="cover" style="width:120px;height:80px;border-radius:6px" />
            <el-button type="danger" size="small" style="margin-left:10px" @click.stop="form.img = ''">删除</el-button>
          </template>
          <el-button v-else type="primary">
            <el-icon><Upload /></el-icon> 上传图片
          </el-button>
        </el-upload>
      </el-form-item>

      <!-- 检查组选择 -->
      <el-form-item label="关联检查组" prop="checkgroupIds">
        <el-checkbox-group v-model="form.checkgroupIds">
          <el-checkbox v-for="g in checkgroups" :key="g.id" :value="g.id!" :label="g.id">
            {{ g.name }} ({{ g.code }})
          </el-checkbox>
        </el-checkbox-group>
        <span v-if="checkgroups.length === 0" style="color:#ccc">暂无检查组数据</span>
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
import { addSetmeal, editSetmeal, findSetmealById, uploadSetmealImg } from '@/api/setmeal'
import { findAllCheckgroup } from '@/api/checkgroup'
import type { Checkgroup } from '@/types/models'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const saving = ref(false)
const loading = ref(false)
const checkgroups = ref<Checkgroup[]>([])

const isEdit = !!route.params.id
const editId = Number(route.params.id)
const uploadUrl = '/setmeal/upload.do'

const form = reactive({
  code: '', name: '', helpCode: '', sex: '2', age: '', price: 0, attention: '', remark: '', img: '',
  checkgroupIds: [] as number[]
})

const rules = {
  code: [{ required: true, message: '请输入套餐编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  helpCode: [{ required: true, message: '请输入助记码', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  sex: [{ required: true, message: '请选择适用性别', trigger: 'change' }]
}

onMounted(async () => {
  // 加载检查组列表
  const gRes = await findAllCheckgroup()
  if (gRes.flag) checkgroups.value = gRes.data

  if (isEdit) {
    loading.value = true
    const res = await findSetmealById(editId)
    if (res.flag) {
      const d = res.data
      Object.assign(form, {
        code: d.code, name: d.name, helpCode: d.helpCode, sex: d.sex, age: d.age,
        price: d.price, attention: d.attention || '', remark: d.remark || '', img: d.img || '',
        checkgroupIds: (d.setmealCheckGroups || []).map((s: any) => s.checkgroupId)
      })
    }
    loading.value = false
  }
})

function beforeUpload(file: File) {
  const isValid = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type)
  if (!isValid) ElMessage.error('仅支持 JPG/PNG/GIF 格式')
  return false // 阻止默认上传行为，手动调用 API
}

async function onUploadSuccess(response: any) {
  if (response.flag) {
    form.img = response.data.url
    ElMessage.success('上传成功')
  }
}

function onUploadError() {
  ElMessage.error('上传失败')
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const submitData = { ...form, id: isEdit ? editId : undefined }
    const res = isEdit ? await editSetmeal(submitData as any) : await addSetmeal(submitData as any)
    if (res.flag) {
      ElMessage.success(isEdit ? '编辑成功' : '新增成功')
      router.push('/admin/setmeal')
    }
  } catch (e) { /* handled by interceptor */ }
  saving.value = false
}
</script>
