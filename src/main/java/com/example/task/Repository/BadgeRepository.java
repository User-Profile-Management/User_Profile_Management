package com.example.task.Repository;

import com.example.task.Entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge,Integer> {
    Optional <Badge> findByName(String name);
}
