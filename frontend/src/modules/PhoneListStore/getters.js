export function getPhoneList (state) {
  return state.phoneList
}

export function getActiveHistory ({ state }, phone) {
  try {
    // console.log('getters: getActiveHistory: ', state)
    if (state.activeHistories === undefined || state.activeHistories === null) {
      return null
    }
    console.log('Value of: ', state.activeHistories.valueOf(phone['phone']))
    if (state.activeHistories[phone['phone']].length) {
      return state.activeHistories[phone['phone']]
    }
  } catch (e) {
    // console.log('getters error: getActiveHistory: ', e)
    return null
  }
  // console.log('getters: getActiveHistory: ', phone)
  // if (state.activeHistories) {
  //   console.log('getters: getActiveHistory: return:', state.activeHistories)
  //   return state.activeHistories
  // }
}

export function getHistory (state) {
  try {
    if (state.activeHistories === undefined || state.activeHistories === null) {
      return null
    }
    if (state.activeHistories.length) {
      return state.activeHistories
    }
  } catch (e) {
    console.log('getters error: getHistory: ', e)
    return null
  }
}

export function getSelectPeople (state) {
  return state.selectedPeople
}
