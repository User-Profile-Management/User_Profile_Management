package com.example.task.Repository;

import com.example.task.Entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Integer> {
    List<UserBadge> findByUserId(String userId);

    List<UserBadge> findByUserIdAndDeletedAtIsNull(String userId); // Only fetch active badges


}
