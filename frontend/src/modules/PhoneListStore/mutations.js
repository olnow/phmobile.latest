import { PHONES_MUTATIONS } from '../mutation-types'
import { postRestApi } from '../../http-common'
import Vue from 'vue'
// import { PHONES_STATE } from '../../utils'

export default {
  [PHONES_MUTATIONS.MUTATION_LOADPHONELIST] (state, { data }) {
    state.phoneList = data
    // console.log(state.phoneList)
  },

  [PHONES_MUTATIONS.MUTATION_UPDATEACTIVEHISTORY] (state, data) {
    console.log('Mutation: UpdateActiveHistory', data)
    state.activeHistories.push(data)
    console.log('Mutation: UpdateActiveHistory ', state.activeHistories)
  },

  async [PHONES_MUTATIONS.MUTATION_UPDATEPEOPLE] (state, people) {
    if (!people || people === undefined) {
      return
    }
    try {
      await postRestApi('updatePeople', { people })
      // let data = await AXIOS.post('updatePeople', {people})
      // let dataValue = data['data']

      // console.log('data: ', people)
      // let data = state.phoneList
      // state.phoneList.forEach((value, index) => {
      // console.log('people foreach: ', value, index)
      //  if (value['people'] && value['people'] !== undefined) {
      //    if (value['people']['people'] === people['people']) {
      //      console.log('updates: ', state.phoneList[index]['people'])
      //      state.phoneList[index]['people'] = people
      //      console.log('updates: ', state.phoneList[index]['people'])
      //    }
      //  }
      // })
    } catch (e) {
      console.log('mutation error: ', PHONES_MUTATIONS.MUTATION_UPDATEPEOPLE, ' - ', e)
    }
  },

  async [PHONES_MUTATIONS.MUTATION_UPDATEPEOPLE_HISTORY] (state, formData) {
    if (!formData) {
      return
    }
    try {
      let res = await postRestApi('updatePeopleAndHistory', formData)
      return res
    } catch (e) {
      console.log('mutation error: ', PHONES_MUTATIONS.MUTATION_UPDATEPEOPLE_HISTORY, ' - ', e)
      return e
    }
  },

  async [PHONES_MUTATIONS.UPDATE_FREEPHONE] (state, data) {
    // let history = state.activeHistories.find(f => f.key === phone['phone'])['value']
    // let history = phone
    // console.log('Mutation ', PHONES_MUTATIONS.UPDATE_FREEPHONE, ': ', this, state)
    try {
      // let response = await postRestApi('freePhone', { history })
      // if (response && response.data) {
      if (data) {
        // console.log(response, response.data)
        state.phoneList.forEach(element => {
          // if (element['phone'] === history['phone']['phone'] &&
          //  response.data.people === null) {
          if (element['phone'] === data['phone'] &&
            data.people === null) {
            // console.log('vue delete: ', data)
            Vue.delete(element, 'people')
          }
        })
      }
    } catch (e) {
      console.log('mutation error:', PHONES_MUTATIONS.MUTATION_UPDATEPEOPLE, ': ', e)
    }
  },

  [PHONES_MUTATIONS.ADD_EMPTY_PHONE] (state) {
    state.phoneList.unshift({
      'active': true,
      'contract': '',
      'phone': '',
      'state': 0 })
    // console.log(state.phoneList)
  },

  [PHONES_MUTATIONS.SELECT_PEOPLE] (state, people) {
    state.selectedPeople = people
    // console.log('mut-sp: ', state.selectedPeople)
  }
}
