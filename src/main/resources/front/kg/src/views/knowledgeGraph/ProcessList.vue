<template>
    <div>
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
            </el-col>
        </el-row>
        <el-table v-loading="loading" :data="processList">
            <!-- <el-table-column label="id" align="center" prop="id" /> -->
            <el-table-column label="工艺编码" align="center" prop="code" />
            <el-table-column label="工艺名称" align="center" prop="name" />
            <el-table-column label="工艺状态" align="center" prop="status">
                <template slot-scope="scope">
                    <el-link type="success" :underline="false" v-if="scope.row.status == 1">启用</el-link>
                    <el-link type="info" :underline="false" v-if="scope.row.status == 0">未启用</el-link>
                    <el-link type="danger" :underline="false" v-if="scope.row.status == 2">异常</el-link>
                </template>
            </el-table-column>>
            <el-table-column label="工艺描述" align="center" prop="description" :show-overflow-tooltip="true">
                <template slot-scope="{row}">
                    <span>{{ row.description }}</span>
                </template>
            </el-table-column>
            <el-table-column label="工艺步骤" align="center" prop="procedures" :show-overflow-tooltip="true">
                <template slot-scope="{row}">
                    <span>{{ row.procedures }}</span>
                </template>
            </el-table-column>
            <el-table-column label="工艺参数" align="center" prop="parameter" :show-overflow-tooltip="true">
                <template slot-scope="{row}">
                    <span>{{ row.parameter }}</span>
                </template>
            </el-table-column>
            <el-table-column label="扩展属性" align="center" prop="properties" :show-overflow-tooltip="true">
                <template slot-scope="{row}">
                    <span>{{ row.properties }}</span>
                </template>
            </el-table-column>
            <el-table-column label="源BOM编码" align="center" prop="sourceBomCode">
                <template slot-scope="scope">
                    <div :style="{ color: scope.row.status == 2 ? 'red' : 'black' }">
                        {{ scope.row.sourceBomCode }}
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="目标BOM编码" align="center" prop="targetBomCode">
                <template slot-scope="scope">
                    <div :style="{ color: scope.row.status == 2 ? 'red' : 'black' }">
                        {{ scope.row.targetBomCode }}
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="关联设备" align="center" prop="relatedEquipment" />
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" icon="el-icon-edit"
                        @click="handleUpdate(scope.row)">修改</el-button>
                    <el-button size="mini" type="text" icon="el-icon-delete"
                        @click="handleDelete(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
            <el-form ref="form" :model="form" :rules="rules" label-width="120px">
                <el-row>
                    <el-form-item label="工艺编码:" prop="code">
                        <el-input v-model="form.code" placeholder="请输入工艺编码" @input="$forceUpdate()" />
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="工艺名称:" prop="name">
                        <el-input v-model="form.name" placeholder="请输入工艺名称" @input="$forceUpdate()" />
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="工艺状态:" prop="status">
                        <el-switch :disabled="isDisable" v-model="processStatus"></el-switch>
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="工艺描述:" prop="description">
                        <el-input v-model="form.description" type="textarea" placeholder="请输入工艺描述" maxlength="500"
                            @input="$forceUpdate()" />
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="工艺步骤:" prop="procedures">
                        <el-input v-model="form.procedures" type="textarea" placeholder="请输入工艺步骤" maxlength="500"
                            @input="$forceUpdate()" />
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="工艺参数:" prop="parameter">
                        <el-input v-model="form.parameter" type="textarea" placeholder="请输入工艺参数" maxlength="1000"
                            @input="$forceUpdate()" />
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="扩展属性:" prop="properties">
                        <el-input v-model="form.properties" type="textarea" placeholder="请输入扩展属性" maxlength="1000"
                            @input="$forceUpdate()" />
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="源BOM编码:" prop="sourceBomCode">
                        <el-input v-model="form.sourceBomCode" placeholder="请输入源BOM编码" @input="$forceUpdate()" />
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="目标BOM编码:" prop="targetBomCode">
                        <el-input v-model="form.targetBomCode" placeholder="请输入目标BOM编码" @input="$forceUpdate()" />
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="关联设备:" prop="relatedEquipment">
                        <el-select v-model="form.relatedEquipment" multiple placeholder="请选择关联设备"
                            @change="$forceUpdate()">
                            <el-option v-for="item in equipmentOptions" :key="item.value" :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                        <el-tooltip class="item" effect="dark" content="点击为您预测工艺适合适配设备（优化设备与工艺的匹配）" placement="top-end">
                            <el-button style="margin-left: 50px;" type="text" icon="el-icon-data-analysis"
                                @click="equipmentPredict">设备预测</el-button>
                        </el-tooltip>

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
import { getAllProcess, deleteProcess, addProcess, editProcess, predictEquipment } from "../../api/process.js"
import { getAllEquipment } from "../../api/equipment.js"
export default {
    name: "",
    data: function () {
        return {
            processList: [],
            rules: {
                code: [
                    { required: true, message: "工艺编码不能为空", trigger: "blur" }
                ],
                name: [
                    { required: true, message: "工艺名称不能为空", trigger: "blur" }
                ],
                status: [
                    { required: true, message: "工艺状态不能为空", trigger: "blur" }
                ],
                description: [
                    { required: true, message: "工艺描述不能为空", trigger: "blur" }
                ],
                procedures: [
                    { required: true, message: "工艺步骤不能为空", trigger: "blur" }
                ],
                parameter: [
                    { required: true, message: "工艺参数不能为空", trigger: "blur" }
                ],
                properties: [
                    { required: true, message: "扩展属性不能为空", trigger: "blur" }
                ],
                // sourceBomCode: [
                //     { required: true, message: "源BOM编码不能为空", trigger: "blur" }
                // ],
                // targetBomCode: [
                //     { required: true, message: "目标BOM编码不能为空", trigger: "blur" }
                // ],
                // relatedEquipment: [
                //     { required: true, message: "关联设备不能为空", trigger: "blur" }
                // ]
            },
            // 遮罩层
            loading: true,
            // 弹出层标题
            title: "",
            // 是否显示弹出层
            open: false,
            // 表单参数
            form: {
                relatedEquipment: []
            },
            processStatus: undefined,
            isDisable: undefined,
            equipmentOptions: [],
            // 弹窗状态 true为新增 false为修改
            editOrAdd: true,
        };
    },
    created() {
        this.getEquipmentOptions();
    },
    watch: {
        processStatus(newVal, oldVal) {
            this.form.status = newVal ? 1 : 0;
        }
    },
    mounted() {
        this.getProcessList();
        
    },
    methods: {
        getProcessList() {
            this.loading = true;
            getAllProcess().then((res) => {
                this.processList = res.data;
                for (var i = 0; i < this.processList.length; i++) {
                    var array = JSON.parse(this.processList[i].relatedEquipment)
                    this.processList[i].relatedEquipment = this.getEquipmentLabel(array)
                }
                this.loading = false;
            })
        },
        getEquipmentOptions() {
            getAllEquipment().then((res) => {
                for (var option of res.data) {
                    this.equipmentOptions.push({
                        value: option.code,
                        label: option.name
                    })
                }
            });
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
                code: undefined,
                name: undefined,
                status: false,
                description: undefined,
                procedures: undefined,
                parameter: undefined,
                properties: undefined,
                sourceBomCode: undefined,
                targetBomCode: undefined,
                relatedEquipment: undefined,
            };
            this.processStatus = false;
            this.resetForm("form");
        },
        /** 新增按钮操作 */
        handleAdd() {
            this.reset();
            this.editOrAdd = true;
            this.open = true;
            this.title = "添加工艺";
        },
        /** 修改按钮操作 */
        handleUpdate(row) {
            this.form.id = row.id;
            this.form.code = row.code;
            this.form.name = row.name;
            this.form.status = row.status;
            if (row.status == 2) {
                this.isDisable = true
            } else {
                this.isDisable = false
            }
            this.form.description = row.description;
            this.form.procedures = row.procedures;
            this.form.parameter = row.parameter;
            this.form.properties = row.properties;
            this.form.sourceBomCode = row.sourceBomCode;
            this.form.targetBomCode = row.targetBomCode;
            this.form.relatedEquipment = JSON.parse(row.relatedEquipment);
            this.processStatus = row.status == 1 ? true : false;
            this.editOrAdd = false;
            this.open = true;
            this.title = "修改工艺";
        },
        /** 提交按钮 */
        submitForm() {
            this.$refs["form"].validate(valid => {
                if (valid) {
                    if (this.editOrAdd) {
                        this.form.relatedEquipment = JSON.stringify(this.form.relatedEquipment)
                        addProcess(this.form).then((res) => {
                            if (res.data) {
                                this.open = false;
                                this.getProcessList();
                                this.$message({
                                    type: 'success',
                                    message: '新增成功'
                                });
                            } else {
                                this.open = false;
                                this.getProcessList();
                                this.$message({
                                    message: '新增失败，工艺编码重复'
                                });
                            }
                        })
                    } else {
                        this.form.relatedEquipment = JSON.stringify(this.form.relatedEquipment)
                        editProcess(this.form).then((res) => {
                            this.open = false;
                            this.getProcessList();
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
            const processId = row.id;
            this.$confirm('是否确认删除工艺编码为"' + row.code + '"的工艺吗？').then(() => {
                deleteProcess(processId).then(() => {
                    this.getProcessList();
                    this.$message({
                        type: 'success',
                        message: '删除成功'
                    });
                })
            })
        },
        equipmentPredict() {
            setTimeout(() => {
                this.$set(this.form, "relatedEquipment", [this.equipmentOptions[1].value])
            }, 1000);
            // predictEquipment(this.form)
        },
        getEquipmentLabel(data) {
            var newArray = []
            for (var i = 0; i < data.length; i++) {
                for (var j = 0; j < this.equipmentOptions.length; j++) {
                    if (data[i] == this.equipmentOptions[j].value) {
                        newArray.push(this.equipmentOptions[j].label)
                    }
                }
            }
            
            return newArray.join("，");
        }
    }
};
</script>

<style scoped>
.el-tooltip__popper {
    font-size: 14px;
    max-width: 40%
}

.danger {
    color: red
}
</style>