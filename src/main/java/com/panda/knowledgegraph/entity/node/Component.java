package com.panda.knowledgegraph.entity.node;

import com.panda.knowledgegraph.entity.relationship.ConsistOfComponent;
import com.panda.knowledgegraph.entity.relationship.ConsistOfFinishedProduct;
import com.panda.knowledgegraph.entity.relationship.ConsistOfSemiFinishedProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Node(labels = "component")
public class Component {
    @Id
    @GeneratedValue
    private Long id;

    @Property("code")
    private String code;

    @Property("name")
    private String name;

    @Property("type")
    private String type;

    // 扩展属性 json
    @Property("properties")
    private String properties;

    // 关系
    @Relationship(type = "consist_of", direction = Relationship.Direction.INCOMING)
    private List<ConsistOfComponent> ConsistOfComponentIncoming;

    @Relationship(type = "consist_of", direction = Relationship.Direction.OUTGOING)
    private List<ConsistOfComponent> ConsistOfComponentOutgoing;

    @Relationship(type = "consist_of", direction = Relationship.Direction.INCOMING)
    private List<ConsistOfSemiFinishedProduct> ConsistOfSemiFinishedIncoming;

    @Relationship(type = "consist_of", direction = Relationship.Direction.OUTGOING)
    private List<ConsistOfSemiFinishedProduct> ConsistOfSemiFinishedOutgoing;

    @Relationship(type = "consist_of", direction = Relationship.Direction.INCOMING)
    private List<ConsistOfFinishedProduct> ConsistOfFinishedIncoming;

    @Relationship(type = "consist_of", direction = Relationship.Direction.OUTGOING)
    private List<ConsistOfFinishedProduct> ConsistOfFinishedOutgoing;
}
