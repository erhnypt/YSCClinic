package com.yscclinic.service;

import com.yscclinic.entity.Role;
import com.yscclinic.exception.ResourceNotFoundException;
import com.yscclinic.repository.RoleRepository;
import com.yscclinic.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_TEST");
    }

    @Test
    void createRole_Success() {
        when(roleRepository.existsByName(role.getName())).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role savedRole = roleService.createRole(role);

        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getName()).isEqualTo(role.getName());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void createRole_DuplicateName_ThrowsException() {
        when(roleRepository.existsByName(role.getName())).thenReturn(true);

        assertThatThrownBy(() -> roleService.createRole(role))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bu rol adı zaten mevcut");

        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void getRoleById_Success() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Role foundRole = roleService.getRoleById(1L);

        assertThat(foundRole).isNotNull();
        assertThat(foundRole.getId()).isEqualTo(1L);
        assertThat(foundRole.getName()).isEqualTo(role.getName());
    }

    @Test
    void getRoleById_NotFound_ThrowsException() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getRoleById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Rol bulunamadı");
    }

    @Test
    void getRoleByName_Success() {
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));

        Role foundRole = roleService.getRoleByName(role.getName());

        assertThat(foundRole).isNotNull();
        assertThat(foundRole.getName()).isEqualTo(role.getName());
    }

    @Test
    void getRoleByName_NotFound_ThrowsException() {
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getRoleByName(role.getName()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Rol bulunamadı");
    }

    @Test
    void getAllRoles_Success() {
        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("ROLE_TEST2");

        List<Role> roles = Arrays.asList(role, role2);
        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> foundRoles = roleService.getAllRoles();

        assertThat(foundRoles).hasSize(2);
        assertThat(foundRoles).contains(role, role2);
    }

    @Test
    void updateRole_Success() {
        Role updatedRole = new Role();
        updatedRole.setName("ROLE_UPDATED");

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.existsByName(updatedRole.getName())).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(updatedRole);

        Role result = roleService.updateRole(1L, updatedRole);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(updatedRole.getName());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void updateRole_DuplicateName_ThrowsException() {
        Role updatedRole = new Role();
        updatedRole.setName("ROLE_UPDATED");

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.existsByName(updatedRole.getName())).thenReturn(true);

        assertThatThrownBy(() -> roleService.updateRole(1L, updatedRole))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bu rol adı zaten mevcut");

        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void deleteRole_Success() {
        when(roleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(roleRepository).deleteById(1L);

        roleService.deleteRole(1L);

        verify(roleRepository).deleteById(1L);
    }

    @Test
    void deleteRole_NotFound_ThrowsException() {
        when(roleRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> roleService.deleteRole(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Rol bulunamadı");

        verify(roleRepository, never()).deleteById(anyLong());
    }
}
