import { CASH_MUTATIONS } from '../mutation-types'
import { postRestApi } from '../../http-common'

export async function loadPhonesCash ({ commit }, year) {
  let formDate
  if (year) {
    // console.log('send year: ', year)
    formDate = new FormData()
    formDate.append('year', year)
  }
  let data = await postRestApi('getCashYear', formDate)
  data = data['data']
  // set(MUTATION_PHONELIST, data)
  // console.log('loadPhonesCash:', data)
  commit(CASH_MUTATIONS.MUTATION_LOADPHONESCASH, { data })
}

export async function loadCashYear ({ commit }, year) {
  let formData
  if (year) {
    // console.log('send year: ', year)
    formData = new FormData()
    formData.append('year', year)
  }
  let data = await postRestApi('getCashYear', formData)
  data = data['data']
  // set(MUTATION_PHONELIST, data)
  // console.log('loadPhonesCash:', data)
  commit(CASH_MUTATIONS.MUTATION_LOADPHONESCASH, { data })
}

export async function loadCashYearDepart ({ commit }, formData) {
  let data = await postRestApi('getCashYearDepartment', formData)
  data = data['data']
  // set(MUTATION_PHONELIST, data)
  // console.log('loadPhonesCash:', data)
  commit(CASH_MUTATIONS.MUTATION_LOADCASH_YEAR_DEPART, { data })
}

export async function loadCashMonth ({ commit }, formData) {
  let data = await postRestApi('getCashMonth', formData)
  // console.log('loadCashMonth:', data)
  data = data['data']
  // set(MUTATION_PHONELIST, data)
  // console.log('loadCashMonth:', data)
  commit(CASH_MUTATIONS.MUTATION_LOADCASHMONTH, { data })
}

export async function loadCashDetail ({ commit, state }, sendData) {
  // console.log('action: loadActiveHistory: getActive: ', state.getters.getActiveHistory(state, phone))
  // if (state.activeHistories && state.activeHistories[phone['phone']]) {
  //  console.log('getters: getActiveHistory: return:', state.activeHistories[phone['phone']])
  //   return state.activeHistories[phone['phone']]
  // }
  try {
    let data = await postRestApi('getCashDetail', sendData)
    let dataValue = data['data']
    // let json = { key: phone['phone'], value: dataValue }
    // console.log('action: loadActiveHistory: data: ', data)
    // commit(PHONES_MUTATIONS.MUTATION_UPDATEACTIVEHISTORY, dataValue)
    // return json
    return dataValue
  } catch (e) {
    console.log('action error: loadCashDetail: ', e)
    // return null
  }
}

export function setSelectedMonth ({ commit }, data) {
  commit(CASH_MUTATIONS.SET_SELECTED_MONTH, data)
  localStorage.cashSelectedMonth = data
}

export function setSelectedYear ({ commit }, data) {
  commit(CASH_MUTATIONS.SET_SELECTED_YEAR, data)
  localStorage.cashSelectedYear = data
}

/*
export async function callRestGet (request) {
  try {
    let data = await AXIOS.post(request)
    // console.log(data)
    return data['data']
  } catch (e) {
    console.log(e)
    return null
  }
} */
