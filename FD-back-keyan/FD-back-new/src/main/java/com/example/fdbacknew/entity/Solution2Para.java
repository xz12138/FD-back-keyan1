package com.example.fdbacknew.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Data
@RelationshipProperties
public class Solution2Para {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String weight;

    @TargetNode
    private Para para;
}
