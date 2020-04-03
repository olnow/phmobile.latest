<template lang="html">
<div fluid>
  <br>

  <b-container>
  <b-form-group>
    <b-progress v-if="updateActive" :max=100 :value="updateProgress"></b-progress>
    <b-form-select :options="monthList" v-model="monthSelected" @change="loadDetailAnalize(monthSelected)">
    </b-form-select>
  </b-form-group>
  </b-container>
  <b-table small :items="detailanalize" :fields="fields"
           selectable selected-variant="active"
           select-mode="single"
           >
    <template v-slot:cell(0)="fio">
      {{ fio.item['phone']['phone'] }}
      <span v-if="fio.item['phone']['people']">
        {{ fio.item['phone']['people']['fio'] }}
        <!--{{ fio.item['phone']['people']['department'] }}-->
      </span>
    </template>
    <template v-slot:cell(optimalTariff)="optimalTariff">
      <span v-if="optimalTariff.item['optimalTariff']">
      {{ optimalTariff.item['optimalTariff']['tariff']['name'] }}
      </span>
    </template>
    <template v-slot:cell(economy)="economy">
      {{ economy.item['economi'].toFixed() }}
    </template>
    <!--{ "detailminutes": 816101, "detailcost": 5.08,
    "tariff": { "idtariff": 1, "name": "Для бизнеса гос 300",
    "minutes": 400, "sms": 500, "internet": 6, "cost": 300,
    "minutescost": 0, "smscost": 0, "internetcost": 0 } },
    { "detailminutes": 816101, "detailcost": 5.08, "tariff": null } ]

    { "idtariff": 1, "name": "Для бизнеса гос 300",
    "minutes": 400, "sms": 500, "internet": 6,
    "cost": 300, "minutescost": 0, "smscost": 0, "internetcost": 0 }
    -->

    <template v-slot:cell(actions)="detail">
      <b-button size="sm" @click="detail.toggleDetails">
        {{ detail.detailsShowing ? 'Hide' : 'Show' }} Details
      </b-button>
    </template>
    <template v-slot:row-details="detail">
    <!--template v-slot:cell(3)="detail"-->
      <b-container>
      <span v-for="(key, value) in detail.item['tariffDetails']" :key="value" :value="key">
        <b-row>
        <b-col>
          {{ key['detailminutes'].toFixed(2) }} min
        </b-col>
        <b-col>
          {{ key['detailcost'].toFixed(2) }} rub
        </b-col>
        <b-col>
          {{ key['tariff']['name'] }}
          (m:{{ key['tariff']['minutes'] }} -
           p:{{ key['tariff']['cost'] }})
        </b-col>
        </b-row>
      </span>
      </b-container>
    </template>

  </b-table>
  <br>
  {{ fulleconomy.toFixed() }}
  <br>
</div>
</template>

<script>
import Vue from 'vue'
import Vuex from 'vuex'
import { AXIOS } from '../http-common'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
// import Datepicker from 'vuejs-datepicker'

Vue.use(BootstrapVue)

export default {
  data () {
    return {
      fields: [ { key: '0', label: 'FIO', sortable: true },
        { key: 'currentCash', label: 'CurrentCash', sortable: true },
        { key: 'optimalTariff', label: 'optimal', sortable: true },
        { key: 'economy', label: 'Economy', sortable: true },
        { key: '3', label: 'Detail', sortable: true },
        { key: 'actions', label: 'Actions' }],
      detailanalize: [],
      monthList: [
        { value: 1, text: 'Jan' },
        { value: 2, text: 'Feb' },
        { value: 3, text: 'March' },
        { value: 4, text: 'April' },
        { value: 5, text: 'May' },
        { value: 6, text: 'June' },
        { value: 7, text: 'July' },
        { value: 8, text: 'August' },
        { value: 9, text: 'September' },
        { value: 10, text: 'October' },
        { value: 11, text: 'November' },
        { value: 12, text: 'Decemver' }
      ],
      monthSelected: 3,
      updateProgress: -1,
      updateActive: false
    }
  },
  computed: {
    ...Vuex.mapGetters({
      importList: 'ImportStore/getContractCashDetail'
    }),
    fulleconomy () {
      if (this.detailanalize) {
        return this.countFullEcomony()
      }
      return 0
    }
  },
  async mounted () {
    if (!this.detailanalize || !this.detailanalize.length) {
      this.loadDetailAnalize(this.monthSelected)
    }
  },
  methods: {
    formatDate (date) {
      let dt = new Date(date)
      let options = { year: 'numeric', month: 'long' }
      return dt.toLocaleDateString('ru-RU', options)
    },
    countFullEcomony () {
      let economy = 0
      this.detailanalize.forEach(function (value, index, array) {
        // this.fulleconomy = this.fulleconomy + value.economi
        economy += value.economi
      })
      return economy
    },
    async loadDetailAnalize (val) {
      this.updateActive = true
      this.updateProgress = 0
      console.log('loadDetailAnalize')
      let progress = { 'progress': val }
      let data = await AXIOS.post('analizePhoneDetailTariffsMonth', { progress })
      // console.log(data)
      this.detailanalize = data.data
      console.log(this.detailanalize)
      this.updateActive = false
      this.updateProgress = -1
    }
  },
  watch: {
    updateProgress: async function (progress) {
      if (this.updateActive) {
        // console.log('progress: ', progress)
        progress = { 'progress': progress }
        const self = this
        await AXIOS.post('getDetailAnalizeProgress', { progress })
          .then(function (response) {
            let value = response.data['progress']
            if (value && value > self.updateProgress) {
              self.updateProgress = value
            }
          })
      }
    }
  }
}
</script>
