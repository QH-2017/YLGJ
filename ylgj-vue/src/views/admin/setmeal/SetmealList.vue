<template>
  <div>
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchText" placeholder="搜索套餐编码/名称/助记码" clearable @clear="loadData" @keyup.enter="loadData" />
      <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon> 搜索</el-button>
      <el-button type="success" @click="$router.push('/admin/setmeal/add')">
        <el-icon><Plus /></el-icon> 新增套餐
      </el-button>
    </div>

    <!-- 表格 -->
    <div class="admin-card">
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="code" label="套餐编码" width="120" />
        <el-table-column prop="name" label="套餐名称" min-width="180" />
        <el-table-column prop="sex" label="适用性别" width="100">
          <template #default="{ row }">{{ sexMap[row.sex] || row.sex }}</template>
        </el-table-column>
        <el-table-column prop="age" label="适用年龄" width="100" />
        <el-table-column prop="price" label="价格(元)" width="100" align="right">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="套餐图片" width="100">
          <template #default="{ row }">
            <el-image :src="imageSrc(row.img)" fit="cover" style="width:60px;height:40px;border-radius:4px" preview-teleported>
              <template #error>
                <!-- 图片缺失/加载失败时回落到占位图，避免显示"加载失败" -->
                <img :src="PLACEHOLDER_IMG" alt="套餐图片" style="width:60px;height:40px;border-radius:4px;object-fit:cover" />
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="$router.push(`/admin/setmeal/edit/${row.id}`)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;text-align:right">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import { findSetmealPage, deleteSetmeal } from '@/api/setmeal'
import type { Setmeal } from '@/types/models'

const searchText = ref('')
const tableData = ref<Setmeal[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const sexMap: Record<string, string> = { '0': '男', '1': '女', '2': '不限' }

/** 缺图时的统一占位图（后端 /static 静态资源提供，随代码打包可迁移） */
const PLACEHOLDER_IMG = '/static/setmeal-placeholder.svg'

/** 图片地址归一：无图 / 裸文件名 → 占位图；http(s) 或 /uploads 完整路径原样使用 */
function imageSrc(img?: string | null): string {
  if (img && (img.startsWith('/') || /^https?:\/\//.test(img))) return img
  return PLACEHOLDER_IMG
}

async function loadData() {
  loading.value = true
  try {
    const res = await findSetmealPage({ currentPage: currentPage.value, pageSize: pageSize.value, queryString: searchText.value })
    tableData.value = res.rows
    total.value = res.total
  } catch (e) { /* handled by interceptor */ }
  loading.value = false
}

async function handleDelete(row: Setmeal) {
  await ElMessageBox.confirm(`确定删除套餐「${row.name}」吗？`, '确认删除', { type: 'warning' })
  await deleteSetmeal(row.id!)
  loadData()
}

onMounted(loadData)
</script>
