<template>
  <div class="admin-layout">
    <!-- 炫酷医疗科技风侧边栏 -->
    <aside class="admin-sidebar" :class="{ collapsed: appStore.sidebarCollapsed }">
      <!-- 装饰光效 -->
      <div class="sidebar-glow"></div>

      <!-- Logo 区域 -->
      <div class="sidebar-brand">
        <div class="brand-icon">
          <span class="medical-cross">+</span>
          <div class="brand-pulse"></div>
        </div>
        <transition name="brand-text">
          <div v-show="!appStore.sidebarCollapsed" class="brand-info">
            <span class="brand-title">医疗管家</span>
            <span class="brand-subtitle">Smart Health Manager</span>
          </div>
        </transition>
      </div>

      <!-- 分隔线 -->
      <div class="sidebar-divider"></div>

      <!-- 用户信息卡片 -->
      <div class="sidebar-user" v-show="!appStore.sidebarCollapsed">
        <div class="user-avatar">
          <el-icon :size="22"><UserFilled /></el-icon>
        </div>
        <div class="user-info">
          <span class="user-name">{{ userStore.username || '管理员' }}</span>
          <span class="user-role">
            <span class="role-dot"></span> {{ userStore.roleLabel }}
          </span>
        </div>
      </div>

      <!-- 导航菜单 -->
      <nav class="sidebar-nav">
        <div
          v-for="item in navItems"
          :key="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
          @click="navigate(item.path)"
        >
          <span class="nav-icon">
            <el-icon :size="18"><component :is="item.icon" /></el-icon>
          </span>
          <span class="nav-label">{{ item.title }}</span>
          <span v-if="isActive(item.path)" class="nav-indicator"></span>
        </div>
      </nav>

      <!-- 底部系统信息 -->
      <div class="sidebar-footer" v-show="!appStore.sidebarCollapsed">
        <div class="footer-clock">
          <el-icon :size="14"><Clock /></el-icon>
          <span>{{ currentTime }}</span>
        </div>
        <div class="footer-info">
          <span class="sys-ver">v2.0 · Medical</span>
        </div>
      </div>

      <!-- 折叠按钮 -->
      <div class="collapse-btn" @click="appStore.toggleSidebar()">
        <el-icon :size="16">
          <DArrowLeft v-if="!appStore.sidebarCollapsed" />
          <DArrowRight v-else />
        </el-icon>
      </div>
    </aside>

    <!-- 主区域 -->
    <div class="admin-main">
      <!-- 顶部导航栏 -->
      <header class="admin-header">
        <div class="header-left">
          <div class="header-breadcrumb">
            <el-breadcrumb separator="›">
              <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">
                <el-icon><HomeFilled /></el-icon> 首页
              </el-breadcrumb-item>
              <el-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
        </div>

        <div class="header-right">
          <!-- 快捷操作 -->
          <el-tooltip content="导出报表" placement="bottom">
            <span class="header-btn" @click="$router.push('/admin/export')">
              <el-icon :size="16"><Download /></el-icon>
            </span>
          </el-tooltip>
          <el-tooltip content="消息通知" placement="bottom">
            <span class="header-btn notify-btn" @click="handleNotifyClick">
              <el-icon :size="16"><Bell /></el-icon>
              <span v-if="notifyCount > 0" class="notify-badge">{{ notifyCount }}</span>
            </span>
          </el-tooltip>

          <div class="header-divider"></div>

          <!-- 用户下拉 -->
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="header-user">
              <div class="header-avatar">
                <el-icon :size="16"><UserFilled /></el-icon>
              </div>
              <span class="header-username">{{ userStore.username }}</span>
              <el-icon :size="12"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon> 个人中心
                </el-dropdown-item>
                <el-dropdown-item command="password">
                  <el-icon><Lock /></el-icon> 修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区 -->
      <div class="admin-content">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import { adminRoutes } from '@/router'
import { getMyMenus } from '@/api/menu'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

interface NavItem { path: string; title: string; icon: string }

// RBAC 动态菜单：优先渲染后端「当前用户可见菜单」，接口失败/为空时回退静态路由
const backendMenus = ref<any[]>([])
const navItems = computed<NavItem[]>(() => {
  const dyn = backendMenus.value
    .filter((m: any) => m && m.path && m.path.startsWith('/admin/'))
    .map((m: any) => ({ path: m.path, title: m.name || m.path, icon: m.icon || 'Menu' }))
  if (dyn.length) return dyn
  return (adminRoutes as any[])
    .filter((r: any) => !r.meta?.hidden && r.path)
    .map((r: any) => ({ path: '/admin/' + r.path, title: r.meta?.title, icon: r.meta?.icon }))
})

