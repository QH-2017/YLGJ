<template>
  <div>
    <div class="admin-card" style="max-width:500px">
      <h3 style="margin-bottom:16px">设置可预约日期和人数</h3>
      <el-form :model="form" label-width="120px">
        <el-form-item label="选择日期">
          <el-date-picker v-model="form.orderDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="可预约人数">
          <el-input-number v-model="form.number" :min="0" :max="999" style="width:100%" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleAdd">保存设置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="admin-card" style="margin-top:16px">
      <h3 style="margin-bottom:16px">已设置的预约日期</h3>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="orderDate" label="日期" width="160" />
        <el-table-column prop="number" label="可预约人数" width="140" align="center" />
        <el-table-column prop="reservations" label="已预约数" width="120" align="center" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" link @click="editRow = row; editForm.number = row.number; dialogVisible = true">修改人数</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 修改弹窗 -->
    <el-dialog v-model="dialogVisible" title="修改可预约人数" width="400px">
      <el-form :model="editForm">
        <el-form-item label="可预约人数">
          <el-input-number v-model="editForm.number" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { findAllOrdersetting, addOrdersetting, editOrdersetting } from '@/api/ordersetting'
import type { Ordersetting } from '@/types/models'

const tableData = ref<Ordersetting[]>([]); const loading = ref(false); const saving = ref(false)
const dialogVisible = ref(false); const editRow = ref<Ordersetting | null>(null)

const form = reactive({ orderDate: '', number: 50 })
const editForm = reactive({ number: 50 })

async function loadData() {
  loading.value = true
  const res = await findAllOrdersetting()
  if (res.flag) tableData.value = res.data.sort((a: any, b: any) => a.orderDate.localeCompare(b.orderDate))
  loading.value = false
}

async function handleAdd() {
  if (!form.orderDate) { ElMessage.warning('请选择日期'); return }
  saving.value = true
  await addOrdersetting({ orderDate: form.orderDate, number: form.number, reservations: 0 })
  saving.value = false
  loadData()
}

async function handleEdit() {
  if (!editRow.value) return
  await editOrdersetting({ ...editRow.value, number: editForm.number })
  ElMessage.success('修改成功')
  dialogVisible.value = false
  loadData()
}

onMounted(loadData)
</script>
