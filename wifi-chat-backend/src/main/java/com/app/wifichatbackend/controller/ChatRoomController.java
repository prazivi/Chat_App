package com.app.wifichatbackend.controller;

import com.app.wifichatbackend.model.ChatRoom;
import com.app.wifichatbackend.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Chat Rooms", description = "Create rooms, add members, and list user chat rooms")
@SecurityRequirement(name = "bearerAuth")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // POST /api/chatrooms — Create a new chat room
    @PostMapping
    @Operation(summary = "Create a chat room",
            description = "Creates a chat room and automatically adds the authenticated user as a member")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Chat room created"),
            @ApiResponse(responseCode = "400", description = "Invalid room type or request body"),
            @ApiResponse(responseCode = "401", description = "JWT token missing or invalid")
    })
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
    @Operation(summary = "Add member to chat room",
            description = "Adds a user to an existing chat room by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User added to room"),
            @ApiResponse(responseCode = "400", description = "User already exists in room or invalid request"),
            @ApiResponse(responseCode = "401", description = "JWT token missing or invalid")
    })
    public ResponseEntity<?> addMember(
            @PathVariable Long roomId,
            @RequestBody Map<String, String> body) {

        chatRoomService.addMember(roomId, body.get("username"));
        return ResponseEntity.ok(Map.of("message", "User added to room"));
    }

    // GET /api/chatrooms/my — Get all your chat rooms
    @GetMapping("/my")
    @Operation(summary = "List my chat rooms",
            description = "Returns all chat rooms where the authenticated user is a member")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chat rooms returned"),
            @ApiResponse(responseCode = "401", description = "JWT token missing or invalid")
    })
    public ResponseEntity<List<ChatRoom>> myRooms(Principal principal) {
        return ResponseEntity.ok(chatRoomService.getUserRooms(principal.getName()));
    }
}