const currentTitle = computed(() => (route.meta?.title as string) || '')
const currentTime = ref('')
// 未读消息数（点击铃铛后清零并持久化，刷新后保持已读状态）
const notifyCount = ref(Number(localStorage.getItem('notify_count') ?? 3))

function handleNotifyClick() {
  notifyCount.value = 0
  localStorage.setItem('notify_count', '0')
  router.push('/admin/news')
}

let timer: number | null = null

function updateTime() {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

onMounted(async () => {
  updateTime()
  timer = window.setInterval(updateTime, 1000)
  try {
    const res = await getMyMenus()
    if (res.flag && res.data?.length) backendMenus.value = res.data
  } catch (e) { /* 拉取失败则回退静态路由 */ }
})
onUnmounted(() => { if (timer) clearInterval(timer) })

function isActive(path: string) {
  return route.path === path || route.path.startsWith(path + '/')
}

function navigate(path: string) {
  router.push(path)
}

function handleCommand(cmd: string) {
  if (cmd === 'profile') router.push('/admin/profile')
  else if (cmd === 'password') router.push('/admin/password')
  else if (cmd === 'logout') { userStore.logout(); router.push('/admin/login') }
}
</script>

<style lang="scss" scoped>
// ==================== 布局容器 ====================
.admin-layout {
  height: 100vh;
  display: flex;
  overflow: hidden;
}

// ==================== 侧边栏 ====================
.admin-sidebar {
  position: relative;
  width: 240px;
  min-width: 240px;
  height: 100vh;
  background: linear-gradient(175deg, #0a1628 0%, #0c2540 30%, #0d2f50 60%, #0b1e38 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.35s cubic-bezier(0.4, 0, 0.2, 1), min-width 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  z-index: 100;

  &.collapsed {
    width: 68px;
    min-width: 68px;
  }
}

// 背景光效
.sidebar-glow {
  position: absolute;
  top: -120px;
  left: -60px;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(20, 184, 166, 0.08) 0%, transparent 70%);
  pointer-events: none;
  transition: opacity 0.35s;
}

// 品牌区域
.sidebar-brand {
  display: flex;
  align-items: center;
  padding: 20px 16px 16px;
  gap: 12px;

  .brand-icon {
    position: relative;
    width: 40px;
    height: 40px;
    min-width: 40px;
    background: linear-gradient(135deg, #14b8a6 0%, #0d9488 100%);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 15px rgba(20, 184, 166, 0.35);

    .medical-cross {
      color: #fff;
      font-size: 22px;
      font-weight: 300;
      line-height: 1;
    }

    .brand-pulse {
      position: absolute;
      inset: -3px;
      border-radius: 15px;
      border: 1.5px solid rgba(20, 184, 166, 0.5);
      animation: brandPulse 2.5s ease-in-out infinite;
    }
  }

  .brand-info {
    overflow: hidden;

    .brand-title {
      display: block;
      color: #e2e8f0;
      font-size: 16px;
      font-weight: 700;
      letter-spacing: 1px;
      white-space: nowrap;
    }
    .brand-subtitle {
      display: block;
      color: rgba(148, 163, 184, 0.8);
      font-size: 10px;
      font-weight: 400;
      letter-spacing: 0.5px;
      white-space: nowrap;
      margin-top: 2px;
    }
  }
}

@keyframes brandPulse {
  0%, 100% { opacity: 0.5; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.08); }
}

.sidebar-divider {
  margin: 0 16px;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, rgba(148,163,184,0.2) 20%, rgba(148,163,184,0.2) 80%, transparent 100%);
}

