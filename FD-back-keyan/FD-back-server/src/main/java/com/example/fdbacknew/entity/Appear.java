package com.example.fdbacknew.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node(labels = "appear")
public class Appear {

    @Id
    @GeneratedValue
    private Long neo4jId;

    @Property("id")
    private String customId;

    @Property("name")
    private String name;

    @Relationship(type = "appear2reason")
    private List<Reason>  reasons = new ArrayList<>();
}
