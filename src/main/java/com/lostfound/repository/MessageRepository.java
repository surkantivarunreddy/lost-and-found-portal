package com.lostfound.repository;

import com.lostfound.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // All messages where user is sender or receiver (for inbox view)
    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender.id = :userId OR m.receiver.id = :userId) " +
           "ORDER BY m.sentAt DESC")
    List<Message> findByUserId(@Param("userId") Long userId);

    // Conversation thread between two users
    @Query("SELECT m FROM Message m WHERE " +
           "((m.sender.id = :user1 AND m.receiver.id = :user2) OR " +
           "(m.sender.id = :user2 AND m.receiver.id = :user1)) " +
           "ORDER BY m.sentAt ASC")
    List<Message> findConversation(@Param("user1") Long user1,
                                   @Param("user2") Long user2);

    // Messages linked to a specific item
    List<Message> findByItemId(Long itemId);

    // Unread count for notification badge
    long countByReceiverIdAndIsReadFalse(Long receiverId);

    // ── Native SQL deletes (bypass Hibernate cache — no stale state errors) ──

    /** Delete all messages where user is sender OR receiver. */
    @Modifying
    @Query(value = "DELETE FROM messages WHERE sender_id = :userId OR receiver_id = :userId",
           nativeQuery = true)
    void hardDeleteByUserId(@Param("userId") Long userId);

    /** Delete all messages referencing a specific item. */
    @Modifying
    @Query(value = "DELETE FROM messages WHERE item_id = :itemId",
           nativeQuery = true)
    void hardDeleteByItemId(@Param("itemId") Long itemId);

    /** Delete all messages referencing any of the given item IDs. */
    @Modifying
    @Query(value = "DELETE FROM messages WHERE item_id IN :itemIds",
           nativeQuery = true)
    void hardDeleteByItemIds(@Param("itemIds") List<Long> itemIds);
}
