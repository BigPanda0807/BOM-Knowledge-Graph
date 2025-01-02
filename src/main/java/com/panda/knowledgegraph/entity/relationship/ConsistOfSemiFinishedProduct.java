package com.panda.knowledgegraph.entity.relationship;

import com.panda.knowledgegraph.entity.node.SemiFinishedProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RelationshipProperties
public class ConsistOfSemiFinishedProduct {
    @RelationshipId
    @GeneratedValue
    private Long id;

    @Property("quantity")
    private Integer quantity;

    @Property("type")
    private String type;

    @Property("parentCode")
    private String sourceCode;

    @Property("targetCode")
    private String targetCode;

    @TargetNode
    private SemiFinishedProduct semiFinishedProduct;
}
