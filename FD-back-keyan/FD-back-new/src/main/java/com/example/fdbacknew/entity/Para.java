package com.example.fdbacknew.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node(labels = "para")
public class Para {
    @Id
    @GeneratedValue
    private Long neo4jId;

    @Property("id")
    private String customId;

    @Property("name")
    private String name;
}
