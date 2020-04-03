<template lang="html">
<div id="CashPhonesComponent">
            <b-container fluid>
              <b-row>
                <b-col>
                  <b-form-group>
                    <b-input-group size="sm">
                      <b-form-input
                        v-model="filter"
                        type="search"
                        id="filterInput"
                        placeholder="Type to Search"
                        @update="filterChange"
                      ></b-form-input>
                      <b-input-group-append size="sm">
                        <b-button :disabled="!filter" @click="filter = ''">Clear</b-button>
                        <b-form-select
                          size="sm"
                          v-model="selectedyear"
                          :options="years"
                          @change="selectYear()">
                        </b-form-select>
                        <JsonCSV
                          :data = "phoneList"
                          delimiter = ";"
                          name = "cashPhones.csv"
                          :bom = true
                          :labels = "csvLabels"
                        >
                          <img src="@/assets/icons8-export-csv-30.png">
                        </JsonCSV>
                      </b-input-group-append>
                    </b-input-group>
                  </b-form-group>
                </b-col>
              </b-row>
              <b-row v-if="updates.length">
                {{ updates }}
              </b-row>
              <b-row v-if="errors.length">
                {{ errors }}
              </b-row>
            <b-row>
            <b-col sm cols="6">
              <div>
                <b-navbar>
                <b-pagination
                  :total-rows="pagesCount"
                  :per-page="phonesPerPage"
                  v-model="phonesPage">
                </b-pagination>
                </b-navbar>
            <b-table :items="filterPhoneList" :fields="fields" :tbody-tr-class="rowClass"
                     :filter="localSearchFilter" :striped="true" :small="true"
                     class="h-25 d-inline-block" :sort-by="sortBy" :current-page="phonesPage" :per-page="phonesPerPage"
                     :sort-desc="true" selectable selected-variant="active"
                     select-mode="single" @row-selected="showDetail">
              <!-- A custom formatted column -->
              <template v-slot:cell(3)="data">
                <b class="text-info" >{{ printDouble(data.value) }}</b>
              </template>
              <template v-slot:table-colgroup="scope">
                <col v-for="field in scope.fields"
                     :key="field.key">
              </template>
              <template v-slot:cell(actions)="row">
                <b-button size="sm" @click="row.toggleDetails">
                  {{ row.detailsShowing ? 'Hide' : 'Show' }} Details
                </b-button>
              </template>

              <template v-slot:row-details="row">
                <b-card>
                    <b-container size="sm" fluid>
                        <span v-for="(value, key) in row.item.people" :value="value" :key="key">
                        <span v-if="checkPeopleKeyType(key, 'text')">
                        <b-row class="my-1"  :key="key">
                          <b-col sm="2">
                            <label  :for="`${key}`">{{ key }}:</label>
                          </b-col>
                          <b-col >
                            <b-form-input :id="`${key}`" v-model="row.item.people[key]" :placeholder="`${value}`"></b-form-input>
                          </b-col>
                        </b-row>
                        </span>
                        <span v-if="checkPeopleKeyType(key, 'checkbox')">
                        <b-row class="my-1"  :key="key">
                          <b-col>
                            <label  :for="`${key}`">{{ key }}:</label>
                          </b-col>
                          <b-col align="left">
                            <b-form-checkbox :value="1" :unchecked-value="0" :key="`${key}`" v-model="row.item.people[key]"></b-form-checkbox>
                          </b-col>
                        </b-row>
                        </span>
                        </span>
                        <b-row>
                          <b-col sm="3">
                        <b-button align="right" size="sm" @click="updatePeople(row.item.people)">
                          Update
                        </b-button>
                          </b-col>
                        <span v-if="history">
                          <b-col>
                          <Datepicker :bootstrap-styling="true" v-model="history['datestart']">
                            <div slot="beforeCalendarHeader" class="calender-header">
                              Choose a Date Start
                            </div>
                          </Datepicker>
                          </b-col>
                        </span>
                        </b-row>
                        <b-row>
                          <b-col sm="3">
                            <b-button :id="row.item['id']" @click="freePhone(history)" :disabled="freebutton[row.item['id']]" align="right" size="sm">
                              Free phone
                            </b-button>
                          </b-col>
                          <span v-if="history">
                          <b-col>
                          <Datepicker @selected="setButtonInVisibility(row.item['id'], false)" v-model="history['dateend']" :bootstrap-styling="true">
                            <div slot="beforeCalendarHeader" class="calender-header">
                              Choose a Date End
                            </div>
                          </Datepicker>
                          </b-col>
                        </span>
                        </b-row>
                      </b-container>
                </b-card>
              </template>
            </b-table>
              </div>
            </b-col>
            <b-col sm cols="6"> <!-- detail info-->
              <div>
                <b-navbar>
                <b-pagination :total-rows="pagesDetailCount" :per-page="detailPerPage" v-model="detailPage">
                </b-pagination>
                <JsonCSV
                  :data = "detail"
                  delimiter = ";"
                  name = "phoneMonthDetail.csv"
                  :bom = true
                >
                  <img src="@/assets/icons8-export-csv-30.png">
                </JsonCSV>
                </b-navbar>
              <b-table :items="detail"
                :fields="detailfields"
                :small="true"
                :current-page="detailPage"
                :per-page="detailPerPage">
                <template v-slot:cell(2)="date">
                  {{ printdate(date.item[2]) }}
                </template>
              </b-table>
              </div>
            </b-col>
            </b-row>
            </b-container>
</div>
</template>

<script>
import Vue from 'vue'
import Vuex from 'vuex'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import Datepicker from 'vuejs-datepicker'
import JsonCSV from 'vue-json-csv'
import { searchFilter } from '../utils'

