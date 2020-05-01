import { CASH_MUTATIONS } from '../mutation-types'

export default {
  [CASH_MUTATIONS.MUTATION_LOADPHONESCASH] (state, { data }) {
    state.phoneList = data
  },
  [CASH_MUTATIONS.MUTATION_LOADCASHMONTH] (state, { data }) {
    state.cashMonth = data
  },
  [CASH_MUTATIONS.MUTATION_LOADCASHYEAR] (state, { data }) {
    state.cashYear = data
  },
  [CASH_MUTATIONS.MUTATION_LOADCASH_YEAR_DEPART] (state, { data }) {
    state.cashYearDepart = data
  },
  [CASH_MUTATIONS.SET_SELECTED_MONTH] (state, data) {
    state.selectedMonth = data
    // console.log('mutation: ', data, state.selectedMonth)
  },
  [CASH_MUTATIONS.SET_SELECTED_YEAR] (state, data) {
    state.selectedYear = data
    // console.log('mutation: ', data, state.selectedYear)
  }
}
