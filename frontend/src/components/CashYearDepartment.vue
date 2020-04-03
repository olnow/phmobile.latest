<template lang="html">
<div>
  <!-- b-btn @click="callRestGet('getCashYearDepartment')">Сформировать отчет</b-btn-->
  <b-input-group size="sm">
    <b-form-select
      size="sm"
      v-model="selectedyear"
      :options="years"
      @change="getCashYearDepartment()">
    </b-form-select>
    <JsonCSV
      :data = "cashDepartment"
      delimiter = ";"
      name = "cashDeparment.csv"
      :bom = true
      :labels = "csvLabels"
    >
      <img src="@/assets/icons8-export-csv-30.png">
    </JsonCSV>
  </b-input-group>
  <p/>
              <b-table :items="cashDepartment" :fields="fields" :small="true" :sort-by="1" :sort-desc="true" selectable selected-variant="active" select-mode="single">
                <!-- A custom formatted column -->
                <template v-slot:cell(1)="data">
                  <b class="text-info" >{{ data.value.toFixed(2) }}</b>
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
import JsonCSV from 'vue-json-csv'

Vue.use(BootstrapVue)

export default {
  data () {
    return {
      fields: [ { key: '0', label: 'Подразделение', sortable: true },
        { key: '1', label: 'Сумма, руб (год, без налогов)', sortable: true } ],
      csvLabels: {
        0: 'Подразделение',
        1: 'Сумма'
      },
      posts: [],
      errors: [],
      selectedyear: this.getLastYear()
    }
  },
  computed: {
    ...Vuex.mapGetters({
      cashDepartment: 'CashStore/getCashYearDepart'
    }),
    years () {
      const year = new Date().getFullYear()
      return Array.from({ length: 4 }, (value, index) => year - 3 + index)
    }
  },
  mounted () {
    this.getCashYearDepartment()
  },
  components: {
    JsonCSV
  },
  methods: {
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
    getCashYearDepartment () {
      let formData = new FormData()
      formData.append('year', this.selectedyear)
      this.$store.dispatch('CashStore/loadCashYearDepart', formData)
    }
    // Fetches posts when the component is created.
  }
}
</script>
