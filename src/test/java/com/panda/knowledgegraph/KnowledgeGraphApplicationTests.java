package com.panda.knowledgegraph;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.knowledgegraph.algorithm.graph.Graph;
import com.panda.knowledgegraph.dao.*;
import com.panda.knowledgegraph.dto.*;
import com.panda.knowledgegraph.entity.Equipment;
import com.panda.knowledgegraph.entity.node.BomNode;
import com.panda.knowledgegraph.mapper.BomMapper;
import com.panda.knowledgegraph.mapper.RecommendMapper;
import com.panda.knowledgegraph.service.EquipmentService;
import com.panda.knowledgegraph.service.ProcessService;
import com.panda.knowledgegraph.service.RecommendService;
import com.panda.knowledgegraph.utils.Neo4jUtils;
import com.panda.knowledgegraph.utils.RecommendUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

@SpringBootTest
@Slf4j
class KnowledgeGraphApplicationTests {

    @Autowired
    ComponentRepository componentRepository;

    @Autowired
    FinishedProductRepository finishedProductRepository;

    @Autowired
    SemiFinishedProductRepository semiFinishedProductRepository;

    @Autowired
    BomNodeRepository bomNodeRepository;

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    ManufacturingTechniqueRepository manufacturingTechniqueRepository;

    @Autowired
    Neo4jUtils neo4jUtils;

    @Autowired
    RecommendUtils recommendUtils;

    @Autowired
    Neo4jClient neo4jClient;

    @Autowired
    RecommendService recommendService;

    @Autowired
    RecommendMapper recommendMapper;

    @Autowired
    BomMapper bomMapper;

    @Autowired
    ProcessService processService;

    @Autowired
    EquipmentService equipmentService;

    @Test
    void contextLoads() {
    }

    @Test
    void testVF2Algorithm() throws FileNotFoundException {
//        Path graphPath = Paths.get("D:\\GitStorage\\knowledge-graph\\src\\main\\resources\\static\\graphDB", "kg.data");
//        Path queryPath = Paths.get("D:\\GitStorage\\knowledge-graph\\src\\main\\resources\\static\\graphDB", "Q.my");
//        Path outPath = Paths.get("D:\\GitStorage\\knowledge-graph\\src\\main\\resources\\static\\graphDB", "res_Q20.my");

//        ArrayList<Graph> graphSet = loadGraphSetFromFile(graphPath, "Graph ");
//        ArrayList<Graph> querySet = loadGraphSetFromFile(queryPath, "Query ");
//
//        PrintWriter writer = new PrintWriter(outPath.toFile());
//
//        // 使用VF2算法
//        VF2 vf2 = new VF2();
//
//        for (Graph queryGraph : querySet) {
//            ArrayList<State> stateSet = vf2.matchGraphSetWithQuery(graphSet, queryGraph);
//            if (stateSet.isEmpty()) {
//                System.out.println("Cannot find a map for: " + queryGraph.name);
//                writer.write("Cannot find a map for: " + queryGraph.name + "\n");
//                writer.flush();
//            } else {
//                System.out.println("Found " + stateSet.size() + " maps for: " + queryGraph.name);
//                System.out.println();
//
//                writer.write("Maps for: " + queryGraph.name + "\n");
//                for (State state : stateSet) {
//                    writer.write("In: " + state.targetGraph.name + "\n");
//                    HashMap<Integer, Integer> hashMap = state.writeMapping(writer);
//                    System.out.println(hashMap);
//                }
//                writer.write("\n");
//                writer.flush();
//            }
//        }
    }

