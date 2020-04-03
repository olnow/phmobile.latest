import Vuex from 'vuex'

export const PHONES_STATE = {
  ACTIVE: 1,
  INACTIVE: 0,
  EXCLUDED: 2,
  REISSUED: 3
}

export function getLastMonth () {
  let month = new Date().getMonth() + 1
  if (month > 1) {
    return month - 1
  } else {
    return 12
  }
}

export function getLastYear () {
  if (getLastMonth() === 12) {
    // console.log(new Date().getFullYear() - 1)
    return new Date().getFullYear() - 1
  } else {
    // console.log(new Date().getFullYear())
    return new Date().getFullYear()
  }
}

export function printdate (dt) {
  let ms = ['Январь', 'Февраль', 'Март',
    'Апрель', 'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь']
  // console.log('import date: ', dt)
  let date = new Date(dt)
  // console.log('date: ', date, date.getFullYear())
  return date.getFullYear() + ': ' + ms[date.getMonth()]
}

export async function sleep (ms) {
  await new Promise(resolve => setTimeout(resolve, ms))
  // return
}

export var searchFilter = {
  data () {
    return {
      filter: ''
    }
  },
  computed: {
    ...Vuex.mapGetters({
      searchFilter: 'GlobalStore/getSearchFilter'
    }),
    localSearchFilter () {
      if (this.searchFilter !== this.filter) {
        this.filter = this.searchFilter
      }
      if (this.searchFilter) return this.searchFilter
      return ''
    }
  },
  methods: {
    filterChange (value) {
      this.$store.dispatch('GlobalStore/setSearchFilter', value)
    }
  }
}

export var formatRow = {
  methods: {
    getRowClass: function (state, people, checkhistory, datestart) {
      let textNormal = 'textNormal'
      // if (!state && !people) return textNormal
      // console.log(people)
      if (people && !people.people) return 'table-danger'
      if (state === PHONES_STATE.REISSUED || state === PHONES_STATE.EXCLUDED) {
        if (people && people.people) return 'table-warning'
        return 'textLightGray'
      }
      if (state === PHONES_STATE.INACTIVE) {
        if (people && people.people) return 'table-warning'
        return 'textGray'
      }
      if (people && people['serviceaccount'] === 1) return textNormal
      if (people && people['adstate'] === 0) return 'table-danger'
      if (!people || !people.people) return 'table-secondary'
      if (checkhistory && !datestart) return 'table-danger'
      return textNormal
    },
    getStateName: function (state) {
      switch (state) {
        case PHONES_STATE.ACTIVE:
          return 'Активный'
        case PHONES_STATE.INACTIVE:
          return 'Заблокированный'
        case PHONES_STATE.EXCLUDED:
          return 'Исключен'
        case PHONES_STATE.REISSUED:
          return 'Переоформлен'
      }
    }
  }
}
