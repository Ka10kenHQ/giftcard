package com.gitfcard.giftcard.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gitfcard.giftcard.entity.Role;
import com.gitfcard.giftcard.repository.RoleRepository;

import jakarta.annotation.PostConstruct;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

   @PostConstruct
    public void createRolesIfNotExist() {
        createRoleIfNotFound("ROLE_USER");
        createRoleIfNotFound("ROLE_ADMIN");
    }

    public void createRoleIfNotFound(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role(roleName);
            roleRepository.save(role);
            System.out.println("Role " + roleName + " created.");
        } else {
            System.out.println("Role " + roleName + " already exists.");
        }
    }
}

