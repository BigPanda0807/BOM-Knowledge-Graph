package com.panda.knowledgegraph.dao;

import com.panda.knowledgegraph.entity.node.ManufacturingTechnique;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ManufacturingTechniqueRepository extends Neo4jRepository<ManufacturingTechnique, Long> {
    @Query("merge (m:manufacturingTechnique{code:$code,name:$name,status:$status,description:$description,procedures:$procedures,parameter:$parameter,properties:$properties}) return id(m)")
    Long mergeManufacturingTechnique(@Param("code") String code, @Param("name") String name, @Param("status") Integer status, @Param("description") String description, @Param("procedures") String procedures, @Param("parameter") String parameter, @Param("properties") String properties);

    @Query("match (m:manufacturingTechnique) return m")
    List<ManufacturingTechnique> findAllManufacturingTechnique();

    @Query("MATCH (m:manufacturingTechnique) WHERE m.code = $code DETACH DELETE m")
    void deleteProcessByCode(@Param("code") String code);

    @Query("MATCH (m:manufacturingTechnique{code:$code}) SET m.name = $name, m.description = $description, m.parameter = $parameter, m.procedures = $procedures, m.status = $status, m.properties = $properties")
    void updateProcessByCode(@Param("code") String code, @Param("name") String name, @Param("description") String description, @Param("parameter") String parameter, @Param("procedures") String procedures, @Param("status") Integer status, @Param("properties") String properties);

    @Query("MATCH (m:manufacturingTechnique)<-[:use]-(e:equipment) where m.code = $code return e")
    List<Map<String, Object>> findRelatedEquipmentByCode(@Param("code") String code);

    // 更新图谱中设备状态
    @Query("MATCH (m:manufacturingTechnique{code:$code}) SET m.status = $status")
    void updateEquipmentStatus(@Param("code") String code, @Param("status") Integer status);
}