    public ArrayList<Graph> loadGraphSetFromFile(Path inpath, String namePrefix) throws FileNotFoundException {
        ArrayList<Graph> graphSet = new ArrayList<Graph>();
        Scanner scanner = new Scanner(inpath.toFile());
        Graph graph = null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.equals("")) {
                continue;
            } else if (line.startsWith("t")) {
                String graphId = line.split(" ")[2];
                if (graph != null) {
                    graphSet.add(graph);
                }
                graph = new Graph(namePrefix + graphId);
            } else if (line.startsWith("v")) {
                String[] lineSplit = line.split(" ");
                int nodeId = Integer.parseInt(lineSplit[1]);
                int nodeLabel = Integer.parseInt(lineSplit[2]);
                graph.addNode(nodeId, nodeLabel);
            } else if (line.startsWith("e")) {
                String[] lineSplit = line.split(" ");
                int sourceId = Integer.parseInt(lineSplit[1]);
                int targetId = Integer.parseInt(lineSplit[2]);
                int edgeLabel = Integer.parseInt(lineSplit[3]);
                graph.addEdge(sourceId, targetId, edgeLabel);
            }
        }
        scanner.close();
        return graphSet;
    }

    @Test
    public void testUtils() {
        recommendUtils.createKnowledgeGraph();
        recommendUtils.createQueryGraph("推荐1");
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:5000/recommend/getMapping?recommendName=推荐1";
        ResponseEntity<List<Map<Long, Long>>> entity = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<Long, Long>>>() {
        });
        List<Mapping> mappingList = new ArrayList<>();
        for (Map<Long, Long> map : entity.getBody()) {
            List<Long> ids = new ArrayList<>(map.keySet());
            List<Map<String, Object>> relationships = bomNodeRepository.queryBomPartRelationship(ids);
            ArrayList<BomNode> bomNodes = new ArrayList<>();
            for (Long id : ids) {
                bomNodes.add(bomNodeRepository.queryBomNodeById(id));
            }
            Mapping mapping = new Mapping();
            mapping.setBomNodeList(bomNodes);
            mapping.setRelationshipList(relationships);
            mappingList.add(mapping);
        }
        System.out.println(mappingList);

    }

    @Test
    public void otherTest() throws JsonProcessingException {
//        BomNode bomNode = bomNodeRepository.queryBomNodeById(5L);
//        System.out.println(bomNode);
//        List<FinishedProductVO> finishedProductNameList = bomMapper.getFinishedProductNameList();
//        System.out.println(finishedProductNameList);
        List<BomNode> bomNodeList = bomNodeRepository.queryBomPart("PTQC001");
        Map<String, FinishedProductTreeNode> bomNodeMap = new HashMap<>();

        List<Long> idList = new ArrayList<>(bomNodeList.size());
        bomNodeList.stream().forEach((item) -> {
            idList.add(item.getId());
            FinishedProductTreeNode finishedProductTreeNode = new FinishedProductTreeNode();
            finishedProductTreeNode.setId(item.getId());
            finishedProductTreeNode.setCode(item.getCode());
            finishedProductTreeNode.setName(item.getName());
            finishedProductTreeNode.setProperties(item.getProperties());
            finishedProductTreeNode.setChildren(new ArrayList<FinishedProductTreeNode>());
            bomNodeMap.put(item.getCode(), finishedProductTreeNode);
        });

        List<Map<String, Object>> relationship = bomNodeRepository.queryBomPartRelationship(idList);
        Map<String, List<String>> relationshipMap = new HashMap<>();
        relationship.stream().forEach(item -> {
            if (relationshipMap.containsKey(item.get("targetCode"))) {
                List<String> sourceCodeList = relationshipMap.get(item.get("targetCode"));
                sourceCodeList.add((String) item.get("sourceCode"));
            } else {
                List<String> sourceCodeList = new ArrayList<>();
                sourceCodeList.add((String) item.get("sourceCode"));
                relationshipMap.put((String) item.get("targetCode"), sourceCodeList);
            }
        });
        System.out.println(bomNodeMap);
        bomNodeMap.keySet().stream().forEach(item -> {
            if (relationshipMap.get(item) != null) {
                for (String code : relationshipMap.get(item)) {
                    bomNodeMap.get(item).getChildren().add(bomNodeMap.get(code));
                }
            }
        });
        System.out.println(bomNodeMap.get("PTQC001"));
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(bomNodeMap.get("PTQC001"));
        System.out.println(json);
    }

    @Test
    public void testService() {
        JSONArray processStrArray = JSON.parseArray("[]");
        String[] processCodeArray = processStrArray.toArray(new String[0]);
        System.out.println(processCodeArray.length);
    }

    @Test
    public void testExcel() {
        File file = new File("D:\\GitStorage\\knowledge-graph\\src\\main\\resources\\static\\汽车与电动汽车BOM.xlsx");//创建文件
        try {
            //创建文件的输入流
            FileInputStream stream = new FileInputStream(file);
            //创建工作簿
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            //获取一个工作表，下表从0开始
            XSSFSheet sheet = workbook.getSheetAt(0);
            //循环读取数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { //getLastRowNum 获取最后一行行标，0开始，行数-1
                //获取行
                XSSFRow row = sheet.getRow(i);
                String parent_code = row.getCell(0).getStringCellValue();
                String code = row.getCell(1).getStringCellValue();
                String relationship = row.getCell(2).getStringCellValue();
                String name = row.getCell(3).getStringCellValue();
                String type = row.getCell(4).getStringCellValue();
                int quantity = (int) row.getCell(5).getNumericCellValue();
                String relationshipProperties = row.getCell(6).getStringCellValue();
                String properties = row.getCell(7).getStringCellValue();
                System.out.println(parent_code + "-" + code + "-" + relationship + "-" + name + "-" + type + "-" + quantity + "-" + relationshipProperties + "-" + properties);
            }
            stream.close();
            //测试是否正确读取
            //System.out.println(this.basePermutation[0].getId()+"\t"+this.basePermutation[0].getDataSize()+"\n");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void test() {
////        System.out.println(equipmentRepository.findEquipmentRelatedManufacturingTechnique("shebei1"));
////        System.out.println(bomNodeRepository.findAllBomNode());
////        System.out.println(equipmentRepository.findAllEquipment());
////        System.out.println(manufacturingTechniqueRepository.findAllManufacturingTechnique());
//        System.out.println(equipmentRepository.findProcessRelatedSourceBom("gongyi1"));
//        System.out.println(equipmentRepository.findProcessRelatedTargetBom("gongyi1"));
//        System.out.println(equipmentService.showRelationship("shebei1"));
//        System.out.println(manufacturingTechniqueRepository.findAllManufacturingTechnique());
//        System.out.println(manufacturingTechniqueRepository.findRelatedEquipmentByCode("PROCESS1"));
        Equipment equipment = new Equipment();
        equipment.setCode("PROCESS1");
        equipment.setId(1817194148205842434L);
        equipmentService.updateById(equipment);

    }
}
