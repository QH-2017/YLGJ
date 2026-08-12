<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchText" placeholder="搜索编码/名称/助记码" clearable @clear="loadData" @keyup.enter="loadData" />
      <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon> 搜索</el-button>
      <el-button type="success" @click="$router.push('/admin/checkgroup/add')"><el-icon><Plus /></el-icon> 新增检查组</el-button>
    </div>
    <div class="admin-card">
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="code" label="编码" width="120" />
        <el-table-column prop="name" label="名称" min-width="160" />
        <el-table-column prop="helpCode" label="助记码" width="120" />
        <el-table-column prop="sex" label="适用性别" width="100">
          <template #default="{ row }">{{ sexMap[row.sex] || row.sex }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="说明" min-width="200" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="$router.push(`/admin/checkgroup/edit/${row.id}`)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;text-align:right">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total"
          :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @change="loadData" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import { findCheckgroupPage, deleteCheckgroup } from '@/api/checkgroup'
import type { Checkgroup } from '@/types/models'

const searchText = ref('')
const tableData = ref<Checkgroup[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const sexMap: Record<string, string> = { '0': '男', '1': '女', '2': '不限' }

async function loadData() {
  loading.value = true
  const res = await findCheckgroupPage({ currentPage: currentPage.value, pageSize: pageSize.value, queryString: searchText.value })
  tableData.value = res.rows; total.value = res.total
  loading.value = false
}

async function handleDelete(row: Checkgroup) {
  await ElMessageBox.confirm(`确定删除检查组「${row.name}」吗？`, '确认删除', { type: 'warning' })
  await deleteCheckgroup(row.id!); loadData()
}

onMounted(loadData)
</script>
