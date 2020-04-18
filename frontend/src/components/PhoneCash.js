export default class PhoneCash {
  constructor (options) {
    if (!options) options = {}
    this.phone = options.phone || null
    this.people = options.people || null
    this.month = options.month || null
    this.internationalcalls = options.internationalcalls || 0
    this.longcalls = options.longcalls || 0
    this.localcalls = options.localcalls || 0
    this.localsms = options.localcalls || 0
    this.gprs = options.gprs || 0
    this.internationalroamingcalls = options.internationalroamingcalls || 0
    this.internationalroamingsms = options.internationalroamingsms || 0
    this.internationalgprsroaming = options.internationalroamingsms || 0
    this.internationalroamingcash = options.internationalroamingsms || 0
    this.russiaroamingcalls = options.russiaroamingcalls || 0
    this.russiaroamingsms = options.russiaroamingsms || 0
    this.russiaroaminginet = options.russiaroaminginet || 0
    this.russiaroamingtraffic = options.russiaroamingtraffic || 0
    this.subscriptionfee = options.subscriptionfee || 0
    this.subscriptionfeeaddon = options.subscriptionfeeaddon || 0
    this.discounts = options.discounts || 0
    this.onetime = options.onetime || 0
    this.sum = options.sum || 0
    this.vat = options.vat || 0
    this.fullsum = options.fullsum || 0
  }
}
