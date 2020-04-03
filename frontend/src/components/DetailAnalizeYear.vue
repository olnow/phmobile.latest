<template lang="html">
<div fluid>
  <br>
  <b-container>
  <b-form-group>
    <b-progress v-if="restLoadData" :max=100 :value="localprogress"></b-progress>
  </b-form-group>
  </b-container>
  <b-table small :items="detailanalize" :fields="fields"
           selectable selected-variant="active"
           select-mode="single"
           :tbody-tr-class="rowClass"
           >
    <template v-slot:cell(0)="fio">
      {{ fio.item['phone']['phone'] }}
      <span v-if="fio.item['phone']['people']" :title="getStateName(fio.item.phone.state)">
        {{ fio.item['phone']['people']['fio'] }}
        <!--{{ fio.item['phone']['people']['department'] }}-->
      </span>
    </template>
    <template v-slot:cell(contract)="data">
      {{ data.item.phone.contract }}
    </template>
    <template v-slot:cell(tariff)="phone">
      {{ phone.item['phone']['tariff'] }}
      <!--{{ fio.item['phone']['people']['department'] }}-->
    </template>
    <template v-slot:cell(optimalTariff)="optimalTariff">
      <span class="optimalTariff" v-if="optimalTariff.item['optimalTariff'] && optimalTariff.item['phone']['tariff'] && optimalTariff.item['phone']['tariff'] == optimalTariff.item['optimalTariff']['code'] ">
        {{ print(optimalTariff.item['optimalTariff']['name']) }}
      </span>
      <span v-if="optimalTariff.item['optimalTariff'] && optimalTariff.item['phone']['tariff'] != optimalTariff.item['optimalTariff']['code']">
        {{ print(optimalTariff.item['optimalTariff']['name']) }}
      </span>
    </template>
    <template v-slot:cell(economy)="economy">
      <span v-if="economy.item['economy']">
        {{ economy.item['economy'].toFixed() }}
      </span>
    </template>
    <!--
    "tariffDetailYears": { "currentCash": [ 353.01000000000005, 469.4100000000001, 550.73, 497.79, 370.79, 647.87, 670.48, 0, 0, 0, 0, 0 ],
    "tariffDetails": [ [ { "detailminutes": 0, "detailcost": 0, "tariff": null },
    { "detailminutes": 88, "detailcost": 0, "tariff": null },
    { "detailminutes": 156, "detailcost": 0, "tariff": null },
    { "detailminutes": 82, "detailcost": 0, "tariff": null }, { "detailminutes": 0, "detailcost": 0, "tariff": null }, { "detailminutes": 215, "detailcost": 0, "tariff": null }, { "detailminutes": 363, "detailcost": 0, "tariff": null } ], [ { "detailminutes": 0, "detailcost": 0, "tariff": null }, { "detailminutes": 88, "detailcost": 0, "tariff": null }, { "detailminutes": 156, "detailcost": 0, "tariff": null }, { "detailminutes": 82, "detailcost": 0, "tariff": null }, { "detailminutes": 0, "detailcost": 0, "tariff": null }, { "detailminutes": 215, "detailcost": 0, "tariff": null }, { "detailminutes": 363, "detailcost": 0, "tariff": null } ], [ { "detailminutes": 0, "detailcost": 0, "tariff": null }, { "detailminutes": 88, "detailcost": 0, "tariff": null }, { "detailminutes": 156, "detailcost": 0, "tariff": null }, { "detailminutes": 82, "detailcost": 0, "tariff": null }, { "detailminutes": 0, "detailcost": 0, "tariff": null }, { "detailminutes": 215, "detailcost": 0, "tariff": null }, { "detailminutes": 363, "detailcost": 0, "tariff": null } ], [ { "detailminutes": 0, "detailcost": 0, "tariff": null }, { "detailminutes": 88, "detailcost": 0, "tariff": null }, { "detailminutes": 156, "detailcost": 0, "tariff": null }, { "detailminutes": 82, "detailcost": 0, "tariff": null }, { "detailminutes": 0, "detailcost": 0, "tariff": null }, { "detailminutes": 215, "detailcost": 0, "tariff": null }, { "detailminutes": 363, "detailcost": 0, "tariff": null } ], [ { "detailminutes": 0, "detailcost": 0, "tariff": null }, { "detailminutes": 88, "detailcost": 0, "tariff": null }, { "detailminutes": 156, "detailcost": 0, "tariff": null }, { "detailminutes": 82, "detailcost": 0, "tariff": null }, { "detailminutes": 0, "detailcost": 0, "tariff": null }, { "detailminutes": 215, "detailcost": 0, "tariff": null },
    { "detailminutes": 363, "detailcost": 0, "tariff": null } ] ] }
    -->
    <template v-slot:cell(actions)="detail">
      <b-button size="sm" variant="outline-light" @click="detail.toggleDetails">
        <img v-if="!detail.detailsShowing" src="@/assets/icons8-forward-40.png" width="15px;">
        <img v-else src="@/assets/icons8-expand-arrow-40.png" width="15px;">
      </b-button>
    </template>
    <template v-slot:row-details="detail">
    <!--template v-slot:cell(3)="detail"-->
      <div>
      <h4>
        Начисления за год:
        {{ print(detail.item['currentCash']) }} руб
        <br>
      </h4>
      <b-table-simple :borderless="false" hover fixed small>
      <b-tbody v-for="(value, key) in detail.item['tariffs']" :key="value">
        <slot v-if="key == 0">
          <b-tr>
            <b-th>Tariff Name</b-th>
            <b-th v-for="(id, index) in detail.item['tariffDetailYears']['tariffDetails'][key]" :key="id">
              <p v-if="monthList[getDateIndex(index)]">
                {{ print(monthList[getDateIndex(index)].text) }}
              </p>
            </b-th>
          </b-tr>
        </slot>

        <b-tr>
        <b-th >
          {{ print(value['name']) }}
        </b-th>
        <!--b-th v-for="(data, id) in detail.item['tariffDetailYears']['tariffDetails'][key]" :key="id">
          <br>{{ printDouble(detail.item['tariffDetailYears']['currentCash'][id]) }} руб
          <br>{{ printDouble(data['detailminutes']) }} мин
          <br>{{ printDouble(data['detailcost']) }} rub
        </b-th-->
        <b-th v-for="(data, id) in detail.item['tariffDetailYears']['tariffDetails'][key]" :key="id">
          <br>{{ printDouble(detail.item['tariffDetailYears']['currentCash'][getDateIndex(id)]) }} р
          <br>{{ printDouble(detail.item.tariffDetailYears.tariffDetails[key][getDateIndex(id)].detailminutes) }} м
          <br>{{ printDouble(detail.item.tariffDetailYears.tariffDetails[key][getDateIndex(id)].detailcost) }} р
        </b-th>
        </b-tr>
      </b-tbody>
      </b-table-simple>
      </div>
    </template>

  </b-table>
  <br>
  <h4>Итого экономия за год: {{ printDouble(fulleconomy) }} рублей
  </h4>
  <br>
