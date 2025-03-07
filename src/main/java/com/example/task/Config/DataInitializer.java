package com.example.task.Config;
<<<<<<< HEAD

=======
>>>>>>> 3a184786dd3f0b4132fd4967c5e52ff40c099b75
import com.example.task.Entity.Role;
import com.example.task.Repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
<<<<<<< HEAD

=======
>>>>>>> 3a184786dd3f0b4132fd4967c5e52ff40c099b75
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