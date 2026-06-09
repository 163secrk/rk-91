import { createRouter, createWebHistory } from 'vue-router'
import RelicList from '../views/RelicList.vue'
import RelicForm from '../views/RelicForm.vue'
import RelicDetail from '../views/RelicDetail.vue'
import RelicDashboard from '../views/RelicDashboard.vue'

const routes = [
  {
    path: '/',
    redirect: '/relics'
  },
  {
    path: '/relics',
    name: 'RelicList',
    component: RelicList
  },
  {
    path: '/dashboard',
    name: 'RelicDashboard',
    component: RelicDashboard
  },
  {
    path: '/relics/new',
    name: 'RelicCreate',
    component: RelicForm
  },
  {
    path: '/relics/:id/edit',
    name: 'RelicEdit',
    component: RelicForm,
    props: true
  },
  {
    path: '/relics/:id',
    name: 'RelicDetail',
    component: RelicDetail,
    props: true
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
