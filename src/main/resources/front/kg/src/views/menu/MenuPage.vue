<template>
  <div>
    <el-menu class="el-menu-vertical-demo" @open="handleOpen" @close="handleClose" @select="handleSelect"
      :unique-opened="true" default-active="/super-bom-menu/super-bom-attr" background-color="#324056"
      text-color="#bfcbd9" active-text-color="#409EFF" :router=true>
      <!--菜单组件 绑定内部属性 -->
      <my-menu v-for="menu in menus" :menu="menu" :key="menu.id"></my-menu>
    </el-menu>
  </div>
</template>

<script>
import MyMenu from './MyMenu.vue';
export default {
  name: "MenuPageVue",
  components: { MyMenu },
  props: {},
  data() {
    return {
      menus: []
    };
  },
  mounted() {

    // let obj=this.$store.state.menus;
    // let obj = JSON.parse(localStorage.getItem("menus"));
    // 直接展示路由
    var obj = [
      { id: "1", name: "Index", path: "/", "icon": "el-icon-s-home", hide: 0, children: [] },
      {
        id: "2", name: "Recommendation Management", path: "/", "icon": "el-icon-s-order", hide: 0, children: [
          {
            id: "3", name: "Recommendation List", path: "/recommendList", "icon": "el-icon-collection", hide: 0, children: []
          }
        ]
      },
      {
        id: "4", name: "Knowledge Graph", path: "/", "icon": "el-icon-cpu", hide: 0, children: [
          {
            id: "5", name: "Graph Import", path: "/importToKG", "icon": "el-icon-collection", hide: 0, children: []
          }, {
            id: "7", name: "Equipment Management", path: "/equipment", "icon": "el-icon-collection", hide: 0, children: []
          }, {
            id: "8", name: "Process Management", path: "/process", "icon": "el-icon-collection", hide: 0, children: []
          }, {
            id: "9", name: "Attribute Management", path: "/attributes", "icon": "el-icon-collection", hide: 0, children: []
          }, {
            id: "6", name: "Visualization Graph", path: "/knowledgeGraph", "icon": "el-icon-collection", hide: 0, children: []
          }
        ]
      }
    ]
    //过滤掉那些不想显示的
    this.menus = obj.filter(item => {
      return item.hide == false
    });
  },
  methods: {
    handleSelect(key, keyPath) {
      // this.$router.push(key);
      //console.log(key, keyPath);
    },
    handleOpen(key, keyPath) {
      //console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      //console.log(key, keyPath);
    },
  },
};
</script>
<style scoped>
.el-menu-vertical-demo {
  width: 1000px;
  height: 1000px;
}
</style>
