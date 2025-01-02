<template>
    <div class="navbar">
        <div>
            <el-breadcrumb separator="/" class="breadCrumbList">
                <el-breadcrumb-item :to="{ path: '/' }" class="breadCrumbItem">首页</el-breadcrumb-item>
                <el-breadcrumb-item v-for="breadCrumbItem in breadCrumbList" :key="breadCrumbItem.path"
                    :to="breadCrumbItem.path" class="breadCrumbItem">
                    {{ breadCrumbItem.name }}
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>

        <!-- <div class="right-menu">
            <el-dropdown class="avatar-container" trigger="click">
                <div class="avatar-wrapper">
                    <img :src="header" class="user-avatar" />
                    <i class="el-icon-caret-bottom" />
                </div>
                <el-dropdown-menu slot="dropdown" class="user-dropdown">
                    <el-dropdown-item @click.native="showMsg">
                        {{ messageTxt }}
                        <el-badge class="mark" v-model="unReadMessageNumber" :style="{ display: displayActive }" />
                    </el-dropdown-item>
                    <el-dropdown-item @click.native="userInfo">
                        <span style="display: block">我的信息</span>
                    </el-dropdown-item>
                    <el-dropdown-item @click.native="logout">
                        <span style="display: block">退出</span>
                    </el-dropdown-item>

                </el-dropdown-menu>
            </el-dropdown>
        </div> -->
    </div>
</template>

<script>
// import header from "../assets/images/header.gif";
export default {
    components: {},
    data() {
        return {
            breadCrumbList: [],
        };
    },
    methods: {

    },
    computed: {},
    mounted() {
        if (this.$route.path == "/") {
            this.breadCrumbList = [];
            return;
        }
        var breadArray = [];
        for (var i = 0; i < this.$route.matched.length; i++) {
            breadArray.push(this.$route.matched[i]);
        }
        this.breadCrumbList = breadArray;
    },
    watch: {
        $route(to, from) {
            if (to.path == "/") {
                this.breadCrumbList = [];
                return;
            }
            var breadArray = [];
            for (var i = 0; i < this.$route.matched.length; i++) {
                breadArray.push(this.$route.matched[i]);
            }
            this.breadCrumbList = breadArray;
            // // 不可前进
            // for (var i = 0; i < this.breadCrumbList.length; i++) {
            //     if (this.breadCrumbList[i].path === to.path)
            //         // this.breadCrumbList = this.breadCrumbList.slice(0,i);
            //         return;//可前进
            // }
            // this.breadCrumbList.push({
            //     path: to.path,
            //     name: to.name,
            // });
            // // 防止面包屑过长
            // if (this.breadCrumbList.length == 9) {
            //     this.breadCrumbList = this.breadCrumbList.slice(1);
            // }
        },
    },
};
</script>

<style lang="scss" scoped>
.el-input {
    width: 200px;
}

.navbar {
    height: 50px;
    overflow: hidden;
    position: relative;
    background: #fff;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

    .hamburger-container {
        line-height: 46px;
        height: 100%;
        float: left;
        cursor: pointer;
        transition: background 0.3s;
        -webkit-tap-highlight-color: transparent;

        &:hover {
            background: rgba(0, 0, 0, 0.025);
        }
    }

    .breadcrumb-container {
        float: left;
    }

    .breadCrumbList {
        float: left;
        margin-top: 20px;
        margin-left: 20px;
    }

    .breadCrumbItem .el-breadcrumb__inner {
        font-weight: 400;
        color: #999;
    }

    .right-menu {
        float: right;
        height: 100%;
        line-height: 50px;
        position: relative;

        &:focus {
            outline: none;
        }

        .right-menu-item {
            display: inline-block;
            padding: 0 8px;
            height: 100%;
            font-size: 18px;
            color: #5a5e66;
            vertical-align: text-bottom;

            &.hover-effect {
                cursor: pointer;
                transition: background 0.3s;

                &:hover {
                    background: rgba(0, 0, 0, 0.025);
                }
            }
        }

        .avatar-container {
            margin-right: 30px;

            .avatar-wrapper {
                margin-top: 5px;
                position: relative;

                .user-avatar {
                    cursor: pointer;
                    width: 40px;
                    height: 40px;
                    border-radius: 10px;
                }

                .el-icon-caret-bottom {
                    cursor: pointer;
                    position: absolute;
                    right: -20px;
                    top: 25px;
                    font-size: 12px;
                }
            }
        }
    }

    .pagination {
        margin-top: 20px;
    }
}

.sendMessageButton {
    position: absolute;
    right: 80px;
    top: 10px;
}

.demonstration {
    margin-right: 20px;
}

.demonstrationTo {
    margin-left: 20px;
    margin-right: 20px;
}
</style>