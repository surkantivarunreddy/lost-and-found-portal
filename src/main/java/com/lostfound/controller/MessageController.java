package com.lostfound.controller;

import com.lostfound.dto.MessageDto;
import com.lostfound.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /** Send a plain text message (optionally referencing an item). */
    @PostMapping
    public ResponseEntity<MessageDto.Response> sendMessage(
            @Valid @RequestBody MessageDto.SendRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.sendMessage(request));
    }

    /**
     * NEW: Send a message with an optional image attachment and/or location.
     * Accepts multipart/form-data so the frontend can attach a file.
     */
    @PostMapping(value = "/with-media", consumes = "multipart/form-data")
    public ResponseEntity<MessageDto.Response> sendMessageWithMedia(
            @RequestParam Long receiverId,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String locationText,
            @RequestParam(required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.sendMessageWithMedia(
                        receiverId, itemId, content, locationText, image));
    }

    @GetMapping
    public ResponseEntity<List<MessageDto.Response>> getMyMessages() {
        return ResponseEntity.ok(messageService.getMyMessages());
    }

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<List<MessageDto.Response>> getConversation(
            @PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getConversation(userId));
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<MessageDto.Response>> getItemMessages(
            @PathVariable Long itemId) {
        return ResponseEntity.ok(messageService.getItemMessages(itemId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        return ResponseEntity.ok(Map.of("count", messageService.getUnreadCount()));
    }
}