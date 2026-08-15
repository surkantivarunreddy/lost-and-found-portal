package com.lostfound.service;
 
import com.lostfound.dto.AdminDto;
import com.lostfound.entity.Item;
import com.lostfound.entity.Item.ItemStatus;
import com.lostfound.entity.Item.ItemType;
import com.lostfound.entity.User;
import com.lostfound.entity.User.Role;
import com.lostfound.exception.ResourceNotFoundException;
import com.lostfound.repository.ItemRepository;
import com.lostfound.repository.MessageRepository;
import com.lostfound.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.List;
import java.util.stream.Collectors;
 
@Service
public class AdminService {
 
    private final UserRepository    userRepository;
    private final ItemRepository    itemRepository;
    private final MessageRepository messageRepository;
 
    public AdminService(UserRepository userRepository,
                        ItemRepository itemRepository,
                        MessageRepository messageRepository) {
        this.userRepository    = userRepository;
        this.itemRepository    = itemRepository;
        this.messageRepository = messageRepository;
    }
 
    // ── Stats ──────────────────────────────────────────────
    public AdminDto.StatsResponse getStats() {
        long totalUsers    = userRepository.count();
        long totalItems    = itemRepository.count();
        long totalMessages = messageRepository.count();
        long lostItems     = itemRepository.countByType(ItemType.LOST);
        long foundItems    = itemRepository.countByType(ItemType.FOUND);
        long activeItems   = itemRepository.countByStatus(ItemStatus.ACTIVE);
        long resolvedItems = itemRepository.countByStatus(ItemStatus.RESOLVED);
 
        return new AdminDto.StatsResponse(
                totalUsers, totalItems, totalMessages,
                lostItems, foundItems, activeItems, resolvedItems);
    }
 
    // ── Users ──────────────────────────────────────────────
    public List<AdminDto.UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> AdminDto.UserResponse.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .phone(u.getPhone())
                        .role(u.getRole().name())
                        .createdAt(u.getCreatedAt())
                        .itemCount(itemRepository.countByReportedById(u.getId()))
                        .build())
                .collect(Collectors.toList());
    }
 
    @Transactional
    public AdminDto.UserResponse updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setRole(newRole);
        userRepository.save(user);
        return AdminDto.UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .itemCount(itemRepository.countByReportedById(user.getId()))
                .build();
    }
 
    /**
     * Delete a user and all their data using 100% native SQL.
     *
     * WHY native SQL only:
     *   Mixing native SQL deletes with JPA entity deletes causes:
     *   "Row was updated or deleted by another transaction"
     *   because Hibernate's session cache still holds the entity after
     *   the native DELETE already removed the row from the DB.
     *
     * Safe deletion order (respects all FK constraints):
     *   1. messages WHERE sender_id = userId OR receiver_id = userId
     *      → clears all message FK refs to this user AND their items' messages
     *   2. messages WHERE item_id IN (SELECT id FROM items WHERE reported_by = userId)
     *      → clears messages from OTHER users about this user's items
     *   3. items WHERE reported_by = userId
     *      → clears item FK ref to user
     *   4. users WHERE id = userId
     *      → user row deleted cleanly
     */
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
 
        // Step 1: Delete all messages sent or received by this user
        messageRepository.hardDeleteByUserId(userId);
 
        // Step 2: Get this user's item IDs, then delete any remaining messages
        // referencing those items (sent by OTHER users about this user's items)
        List<Long> itemIds = itemRepository.findIdsByUserId(userId);
        if (!itemIds.isEmpty()) {
            messageRepository.hardDeleteByItemIds(itemIds);
        }
 
        // Step 3: Delete all items owned by this user
        itemRepository.hardDeleteByUserId(userId);
 
        // Step 4: Delete the user row — zero FK references remain
        userRepository.hardDeleteById(userId);
    }
 
    // ── Items ──────────────────────────────────────────────
    public List<AdminDto.ItemResponse> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(this::mapItem)
                .collect(Collectors.toList());
    }
 
    @Transactional
    public AdminDto.ItemResponse updateItemStatus(Long itemId, ItemStatus newStatus) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + itemId));
        item.setStatus(newStatus);
        return mapItem(itemRepository.save(item));
    }
 
    /**
     * Delete an item using 100% native SQL.
     *
     * Safe deletion order:
     *   1. messages WHERE item_id = itemId  → clears item FK in messages
     *   2. items WHERE id = itemId          → item row deleted cleanly
     */
    @Transactional
    public void deleteItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Item not found: " + itemId);
        }
 
        // Step 1: Remove all messages referencing this item
        messageRepository.hardDeleteByItemId(itemId);
 
        // Step 2: Delete the item row directly
        itemRepository.hardDeleteById(itemId);
    }
 
    private AdminDto.ItemResponse mapItem(Item item) {
        return AdminDto.ItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .type(item.getType().name())
                .status(item.getStatus().name())
                .category(item.getCategory())
                .location(item.getLocation())
                .reportedByName(item.getReportedBy().getName())
                .reportedByEmail(item.getReportedBy().getEmail())
                .dateLostFound(item.getDateLostFound())
                .createdAt(item.getCreatedAt())
                .build();
    }
}