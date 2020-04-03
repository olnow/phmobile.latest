import { DETAIL_MUTATIONS } from '../mutation-types'

export default {
  [DETAIL_MUTATIONS.SET_DETAIL_ANALIZE_YEAR] (state, { data }) {
    // console.log('mutation import: ', state, data)
    state.detailAnalizeYear = data
    // console.log('mutation import: ', state, state.importList)
  }
}
