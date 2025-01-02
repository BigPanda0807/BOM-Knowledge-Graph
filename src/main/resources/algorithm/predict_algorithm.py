from cal_properties_similarity import node_properties_similarity, process_similarity_cal


class predictionEntity:
    def __init__(self, manufacturingTechnique, processRelations, sourceBom, targetBom):
        self.manufacturingTechnique = manufacturingTechnique
        self.sourceBom = sourceBom
        self.targetBom = targetBom
        self.processRelations = processRelations


class equipment:
    def __init__(self, code, name, properties, status, technicalSpecifications, type):
        self.code = code
        self.name = name
        self.properties = properties
        self.status = status
        self.technicalSpecifications = technicalSpecifications
        self.type = type


class bomNode:
    def __init__(self, name, code, type, properties):
        self.name = name
        self.code = code
        self.type = type
        self.properties = properties


class processRelations:
    def __init__(self, equipmentList, manufacturingTechnique, sourceBom, targetBom):
        self.equipmentList = equipmentList
        self.manufacturingTechnique = manufacturingTechnique
        self.sourceBom = sourceBom
        self.targetBom = targetBom


class manufacturingTechnique:
    def __init__(self, code, description, id, name, parameter, procedures, properties, status):
        self.code = code
        self.description = description
        self.id = id
        self.name = name
        self.parameter = parameter
        self.procedures = procedures
        self.properties = properties
        self.status = status


def create_predict_entity(predictionInfoDict):
    toBePredictProcessDict = predictionInfoDict["manufacturingTechnique"]
    toBePredictProcessObj = manufacturingTechnique(toBePredictProcessDict["code"],
                                                   toBePredictProcessDict["description"],
                                                   toBePredictProcessDict["id"],
                                                   toBePredictProcessDict["name"],
                                                   toBePredictProcessDict["parameter"],
                                                   toBePredictProcessDict["procedures"],
                                                   toBePredictProcessDict["properties"],
                                                   toBePredictProcessDict["status"])
    toBePredictProcessObjSourceBom = bomNode(predictionInfoDict["sourceBom"]["name"],
                                             predictionInfoDict["sourceBom"]["code"],
                                             predictionInfoDict["sourceBom"]["type"],
                                             predictionInfoDict["sourceBom"]["properties"])
    toBePredictProcessObjTargetBom = bomNode(predictionInfoDict["targetBom"]["name"],
                                             predictionInfoDict["targetBom"]["code"],
                                             predictionInfoDict["targetBom"]["type"],
                                             predictionInfoDict["targetBom"]["properties"])
    processRelationsDictList = predictionInfoDict["processRelations"]
    processRelationsList = []
    for processRelationDict in processRelationsDictList:
        sourceBom = bomNode(processRelationDict["sourceBom"]["name"], processRelationDict["sourceBom"]["code"],
                            processRelationDict["sourceBom"]["type"], processRelationDict["sourceBom"]["properties"])
        targetBom = bomNode(processRelationDict["targetBom"]["name"], processRelationDict["targetBom"]["code"],
                            processRelationDict["targetBom"]["type"], processRelationDict["targetBom"]["properties"])
        process = manufacturingTechnique(processRelationDict["manufacturingTechnique"]["code"],
                                         processRelationDict["manufacturingTechnique"]["description"],
                                         processRelationDict["manufacturingTechnique"]["id"],
                                         processRelationDict["manufacturingTechnique"]["name"],
                                         processRelationDict["manufacturingTechnique"]["parameter"],
                                         processRelationDict["manufacturingTechnique"]["procedures"],
                                         processRelationDict["manufacturingTechnique"]["properties"],
                                         processRelationDict["manufacturingTechnique"]["status"])
        equipmentList = []
        for equipmentDict in processRelationDict["equipmentList"]:
            equipmentList.append(equipment(equipmentDict["code"],
                                           equipmentDict["name"],
                                           equipmentDict["properties"],
                                           equipmentDict["status"],
                                           equipmentDict["technicalSpecifications"],
                                           equipmentDict["type"]))
        processRelation = processRelations(equipmentList, process, sourceBom, targetBom)
        processRelationsList.append(processRelation)
    predictionInfo = predictionEntity(toBePredictProcessObj, processRelationsList,
                                      toBePredictProcessObjSourceBom, toBePredictProcessObjTargetBom)
    return predictionInfo


class TotalSimilarity:
    def __init__(self):
        self.bomSimilarity = []
        self.processSimilarity = []
        self.totalSimilarity = []

    def addSimilarity(self, bomSimilarity, processSimilarity):
        self.bomSimilarity.append(bomSimilarity)
        self.processSimilarity.append(processSimilarity)

    def getPredictionIndex(self):
        for index in range(len(self.bomSimilarity)):
            self.totalSimilarity.append(
                self.bomSimilarity[index] / 2 + self.processSimilarity[index] / 2
            )
        max_value = max(self.totalSimilarity)
        max_index = self.totalSimilarity.index(max_value)
        return max_index


def get_process_equipment_prediction(predict_entity):
    index = 1
    # 总相似度
    totalSimilarity = TotalSimilarity()
    # 要进行预测的工艺
    toBePredictProcess = predict_entity.manufacturingTechnique
    toBePredictProcessSourceBom = predict_entity.sourceBom
    toBePredictProcessTargetBom = predict_entity.targetBom
    print(predict_entity.processRelations)
    for item in predict_entity.processRelations:
        # 首先计算两个Bom节点之间的
        sourceSimilarity1, sourceSimilarity2 = node_properties_similarity(item.sourceBom.properties,
                                                                          toBePredictProcessSourceBom.properties)
        targetSimilarity1, targetSimilarity2 = node_properties_similarity(item.targetBom.properties,
                                                                          toBePredictProcessTargetBom.properties)
        bomSimilarity = (sourceSimilarity1 + targetSimilarity2) / 2 + (targetSimilarity1 + targetSimilarity2) / 2
        # 计算工序之间的
        processSimilarity = process_similarity_cal(toBePredictProcess, item.manufacturingTechnique)
        totalSimilarity.addSimilarity(bomSimilarity, processSimilarity)
    prediction_index = totalSimilarity.getPredictionIndex()
    # 处理输入
    return predict_entity.processRelations[prediction_index]
