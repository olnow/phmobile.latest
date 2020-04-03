import axios from 'axios'
import {AXIOS} from './http-common'

export default {
  name: 'getPhones',

  data () {
    return {
      posts: [],
      errors: []
    }
  },
  methods: {
    // Fetches posts when the component is created.
    callRestService () {
<h1>ssdsd</h1>
      AXIOS.get(`getPhones`)
        .then(response => {
          // JSON responses are automatically parsed.
          this.posts = response.data
        })
        .catch(e => {
          this.errors.push(e)
        })
    }
  }
