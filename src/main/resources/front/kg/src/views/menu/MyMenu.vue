<template>

    <!--一级菜单-->
    <!--如果不存在children-->
    <div v-if="menu.children != null">
        <!--孩子数量等于0 单级-->
        <div v-if="menu.children.length === 0">
            <el-menu-item :index="menu.path" :key="menu.id">
                <i :class="menu.icon"></i>
                <span slot="title">{{ menu.name }}</span>
            </el-menu-item>
        </div>
        <!--孩子数量为一直接展示孩子-->
        <!-- <div v-else-if="menu.children.length === 1">
            <el-menu-item :index="menu.children[0].path" :key="menu.children[0].path">
                <i :class="menu.children[0].icon"></i>
                <span slot="title">{{ menu.children[0].name }}</span>
            </el-menu-item>
        </div> -->
        <!-- 如果有孩子菜单就用el-submenu -->
        <div v-else>
            <el-submenu :index="menu.id">
                <template slot="title"> <i :class="menu.icon"></i>{{ menu.name }}</template>
                <MyMenu v-for="(each, index) in menu.children" :menu="each" :key="index" />
            </el-submenu>
        </div>
    </div>


    <!--单独一级-->
    <div v-else>
        <el-menu-item :index="menu.path" :key="menu.path">
            <i :class="menu.icon"></i>
            <span slot="title">{{ menu.name }}</span>
        </el-menu-item>
    </div>



</template>

<script>
export default {

    name: 'MyMenu',
    props: ['menu'],
    components: {
        // MyMenu
    },
    data() {
        return {

        };
    },
    computed: {

    },
    watch: {

    },
    created() {

    },
    mounted() {
    },
    methods: {

    },
};
</script>

<style scoped lang="less">

</style>
