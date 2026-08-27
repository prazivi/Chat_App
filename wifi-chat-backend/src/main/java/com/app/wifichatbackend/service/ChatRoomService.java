package com.app.wifichatbackend.service;


import com.app.wifichatbackend.model.ChatRoom;
import com.app.wifichatbackend.model.ChatRoomMember;
import com.app.wifichatbackend.model.User;
import com.app.wifichatbackend.repository.ChatRoomMemberRepository;
import com.app.wifichatbackend.repository.ChatRoomRepository;
import com.app.wifichatbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository memberRepository;
    private final UserRepository userRepository;

    // Create a new chat room and add the creator as a member
    @Transactional
    public ChatRoom createRoom(String name, ChatRoom.RoomType type, String creatorUsername) {
        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create the room
        ChatRoom room = ChatRoom.builder()
                .name(name)
                .type(type)
                .build();
        ChatRoom savedRoom = chatRoomRepository.save(room);

        // Add creator as a member
        ChatRoomMember member = ChatRoomMember.builder()
                .chatRoomId(savedRoom.getId())
                .userId(creator.getId())
                .build();
        memberRepository.save(member);

        return savedRoom;
    }

    // Add a user to a chat room
    @Transactional
    public void addMember(Long chatRoomId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (memberRepository.existsByChatRoomIdAndUserId(chatRoomId, user.getId())) {
            throw new RuntimeException("User is already a member of this room");
        }

        ChatRoomMember member = ChatRoomMember.builder()
                .chatRoomId(chatRoomId)
                .userId(user.getId())
                .build();
        memberRepository.save(member);
    }

    // Get all rooms a user belongs to
    public List<ChatRoom> getUserRooms(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ChatRoomMember> memberships = memberRepository.findByUserId(user.getId());
        List<Long> roomIds = memberships.stream()
                .map(ChatRoomMember::getChatRoomId)
                .toList();

        return chatRoomRepository.findAllById(roomIds);
    }
}
