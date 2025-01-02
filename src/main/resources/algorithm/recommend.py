from recommend_algorithm import create_query_and_kg, VF2_algorithm


def recommend(recommendName):
    create_query_and_kg(recommendName)
    return VF2_algorithm()


# 测试数据
json_str1 = (
    "{\"Threaded/Non-threaded\" \"Threaded\",\"Male/Female\": \"Male\",\"Screw\": 456,\"Thread-size\": 789,\"Color\": \"蓝色\""
    ",\"Texture\": \"铝合金\",\"Instructions\": \"这个东西使用的时候一定要非常小心，要和螺母一起用\",\"Inner diameter\": 123,\"Outer diameter\": 456}")
json_str2 = (
    "{\"Threaded/Non-threaded\": \"Threaded\",\"Male/Female\": \"Female\",\"Screw\": 400,\"Thread-size\": 800,\"Color\": \"红色\""
    ",\"Texture\": \"铝合金和钢铁\",\"Instructions\": \"这个东西使用的时候非常随意，随便使用就行\",\"Board spacing height\": 789,\"Total length\": 123}")
