package com.example.task.Repository;

import com.example.task.Entity.UserCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCertificateRepository extends JpaRepository<UserCertificate, Integer> {
    List<UserCertificate> findByUserId(String userId);
}
