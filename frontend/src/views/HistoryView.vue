<template>
<div id="HistoryView" class="container p-0">
  <HistoryCashAnalize></HistoryCashAnalize>
<div v-if="cashyearerrors.length" id="error">
<h3>{{ cashyearerrors }}</h3>
</div>
</div>
</template>

<script>
import Vue from 'vue'
import { AXIOS } from '../http-common'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
// import CashYear from '../components/CashYear'
import HistoryCashAnalize from '../components/HistoryCashAnalize'

Vue.use(BootstrapVue)

export default {
  name: 'history',
  components: { HistoryCashAnalize },
  data () {
    return {
      fields: [ { key: '0', label: 'Phone', sortable: true },
        { key: '1', label: 'FIO', sortable: true },
        { key: '2', label: 'Department', sortable: true },
        { key: '3', label: 'SUM (year)', sortable: true } ],
      cashyear: [],
      cashyearerrors: []
    }
  },
  methods: {
    // Fetches posts when the component is created.
  }
}
</script>
