package com.example.fdbacknew.dao;



import com.example.fdbacknew.entity.AlarmInf;
import com.example.fdbacknew.entity.Appear;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppearRespository extends Neo4jRepository<Appear,Long> {
    Appear findByName(String name);
}


