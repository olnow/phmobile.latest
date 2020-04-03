import { DETAIL_MUTATIONS } from '../mutation-types'
import { postRestApi } from '../../http-common'
import { store } from '../../store'

export async function setDetailAnalizeYear ({ commit }, data) {
  // console.log('action: ', data)
  commit(DETAIL_MUTATIONS.SET_DETAIL_ANALIZE_YEAR, { data })
}

export async function getDetailAnalizeYear ({ commit }, formData) {
  try {
    // let data = await postRestApi('getDetailAnalizeYear', formData)
    // let dt = new Future()
    // console.log('starting load')
    postRestApi('getDetailAnalizeYear', formData)
      .then(function (response) {
        // console.log('load end', response, response.data)
        let dt = response.data
        // console.log('res: ', dt)
        store.dispatch('DetailStore/setDetailAnalizeYear', dt)
      })
      .finally(function (response) {
        store.dispatch('GlobalStore/setRestLoadData', false)
      })
    // console.log('load proceed')
    // console.log('befor global', commit)
    store.dispatch('GlobalStore/setRestLoadData', true)
    // console.log('after global', commit)
    // await data
    // let dt = await data
    // console.log('load end', data, dt)
    // store.dispatch('GlobalStore/setRestLoadData', false)
    // let progress = await postRestApi('getDetailAnalizeProgress', 0)
    //  .then(function (response) {
    //    console.log('action: ', response)
    //    commit(DETAIL_MUTATIONS.SET_DETAIL_ANALIZE_YEAR, { data })
    // data = response
    // return response
    //  })
    // console.log(this.$store)
    // store.commit('GlobalStore/' + GLOBAL_MUTATIONS.REST_LOAD_DATA, true)
    // console.log('dt: ', data)
    // dt.onload(function (response) {
    //  console.log(response)
    // })
    // let nn = dt.then(function (response) {
    //  console.log('resp: ', response, dt)
    //  return response
    // })
    // data = data['data']
    // console.log('Future:', nn, nn.data)
    // commit(DETAIL_MUTATIONS.SET_DETAIL_ANALIZE_YEAR, { data })
  } catch (e) {
    console.log('[DetailStore] action error: getDetailAnalizeYear: ', e)
    // return null
  }
}
