package com.lostfound.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lostfound.dto.MessageDto;
import com.lostfound.entity.Item;
import com.lostfound.entity.Message;
import com.lostfound.entity.User;
import com.lostfound.exception.ResourceNotFoundException;
import com.lostfound.repository.ItemRepository;
import com.lostfound.repository.MessageRepository;
import com.lostfound.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Cloudinary cloudinary;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          ItemRepository itemRepository,
                          Cloudinary cloudinary) {
        this.messageRepository = messageRepository;
        this.userRepository    = userRepository;
        this.itemRepository    = itemRepository;
        this.cloudinary        = cloudinary;
    }

    // ── Send plain text message ───────────────────────────────
    public MessageDto.Response sendMessage(MessageDto.SendRequest request) {
        User sender   = getCurrentUser();
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        Item item = null;
        if (request.getItemId() != null) {
            item = itemRepository.findById(request.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        }

        Message message = Message.builder()
                .content(request.getContent())
                .sender(sender)
                .receiver(receiver)
                .item(item)
                .build();

        return mapToResponse(messageRepository.save(message));
    }

    // ── Send message with image (multipart) ───────────────────
    public MessageDto.Response sendMessageWithImage(Long receiverId,
                                                    Long itemId,
                                                    String content,
                                                    MultipartFile imageFile) {
        User sender   = getCurrentUser();
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        Item item = null;
        if (itemId != null) {
            item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        }

        // Upload image to Cloudinary
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(
                        imageFile.getBytes(),
                        ObjectUtils.asMap(
                                "folder",          "lostfound/messages",
                                "resource_type",   "image",
                                "transformation",  "q_auto,f_auto,w_1200,c_limit"
                        )
                );
                imageUrl = uploadResult.get("secure_url").toString();
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image to Cloudinary: " + e.getMessage());
            }
        }

        Message message = Message.builder()
                .content((content != null && !content.isBlank()) ? content : null)
                .sender(sender)
                .receiver(receiver)
                .item(item)
                .imageUrl(imageUrl)
                .build();

        return mapToResponse(messageRepository.save(message));
    }

    // ── Get all messages for current user ─────────────────────
    public List<MessageDto.Response> getMyMessages() {
        User currentUser = getCurrentUser();
        return messageRepository.findByUserId(currentUser.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ── Get conversation thread ───────────────────────────────
    public List<MessageDto.Response> getConversation(Long otherUserId) {
        User currentUser = getCurrentUser();
        return messageRepository.findConversation(currentUser.getId(), otherUserId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ── Get messages linked to an item ────────────────────────
    public List<MessageDto.Response> getItemMessages(Long itemId) {
        return messageRepository.findByItemId(itemId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ── Mark message as read ──────────────────────────────────
    @Transactional
    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        message.setRead(true);
        messageRepository.save(message);
    }

    // ── Unread count ──────────────────────────────────────────
    public long getUnreadCount() {
        User currentUser = getCurrentUser();
        return messageRepository.countByReceiverIdAndIsReadFalse(currentUser.getId());
    }

    // ── Helpers ───────────────────────────────────────────────
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private MessageDto.Response mapToResponse(Message message) {
        return MessageDto.Response.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getName())
                .receiverId(message.getReceiver().getId())
                .receiverName(message.getReceiver().getName())
                .itemId(message.getItem() != null ? message.getItem().getId() : null)
                .itemTitle(message.getItem() != null ? message.getItem().getTitle() : null)
                .isRead(message.isRead())
                .sentAt(message.getSentAt())
                .imageUrl(message.getImageUrl())
                .build();
    }
}