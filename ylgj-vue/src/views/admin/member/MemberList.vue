<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchText" placeholder="搜索姓名/手机号/身份证号" clearable @clear="loadData" @keyup.enter="loadData" />
      <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon> 搜索</el-button>
    </div>
    <div class="admin-card">
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="sex" label="性别" width="60" />
        <el-table-column prop="phoneNumber" label="手机号" width="130" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="regTime" label="注册日期" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDelete(row)">删除</el-button>
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
import { findMemberPage, deleteMember } from '@/api/member'
import type { Member } from '@/types/models'

const searchText = ref(''); const tableData = ref<Member[]>([]); const loading = ref(false)
const currentPage = ref(1); const pageSize = ref(10); const total = ref(0)

async function loadData() {
  loading.value = true
  const res = await findMemberPage({ currentPage: currentPage.value, pageSize: pageSize.value, queryString: searchText.value })
  tableData.value = res.rows; total.value = res.total; loading.value = false
}
async function handleDelete(row: Member) {
  await ElMessageBox.confirm(`确定删除会员「${row.name}」吗？`, '确认删除', { type: 'warning' })
  await deleteMember(row.id!); loadData()
}
onMounted(loadData)
</script>
