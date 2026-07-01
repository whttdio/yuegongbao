import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout'
import { resolvePortalLoginPath } from '@/utils/portal'

export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index.vue')
      }
    ]
  },
  {
    path: '/login',
    redirect: () => resolvePortalLoginPath()
  },
  {
    path: '/login/:portalCode(ygb|azb)',
    component: () => import('@/views/login.vue'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register.vue'),
    hidden: true
  },
  {
    path: '/portal/official',
    component: () => import('@/views/portal/official/index.vue'),
    hidden: true,
    meta: { title: '安责保官网', portalCode: 'azb' }
  },
  {
    path: '/officialSite',
    component: () => import('@/views/portal/official/index.vue'),
    hidden: true,
    meta: { title: '安责保官网', portalCode: 'azb' }
  },
  {
    path: '/portal/ygb-official',
    component: () => import('@/views/portal/ygb-official/index.vue'),
    hidden: true,
    meta: { title: '粤工保官网', portalCode: 'ygb' }
  },
  {
    path: '/ygbOfficialSite',
    component: () => import('@/views/portal/ygb-official/index.vue'),
    hidden: true,
    meta: { title: '粤工保官网', portalCode: 'ygb' }
  },
  {
    path: '/portal/content/:contentId',
    component: () => import('@/views/portal/content/detail.vue'),
    hidden: true,
    meta: { title: '内容详情' }
  },
  {
    path: '/portal/job/:jobId',
    component: () => import('@/views/portal/job/detail.vue'),
    hidden: true,
    meta: { title: '岗位详情' }
  },
  {
    path: '',
    component: Layout,
    redirect: '/index',
    children: [
      {
        path: '/index',
        component: () => import('@/views/index.vue'),
        name: 'Index',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/lock',
    component: () => import('@/views/lock.vue'),
    hidden: true,
    meta: { title: '锁定屏幕' }
  },
  {
    path: '/cockpit-screen/ygb',
    component: () => import('@/views/ygb/cockpit/index.vue'),
    name: 'YgbCockpitScreen',
    hidden: true,
    meta: { title: '驾驶舱大屏', standalone: true }
  },
  {
    path: '/cockpit-screen/azb',
    component: () => import('@/views/azb/cockpit/index.vue'),
    name: 'AzbCockpitScreen',
    hidden: true,
    meta: { title: '驾驶舱大屏', standalone: true }
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: 'profile/:activeTab?',
        component: () => import('@/views/system/user/profile/index.vue'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  },
  {
    path: '/401',
    component: () => import('@/views/error/401.vue'),
    hidden: true
  },
  {
    path: '/:pathMatch(.*)*',
    component: () => import('@/views/error/404.vue'),
    hidden: true
  }
]

export const dynamicRoutes = [
  {
    path: '/ygb/worker-message',
    component: Layout,
    hidden: true,
    permissions: ['ygb:workerMessage:list'],
    children: [
      {
        path: 'complaint',
        component: () => import('@/views/ygb/workerComplaint/index.vue'),
        name: 'YgbWorkerComplaint',
        meta: { title: '劳动者投诉管理', activeMenu: '/operation/workerComplaint' }
      },
      {
        path: 'legal-consult',
        component: () => import('@/views/ygb/workerLegalConsult/index.vue'),
        name: 'YgbWorkerLegalConsult',
        meta: { title: '劳动者法律咨询管理', activeMenu: '/operation/workerLegalConsult' }
      },
      {
        path: 'feedback',
        component: () => import('@/views/ygb/workerFeedback/index.vue'),
        name: 'YgbWorkerFeedback',
        meta: { title: '劳动者反馈管理', activeMenu: '/operation/workerFeedback' }
      },
      {
        path: 'upload-record',
        component: () => import('@/views/ygb/workerUploadRecord/index.vue'),
        name: 'YgbWorkerUploadRecord',
        meta: { title: '劳动者上传归档管理', activeMenu: '/operation/workerUploadRecord' }
      }
    ]
  },
  {
    path: '/citizen-service',
    component: Layout,
    hidden: true,
    permissions: ['ygb:citizenService:list', 'ygb:portalContent:list'],
    children: [
      {
        path: 'overview',
        component: () => import('@/views/ygb/citizenService/overview/index.vue'),
        name: 'YgbCitizenServiceOverview',
        meta: { title: '便民服务总览', activeMenu: '/citizen-service/warmMap' }
      }
    ]
  },
  {
    path: '/ygb/worker-activity',
    component: Layout,
    hidden: true,
    permissions: ['ygb:workerActivity:list'],
    children: [
      {
        path: 'index',
        component: () => import('@/views/ygb/workerActivity/index.vue'),
        name: 'YgbWorkerActivity',
        meta: { title: '劳动者活动管理', activeMenu: '/operation/workerActivity' }
      }
    ]
  },
  {
    path: '/system/user-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:user:edit'],
    children: [
      {
        path: 'role/:userId(\\d+)',
        component: () => import('@/views/system/user/authRole.vue'),
        name: 'AuthRole',
        meta: { title: '分配角色', activeMenu: '/system/user' }
      }
    ]
  },
  {
    path: '/system/role-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:role:edit'],
    children: [
      {
        path: 'user/:roleId(\\d+)',
        component: () => import('@/views/system/role/authUser.vue'),
        name: 'AuthUser',
        meta: { title: '分配用户', activeMenu: '/system/role' }
      }
    ]
  },
  {
    path: '/system/dict-data',
    component: Layout,
    hidden: true,
    permissions: ['system:dict:list'],
    children: [
      {
        path: 'index/:dictId(\\d+)',
        component: () => import('@/views/system/dict/data.vue'),
        name: 'Data',
        meta: { title: '字典数据', activeMenu: '/system/dict' }
      }
    ]
  },
  {
    path: '/monitor/job-log',
    component: Layout,
    hidden: true,
    permissions: ['monitor:job:list'],
    children: [
      {
        path: 'index/:jobId(\\d+)',
        component: () => import('@/views/monitor/job/log.vue'),
        name: 'JobLog',
        meta: { title: '调度日志', activeMenu: '/monitor/job' }
      }
    ]
  },
  {
    path: '/tool/gen-edit',
    component: Layout,
    hidden: true,
    permissions: ['tool:gen:edit'],
    children: [
      {
        path: 'index/:tableId(\\d+)',
        component: () => import('@/views/tool/gen/editTable.vue'),
        name: 'GenEdit',
        meta: { title: '修改生成配置', activeMenu: '/tool/gen' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  }
})

export default router
