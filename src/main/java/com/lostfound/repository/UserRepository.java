package com.lostfound.repository;

import com.lostfound.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    /**
     * Native SQL delete — bypasses Hibernate session cache.
     * Use this instead of deleteById() when called after other
     * native SQL deletes in the same transaction to avoid
     * "Row was updated or deleted by another transaction" errors.
     */
    @Modifying
    @Query(value = "DELETE FROM users WHERE id = :userId", nativeQuery = true)
    void hardDeleteById(@Param("userId") Long userId);
}
