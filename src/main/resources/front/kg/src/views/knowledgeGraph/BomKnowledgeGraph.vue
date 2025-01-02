<template>
    <div>
        <el-row>
            <el-col :span="4">
                <!-- <div><el-link type="success" style="font-size: 16px;">成品列表</el-link></div> -->
                <div>
                    <el-link type="success" style="font-size: 16px;">
                        <el-button style="margin-right: 20px; margin-bottom: 20px;float: left; margin-top: 10px;" type="info" size="mini"
                            @click="backToList">
                            返回图谱管理
                        </el-button>
                    </el-link>
                </div>
                <el-tree highlight-current :data="treeData" :props="defaultProps" @node-click="handleNodeClick"
                    node-key="label" :default-expanded-keys="['成品/产品']"></el-tree>
            </el-col>
            <el-col :span="20">
                <div id="graph"
                    style="width: 100%;height: 800px;margin-top: 20px; border-radius: 10px; border: 1px solid #ccc;">
                </div>
            </el-col>
        </el-row>



    </div>
</template>

<script>
import * as echarts from 'echarts';
import { renderBomMap, getAllBom, getBomNodeAndRelationship } from "../../api/knowledgeGraph"
export default {
    name: "BomKnowledgeGraph",
    data: function () {
        return {
            treeData: [],
            defaultProps: {
                children: "children",
                label: "label",
            },
            currentProduct: null
        };
    },
    created() {
    },
    mounted() {
        this.initShow()
    },
    methods: {
        initShow() {
            getAllBom().then((res) => {
                this.treeData = [
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
                for (var i = 0; i < res.data.length; i++) {
                    if (res.data[i].type == "成品（产品）") {
                        this.treeData[0].children.push({
                            label: res.data[i].code + "_" + res.data[i].name,
                            children: [],
                        })
                    } else if (res.data[i].type == "半成品（中间产品）") {
                        this.treeData[1].children.push({
                            label: res.data[i].code + "_" + res.data[i].name,
                            children: [],
                        })
                    } else if (res.data[i].type == "零部件（原材料）") {
                        this.treeData[2].children.push({
                            label: res.data[i].code + "_" + res.data[i].name,
                            children: [],
                        })
                    }
                }
                renderBomMap().then((res) => {
                    this.showRelationGraph(res.data.bomNodeList, res.data.relationshipList);
                });
            })
        },
        showRelationGraph(bomNodeList, relationshipList) {
            var chartDom = document.getElementById("graph");
            var myChart = echarts.init(chartDom);
            myChart.showLoading();
            var demoData = {
                nodes: [],
                links: [],
            };
            for (var i = 0; i < bomNodeList.length; i++) {
                var node = {
                    id: bomNodeList[i].code,
                    name: bomNodeList[i].name,
                };
                if (bomNodeList[i].type == "零部件（原材料）") {
                    node.category = 0;
                } else if (
                    bomNodeList[i].type == "半成品（中间产品）"
                ) {
                    node.category = 1;
                } else if (bomNodeList[i].type == "成品（产品）") {
                    node.category = 2;
                }
                demoData.nodes.push(node);
            }
            for (
                var j = 0;
                j < relationshipList.length;
                j++
            ) {
                var line = {
                    source: relationshipList[j].sourceCode,
                    target: relationshipList[j].targetCode,
                    value: "consist_of",
                    processName: relationshipList[j].type
                };
                demoData.links.push(line);
            }

            var option;
            // 图谱的配置项
            option = {
                // 提示框的配置
                title: {
                    text: "Industrial BOM Knowledge Graph",
                },
                tooltip: {
                    formatter: (x) => {
                        if (x.dataType === 'edge') {
                            return x.data.source + " " + x.data.processName + " " + x.data.target;
                        }
                        return x.data.name;
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
                        // draggable: true, //指示节点是否可以拖动
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
            myChart.hideLoading();
        },
        backToList() {
            this.$router.push("/importToKG");
        },
        handleNodeClick(data) {
            this.currentProduct = data.label;
            getBomNodeAndRelationship(this.currentProduct.split("_")[0]).then((res) => {
                this.showRelationGraph(res.data.bomNodeList, res.data.relationshipList);
            })
        }
    },
};
</script>

<style scoped></style>