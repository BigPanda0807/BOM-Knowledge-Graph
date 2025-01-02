package com.panda.knowledgegraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csvreader.CsvReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.knowledgegraph.common.R;
import com.panda.knowledgegraph.dao.BomNodeRepository;
import com.panda.knowledgegraph.dao.ComponentRepository;
import com.panda.knowledgegraph.dao.FinishedProductRepository;
import com.panda.knowledgegraph.dao.SemiFinishedProductRepository;
import com.panda.knowledgegraph.dto.BomMap;
import com.panda.knowledgegraph.dto.FinishedProductTreeNode;
import com.panda.knowledgegraph.entity.Bom;
import com.panda.knowledgegraph.entity.node.BomNode;
import com.panda.knowledgegraph.entity.relationship.ConsistOf;
import com.panda.knowledgegraph.mapper.BomMapper;
import com.panda.knowledgegraph.service.BomService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

@Service
@Slf4j
public class BomServiceImpl extends ServiceImpl<BomMapper, Bom> implements BomService {

    @Autowired
    ComponentRepository componentRepository;

    @Autowired
    SemiFinishedProductRepository semiFinishedProductRepository;

    @Autowired
    FinishedProductRepository finishedProductRepository;

    @Autowired
    BomNodeRepository bomNodeRepository;

    @Autowired
    BomMapper bomMapper;

    // csv文件上传路径
    @Value("${csv.upload_path}")
    private String basePath;

