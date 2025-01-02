import Vue from 'vue'
import VueRouter from 'vue-router'
import IndexPage from '@/views/IndexPage'

Vue.use(VueRouter)

const routes = [
  {
    path: '/', component: IndexPage, children: [
      {
        path: '', component: () => import('@/views/MainPage'), name: 'INDEX', children: []
      },
    ]
  },
  // 推荐管理
  {
    path: '/', component: IndexPage, name: 'Recommendation Management', children: [
      {
        path: '/recommendList', component: () => import('@/views/recommend/RecommendList'), name: 'Recommendation List', children: []
      }
    ]
  },
  // 推荐详情页
  {
    path: '/recommendDetail', component: () => import('@/views/recommend/RecommendDetail'), name: '用户输入', children: []
  },
  // 图谱展示
  {
    path: '/knowledgeGraph', component: () => import('@/views/knowledgeGraph/BomKnowledgeGraph'), name: '图谱展示', children: []
  },
  // 知识图谱管理
  {
    path: '/', component: IndexPage, name: 'Knowledge Graph', children: [
      {
        path: '/importToKG', component: () => import('@/views/knowledgeGraph/ImportToKG'), name: 'Graph Import', children: []
      }, {
        path: '/equipment', component: () => import('@/views/knowledgeGraph/EquipmentList'), name: 'Equipment Management', children: []
      }, {
        path: '/attributes', component: () => import('@/views/knowledgeGraph/AttributesList'), name: 'Attribute Management', children: []
      }, {
        path: '/process', component: () => import('@/views/knowledgeGraph/ProcessList'), name: 'Process Management', children: []
      }
    ]
  },

]

const createRouter = () => new VueRouter({
  scrollBehavior: () => ({ y: 0 }),
  routes
})
const router = createRouter()

// 解决重复点击路由导致报错的问题
const VueRouterPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(to) {
  return VueRouterPush.call(this, to).catch(err => err)
}

router.beforeEach((to, from, next) => {
  // document.title = to.meta.title || 'BOM知识图谱';
  document.title = 'BOM知识图谱';
  next();
});

export default router
