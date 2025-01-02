import jieba
from sklearn.feature_extraction.text import CountVectorizer

# 中文文本示例
text1 = "我爱你，机器，学习，规格好大，尺度15"
text2 = "我喜欢，学习，关于，机器，的知识"

# 使用jieba进行中文分词
text1_cut = " ".join(jieba.lcut(text1))
text2_cut = " ".join(jieba.lcut(text2))

# 创建一个CountVectorizer实例
vectorizer = CountVectorizer()

# 将分词后的文本列表转化为词袋模型
texts = [text1_cut, text2_cut]
X = vectorizer.fit_transform(texts)

# 转换后的稀疏矩阵转化为数组
vectorized_texts = X.toarray()

# 显示词汇表
vocab = vectorizer.get_feature_names_out()

# 输出结果
print("Vocabulary:", vocab)
print("Vectorized Texts:")
print(vectorized_texts)

graph = {
    "nodes": {
        1: {"id": 1, "name": "成品", "type": "成品（产品）"},
        2: {"id": 2, "name": "半成品", "type": "半成品（中间产品）"},
        3: {"id": 3, "name": "零部件", "type": "零部件（原材料）"},
        4: {"id": 4, "name": "曲轴", "type": "零部件（原材料）"},
        5: {"id": 5, "name": "Wi-Fi模块", "type": "零部件（原材料）"}
    },
    "edges": [
        {"id": 1, "source": 4, "target": 2, "name": "连接"},
        {"id": 2, "source": 3, "target": 2, "name": "组装"},
        {"id": 3, "source": 2, "target": 1, "name": "焊接"},
        {"id": 4, "source": 5, "target": 1, "name": "组装"}
    ]
}

def prune_tree(graph):
    leaves = [node["id"] for node in graph["nodes"].values() if not any(edge["source"] == node["id"] for edge in graph["edges"])]

    while leaves:
        for leaf_id in leaves:
            graph["nodes"].pop(leaf_id)

            edges_to_remove = [edge for edge in graph["edges"] if edge["source"] == leaf_id or edge["target"] == leaf_id]
            for edge in edges_to_remove:
                graph["edges"].remove(edge)

        leaves = [node["id"] for node in graph["nodes"].values() if not any(edge["source"] == node["id"] for edge in graph["edges"])]

    return graph

pruned_graph = prune_tree(graph)
print(pruned_graph)