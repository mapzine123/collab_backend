package com.kgat.repository;

import com.kgat.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.profileImagePath = :profileImagePath WHERE u.id = :userId")
    void updateProfileImagePath(@Param("profileImagePath") String profileImagePath, @Param("userId") String userId);
}
