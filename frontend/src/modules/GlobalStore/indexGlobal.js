import state from './stateGlobal'
import * as actions from './actionsGlobal'
import mutations from './mutationsGlobal'
import * as getters from './gettersGlobal'

export default {
  namespaced: true,
  mutations,
  actions,
  state,
  getters,
  mounted () {
    // actions.loadCSRF()
  }
}
