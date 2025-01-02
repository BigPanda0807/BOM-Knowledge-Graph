from flask import Flask, jsonify, request
from recommend import recommend
from predict import predict

app = Flask(__name__)


@app.route('/recommend/getMapping', methods=['GET'])
def getMapping():
    recommendName = request.args.get('recommendName')
    mapping = recommend(recommendName)
    # 返回 JSON 格式的响应
    return jsonify(mapping)


@app.route('/predict/equipment', methods=['POST'])
def getEquipmentPrediction():
    predictionInfo = request.get_json()
    prediction = predict(predictionInfo)
    # 返回 JSON 格式的响应
    return jsonify(prediction)


if __name__ == '__main__':
    app.run(debug=True)
