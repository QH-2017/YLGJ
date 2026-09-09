<template>
  <div class="mobile-layout">
    <!-- 顶部导航 -->
    <div class="mobile-header" v-if="showHeader">
      <span class="back-btn" v-if="showBack" @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
      </span>
      <span class="title">{{ title }}</span>
      <span style="width:24px"></span>
    </div>

    <!-- 内容区 -->
    <div class="mobile-content" :style="{ paddingBottom: showTabBar ? '20px' : '0' }">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>

    <!-- 底部 Tab 栏 -->
    <div class="tab-bar" v-if="showTabBar">
      <router-link
        v-for="tab in tabs"
        :key="tab.path"
        :to="tab.path"
        class="tab-item"
        :class="{ active: route.path === tab.path }"
      >
        <el-icon><component :is="tab.icon" /></el-icon>
        <span>{{ tab.label }}</span>
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const tabRoutes = ['/mobile', '/mobile/setmeal', '/mobile/ai', '/mobile/profile']
const showTabBar = computed(() => tabRoutes.includes(route.path) || route.path.startsWith('/mobile/setmeal') && !route.params.id)
const showHeader = computed(() => true)
const showBack = computed(() => route.path !== '/mobile' && route.path !== '/mobile/setmeal' && route.path !== '/mobile/ai' && route.path !== '/mobile/profile')

const title = computed(() => (route.meta?.title as string) || '医疗管家')

const tabs = [
  { path: '/mobile', label: '首页', icon: 'House' },
  { path: '/mobile/setmeal', label: '体检套餐', icon: 'DishDot' },
  { path: '/mobile/ai', label: 'AI助手', icon: 'ChatDotRound' },
  { path: '/mobile/profile', label: '我的', icon: 'User' }
]
</script>
