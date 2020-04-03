export function getCSRF (state) {
  // console.log('getCSRF', state, state.csrf)
  return state.csrf
}

export function getAuth (state) {
  return state.auth
  // if (localStorage.auth) {
  //   return localStorage.auth
  // }
  // return false
}

export function getCsrfEnable (state) {
  return state.csrfEnable
}

export function getAuthEnable (state) {
  return state.authEnable
}

export function getRestLoadData (state) {
  return state.restLoadData
}

export function getProgress (state) {
  return state.progress
}

export function getSearchFilter (state) {
  // console.log('get:', state.searchFilter)
  return state.searchFilter
}

export function getCurrentPath (state) {
  return state.currentPath
}
