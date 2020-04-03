import * as actions from './actionsDetailStore'
import mutations from './mutationsDetailStore'
import * as getters from './gettersDetailStore'
import state from './stateDetailStore'

export default {
  namespaced: true,
  mutations,
  actions,
  state,
  getters
}
