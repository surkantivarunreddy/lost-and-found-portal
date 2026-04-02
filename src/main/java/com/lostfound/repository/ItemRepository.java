package com.lostfound.repository;

import com.lostfound.entity.Item;
import com.lostfound.entity.Item.ItemStatus;
import com.lostfound.entity.Item.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByType(ItemType type, Pageable pageable);
    Page<Item> findByTypeAndStatus(ItemType type, ItemStatus status, Pageable pageable);
    Page<Item> findByStatus(ItemStatus status, Pageable pageable);
    List<Item> findByReportedById(Long userId);

    long countByType(ItemType type);
    long countByStatus(ItemStatus status);
    long countByReportedById(Long userId);

    /**
     * FIX: Switched from JPQL to native SQL.
     *
     * ROOT CAUSE of 500 errors: Hibernate's JPQL binding of Java enums against
     * PostgreSQL VARCHAR columns fails when params are null OR when category
     * contains '/' characters (e.g. "Wallet/Purse"). Native SQL treats all
     * params as plain strings — no enum binding, no crashes.
     */
    @Query(value = """
            SELECT * FROM items i
            WHERE i.status = :status
              AND (:type     IS NULL OR :type     = '' OR i.type     = :type)
              AND (:keyword  IS NULL OR :keyword  = ''
                   OR LOWER(i.title)       LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:category IS NULL OR :category = '' OR i.category = :category)
              AND (:location IS NULL OR :location = ''
                   OR LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%')))
            ORDER BY i.created_at DESC
            """,
            countQuery = """
            SELECT COUNT(*) FROM items i
            WHERE i.status = :status
              AND (:type     IS NULL OR :type     = '' OR i.type     = :type)
              AND (:keyword  IS NULL OR :keyword  = ''
                   OR LOWER(i.title)       LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:category IS NULL OR :category = '' OR i.category = :category)
              AND (:location IS NULL OR :location = ''
                   OR LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%')))
            """,
            nativeQuery = true)
    Page<Item> searchItems(
            @Param("keyword")  String keyword,
            @Param("type")     String type,     // String, NOT ItemType enum
            @Param("category") String category,
            @Param("location") String location,
            @Param("status")   String status,   // String, NOT ItemStatus enum
            Pageable pageable
    );

    @Modifying
    @Query(value = "DELETE FROM items WHERE id = :itemId", nativeQuery = true)
    void hardDeleteById(@Param("itemId") Long itemId);

    @Modifying
    @Query(value = "DELETE FROM items WHERE reported_by = :userId", nativeQuery = true)
    void hardDeleteByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT id FROM items WHERE reported_by = :userId", nativeQuery = true)
    List<Long> findIdsByUserId(@Param("userId") Long userId);
}