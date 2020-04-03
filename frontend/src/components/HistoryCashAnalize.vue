<template lang="html">
<div id="HistoryCashAnalizeComponent" class="pl-0">
  <div id="nav" class="p-0">
    <b-container class="p-0">
      <b-row>
        <b-col>
          <b-navbar toggleable="lg" type="light" variant="light">
            <b-navbar-nav>
              <b-form-tags class="mb-2" v-model="tagsSelected" size="sm" placeholder="Множественный поиск (И)">
              </b-form-tags>
            </b-navbar-nav>
          </b-navbar>
        </b-col>
      </b-row>
              <b-row class="h-25">
                <b-col>
                  <b-navbar toggleable="lg" type="light" variant="light">
                  <b-navbar-nav>
                  <b-nav-form>
                    <b-input-group size="sm">
                      <b-form-input
                        v-model="filter"
                        type="search"
                        id="filterInput"
                        @update="filterChange"
                        placeholder="Type to Search"
                      ></b-form-input>
                      <b-input-group-append size="sm">
                        <b-button :disabled="!filter" @click="filter = ''">Clear</b-button>
                      </b-input-group-append>
                      <b-form-radio-group
                        size="sm"
        v-model="filterSelected"
        button-variant="outline-secondary"
        :options="filterOptions"
        buttons>
      </b-form-radio-group>
                    </b-input-group>
                  </b-nav-form>
                  </b-navbar-nav>
                  <b-navbar-nav class="ml-auto" right>
                  <b-nav-form>
                    <!--b-button size="sm" class="my-2 my-sm-0"-->
                      <!-- JsonCSV
                        :data = "simplePhoneList"
                        delimiter = ";"
                        name = "phoneList.csv"
                        :bom = true
                      >
                          <img src="@/assets/icons8-export-csv-30.png">
                      </JsonCSV>
                    </b-button-->
                  </b-nav-form>
                  </b-navbar-nav>
                  </b-navbar>
                </b-col>
              </b-row>
            </b-container>
  </div>
  <b-table sm striped :items="historyCashAnalize" :fields="fields"
    :tbody-tr-class="rowClass"
    :sort-compare="sortCompare"
    :filter="complexFilter"
    :filter-function="emptyFilter"
    fixed
    >
    <template v-slot:table-colgroup="scope">
      <col
        v-for="field in scope.fields"
        :key="field.key"
        :style="{ width: field.width ? field.width : '100px' }"
      >
    </template>
  <!--
      { "item": [ "9682662030", { "idhistory": 346,
      "phone": { "id": 909,
      "people": { "first": "Захаревич", "last": "Жанна", "second": "Николаевна",
      "fio": "Захаревич Жанна Николаевна", "account": "jzaharevich",
      "department": "Ректорат", "position": "Начальник аппапата ректора",
      "adstate": 1, "serviceaccount": 0, "active": true, "people": 582 },
      "phone": "9682662030", "state": 1, "contract": "395846009",
      "tariff": "26CIRC_T", "active": true },
      "people": { "first": "Захаревич", "last": "Жанна", "second": "Николаевна",
      "fio": "Захаревич Жанна Николаевна", "account": "jzaharevich",
      "department": "Ректорат", "position": "Начальник аппапата ректора",
      "adstate": 1, "serviceaccount": 0, "active": true, "people": 582 },
      "datestart": "2018-12-31T21:00:00.000+0000",
      "dateend": null, "comment": null,
      "type": { "idhistorytype": 1, "name": "Выдача номера" } },
      null, null, null ], "index": 193,
      "field": { "key": "0", "label": "Телефон", "sortable": true },
      "unformatted": "9682662030", "value": "9682662030",
      "detailsShowing": false, "rowSelected": false }
