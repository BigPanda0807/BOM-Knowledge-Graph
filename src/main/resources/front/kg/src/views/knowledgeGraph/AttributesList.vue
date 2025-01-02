<template>
    <div>
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
            </el-col>
        </el-row>
        <el-table v-loading="loading" :data="attributesList">
            <el-table-column label="id" align="center" prop="id" />
            <el-table-column label="属性名称" align="center" prop="name" />
            <el-table-column label="属性别名" align="center" prop="aliasName" />
            <el-table-column label="属性类型" align="center" prop="biztype" />
            <el-table-column label="备注" align="center" prop="remark" />
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" icon="el-icon-edit"
                        @click="handleUpdate(scope.row)">修改</el-button>
                    <el-button size="mini" type="text" icon="el-icon-delete"
                        @click="handleDelete(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
            <el-form ref="form" :model="form" :rules="rules" label-width="100px">
                <el-row>
                    <el-form-item label="属性名称:" prop="name">
                        <el-input v-model="form.name" placeholder="请输入属性名称" @input="$forceUpdate()" />
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="属性别名:" prop="aliasName">
                        <el-input v-model="form.aliasName" placeholder="请输入属性别名" @input="$forceUpdate()" />
                    </el-form-item>
                </el-row>

                <el-row>
                    <el-form-item label="属性类型:" prop="biztype">
                        <el-select v-model="form.biztype" placeholder="请选择属性类型" @change="$forceUpdate()">
                            <el-option v-for="item in biztypeOptions" :key="item.value" :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="备注:" prop="remark">
                        <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" maxlength="200"
                            @input="$forceUpdate()" />
                    </el-form-item>
                </el-row>

            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="submitForm">确 定</el-button>
                <el-button @click="cancel">取 消</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { addAttributes, getAllAttributes, editAttributes, deleteAttributes } from "../../api/attributes.js"
export default {
    name: "",
    data: function () {
        return {
            attributesList: [],
            rules: {
                name: [
                    { required: true, message: "属性名称不能为空", trigger: "blur" }
                ],
                aliasName: [
                    { required: true, message: "属性别名不能为空", trigger: "blur" }
                ],
                biztype: [
                    { required: true, message: "属性类型不能为空", trigger: "blur" }
                ],
                remark: [
                    { required: true, message: "备注不能为空", trigger: "blur" }
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
            biztypeOptions: [
                {
                    value: "工艺属性",
                    label: "工艺属性"
                }, {
                    value: "BOM属性",
                    label: "BOM属性"
                }
            ],
            // 弹窗状态 true为新增 false为修改
            editOrAdd: true,
        };
    },
    created() {
    },
    watch: {
    },
    mounted() {
        this.getAttributesList();
    },
    methods: {
        getAttributesList() {
            this.loading = true;
            getAllAttributes().then((res) => {
                this.attributesList = res.data;
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
                id: undefined,
                name: undefined,
                aliasName: undefined,
                biztype: undefined,
                remark: undefined,
            };
            this.resetForm("form");
        },
        /** 新增按钮操作 */
        handleAdd() {
            this.reset();
            this.editOrAdd = true;
            this.open = true;
            this.title = "添加属性";
        },
        /** 修改按钮操作 */
        handleUpdate(row) {
            this.form.id = row.id;
            this.form.name = row.name;
            this.form.aliasName = row.aliasName;
            this.form.biztype = row.biztype;
            this.form.remark = row.remark;
            this.editOrAdd = false;
            this.open = true;
            this.title = "修改属性";
        },
        /** 提交按钮 */
        submitForm() {
            this.$refs["form"].validate(valid => {
                if (valid) {
                    if (this.editOrAdd) {
                        addAttributes(this.form).then((res) => {
                            if (res.data) {
                                this.open = false;
                                this.getAttributesList();
                                this.$message({
                                    type: 'success',
                                    message: '新增成功'
                                });
                            } else {
                                this.open = false;
                                this.getAttributesList();
                                this.$message({
                                    message: '新增失败，属性名称重复'
                                });
                            }
                        })
                    } else {
                        editAttributes(this.form).then((res) => {
                            this.open = false;
                            this.getAttributesList();
                            this.$message({
                                type: 'success',
                                message: '修改成功'
                            });
                        })
                    }
                }
            });
        },
        /** 删除按钮操作 */
        handleDelete(row) {
            const attributeId = row.id;
            this.$confirm('是否确认删除属性名称为"' + row.aliasName + '"的属性吗？').then(() => {
                deleteAttributes(attributeId).then(() => {
                    this.getAttributesList();
                    this.$message({
                        type: 'success',
                        message: '删除成功'
                    });
                })
            })
        }
    }
};
</script>

<style scoped>
.el-tooltip__popper {
    font-size: 14px;
    max-width: 40%
}
</style>