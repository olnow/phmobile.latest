import { IMPORT_MUTATIONS } from '../mutation-types'

export default {
  [IMPORT_MUTATIONS.UPDATE_CONTRACT_CASH_DETAIL] (state, { data }) {
    // console.log('mutation import: ', state, data)
    state.importList = data
    // console.log('mutation import: ', state, state.importList)
  },
  [IMPORT_MUTATIONS.SET_IMPORT_HISTORY] (state, { data }) {
    // console.log('mutation import: ', state, data)
    state.importHistory = data
    // console.log('mutation import: ', state, state.importList)
  },
  [IMPORT_MUTATIONS.SET_LAST_UPLOAD_DATE] (state, { data }) {
    // console.log('mutation import: ', state, data)
    state.lastUploadDate = data
    // console.log('mutation import: ', state, state.importList)
  }
}