9682662030,
    -->
    <template v-slot:cell(0)="data">
      {{ data.value.phone }}
    </template>
    <template v-slot:cell(fio)="data">
      <span v-if="data.item[1] && data.item[1]['people']">
        <span :title="getStateName(data.item[0].state)">
          {{ data.item[1]['people']['fio'] }}
        </span>
      </span>
      <span v-else-if="data">
        {{ getStateName(data.item[0].state) }}
      </span>
    </template>
    <template v-slot:cell(services)="data">
      <span v-if="data.item[1] && data.item[1]['phone'] && data.item[1]['phone']['services']">
        {{ printServices(data.item[1]['phone']['services']) }}
      </span>
    </template>
    <template v-slot:cell(department)="data">
      <span v-if="data.item[1] && data.item[1]['people']">
        {{ print(data.item[1]['people']['department']) }}
      </span>
    </template>
    <template v-slot:cell(datestart)="data">
      <span v-if="data.item[1]">
        {{ formatDate(data.item[1]['datestart']) }}
      </span>
    </template>
    <template v-slot:cell(dateend)="data">
      <span v-if="data.item[1]">
        {{ formatDate(data.item[1]['dateend']) }}
      </span>
    </template>
    <template v-slot:cell(duration)="data">
      <span v-if="data.item[1]">
        {{ data.item[1]['duration'] = countDuration(data.item[1]['datestart'], data.item[1]['dateend']) }}
      </span>
    </template>
    <template v-slot:cell(2)="data">
      {{ printDouble(data.value) }}
    </template>
    <template v-slot:cell(3)="data">
      {{ printDouble(data.value) }}
    </template>
    <template v-slot:cell(4)="data">
      {{ printDouble(data.value) }}
    </template>
  </b-table>
</div>
</template>

<script>
import Vue from 'vue'
import Vuex from 'vuex'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import { formatRow, searchFilter } from '../utils'

Vue.use(BootstrapVue)

