<template>
<div id="CashMonthView" class="container wrap">
  <br>
  <CashMonth></CashMonth>
  <div v-if="cashyearerrors.length" id="error">
    <h3>{{ cashyearerrors }}</h3>
  </div>
</div>
</template>

<script>
import CashMonth from '../components/CashMonth'

export default {
  name: 'cash',
  components: { CashMonth },
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
