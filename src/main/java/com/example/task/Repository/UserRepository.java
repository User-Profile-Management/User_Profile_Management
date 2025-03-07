package com.example.task.Repository;

import com.example.task.Entity.Role;
import com.example.task.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Integer countByRole(Role role);
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleId(String googleId);

    List<User> findByStatus(User.Status status);

    Optional<User> findById(String userId);
}