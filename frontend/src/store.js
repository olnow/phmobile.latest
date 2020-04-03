import Vue from 'vue'
import Vuex from 'vuex'
import PhoneListStore from './modules/PhoneListStore/index'
import CashStore from './modules/CashStore/indexCashStore'
import ImportStore from './modules/ImportStore/indexImportStore'
import GlobalStore from './modules/GlobalStore/indexGlobal'
import DetailStore from './modules/DetailStore/indexDetailStore'
import HistoryStore from './modules/HistoryStore/indexHistoryStore'

Vue.use(Vuex)

// export default new Vuex.Store({
export const store = new Vuex.Store({
  state: {

  },
  mutations: {

  },
  actions: {

  },
  modules: {
    PhoneListStore,
    CashStore,
    ImportStore,
    GlobalStore,
    DetailStore,
    HistoryStore
  }
})
