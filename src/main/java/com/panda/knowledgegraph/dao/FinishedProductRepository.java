package com.panda.knowledgegraph.dao;

import com.panda.knowledgegraph.entity.node.FinishedProduct;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinishedProductRepository extends Neo4jRepository<FinishedProduct, Long> {
    @Query("merge (f:finishedProduct{code:$code}) ON MATCH SET f.name = $name, f.type = $type, f.properties = $properties ON CREATE SET f.name = $name, f.type = $type, f.properties = $properties  set f:bom return id(f)")
    Long mergeFinishedProduct(@Param("code") String code, @Param("name") String name, @Param("type") String type, @Param("properties") String properties);

    @Query("match (f:finishedProduct) return f")
    List<FinishedProduct> findAllFinishedProduct();
}
