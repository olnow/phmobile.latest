import { PHONES_MUTATIONS } from '../mutation-types'
import { getRestApi, postRestApi } from '../../http-common'

export function set ({ commit }, { data }) {
  commit(PHONES_MUTATIONS.MUTATION_LOADPHONELIST, { data })
}

export async function loadPhoneList ({ commit }) {
  let data = await postRestApi('getPhones', '')
  if (data) {
    data = data['data']
    // set(MUTATION_PHONELIST, data)
    // console.log('loadPhoneList:', data)
    commit(PHONES_MUTATIONS.MUTATION_LOADPHONELIST, { data })
  }
}

export async function loadActiveHistory ({ commit, state }, phone) {
  // console.log('action: loadActiveHistory: getActive: ', state.getters.getActiveHistory(state, phone))
  // if (state.activeHistories && state.activeHistories[phone['phone']]) {
  //  console.log('getters: getActiveHistory: return:', state.activeHistories[phone['phone']])
  //   return state.activeHistories[phone['phone']]
  // }
  try {
    // let data = await AXIOS.post('getActiveHistory', { phone })
    let data = await postRestApi('getActiveHistory', { phone })
    let dataValue = data['data']
    // let json = { key: phone['phone'], value: dataValue }
    // console.log('action: loadActiveHistory: data: ', data)
    // commit(PHONES_MUTATIONS.MUTATION_UPDATEACTIVEHISTORY, dataValue)
    // return json
    return dataValue
  } catch (e) {
    console.log('action error: loadActiveHistory: ', e)
    // return null
  }
}

export async function updatePeople ({ commit }, data) {
  try {
    // let data = await AXIOS.post('updatePeople', { people })
    // let dataValue = data['data']
    // let json = { key: phone['phone'], value: dataValue }
    // console.log('action: updatePeople: data: ', people)
    commit(PHONES_MUTATIONS.MUTATION_UPDATEPEOPLE, data)
    // return json
  } catch (e) {
    console.log('action error: updatePeople: ', e)
    // return null
  }
}

export async function updatePeopleAndHistory ({ commit }, formData) {
  try {
    // let data = await AXIOS.post('updatePeople', { people })
    // let dataValue = data['data']
    // let json = { key: phone['phone'], value: dataValue }
    // console.log('action: updatePeople: data: ', people)
    return commit(PHONES_MUTATIONS.MUTATION_UPDATEPEOPLE_HISTORY, formData)
    // return json
  } catch (e) {
    console.log('action error: updatePeopleAndHistory: ', e)
    // return null
  }
}

export async function createAndSetPeople ({ commit }, phone) {
  try {
    // console.log('createAndSetPeople: phone: ', phone)
    return postRestApi('createAndSetPeople', phone)
    // let dataValue = data['data']
  } catch (e) {
    // console.log('action createAndSetPeople: ', e)
  }
  return false
}

export async function createOrUpdatePhone ({ commit }, phone) {
  try {
    // console.log('createAndSetPeople: phone: ', phone)
    return postRestApi('createOrUpdatePhone', { phone })
    // let dataValue = data['data']
  } catch (e) {
    console.log('action createOrUpdatePhone error: ', e)
  }
  return false
}

export async function freePhone ({ commit }, history) {
  try {
    // console.log('Action freePhone: ', phone)
    let res = postRestApi('freePhone', { history })
      .then(res => {
        if (res && res.data) {
          commit(PHONES_MUTATIONS.UPDATE_FREEPHONE, res.data)
        }
        return res
      })
    // console.log('free res:', res)
    return res
    // commit(PHONES_MUTATIONS.UPDATE_FREEPHONE, history)
  } catch (e) {
    console.log('Action freePhone: ', e)
    return e
  }
}

export async function addEmptyPhone ({ commit }) {
  try {
    // console.log('Action freePhone: ', phone)
    commit(PHONES_MUTATIONS.ADD_EMPTY_PHONE)
  } catch (e) {
    console.log('Action addEmptyPhone: ', e)
  }
}

export async function loadPeopleList ({ commit }) {
  try {
    let data = await getRestApi('getPeopleList')
    // console.log('getPeopleList: ', data['data'])
    return data['data']
  } catch (e) {
    console.log('getPeopleList error: ', e)
    return null
  }
}

export async function syncADState () {
  try {
    await getRestApi('syncADState')
    loadPhoneList()
  } catch (e) {
    console.log('syncADState: ', e)
  }
}

export async function findPeopleLocal ({ commit }, formData) {
  try {
    return await postRestApi('people/findLocal', formData)
  } catch (e) {
    console.log('[findPeopleLocal] ', e)
  }
}

export async function findPeopleAD ({ commit }, formData) {
  try {
    return await postRestApi('people/findAD', formData)
  } catch (e) {
    console.log('[findPeopleAD] ', e)
  }
}

export function selectPeople ({ commit }, people) {
  commit(PHONES_MUTATIONS.SELECT_PEOPLE, people)
}

/*
export async function callRestGet (request) {
  try {
    let data = await AXIOS.get(request)
    return data['data']
  } catch (e) {
    console.log(e)
    return null
  }
} */
