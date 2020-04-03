<template>
  <div>
    <div class="border">
      <!--b-form v.on:submit.prevent="findPeople()"-->
      <b-input-group>
        <div>
          <label>ФИО: </label>
        </div>
        <div>
          <b-form-input v-on:keyup.enter="findPeople()" size="sm" placeholder="ФИО" v-model="fio">
          </b-form-input>
        </div>
        <b-form-append size="sm">
          <b-button size="sm" @click="findPeople()">
            Поиск
          </b-button>
            <b-checkbox v-model="findLocalPeople">
              Поиск в локальной базе
            </b-checkbox>
        </b-form-append>
        <b-table
          :items="people"
          :fields="fields"
          selected-variant="active"
          selectable
          select-mode="single"
          :tbody-tr-class="rowClass"
          @row-selected="setSelectedPeople"
          >
          <template v-slot:cell(fio)="row">
          <div>{{ row.item.account }}</div>
          <div>{{ row.item.fio}}</div>
          <div v-if="row.item.department">{{ row.item.department}}</div>
          <div v-if="row.item.department">{{ row.item.position }}</div>
          </template>
        </b-table>
        <b-button class="right" @click="selectPeople" size="sm">
          Выбрать
        </b-button>
      </b-input-group>
      <!--/b-form-->
    </div>
        <!--/b-col>
    </b-row>
  </b-container-->
  </div>
</template>

<script>
// import { postRestApi } from '../http-common'
// import Datepicker from 'vuejs-datepicker'

export default {
  name: 'FindPeople',
  components: {
    // Datepicker
  },
  data () {
    return {
      fio: '',
      people: {},
      selectedPeople: {},
      fields: [
        { key: 'fio', label: '' }
        // { key: 'account', label: '', sortable: true },
        // { key: 'department', label: '', sortable: true },
        // { key: 'position', label: '', sortable: true },
        // { key: 'adstate', label: '', sortable: false },
        // { key: 'active', label: '', sortable: false },
        // { key: 'people', label: '', sortable: false }
      ],
      findLocalPeople: false
    }
  },
  props: {
    // importType: Number,
    // year: Number,
    // importTypeSelected: String
  },
  computed: {
  },
  mounted () {
    // if (this.selectedyear === 0) {
    //   const year = new Date().getFullYear()
    //  this.selectedyear = year
    // }
  },
  watch: {
  },
  methods: {
    async findPeople () {
      this.people = []
      this.selectedPeople = {}
      let formData = new FormData()
      formData.append('fio', this.fio)
      // console.log(this.fio)
      let res
      if (this.findLocalPeople) {
        res = await this.$store.dispatch('PhoneListStore/findPeopleLocal', formData)
      } else {
        res = await this.$store.dispatch('PhoneListStore/findPeopleAD', formData)
      }
      if (res && res.data) this.people = res.data
      // console.log(this.people)
    },
    selectPeople () {
      this.$store.dispatch('PhoneListStore/selectPeople', this.selectedPeople)
    },
    setSelectedPeople (data) {
      this.selectedPeople = data[0]
      // console.log(data[0])
    },
    rowClass (item, type) {
      // console.log(item, type)
      if (item && item.people && item.people > 0) {
        return 'table-secondary'
      }
      return ''
    }
  }

}
</script>

<style>
.redText {
  color: red;
}
</style>
