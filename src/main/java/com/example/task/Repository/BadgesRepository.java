package com.example.task.Repository;

import com.example.task.Entity.Badges;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgesRepository extends JpaRepository<Badges,Integer> {
}
