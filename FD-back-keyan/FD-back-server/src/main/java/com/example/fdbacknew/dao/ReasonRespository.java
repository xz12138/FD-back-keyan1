package com.example.fdbacknew.dao;



import com.example.fdbacknew.entity.AlarmInf;
import com.example.fdbacknew.entity.Reason;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReasonRespository extends Neo4jRepository<Reason,Long> {
    Reason findByName(String name);
}


