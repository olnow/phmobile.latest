<template>
<!-- FILE NOT USED -->
<div id="root" class="container wrap">
<div class="h-25 d-inline-block">
  <b-card no-body>
    <b-tabs pills card horizontal fill>
      <b-tab title="По телефонам" active>
        <b-card-text>
        </b-card-text>
        <CashPhones></CashPhones>
      </b-tab>
      <!--b-tab title="За год">
        <b-card-text>Начисления за год</b-card-text>
        <CashYear></CashYear>
      </b-tab-->
      <b-tab title="За год по подразделениям">
        <CashYearDepartment></CashYearDepartment>
      </b-tab>
      <b-tab title="За месяц">
        <b-card-text>Начисления за месяц</b-card-text>
        <CashMonth></CashMonth>
      </b-tab>
    </b-tabs>
  </b-card>
</div>
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
import CashYearDepartment from '../components/CashYearDepartment'
import CashPhones from '../components/CashPhones'
import CashMonth from '../components/CashMonth'

Vue.use(BootstrapVue)

export default {
  name: 'cash',
  components: { CashYearDepartment, CashPhones, CashMonth },
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
    callRestGet (request) {
      AXIOS.post(request)
        .then(response => {
        // JSON responses are automatically parsed.
          this.cashyear = response.data
        })
        .catch(e => {
          this.cashyearerrors.push(e)
        })
    }
  }
}
</script>
