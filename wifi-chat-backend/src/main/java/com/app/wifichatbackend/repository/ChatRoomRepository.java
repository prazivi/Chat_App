package com.app.wifichatbackend.repository;


import com.app.wifichatbackend.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // JpaRepository gives us everything we need for now
    // save(), findById(), findAll(), deleteById() — all free!
}