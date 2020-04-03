<template>
  <div>
    <!--b-navbar small type="light" variant="light" small="true">
      <b-navbar-nav>
        <b-nav-item-dropdown text="Начисления">
          <b-dropdown-item>Начисления (.csv)</b-dropdown-item>
          <b-dropdown-item>Начисления (.dbf)</b-dropdown-item>
        </b-nav-item-dropdown>
      </b-navbar-nav>
    </b-navbar>
    <b-card no-body>
      <b-tabs pills card horizontal fill>
        <b-tab title="csv" active>
          <b-card-text>
          </b-card-text>
        </b-tab>
        <b-tab title="dbf">
          <b-card-text>Начисления за год</b-card-text>
        </b-tab>
        <b-tab title="За год по подразделениям">
        </b-tab>
        <b-tab title="За месяц">
          <b-card-text>Tab Contents 3
          </b-card-text>
        </b-tab>
      </b-tabs>
    </b-card-->
    <b-container>
    <b-row>
      <b-col>
    <div class="align-center">
    <b-form-group class="no-margin-bottom">
      <b-form-radio-group
        v-model="importTypeSelected"
        button-variant="outline-secondary"
        :options="importTypes"
        name="importTypes"
        buttons>
      </b-form-radio-group>
      <!--b-form-select sm :options="importTypes" v-model="importTypeSelected"></b-form-select-->
      <!--b-form-select sm :options="importFileTypes" v-model="importFileTypeSelected"></b-form-select-->
      <b-form-select v-model="selectedyear" :options="years">
      </b-form-select>
    </b-form-group>
    </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <ImportComponent
          :year="selectedyear"
          :importType="importTypeNumeric"
          :importTypeSelected="importTypeSelected">
        </ImportComponent>
      </b-col>
    </b-row>
    </b-container>
    <ImportHistory :year="selectedyear" :importType="importTypeNumeric"></ImportHistory>
    <ImportInfo></ImportInfo>
  </div>
</template>

<script>
import ImportInfo from '../components/ImportInfo'
import ImportComponent from '../components/ImportComponent'
import ImportHistory from '../components/ImportHistory'
import { getLastYear } from '../utils'

export default {
  name: 'ImportView',
  components: { ImportInfo, ImportComponent, ImportHistory },
  data () {
    return {
      importTypeSelected: 'cash',
      importTypes: [
        { value: 'cash', text: 'Начисления' },
        { value: 'detail', text: 'Детализация' },
        { value: 'phonestariff', text: 'Информация о тарифах' },
        { value: 'checkExcludedPhones', text: 'Сверка переоформленных' },
        { value: 'services', text: 'Информация о сервисах' }
      ],
      selectedyear: getLastYear()
    }
  },
  computed: {
    years () {
      const year = new Date().getFullYear()
      return Array.from({ length: 4 }, (value, index) => year - 3 + index)
    },
    importTypeNumeric: function () {
      switch (this.importTypeSelected) {
        case 'cash':
          return 1
        case 'detail':
          return 2
        case 'phonestariff':
          return 3
        case 'services':
          return 4
        case 'checkExcludedPhones':
          return 5
        default:
          return 0
      }
    }
  }
}
</script>

<style>
.align-center {
  margin: 0 auto;
  width: 70%;
}
.no-margin-bottom {
  margin-bottom: 0px;
}
</style>