    @Override
    public R<String> uploadAndSaveToNeo4j(MultipartFile file) throws IOException {
        // 获取文件类型
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!(suffix.equals(".xlsx") || suffix.equals(".csv"))) {
            return R.error("文件格式错误，必须为xlsx或csv文件格式");
        }
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        String fileName = UUID.randomUUID().toString() + suffix;
        try {
            // 将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (suffix.equals(".csv")) {
            saveToNeo4j(basePath + fileName);
        } else {
            saveExcelToNeo4j(basePath + fileName);
        }
        return R.success(fileName);
    }

    public void saveToNeo4j(String file) throws IOException {
        CsvReader csvReader = new CsvReader(file, ';', Charset.forName("UTF-8"));
        csvReader.readHeaders();
        while (csvReader.readRecord()) {
            Bom bom = new Bom();
            bom.setCode(csvReader.get(1));
            bom.setName(csvReader.get(3));
            bom.setType(csvReader.get(4));
            bom.setProperties(csvReader.get(7));
            if (csvReader.get(4).equals("零部件（原材料）")) {
                componentRepository.mergeComponent(csvReader.get(1), csvReader.get(3), csvReader.get(4), csvReader.get(7));
            } else if (csvReader.get(4).equals("半成品（中间产品）")) {
                semiFinishedProductRepository.mergeSemiFinishedProduct(csvReader.get(1), csvReader.get(3), csvReader.get(4), csvReader.get(7));
            } else if (csvReader.get(4).equals("成品（产品）")) {
                finishedProductRepository.mergeFinishedProduct(csvReader.get(1), csvReader.get(3), csvReader.get(4), csvReader.get(7));
            }
            Bom bomByCode = bomMapper.findBomByCode(bom.getCode());
            if (bomByCode == null) {
                // 说明之前没有进行新增
                save(bom);
            } else {
                // 说明之前有进行修改
                bom.setId(bomByCode.getId());
                updateById(bom);
            }
        }
        CsvReader csvReaderRelationship = new CsvReader(file, ';', Charset.forName("UTF-8"));
        csvReaderRelationship.readHeaders();
        while (csvReaderRelationship.readRecord()) {
            if (csvReaderRelationship.get(0).equals("null")) {
                continue;
            }
            bomNodeRepository.mergeRelationship(csvReaderRelationship.get(0), csvReaderRelationship.get(1), csvReaderRelationship.get(2), Integer.valueOf(csvReaderRelationship.get(5)), csvReaderRelationship.get(6));
        }
    }

    public void saveExcelToNeo4j(String file) throws IOException {
        System.out.println(111);
        File excelBomFile = new File(file);//创建文件
        try {
            //创建文件的输入流
            FileInputStream stream = new FileInputStream(excelBomFile);
            //创建工作簿
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            //获取一个工作表，下表从0开始
            XSSFSheet sheet = workbook.getSheetAt(0);
            //循环读取数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { //getLastRowNum 获取最后一行行标，0开始，行数-1
                //获取行
                XSSFRow row = sheet.getRow(i);
                Bom bom = new Bom();
                bom.setCode(row.getCell(1).getStringCellValue());
                bom.setName(row.getCell(3).getStringCellValue());
                bom.setType(row.getCell(4).getStringCellValue());
                bom.setProperties(row.getCell(7).getStringCellValue());
                if (row.getCell(4).getStringCellValue().equals("零部件（原材料）")) {
                    componentRepository.mergeComponent(row.getCell(1).getStringCellValue(), row.getCell(3).getStringCellValue(), row.getCell(4).getStringCellValue(), row.getCell(7).getStringCellValue());
                } else if (row.getCell(4).getStringCellValue().equals("半成品（中间产品）")) {
                    semiFinishedProductRepository.mergeSemiFinishedProduct(row.getCell(1).getStringCellValue(), row.getCell(3).getStringCellValue(), row.getCell(4).getStringCellValue(), row.getCell(7).getStringCellValue());
                } else if (row.getCell(4).getStringCellValue().equals("成品（产品）")) {
                    finishedProductRepository.mergeFinishedProduct(row.getCell(1).getStringCellValue(), row.getCell(3).getStringCellValue(), row.getCell(4).getStringCellValue(), row.getCell(7).getStringCellValue());
                }
                Bom bomByCode = bomMapper.findBomByCode(bom.getCode());
                if (bomByCode == null) {
                    // 说明之前没有进行新增
                    save(bom);
                } else {
                    // 说明之前有进行修改
                    bom.setId(bomByCode.getId());
                    updateById(bom);
                }
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { //getLastRowNum 获取最后一行行标，0开始，行数-1
                //获取行
                XSSFRow row = sheet.getRow(i);
                if (row.getCell(4).getStringCellValue().equals("null")) {
                    continue;
                }
                bomNodeRepository.mergeRelationship(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue(), row.getCell(2).getStringCellValue(), (int) row.getCell(5).getNumericCellValue(), row.getCell(6).getStringCellValue());
            }
            stream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public R<BomMap> renderMap() {
        List<BomNode> bomNodeList = bomNodeRepository.findAllBomNode();
        List<ConsistOf> relationshipList = bomNodeRepository.findAllBomRelationship();
        BomMap bomMap = new BomMap();
        bomMap.setBomNodeList(bomNodeList);
        bomMap.setRelationshipList(relationshipList);
        return R.success(bomMap);
    }

    @Override
    public R<List<Bom>> getAllBom() {
        return R.success(list());
    }

    @Override
    public R<String> getFinishedProductNameByCode(String code) throws JsonProcessingException {
        List<BomNode> bomNodeList = bomNodeRepository.queryBomPart(code);
        System.out.println(bomNodeList);
        Map<String, FinishedProductTreeNode> bomNodeMap = new HashMap<>();

        List<Long> idList = new ArrayList<>(bomNodeList.size());
        bomNodeList.stream().forEach((item) -> {
            idList.add(item.getId());
            FinishedProductTreeNode finishedProductTreeNode = new FinishedProductTreeNode();
            finishedProductTreeNode.setId(item.getId());
            finishedProductTreeNode.setCode(item.getCode());
            finishedProductTreeNode.setName(item.getName());
            finishedProductTreeNode.setType(item.getType());
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
            bomNodeMap.get(item.get("sourceCode")).setProcess((String) item.get("type"));
            bomNodeMap.get(item.get("sourceCode")).setProcessProperties((String) item.get("properties"));
            bomNodeMap.get(item.get("sourceCode")).setCount((Long) item.get("quantity"));
        });
        bomNodeMap.keySet().stream().forEach(item -> {
            if (relationshipMap.get(item) != null) {
                for (String itemCode : relationshipMap.get(item)) {
                    bomNodeMap.get(item).getChildren().add(bomNodeMap.get(itemCode));
                    bomNodeMap.get(itemCode).setParentId(bomNodeMap.get(item).getId());
                    bomNodeMap.get(itemCode).setParentCode(bomNodeMap.get(item).getCode());
                }
            }
        });
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(bomNodeMap.get(code));
        return R.success(json);
    }

    @Override
    public R<BomMap> getBomNodeAndRelationship(String code) {
        List<BomNode> bomNodeList = bomNodeRepository.queryBomPart(code);
        List<Long> idList = new ArrayList<>(bomNodeList.size());
        bomNodeList.stream().forEach((item) -> {
            idList.add(item.getId());
        });
        List<Map<String, Object>> relationship = bomNodeRepository.queryBomPartRelationship(idList);
        List<ConsistOf> relationshipList = new ArrayList<>(relationship.size());
        relationship.stream().forEach((item) -> {
            ConsistOf consistOf = new ConsistOf();
            consistOf.setRid((Long) item.get("id"));
            consistOf.setQuantity((Long) item.get("quantity"));
            consistOf.setType((String) item.get("type"));
            consistOf.setSourceCode((String) item.get("sourceCode"));
            consistOf.setTargetCode((String) item.get("targetCode"));
            consistOf.setSourceId((Long) item.get("sourceId"));
            consistOf.setTargetId((Long) item.get("targetId"));
            relationshipList.add(consistOf);
        });
        BomMap bomMap = new BomMap();
        bomMap.setBomNodeList(bomNodeList);
        bomMap.setRelationshipList(relationshipList);
        return R.success(bomMap);
    }
}
