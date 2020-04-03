<template lang='html'>
<div id="phoneListComponent">
            <b-container class="h-25 d-inline-block">
              <b-row>
                <b-col>
                  <b-navbar toggleable="lg" type="light" variant="light">
                  <b-navbar-nav>
                  <b-nav-form>
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
                        <b-button @click="addPhone()">Add phone</b-button>
                        <b-button @click="syncADState()">Sync AD State</b-button>
                      </b-input-group-append>
                    </b-input-group>
                  </b-nav-form>
                  </b-navbar-nav>
                  <b-navbar-nav class="ml-auto" right>
                  <b-nav-form>
                    <!--b-button size="sm" class="my-2 my-sm-0"-->
                      <JsonCSV
                        :data = "simplePhoneList"
                        delimiter = ";"
                        name = "phoneList.csv"
                        :bom = true
                        :labels = "csvLabels"
                      >
                          <img src="@/assets/icons8-export-csv-30.png">
                      </JsonCSV>
                    <!--/b-button-->
                  </b-nav-form>
                  </b-navbar-nav>
                  </b-navbar>
                </b-col>
              </b-row>
              <b-row v-if="updates.length">
                {{ updates }}
              </b-row>
              <b-row v-if="errors.length">
                {{ errors }}
              </b-row>
            </b-container>

            <b-table :items="phoneList"
              :fields="fields"
              :tbody-tr-class="rowClass"
              :filter="localSearchFilter"
              :striped="true"
              :small="true"
              class="h-25 d-inline-block"
              :sort-by="people"
              selected-variant="active" select-mode="single">
              <!-- A custom formatted column -->
              <template v-slot:cell(phonePlusBtn)="row">
                <!--b-button size="sm" @click="editPhone(row)"-->
                <b-button size="sm" variant="outline-light" @click="editPhone(row)">
                  <!--span v-if="!checkEdit(row)"-->
                  <img v-if="!checkEdit(row)" src="@/assets/icons8-forward-40.png" width="15px;">
                  <img v-else src="@/assets/icons8-expand-arrow-40.png" width="15px;">
                </b-button>
              </template>
              <template v-slot:cell(phone)="data">
                  {{ print(data.value) }}
              </template>
              <template v-slot:cell(people)="data">
                <span v-if="data.value">
                  {{ print(data.value.fio) }}
                </span>
                <span v-else>
                  {{ getStateName(data.item.state) }}
                </span>
              </template>
              <template v-slot:cell(department)="data">
                <span v-if="data.item['people']">
                  {{ print(data.item.people.department) }}
                </span>
              </template>
              <template v-slot:cell(actions)="row">
                <!--b-button size="sm" @click="editPhone(row)">
                  {{ checkEdit(row) ? 'Hide' : 'Show' }} Phone Edit
                </b-button-->
                <b-button class="btn-no-padding" size="sm" @click="showDetail(row)" variant="outline-light">
                  <img v-if="!checkDetail(row)"
                    src="@/assets/icons8-contact-30.png"
                    width="30px;">
                  <img v-else src="@/assets/icons8-expand-arrow-40.png" width="30px;">
                </b-button>
              </template>
              <template v-slot:row-details="row">
                <b-card v-if="editPhoneMode">
              <b-container>
              <b-row>
                <b-col sm="2">
                  <label for="new-phone">Телефон</label>
                </b-col>
                <b-col sm="10">
                  <b-form-input id="new-phone" v-model="row.item['phone']"></b-form-input>
                </b-col>
              </b-row>
              <b-row>
                <b-col sm="2">
                  <label for="new-phone-contract">Договор</label>
                </b-col>
                <b-col sm="10">
                  <b-form-select id="new-phone-contract" :options="contractList" v-model="row.item['contract']"></b-form-select>
                </b-col>
                </b-row>
              <b-row>
                <b-col sm="2">
                  <label for="new-phone-state">Состояние</label>
                </b-col>
                <b-col sm="10">
                  <b-form-select
                    id="new-phone-state"
                    :options="stateList"
                    v-model="row.item['state']"></b-form-select>
                </b-col>
              </b-row>
              <!--b-row>
                <b-col sm="2">
                  <label for="new-phone-active">Active</label>
                </b-col>
                <b-col sm="10">
                  <b-form-checkbox id="new-phone-active" v-model="row.item['active']"></b-form-checkbox>
                </b-col>
              </b-row-->
              <b-row>
                <b-col sm="2">
                  <label for="new-phone-tariff">Тариф</label>
                </b-col>
                <b-col sm="10">
                  <b-form-input id="new-phone-tariff" v-model="row.item['tariff']"></b-form-input>
                </b-col>
              </b-row>
              <b-row>
                <b-col>
                  <b-button align="right" size="sm"
                            @click="createOrUpdatePhone(row.item)">
                    Create or Update
                  </b-button>
                </b-col>
                <b-col>
                 <mark v-if="isError" class="inline-block secondary">Ошибка: {{ errorCode }}</mark>
                 <mark v-if="isOK" class="inline-block ok">OK</mark>
                </b-col>
              </b-row>
              </b-container>

                </b-card>
                <b-card v-if="!editPhoneMode">
                    <b-container size="xs" fluid>
                        <span v-if="!row.item.people">
                          {{ row.item = generateEmptyPeople(row.item) }}
                        </span>
                        <div>
                        <b-button variant="link" @click="findPeople = !findPeople">
                          <img src="@/assets/icons8-forward-24.png" width="24px;">
                          Поиск сотрудников
                        </b-button>
                        </div>
                        <div>
                        <FindPeople v-if="findPeople"></FindPeople>
                        </div>
                        <span v-for="(value, key) in row.item.people" :value="value" :key="key">
                        <span v-if="checkPeopleKeyType(key, 'text')">
                        <b-row class="my-1"  :key="key">
                          <b-col sm="2">
                            <label  :for="`${key}`">{{ getPeopleFieldLabel(key) }}:</label>
                          </b-col>
                          <b-col >
                            <!--b-form-select v-if="key == 'fio' && value == ''"
                              v-model="peopleListSelected"
                              :options="peopleList"
                              text-field="fio"
                              value-field="people"
                              @change="selectPeople()"></b-form-select-->
                            <b-form-input :id="`${key}`" v-model="row.item.people[key]" :placeholder="`${value}`"></b-form-input>
                          </b-col>
                        </b-row>
                        </span>
                        <span v-if="checkPeopleKeyType(key, 'checkbox')">
                        <b-row class="my-1"  :key="key">
                          <b-col>
                            <label  :for="`${key}`">{{ getPeopleFieldLabel(key) }}:</label>
                          </b-col>
                          <b-col align="left">
                            <b-form-checkbox :value="1" :unchecked-value="0" :key="`${key}`" v-model="row.item.people[key]"></b-form-checkbox>
                          </b-col>
                        </b-row>
                        </span>
                        </span>
                        <b-row>
                          <b-col sm="3">
                        <b-button v-if="checkUpdate(row)"
                                  align="right" size="sm"
                                  @click="updatePeople(row.item.people)"
                                  :disabled="row.item.people && !row.item.people['people']">
                          Update
                        </b-button>
                            <b-button v-if="checkCreate(row)"
                                      align="right" size="sm"
                                      @click="createPeople(row.item)"
                                      :disabled="!createbutton[row.item['id']]">
                              Create
                            </b-button>
                          </b-col>
                        <span v-if="history">
                          <b-col>
                          <Datepicker :highlighted="pickerState.highlighted"
                            :bootstrap-styling="true"
                            v-model="history['datestart']">
                            <div slot="beforeCalendarHeader" class="calender-header">
                              Choose a Date Start
                            </div>
                          </Datepicker>
                          </b-col>
                        </span>
                          <b-col>
                            <mark v-if="isError" class="inline-block secondary">Ошибка: {{ errorCode }}</mark>
                            <mark v-if="isOK" class="inline-block ok">OK</mark>
                          </b-col>
                        </b-row>
                        <b-row>
                          <b-col sm="3">
                            <b-button v-if="!createbutton[row.item['id']]" :id="row.item['id']"
                                      @click="freePhone(history)"
                                      :disabled="freebutton[row.item['id']]"
                                      align="right" size="sm">
                              Free phone
                            </b-button>
                          </b-col>
                          <span v-if="history">
                          <b-col>
                          <Datepicker @selected="setButtonInVisibility(row.item['id'], false)"
                                      v-model="history['dateend']"
                                      :bootstrap-styling="true"
                                      :highlighted="pickerState.highlighted"
                                      >
                            <div slot="beforeCalendarHeader" class="calender-header">
                              Choose a Date End
                            </div>
                          </Datepicker>
                          </b-col>
                          <!--b-col>
                            <mark v-if="isError" class="inline-block secondary">Ошибка: {{ errorCode }}</mark>
                            <mark v-if="isOK" class="inline-block ok">OK</mark>
                          </b-col-->
                        </span>
                        </b-row>
                      </b-container>
                </b-card>
              </template>
            </b-table>
