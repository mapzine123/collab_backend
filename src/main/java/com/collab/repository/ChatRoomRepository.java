package com.collab.repository;

import com.collab.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    Optional<ChatRoom> findById(String roomId);

    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
            "JOIN cr.users cru " +
            "WHERE cru.user.id = :userId")
    List<ChatRoom> findAllByUserId(@Param("userId") String userId);
}
