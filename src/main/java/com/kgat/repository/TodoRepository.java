package com.kgat.repository;

import com.kgat.entity.Todo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends CrudRepository<Todo,Integer> {
    @Query("SELECT t FROM Todo t JOIN t.user u WHERE u.id = :userId")
    List<Todo> findAllByUser(@Param("userId") String userId);

}
