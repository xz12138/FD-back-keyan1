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
@Node(labels = "alarm_id")
public class AlarmId {

    @Id
    @GeneratedValue
    private Long neo4jId;

    @Property("id")
    private String customId;

    @Property("name")
    private String name;

    @Relationship(type = "alarm_id2alarm_inf")
    private List<AlarmId2AlarmInf>  alarmId2AlarmInfs = new ArrayList<>();
}
