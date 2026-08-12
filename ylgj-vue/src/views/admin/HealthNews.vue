<template>
  <div>
    <div class="page-toolbar">
      <div>
        <h2 style="font-size:18px;color:#1e293b;margin:0">健康资讯管理</h2>
        <p style="color:#94a3b8;font-size:13px;margin:4px 0 0">发布和管理健康科普资讯、体检通知</p>
      </div>
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon> 发布资讯
      </el-button>
    </div>

    <!-- 资讯列表 -->
    <div class="news-grid">
      <div v-for="(item, idx) in newsList" :key="idx" class="news-card">
        <div class="news-img" :style="{ background: item.bgColor }">
          <el-icon :size="32"><component :is="item.icon" /></el-icon>
        </div>
        <div class="news-body">
          <div class="news-meta">
            <el-tag :type="item.tagType" size="small">{{ item.category }}</el-tag>
            <span class="news-date">{{ item.date }}</span>
          </div>
          <h4>{{ item.title }}</h4>
          <p>{{ item.summary }}</p>
          <div class="news-footer">
            <span><el-icon><View /></el-icon> {{ item.views }}</span>
            <span><el-icon><Star /></el-icon> {{ item.likes }}</span>
            <div>
              <el-button type="primary" link size="small" @click="editNews(item)">编辑</el-button>
              <el-popconfirm title="确定删除？" @confirm="deleteNews(idx)">
                <template #reference>
                  <el-button type="danger" link size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 发布/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑资讯' : '发布资讯'" width="560px" :close-on-click-modal="false">
      <el-form :model="newsForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="newsForm.title" placeholder="请输入资讯标题" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="newsForm.category" style="width:100%">
            <el-option label="健康科普" value="健康科普" />
            <el-option label="体检通知" value="体检通知" />
            <el-option label="季节养生" value="季节养生" />
            <el-option label="疾病预防" value="疾病预防" />
            <el-option label="饮食健康" value="饮食健康" />
          </el-select>
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="newsForm.summary" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="newsForm.content" type="textarea" :rows="6" placeholder="资讯正文..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const dialogVisible = ref(false)
const isEdit = ref(false)
const editIdx = ref(-1)

const newsForm = ref({ title: '', category: '健康科普', summary: '', content: '' })

const gradients = [
  'linear-gradient(135deg, #14b8a6, #0d9488)',
  'linear-gradient(135deg, #3b82f6, #6366f1)',
  'linear-gradient(135deg, #8b5cf6, #a855f7)',
  'linear-gradient(135deg, #f59e0b, #f97316)',
  'linear-gradient(135deg, #ef4444, #dc2626)',
  'linear-gradient(135deg, #06b6d4, #0891b2)'
]

const newsList = ref([
  {
    title: '夏季体检注意事项，这些项目不可忽视',
    category: '健康科普', tagType: 'success', date: '2026-07-30',
    summary: '夏季高温天气下体检需要注意哪些事项？心血管、血糖等项目为何夏季更需要关注？',
    icon: 'Sunny', bgColor: gradients[0], views: 1256, likes: 89
  },
  {
    title: '中老年人体检套餐推荐指南',
    category: '体检通知', tagType: 'warning', date: '2026-07-28',
    summary: '针对45岁以上人群，推荐心脑血管、肿瘤筛查、骨密度等重点检查项目。',
    icon: 'UserFilled', bgColor: gradients[1], views: 2341, likes: 156
  },
  {
    title: '三伏天养生全攻略：饮食+运动+作息',
    category: '季节养生', tagType: 'info', date: '2026-07-25',
    summary: '三伏天如何科学养生？从饮食调理到运动方式，全方位解读夏季养生要点。',
    icon: 'Orange', bgColor: gradients[2], views: 3890, likes: 267
  },
  {
    title: '心血管疾病早期筛查的重要性',
    category: '疾病预防', tagType: 'danger', date: '2026-07-22',
    summary: '心血管疾病是威胁健康的首要杀手，定期筛查是预防的关键。了解哪些检查能够及早发现问题。',
    icon: 'Warning', bgColor: gradients[3], views: 1876, likes: 134
  },
  {
    title: '体检前饮食禁忌：这些食物不能吃',
    category: '饮食健康', tagType: '', date: '2026-07-20',
    summary: '体检前饮食对检查结果影响很大，了解哪些食物需要避免，确保检查结果准确。',
    icon: 'DishDot', bgColor: gradients[4], views: 4521, likes: 312
  },
  {
    title: '2026年度健康体检套餐全新升级',
    category: '体检通知', tagType: 'warning', date: '2026-07-18',
    summary: '医疗管家2026年度体检套餐全面升级，新增AI智能分析、基因检测等高端项目。',
    icon: 'Promotion', bgColor: gradients[5], views: 5620, likes: 423
  }
])

function openAddDialog() {
  isEdit.value = false; editIdx.value = -1
  newsForm.value = { title: '', category: '健康科普', summary: '', content: '' }
  dialogVisible.value = true
}

function editNews(item: any) {
  isEdit.value = true; editIdx.value = newsList.value.indexOf(item)
  newsForm.value = { ...item }
  dialogVisible.value = true
}

function handleSave() {
  if (!newsForm.value.title) { ElMessage.warning('请输入标题'); return }
  if (isEdit.value && editIdx.value >= 0) {
    // 保留原有的图标和颜色
    newsList.value[editIdx.value] = { ...newsList.value[editIdx.value], ...newsForm.value }
    ElMessage.success('更新成功')
  } else {
    const idx = newsList.value.length % gradients.length
    newsList.value.unshift({
      ...newsForm.value,
      date: new Date().toISOString().slice(0, 10),
      icon: 'Document',
      bgColor: gradients[idx],
      views: 0, likes: 0,
      tagType: newsForm.value.category === '健康科普' ? 'success' : newsForm.value.category === '体检通知' ? 'warning' : 'info'
    })
    ElMessage.success('发布成功')
  }
  dialogVisible.value = false
}

function deleteNews(idx: number) {
  newsList.value.splice(idx, 1)
  ElMessage.success('删除成功')
}
</script>

<style lang="scss" scoped>
.page-toolbar {
  display: flex; justify-content: space-between; align-items: flex-start;
  margin-bottom: 16px;
}
.news-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;

  .news-card {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 1px 6px rgba(0,0,0,.04);
    display: flex;
    transition: box-shadow 0.25s, transform 0.2s;

    &:hover {
      box-shadow: 0 4px 16px rgba(0,0,0,.08);
      transform: translateY(-2px);
    }

    .news-img {
      width: 120px; min-height: 140px;
      display: flex; align-items: center; justify-content: center;
      color: rgba(255,255,255,.8);
      flex-shrink: 0;
    }

    .news-body {
      padding: 16px;
      flex: 1;
      display: flex; flex-direction: column;

      .news-meta {
        display: flex; align-items: center; gap: 8px; margin-bottom: 8px;
        .news-date { color: #94a3b8; font-size: 12px; }
      }
      h4 { font-size: 14px; color: #1e293b; margin: 0 0 6px; line-height: 1.4; }
      p { font-size: 12px; color: #94a3b8; margin: 0; line-height: 1.5; flex: 1; }

      .news-footer {
        display: flex; align-items: center; gap: 14px; margin-top: 12px;
        font-size: 12px; color: #94a3b8;
        span { display: flex; align-items: center; gap: 3px; }
        div:last-child { margin-left: auto; }
      }
    }
  }
}
</style>
