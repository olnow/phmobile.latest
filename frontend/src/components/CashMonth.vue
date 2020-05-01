<template lang="html">
<div>
  <b-navbar size="sm">
    <b-nav-form>
      <b-form-select
        size="sm"
        v-model="selectedmonth"
        :options="months"
        @change="getCashMonth()">
      </b-form-select>
      <b-form-select
        size="sm"
        v-model="selectedyear"
        :options="years"
        @change="getCashMonth()">
      </b-form-select>
    </b-nav-form>
    <b-btn size="sm" @click="getCashMonth()">Сформировать отчет</b-btn>
      <JsonCSV
                        :data = "exportCashMonth"
                        :labels = "csvlabels"
                        delimiter = ";"
                        name = "cashMonth.csv"
                        :bom = true
                      >
                          <img src="@/assets/icons8-export-csv-30.png">
      </JsonCSV>
      <b-btn size="sm" @click="sendToEMail()">Отправить по почте</b-btn>
      <div v-if="timer" class="resultMessage p-1"  :class="requestResultClass">
        {{ requestResult }}
      </div>
  </b-navbar>
  <p/>
  <b-modal
    ref="bv-modal-adjustCash"
    :title="adjustPhone + ': ' + adjustFIO"
    @ok="adjustCashOk"
  >
        <div class="d-block text-center">
          <h5>Укажите сумму корректировки:</h5>
        </div>
        <b-input-group>
          <b-form-select :options="adjustOptions" v-model="adjustSelected">
          </b-form-select>
          <b-input-group-append>
            <b-button size="sm" variant="outline-light" @click="addAdjust()">
              <img src="@/assets/icons8-expand-arrow-40.png" width="15px;">
            </b-button>
          </b-input-group-append>
        </b-input-group>
        <!-- input fields with values-->
        <div v-if="adjustCashValues" class="adjustValues">
          <div v-for="(value, key) in adjustSelectedOptions">
            <label :for="key">{{ getLabel(key) }}</label>
            <b-form-input :id="key" v-model="adjustCashValues[key]"></b-form-input>
          </div>
          <div>
            <label>Сумма: {{ cashSum }}</label>
          </div>
        </div>
      </b-modal>
  <b-table :items="cashMonth" :fields="fields" :small="true" :sort-by="sortBy" :sort-desc="true" selectable selected-variant="active" select-mode="single">
    <template v-slot:cell(3)="data">
      <b class="text-info" >{{ data.value.toFixed(2) }}</b>
    </template>
    <template v-slot:cell(adjust)="data">
      <b-button size="sm" variant="outline-light" @click="adjustCash(data.item)">
        <img src="@/assets/icons8-expand-arrow-40.png" width="15px;">
      </b-button>
      <!--b-modal
        :ref="'bv-modal-adjustCash-' + data.item[0]"
        :title="data.item[0] + ': ' + data.item[1]"
        @ok="adjustCashOk"
      -->
    </template>
  </b-table>
</div>
</template>

<script>
import Vue from 'vue'
import Vuex from 'vuex'
import { postRestApi } from '../http-common'
import { BootstrapVue, ModalPlugin } from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import JsonCSV from 'vue-json-csv'
import PhoneCash from './PhoneCash'
// import { ModalPlugin } from 'bootstrap-vue'

Vue.use(BootstrapVue)
Vue.use(ModalPlugin)

