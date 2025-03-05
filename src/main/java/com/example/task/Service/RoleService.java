package com.example.task.Service;

import com.example.task.Entity.Role;
import com.example.task.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public Role assignRole(String roleName) {
        Role role = roleRepository.findByRoleName(roleName);
        if (role == null) {
            throw new RuntimeException("Role not found: " + roleName);
        }
        return role;
    }


    public Optional<Role> getRoleById(Integer roleId) {
        return roleRepository.findById(roleId);
    }


    public Role getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }


    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }


    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

}