// 用户卡片
.sidebar-user {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  gap: 10px;

  .user-avatar {
    width: 38px;
    height: 38px;
    min-width: 38px;
    border-radius: 10px;
    background: linear-gradient(135deg, rgba(59,130,246,0.3) 0%, rgba(99,102,241,0.3) 100%);
    border: 1.5px solid rgba(99,102,241,0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #94a3b8;
  }

  .user-info {
    display: flex;
    flex-direction: column;
    gap: 3px;
    overflow: hidden;

    .user-name {
      color: #e2e8f0;
      font-size: 13px;
      font-weight: 600;
      white-space: nowrap;
    }
    .user-role {
      color: #64748b;
      font-size: 11px;
      display: flex;
      align-items: center;
      gap: 5px;

      .role-dot {
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: #14b8a6;
        box-shadow: 0 0 6px rgba(20,184,166,0.6);
      }
    }
  }
}

// 导航区
.sidebar-nav {
  flex: 1;
  padding: 12px 8px;
  overflow-y: auto;
  overflow-x: hidden;

  &::-webkit-scrollbar { width: 3px; }
  &::-webkit-scrollbar-thumb { background: rgba(148,163,184,0.2); border-radius: 2px; }

  .nav-item {
    position: relative;
    display: flex;
    align-items: center;
    padding: 11px 14px;
    margin-bottom: 3px;
    border-radius: 10px;
    cursor: pointer;
    color: #94a3b8;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    user-select: none;
    overflow: hidden;

    .nav-icon {
      width: 20px;
      min-width: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 10px;
      transition: all 0.25s;
    }

    .nav-label {
      font-size: 13.5px;
      font-weight: 500;
      white-space: nowrap;
      transition: all 0.25s;
    }

    .nav-indicator {
      position: absolute;
      right: 4px;
      width: 3px;
      height: 18px;
      border-radius: 3px;
      background: #14b8a6;
      box-shadow: 0 0 8px rgba(20,184,166,0.6);
    }

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%) scaleY(0);
      width: 3px;
      height: 60%;
      border-radius: 0 3px 3px 0;
      background: #14b8a6;
      transition: transform 0.25s;
    }

    &:hover {
      background: rgba(255,255,255,0.04);
      color: #cbd5e1;

      .nav-icon { color: #5eead4; }
    }

    &.active {
      background: linear-gradient(90deg, rgba(20,184,166,0.12) 0%, rgba(20,184,166,0.04) 100%);
      color: #f1f5f9;

      .nav-icon { color: #14b8a6; }
      .nav-label { font-weight: 600; }

      &::before { transform: translateY(-50%) scaleY(1); }
    }
  }
}

// 侧边栏底部
.sidebar-footer {
  padding: 12px 16px;
  border-top: 1px solid rgba(148,163,184,0.08);

  .footer-clock {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #64748b;
    font-size: 11px;
    font-family: 'SF Mono', 'JetBrains Mono', 'Consolas', monospace;
  }

  .footer-info {
    margin-top: 4px;
    .sys-ver {
      color: rgba(100,116,139,0.6);
      font-size: 10px;
    }
  }
}

// 折叠按钮
.collapse-btn {
  position: absolute;
  bottom: 20px;
  right: -12px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1e3a5f 0%, #162d4a 100%);
  border: 1.5px solid rgba(148,163,184,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #94a3b8;
  z-index: 10;
  transition: all 0.25s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.3);

  &:hover {
    color: #14b8a6;
    border-color: rgba(20,184,166,0.4);
    box-shadow: 0 2px 12px rgba(0,0,0,0.4);
  }
}

.collapsed .sidebar-glow { opacity: 0; }
.collapsed .sidebar-nav .nav-item .nav-icon { margin-right: 0; }
.collapsed .collapse-btn { right: 20px; bottom: 14px; }

// ==================== 主区域 ====================
.admin-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
  background: #f0f4f8;
}

// 顶栏
.admin-header {
  height: 52px;
  min-height: 52px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  border-bottom: 1px solid #e2e8f0;
  z-index: 50;
  box-shadow: 0 1px 3px rgba(0,0,0,.03);

  .header-left {
    display: flex;
    align-items: center;
  }

  .header-breadcrumb {
    :deep(.el-breadcrumb) {
      font-size: 13px;
      .el-breadcrumb__inner { color: #64748b; font-weight: 500; }
      .el-breadcrumb__item:last-child .el-breadcrumb__inner { color: #0f172a; font-weight: 600; }
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .header-btn {
    width: 34px;
    height: 34px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: #64748b;
    transition: all 0.2s;

    &:hover { background: #f1f5f9; color: #14b8a6; }

    &.notify-btn { position: relative; }
  }

  .notify-badge {
    position: absolute;
    top: 4px;
    right: 5px;
    width: 15px;
    height: 15px;
    border-radius: 50%;
    background: #ef4444;
    color: #fff;
    font-size: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 600;
    box-shadow: 0 0 0 2px #fff;
  }

  .header-divider {
    width: 1px;
    height: 22px;
    background: #e2e8f0;
    margin: 0 6px;
  }

  .header-user {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 8px;
    transition: background 0.2s;

    &:hover { background: #f8fafc; }

    .header-avatar {
      width: 30px;
      height: 30px;
      border-radius: 8px;
      background: linear-gradient(135deg, #14b8a6 0%, #0d9488 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
    }
    .header-username { font-size: 13px; font-weight: 500; color: #334155; }
  }
}

// 内容区
.admin-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;

  &::-webkit-scrollbar { width: 5px; }
  &::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 3px; }
}

// 页面切换动画
.page-fade-enter-active, .page-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.page-fade-enter-from { opacity: 0; transform: translateY(8px); }
.page-fade-leave-to { opacity: 0; transform: translateY(-8px); }

// 品牌文字过渡
.brand-text-enter-active, .brand-text-leave-active { transition: opacity 0.2s; }
.brand-text-enter-from, .brand-text-leave-to { opacity: 0; }
</style>