</div>
</template>

<script>
import Vue from 'vue'
import Vuex from 'vuex'
import { getRestApi } from '../http-common'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import Datepicker from 'vuejs-datepicker'
import JsonCSV from 'vue-json-csv'
import FindPeople from '../components/FindPeopleComponent'
import { PHONES_STATE, formatRow, searchFilter } from '../utils'
// import { isObject } from 'util';
// import { selectPeople, findPeople } from '../modules/PhoneListStore/actions'

Vue.use(BootstrapVue)

export default {
  data () {
    return {
      freebutton: [],
      createbutton: {},
      fields: [
        { key: 'phonePlusBtn', label: '' },
        { key: 'phone', label: 'Телефон', sortable: true },
        { key: 'people', label: 'ФИО', sortable: true },
        { key: 'department', label: 'Подразделение', sortable: true },
        { key: 'actions', label: '', sortable: false }
      ],
      newPhoneFields: [
        { key: 'phone', sortable: true },
        { key: 'contract', label: 'Контракт' },
        { key: 'state', label: 'Состояние' }
      ],
      peopleFields: [
        { key: 'fio', label: 'ФИО', type: 'text' },
        { key: 'account', label: 'Уч. запись', type: 'text' },
        { key: 'department', label: 'Подразделение', type: 'text' },
        { key: 'position', label: 'Должность', type: 'text' },
        { key: 'adstate', label: 'Состояние', type: 'checkbox' },
        {
          key: 'serviceaccount',
          label: 'Сервисная уч. запись',
          type: 'checkbox'
        }
      ],
      test: { fio: { label: '' }, account: { label: '' } },
      contractList: [
        { value: '395846009', text: 'Beeline main 395846009' },
        { value: '512710831', text: 'Beeline LC141 512710831' },
        { value: '154802955', text: 'Megafon 154802955' }
      ],
      stateList: [
        { value: PHONES_STATE.ACTIVE, text: 'Активный' },
        { value: PHONES_STATE.INACTIVE, text: 'Заблокированный' },
        { value: PHONES_STATE.EXCLUDED, text: 'Исключен' },
        { value: PHONES_STATE.REISSUED, text: 'Переоформлен' }
      ],
      csvLabels: {
        phone: 'Телефон',
        fio: 'ФИО',
        department: 'Подразделение',
        position: 'Должность',
        state: 'Состояние'
      },
      posts: [],
      updates: [],
      errors: [],
      errorCode: '',
      // filter: this.searchFilter,
      selected: [],
      history: [],
      // acthistory: [],
      infoModal: {
        id: 'info-modal',
        title: '',
        content: ''
      },
      peopleList: [],
      peopleListSelected: '',
      simplePhoneList: [],
      currentRow: [],
      newphoneid: -1,
      newphone: '',
      postRes: null,
      editPhoneMode: false,
      filterEmptyPeople: true,
      findPeople: false
    }
  },
  computed: {
    ...Vuex.mapGetters({
      phoneList: 'PhoneListStore/getPhoneList',
      selectPeople: 'PhoneListStore/getSelectPeople'
      // searchFilter: 'GlobalStore/getSearchFilter'
      // activeHistory: 'PhoneListStore/getActiveHistory'
    }),
    sortOptions () {
      // Create an options list from our fields
      return this.fields.filter(f => f.sortable).map(f => {
        return { text: f.label, value: f.key }
      })
    },
    pickerState () {
      return {
        highlighted: {
          dates: [ new Date() ]
        },
        datenow: new Date()
      }
    },
    isError () {
      return this.errorCode !== ''
    },
    isOK () {
      if (!this.isError && this.postRes) return true
      return false
    }
    /**
    localSearchFilter () {
      // console.log('computed:', this.searchFilter)
      // if (this.searchFilter !== this.filter) {
      //  this.filter = this.searchFilter
      // }
      if (this.searchFilter) return this.searchFilter
      return ''
    }
    */
  },
  watch: {
    phoneList: function () {
      this.generatePhoneList()
    },
    selectPeople: function () {
      // console.log('selectPeople', this.selectPeople, this.detailrow)
      if (this.detailrow) {
        // console.log(this.selectPeople.fio, this.detailrow.item.people)
        if (!this.detailrow.item.people.people) {
          this.detailrow.item.people.fio = this.selectPeople.fio
          this.detailrow.item.people.position = this.selectPeople.position
          this.detailrow.item.people.department = this.selectPeople.department
          this.detailrow.item.people.account = this.selectPeople.account
          this.detailrow.item.people.adstate = this.selectPeople.adstate
          this.detailrow.item.people.people = this.selectPeople.people
          console.log(this.detailrow.item.people)
        }
        this.findPeople = false
      }
    }
  },
  /**
  beforeRouteUpdate (to, from, next) {
    // вызывается когда маршрут, что рендерит этот компонент изменился,
    // но этот компонент будет повторно использован в новом маршруте.
    // Например, для маршрута с динамическими параметрами `/foo/:id`, когда мы
    // перемещаемся между `/foo/1` и `/foo/2`, экземпляр того же компонента `Foo`
    // будет использован повторно, и этот хук будет вызван когда это случится.
    // Также имеется доступ в `this` к экземпляру компонента.
    console.log('route:', to, from, next)
    this.filter = this.searchFilter
  },
  */
  components: {
    Datepicker,
    JsonCSV,
    FindPeople
  },
  mixins: [formatRow, searchFilter],
  mounted () {
    // console.log('mounted phonelist: ', this.$store.getters['PhoneListStore/getPhoneList'])
    console.log(this.filter, this.searchFilter)
    if (!this.phoneList || !this.phoneList.length) {
      // this.$store.dispatch('PhoneListStore/loadPhoneList')
      this.loadPhoneList()
    }
  },
  methods: {
    showDetail (data) {
      this.peopleListSelected = ''
      this.history = []
      this.editPhoneMode = false
      this.postRes = null
      this.findPeople = false
      if (
        this.detailrow &&
        this.detailrow.item['id'] !== data.item['id'] &&
        !this.detailrow.detailsShowing
      ) {
        this.detailrow.toggleDetails()
        // console.log('show: ', this.detailrow.detailsShowing)
      }
      if (!data) {
        return
      }
      data.toggleDetails()
      data['field']['showdetail'] = !data['field']['showdetail']
      this.detailrow = data
      if (!data.detailsShowing) {
        this.loadActiveHistory(data.item)
        if (this.history) {
          this.setButtonInVisibility(data.item['id'], true)
        }
      }
    },
    editPhone (data) {
      this.peopleListSelected = ''
      this.history = []
      this.postRes = null
      this.findPeople = false
      if (
        this.detailrow &&
        this.detailrow.item['id'] !== data.item['id'] &&
        !this.detailrow.detailsShowing
      ) {
        this.detailrow.toggleDetails()
        // console.log('show: ', this.detailrow.detailsShowing)
      }
      data.toggleDetails()
      data['field']['editphone'] = !data['field']['editphone']
      this.editPhoneMode = !data.detailsShowing
      this.detailrow = data
      if (!data.detailsShowing) {
      }
    },
    checkDetail (data) {
      // console.log(data.detailsShowing, this.detailrow, data)
      if (
        data &&
        data.detailsShowing &&
        this.detailrow &&
        !this.editPhoneMode &&
        this.detailrow.index === data.index
      ) {
        return true
      }
      return false
    },
    checkEdit (data) {
      // console.log(data.detailsShowing, this.detailrow, data)
      if (
        data &&
        data.detailsShowing &&
        this.detailrow &&
        this.editPhoneMode &&
        this.detailrow.index === data.index
      ) {
        return true
      }
      return false
    },
    showStatusIcon (data) {
      // @/assets/icons8-plus-24.png
      // console.log(data.detailsShowing, this.detailrow, data)
      if (data &&
        this.detailrow &&
        data.detailsShowing &&
        this.detailrow.index === data.index) {
        return '~/assets/icons8-expand-arrow-40.png'
      }
      return '~/images/icons8-forward-40.png'
    },
    generateEmptyPeople (row) {
      this.$set(row, 'people', {
        fio: '',
        account: '',
        department: '',
        position: '',
        serviceaccount: '0',
        adstate: '0'
      })
      let date = new Date()
      this.createbutton[row['id']] = true
      this.$set(this.history, 'datestart', date.toDateString())
      this.loadPeopleList()
      // console.log('hist:', this.history)
      return row
    },
    async createPeople (phone) {
      // console.log('create phone: ', phone, this.history)
      let data = {}
      // let hist = {}
      // let date = new Date()
      // date.set
      let date = new Date(this.history['datestart'])
      let dt = new Date(
        Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 0, 0, 0)
      )
      // dt.UTC(date.getFullYear(), date.getMonth(), date.getDate())
      // console.log('date: ', dt.toString(), dt.getTime())
      data['datestart'] = dt.getTime()
      // hist['phone'] = phone
      // hist['people'] = phone['people']
      data['phone'] = phone
      this.$store.dispatch(
        'PhoneListStore/createAndSetPeople',
        data
      )
        .then(res => {
          // console.log('then error:', res, res.response, res.data)
          if (res && res.status === 200) {
            if (res.data) {
              phone['people'] = res.data
              this.postRes = res.data
            }
            this.createbutton[phone['id']] = false
          } else {
            this.postRes = null
            let resp = res.response
            this.errorCode = (resp && resp.data && resp.data.code) ? res.data.code : 'unknown_error'
            // console.log('test error 1:', this.errorCode)
          }
        })
        .catch(error => {
          this.postRes = null
          console.log('test error:', error, error.response)
          let resp = error.response
          this.errorCode = (resp && resp.data && resp.data.code) ? resp.data.code : 'unknown_error'
          // console.log('test error 2:', this.errorCode)
        })
    },
    async createOrUpdatePhone (phone) {
      // let response =
      this.$store.dispatch('PhoneListStore/createOrUpdatePhone', phone)
        .then(res => {
          // console.log('then error:', res, res.response, res.data)
          if (res && res.status === 200) {
            if (res.data) {
              // if (res && res.data)
              phone = res.data
              // phone['people'] = res.data
              // this.postRes = res.data
            }
            // this.createbutton[phone['id']] = false
          } else {
            // this.postRes = null
            let resp = res.response
            this.errorCode = (resp && resp.data && resp.data.code) ? res.data.code : 'unknown_error'
            // console.log('test error 1:', this.errorCode)
          }
        })

      // let res = await response
      // if (res) {
      //  console.log('resp: ', response, res, res.data)
      // if (res && res.data) phone = res.data
      // this.createbutton[phone['id']] = false
      // }
      // if (response) {
      //  this.createbutton[phone['id']] = false
      // }
    },
    checkCreate (row) {
      // console.log(this.createbutton[row.item['id']])
      if (
        this.createbutton[row.item['id']] &&
        this.createbutton[row.item['id']] === true
      ) {
        return true
      }
      // console.log('create: false')
      return false
    },
    checkUpdate (row) {
      // console.log('update: ', row.item['people'], row.item['people']['people'], row.item['people'] && !row.item['people']['people'])
      if (row.item['people'] && !row.item['people']['people']) {
        return false
      }
      if (
        this.createbutton[row.item['id']] &&
        this.createbutton[row.item['id']] === true
      ) {
        return false
      }
      // console.log('update: true')
      return true
    },
    checkUpdatePhone (row) {
      if (row.item && row.item['phone'] && row.item['contract']) {
        return true
      }
      return false
    },
    setButtonInVisibility (id, visibility) {
      this.$set(this.freebutton, id, visibility)
      // console.log('info2: ', id, ' - ', this.freebutton[id])
    },
    // addhistory(phone, hist) {
    // this.$set(this.acthistory, phone, hist);
    // },
    loadPhoneList () {
      // console.log('Load Phone list')
      // this.$cookies.config('7d')
      this.$store.dispatch('PhoneListStore/loadPhoneList')
      // let cook = this.$cookies.get('CSRF-TOKEN')
      // console.log(cook)
    },
    async loadPeopleList () {
      this.peopleList = await this.$store.dispatch(
        'PhoneListStore/loadPeopleList'
      )
      // console.log(this.peopleList)
    },
    async loadActiveHistory (phone) {
      let data = await this.$store.dispatch(
        'PhoneListStore/loadActiveHistory',
        phone
      )
      if (data) {
        this.history = data
      }
      // console.log('history ', data)
      // return this.$store.getters['PhoneListStore/getActiveHistory2'](phone)
    },
    findHistory (phone) {
      // console.log('ph: ', phone)
      // let data = this.history.filter(function (ele) {
      // console.log('ele: ', ele, phone)
      // return (ele.key === phone)
      // })
      console.log('finds: ', phone, this.history[phone])
      return history
    },
    updatePeople (people) {
      let data = {}
      data['history'] = this.history
      data['history']['people'] = people
      this.$store.dispatch(
        'PhoneListStore/updatePeopleAndHistory',
        data
      )
        .then(res => {
          // console.log('then error:', res, res.response, res.data)
          if (res && res.status === 200) {
            // if (res.data) {
            //   phone['people'] = res.data
            //  this.postRes = res.data
            // }
            // this.createbutton[phone['id']] = false
          } else {
            // this.postRes = null
            let resp = res.response
            this.errorCode = (resp && resp.data && resp.data.code) ? res.data.code : 'unknown_error'
            // console.log('test error 1:', this.errorCode)
          }
        })

      // this.$store.dispatch('PhoneListStore/updatePeople', people);
    },
    freePhone (history) {
      this.$store.dispatch('PhoneListStore/freePhone', history)
        .then(res => {
          // console.log('free res:', res)
          if (res && res.status === 200) {
            // if (res.data) {
            //   phone['people'] = res.data
            this.postRes = res.data
            // }
            // this.createbutton[phone['id']] = false
          } else {
            // this.postRes = null
            let resp = res.response
            this.errorCode = (resp && resp.data && resp.data.code) ? res.data.code : 'unknown_error'
            // console.log('test error 1:', this.errorCode)
          }
        })
    },
    info (item, index, button) {
      // this.infoModal.title = `Row index: ${index}`
      // this.infoModal.content = JSON.stringify(item, null, 2)
      // this.$root.$emit('bv::show::modal', this.infoModal.id, button)
    },
    resetInfoModal () {
      this.infoModal.title = ''
      this.infoModal.content = ''
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
    },
    checkPeopleKeyType (key, type) {
      var keys = Object.keys(this.peopleFields)
      for (var k = 0; k < keys.length; k++) {
        if (
          this.peopleFields[k].key === key &&
          this.peopleFields[k].type === type
        ) {
          return true
        }
      }
      return false
    },
    getPeopleFieldLabel (key) {
      var keys = Object.keys(this.peopleFields)
      for (var k = 0; k < keys.length; k++) {
        if (this.peopleFields[k].key === key) {
          return this.peopleFields[k].label
        }
      }
      return ''
    },
    selectPeople (value) {
      // console.log(this.peopleListSelected)
      let obj
      this.peopleList.find(people => {
        if (people.people === this.peopleListSelected) {
          obj = people
        }
      })
      // console.log(obj, this.detailrow)
      this.detailrow.item.people = obj
    },
    rowClass (item, type) {
      // return formatRow.getRowClass(item.state, item.people)
      return this.getRowClass(item.state, item.people)
    },
    printDouble (val) {
      if (val) {
        // return val.toFixed()
        // let i = val.toFixed()
        return parseFloat(val.toFixed()).toLocaleString()
      }
      return 0
    },
    print (val) {
      if (val) {
        return val
      }
      return ''
    },
    // add new phone
    addPhone () {
      this.$store.dispatch('PhoneListStore/addEmptyPhone')
    },
    // Fetches posts when the component is created.
    /**
    callRestGet (request) {
      AXIOS.post(request)
        .then(response => {
          // JSON responses are automatically parsed.
          this.posts = response.data
        })
        .catch(e => {
          this.errors.push(e)
        })
    },
    callRestPost (request, people) {
      console.log(request)
      // const people = JSON.stringify(body)
      console.log(people)
      AXIOS.post(request, {
        people
      })
        .then(response => {
          // JSON responses are automatically parsed.
          this.updates = response.data
        })
        .catch(e => {
          this.errors.push(e)
        })
      console.log(this.updates)
    },
    */
    generatePhoneList () {
      if (this.phoneList && this.phoneList.length > 0) {
        let array = []
        // console.log(this.csvLabels, isObject(this.csvLabels))
        this.phoneList.forEach(element => {
          let phone = element['phone']
          let fio
          let department
          let position
          let state = element['state']
          if (element['people']) {
            fio = element['people']['fio']
            department = element['people']['department']
            position = element['people']['position']
          } else if (this.filterEmptyPeople) {
            return
          }
          array.push({
            phone: phone,
            fio: fio,
            department: department,
            position: position,
            state: state
          })
        })
        this.simplePhoneList = array
      } else {
        this.simplePhoneList = []
      }
    },
    async syncADState () {
      getRestApi('syncADState')
        .then(response => {
          // JSON responses are automatically parsed.
          this.loadPhoneList()
        })
        .catch(e => {
          this.errors.push(e)
        })
    }
    /**
    filterChange (value) {
      // if (value) console.log('filter chnage:', value)
      this.$store.dispatch('GlobalStore/setSearchFilter', value)
    }
    */
  }
}
</script>
