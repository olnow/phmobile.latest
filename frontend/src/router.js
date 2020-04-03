import Vue from 'vue'
import Router from 'vue-router'
import phones from './views/PhonesView.vue'
// import cash from './views/CashView.vue'
import cashPhones from './views/CashPhonesView.vue'
import cashYearDepartment from './views/CashYearDepartmentView.vue'
import cashMonth from './views/CashMonthView.vue'
import ImportView from './views/ImportView'
import DetailMonthView from './views/DetailMonthView'
import DetailYearView from './views/DetailYearView'
import HistoryView from './views/HistoryView'
import Login from './views/Login'
import { store } from './store'

Vue.use(Router)

let router = new Router({
  mode: 'history',
  store,
  routes: [
    {
      path: '/phones',
      name: 'Телефоны',
      // route level code-splitting
      // this generates a separate chunk (about.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: phones,
      meta: {
        requiresAuth: true,
        breadcrumb: [
          { text: 'home', path: '/phones' },
          { text: 'Телефоны', path: '/phones' }
        ]
      }
      //
      // () => import(/* webpackChunkName: "phones" */ './views/Phones.vue')
    },
    {
      path: '/cashPhones',
      name: 'По телефонам',
      component: cashPhones,
      meta: {
        requiresAuth: true,
        breadcrumb: [
          { text: 'home', path: '/phones' },
          { text: 'Начисления' },
          { text: 'По телефонам' }
        ]
      }
    },
    {
      path: '/cashYearDepartment',
      name: 'По подразделениям (за год)',
      component: cashYearDepartment,
      meta: {
        requiresAuth: true,
        breadcrumb: [
          { text: 'home', path: '/phones' },
          { text: 'Начисления' },
          { text: 'По подразделениям' }
        ]
      }
    },
    {
      path: '/cashMonth',
      name: 'За месяц',
      component: cashMonth,
      meta: {
        requiresAuth: true,
        breadcrumb: [
          { text: 'home', path: '/phones' },
          { text: 'Начисления' },
          { text: 'За месяц' }
        ]
      }
    },
    {
      path: '/import',
      name: 'Импорт',
      component: ImportView,
      meta: {
        requiresAuth: true,
        breadcrumb: [
          { text: 'home', path: '/phones' },
          { text: 'Импорт' }
        ]
      }
    },
    {
      path: '/detailMonth',
      name: 'Анализ тарифов за месяц',
      component: DetailMonthView,
      meta: {
        requiresAuth: true,
        breadcrumb: [
          { text: 'home', path: '/phones' },
          { text: 'Детализации' },
          { text: 'Анализ тарифов за месяц' }
        ]
      }
    },
    {
      path: '/detailYear',
      name: 'Анализ тарифов за год',
      component: DetailYearView,
      meta: {
        requiresAuth: true,
        breadcrumb: [
          { text: 'home', path: '/phones' },
          { text: 'Детализации' },
          { text: 'Анализ тарифов за год' }
        ]
      }
    },
    {
      path: '/history',
      name: 'История',
      component: HistoryView,
      meta: {
        requiresAuth: true,
        breadcrumb: [
          { text: 'home', path: '/phones' },
          { text: 'История' },
          { text: 'История' }
        ]
      }
    },
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    { path: '*',
      component: Login
    }
  ]
})

router.beforeEach((to, from, next) => {
  // console.log('0 befor: ', localStorage.getItem('auth'))
  // console.log('befor route: ', from.path, to.path)
  if (to.path === '/login') {
    // console.log('befor route to: ', from, to)
    store.dispatch('GlobalStore/setCurrentPath', from.path)
  }
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // этот путь требует авторизации, проверяем залогинен ли
    // пользователь, и если нет, перенаправляем на страницу логина
    // console.log('befor: ', localStorage.getItem('auth'))
    if (localStorage.getItem('auth') == null) {
      next({
        path: '/login',
        params: { nextUrl: to.fullPath }
      })
    } else {
      switch (to.path) {
        case '/cash':
          // console.log('/cash', store)
          // console.log('/cash', store.getters['CashStore/getPhonesCash'])
          if (!store.getters['CashStore/getPhonesCash'].length) {
            // console.log('Load Cash')
            // store.dispatch('CashStore/loadPhonesCash')
          }
          break
        case '/phones':
          break
      }
      next()
    }
  } else {
    next() // всегда так или иначе нужно вызвать next()!
  }
})

export default router
