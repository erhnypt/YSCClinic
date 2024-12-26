package com.yscclinic.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yscclinic.entity.Role;
import com.yscclinic.repository.RoleRepository;
import com.yscclinic.service.RoleService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException("Bu rol adı zaten mevcut: " + role.getName());
        }
        return roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol bulunamadı: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Rol bulunamadı: " + name));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role updateRole(Long id, Role role) {
        Role existingRole = getRoleById(id);
        if (!existingRole.getName().equals(role.getName()) && roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException("Bu rol adı zaten mevcut: " + role.getName());
        }
        existingRole.setName(role.getName());
        return roleRepository.save(existingRole);
    }

    @Override
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new EntityNotFoundException("Rol bulunamadı: " + id);
        }
        roleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
}
