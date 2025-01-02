<template>
  <div id="app">
    <!-- <nav>
      <router-link to="/recommendList">推荐列表</router-link> |
      <router-link to="/equipmentList">设备管理</router-link> |
      <router-link to="/ManufacturingTechniqueList">工艺管理</router-link> |
      <router-link to="/SupplierList">供应商管理</router-link> |
      <router-link to="/importCsv">知识图谱导入（CSV）</router-link>
    </nav> -->
    <router-view />
  </div>
</template>
<script>
export default {
  name: "App",
  components: {
  },
};
</script>

<style>

/* #app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
} */

/* nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
} */
</style>
