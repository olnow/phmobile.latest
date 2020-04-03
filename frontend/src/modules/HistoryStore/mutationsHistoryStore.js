import { HISTORY_MUTATIONS } from '../mutation-types'

export default {
  [HISTORY_MUTATIONS.SET_HISTORY_CASH_ANALIZE] (state, { data }) {
    // console.log('mutation import: ', state, data)
    state.historyCashAnalize = data
    // console.log('mutation import: ', state, state.importList)
  }
}
