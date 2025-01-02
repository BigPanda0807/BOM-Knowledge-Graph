package com.panda.knowledgegraph.dao;

import com.panda.knowledgegraph.entity.node.SemiFinishedProduct;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemiFinishedProductRepository extends Neo4jRepository<SemiFinishedProduct, Long> {
    @Query("merge (s:semiFinishedProduct{code:$code}) ON MATCH SET s.name = $name, s.type = $type, s.properties = $properties ON CREATE SET s.name = $name, s.type = $type, s.properties = $properties set s:bom return id(s)")
    Long mergeSemiFinishedProduct(@Param("code") String code, @Param("name") String name, @Param("type") String type, @Param("properties") String properties);

    @Query("match (s:semiFinishedProduct) return s")
    List<SemiFinishedProduct> findAllSemiFinishedProduct();
}