export default {
  data () {
    return {
      fields: [ { key: '0', label: 'Телефон', sortable: true },
        { key: 'fio', label: 'ФИО', width: '150px', sortable: true },
        { key: 'department', label: 'Подразделение', width: '160px', sortable: true },
        { key: 'datestart', label: 'Дата начала', sortable: true },
        { key: 'dateend', label: 'Дата окончания', sortable: true },
        { key: 'duration', label: 'Длит.', sortable: true },
        { key: 'services', label: 'Услуги', width: '350px', sortable: true },
        { key: '2', label: '1 мес', sortable: true },
        { key: '3', label: '6 мес', sortable: true },
        { key: '4', label: '1 год', sortable: true }],
      filterSelected: 'full',
      filterOptions: [
        { value: 'full', text: 'Выданные' },
        { value: 'empty', text: 'Не выданные' },
        { value: 'all', text: 'Все' }
      ],
      // filter: '',
      tagsSelected: []
      // complexFilter: ',' + this.filterSelected
    }
  },
  computed: {
    ...Vuex.mapGetters({
      historyCashAnalize: 'HistoryStore/getHistoryCashAnalize'
    }),
    complexFilter: function () {
      // return this.filter + ',' + this.filterSelected
      return this.localSearchFilter + ',' + this.filterSelected
    }
  },
  watch: {
  },
  mounted () {
    if (!this.historyCashAnalize || !this.historyCashAnalize.length) {
      this.loadData()
    }
  },
  mixins: [formatRow, searchFilter],
  methods: {
    print (val) {
      if (val) {
        return val
      }
      return ''
    },
    printDouble (val) {
      if (val) {
        // return val.toFixed()
        // let i = val.toFixed()
        return parseFloat(val.toFixed()).toLocaleString()
      }
      return 0
    },
    checkAvailable (val) {
      // console.log(val)
    },
    formatDate (date) {
      if (!date) {
        return ''
      }
      let dt = new Date(date)
      let options = { year: 'numeric', month: 'long' }
      return dt.toLocaleDateString('ru-RU', options)
    },
    formatMonth (date) {
      let dt = new Date(date)
      let options = { month: 'long' }
      return dt.toLocaleDateString('ru-RU', options)
    },
    async loadData () {
      await this.$store.dispatch('HistoryStore/loadHistoryCashAnalize')
      // console.log(this.historyCashAnalize)
    },
    rowClass (item, type) {
      // console.log(item[1], item[1].history)
      let people = (item[0] && item[0].people) ? item[0].people : null
      let state = (item[0] && item[0].state) ? item[0].state : null
      let datestart = (item[1] && item[1].datestart) ? item[1].datestart : null
      return this.getRowClass(state, people, 1, datestart)
      // if (!item) return
      // if (item[0]['dateend']) return 'textGray'
      // if (item.people['adstate'] === 0) return 'table-danger'
    },
    countDuration (date1, date2) {
      if (!date1) {
        return
      }
      let dt1
      let multiplier = 1
      if (!date2) {
        dt1 = new Date(date1)
      } else {
        dt1 = new Date(date2)
        multiplier = -1
      }
      let now = new Date()
      return ((now - dt1) / 1000 / 60 / 60 / 24 * multiplier).toFixed()
    },
    emptyFilter (row, filter) {
      let filters = filter.split(',')
      // let filters = this.localSearchFilter.split(',')
      let res = true
      if (this.filterSelected && this.filterSelected.length) {
        switch (this.filterSelected) {
          case 'full':
            if (!row[1] || row[1]['dateend']) {
              res = false
            }
            break
          case 'empty':
            if (!row[1] || (row[1] && row[1]['dateend'])) {
              res = true
            } else {
              res = false
            }
            break
          case 'all':
            break
        }
      }
      let text = JSON.stringify(row)
      // console.log(text, filters, text.indexOf(filters[0]))
      if (res && text.indexOf(filters[0]) >= 0) {
        res = true
      } else if (filters[0].length) {
        res = false
      }
      // tagsSelected
      // console.log(this.tagsSelected)
      if (res && this.tagsSelected.length) {
        res = false
        this.tagsSelected.forEach(element => {
          if (!res && text.indexOf(element) >= 0) {
            res = true
          } else {
            res = false
          }
        })
      }
      return res
    },
    sortCompare (aRow, bRow, key, sortDesc, formatter, compareOptions, compareLocale) {
      let t1
      let t2
      if (key === '0' || key === '2' || key === '3' || key === '4') {
        t1 = aRow[key]
        t2 = bRow[key]
      } else {
        t1 = aRow[1]
        t2 = bRow[1]
      }
      // console.log(aRow[key], bRow[key], key, t1 && !t2, !t1 && t2)
      if (t1 && !t2) {
        return 1
      } else if (!t1 && t2) {
        return -1
      }
      switch (key) {
        case 'duration':
          if (aRow[1] && bRow[1]) {
            const a = parseInt(aRow[1][key]) // or use Lodash `_.get()`
            const b = parseInt(bRow[1][key])
            return a < b ? -1 : a > b ? 1 : 0
          }
          break
        case 'fio':
        case 'department':
          if (aRow[1] && bRow[1]) {
            const a = aRow[1]['people'][key] // or use Lodash `_.get()`
            const b = bRow[1]['people'][key]
            return a < b ? -1 : a > b ? 1 : 0
          }
          break
        case '0':
          let a = aRow[key] // or use Lodash `_.get()`
          let b = bRow[key]
          // console.log(a, b, a < b ? -1 : a > b ? 1 : 0)
          return a < b ? -1 : a > b ? 1 : 0
        case '2':
        case '3':
        case '4':
          a = parseFloat(aRow[key]) // or use Lodash `_.get()`
          b = parseFloat(bRow[key])
          // console.log(a, b, a < b ? -1 : a > b ? 1 : 0)
          return a < b ? -1 : a > b ? 1 : 0
        default:
          break
      }
      return 0
    },
    // { "name": "Форсаж 3 ГБ", "code": "YUG2FG3T",
    // "type": 2, "datestart": "2017-03-15T00:00:00.000+0000",
    // "dateend": null },
    // { "name": "Интернет-уведомление в роуминге",
    // "code": "GPRSNOT_C", "type": 2, "datestart":
    // "2017-03-16T00:00:00.000+0000", "dateend": null }, { "name": "Доступ в 4G", "code": "LTE_INETP", "type": 2, "datestart": "2017-03-16T00:00:00.000+0000", "dateend": null } ]
    printServices (data) {
      let res = ''
      data.forEach(element => {
        if (res.length) {
          res += ', '
        }
        res += element.name + ' (' + element.code + ')'
      })
      return res
    }
  }
}
</script>

<style>
  .container {
    margin-left: 0;
  }
</style>
