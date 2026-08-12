import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

// 管理后台路由
const adminRoutes: RouteRecordRaw[] = [
  {
    path: 'dashboard',
    name: 'AdminDashboard',
    component: () => import('@/views/admin/Dashboard.vue'),
    meta: { title: '数据概览', icon: 'Odometer' }
  },
  {
    path: 'setmeal',
    name: 'SetmealList',
    component: () => import('@/views/admin/setmeal/SetmealList.vue'),
    meta: { title: '套餐信息管理', icon: 'DishDot' }
  },
  {
    path: 'setmeal/add',
    name: 'SetmealAdd',
    component: () => import('@/views/admin/setmeal/SetmealForm.vue'),
    meta: { title: '新增套餐', hidden: true }
  },
  {
    path: 'setmeal/edit/:id',
    name: 'SetmealEdit',
    component: () => import('@/views/admin/setmeal/SetmealForm.vue'),
    meta: { title: '编辑套餐', hidden: true }
  },
  {
    path: 'checkgroup',
    name: 'CheckgroupList',
    component: () => import('@/views/admin/checkgroup/CheckgroupList.vue'),
    meta: { title: '检查组管理', icon: 'Collection' }
  },
  {
    path: 'checkgroup/add',
    name: 'CheckgroupAdd',
    component: () => import('@/views/admin/checkgroup/CheckgroupForm.vue'),
    meta: { title: '新增检查组', hidden: true }
  },
  {
    path: 'checkgroup/edit/:id',
    name: 'CheckgroupEdit',
    component: () => import('@/views/admin/checkgroup/CheckgroupForm.vue'),
    meta: { title: '编辑检查组', hidden: true }
  },
  {
    path: 'checkitem',
    name: 'CheckitemList',
    component: () => import('@/views/admin/checkitem/CheckitemList.vue'),
    meta: { title: '检查项管理', icon: 'List' }
  },
  {
    path: 'checkitem/add',
    name: 'CheckitemAdd',
    component: () => import('@/views/admin/checkitem/CheckitemForm.vue'),
    meta: { title: '新增检查项', hidden: true }
  },
  {
    path: 'checkitem/edit/:id',
    name: 'CheckitemEdit',
    component: () => import('@/views/admin/checkitem/CheckitemForm.vue'),
    meta: { title: '编辑检查项', hidden: true }
  },
  {
    path: 'member',
    name: 'MemberList',
    component: () => import('@/views/admin/member/MemberList.vue'),
    meta: { title: '会员管理', icon: 'User' }
  },
  {
    path: 'order',
    name: 'OrderList',
    component: () => import('@/views/admin/order/OrderList.vue'),
    meta: { title: '预约管理', icon: 'Calendar' }
  },
  {
    path: 'ordersetting',
    name: 'OrderSetting',
    component: () => import('@/views/admin/ordersetting/OrderSetting.vue'),
    meta: { title: '预约条件设置', icon: 'Setting' }
  },
  {
    path: 'report',
    name: 'Report',
    component: () => import('@/views/admin/report/Report.vue'),
    meta: { title: '统计分析', icon: 'TrendCharts' }
  },
  // ====== 新增功能模块 ======
  {
    path: 'profile',
    name: 'AdminProfile',
    component: () => import('@/views/admin/Profile.vue'),
    meta: { title: '个人中心', icon: 'Avatar' }
  },
  {
    path: 'users',
    name: 'UserManagement',
    component: () => import('@/views/admin/UserList.vue'),
    meta: { title: '系统用户管理', icon: 'UserFilled' }
  },
  {
    path: 'export',
    name: 'ExportCenter',
    component: () => import('@/views/admin/ExportCenter.vue'),
    meta: { title: '数据导出中心', icon: 'Download' }
  },
  {
    path: 'news',
    name: 'HealthNews',
    component: () => import('@/views/admin/HealthNews.vue'),
    meta: { title: '健康资讯管理', icon: 'Bell' }
  },
  {
    path: 'password',
    name: 'ChangePassword',
    component: () => import('@/views/admin/ChangePassword.vue'),
    meta: { title: '修改密码', hidden: true }
  }
]

// 移动端路由
const mobileRoutes: RouteRecordRaw[] = [
  {
    path: '',
    name: 'MobileHome',
    component: () => import('@/views/mobile/Home.vue'),
    meta: { title: '医疗管家' }
  },
  {
    path: 'setmeal',
    name: 'MobileSetmealList',
    component: () => import('@/views/mobile/SetmealList.vue'),
    meta: { title: '体检套餐' }
  },
  {
    path: 'setmeal/:id',
    name: 'MobileSetmealDetail',
    component: () => import('@/views/mobile/SetmealDetail.vue'),
    meta: { title: '套餐详情' }
  },
  {
    path: 'order/:setmealId',
    name: 'MobileOrderForm',
    component: () => import('@/views/mobile/OrderForm.vue'),
    meta: { title: '提交预约' }
  },
  {
    path: 'orders',
    name: 'MobileOrders',
    component: () => import('@/views/mobile/OrderList.vue'),
    meta: { title: '我的预约' }
  },
  {
    path: 'ai',
    name: 'MobileAiChat',
    component: () => import('@/views/mobile/AiChat.vue'),
    meta: { title: 'AI 健康助手' }
  },
  {
    path: 'health',
    name: 'MobileHealth',
    component: () => import('@/views/mobile/HealthRecord.vue'),
    meta: { title: '健康档案' }
  },
  {
    path: 'profile',
    name: 'MobileProfile',
    component: () => import('@/views/mobile/Profile.vue'),
    meta: { title: '个人中心' }
  }
]

const routes: RouteRecordRaw[] = [
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/admin/register',
    name: 'AdminRegister',
    component: () => import('@/views/admin/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: adminRoutes,
    meta: { requiresAuth: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/MobileLayout.vue'),
    children: mobileRoutes
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/shared/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

// 路由守卫 - 校验 JWT Token 登录状态
router.beforeEach((to, _from, next) => {
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('admin_token')
    if (!token) {
      next('/admin/login')
    } else {
      next()
    }
  } else {
    next()
  }
})

export { adminRoutes, mobileRoutes }
export default router
