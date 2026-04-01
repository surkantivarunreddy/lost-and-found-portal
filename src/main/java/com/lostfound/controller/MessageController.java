package com.lostfound.controller;

import com.lostfound.dto.MessageDto;
import com.lostfound.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    // ── Send plain text message (JSON body) ───────────────────
    @PostMapping
    public ResponseEntity<MessageDto.Response> sendMessage(
            @Valid @RequestBody MessageDto.SendRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(messageService.sendMessage(request));
    }

    // ── Send message with image (multipart/form-data) ─────────
    // Frontend posts:  receiverId (Long), content (String, optional),
    //                  itemId (Long, optional), image (MultipartFile)
    @PostMapping(value = "/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto.Response> sendMessageWithImage(
            @RequestParam("receiverId")                 Long receiverId,
            @RequestParam(value = "content",  required = false) String content,
            @RequestParam(value = "itemId",   required = false) Long itemId,
            @RequestParam(value = "image",    required = false) MultipartFile image) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(messageService.sendMessageWithImage(receiverId, itemId, content, image));
    }

    // ── Get all messages for current user ─────────────────────
    @GetMapping
    public ResponseEntity<List<MessageDto.Response>> getMyMessages() {
        return ResponseEntity.ok(messageService.getMyMessages());
    }

    // ── Get conversation thread ───────────────────────────────
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<List<MessageDto.Response>> getConversation(
            @PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getConversation(userId));
    }

    // ── Get messages for an item ──────────────────────────────
    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<MessageDto.Response>> getItemMessages(
            @PathVariable Long itemId) {
        return ResponseEntity.ok(messageService.getItemMessages(itemId));
    }

    // ── Mark message as read ──────────────────────────────────
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    // ── Unread count ──────────────────────────────────────────
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        return ResponseEntity.ok(Map.of("count", messageService.getUnreadCount()));
    }
}