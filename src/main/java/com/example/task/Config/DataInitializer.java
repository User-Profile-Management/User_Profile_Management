package com.example.task.Config;

import com.example.task.Entity.Role;
import com.example.task.Repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer { private final RoleRepository roleRepository;

    @Autowired
    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void initializeRoles() {
        createRoleIfNotExists("STUDENT");
        createRoleIfNotExists("EMPLOYEE");
    }

    private void createRoleIfNotExists(String roleName) {
        if (roleRepository.findByRoleName(roleName) == null) {
            Role role = new Role();
            role.setRoleName(roleName);
            roleRepository.save(role);
        }
    }
}