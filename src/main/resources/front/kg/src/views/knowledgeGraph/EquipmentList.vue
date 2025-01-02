<template>
    <div>
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
            </el-col>
        </el-row>
        <el-table v-loading="loading" :data="equipmentList">
            <!-- <el-table-column label="id" align="center" prop="id" /> -->
            <el-table-column label="设备编码" align="center" prop="code" />
            <el-table-column label="设备名称" align="center" prop="name" />
            <el-table-column label="设备类型" align="center" prop="type" />
            <el-table-column label="设备状态" align="center" prop="status">
                <template slot-scope="scope">
                    <el-link type="success" :underline="false" v-if="scope.row.status == 1">启用</el-link>
                    <el-link type="info" :underline="false" v-else-if="scope.row.status == 0">未启用</el-link>
                    <el-link type="danger" :underline="false" v-else-if="scope.row.status == 2">故障</el-link>
                </template>
            </el-table-column>>
            <el-table-column label="关联工艺" align="center" prop="relatedProcess" />
            <el-table-column label="设备规格" align="center" prop="technicalSpecifications" :show-overflow-tooltip="true">
                <template slot-scope="{row}">
                    <span>{{ row.technicalSpecifications }}</span>
                </template>
            </el-table-column>
            <el-table-column label="扩展属性" align="center" prop="properties" :show-overflow-tooltip="true">
                <template slot-scope="{row}">
                    <span>{{ row.properties }}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" icon="el-icon-edit"
                        @click="handleUpdate(scope.row)">修改</el-button>
                    <el-button size="mini" type="text" icon="el-icon-delete"
                        @click="handleDelete(scope.row)">删除</el-button>
                    <el-button size="mini" type="text" icon="el-icon-setting"
                        @click="showEquipmentRelationship(scope.row)">查看设备关联</el-button>
                    <el-button size="mini" type="text" icon="el-icon-setting" v-if="scope.row.status == 2"
                        @click="releaseEquipmentException(scope.row)">解除设备故障</el-button>
                    <el-button size="mini" type="text" icon="el-icon-setting" v-else
                        @click="reportEquipmentException(scope.row)">报告设备故障</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 查看设备关联 -->
        <el-dialog :title="relationTitle" @open="loadEcharts()" :visible.sync="relationsipOpen" width="600px"
            append-to-body>
            <div id="graph"
                style="width: 100%;height: 500px;margin-top: 20px; border-radius: 10px; border: 1px solid #ccc;">
            </div>
        </el-dialog>

        <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
            <el-form ref="form" :model="form" :rules="rules" label-width="100px">
                <el-row>
                    <el-form-item label="设备编码:" prop="code">
                        <el-input v-model="form.code" placeholder="请输入设备编码" @input="$forceUpdate()" />
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="设备名称:" prop="name">
                        <el-input v-model="form.name" placeholder="请输入设备名称" @input="$forceUpdate()" />
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="设备类型:" prop="type">
                        <el-input v-model="form.type" placeholder="请输入设备类型" @input="$forceUpdate()" />
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="设备状态:" prop="status">
                        <el-switch :disabled="isDisable" v-model="equipmentStatus"></el-switch>
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="关联工艺:" prop="relatedProcess">
                        <el-select v-model="form.relatedProcess" multiple placeholder="请选择关联工艺"
                            @change="$forceUpdate()">
                            <el-option v-for="item in processOptions" :key="item.value" :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="设备规格:" prop="technicalSpecifications">
                        <el-input v-model="form.technicalSpecifications" type="textarea" placeholder="请输入设备规格"
                            maxlength="500" @input="$forceUpdate()" />
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="扩展属性:" prop="properties">
                        <el-input v-model="form.properties" type="textarea" placeholder="请输入扩展属性" maxlength="500"
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
import { addEquipment, getAllEquipment, editEquipment, deleteEquipment, showRelationship, releaseException, reportException } from "../../api/equipment.js"
import { getAllProcess } from "../../api/process.js"
import * as echarts from 'echarts';
export default {
    name: "",
    data: function () {
        return {
            equipmentList: [],
            rules: {
                code: [
                    { required: true, message: "设备编码不能为空", trigger: "blur" }
                ],
                name: [
                    { required: true, message: "设备名称不能为空", trigger: "blur" }
                ],
                type: [
                    { required: true, message: "设备类型不能为空", trigger: "blur" }
                ],
                status: [
                    { required: true, message: "设备状态不能为空", trigger: "blur" }
                ],
                // relatedProcess: [
                //     { required: true, message: "关联工艺不能为空", trigger: "blur" }
                // ],
                technicalSpecifications: [
                    { required: true, message: "设备规格不能为空", trigger: "blur" }
                ],
                properties: [
                    { required: true, message: "扩展属性不能为空", trigger: "blur" }
                ]
            },
            // 遮罩层
            loading: true,
            // 弹出层标题
            title: "",
            // 是否显示弹出层
            open: false,
            relationsipOpen: false,
            // 表单参数
            form: {},
            equipmentStatus: undefined,
            isDisable: undefined,
            processOptions: [],
            // 弹窗状态 true为新增 false为修改
            editOrAdd: true,
            currentEquipmentCode: undefined,
            currentEquipmentName: undefined
        };
    },
    created() {
    },
    watch: {
        equipmentStatus(newVal, oldVal) {
            this.form.status = newVal ? 1 : 0;
        }
    },
    mounted() {
        this.getEquipmentList();
        this.getProcessOptions();
    },
    methods: {
        getEquipmentList() {
            this.loading = true;
            getAllEquipment().then((res) => {
                this.equipmentList = res.data;
                // for (var equipment of this.equipmentList) {
                //     equipment.relatedProcess = JSON.parse(equipment.relatedProcess).join(",")
                // }
                this.loading = false;
            })
        },
        getProcessOptions() {
            getAllProcess().then((res) => {
                for (var option of res.data) {
                    this.processOptions.push({
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
                type: undefined,
                status: false,
                relatedProcess: undefined,
                technicalSpecifications: undefined,
                properties: undefined
            };
            this.equipmentStatus = false;
            this.resetForm("form");
        },
        /** 新增按钮操作 */
        handleAdd() {
            this.reset();
            this.editOrAdd = true;
            this.open = true;
            this.title = "添加设备";
        },
        /** 修改按钮操作 */
        handleUpdate(row) {
            this.form.id = row.id;
            this.form.code = row.code;
            this.form.name = row.name;
            this.form.properties = row.properties;
            this.form.relatedProcess = JSON.parse(row.relatedProcess);
            this.form.status = row.status;
            if (row.status == 2) {
                this.isDisable = true
            } else {
                this.isDisable = false
            }
            this.equipmentStatus = row.status == 1 ? true : false;
            this.form.technicalSpecifications = row.technicalSpecifications;
            this.form.type = row.type;
            this.editOrAdd = false;
            this.open = true;
            this.title = "修改设备";
        },
        /** 提交按钮 */
        submitForm() {
            this.$refs["form"].validate(valid => {
                if (valid) {
                    if (this.editOrAdd) {
                        this.form.relatedProcess = JSON.stringify(this.form.relatedProcess)
                        addEquipment(this.form).then((res) => {
                            if (res.data) {
                                this.open = false;
                                this.getEquipmentList();
                                this.$message({
                                    type: 'success',
                                    message: '新增成功'
                                });
                            } else {
                                this.open = false;
                                this.getEquipmentList();
                                this.$message({
                                    message: '新增失败，设备编码重复'
                                });
                            }
                        })
                    } else {
                        this.form.relatedProcess = JSON.stringify(this.form.relatedProcess)
                        editEquipment(this.form).then((res) => {
                            this.open = false;
                            this.getEquipmentList();
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
            const equipmentId = row.id;
            this.$confirm('是否确认删除设备编码为"' + row.code + '"的设备吗？').then(() => {
                deleteEquipment(equipmentId).then(() => {
                    this.getEquipmentList();
                    this.$message({
                        type: 'success',
                        message: '删除成功'
                    });
                })
            })
        },
        showEquipmentRelationship(row) {
            this.relationsipOpen = true
            this.currentEquipmentCode = row.code
            this.currentEquipmentName = row.name
            this.relationTitle = "设备关联"
        },
        loadEcharts() {
            setTimeout(() => {
                var chartDom = document.getElementById("graph");
                var myChart = echarts.init(chartDom);
                var demoData = {
                    nodes: [],
                    links: []
                }
                showRelationship(this.currentEquipmentCode).then((res) => {
                    console.log();

                    var equipmentNode = {
                        id: this.currentEquipmentCode,
                        name: this.currentEquipmentName,
                        category: 0
                    }
                    demoData.nodes.push(equipmentNode)
                    for (var i = 0; i < res.data.length; i++) {
                        var node = {
                            id: res.data[i].process.code,
                            name: res.data[i].process.name,
                            category: 1
                        }
                        demoData.nodes.push(node)
                        var link = {
                            source: this.currentEquipmentCode,
                            target: res.data[i].process.code,
                            value: "use"
                        }
                        demoData.links.push(link)
                        var snode = {
                            id: res.data[i].sourceBom.code,
                            name: res.data[i].sourceBom.name,
                            category: 2
                        }
                        if (!this.isInNodeArray(demoData.nodes, snode)) {
                            demoData.nodes.push(snode)
                        }
                        var slink = {
                            source: res.data[i].sourceBom.code,
                            target: res.data[i].process.code,
                            value: "apply"
                        }
                        demoData.links.push(slink)
                        var tnode = {
                            id: res.data[i].targetBom.code,
                            name: res.data[i].targetBom.name,
                            category: 2
                        }
                        if (!this.isInNodeArray(demoData.nodes, tnode)) {
                            demoData.nodes.push(tnode)
                        }
                        var tlink = {
                            source: res.data[i].process.code,
                            target: res.data[i].targetBom.code,
                            value: "apply"
                        }
                        demoData.links.push(tlink)
                    }
                    var option;
                    // 图谱的配置项
                    option = {
                        // 提示框的配置
                        title: {
                            text: "Equipment Graph",
                        },
                        // tooltip: {
                        //     formatter: (x) => {
                        //         if (x.dataType === 'edge') {
                        //             return x.data.source + " " + x.data.processName + " " + x.data.target;
                        //         }
                        //         return x.data.name;
                        //     },
                        // },
                        animationDurationUpdate: 5000,
                        animationEasingUpdate: "quarticlnOut", // quarticlnOut quinticInOut

                        //图形上的文本标签，可用于说明图形的一些数据信息
                        legend: {
                            x: "center",
                            show: true,
                        },
                        series: [
                            {
                                type: "graph", // 类型:关系图
                                layout: "force", //图的布局，类型为力导图
                                symbolSize: 100, //节点大小
                                focusNodeAdjacency: true, //当鼠标移动到节点上，突出显示节点以及节点的边和邻接节点
                                draggable: true, //指示节点是否可以拖动
                                roam: true,
                                edgeSymbol: ["none", "arrow"],
                                categories: [
                                    {
                                        name: "设备",
                                        itemStyle: {
                                            color: "lightgreen",
                                        },
                                    },
                                    {
                                        name: "工艺",
                                        itemStyle: {
                                            color: "orange",
                                        },
                                    },
                                    {
                                        name: "BOM",
                                        itemStyle: {
                                            color: "red",
                                        },
                                    },
                                ],
                                label: {
                                    show: true,
                                    textStyle: {
                                        fontSize: 12,
                                        color: "black",
                                    },
                                },
                                force: {
                                    repulsion: 4000, //节点之间的斥力因子。支持数组表达斥力范围，值越大斥力越大。
                                    edgeLength: 500, //边的两个节点之间的距离
                                    gravity: 0.1, //节点受到的向中心的引力因子。该值越大节点越往中心点靠拢。
                                },
                                edgeSymbolSize: [4, 10], // 边两端的标记大小，可以是一个数组分别指定两端，也可以是单个统一指定。
                                edgeLabel: {
                                    show: true,
                                    textStyle: {
                                        fontSize: 12,
                                    },
                                    formatter: "{c}",
                                },
                                data: demoData.nodes,
                                links: demoData.links,
                                lineStyle: {
                                    opacity: 0.9,
                                    width: 1.1,
                                    curveness: 0,
                                    color: "#262626",
                                },
                            },
                        ],
                    };
                    myChart.setOption(option);
                    myChart.hideLoading();
                })
            }, 1000);
        },
        isInNodeArray(nodes, node) {
            for (var i = 0; i < nodes.length; i++) {
                if (nodes[i].id == node.id) {
                    return true;
                }
            }
            return false;
        },
        reportEquipmentException(row) {
            this.$confirm('是否确认报告设备编码为"' + row.code + '"的设备出现故障吗？').then(() => {
                reportException(row.id, row.code, 2).then((res) => {
                    console.log(res);
                    this.getEquipmentList();
                    this.$message({
                        type: 'success',
                        message: '报告成功'
                    });
                })
            })
        },
        releaseEquipmentException(row) {
            this.$confirm('是否解除设备编码为"' + row.code + '"的设备故障吗？').then(() => {
                releaseException(row.id, row.code, 0).then((res) => {
                    console.log(res);
                    this.getEquipmentList();
                    this.$message({
                        type: 'success',
                        message: '解除成功'
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