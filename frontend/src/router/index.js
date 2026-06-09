import { createRouter, createWebHistory } from 'vue-router'
import RelicList from '../views/RelicList.vue'
import RelicForm from '../views/RelicForm.vue'
import RelicDetail from '../views/RelicDetail.vue'
import RelicDashboard from '../views/RelicDashboard.vue'
import ExcavationUnitList from '../views/ExcavationUnitList.vue'
import ExcavationUnitForm from '../views/ExcavationUnitForm.vue'

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
    path: '/excavation-units',
    name: 'ExcavationUnitList',
    component: ExcavationUnitList
  },
  {
    path: '/excavation-units/new',
    name: 'ExcavationUnitCreate',
    component: ExcavationUnitForm
  },
  {
    path: '/excavation-units/:id/edit',
    name: 'ExcavationUnitEdit',
    component: ExcavationUnitForm,
    props: true
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
