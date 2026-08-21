package com.app.wifichatbackend.repository;

import com.app.wifichatbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository<User, Long> means:
    //   - User = the entity this repository manages
    //   - Long = the type of the primary key (id)
    // You automatically get: save(), findById(), findAll(), delete(), count(), etc.

    // Spring generates the SQL automatically from the method name!
    // findByUsername → SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);

    // findByEmail → SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // existsByUsername → SELECT COUNT(*) > 0 FROM users WHERE username = ?
    boolean existsByUsername(String username);

    // existsByEmail → SELECT COUNT(*) > 0 FROM users WHERE email = ?
    boolean existsByEmail(String email);

    // findByStatus → SELECT * FROM users WHERE status = ?
    List<User> findByStatus(User.UserStatus status);

    // For custom queries that don't fit the naming convention, use @Query
    @Modifying        // Required for UPDATE/DELETE queries
    @Transactional    // Required: wraps this in a database transaction
    @Query("UPDATE User u SET u.status = :status, u.lastSeen = CURRENT_TIMESTAMP WHERE u.username = :username")
    void updateStatus(@Param("username") String username, @Param("status") User.UserStatus status);
}
