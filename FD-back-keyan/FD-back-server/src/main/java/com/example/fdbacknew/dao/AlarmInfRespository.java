package com.example.fdbacknew.dao;



import com.example.fdbacknew.entity.AlarmInf;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmInfRespository extends Neo4jRepository<AlarmInf,Long> {
    AlarmInf findByName(String name);
}


