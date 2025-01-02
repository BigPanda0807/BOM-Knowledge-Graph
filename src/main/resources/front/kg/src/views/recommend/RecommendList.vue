<template>
    <div>
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增推荐</el-button>
            </el-col>
        </el-row>
        <el-table v-loading="loading" :data="recommendList">
            <el-table-column label="id" align="center" prop="id" />
            <el-table-column label="推荐名称" align="center" prop="recommendName" />
            <el-table-column label="用户输入结构" align="center" prop="userInputStructure" :show-overflow-tooltip="true">
                <template slot-scope="{row}">
                    <span>{{ row.userInputStructure }}</span>
                </template>
            </el-table-column>
            <el-table-column label="节点个数" align="center" prop="userInputNodeCount" />
            <el-table-column label="推荐结果" align="center" prop="recommendResult" :show-overflow-tooltip="true">
                <template slot-scope="{row}">
                    <span>{{ row.recommendResult }}</span>
                </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center" prop="createTime" width="180">
                <template slot-scope="scope">
                    <span>{{ parseTime(scope.row.createTime) }}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" icon="el-icon-edit"
                        @click="handleUpdate(scope.row)">修改</el-button>
                    <el-button size="mini" type="text" icon="el-icon-delete"
                        @click="handleDelete(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
            <el-form ref="form" :model="form" :rules="rules" label-width="80px">
                <el-form-item label="推荐名称" prop="recommendName">
                    <el-input v-model="form.recommendName" placeholder="请输入推荐名称,注意必须要中文开头" />
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="submitForm">确 定</el-button>
                <el-button @click="cancel">取 消</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { getAllRecommend, deleteRecommend, addRecommend } from "../../api/recommend.js"
export default {
    name: "",
    data: function () {
        return {
            recommendList: [],
            rules: {
                recommendName: [
                    { required: true, message: "推荐名称不能为空", trigger: "blur" }
                ]
            },
            // 遮罩层
            loading: true,
            // 弹出层标题
            title: "",
            // 是否显示弹出层
            open: false,
            // 表单参数
            form: {},
        };
    },
    created() {
    },
    mounted() {
        this.getRecommendList();
    },
    methods: {
        getRecommendList() {
            this.loading = true;
            getAllRecommend().then((res) => {
                this.recommendList = res.data;
                this.loading = false;
            })
        },
        // 取消按钮
        cancel() {
            this.open = false;
            this.reset();
        },
        // 表单重置
        reset() {
            this.form = {
                recommendName: undefined
            };
            this.resetForm("form");
        },
        /** 新增按钮操作 */
        handleAdd() {
            this.reset();
            this.open = true;
            this.title = "添加推荐";
        },
        /** 修改按钮操作 */
        handleUpdate(row) {
            this.$router.push({
                path: '/recommendDetail',
                query: {
                    id: row.id
                }
            })
        },
        /** 提交按钮 */
        submitForm() {
            this.$refs["form"].validate(valid => {
                if (valid) {
                    addRecommend(this.form.recommendName).then((res) => {
                        if (res.data) {
                            this.getRecommendList();
                            this.open = false;
                            this.$message({
                                type: 'success',
                                message: '新增成功'
                            });
                        } else {
                            this.getRecommendList();
                            this.open = false;
                            this.$message({
                                type: 'warning',
                                message: '新增失败,推荐名已存在'
                            });
                        }
                    })
                }
            });
        },
        /** 删除按钮操作 */
        handleDelete(row) {
            const recommendId = row.id;
            this.$confirm('是否确认删除编号为"' + recommendId + '"的推荐吗？').then(function () {
                return deleteRecommend(recommendId);
            }).then(() => {
                this.getRecommendList();
                this.$message({
                    type: 'success',
                    message: '删除成功'
                });
            }).catch(() => { });
        }
    },
};
</script>

<style scoped>
.el-tooltip__popper {
    font-size: 14px;
    max-width: 40%
}
</style>