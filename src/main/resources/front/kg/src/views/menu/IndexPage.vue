<template>
    <div>
        <el-container style="height: 1000px">
            <!-- <el-aside width="188px">
                <MenuPageVue />
            </el-aside> -->
            <el-container>
                <el-header>
                    <!-- <NavbarVue /> -->
                </el-header>
                <el-main>
                    <!-- <el-tabs v-model="active" @tab-remove="removeTab" @tab-click="handleClick">
                        <el-tab-pane label="首页" name="/">
                            <router-view></router-view>
                        </el-tab-pane>
                        <el-tab-pane v-for="item in editableTabs" :key="item.path" :label="item.name" :name="item.path"
                            :closable="true">
                            <router-view></router-view>
                        </el-tab-pane>
                    </el-tabs> -->
                </el-main>
                <!-- <el-footer></el-footer> -->
            </el-container>
        </el-container>
    </div>
</template>

<script>
import MenuPageVue from "../../components/menu/MenuPage.vue";
import NavbarVue from "../Navbar.vue";
export default {
    name: "IndexPage",
    components: {
        MenuPageVue,
        NavbarVue,
    },
    data() {
        return {
            editableTabs: [],
            active: "/",
        };
    },
    methods: {
        handleClick(tab, event) {
            if (tab.name === "/") {
                this.$router.push("/");
                this.active = "/";
            } else {
                this.$router.push(tab.name);
                this.active = tab.name;
            }
        },
        removeTab(targetName) {
            if (targetName === this.active) {
                for (var i = 0; i < this.editableTabs.length; i++) {
                    if (this.active === this.editableTabs[i].path) {
                        this.editableTabs.splice(i, 1);
                        if (this.editableTabs.length == 0) {
                            this.$router.push("/");
                            this.active = "/";
                            return;
                        } else {
                            this.$router.push(this.editableTabs[this.editableTabs.length - 1].path);
                            this.active = this.editableTabs[this.editableTabs.length - 1].path;
                            return;
                        }
                    }
                }
            } else {
                for (var i = 0; i < this.editableTabs.length; i++) {
                    if (targetName === this.editableTabs[i].path) {
                        this.editableTabs.splice(i, 1);
                        if (this.editableTabs == 0) {
                            this.$router.push("/");
                            this.active = "/";
                        }
                    }
                }
            }
        },
    },
    mounted() {
        if (this.$route.path == "/") {
            this.editableTabs = [];
            return;
        }
        this.editableTabs.push({
            name: this.$route.name,
            path: this.$route.path,
        });
        this.active = this.$route.path;
    },
    watch: {
        $route(to, from) {
            if (to.path === "/") {
                this.$router.push("/");
                this.active = "/";
                return;
            }
            for (var i = 0; i < this.editableTabs.length; i++) {
                if (this.editableTabs[i].path === to.path) {
                    this.active = to.path;
                    return;
                }
            }
            if (this.editableTabs.length >= 5) {
                this.editableTabs = this.editableTabs.slice(1);
            }
            this.editableTabs.push({
                name: to.name,
                path: to.path,
            });
            this.active = to.path;
        },
    },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.el-header,
.el-footer {
    background-color: #b3c0d1;
    color: #333;
    text-align: center;
    line-height: 60px;
    width: 100%;
    padding: 0px;
}

body>.el-container {
    margin-bottom: 40px;
}

.el-main {
    /* background-color: #e9eef3;
  color: #333; */
    padding-top: 0px;
}

.el-header {
    background-color: white !important;
}

.el-tabs__nav .el-tabs__item:nth-child(1) span {
    display: none;
}
</style>
