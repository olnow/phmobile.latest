import * as actions from './actionsImportStore'
import mutations from './mutationsImportStore'
import * as getters from './gettersImportStore'
import state from './stateImportStore'

export default {
  namespaced: true,
  mutations,
  actions,
  state,
  getters
}
