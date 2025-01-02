<template>
	<div>
		<div style="width: 100%;">
			<el-button style="margin-right: 20px; float: right;" type="info" size="small"
				@click="showKnowledgeGraph">可视化查看知识图谱</el-button>
			<el-upload class="upload-demo" action="/api/bom/upload" :file-list="fileList">
				<el-button size="small" type="primary">点击上传</el-button>
				<div slot="tip" class="el-upload__tip">支持通过csv，xlsx文件进行图谱的更新</div>
			</el-upload>
		</div>
		<div style="margin-top: 10px;">
			<el-row :gutter="20">
				<el-col :span="6">
					<div class="classfication">
						<el-tree highlight-current :data="treeData" :props="defaultProps" @node-click="handleNodeClick"
							node-key="label" :default-expanded-keys="['成品/产品']"></el-tree>
					</div>
				</el-col>
				<el-col :span="18">

					<el-tag size="medium">{{ currentProduct }}</el-tag>
					<el-button style="margin-right: 20px; float: right;" type="info" plain icon="el-icon-sort"
						size="mini" @click="toggleExpandAll">展开/折叠</el-button>
					<el-table v-if="refreshTable" v-loading="loading" :data="productTreeData" row-key="id"
						:default-expand-all="isExpandAll"
						:tree-props="{ children: 'children', hasChildren: 'hasChildren' }">

						<!-- <el-table-column prop="id" label="ID" width="200"></el-table-column> -->
						<el-table-column prop="code" label="物料编码" width="200"></el-table-column>
						<el-table-column prop="name" label="名称" width="200"></el-table-column>
						<el-table-column prop="type" label="类型" width="200"></el-table-column>
						<el-table-column prop="properties" label="其他属性" width="260"></el-table-column>
						<!-- <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
                <el-button size="mini" type="text" icon="el-icon-plus" @click="handleAdd(scope.row)">新增</el-button> -->
						<!-- 产品根节点不允许删除-->
						<!-- <el-button v-if="scope.row.parentId != 0" size="mini" type="text" icon="el-icon-delete"
                  @click="handleDelete(scope.row)">删除</el-button>
              </template>
</el-table-column> -->
					</el-table>
				</el-col>
			</el-row>
		</div>
	</div>
</template>

<script>
import { getAllBom, getFinishedProductByCode } from "../../api/knowledgeGraph"
export default {
	name: "",
	data: function () {
		return {
			fileList: [],
			treeData: [],
			// 重新渲染表格状态
			refreshTable: true,
			// 遮罩层
			loading: false,
			// 是否展开，默认全部展开
			isExpandAll: true,
			defaultProps: {
				children: "children",
				label: "label",
			},
			currentProduct: "成品/产品",
			productTreeData: [],
			bomList: []
		};
	},
	created() {
	},
	mounted() {
		this.getTreeData();
	},
	methods: {
		showKnowledgeGraph() {
			this.$router.push("/knowledgeGraph");
		},
		getTreeData() {
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
				this.bomList = res.data;
				this.productTreeData = [];
				for (var j = 0; j < res.data.length; j++) {
					if (res.data[j].type == "成品（产品）") {
						this.productTreeData.push(res.data[j]);
					}
				}
			})
		},
		handleNodeClick(data) {
			this.loading = true;
			this.currentProduct = data.label;
			this.productTreeData = [];
			if (this.currentProduct == "成品/产品") {
				for (var j = 0; j < this.bomList.length; j++) {
					if (this.bomList[j].type == "成品（产品）") {
						this.productTreeData.push(this.bomList[j]);
					}
				}
				this.loading = false;
			} else if (this.currentProduct == "半成品/中间产品") {
				for (var j = 0; j < this.bomList.length; j++) {
					if (this.bomList[j].type == "半成品（中间产品）") {
						this.productTreeData.push(this.bomList[j]);
					}
				}
				this.loading = false;
			} else if (this.currentProduct == "零部件/原材料") {
				for (var j = 0; j < this.bomList.length; j++) {
					if (this.bomList[j].type == "零部件（原材料）") {
						this.productTreeData.push(this.bomList[j]);
					}
				}
				this.loading = false;
			} else {
				getFinishedProductByCode(this.currentProduct.split("_")[0]).then((res) => {
					this.productTreeData.push(JSON.parse(res.data))
					this.loading = false;
				})
			}


		},
		toggleExpandAll() {
			this.refreshTable = false;
			this.isExpandAll = !this.isExpandAll;
			this.$nextTick(() => {
				this.refreshTable = true;
			});
		},
	},
};
</script>

<style scoped></style>