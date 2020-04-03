import { IMPORT_MUTATIONS } from '../mutation-types'
import { postRestApi } from '../../http-common'

export function set ({ commit }, { data }) {
  // commit(IMPORT, { data });
}

export async function loadImportStatus ({ commit, rootState, rootGetters }) {
  try {
    // this.rootStore.dispatch('GlobalStore/getCSRF')
    // await rootState.GlobalStore.csrf

    // let csrf = rootGetters['GlobalStore/getCSRF']
    // if (!csrf.length) {
    //   return
    // }
    // console.log('import store: load')
    // AXIOS.defaults.xsrfHeaderName = 'X-CSRF-TOKEN'
    // AXIOS.defaults.withCredentials = true
    // AXIOS.defaults.headers.post['X-CSRF-TOKEN'] = csrf
    // AXIOS.defaults.headers.post['_csrf'] = csrf
    // let data = await AXIOS.post('getContractCashDetail')
    let data = await postRestApi('getContractCashDetail', null)
    data = data['data']
    // console.log('loadImport: ', data)
    // let json = { key: phone['phone'], value: dataValue }
    // console.log('action: loadActiveHistory: data: ', data)
    commit(IMPORT_MUTATIONS.UPDATE_CONTRACT_CASH_DETAIL, { data })
    // return json
    // return dataValue
  } catch (e) {
    console.log('action error: loadImportStatus: ', e)
    // return null
  }
}

export async function loadImportHistory ({ commit }, formData) {
  try {
    let data = await postRestApi('getImportHistory', formData)
    data = data['data']
    commit(IMPORT_MUTATIONS.SET_IMPORT_HISTORY, { data })
  } catch (e) {
    console.log('[ImportStore] action error: loadImportHistory: ', e)
    // return null
  }
}

export function updateLastUploadDate ({ commit }) {
  commit(IMPORT_MUTATIONS.SET_LAST_UPLOAD_DATE, new Date())
}

export async function checkFileName ({ commit }, formData) {
  try {
    let data = await postRestApi('checkFileImportStatus', formData)
    data = data['data']
    return data
    // commit(IMPORT_MUTATIONS.SET_IMPORT_HISTORY, { data })
  } catch (e) {
    console.log('[ImportStore] action error: checkFileName: ', e)
    return null
  }
}
