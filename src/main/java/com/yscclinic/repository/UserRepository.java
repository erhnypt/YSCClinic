package com.yscclinic.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.yscclinic.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE "
            + "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> searchUsers(String search, Pageable pageable);
}
