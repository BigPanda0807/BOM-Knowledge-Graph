from predict_algorithm import create_predict_entity, get_process_equipment_prediction


def predict(predictionInfo):
    predict_entity = create_predict_entity(predictionInfo)
    equipment_prediction = get_process_equipment_prediction(predict_entity)
    return equipment_prediction


test = {
    "manufacturingTechnique": {
        "code": "PROCESS3",
        "description": "焊纵缝是指沿工件的长度方向进行焊接的一种焊接工艺，常用于圆柱形工件（如管道、筒体等）的拼接",
        "id": 1813870228749172737,
        "name": "焊纵缝",
        "parameter": "{\n    \"1\": {\n        \"材料规格\": \"材质、厚度符合设计要求\",\n        \"化学成分\": \"符合相关标准（如GB/T 1591、GB/T 3077等）\"\n    },\n    \"2\": {\n        \"切割精度\": \"±1 mm\",\n        \"端面平整度\": \"≤0.5 mm\"\n    },\n    \"3\": {\n        \"同轴度公差\": \"≤1 mm（圆柱形工件）\",\n        \"垂直度公差\": \"≤1°\",\n        \"间隙控制\": \"1-3 mm\"\n    },\n    \"4\": {\n        \"点焊电流\": \"100-150 A\",\n        \"点焊电压\": \"20-25 V\",\n        \"点焊时间\": \"0.5-1秒/点\"\n    },\n    \"5\": {\n        \"焊接电流\": \"TIG焊（100-200 A），MIG焊（150-250 A），手工电弧焊（120-220 A）\",\n        \"焊接电压\": \"TIG焊（10-20 V），MIG焊（20-30 V），手工电弧焊（20-25 V）\",\n        \"焊接速度\": \"100-300 mm/min\",\n        \"焊丝直径\": \"1.0-1.6 mm\"\n    },\n    \"6\": {\n        \"去应力处理温度\": \"550-650℃\",\n        \"保温时间\": \"1-2小时\"\n    },\n    \"7\": {\n        \"喷砂压力\": \"0.5-0.8 MPa\",\n        \"涂层厚度\": \"100-200 μm\"\n    }\n}",
        "procedures": "{\n    \"1\": \"材料准备\",\n    \"2\": \"工件准备\",\n    \"3\": \"组对定位\",\n    \"4\": \"点焊固定\",\n    \"5\": \"焊接\",\n    \"6\": \"后处理\",\n    \"7\": \"表面处理\"\n}",
        "properties": "{\n    \"是否需要质检\": \"是\",\n    \"存储\": \"低温存储\"\n}",
        "status": 1
    },
    "processRelations": [
        {
            "equipmentList": [
                {
                    "code": "MIG/MAGWeldingMachine",
                    "name": "MIG/MAG焊机",
                    "properties": "{\n    \"数字显示\":\"支持\",\n    \"过热保护\":\"支持\",\n    \"记忆功能\":\"支持\",\n    \"节能模式\":\"支持\"\n}",
                    "status": 1,
                    "technicalSpecifications": "{\n    \"型号\": \"MIG-250\",\n    \"输入电压\": \"220V ±10% / 380V ±10%\",\n    \"输入功率\": \"8.5 kW\",\n    \"输出电流范围\": \"50-250A\",\n    \"空载电压\": \"60V\",\n    \"工作循环\": \"60% @ 250A\",\n    \"焊丝直径\": \"0.8-1.2 mm\",\n    \"焊接材料\": \"碳钢、不锈钢、铝\",\n    \"冷却方式\": \"风冷\",\n    \"重量\": \"35 kg\",\n    \"尺寸\": \"500 x 250 x 400 mm\"\n}",
                    "type": "焊机"
                }
            ],
            "manufacturingTechnique": {
                "code": "PROCESS1",
                "description": "组对焊接法兰环",
                "id": 140,
                "name": "组对焊接法兰环",
                "parameter": "{\n    \"1\": {\n        \"材料规格\": \"法兰和管道材质、尺寸符合设计要求\",\n        \"化学成分\": \"符合GB/T 1591、GB/T 3077等标准\"\n    },\n    \"2\": {\n        \"切割精度\": \"±1 mm\",\n        \"端面平整度\": \"≤0.5 mm\"\n    },\n    \"3\": {\n        \"同轴度公差\": \"≤1 mm\",\n        \"垂直度公差\": \"≤1°\",\n        \"间隙控制\": \"1-3 mm\"\n    },\n    \"4\": {\n        \"点焊电流\": \"100-150 A\",\n        \"点焊电压\": \"20-25 V\",\n        \"点焊时间\": \"0.5-1秒/点\"\n    },\n    \"5\": {\n        \"焊接电流\": \"TIG焊（100-200 A），MIG焊（150-250 A），手工电弧焊（120-220 A）\",\n        \"焊接电压\": \"TIG焊（10-20 V），MIG焊（20-30 V），手工电弧焊（20-25 V）\",\n        \"焊接速度\": \"100-300 mm/min\",\n        \"焊丝直径\": \"1.0-1.6 mm\"\n    },\n    \"6\": {\n        \"去应力处理温度\": \"550-650℃\",\n        \"保温时间\": \"1-2小时\"\n    },\n    \"7\": {\n        \"喷砂压力\": \"0.5-0.8 MPa\",\n        \"涂层厚度\": \"100-200 μm\"\n    }\n}",
                "procedures": "{\n    \"1\": \"材料准备\",\n    \"2\": \"下料\",\n    \"3\": \"组对定位\",\n    \"4\": \"点焊固定\",\n    \"5\": \"焊接\",\n    \"6\": \"后处理\",\n    \"7\": \"表面处理\"\n}",
                "properties": "{\n    \"是否需要质检\":\"是\"\n}",
                "status": 1
            },
            "sourceBom": {
                "name": "法兰",
                "code": "FLN001",
                "type": "零部件（原材料）",
                "properties": "{\"drawing_name\":\"FLN001.dwg\",\"drawing_code\":\"FLN001.dwg\",\"texture\":\"不锈钢\",\"model\":\"FL-200\",\"unit\":\"件\"}"
            },
            "targetBom": {
                "name": "压缩机润滑油管路系统",
                "code": "SYS001",
                "type": "成品（产品）",
                "properties": "{\"drawing_name\":\"pipeline.dwg\",\"drawing_code\":\"E1A3669324\",\"texture\":\"ASTM A312-TP304L\",\"model\":\"3 S40S - S\",\"unit\":\"个\"}"
            }
        },
        {
            "equipmentList": [
                {
                    "code": "shebei1",
                    "name": "设备1",
                    "properties": "{}",
                    "status": 1,
                    "technicalSpecifications": "{}",
                    "type": "焊机"
                }
            ],
            "manufacturingTechnique": {
                "code": "gongyi1",
                "description": "{}",
                "id": 158,
                "name": "工艺1",
                "parameter": "{}",
                "procedures": "{}",
                "properties": "{}",
                "status": 1
            },
            "sourceBom": {
                "name": "泵盖",
                "code": "PUMPCOVER001",
                "type": "半成品（中间产品）",
                "properties": "{\"drawing_name\":\"PumpCover.dwg\",\"drawing_code\":\"PumpCover.dwg\",\"texture\":\"铸铁\",\"model\":\"PC-70\",\"unit\":\"个\"}"
            },
            "targetBom": {
                "name": "泵体",
                "code": "PUMPBODY001",
                "type": "半成品（中间产品）",
                "properties": "{\"drawing_name\":\"PumpBody.dwg\",\"drawing_code\":\"PumpBody.dwg\",\"texture\":\"铸铁\",\"model\":\"PP-600\",\"unit\":\"个\"}"
            }
        },
        {
            "equipmentList": [
                {
                    "code": "PlasmaCuttingMachine",
                    "name": "等离子切割机",
                    "properties": "{\n    \"数字显示\":\"支持\",\n    \"过热保护\":\"支持\",\n    \"记忆功能\":\"支持\",\n    \"节能模式\":\"支持\"\n}",
                    "status": 1,
                    "technicalSpecifications": "{\n    \"型号\": \"CUT-60\",\n    \"输入电压\": \"220V ±10% / 380V ±10%\",\n    \"输入功率\": \"7.5 kW\",\n    \"输出电流范围\": \"20-60A\",\n    \"空载电压\": \"280V\",\n    \"工作循环\": \"60% @ 60A\",\n    \"切割厚度\": \"1-20 mm\",\n    \"切割材料\": \"碳钢、不锈钢、铝\",\n    \"冷却方式\": \"风冷\",\n    \"重量\": \"23 kg\",\n    \"尺寸\": \"470 x 210 x 320 mm\"\n}",
                    "type": "切割机"
                }
            ],
            "manufacturingTechnique": {
                "code": "PROCESS2",
                "description": "切割板材工艺",
                "id": 159,
                "name": "切割板材工艺",
                "parameter": "{\n    \"4\": {\n        \"切割电流\": \"50-200 A\",\n        \"切割速度\": \"500-3000 mm/min\",\n        \"切割厚度\": \"1-30 mm\",\n        \"切割精度\": \"±0.3 mm\"\n    }\n}",
                "procedures": "{\n    \"1\": \"材料准备\",\n    \"2\": \"选择切割方式\",\n    \"3\": \"编程\",\n    \"4\": \"切割操作\",\n    \"5\": \"焊接\",\n    \"6\": \"后处理\"\n}",
                "properties": "{\n    \"是否需要质检\": \"是\",\n    \"存储\": \"低温存储\"\n}",
                "status": 1
            },
            "sourceBom": {
                "name": "碳钢板底座",
                "code": "STEELBASE001",
                "type": "半成品（中间产品）",
                "properties": "{\"drawing_name\":\"STEELBASE001.dwg\",\"drawing_code\":\"STEELBASE001.dwg\",\"texture\":\"铁\",\"model\":\"1000*2000mm\",\"unit\":\"个\"}"
            },
            "targetBom": {
                "name": "管道支架",
                "code": "BRACKET001",
                "type": "半成品（中间产品）",
                "properties": "{\"drawing_name\":\"Bracket.dwg\",\"drawing_code\":\"Bracket.dwg\",\"texture\":\"铝合金\",\"model\":\"PS-25\",\"unit\":\"个\"}"
            }
        },
        {
            "equipmentList": [],
            "manufacturingTechnique": {
                "code": "PROCESS3",
                "description": "焊纵缝是指沿工件的长度方向进行焊接的一种焊接工艺，常用于圆柱形工件（如管道、筒体等）的拼接",
                "id": 160,
                "name": "焊纵缝",
                "parameter": "{\n    \"1\": {\n        \"材料规格\": \"材质、厚度符合设计要求\",\n        \"化学成分\": \"符合相关标准（如GB/T 1591、GB/T 3077等）\"\n    },\n    \"2\": {\n        \"切割精度\": \"±1 mm\",\n        \"端面平整度\": \"≤0.5 mm\"\n    },\n    \"3\": {\n        \"同轴度公差\": \"≤1 mm（圆柱形工件）\",\n        \"垂直度公差\": \"≤1°\",\n        \"间隙控制\": \"1-3 mm\"\n    },\n    \"4\": {\n        \"点焊电流\": \"100-150 A\",\n        \"点焊电压\": \"20-25 V\",\n        \"点焊时间\": \"0.5-1秒/点\"\n    },\n    \"5\": {\n        \"焊接电流\": \"TIG焊（100-200 A），MIG焊（150-250 A），手工电弧焊（120-220 A）\",\n        \"焊接电压\": \"TIG焊（10-20 V），MIG焊（20-30 V），手工电弧焊（20-25 V）\",\n        \"焊接速度\": \"100-300 mm/min\",\n        \"焊丝直径\": \"1.0-1.6 mm\"\n    },\n    \"6\": {\n        \"去应力处理温度\": \"550-650℃\",\n        \"保温时间\": \"1-2小时\"\n    },\n    \"7\": {\n        \"喷砂压力\": \"0.5-0.8 MPa\",\n        \"涂层厚度\": \"100-200 μm\"\n    }\n}",
                "procedures": "{\n    \"1\": \"材料准备\",\n    \"2\": \"工件准备\",\n    \"3\": \"组对定位\",\n    \"4\": \"点焊固定\",\n    \"5\": \"焊接\",\n    \"6\": \"后处理\",\n    \"7\": \"表面处理\"\n}",
                "properties": "{\n    \"是否需要质检\": \"是\",\n    \"存储\": \"低温存储\"\n}",
                "status": 1
            },
            "sourceBom": {
                "name": "弯管",
                "code": "ELBOW001",
                "type": "半成品（中间产品）",
                "properties": "{\"drawing_name\":\"Elbow.dwg\",\"drawing_code\":\"Elbow.dwg\",\"texture\":\"铜\",\"model\":\"DN15\",\"unit\":\"个\"}"
            },
            "targetBom": {
                "name": "压缩机润滑油管路系统",
                "code": "SYS001",
                "type": "成品（产品）",
                "properties": "{\"drawing_name\":\"pipeline.dwg\",\"drawing_code\":\"E1A3669324\",\"texture\":\"ASTM A312-TP304L\",\"model\":\"3 S40S - S\",\"unit\":\"个\"}"
            }
        }
    ]
}
