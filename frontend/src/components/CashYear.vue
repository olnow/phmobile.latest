<template lang="html">
<div>
  <b-navbar size="sm">
    <b-nav-form>
  <b-form-select
    size="sm"
    v-model="selectedyear"
    :options="years"
    @change="selectYear()">
  </b-form-select>
    </b-nav-form>
  <b-btn size="sm" @click="callRestGet('getCashYear')">Сформировать отчет</b-btn>
  <JsonCSV
                        :data = "phoneList"
                        delimiter = ";"
                        name = "phoneList.csv"
                        :bom = true
                      >
                          <img src="@/assets/icons8-export-csv-30.png">
  </JsonCSV>
  </b-navbar>
  <p/>
              <b-table :items="posts" :fields="fields" :small="true" :sort-by="3" :sort-desc="true" selectable selected-variant="active" select-mode="single">
                <!-- A custom formatted column -->
                <template v-slot:cell(3)="data">
                  <b class="text-info" >{{ data.value.toFixed(2) }}</b>
                </template>
              </b-table>
</div>
</template>

<script>
import Vue from 'vue'
import Vuex from 'vuex'
import { AXIOS, postRestApi } from '../http-common'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import JsonCSV from 'vue-json-csv'

Vue.use(BootstrapVue)

export default {
  data () {
    return {
      fields: [ { key: '0', label: 'Phone', sortable: true },
        { key: '1', label: 'FIO', sortable: true },
        { key: '2', label: 'Department', sortable: true },
        { key: '3', label: 'SUM (year)', sortable: true } ],
      posts: [],
      errors: [],
      selectedyear: this.getLastYear()
    }
  },
  computed: {
    years () {
      const year = new Date().getFullYear()
      return Array.from({ length: 4 }, (value, index) => year - 3 + index)
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
    selectYear () {
      this.$store.dispatch('CashStore/loadPhonesCash', this.selectedyear)
    }
  },
  components: {
    JsonCSV
  }
}
</script>
