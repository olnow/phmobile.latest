import * as actions from './actionsHistoryStore'
import mutations from './mutationsHistoryStore'
import * as getters from './gettersHistoryStore'
import state from './stateHistoryStore'

export default {
  namespaced: true,
  mutations,
  actions,
  state,
  getters
}
