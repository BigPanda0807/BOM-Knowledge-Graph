package com.panda.knowledgegraph.dao;

import com.panda.knowledgegraph.entity.node.Supplier;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupplierRepository extends Neo4jRepository<Supplier, Long> {
    @Query("merge (s:supplier{code:$code,name:$name,type:$type,status:$status,address:$address,scope:$scope,properties:$properties}) return id(s)")
    Long mergeSupplier(@Param("code") String code, @Param("name") String name, @Param("type") String type, @Param("status") Integer status, @Param("address") String address, @Param("scope") String scope, @Param("properties") String properties);

    @Query("match (s:supplier) return s")
    List<Supplier> findAllSupplier();
}
