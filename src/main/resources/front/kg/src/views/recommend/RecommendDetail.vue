<template>
    <div>
        <el-row style="margin-bottom: 10px;">
            <el-col style="padding-top: 10px;">推荐名称：<b>{{ recommendName }}</b></el-col>
            <!-- <el-col :span="5">
                <el-input v-model="recommendName" @blur="onBlur" placeholder="请输入推荐名称" />
            </el-col> -->
            <el-link type="success">该表用于构建用户输入BOM结构从而进行推荐</el-link>
        </el-row>
        <div style="text-align: right">
            <el-button style="margin-right: 20px; float: left;" type="info" size="mini"
                @click="backToRecommendList">返回推荐列表</el-button>
            <el-button style="margin-right: 20px;" type="info" plain icon="el-icon-sort" size="mini"
                @click="toggleExpandAll">展开/折叠</el-button>
        </div>

        <!-- 用户输入 -->
        <el-table v-if="refreshTable" v-loading="loading" :data="inputTreeData" row-key="id"
            :default-expand-all="isExpandAll" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
            <el-table-column prop="id" label="ID" width="120"></el-table-column>
            <el-table-column prop="parentId" label="父节点ID" width="120"></el-table-column>
            <el-table-column prop="name" label="名称" width="200"></el-table-column>
            <el-table-column prop="type" label="类型" width="200"></el-table-column>
            <el-table-column prop="properties" label="其他属性" width="200"></el-table-column>
            <!-- <el-table-column prop="process" label="所用工艺" width="200"></el-table-column>
            <el-table-column prop="processProperties" label="工艺属性" width="200"></el-table-column> -->
            <el-table-column prop="count" label="数量" width="100"></el-table-column>
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" icon="el-icon-edit"
                        @click="handleUpdate(scope.row)">修改</el-button>
                    <el-button size="mini" type="text" icon="el-icon-plus" @click="handleAdd(scope.row)">新增</el-button>
                    <!-- 产品根节点不允许删除-->
                    <el-button v-if="scope.row.parentId != 0" size="mini" type="text" icon="el-icon-delete"
                        @click="handleDelete(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-row style="margin-top: 20px; text-align: right; padding-right: 10px;">
            <el-link type="success" style="float: left; font-size: 20px;">所有推荐结果:</el-link>
            <el-button type="primary" @click="onInputSubmit">输入到知识图谱</el-button>
            <el-button type="primary" v-if="!isClickRecommend" @click="onRecommendSubmit">推 荐</el-button>
            <el-button type="primary" v-if="isClickRecommend" @click="nextRecommend">下一项推荐</el-button>
            <el-button type="primary" v-if="isClickRecommend" @click="queryFinishedBom">查看顶层父节点成品</el-button>
            <el-button type="primary" v-if="isClickRecommend" @click="setRecommendResult">配置推荐结果</el-button>
        </el-row>

        <!-- 所有推荐结果 -->
        <div class="footer" style="height: 420px; margin-bottom:20px;">
            <div id="graph"
                style="width: 700px; height: 400px; margin-top: 20px;  border: 1px solid #ccc; border-radius: 5px;float: left;">
            </div>
            <!-- <div
                style="float: left;height: 400px;margin-top: 20px;margin-left: 120px;text-align: center;line-height: 500px; font-size: 30px;">
                →</div> -->
            <div id="recommendGraph"
                style="width: 700px; height: 400px; margin-top: 20px; border: 1px solid #ccc; border-radius: 5px;float: right;">
            </div>
        </div>

        <el-table :data="allRecommendList" row-key="id" :default-expand-all="isExpandAll"
            :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">

            <el-table-column prop="id" label="ID" width="120"></el-table-column>
            <el-table-column prop="parentId" label="父节点ID" width="120"></el-table-column>
            <el-table-column prop="code" label="物料编码" width="250"></el-table-column>
            <el-table-column prop="parentCode" label="父级物料编码" width="250"></el-table-column>
            <el-table-column prop="name" label="名称" width="250"></el-table-column>
            <el-table-column prop="type" label="类型" width="250"></el-table-column>
            <el-table-column prop="properties" label="其他属性" width="250"></el-table-column>
            <!-- <el-table-column prop="process" label="所用工艺" width="200"></el-table-column>
            <el-table-column prop="processProperties" label="工艺属性" width="200"></el-table-column> -->
            <el-table-column prop="count" label="数量" width="100"></el-table-column>
        </el-table>

        <!-- 已存在的推荐结果 -->
        <el-row style="margin-top: 30px;margin-bottom: 20px; text-align: right; padding-right: 10px;">
            <el-link type="success" style="float: left; font-size: 20px;">已存在的推荐结果:</el-link>
            <el-button type="primary" @click="recommendResultReturnToKG">推荐结果补充知识图谱</el-button>
            <el-button type="primary" @click="exportExcel(currentRecommendList)">推荐结果导出excel</el-button>
        </el-row>

        <el-table :data="currentRecommendList" row-key="id" :default-expand-all="isExpandAll"
            :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
            <!-- id过长 先暂时不显示 -->
            <!-- <el-table-column prop="id" label="ID" width="220" :show-overflow-tooltip="true">
                <template slot-scope="{row}">
                    <span>{{ row.id }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="parentId" label="父节点ID" width="120"></el-table-column> -->
            <el-table-column prop="code" label="物料编码" width="180"></el-table-column>
            <el-table-column prop="parentCode" label="父级物料编码" width="120"></el-table-column>
            <el-table-column prop="name" label="名称" width="120"></el-table-column>
            <el-table-column prop="type" label="类型" width="200"></el-table-column>
            <el-table-column prop="properties" label="其他属性" width="200"></el-table-column>
            <!-- <el-table-column prop="process" label="所用工艺" width="200"></el-table-column>
            <el-table-column prop="processProperties" label="工艺属性" width="200"></el-table-column> -->
            <el-table-column prop="count" label="数量" width="100"></el-table-column>
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" icon="el-icon-edit"
                        @click="handleRecommendUpdate(scope.row)">修改</el-button>
                    <el-button size="mini" type="text" icon="el-icon-plus"
                        @click="handleRecommendAdd(scope.row)">新增</el-button>
                    <el-button v-if="scope.row.parentId != undefined" size="mini" type="text" icon="el-icon-delete"
                        @click="handleRecommendDelete(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 用于编辑和新增用户输入 -->
        <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
            <el-form ref="form" :model="form" :rules="rules" label-width="100px">
                <el-row>
                    <el-form-item label="ID:" prop="id">
                        {{ form.id }}
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="父节点ID:" prop="parentId">
                        {{ form.parentId }}
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="BOM名称:" prop="name">
                        <el-input v-model="form.name" placeholder="请输入BOM名称" />
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="BOM类型:" prop="type">
                        <el-select v-model="form.type" placeholder="请选择BOM类型">
                            <el-option v-for="(type) in bomTypeOptions" :key="type.value" :label="type.label"
                                :value="type.value"></el-option>
                        </el-select>
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="其他属性:" prop="properties">
                        <!-- <el-input v-model="form.properties" type="textarea" placeholder="请输入其他属性" maxlength="20" /> -->
                        {{ form.properties == null ? "暂无属性" : form.properties }}
                        <br>
                        <el-button type="success"
                            @click="handleInputProperties(form.properties, true)">点击输入其他属性</el-button>
                    </el-form-item>
                </el-row>
                <!-- <el-row v-if="form.parentId != 0">
                    <el-form-item label="所用工艺:" prop="process">
                        <el-select v-model="form.process" placeholder="请选择所用工艺">
                            <el-option v-for="(process) in processOptions" :key="process.value" :label="process.label"
                                :value="process.value"></el-option>
                        </el-select>
                    </el-form-item>
                </el-row>
                <el-row v-if="form.parentId != 0">
                    <el-form-item label="工艺属性:" prop="processProperties"> -->
                        <!-- <el-input v-model="form.processProperties" type="textarea" placeholder="请输入工艺属性" maxlength="20" /> -->
                        <!-- {{ form.processProperties == null ? "暂无属性" : form.processProperties }}
                        <br>
                        <el-button type="success"
                            @click="handleInputProperties(form.processProperties, false)">点击输入工艺属性</el-button>
                    </el-form-item>
                </el-row> -->
                <el-row v-if="form.parentId != 0">
                    <el-form-item label="所需数量:" prop="count">
                        <el-input-number v-model="form.count" controls-position="right" :min="0" />
                    </el-form-item>
                </el-row>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="success" v-if="editOrAdd" @click="handleChooseSysBom">选择系统内部件</el-button>
                <el-button type="primary" @click="submitForm">确 定</el-button>
                <el-button @click="cancel">取 消</el-button>
            </div>
        </el-dialog>

        <!-- 用于编辑和新增推荐结果 -->
        <el-dialog :title="resultTitle" :visible.sync="editResultOpen" width="600px" append-to-body
            @close='closeDialog'>
            <el-form ref="resultForm" :model="resultForm" :rules="rules" label-width="120px">
                <el-row>
                    <el-form-item label="ID:" prop="id">
                        {{ resultForm.id }}
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="父节点ID:" prop="parentId">
                        {{ resultForm.parentId }}
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="BOM编码:" prop="code">
                        <el-input v-model="resultForm.code" placeholder="请输入BOM编码" @input="$forceUpdate()" />
                    </el-form-item>
                </el-row>
                <!-- <el-row>
                    <el-form-item label="父级BOM编码:" prop="parentCode">
                        <el-input v-model="resultForm.parentCode" placeholder="父级BOM编码" disabled="false"/>
                    </el-form-item>
                </el-row> -->
                <el-row>
                    <el-form-item label="父级BOM编码:" prop="parentCode">
                        <el-input v-model="resultForm.parentCode" placeholder="父级BOM编码" disabled="false" />
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="BOM名称:" prop="name">
                        <el-input v-model="resultForm.name" placeholder="请输入BOM名称" />
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="BOM类型:" prop="type">
                        <el-select v-model="resultForm.type" placeholder="请选择BOM类型">
                            <el-option v-for="(type) in bomTypeOptions" :key="type.value" :label="type.label"
                                :value="type.value"></el-option>
                        </el-select>
                    </el-form-item>
                </el-row>
                <el-row>
                    <el-form-item label="其他属性:" prop="properties">
                        <!-- <el-input v-model="form.properties" type="textarea" placeholder="请输入其他属性" maxlength="20" /> -->
                        {{ resultForm.properties == null ? "暂无属性" : resultForm.properties }}
                        <br>
                        <el-button type="success"
                            @click="handleInputProperties(resultForm.properties, true)">点击输入其他属性</el-button>
                    </el-form-item>
                </el-row>
                <!-- <el-row v-if="resultForm.parentId != undefined">
                    <el-form-item label="所用工艺:" prop="process">
                        <el-select v-model="resultForm.process" placeholder="请选择所用工艺">
                            <el-option v-for="(process) in processOptions" :key="process.value" :label="process.label"
                                :value="process.value"></el-option>
                        </el-select>
                    </el-form-item>
                </el-row>
                <el-row v-if="resultForm.parentId != undefined">
                    <el-form-item label="工艺属性:" prop="processProperties"> -->
                        <!-- <el-input v-model="form.processProperties" type="textarea" placeholder="请输入工艺属性" maxlength="20" /> -->
                        <!-- {{ resultForm.processProperties == null ? "暂无属性" : resultForm.processProperties }}
                        <br>
                        <el-button type="success"
                            @click="handleInputProperties(resultForm.processProperties, false)">点击输入工艺属性</el-button>
                    </el-form-item>
                </el-row> -->
                <el-row v-if="resultForm.parentId != undefined">
                    <el-form-item label="所需数量:" prop="count">
                        <el-input-number v-model="resultForm.count" controls-position="right" :min="0" />
                    </el-form-item>
                </el-row>

            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="success" v-if="resultEditOrAdd" @click="handleChooseSysBom">选择系统内标准件</el-button>
                <el-button type="primary" @click="submitResultForm">确 定</el-button>
                <el-button @click="resultCancel">取 消</el-button>
            </div>
        </el-dialog>

        <!-- 选择系统内组件 -->
        <el-dialog title="选择系统内组件" :visible.sync="openSysBom" width="1300px" append-to-body>
            <el-row :gutter="20">
                <el-col :span="6">
                    <div class="classfication">
                        <el-tree highlight-current :data="sysBomDataType" :props="defaultProps"
                            @node-click="handleNodeClick" node-key="label" :default-expanded-keys="['成品/产品']"></el-tree>
                    </div>
                </el-col>
                <el-col :span="18">

                    <el-tag size="medium">{{ currentProductType }}</el-tag>
                    <el-table v-loading="loading"
                        :data="sysBomFilterList.filter(data => !search || data.name.toLowerCase().includes(search.toLowerCase()))"
                        height="500px">

                        <!-- <el-table-column prop="id" label="ID" width="200"></el-table-column> -->
                        <el-table-column prop="code" label="物料编码" width="150"></el-table-column>
                        <el-table-column prop="name" label="名称" width="150"></el-table-column>
                        <el-table-column prop="type" label="类型" width="150"></el-table-column>
                        <el-table-column prop="properties" label="其他属性" width="200"></el-table-column>
                        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                            <template slot="header" slot-scope="scope">
                                <el-input v-model="search" size="mini" placeholder="输入名称关键字搜索" />
                            </template>
                            <template slot-scope="scope">
                                <el-button size="mini" type="text" icon="el-icon-edit"
                                    @click="handleChooseSysBomToForm(scope.row)">选择</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-col>
            </el-row>
        </el-dialog>

        <!-- 用于属性输入 -->
        <el-dialog :title="propertiesTitle" :visible.sync="openInputProperties" width="600px" append-to-body>
            <el-form label-width="100px">
                <div v-for="(val, i) in  propertyJsonArray " :key="i">
                    <el-row>
                        <el-form-item :label="'属性' + (i + 1) + ':'">
                            <!-- <el-input v-model="propertyJsonArray[i].key" placeholder="请输入属性名" /> -->
                            <el-select v-model="propertyJsonArray[i].key" filterable placeholder="请选择属性名"
                                @change="$forceUpdate()">
                                <el-option v-for="item in attributesOptions" :key="item.value" :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item :label="'属性值' + (i + 1) + ':'">
                            <el-input v-model="propertyJsonArray[i].value" placeholder="请输入属性值" />
                        </el-form-item>
                    </el-row>
                </div>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="success" @click="handleAddProperties">新增属性</el-button>
                <el-button type="primary" @click="submitProperties">确 定</el-button>
                <el-button @click="cancelProperties">取 消</el-button>
            </div>
        </el-dialog>

        <el-dialog title="所处BOM成品" :visible.sync="finishedOpen" width="1200px" append-to-body>
            <el-table :data="finishedBomList" row-key="id" :default-expand-all="isExpandAll"
                :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">

                <el-table-column prop="id" label="ID" width="120"></el-table-column>
                <el-table-column prop="parentId" label="父节点ID" width="120"></el-table-column>
                <el-table-column prop="code" label="BOM编码" width="120"></el-table-column>
                <el-table-column prop="parentCode" label="父级BOM编码" width="120"></el-table-column>
                <el-table-column prop="name" label="名称" width="200"></el-table-column>
                <el-table-column prop="type" label="类型" width="200"></el-table-column>
                <el-table-column prop="properties" label="其他属性" width="200"></el-table-column>
                <el-table-column prop="process" label="所用工艺" width="200"></el-table-column>
                <el-table-column prop="processProperties" label="工艺属性" width="200"></el-table-column>
                <el-table-column prop="count" label="数量" width="100"></el-table-column>
            </el-table>
        </el-dialog>

    </div>
</template>

<script>
import { getRecommendById, updateRecommend, doInput, getInput, getStructureRecommend, updateRecommendResult, getRecommendFinishedProduct, sendRecommendResultReturnToKG } from "../../api/recommend.js";
import { getAllBom } from "../../api/knowledgeGraph"
import { getAllAttributes } from "../../api/attributes.js"
import { ElMapExportTable } from "table-excel";
import * as echarts from 'echarts';
import { v4 as uuidv4 } from 'uuid';
import SnowflakeId from "snowflake-id";


export default {
    name: "",
    data: function () {
        return {
            // 遮罩层
            loading: true,
            // 表格树数据
            inputTreeData: [],
            // 弹出层标题
            title: "",
            // 是否显示弹出层
            open: false,
            openSysBom: false,
            // 是否展开，默认全部展开
            isExpandAll: true,
            // 重新渲染表格状态
            refreshTable: true,
            // 表单
            form: {},
            QForm: {},
            // 表单校验
            rules: {
                name: [
                    { required: true, message: "BOM名称不能为空", trigger: "blur" }
                ],
                type: [
                    { required: true, message: "BOM类型不能为空", trigger: "change" }
                ],
                properties: [
                    { required: true, message: "其他属性不能为空", trigger: "blur" }
                ],
                process: [
                    { required: true, message: "所用工艺不能为空", trigger: "blur" }
                ],
                processProperties: [
                    { required: true, message: "工艺属性不能为空", trigger: "blur" }
                ],
                count: [
                    { required: true, message: "所需数量不能为空", trigger: "blur" }
                ],
                code: [
                    { required: true, message: "BOM编码不能为空", trigger: "blur" }
                ]
            },
            // 弹窗状态 true为新增 false为修改
            editOrAdd: true,
            recommendId: undefined,
            recommendName: "",
            // recommendOldName: "",
            // id自增
            startId: undefined,
            // 节点个数
            userInputNodeCount: undefined,
            // 删除个数
            deleteCount: 1,
            bomTypeOptions: [
                {
                    value: "成品（产品）",
                    label: "成品（产品）"
                }, {
                    value: "半成品（中间产品）",
                    label: "半成品（中间产品）"
                }, {
                    value: "零部件（原材料）",
                    label: "零部件（原材料）"
                }
            ],
            processOptions: [
                {
                    value: "装配",
                    label: "装配"
                }, {
                    value: "组装",
                    label: "组装"
                }, {
                    value: "焊接",
                    label: "焊接"
                }, {
                    value: "连接",
                    label: "连接"
                }, {
                    value: "沾合",
                    label: "沾合"
                }, {
                    value: "切割",
                    label: "切割"
                }, {
                    value: "铸造",
                    label: "铸造"
                }, {
                    value: "涂装",
                    label: "涂装"
                }, {
                    value: "塑性",
                    label: "塑性"
                },
            ],
            bomNodeList: [],
            relationshipList: [],
            defaultProps: {
                children: "children",
                label: "label",
            },
            sysBomDataType: [],
            currentProductType: "All",
            sysBomList: [],
            sysBomFilterList: [],
            search: "",
            // 推荐结果
            allRecommendResult: [],
            allRecommendList: [],

            currentRecommendList: [],
            recommendNum: 0,
            // 是否点击过推荐
            isClickRecommend: false,
            propertiesTitle: "",
            // 属性输入框开关
            openInputProperties: false,
            // 属性
            propertyJsonArray: [],
            // 判断编辑哪个属性 工艺还是其他
            is: true,
            // 属性输入是在编辑结果还是用户输入
            isInput: true,
            // 编辑推荐结果标题
            resultTitle: "",
            // 推荐结果新增还是修改
            resultEditOrAdd: true,
            // 推荐结果表单
            resultForm: {},
            QResultForm: {},
            editResultOpen: false,
            finishedOpen: false,
            finishedBomList: [],
            allAttributes: [],
            attributesOptions: []
        };
    },
    created() {
    },
    mounted() {
        this.recommendId = this.$route.query.id;
        this.QForm.id = this.recommendId;
        this.QResultForm.id = this.recommendId;
        this.getList();
        this.getAllAttributesOptions();
    },
    methods: {
        getList() {
            this.loading = true;
            getRecommendById(this.recommendId).then((res) => {
                this.recommendName = res.data.recommendName;
                // 防止推荐名称为空
                // this.recommendOldName = res.data.recommendName;
                this.inputTreeData = JSON.parse(res.data.userInputStructure);
                this.currentRecommendList = JSON.parse(res.data.recommendResult);
                this.startId = res.data.startId;
                this.userInputNodeCount = res.data.userInputNodeCount;
                this.loading = false;
                this.getUserInputBom()
            })
        },
        onInputSubmit() {
            doInput(this.recommendId).then((res) => {
                this.$message({
                    type: 'success',
                    message: '输入成功'
                });
                this.getUserInputBom();
                this.isClickRecommend = false;
                this.recommendNum = 1;
            });
        },
        onRecommendSubmit() {
            getStructureRecommend(this.recommendId).then((res) => {
                if (res.data.length == 0) {
                    this.$message({
                        type: 'info',
                        message: '暂未找到结构相似，代码待完善'
                    });
                    return;
                }
                this.allRecommendResult = [...res.data];
                this.getRecommendTreeData(this.allRecommendResult[this.recommendNum]);
                this.recommendNum++;
                this.isClickRecommend = true;
            })
        },
        nextRecommend() {
            this.getRecommendTreeData(this.allRecommendResult[this.recommendNum]);
            this.recommendNum++
        },
        getRecommendTreeData(data) {
            var demoData = {
                nodes: [],
                links: [],
            };
            // for (var i = 0; i < res.data.length; i++) {
            for (var j = 0; j < data.bomNodeList.length; j++) {
                var node = {
                    id: data.bomNodeList[j].code,
                    name: data.bomNodeList[j].name,
                };
                if (data.bomNodeList[j].type == "零部件（原材料）") {
                    node.category = 0;
                } else if (data.bomNodeList[j].type == "半成品（中间产品）") {
                    node.category = 1;
                } else if (data.bomNodeList[j].type == "成品（产品）") {
                    node.category = 2;
                }
                demoData.nodes.push(node);
            }
            for (
                var k = 0;
                k < data.relationshipList.length;
                k++
            ) {
                var line = {
                    source: data.relationshipList[k].sourceCode,
                    target: data.relationshipList[k].targetCode,
                    value: "consist_of",
                };
                demoData.links.push(line);
            }

            var chartDom = document.getElementById("recommendGraph");
            var myChart = echarts.init(chartDom);
            var option;
            // 图谱的配置项
            option = {
                // 提示框的配置
                title: {
                    text: "Recommend BOM",
                },
                tooltip: {
                    formatter: (x) => {
                        return [x.data.name]; //设置提示框的内容和格式 节点和边都显示name属性
                    },
                },
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
                                name: "零部件",
                                itemStyle: {
                                    color: "lightgreen",
                                },
                            },
                            {
                                name: "半成品",
                                itemStyle: {
                                    color: "orange",
                                },
                            },
                            {
                                name: "成品",
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

            var bomNodeMap = new Map();
            var bomNodeList = data.bomNodeList;
            var relationshipList = data.relationshipList;
            var parentCode = null;
            for (var node of bomNodeList) {
                node.children = [];
                bomNodeMap.set(node.code, node)
            }
            var relationshipMap = new Map();
            for (var relationship of relationshipList) {
                if (relationshipMap.has(relationship.targetCode)) {
                    var children = relationshipMap.get(relationship.targetCode)
                    children.push(relationship.sourceCode)
                } else {
                    var children = [];
                    children.push(relationship.sourceCode)
                    relationshipMap.set(relationship.targetCode, children)
                }
                bomNodeMap.get(relationship.sourceCode).process = relationship.type
                bomNodeMap.get(relationship.sourceCode).processProperties = relationship.properties
                bomNodeMap.get(relationship.sourceCode).count = relationship.quantity

            }
            for (let [key, value] of bomNodeMap) {
                if (relationshipMap.get(key) != undefined) {
                    for (var code of relationshipMap.get(key)) {
                        bomNodeMap.get(key).children.push(bomNodeMap.get(code))
                        bomNodeMap.get(code).parentId = bomNodeMap.get(key).id
                        bomNodeMap.get(code).parentCode = bomNodeMap.get(key).code
                    }
                }
            }
            for (let [key, value] of bomNodeMap) {
                if (value.id == data.parentId) {
                    parentCode = key;
                    break
                }
            }
            this.allRecommendList = [bomNodeMap.get(parentCode)]
        },
        getUserInputBom() {
            getInput(this.recommendId).then((res) => {
                this.bomNodeList = res.data.bomNodeList;
                this.relationshipList = res.data.relationshipList;
                if (this.bomNodeList.length == 0) {
                    this.$message({
                        message: '用户还未输入到知识图谱中',
                        type: 'warning'
                    });
                    return;
                }
                var demoData = {
                    nodes: [],
                    links: [],
                };
                for (var i = 0; i < this.bomNodeList.length; i++) {
                    var node = {
                        id: this.bomNodeList[i].inputId,
                        name: this.bomNodeList[i].name,
                    };
                    if (this.bomNodeList[i].type == "零部件（原材料）") {
                        node.category = 0;
                    } else if (
                        this.bomNodeList[i].type == "半成品（中间产品）"
                    ) {
                        node.category = 1;
                    } else if (this.bomNodeList[i].type == "成品（产品）") {
                        node.category = 2;
                    }
                    demoData.nodes.push(node);
                }
                for (
                    var j = 0;
                    j < this.relationshipList.length;
                    j++
                ) {
                    var line = {
                        source: String(this.relationshipList[j].sourceId),
                        target: String(this.relationshipList[j].targetId),
                        value: "input_consist_of",
                    };
                    demoData.links.push(line);
                }
                var chartDom = document.getElementById("graph");
                var myChart = echarts.init(chartDom);
                var option;
                // 图谱的配置项
                option = {
                    // 提示框的配置
                    title: {
                        text: "User Input BOM",
                    },
                    tooltip: {
                        formatter: (x) => {
                            return [x.data.name]; //设置提示框的内容和格式 节点和边都显示name属性
                        },
                    },
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
                                    name: "零部件",
                                    itemStyle: {
                                        color: "lightgreen",
                                    },
                                },
                                {
                                    name: "半成品",
                                    itemStyle: {
                                        color: "orange",
                                    },
                                },
                                {
                                    name: "成品",
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
            })
        },
        // onBlur() {
        //     if (this.recommendName.trim() == "" || this.recommendName.trim() == null || this.recommendName.trim() == undefined) {
        //         this.$message({
        //             message: '推荐名不得为空',
        //             type: 'warning'
        //         });
        //         this.recommendName = this.recommendOldName;
        //     }
        // },
        // 取消按钮
        cancel() {
            this.open = false;
            this.reset();
        },
        resultCancel() {
            this.editResultOpen = false;
            this.resultReset();
        },
        closeDialog() {
            this.editResultOpen = false;
            this.resultReset();
        },
        // 表单重置
        reset() {
            this.form = {
                id: undefined,
                parentId: undefined,
                name: undefined,
                type: undefined,
                properties: undefined,
                process: undefined,
                processProperties: undefined,
                count: undefined,
            };
            this.resetForm("form");
        },
        // 推荐表单重置
        resultReset() {
            this.resultForm = {
                id: undefined,
                parentId: undefined,
                name: undefined,
                type: undefined,
                properties: undefined,
                process: undefined,
                processProperties: undefined,
                count: undefined,
            };
            this.resetForm("resultForm");
        },
        /** 新增按钮操作 */
        handleAdd(row) {
            this.editOrAdd = true;
            this.isInput = true;
            this.reset();
            this.open = true;
            this.title = "添加BOM节点";
            this.form.id = this.startId;
            this.form.parentId = row.id;
            this.form.children = [];
        },
        /** 展开/折叠操作 */
        toggleExpandAll() {
            this.refreshTable = false;
            this.isExpandAll = !this.isExpandAll;
            this.$nextTick(() => {
                this.refreshTable = true;
            });
        },
        // 选择系统内部件
        handleChooseSysBom() {
            this.openSysBom = true;
            // this.open = false;
            // this.editResultOpen = false;
            this.sysBomDataType = [
                {
                    label: "成品/产品",
                    children: [],
                }, {
                    label: "半成品/中间产品",
                    children: [],
                }, {
                    label: "零部件/原材料",
                    children: [],
                }
            ]
            getAllBom().then((res) => {
                this.sysBomList = res.data;
                this.sysBomFilterList = this.sysBomList;
            })
        },
        handleNodeClick(data) {
            this.loading = true;
            this.currentProductType = data.label;
            this.sysBomFilterList = [];
            if (this.currentProductType == "成品/产品") {
                for (var j = 0; j < this.sysBomList.length; j++) {
                    if (this.sysBomList[j].type == "成品（产品）") {
                        this.sysBomFilterList.push(this.sysBomList[j]);
                    }
                }
                this.loading = false;
            } else if (this.currentProductType == "半成品/中间产品") {
                for (var j = 0; j < this.sysBomList.length; j++) {
                    if (this.sysBomList[j].type == "半成品（中间产品）") {
                        this.sysBomFilterList.push(this.sysBomList[j]);
                    }
                }
                this.loading = false;
            } else if (this.currentProductType == "零部件/原材料") {
                for (var j = 0; j < this.sysBomList.length; j++) {
                    if (this.sysBomList[j].type == "零部件（原材料）") {
                        this.sysBomFilterList.push(this.sysBomList[j]);
                    }
                }
                this.loading = false;
            }
        },
        handleChooseSysBomToForm(row) {
            if (this.isInput) {
                this.form.name = row.name;
                this.form.type = row.type;
                this.form.properties = row.properties;
                // this.open = true;
                this.openSysBom = false;
            } else {
                this.resultForm.name = row.name;
                this.resultForm.type = row.type;
                this.resultForm.properties = row.properties;
                this.resultForm.code = row.code;
                // this.editResultOpen = true;
                this.openSysBom = false;

            }

        },
        /** 修改按钮操作 */
        handleUpdate(row) {
            this.editOrAdd = false;
            this.isInput = true;
            this.reset();
            this.open = true;
            this.title = "修改BOM节点";
            this.form.id = row.id;
            this.form.parentId = row.parentId;
            this.form.name = row.name;
            this.form.type = row.type;
            this.form.properties = row.properties;
            this.form.process = row.process;
            this.form.processProperties = row.processProperties;
            this.form.count = row.count;
        },
        findAndAddById(parentId, bomList, form) {
            for (var item of bomList) {
                if (item.id === parentId) {
                    item.children.push(form);
                    return;
                }
                if (item.children && item.children.length > 0) {
                    // 递归遍历子对象
                    this.findAndAddById(parentId, item.children, form);
                }
            }
        },
        findAndUpdateById(id, bomList, modifyObj) {
            for (var i = 0; i < bomList.length; i++) {
                if (bomList[i].id === id) {
                    bomList[i].name = modifyObj.name;
                    bomList[i].type = modifyObj.type;
                    bomList[i].properties = modifyObj.properties;
                    bomList[i].process = modifyObj.process;
                    bomList[i].processProperties = modifyObj.processProperties;
                    bomList[i].count = modifyObj.count;
                    return;
                }
                if (bomList[i].children && bomList[i].children.length > 0) {
                    this.findAndUpdateById(id, bomList[i].children, modifyObj);
                }
            }
        },
        /** 提交按钮 */
        submitForm() {
            this.isClickRecommend = false;
            this.recommendNum = 1;
            this.$refs["form"].validate(valid => {
                if (valid) {
                    if (this.editOrAdd) {
                        // 如果是新增
                        this.findAndAddById(this.form.parentId, this.inputTreeData, this.form);
                        // this.QForm.recommendName = this.recommendName;
                        this.QForm.userInputStructure = JSON.stringify(this.inputTreeData);
                        // 节点数加1 id加1
                        this.QForm.userInputNodeCount = this.userInputNodeCount + 1;
                        this.QForm.startId = this.startId + 1;
                        updateRecommend(this.QForm).then((res) => {
                            this.open = false;
                            this.getList();
                            this.$message({
                                type: 'success',
                                message: '新增成功'
                            });
                        })
                    } else {
                        // 如果是修改
                        this.findAndUpdateById(this.form.id, this.inputTreeData, this.form);
                        // this.QForm.recommendName = this.recommendName;
                        this.QForm.userInputStructure = JSON.stringify(this.inputTreeData);
                        this.QForm.userInputNodeCount = this.userInputNodeCount;
                        this.QForm.startId = this.startId;
                        updateRecommend(this.QForm).then((res) => {
                            this.open = false;
                            this.getList();
                            this.$message({
                                type: 'success',
                                message: '更新成功'
                            });
                        });
                    }
                }
            });
        },
        /** 删除按钮操作 */
        handleDelete(row) {
            this.$confirm('是否确认删除该节点？注意将同时删除该节点下所有子节点').then(() => {
                this.deleteCount = 1;
                this.findAndDeleteById(row.id, this.inputTreeData);
                // this.QForm.recommendName = this.recommendName;
                this.QForm.userInputStructure = JSON.stringify(this.inputTreeData);
                // 节点数减1 id不变
                this.QForm.userInputNodeCount = this.userInputNodeCount - this.deleteCount;
                this.QForm.startId = this.startId;
                updateRecommend(this.QForm).then(() => {
                    this.getList();
                    this.$message({
                        type: 'success',
                        message: '删除成功'
                    });
                })
            }).catch(() => {
                console.log("取消删除");
            })
        },
        findAndDeleteById(id, bomList) {
            for (var i = 0; i < bomList.length; i++) {
                if (bomList[i].id === id) {
                    // 先查找到这个节点下有多少个节点
                    if (this.isInput) {
                        this.getDeleteCount(bomList[i]);
                    }
                    bomList.splice(i, 1);
                    return;
                }
                if (bomList[i].children && bomList[i].children.length > 0) {
                    this.findAndDeleteById(id, bomList[i].children);
                }
            }
        },
        getDeleteCount(bom) {
            if (bom.children && bom.children.length > 0) {
                this.deleteCount += bom.children.length;
                for (var i = 0; i < bom.children.length; i++) {
                    this.getDeleteCount(bom.children[i]);
                }
            } else {
                return;
            }
        },
        backToRecommendList() {
            this.$router.push("/recommendList");
        },
        handleInputProperties(properties, is) {
            this.openInputProperties = true;
            this.is = is;
            this.attributesOptions = [];
            if (is) {
                this.propertiesTitle = "编辑其他属性"
                for (var i = 0; i < this.allAttributes.length; i++) {
                    if (this.allAttributes[i].biztype == "BOM属性") {
                        this.attributesOptions.push({
                            value: this.allAttributes[i].name,
                            label: this.allAttributes[i].aliasName
                        })
                    }
                }
            } else {
                this.propertiesTitle = "编辑工艺属性"
                for (var i = 0; i < this.allAttributes.length; i++) {
                    if (this.allAttributes[i].biztype == "工艺属性") {
                        this.attributesOptions.push({
                            value: this.allAttributes[i].name,
                            label: this.allAttributes[i].aliasName
                        })
                    }
                }

            }
            var propertiesObj = JSON.parse(properties);
            for (var key in propertiesObj) {
                this.propertyJsonArray.push({
                    key,
                    value: propertiesObj[key]
                })
            }
        },
        submitProperties() {
            this.openInputProperties = false;
            var jsonObj = {}
            for (var item of this.propertyJsonArray) {
                jsonObj[item.key] = item.value;
            }
            if (this.is) {
                if (this.isInput) {
                    this.form.properties = JSON.stringify(jsonObj)
                } else {
                    this.resultForm.properties = JSON.stringify(jsonObj)
                }
            } else {
                if (this.isInput) {
                    this.form.processProperties = JSON.stringify(jsonObj)
                } else {
                    this.resultForm.processProperties = JSON.stringify(jsonObj)
                }
            }
            this.propertyJsonArray = []
        },
        handleAddProperties() {
            this.propertyJsonArray.push({
                key: "",
                value: ""
            })
        },
        cancelProperties() {
            this.openInputProperties = false;
            this.propertyJsonArray = []
        },
        // 更新推荐结果的节点
        handleRecommendUpdate(row) {
            this.resultEditOrAdd = false;
            this.isInput = false;
            this.resultReset();
            this.editResultOpen = true;
            this.resultTitle = "推荐结果：修改BOM节点";
            this.resultForm.id = row.id;
            this.resultForm.parentId = row.parentId;
            this.resultForm.parentCode = row.parentCode;
            this.resultForm.code = row.code;
            this.resultForm.name = row.name;
            this.resultForm.type = row.type;
            this.resultForm.properties = row.properties;
            this.resultForm.process = row.process;
            this.resultForm.processProperties = row.processProperties;
            this.resultForm.count = row.count;
        },
        // 新增推荐结果的节点
        handleRecommendAdd(row) {
            this.resultEditOrAdd = true;
            this.isInput = false;
            this.resultReset();
            this.editResultOpen = true;
            this.resultTitle = "推荐结果：添加BOM节点";
            const guid = num => {
                const snowflake = new SnowflakeId();
                let arr = [];
                for (let i = 0; i < num; i++) {
                    arr.push(snowflake.generate());
                }
                return num ? arr : snowflake.generate();
            };
            this.resultForm.id = guid();
            this.resultForm.parentId = row.id;
            this.resultForm.parentCode = row.code;
            this.resultForm.children = [];
        },
        handleRecommendDelete(row) {
            this.$confirm('是否确认删除该节点？注意将同时删除该节点下所有子节点').then(() => {
                this.findAndDeleteById(row.id, this.currentRecommendList);
                this.QResultForm.recommendResult = JSON.stringify(this.currentRecommendList);
                updateRecommendResult(this.QResultForm).then(() => {
                    this.getList();
                    this.$message({
                        type: 'success',
                        message: '删除成功'
                    });
                })
            }).catch(() => {
                console.log("取消删除");
            })
        },
        submitResultForm() {
            this.$refs["resultForm"].validate(valid => {
                if (valid) {
                    if (this.resultEditOrAdd) {
                        this.findAndAddById(this.resultForm.parentId, this.currentRecommendList, this.resultForm);
                        this.QResultForm.recommendResult = JSON.stringify(this.currentRecommendList);
                        updateRecommendResult(this.QResultForm).then((res) => {
                            this.editResultOpen = false;
                            this.getList();
                            this.$message({
                                type: 'success',
                                message: '新增成功'
                            });
                        })
                    } else {
                        this.findAndUpdateById(this.resultForm.id, this.currentRecommendList, this.resultForm);
                        this.findAndUpdateCode(this.resultForm.id, this.currentRecommendList, this.resultForm)
                        this.QResultForm.recommendResult = JSON.stringify(this.currentRecommendList);
                        updateRecommendResult(this.QResultForm).then((res) => {
                            this.editResultOpen = false;
                            this.getList();
                            this.$message({
                                type: 'success',
                                message: '修改成功'
                            });
                        })
                    }
                }
            });
        },
        findAndUpdateCode(id, bomList, modifyObj) {
            // 修改孩子的父级编码
            for (var i = 0; i < bomList.length; i++) {
                if (bomList[i].id === id) {
                    bomList[i].code = modifyObj.code;
                    for (var j = 0; j < bomList[i].children.length; j++) {
                        bomList[i].children[j].parentCode = modifyObj.code;
                    }
                    return;
                }
                if (bomList[i].children && bomList[i].children.length > 0) {
                    this.findAndUpdateCode(id, bomList[i].children, modifyObj);
                }
            }
        },
        // 将推荐结果放到已存在的推荐结果中进行编辑
        setRecommendResult() {
            this.$confirm('将会用当前推荐结果替换已存在的推荐结果，之后可以进行编辑，是否确认？').then(() => {
                this.QResultForm.recommendResult = JSON.stringify(this.allRecommendList);
                updateRecommendResult(this.QResultForm).then(() => {
                    this.getList();
                    this.$message({
                        type: 'success',
                        message: '替换成功'
                    });
                })
            }).catch(() => {
                console.log("取消替换");
            })
        },
        queryFinishedBom() {
            getRecommendFinishedProduct(this.allRecommendList[0].code).then((res) => {
                this.finishedOpen = true;
                this.finishedBomList = [JSON.parse(res.data)]
            })
        },
        recommendResultReturnToKG() {
            if (this.currentRecommendList.length == 0) {
                this.$message({
                    type: 'error',
                    message: '列表为空'
                });
            } else {
                sendRecommendResultReturnToKG(this.recommendId).then((res) => {
                    this.$message({
                        type: 'success',
                        message: '补充成功'
                    });
                })
            }
        },
        getAllAttributesOptions() {
            getAllAttributes().then((res) => {
                this.allAttributes = res.data;
            })
        },
        // 导出excel
        exportExcel(bomList) {
            let column = [];
            column.push({
                'title': "物料编码",
                'dataIndex': "code",
            });
            column.push({
                'title': "父级物料编码",
                'dataIndex': "parentCode",
            })
            column.push({
                'title': "名称",
                'dataIndex': "name",
            })
            column.push({
                'title': "类型",
                'dataIndex': "type",
            })
            column.push({
                'title': "其他属性",
                'dataIndex': "properties",
            })
            column.push({
                'title': "所用工艺",
                'dataIndex': "process",
            })
            column.push({
                'title': "工艺属性",
                'dataIndex': "processProperties",
            })
            column.push({
                'title': "数量",
                'dataIndex': "count",
            })
            //声明数据数组（二维），
            const data = [];
            this.putData(data, this.currentRecommendList);
            
            const table = [];
            table.push({
                'column': column,
                'data': data,
                'sheetName': "sheet1",
            })
            const instance = new ElMapExportTable(
                table
            );
            instance.download(this.recommendName + '-BOM推荐结果');
        },
        putData(data, bomList) {
            for (var i = 0; i < bomList.length; i++) {
                data.push({
                    "code":bomList[i].code,
                    "parentCode": bomList[i].parentCode,
                    "name": bomList[i].name,
                    "type": bomList[i].type,
                    "properties": bomList[i].properties,
                    "process": bomList[i].process,
                    "processProperties": bomList[i].processProperties,
                    "count": bomList[i].count,
                });
                if (bomList[i].children && bomList[i].children.length > 0) {
                    this.putData(data, bomList[i].children);
                }
            }
        }
    },
};
</script>

<style scoped></style>