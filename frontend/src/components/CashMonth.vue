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
  </b-navbar>
  <p/>
  <b-table :items="cashMonth" :fields="fields" :small="true" :sort-by="sortBy" :sort-desc="true" selectable selected-variant="active" select-mode="single">
    <template v-slot:cell(3)="data">
      <b class="text-info" >{{ data.value.toFixed(2) }}</b>
    </template>
  </b-table>
</div>
</template>

<script>
import Vue from 'vue'
import Vuex from 'vuex'
import { postRestApi } from '../http-common'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import JsonCSV from 'vue-json-csv'

Vue.use(BootstrapVue)

export default {
  data () {
    return {
      fields: [ { key: '0', label: 'Телефон', sortable: true },
        { key: '1', label: 'ФИО', sortable: true },
        { key: '2', label: 'Подразделение', sortable: true },
        { key: '3', label: 'Сумма, руб \n(без налогов)', sortable: true } ],
      csvlabels: {
        'phone': 'Телефон',
        'fio': 'ФИО',
        'department': 'Подразделение',
        'sum': 'Сумма, руб (без налогов)'
      },
      posts: [],
      errors: [],
      selectedyear: this.getLastYear(),
      selectedmonth: this.getLastMonth(),
      exportCashMonth: [],
      exportTemplate: 'phone,fio,department,sum',
      exportTypes: { 'sum': 'double' },
      sortBy: '3',
      exportSkipZero: true
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
      cashMonth: 'CashStore/getCashMonth'
    }),
    years () {
      const year = new Date().getFullYear()
      return Array.from({ length: 4 }, (value, index) => year - 3 + index)
    },
    months () {
      // const month = 12
      return Array.from({ length: 12 }, (value, index) => 1 + index)
    }
  },
  mounted () {
    // console.log('mounted phonelist: ', this.$store.getters['PhoneListStore/getPhoneList'])
    if (!this.cashMonth.length) {
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
      let formData = new FormData()
      formData.append('year', this.selectedyear)
      formData.append('month', this.selectedmonth)
      this.$store.dispatch('CashStore/loadCashMonth', formData)
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
    }
  },
  components: {
    JsonCSV
  }
}
</script>
