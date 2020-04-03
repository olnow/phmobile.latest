import Vue from 'vue'
import Vuex from 'vuex'
import App from './App.vue'
import router from './router'
import { store } from './store'
// import VueCookies from 'vue-cookies'

// Vue.config.productionTip = false
Vue.use(Vuex)
// Vue.use(VueCookies)

new Vue({
  router,
  store,
  computed: {
    ...Vuex.mapGetters({
      CSRF: 'GlobalStore/getCSRF'
    })
  },
  async mounted () {
    // console.log('Mounted main CSRF:', this.CSRF, this.CSRF.length)
    if (store && !this.CSRF.length) {
      await store.dispatch('GlobalStore/loadCSRF')
      // console.log('store: ', store, store.getters['GlobalStore/getCSRF'])
    }
  },
  render: h => h(App)
}).$mount('#app')
