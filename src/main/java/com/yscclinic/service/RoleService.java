package com.yscclinic.service;

import java.util.List;

import com.yscclinic.entity.Role;

public interface RoleService {

    Role createRole(Role role);

    Role getRoleById(Long id);

    Role getRoleByName(String name);

    List<Role> getAllRoles();

    Role updateRole(Long id, Role role);

    void deleteRole(Long id);

    boolean existsByName(String name);
}
