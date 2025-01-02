import networkx as nx
import numpy as np
from cal_properties_similarity import node_properties_similarity

# 用于子图同构计算
# kg
nodeList = []
relationshipList = []
target_graph = nx.DiGraph()
# 查询图
queryNodeList = []
queryRelationshipList = []
query_graph = nx.DiGraph()
# 存储节点、边及其属性-用于属性相似推荐
# kg
bomNodeMap = {}
bomRelationshipList = []
# 查询图
queryBomNodeMap = {}
queryBomRelationshipList = []
# 计算列表
cal_similarity_list = []
# 图 用于剪枝
graph = {}
graph["nodes"] = {}
graph["edges"] = []


class BomNode:
    def __init__(self, id, name, type, properties):
        self.id = id
        self.name = name
        self.type = type
        self.properties = properties


class BomNodeRelationship:
    def __init__(self, source, target, type, quantity, properties):
        self.source = source
        self.target = target
        self.type = type
        self.quantity = quantity
        self.properties = properties


class SimilarityCal:
    def __init__(self, mapping):
        # 一个映射
        self.mapping = mapping
        # 总相似度
        self.similarity = 0
        # 每个节点之间余弦相似度数组
        self.c_similarity = []
        # 每个节点之间欧几里得相似度数组
        self.e_similarity = []

    def cal_final_similarity(self):
        euclidean_distances = np.array(self.e_similarity)
        # 归一化处理 0-1
        for index in range(len(self.e_similarity)):
            self.e_similarity[index] = (self.e_similarity[index] - np.min(euclidean_distances)) / (
                    np.max(euclidean_distances) - np.min(euclidean_distances))
        # 权重 计算组合相似度
        w1 = 0.5
        w2 = 0.5
        temp_similarity = []
        for index in range(len(self.e_similarity)):
            temp_similarity.append(w1 * self.c_similarity[index] + w2 * (1 - self.e_similarity[index]))
        self.similarity = np.mean(temp_similarity)

    def cal_node_properties_similarity(self):
        for key, value in self.mapping.items():
            e_similarity, c_similarity = node_properties_similarity(bomNodeMap[key].properties,
                                                                    queryBomNodeMap[key].properties)
            self.e_similarity.append(e_similarity)
            self.c_similarity.append(c_similarity)


# 创建查询图和知识图谱
def create_query_and_kg(recommendName):
    # 创建节点和关系 存储点和边的属性
    with open('D:\\GitStorage\\knowledge-graph\\src\\main\\resources\\static\\graphDB\\KnowledgeGraph.data', 'r',
              encoding='utf-8') as f:
        for line in f.readlines():
            if 't' in line:
                continue
            if 'v' in line:
                v_parts = line.split("|")
                nodeList.append(int(v_parts[1]))
                bomNodeMap[int(v_parts[1])] = BomNode(int(v_parts[1]), v_parts[2], v_parts[3], v_parts[4])
            if 'e' in line:
                e_parts = line.split("|")
                relationshipList.append((int(e_parts[1]), int(e_parts[2])))
                bomRelationshipList.append(
                    BomNodeRelationship(e_parts[1], e_parts[2], e_parts[3], e_parts[4], e_parts[5]))

    target_graph.add_nodes_from(nodeList)
    target_graph.add_edges_from(relationshipList)

    # 创建节点和关系 存储点和边的属性
    with open('D:\\GitStorage\\knowledge-graph\\src\\main\\resources\\static\\graphDB\\' + recommendName + '.input',
              'r', encoding='utf-8') as f:
        for line in f.readlines():
            if 't' in line:
                continue
            if 'v' in line:
                v_parts = line.split("|")
                queryNodeList.append(int(v_parts[1]))
                queryBomNodeMap[int(v_parts[1])] = BomNode(int(v_parts[1]), v_parts[2], v_parts[3], v_parts[4])
                graph["nodes"][int(v_parts[1])] = BomNode(int(v_parts[1]), v_parts[2], v_parts[3], v_parts[4])
            if 'e' in line:
                e_parts = line.split("|")
                queryRelationshipList.append((int(e_parts[1]), int(e_parts[2])))
                queryBomRelationshipList.append(
                    BomNodeRelationship(e_parts[1], e_parts[2], e_parts[3], e_parts[4], e_parts[5]))
                graph["edges"].append(BomNodeRelationship(e_parts[1], e_parts[2], e_parts[3], e_parts[4], e_parts[5]))

    query_graph.add_nodes_from(queryNodeList)
    query_graph.add_edges_from(queryRelationshipList)
    print(graph)


def VF2_algorithm():
    # 调用VF2算法进行匹配
    matcher = nx.algorithms.isomorphism.DiGraphMatcher(target_graph, query_graph)
    matches = [match for match in matcher.subgraph_isomorphisms_iter()]
    # 输出匹配结果
    print("子图同构结果数:" + str(len(matches)))
    if len(matches) == 0:
        pruning()
        VF2_algorithm()
    else:
        count = 0
        for match in matches:
            if count == 100:
                break
            else:
                print(count)
                print(match)
                count = count + 1
                cal_similarity_list.append(SimilarityCal(match))
        first_100_matches = matches[:100]
        return first_100_matches


# 剪枝算法
def pruning():
    # 进行剪枝操作 直到找到匹配结果
    # 先找出nodes中所有的key
    keys = graph["nodes"].keys()
    target_nodes_id = set()
    for index in range(len(graph["edges"])):
        target_nodes_id.add(graph["edges"][index].target)
    # 要移除的点的id
    remove_id = None
    for index in range(len(keys)):
        if keys[index] not in target_nodes_id:
            remove_id = keys[index]
            break
    del graph["nodes"][remove_id]
    for index in range(len(graph["edges"])):
        if graph["edges"][index].source == remove_id:
            graph["edges"].pop(index)
            break
    return []


# 计算topN
def cal_topN(n):
    for item in cal_similarity_list:
        item.cal_node_properties_similarity()
        item.cal_final_similarity()
    cal_similarity_list.sort(key=lambda x: x.similarity, reverse=True)
    return cal_similarity_list[:n]


# BOM推荐算法
def bom_recommendation_algorithm(recommendName):
    create_query_and_kg(recommendName)
    VF2_algorithm()
    cal_topN(5)
