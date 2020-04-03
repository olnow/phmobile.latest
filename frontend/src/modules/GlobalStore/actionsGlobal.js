import { GLOBAL_MUTATIONS } from '../mutation-types'
import { AXIOS, getRestApi, postRestApi } from '../../http-common'
import Router from '../../router'
// import Vue from 'vue'
// import Cookies from 'js-cookie'
// import Vue from 'vue'
// import VueCookies from 'vue-cookies'
// Vue.use(VueCookies)

// set default config
// VueCookies.config('7d')

export async function loadCSRF ({ commit }) {
  // $cookies.config('7d')
  getRestApi('getOptions')
  // let data = await callRestGet('getOptions')
  // let csrfToken = data.headers['x-csrf-token']
  // let cookToken = data.headers['Set-Cookies']
  // let cook = $cookies.get('XSRF-TOKEN')
  // set(MUTATION_PHONELIST, data)
  // console.log('action loadCSRF cook: ', cook, cookToken)
  // commit(GLOBAL_MUTATIONS.SET_CSRF, csrfToken)
  // console.log('csrf: ', csrfToken)
}

export function toLoginPage (store) {
  // console.log(store)
  if (store.getters['getAuthEnable'] && !store.getters['getAuth']) {
    Router.push('/login')
  }
}

export async function logout (store) {
  // console.log('logout: ', store.getters['getAuthEnable'], store.getters['getAuth'])
  // store.commit(GLOBAL_MUTATIONS.LOGOUT)
  if (store.getters['getAuthEnable'] && store.getters['getAuth']) {
    // store.commit(GLOBAL_MUTATIONS.LOGOUT)
    localStorage.removeItem('auth')
    store.commit(GLOBAL_MUTATIONS.LOGOUT)
    try {
      await postRestApi('logout', null)
    } catch (e) {
    }
    Router.push('/login')
  }
}

// login function
export async function AUTH_REQUEST ({ commit, state }, formdata) {
  // console.log(formdata)
  let data = await postRestApi('login', formdata)
  if (data && data['status'] === 200) {
    commit(GLOBAL_MUTATIONS.AUTH_SUCCESS)
    localStorage.auth = true
    // let token = $('meta[name="_csrf"]').attr('content')
    // console.log('cookie', Cookies.get(), Vue.cookies.get('XSRF-TOKEN'))
    let path = state.currentPath
    if (path === '' || path === '/') path = '/phones'
    Router.push(path)
  }
  // console.log('AUTH: ', data)
}

export async function callRestGet (request) {
  try {
    // AXIOS.defaults.withCredentials = true
    AXIOS.defaults.xsrfHeaderName = 'X-CSRF-TOKEN'
    // AXIOS.defaults.xsrfCookieName = 'X-CSRF-TOKEN'
    AXIOS.defaults.withCredentials = true
    let data = await AXIOS.get(request)
    // data.then((response) => {
    //   console.log(response.xsrfCookieName)
    // })
    // console.log('callRestGet', data)
    // let csrfToken = data.headers['x-csrf-token'];
    // console.log('csrf: ', csrfToken)
    return data
  } catch (e) {
    console.log(e)
    return null
  }
}

export async function setRestLoadData ({ commit }, data) {
  // console.log('restLoadData: ', data)
  commit(GLOBAL_MUTATIONS.REST_LOAD_DATA, data)
}

export function setSearchFilter ({ commit }, data) {
  // console.log('setFilter: ', data)
  commit(GLOBAL_MUTATIONS.SET_SEARCH_FILTER, data)
  localStorage.searchFilter = data
}

export async function setProgress ({ commit }, data) {
  // console.log('setProgress', data)
  commit(GLOBAL_MUTATIONS.SET_PROGRESS, data)
}

export async function updateProgress ({ commit, state }, apiName) {
  // console.log('updateProgress: oldvalue', state.progress)
  let progress = { 'progress': state.progress }
  let data = await postRestApi(apiName, { progress })
  // await data
  // console.log('new progress', data.data.progress)
  if (data && data['status'] === 200) {
    commit(GLOBAL_MUTATIONS.SET_PROGRESS, data.data.progress)
  }
}

export function setCurrentPath ({ commit, state }, path) {
  // console.log('setCurrentPath:', path)
  commit(GLOBAL_MUTATIONS.SET_CURRENT_PATH, path)
}