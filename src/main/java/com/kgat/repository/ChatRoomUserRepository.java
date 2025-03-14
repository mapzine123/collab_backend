package com.kgat.repository;

import com.kgat.entity.ChatRoomUser;
import com.kgat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    List<ChatRoomUser> findByUserAndActiveTrue(User user);

    @Query("SELECT c.user FROM ChatRoomUser c WHERE c.chatRoom.id = :chatRoomId")
    List<User> findByChatRoomId(@Param("chatRoomId") String chatRoomId);

    void deleteByChatRoomIdAndUserId(@Param("chatRoomId") String chatRoomId, @Param("userId") String userId);
}
