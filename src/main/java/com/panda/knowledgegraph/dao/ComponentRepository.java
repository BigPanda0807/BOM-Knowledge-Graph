package com.panda.knowledgegraph.dao;

import com.panda.knowledgegraph.entity.node.Component;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponentRepository extends Neo4jRepository<Component, Long> {
    @Query("merge (c:component{code:$code}) ON MATCH SET c.name = $name, c.type = $type, c.properties = $properties ON CREATE SET c.name = $name, c.type = $type, c.properties = $properties set c:bom return id(c)")
    Long mergeComponent(@Param("code") String code, @Param("name") String name, @Param("type") String type, @Param("properties") String properties);

    @Query("match (c:component) return c")
    List<Component> findAllComponent();
}
