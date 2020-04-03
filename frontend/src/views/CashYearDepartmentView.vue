<template>
<div id="CashYearDepartment" class="container wrap">
  <br>
  <CashYearDepartment></CashYearDepartment>
  <div v-if="cashyearerrors.length" id="error">
    <h3>{{ cashyearerrors }}</h3>
  </div>
</div>
</template>

<script>
import CashYearDepartment from '../components/CashYearDepartment'

export default {
  name: 'cash',
  components: { CashYearDepartment },
  data () {
    return {
      cashyear: [],
      cashyearerrors: []
    }
  },
  methods: {
  }
}
</script>
