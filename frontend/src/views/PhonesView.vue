<template>
<div id="root" class="container p-0">
  <b-container class="bv-row-1" fluid>
    <b-row>
      <b-col>
        <PhonesList></PhonesList>
      </b-col>
    </b-row>
  </b-container>
<div id="error">
<h3>{{ errors }}</h3>
</div>
</div>
</template>

<script>
import Vue from 'vue'
import { AXIOS } from '../http-common'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import PhonesList from '../components/PhonesList'

Vue.use(BootstrapVue)

export default {
  name: 'phones',
  components: { PhonesList },
  data () {
    return {
      loading: true,
      fields: [ 'phone',
        { key: 'people', label: 'People' },
        { key: 'department', label: 'Department' } ],
      posts: [],
      errors: []
    }
  },
  methods: {
    // Fetches posts when the component is created.
    callRestGet (request) {
      this.loading = true
      AXIOS.post(request)
        .then(response => {
          // JSON responses are automatically parsed.
          this.posts = response.data
        })
        .catch(e => {
          this.errors.push(e)
        })
      this.loading = false
    }
  }
}
</script>
