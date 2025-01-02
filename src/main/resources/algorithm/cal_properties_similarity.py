import json
import jieba
import numpy as np
from sklearn.feature_extraction.text import CountVectorizer


# 计算欧几里得距离
def euclidean_distance(p1, p2):
    return np.linalg.norm(p1 - p2)


# 文本数据进行分词得到向量
def text_similarity_cal(t1, t2):
    # 使用jieba进行中文分词
    text1_cut = " ".join(jieba.lcut(t1))
    text2_cut = " ".join(jieba.lcut(t2))
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
    print("词汇表:", vocab)
    print("文本向量:", vectorized_texts)
    return vectorized_texts[0], vectorized_texts[1]


# 计算余弦相似度
def cosine_similarity(vec1, vec2):
    # return 1 - distance.cosine(vec1, vec2)
    return np.array(vec1).dot(np.array(vec2)) / (np.linalg.norm(np.array(vec1)) * np.linalg.norm(np.array(vec2)))


# 计算两个节点属性之间的相似度
def node_properties_similarity(json_str1, json_str2):
    # 属性列表
    properties = []
    # 统计1里的key
    json_properties1 = json.loads(json_str1)
    for key in json_properties1:
        if key not in properties:
            properties.append(key)
    # 统计2里的key
    json_properties2 = json.loads(json_str2)
    for key in json_properties2:
        if key not in properties:
            properties.append(key)
    # 声明两个点用于计算欧几里得距离
    vector_node1 = []
    vector_node2 = []
    # 声明两个字符串数组
    text_list1 = []
    text_list2 = []
    # 属性构建
    for key in properties:
        if (key in json_properties1) and (key in json_properties2):
            if type(json_properties1[key]) == str or type(json_properties2[key]) == str:
                text_list1.append(json_properties1[key])
                text_list2.append(json_properties2[key])
            else:
                vector_node1.append(json_properties1[key])
                vector_node2.append(json_properties2[key])
        else:
            continue
    text1 = " ".join(text_list1)
    text2 = " ".join(text_list2)
    point1 = np.array(vector_node1)
    point2 = np.array(vector_node2)
    distance1 = euclidean_distance(point1, point2)
    print("euclidean_distance:" + str(distance1))
    text_vec1, text_vec2 = text_similarity_cal(text1, text2)
    distance2 = cosine_similarity(text_vec1, text_vec2)
    print("cosine_similarity:" + str(distance2))
    return distance1, distance2


def process_similarity_cal(process1, process2):
    return 0
