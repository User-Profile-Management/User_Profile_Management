package com.example.task.Repository;

import com.example.task.Entity.Certificate;
import com.example.task.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {

    boolean existsByUserUserIdAndCertificateName(String userId, String certificateName);
    List<Certificate> findByUserUserId(String userId);

//    boolean existsByUserUserIdAndCertificateName(String user, String certificateName);
}