export default {
  data () {
    return {
      fields: [ { key: '0', label: 'Телефон', sortable: true },
        { key: '1', label: 'ФИО', sortable: true },
        { key: '2', label: 'Подразделение', sortable: true },
        { key: '3', label: 'Сумма, руб \n(без налогов)', sortable: true },
        { key: 'adjust', label: '' } ],
      csvlabels: {
        'phone': 'Телефон',
        'fio': 'ФИО',
        'department': 'Подразделение',
        'sum': 'Сумма, руб (без налогов)'
      },
      adjustOptions: [
        { key: 'internationalcalls', value: 'internationalcalls', text: 'Международные вызовы' },
        { key: 'longcalls', value: 'longcalls', text: 'Междугородные вызовы' },
        { value: 'localcalls', text: 'Местные вызовы' },
        { value: 'localsms', text: 'Местные смс' },
        { value: 'gprs', text: 'Интернет' },
        { value: 'internationalroamingcalls', text: 'Вызовы в МН роуминге' },
        { value: 'internationalroamingsms', text: 'СМС в МН роуминге' },
        { value: 'internationalgprsroaming', text: 'Интернет в МН роуминге' },
        { value: 'internationalroamingcash', text: 'Начисления в МН роуминге' },
        { value: 'russiaroamingcalls', text: 'Вызовы в МГ роуминге' },
        { value: 'russiaroamingsms', text: 'СМС в МГ роуминге' },
        { value: 'russiaroaminginet', text: 'Интернет в МГ роуминге' },
        { value: 'russiaroamingtraffic', text: 'Трафик в МГ роуминге' },
        { value: 'subscriptionfee', text: 'Абонентская плата' },
        { value: 'subscriptionfeeaddon', text: 'Абонентская плата (доп. усл.)' },
        { value: 'discounts', text: 'Скидки' },
        { value: 'onetime', text: 'One time' },
        { value: 'sum', text: 'Сумма' },
        { value: 'vat', text: 'Налоги' },
        { value: 'fullsum', text: 'Полная сумма' }
      ],
      adjustSelectedOptions: {},
      adjustSelected: '',
      adjustCashValues: null,
      adjustPhone: '',
      adjustFIO: '',
      requestResult: '',
      timer: 0,
      posts: [],
      errors: [],
      selectedyear: 0,
      selectedmonth: 0,
      exportCashMonth: [],
      exportTemplate: 'phone,fio,department,sum',
      exportTypes: { 'sum': 'double' },
      sortBy: '3',
      exportSkipZero: true,
      infoModal: {
        id: 'info-modal',
        title: '',
        content: ''
      }
    }
  },
  watch: {
    cashMonth: function () {
      this.generateExportCashMonth(
        this.cashMonth,
        this.exportTemplate,
        this.exportTypes,
        this.exportSkipZero)
    }
  },
  computed: {
    ...Vuex.mapGetters({
      cashMonth: 'CashStore/getCashMonth',
      getSelectedMonth: 'CashStore/getSelectedMonth',
      getSelectedYear: 'CashStore/getSelectedYear'
    }),
    years () {
      const year = new Date().getFullYear()
      return Array.from({ length: 4 }, (value, index) => year - 3 + index)
    },
    months () {
      // const month = 12
      return Array.from({ length: 12 }, (value, index) => 1 + index)
    },
    cashSum () {
      // console.log(this.adjustSelectedOptions, Object.keys(this.adjustSelectedOptions))
      let sum = 0.0
      // let keys = Object.keys(this.adjustSelectedOptions)
      if (this.adjustSelectedOptions && Object.keys(this.adjustSelectedOptions)) {
        for (var el in this.adjustSelectedOptions) {
          sum += parseFloat(this.adjustCashValues[el])
          // console.log(sum)
        }
      }
      return sum
    },
    requestResultClass () {
      return 'bg-' + this.requestResult
    }
  },
  mounted () {
    // console.log('mounted phonelist: ', this.$store.getters['PhoneListStore/getPhoneList'])
    this.selectedmonth = this.getSelectedMonth || this.getLastMonth()
    this.selectedyear = this.getSelectedYear || this.getLastYear()
    // console.log('mounted: ', this.getSelectedMonth, this.getSelectedYear, this.selectedmonth, this.getSelectedMonth || 4)
    if (!this.cashMonth || !this.cashMonth.length) {
      // this.$store.dispatch('PhoneListStore/loadPhoneList')
      this.getCashMonth()
    }
  },
  methods: {
    // Fetches posts when the component is created.
    loadCashYear (request) {
      // postRestApi()
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
    getCashMonth () {
      // console.log('getCashMonth: ', this.selectedmonth, this.selectedyear, this.getSelectedMonth, this.getSelectedYear)
      let formData = new FormData()
      formData.append('year', this.selectedyear)
      this.$store.dispatch('CashStore/setSelectedMonth', this.selectedmonth)
      this.$store.dispatch('CashStore/setSelectedYear', this.selectedyear)
      formData.append('month', this.selectedmonth)
      this.$store.dispatch('CashStore/loadCashMonth', formData)
      this.showOKMessage(3)
    },
    generateExportCashMonth (source, template, types, skipZero) {
      if (!template || !source) {
        return
      }
      if (source && source.length > 0) {
        let array = []
        let params = template.split(',')
        source.forEach(element => {
          let index = 0
          let row = {}
          let skip = false
          params.forEach(param => {
            let val = element[index]
            if (types && types[param]) {
              switch (types[param]) {
                case 'double':
                  if (val === 0) {
                    skip = true
                  }
                  val = val.toString().replace('.', ',')
                  break
                default:
              }
            }
            row[param] = val
            index++
          })
          if (!skip) {
            array.push(row)
          }
        })
        this.exportCashMonth = array
      } else {
        this.exportCashMonth = []
      }
    },
    sendToEMail () {
      let formData = new FormData()
      formData.append('year', this.selectedyear)
      formData.append('month', this.selectedmonth)
      postRestApi('sendCashToEMail', formData)
    },
    adjustCash (item) {
      if (!this.adjustCashValues) {
        // this.adjustCashValues = new PhoneCash({ phone: item[0], people: item[1], month: this.selectedmonth })
        // this.adjustPhone = item[0]
        this.adjustCashValues = new PhoneCash()
      }
      this.adjustPhone = item[0]
      this.adjustFIO = item[1]
      // console.log(item, this.adjustPhone, this.$refs)
      this.$refs['bv-modal-adjustCash'].show()
    },
    hideModal (id) {
      this.$refs[id].hide()
    },
    addAdjust () {
      if (this.adjustSelected === '') return
      // if (!this.adjustSelectedOptions.sum) {
      //   this.$set(this.adjustSelectedOptions, 'sum', 0)
      // }
      this.$set(this.adjustSelectedOptions, this.adjustSelected, 0)
    },
    getLabel (key) {
      var keys = Object.keys(this.adjustOptions)
      for (var k = 0; k < keys.length; k++) {
        // console.log(this.adjustOptions[k].value, key)
        if (this.adjustOptions[k].value === key) {
          return this.adjustOptions[k].text
        }
      }
      return ''
    },
    startTimer (time) {
      this.timer = time
    },
    countDownTimer () {
      if (this.timer) {
        // console.log(this.timer)
        return setTimeout(() => {
          --this.timer
          this.countDownTimer()
        }, 1000)
      }
    },
    showOKMessage (time) {
      this.requestResult = 'success'
      this.startTimer(time)
      this.countDownTimer()
    },
    adjustCashOk () {
      this.adjustCashValues.sum = this.cashSum
      this.adjustCashValues.fullsum = this.cashSum
      // console.log(this.adjustPhone)
      let formData = new FormData()
      formData.append('year', this.selectedyear)
      formData.append('month', this.selectedmonth)
      formData.append('phone', this.adjustPhone)
      formData.append('phonecash', JSON.stringify(this.adjustCashValues))
      postRestApi('cash/adjustPhoneCash', formData)
        .then(res => {
          if (res && res.data && res.data.idphonecash) {
            // this.showOKMessage(20)
            this.getCashMonth()
            // console.log(res.data)
          }
        })
      this.adjustCashValues = null
      // this.adjustSelectedOptions = {}
    }
  },
  components: {
    JsonCSV
  }
}
</script>
