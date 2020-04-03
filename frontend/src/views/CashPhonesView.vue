<template>
<div id="CashPhonesView" class="container wrap">
  <CashPhones></CashPhones>
<div v-if="cashyearerrors.length" id="error">
<h3>{{ cashyearerrors }}</h3>
</div>
</div>
</template>

<script>
import CashPhones from '../components/CashPhones'

export default {
  name: 'cashPhonesView',
  components: { CashPhones },
  data () {
    return {
      cashyear: [],
      cashyearerrors: []
    }
  }
}
</script>
