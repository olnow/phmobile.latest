<template>
  <div class="no-padding">
  <!--b-container>
    <b-row>
      <b-col ALIGN="right"-->
        <b-progress class="align-center" :max=100 :value="fileProgress"></b-progress>
      <!--/b-col>
    </b-row>
    <b-row>
      <b-col-->
  <b-form-group class="align-center">
      <!--b-form-radio-group
        v-model="importTypeSelected"
        button-variant="outline-secondary"
        :options="importTypes"
        name="importTypes"
        buttons
        @change="selectImportType()">
      </b-form-radio-group-->
      <!--b-form-select sm :options="importTypes" v-model="importTypeSelected"></b-form-select-->
      <!--b-form-select sm :options="importFileTypes" v-model="importFileTypeSelected"></b-form-select-->
      <!--span v-if="importTypeSelected == 'cash'">
       <b-form-select v-model="selectedyear" :options="years">
       </b-form-select>
      </span-->
      <!--span v-if="importTypeSelected == 'detail' || importTypeSelected == 'phonestariff'"-->
       <b-form-select v-model="selectedcharset" :options="charsets">
       </b-form-select>
      <!--/span-->
      <b-form-file
        v-model="file"
        :state="Boolean(file)"
        :class="redText"
        placeholder="Choose a file or drop it here..."
        drop-placeholder="Drop file here..."
      ></b-form-file>
      <div class="mt-3" :class="redText">
        Selected file: {{ file ? file.name : '' }}
        <span v-if="fileAlreadyUpload">
           Already Uploaded!
        </span>
      </div>
      <b-button @click="fileUpload()">Upload</b-button>
      <b-button @click="test()">Test Upload</b-button>
  </b-form-group>
      <!--/b-col>
    </b-row>
  </b-container-->
  </div>
</template>

<script>
import { postRestApi } from '../http-common'
import { getLastYear } from '../utils'
// import Datepicker from 'vuejs-datepicker'

export default {
  name: 'ImportComponent',
  components: {
    // Datepicker
  },
  data () {
    return {
      file: null,
      fileUploadActive: false,
      fileProgress: -1,
      fileUploadProgress: 0,
      importFileTypeSelected: ['csv'],
      importFileTypes: [
        { value: 'csv', text: '.csv' },
        { value: 'dbf', text: '.dbf' }
      ],
      // importTypeSelected: 'cash',
      importTypes: [
        { value: 'cash', text: 'Начисления' },
        { value: 'detail', text: 'Детализация' },
        { value: 'phonestariff', text: 'Информация о тарифах' },
        { value: 'services', text: 'Информация о услугах' }
      ],
      selectedyear: getLastYear(),
      charsets: [
        { value: 'windows-1251', text: 'WINDOWS-1251' },
        { value: 'utf-8', text: 'UTF-8' }
      ],
      selectedcharset: 'windows-1251',
      fileAlreadyUpload: false
    }
  },
  props: {
    importType: Number,
    year: Number,
    importTypeSelected: String
  },
  computed: {
    years () {
      const year = new Date().getFullYear()
      return Array.from({ length: 4 }, (value, index) => year - 3 + index)
    },
    redText: function () {
      if (this.file) {
        this.checkFileName()
        console.log('redText file', this.file.name, this.year, this.fileAlreadyUpload)
      }
      if (this.fileAlreadyUpload === true) {
        console.log('redText', this.fileAlreadyUpload)
        return 'redText'
      } else {
        return ''
      }
    }
  },
  mounted () {
    // if (this.selectedyear === 0) {
    //   const year = new Date().getFullYear()
    //  this.selectedyear = year
    // }
  },
  watch: {
    fileProgress: async function (oldprogress) {
      if (this.fileUploadActive) {
        // console.log('progress: ', oldprogress)
        let progress = { 'progress': oldprogress }
        const self = this
        await postRestApi('getFileUploadProgress', { progress })
          .then(function (response) {
            let value = response.data['progress']
            // console.log('progress res: ', value)
            if (value && value > oldprogress) {
              self.fileProgress = value
              // console.log('update progress: ', self.fileProgress)
            }
          })
      }
    }
  },
  methods: {
    async fileUpload () {
      // console.log(this.importTypeSelected[0], this.importFileTypeSelected[0])
      let cmd = ''
      switch (this.importTypeSelected) {
        case 'cash':
          cmd = 'fileUploadPhoneCash'
          break
        case 'detail':
          cmd = 'fileUploadPhoneDetail'
          break
        case 'phonestariff':
          cmd = 'loadPhonesTariff'
          break
        case 'services':
          cmd = 'loadServices'
          break
        case 'checkExcludedPhones':
          cmd = 'checkExcludedPhones'
          break
        default:
          return
      }
      this.fileUploadActive = true
      this.fileProgress = 0
      let formDate = new FormData()
      formDate.append('charset', this.selectedcharset)
      formDate.append('year', this.selectedyear)
      formDate.append('file', this.file)
      await postRestApi(cmd, formDate
      ).then(function () {
        // console.log('file upload: OK')
        // this.loadCashDetail()
      })
        .catch(function (e) {
          console.log('file upload error: ', e)
        })
      this.fileUploadActive = false
      this.fileProgress = -1
      this.loadCashDetail()
      this.$store.dispatch('ImportStore/updateLastUploadDate')
    },
    async test () {
      let formDate = new FormData()
      formDate.append('year', this.selectedyear)
      formDate.append('file', this.file)
      await postRestApi('test', formDate, {
        headers: {
          'Content-Type': 'multipart/form-data;charset=windows-1251'
        }
      }).then(function () {
        // console.log('file upload: OK')
        // this.loadCashDetail()
      })
        .catch(function (e) {
          console.log('file upload error: ', e)
        })
    },
    async loadCashDetail () {
      this.$store.dispatch('ImportStore/loadImportStatus')
    },
    selectImportType () {
      switch (this.importTypeSelected) {
        case 'cash':
          this.importType = 1
          break
        case 'detail':
          this.importType = 2
          break
        case 'phonestariff':
          this.importType = 3
          break
        default:
      }
    },
    async checkFileName () {
      // console.log('checkFile')
      if (this.file) {
        // console.log('checkFile 2', this.file.name, this.year)
        let formData = new FormData()
        formData.append('filename', this.file.name)
        formData.append('year', this.year)
        this.fileAlreadyUpload = await this.$store.dispatch('ImportStore/checkFileName', formData)
      }
    }
  }

}
</script>

<style>
.no-padding {
  padding: 0;
}
.redText {
  color: red;
}
</style>
