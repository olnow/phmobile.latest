<template>
  <div class="login-page">
        <form class="form-signin" method="post" @submit.prevent="login()">
          <h2 class="form-signin-heading">Please sign in</h2>
          <p>
            <label for="username" class="sr-only">Username</label>
            <input type="text" size="50" v-model="username" id="username" name="username" class="form-control" placeholder="Username" required autofocus>
          </p>
          <p>
            <label for="password" class="sr-only">Password</label>
            <input type="password" size="50" id="password" v-model="password" name="password" class="form-control" placeholder="Password" required>
          </p>
          <button class="btn btn-lg btn-primary btn-block" size="50">Sign in</button>
        </form>
  </div>
</template>

<script>
// import Vue from 'vue'
// import { postRestApi } from '../http-common'

export default {
  name: 'login',
  data () {
    return {
      username: '',
      password: ''
    }
  },
  methods: {
    login (e) {
      let obj = this
      // console.log('login', obj.username, obj.password)
      // let data = { 'username': username, 'password': password }
      let form = new FormData()
      form.append('username', obj.username)
      form.append('password', obj.password)
      // console.log('formdata: ', form)
      // postRestApi('login', form)
      // console.log(JSON.stringify(process.env))
      this.$store.dispatch('GlobalStore/AUTH_REQUEST', form)
    }
  }
}
</script>

<style>
.login-page {
  width: 360px;
  padding: 8% 0 0;
  margin: auto;
}
</style>
