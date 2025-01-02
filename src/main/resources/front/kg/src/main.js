import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from "element-ui"
import "element-ui/lib/theme-chalk/index.css"
import axios from "axios"
import {
  post,
  get
} from '@/utils/request.js'
import VueTreeList from 'vue-tree-list'
import { handleTree, resetForm, parseTime } from "@/utils/formUtil";


Vue.config.productionTip = false
Vue.prototype.$axios = axios
Vue.prototype.$post = post
Vue.prototype.$get = get

Vue.prototype.resetForm = resetForm
Vue.prototype.handleTree = handleTree
Vue.prototype.parseTime = parseTime

Vue.use(ElementUI);
Vue.use(VueTreeList);

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
