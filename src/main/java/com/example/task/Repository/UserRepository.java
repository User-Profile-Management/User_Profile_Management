package com.example.task.Repository;

import com.example.task.Entity.Role;
import com.example.task.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Integer countByRole(Role role);





    @Query("SELECT u FROM User u WHERE UPPER(u.role.roleName) = UPPER(:roleName) AND u.status = :status")
    List<User> findByRoleAndStatus(@Param("roleName") String roleName, @Param("status") User.Status status);

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);


    List<User> findByStatus(User.Status status);

    Optional<User> findByUserIdAndDeletedAtIsNull(String userId);

    List<User> findByStatusAndDeletedAtIsNull(User.Status status);

    @Query("SELECT COUNT(u) FROM User u WHERE UPPER(u.role.roleName) = UPPER(:roleName) AND u.status = :status")
    Long countByRoleAndStatus(@Param("roleName") String roleName, @Param("status") User.Status status);

    List<User> findAllByDeletedAtIsNull();
}