</div>
</template>

<script>
import Vue from 'vue'
import Vuex from 'vuex'
// import { postRestApi } from '../http-common'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import { getLastYear, formatRow, sleep } from '../utils'
// import Datepicker from 'vuejs-datepicker'

Vue.use(BootstrapVue)

export default {
  data () {
    return {
      fields: [ { key: '0', label: 'FIO', sortable: true },
        { key: 'contract', label: 'Договор', sortable: true },
        { key: 'tariff', label: 'Тариф', sortable: true },
        { key: 'optimalTariff', label: 'Опт. тариф', sortable: true },
        { key: 'economy', label: 'Экономия', sortable: true },
        { key: 'actions', label: '' }],
      monthList: [
        { value: 0, text: 'Jan' },
        { value: 1, text: 'Feb' },
        { value: 2, text: 'March' },
        { value: 3, text: 'April' },
        { value: 4, text: 'May' },
        { value: 5, text: 'June' },
        { value: 6, text: 'July' },
        { value: 7, text: 'August' },
        { value: 8, text: 'September' },
        { value: 9, text: 'October' },
        { value: 10, text: 'November' },
        { value: 11, text: 'Decemver' }
      ],
      selectedyear: getLastYear(),
      monthSelected: 3,
      updateProgress: -1,
      updateActive: false,
      oldprogress: 0
    }
  },
  mixins: [formatRow],
  computed: {
    ...Vuex.mapGetters({
      detailanalize: 'DetailStore/getDetailAnalizeYear'
    }),
    ...Vuex.mapGetters({
      restLoadData: 'GlobalStore/getRestLoadData',
      progress: 'GlobalStore/getProgress'
    }),
    fulleconomy () {
      if (this.detailanalize) {
        return this.countFullEcomony()
      }
      return 0
    },
    localprogress () {
      // console.log('computed progress', this.restLoadData, this.progress)
      if (this.restLoadData) {
        // console.log('computed update progress')
        this.loadProgress()
        sleep(1000)
        return this.progress
      }
      return 0
    }
  },
  mounted: async function () {
    if (!this.detailanalize || !this.detailanalize.length) {
      this.loadDetailAnalize(this.selectedyear)
    }
  },
  watch: {
  },
  methods: {
    formatDate (date) {
      let dt = new Date(date)
      let options = { year: 'numeric', month: 'long' }
      return dt.toLocaleDateString('ru-RU', options)
    },
    getDateIndex (index) {
      let dt = new Date()
      let month = dt.getMonth() + index
      if (month > 11) month -= 12
      return month
    },
    countFullEcomony () {
      let economy = 0
      // console.log(this.detailanalize)
      if (this.detailanalize && this.detailanalize.length) {
        this.detailanalize.forEach(function (value, index, array) {
          // this.fulleconomy = this.fulleconomy + value.economi
          // console.log(value, index, array)
          economy += value.economy
        })
      }
      return economy
    },
    rowClass (item, type) {
      // return formatRow.getRowClass(item.state, item.people)
      return this.getRowClass(item.phone.state, item.phone.people)
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
    async loadDetailAnalize (year) {
      this.updateActive = true
      // console.log('progress: ', this.updateProgress)
      this.updateProgress = 0
      // console.log('progress: ', this.updateProgress)
      let formData = new FormData()
      formData.append('year', year)
      // console.log('getDetailAnalizeYear')
      // let progress = { 'progress': val }
      // let data = await AXIOS.post('analizePhoneDetailYear', { progress })
      // let data = postRestApi('get', formData)
      this.$store.dispatch('DetailStore/getDetailAnalizeYear', formData)
      // this.$store.dispatch('GlobalStore/setRestLoadData', false)
      // console.log('detail: ', this.detailanalize)
      // console.log(data)
      // this.detailanalize = data.data
      // console.log(this.detailanalize)
      this.updateActive = false
      this.updateProgress = -1
    },
    loadProgress () {
      this.$store.dispatch('GlobalStore/updateProgress', 'getDetailAnalizeProgress', this.progress)
    }
  }
}
</script>
