<template lang="html">
<div fluid>
  <b-table small :items="importHistory" :fields="fields">
    <template v-slot:cell(importDate)="importDate">
      {{ formatDate(importDate.item['importDate']) }}
    </template>
    <template v-slot:cell(month)="data">
      {{ formatMonth(data.item['month']) }}
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

Vue.use(BootstrapVue)

export default {
  data () {
    return {
      fields: [ { key: 'fileName', label: 'Имя файла', sortable: true },
        { key: 'importDate', label: 'Дата импорта', sortable: true },
        { key: 'recordCount', label: 'Записей в файле', sortable: true },
        { key: 'recordWrite', label: 'Записанно записей', sortable: true },
        { key: 'month', label: 'Месяц', sortable: true } ]
      // isLoadedOptions: false
    }
  },
  props: {
    importType: Number,
    year: Number
  },
  computed: {
    ...Vuex.mapGetters({
      importHistory: 'ImportStore/getImportHistory',
      importLastUploadDate: 'ImportStore/getLastUploadDate'
    })
  },
  mounted () {
    // console.log('mounted import: ', this.phoneList)
    // await thi
    // this.CSRFValue()
    if (!this.importHistory || !this.importHistory.length) {
      this.loadData()
    }
    // console.log('mounted import: ', this.phoneList)
  },
  watch: {
    isLoadedOptions: function (val) {
      if (val && val === true) {
        if (!this.importHistory || !this.importHistory.length) {
          this.loadData()
        }
      }
    },
    year: function () {
      this.loadData()
    },
    importType: function () {
      this.loadData()
    }
  },
  methods: {
    formatDate (date) {
      let dt = new Date(date)
      let options = { year: 'numeric', month: 'long' }
      return dt.toLocaleDateString('ru-RU', options)
    },
    formatMonth (date) {
      let dt = new Date(date)
      let options = { month: 'long' }
      return dt.toLocaleDateString('ru-RU', options)
    },
    loadData () {
      let formData = new FormData()
      formData.append('year', this.year)
      // formData.append('month', this.selectedmonth)
      formData.append('type', this.importType)
      // console.log(this.year, this.importType)
      this.$store.dispatch('ImportStore/loadImportHistory', formData)
    }
  }
}
</script>
