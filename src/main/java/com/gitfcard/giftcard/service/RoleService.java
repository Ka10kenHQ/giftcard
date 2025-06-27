package com.gitfcard.giftcard.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gitfcard.giftcard.entity.Role;
import com.gitfcard.giftcard.repository.RoleRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> findByRoleName(String name) {
        return roleRepository.findByRoleName(name);
    }

   @PostConstruct
    public void createRolesIfNotExist() {
        createRoleIfNotFound("ROLE_USER");
        createRoleIfNotFound("ROLE_ADMIN");
    }

    public void createRoleIfNotFound(String roleName) {
        if (roleRepository.findByRoleName(roleName).isEmpty()) {
            Role role = new Role(roleName);
            roleRepository.save(role);
            System.out.println("Role " + roleName + " created.");
        } else {
            System.out.println("Role " + roleName + " already exists.");
        }
    }
}

