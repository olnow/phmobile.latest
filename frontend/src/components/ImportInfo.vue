<template lang="html">
<div fluid>
  <b-table small :items="importList" :fields="fields">
    <template v-slot:cell(1)="date">
      {{ formatDate(date.item[1]) }}
    </template>
    <template v-slot:cell(3)="sum">
      {{ sum.item[3].toFixed(2) }}
    </template>

  </b-table>
</div>
</template>

<script>
import Vue from 'vue'
import Vuex from 'vuex'
// import { AXIOS } from '../http-common'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
// import Datepicker from 'vuejs-datepicker'

Vue.use(BootstrapVue)

export default {
  data () {
    return {
      fields: [ { key: '0', label: 'Contract', sortable: true },
        { key: '1', label: 'Date', sortable: true },
        { key: '2', label: 'Count', sortable: true },
        { key: '3', label: 'SUM', sortable: true } ]
      // isLoadedOptions: false
    }
  },
  computed: {
    ...Vuex.mapGetters({
      importList: 'ImportStore/getContractCashDetail',
      CSRF: 'GlobalStore/getCSRF'
    }),
    CSRFValue: function () {
      // console.log('computed ImportInfo CSRF: ', this.CSRF)
      if (this.CSRF && this.CSRF.length) {
        return this.CSRF
      }
      //   this.isLoadedOptions = true
      // }
      return ''
    },
    isLoadedOptions: function () {
      if (this.CSRFValue && this.CSRFValue.length) {
        // console.log('Loaded true, ', this.CSRFValue)
        return true
      }
      return false
    }
  },
  mounted () {
    // console.log('mounted import: ', this.phoneList)
    // await thi
    // this.CSRFValue()
    if (!this.importList || !this.importList.length) {
      this.$store.dispatch('ImportStore/loadImportStatus')
    }
    // console.log('mounted import: ', this.phoneList)
  },
  watch: {
    isLoadedOptions: function (val) {
      if (val && val === true) {
        if (!this.importList || !this.importList.length) {
          this.$store.dispatch('ImportStore/loadImportStatus')
        }
      }
    }
  },
  methods: {
    formatDate (date) {
      let dt = new Date(date)
      let options = { year: 'numeric', month: 'long' }
      return dt.toLocaleDateString('ru-RU', options)
    }
  }
}
</script>
