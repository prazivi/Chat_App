package com.app.wifichatbackend.controller;

import com.app.wifichatbackend.model.ChatRoom;
import com.app.wifichatbackend.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // POST /api/chatrooms — Create a new chat room
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody Map<String, String> body, Principal principal) {
        String name = body.get("name");
        String type = body.getOrDefault("type", "GROUP");

        ChatRoom room = chatRoomService.createRoom(
                name,
                ChatRoom.RoomType.valueOf(type),
                principal.getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    // POST /api/chatrooms/{roomId}/members — Add a user to a room
    @PostMapping("/{roomId}/members")
    public ResponseEntity<?> addMember(
            @PathVariable Long roomId,
            @RequestBody Map<String, String> body) {

        chatRoomService.addMember(roomId, body.get("username"));
        return ResponseEntity.ok(Map.of("message", "User added to room"));
    }

    // GET /api/chatrooms/my — Get all your chat rooms
    @GetMapping("/my")
    public ResponseEntity<List<ChatRoom>> myRooms(Principal principal) {
        return ResponseEntity.ok(chatRoomService.getUserRooms(principal.getName()));
    }
}
