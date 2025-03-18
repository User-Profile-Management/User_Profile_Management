package com.example.task.Repository;

import com.example.task.Entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Integer> {
    List<UserBadge> findByUserId(String userId);

    List<UserBadge> findByUserIdAndDeletedAtIsNull(String userId); // Only fetch active badges


    @Query("SELECT COUNT(ub) > 0 FROM UserBadge ub " +
            "JOIN ub.badge b " +
            "WHERE ub.userId = :userId AND b.name = :badgeName")
    boolean existsByUserIdAndBadgeName(@Param("userId") String userId, @Param("badgeName") String badgeName);
}
