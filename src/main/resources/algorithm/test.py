import json
import numpy as np

# 给定的两个JSON字符串
json_str1 = {
    "Threaded/Non-threaded": "1122",
    "Male/Female": "323",
    "Screw": "231",
    "Board spacing height": "3243",
    "Total length": "342",
    "Characteristics": "蓝色"
}

json_str2 = {
    "Threaded/Non-threaded": "1122",
    "Male/Female": "323",
    "Screw": "231",
    "Board spacing height": "3243",
    "Total length": "342",
    "Characteristics": "红色"
}

# 提取特征并排序
features = ["Threaded/Non-threaded", "Male/Female", "Screw", "Board spacing height", "Total length", "Characteristics"]


# 转换为独热编码向量
def convert_to_vector(json_str):
    vector = []
    for feature in features:
        if feature in json_str:
            if feature == "Characteristics":
                value = 1 if json_str[feature] == "红色" else 0  # 非数字类型特征的处理
            else:
                value = int(json_str[feature])
            vector.append(value)
        else:
            vector.append(0)  # 缺失特征的处理
    return vector


# 向量化两个JSON字符串
vector1 = convert_to_vector(json_str1)
vector2 = convert_to_vector(json_str2)


class Person:
    def __init__(self, name, age):
        self.name = name
        self.age = age


# 创建Person对象数组
people = [Person("Alice", 30), Person("Bob", 25), Person("Charlie", 35)]

# 对对象数组按照age属性从大到小排序
people.sort(key=lambda x: x.age, reverse=True)

# 打印排序后的对象数组
for person in people:
    print(person.name, "-", person.age)
