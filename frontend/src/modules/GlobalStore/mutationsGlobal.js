import { GLOBAL_MUTATIONS } from '../mutation-types'
// import Vue from 'vue'
// import { AXIOS } from '../../http-common'

export default {
  [GLOBAL_MUTATIONS.SET_CSRF] (state, data) {
    state.csrf = data
    // console.log('muttation SET CSRF: ', state.csrf)
  },
  [GLOBAL_MUTATIONS.AUTH_SUCCESS] (state) {
    state.auth = true
    // localStorage.auth = true
  },
  [GLOBAL_MUTATIONS.LOGOUT] (state) {
    state.auth = false
    // localStorage.auth = false
  },
  [GLOBAL_MUTATIONS.CLEAR_CSRF] (state) {
    state.csrf = ''
    // console.log('muttation SET CSRF: ', state.csrf)
  },
  [GLOBAL_MUTATIONS.REST_LOAD_DATA] (state, data) {
    state.restLoadData = data
  },
  [GLOBAL_MUTATIONS.SET_PROGRESS] (state, data) {
    state.progress = data
  },
  [GLOBAL_MUTATIONS.SET_SEARCH_FILTER] (state, filter) {
    // console.log('mutation:', filter)
    state.searchFilter = filter
  },
  [GLOBAL_MUTATIONS.SET_CURRENT_PATH] (state, path) {
    state.currentPath = path
  }
}
