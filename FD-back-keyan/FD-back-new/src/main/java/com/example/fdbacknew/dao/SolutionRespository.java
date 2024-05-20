package com.example.fdbacknew.dao;



import com.example.fdbacknew.entity.AlarmInf;
import com.example.fdbacknew.entity.Solution;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRespository extends Neo4jRepository<Solution,Long> {
    Solution findByName(String name);
}


