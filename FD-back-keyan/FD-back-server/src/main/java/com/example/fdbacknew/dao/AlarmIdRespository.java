package com.example.fdbacknew.dao;


import com.example.fdbacknew.entity.AlarmId;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmIdRespository extends Neo4jRepository<AlarmId,Long> {

    List<AlarmId> findAlarmIdsByName(String name);
}


