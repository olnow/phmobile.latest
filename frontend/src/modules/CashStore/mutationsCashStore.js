import { CASH_MUTATIONS } from '../mutation-types'

export default {
  [CASH_MUTATIONS.MUTATION_LOADPHONESCASH] (state, { data }) {
    state.phoneList = data
    // console.log(state.phoneList)
  },
  [CASH_MUTATIONS.MUTATION_LOADCASHMONTH] (state, { data }) {
    state.cashMonth = data
    // console.log(state.phoneList)
  },
  [CASH_MUTATIONS.MUTATION_LOADCASHYEAR] (state, { data }) {
    state.cashYear = data
    // console.log(state.phoneList)
  },
  [CASH_MUTATIONS.MUTATION_LOADCASH_YEAR_DEPART] (state, { data }) {
    state.cashYearDepart = data
    // console.log(state.phoneList)
  }
}