Vue.use(BootstrapVue)

export default {
  data () {
    return {
      freebutton: [],
      fields: [ { key: '0', label: 'Phone', value: 'Phone', sortable: true },
        { key: '1', label: 'ФИО', sortable: true },
        { key: '2', label: 'Подразделение', sortable: true },
        { key: '3', label: 'Сумма (год, без налогов)', value: 'SUM (year)', sortable: true } ],
      detailfields: [ { key: '0', label: 'ФИО', sortable: true },
        { key: '1', label: 'Подразделение', sortable: true },
        { key: '2', label: 'Месяц', sortable: true },
        { key: '3', label: 'Сумма (без налогов)', sortable: true } ],
      peopleFields: [ { key: 'fio', label: 'ФИО', type: 'text' },
        { key: 'account', label: 'account', type: 'text' },
        { key: 'department', label: 'department', type: 'text' },
        { key: 'position', label: 'position', type: 'text' },
        { key: 'adstate', label: 'adstate', type: 'checkbox' },
        { key: 'serviceaccount', label: 'serviceaccount', type: 'checkbox' } ],
      csvLabels: {
        0: 'Телефон',
        1: 'ФИО',
        2: 'Подразделение',
        3: 'Сумма'
      },
      test: { fio: { label: '' }, account: { label: '' } },
      posts: [],
      sortBy: '2',
      updates: [],
      errors: [],
      detailPerPage: 12,
      phonesPerPage: 12,
      phonesPage: 1,
      detailPage: 1,
      selected: [],
      history: [],
      detail: [],
      acthistory: [],
      selectedyear: this.getLastYear()
    }
  },
  computed: {
    ...Vuex.mapGetters({
      phoneList: 'CashStore/getPhonesCash'
    }),
    pagesCount () {
      if (this.phoneList) {
        return this.phoneList.length
      }
      return 0
    },
    pagesDetailCount () {
      if (this.detail) {
        return this.detail.length
      }
      return 0
    },
    sortOptions () {
    // Create an options list from our fields
      return this.fields
        .filter(f => f.sortable)
        .map(f => {
          return { text: f.label, value: f.key }
        })
    },
    years () {
      const year = new Date().getFullYear()
      return Array.from({ length: 4 }, (value, index) => year - 3 + index)
    },
    filterPhoneList () {
      if (!this.phoneList) return []
      let res = this.phoneList.filter(str => {
        if (JSON.stringify(str).search(this.localSearchFilter) >= 0) return str
      })
      return res
    }
  },
  components: {
    Datepicker,
    JsonCSV
  },
  mixins: [ searchFilter ],
  mounted () {
    // this.selectedyear = new Date().getFullYear()
    // this.$store.dispatch('CashStore/loadPhonesCash')
    this.selectYear()
  },
  created () {
    // this.$store.dispatch('CashStore/loadPhonesCash')
  },
  methods: {
    showDetail (data) {
      // this.detail = data
      // console.log('0: ', data[0][0])
      if (data && data[0] && data[0][0]) this.loadCashDetail(data[0][0])
      // console.log('1: ', this.detail)
    },
    loadPhonesCash () {
      // console.log('Load Phone list')
      this.$store.dispatch('CashStore/loadPhonesCash')
      // console.log(this.$store)
    },
    selectYear () {
      this.$store.dispatch('CashStore/loadPhonesCash', this.selectedyear)
    },
    getLastMonth () {
      let month = new Date().getMonth() + 1
      if (month > 1) {
        return month - 1
      } else {
        return 12
      }
    },
    getLastYear () {
      if (this.getLastMonth() === 12) {
        // console.log(new Date().getFullYear() - 1)
        return new Date().getFullYear() - 1
      } else {
        // console.log(new Date().getFullYear())
        return new Date().getFullYear()
      }
    },
    async loadCashDetail (phone) {
      let formData = new FormData()
      formData.append('phone', phone)
      formData.append('year', this.selectedyear)
      // let data = {}
      // data['phone'] = phone
      // data['year'] = this.selectedyear
      // console.log(data)
      this.detail = await this.$store.dispatch('CashStore/loadCashDetail', formData)
    },
    printdate (dt) {
      let ms = ['Январь', 'Февраль', 'Март',
        'Апрель', 'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь']
      // console.log('import date: ', dt)
      let date = new Date(dt)
      // console.log('date: ', date, date.getFullYear())
      return date.getFullYear() + ': ' + ms[date.getMonth()]
    },
    containsPeopleKey (key) {
      var keys = Object.keys(this.peopleFields)
      for (var k = 0; k < keys.length; k++) {
        // console.log(this.peopleFields[k].key, ': ', key)
        if (this.peopleFields[k].key === key) {
          return true
        }
      }
      return false
      // return this.peopleFields.includes(key)
      // return this.peopleFields.indexOf(key) >= 0
    },
    checkPeopleKeyType (key, type) {
      var keys = Object.keys(this.peopleFields)
      for (var k = 0; k < keys.length; k++) {
        if (this.peopleFields[k].key === key && this.peopleFields[k].type === type) {
          return true
        }
      }
      return false
    },
    rowClass (item, type) {
      if (!item || !item.people) return
      if (item.people['serviceaccount'] === 1) return
      if (item.people['adstate'] === 0) return 'table-danger'
    },
    printDouble (val) {
      if (val) {
        // return val.toFixed()
        // let i = val.toFixed()
        return parseFloat(val.toFixed()).toLocaleString()
      }
      return 0
    },
    convertDoubleToComma (val) {
      if (val) {
        return val.replace('.', ',')
      }
      return 0
    }
  }
}
</script>
