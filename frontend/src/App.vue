<template>
  <div id="app" class="wrap">
    <div id="nav" v-if="auth">
      <b-navbar type="light" variant="light" small="true">
        <b-navbar-nav>
          <!--b-nav-item href="/phones">Телефоны</b-nav-item-->
          <router-link
            to="/phones"
            v-slot="{ href, route, navigate, isActive, isExactActive }">
            <b-nav-item :href=href @click="navigate">Телефоны</b-nav-item>
          </router-link>
          <!--router-link
            to="/cash"
            v-slot="{ href, route, navigate, isActive, isExactActive }">
            <b-nav-item :href=href @click="navigate">Начисления</b-nav-item>
          </router-link-->
          <b-nav-item-dropdown left text="Начисления">
            <!--router-link
              to="/detailMonth"
              v-slot="{ href, route, navigate, isActive, isExactActive }">
                <b-dropdown-item :href=href @click="navigate">Анализ тарифов за месяц</b-dropdown-item>
            </router-link-->
            <router-link
              to="/cashPhones"
              v-slot="{ href, route, navigate, isActive, isExactActive }">
                <b-dropdown-item :href=href @click="navigate">По телефонам (за год)</b-dropdown-item>
            </router-link>
            <router-link
              to="/cashYearDepartment"
              v-slot="{ href, route, navigate, isActive, isExactActive }">
                <b-dropdown-item :href=href @click="navigate">По подразделениям (за год)</b-dropdown-item>
            </router-link>
            <router-link
              to="/cashMonth"
              v-slot="{ href, route, navigate, isActive, isExactActive }">
                <b-dropdown-item :href=href @click="navigate">За месяц</b-dropdown-item>
            </router-link>
          </b-nav-item-dropdown>
          <router-link
            to="/import"
            v-slot="{ href, route, navigate, isActive, isExactActive }">
            <b-nav-item :href=href @click="navigate">Импорт</b-nav-item>
          </router-link>
          <!-- Navbar dropdowns -->
          <b-nav-item-dropdown text="Детализации" right>
            <!--router-link
              to="/detailMonth"
              v-slot="{ href, route, navigate, isActive, isExactActive }">
                <b-dropdown-item :href=href @click="navigate">Анализ тарифов за месяц</b-dropdown-item>
            </router-link-->
            <router-link
              to="/detailYear"
              v-slot="{ href, route, navigate, isActive, isExactActive }">
                <b-dropdown-item :href=href @click="navigate">Анализ тарифов за год</b-dropdown-item>
            </router-link>
          </b-nav-item-dropdown>
          <b-nav-item-dropdown text="История" right>
            <!--router-link
              to="/detailMonth"
              v-slot="{ href, route, navigate, isActive, isExactActive }">
                <b-dropdown-item :href=href @click="navigate">Анализ тарифов за месяц</b-dropdown-item>
            </router-link-->
            <router-link
              to="/history"
              v-slot="{ href, route, navigate, isActive, isExactActive }">
                <b-dropdown-item :href=href @click="navigate">История</b-dropdown-item>
            </router-link>
          </b-nav-item-dropdown>
          <!--router-link
            to="/login"
            v-slot="{ href, route, navigate, isActive, isExactActive }"-->
            <b-nav-item @click="logout()">Logout</b-nav-item>
          <!--/router-link-->
        </b-navbar-nav>
      </b-navbar>
    </div>
    <div v-if="auth">
      <b-breadcrumb>
        <b-breadcrumb-item :to="value.path" v-for="(value, key) in this.breadcrumbList" :value="value" :key="key">
          <img v-if="value.text == 'home'" src="@/assets/icons8-home-48.png" width="20px;">
          <a v-if="value.text !== 'home'" :href="value.path">
            {{ value.text }}
          </a>
        </b-breadcrumb-item>
      </b-breadcrumb>
    </div>
    <router-view/>
  </div>
</template>

<script>
import Vuex from 'vuex'
import { store } from './store'

export default {
  name: 'App',
  beforeEnter: store.dispatch('GlobalStore/toLoginPage'),
  data () {
    return {
      breadcrumbList: {}
    }
  },
  computed: {
    ...Vuex.mapGetters({
      auth: 'GlobalStore/getAuth'
    })
  },
  methods: {
    toLoginPage () {
      this.$store.dispatch('GlobalStore/toLoginPage')
    },
    logout () {
      // console.log(this.auth, localStorage.auth, localStorage.getItem('auth'))
      this.$store.dispatch('GlobalStore/logout')
    }
  },
  mounted () {
    this.breadcrumbList = this.$route.meta.breadcrumb
  },
  watch: {
    $route (to, from) {
      this.breadcrumbList = this.$route.meta.breadcrumb
      // console.log('bread:', this.breadcrumbList)
    }
  }
}
</script>

<style>
  .wrap {
    margin-left: auto;
    margin-right: auto;
    width: 90%;
    padding: 1px;
  }
  .max-width {
    width: 100%;
  }
  .no-pad-margin {
    margin: 0px;
    padding: 0px;
  }
  .btn-no-padding {
    padding: 0px;
  }
  .textNormal {
    color: #17a2b8 !important;
    font-weight: bold;
  }
  .textGray {
    color: gray;
    font-weight: lighter;
    font-style: oblique;
  }
  .textLightGray {
    color: lightgray;
    font-weight: lighter;
    font-style: italic;
  }
  .optimalTariff {
    color: #26b532
  }
  .ok {
    color: green
  }
  .border {
    outline: 1px;
    padding: 2%;
    margin: auto;
  }
  .right {
    float:right;
  }
</style>
