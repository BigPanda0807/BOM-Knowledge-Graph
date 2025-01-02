from sklearn.preprocessing import OneHotEncoder
import numpy as np

# 定义非数字字符串数据
data = ["red", "blue", "green", "yellow"]
print(type(ord("红")))
print(str(ord("蓝")) + str(ord("色")))
# print(str(hash("红色")))
# print(str(hash("蓝色")))

# 创建独热编码器对象
encoder = OneHotEncoder()

# 将非数字字符串数据转换为独热编码
encoded_data = encoder.fit_transform(np.array(data).reshape(-1, 1)).toarray()

print(encoded_data)