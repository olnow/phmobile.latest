import { HISTORY_MUTATIONS } from '../mutation-types'
import { postRestApi } from '../../http-common'

export async function loadHistoryCashAnalize ({ commit }) {
  try {
    let data = await postRestApi('getLastHistoryWithCashAnalize')
    data = data['data']
    commit(HISTORY_MUTATIONS.SET_HISTORY_CASH_ANALIZE, { data })
  } catch (e) {
    console.log('[HistoryStore] action error: getLastHistoryWithCashAnalize: ', e)
    // return null
  }
}
