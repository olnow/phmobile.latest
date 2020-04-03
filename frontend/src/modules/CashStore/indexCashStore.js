import state from './stateCashStore'
import * as actions from './actionsCashStore'
import mutations from './mutationsCashStore'
import * as getters from './gettersCashStore'

export default {
  namespaced: true,
  mutations,
  actions,
  state,
  getters
}
