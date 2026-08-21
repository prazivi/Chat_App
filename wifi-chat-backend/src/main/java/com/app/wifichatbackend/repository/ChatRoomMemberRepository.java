package com.app.wifichatbackend.repository;


import com.app.wifichatbackend.model.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    // Find all rooms a user is in
    List<ChatRoomMember> findByUserId(Long userId);

    // Find all members of a room
    List<ChatRoomMember> findByChatRoomId(Long chatRoomId);

    // Check if a user is already in a room
    boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);
}
