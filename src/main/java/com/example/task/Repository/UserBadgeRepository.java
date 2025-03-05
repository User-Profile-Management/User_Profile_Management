package com.example.task.Repository;

import com.example.task.Entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge,Integer> {
    List<UserBadge> findByUserId(String userId);
}
