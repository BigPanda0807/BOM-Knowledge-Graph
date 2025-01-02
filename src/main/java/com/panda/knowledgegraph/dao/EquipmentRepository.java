package com.panda.knowledgegraph.dao;

import com.panda.knowledgegraph.entity.node.Equipment;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface EquipmentRepository extends Neo4jRepository<Equipment, Long> {
    @Query("merge (e:equipment{code:$code,name:$name,type:$type,status:$status,technicalSpecifications:$technicalSpecifications,properties:$properties}) return id(e)")
    Long mergeEquipment(@Param("code") String code, @Param("name") String name, @Param("type") String type, @Param("status") Integer status, @Param("technicalSpecifications") String technicalSpecifications, @Param("properties") String properties);

    @Query("match (e:equipment) return e")
    List<Equipment> findAllEquipment();

    @Query("MATCH (e:equipment) WHERE e.code = $code DETACH DELETE e")
    void deleteEquipmentByCode(@Param("code") String code);

    @Query("MATCH (e:equipment{code:$code}) SET e.name = $name, e.type = $type, e.status = $status, e.technicalSpecifications = $technicalSpecifications, e.properties = $properties")
    void updateEquipmentByCode(@Param("code") String code, @Param("name") String name, @Param("type") String type, @Param("status") Integer status, @Param("technicalSpecifications") String technicalSpecifications, @Param("properties") String properties);

    // 寻找到当前设备所关联的所有工艺
    @Query("MATCH (e:equipment{code:$code})-[:use]->(m:manufacturingTechnique) return m")
    List<Map<String, Object>> findEquipmentRelatedManufacturingTechnique(@Param("code") String code);

    // 寻找到当前工艺所关联的BOM节点
    @Query("MATCH (b1:bom)-[:apply]->(m:manufacturingTechnique{code:$code}) return b1")
    List<Map<String, Object>> findProcessRelatedSourceBom(@Param("code") String code);

    @Query("MATCH (b2:bom)<-[:apply]-(m:manufacturingTechnique{code:$code}) return b2")
    List<Map<String, Object>> findProcessRelatedTargetBom(@Param("code") String code);

    // 得到targetBom所关联的所有工艺
    @Query("MATCH (b:bom{code:$code})-[:apply]->(m:manufacturingTechnique) return m")
    List<Map<String, Object>> findTargetBomRelatedProcess(@Param("code") String code);

    // 更新图谱中设备状态
    @Query("MATCH (e:equipment{code:$code}) SET e.status = $status")
    void updateEquipmentStatus(@Param("code") String code, @Param("status") Integer status);

}
