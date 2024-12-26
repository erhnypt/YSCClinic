package com.yscclinic.service;

import com.yscclinic.dto.UserDto;
import com.yscclinic.entity.Role;
import com.yscclinic.entity.User;
import com.yscclinic.exception.ResourceNotFoundException;
import com.yscclinic.repository.RoleRepository;
import com.yscclinic.repository.UserRepository;
import com.yscclinic.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setEnabled(true);
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
    }

    @Test
    void createUser_Success() {
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        userDto.setEmail(user.getEmail());
        userDto.setFullName(user.getFullName());

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.createUser(userDto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_DuplicateUsername() {
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        userDto.setEmail(user.getEmail());
        userDto.setFullName(user.getFullName());

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(userDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bu kullanıcı adı zaten kullanılıyor");
    }

    @Test
    void createUser_DuplicateEmail() {
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        userDto.setEmail(user.getEmail());
        userDto.setFullName(user.getFullName());

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(userDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bu e-posta adresi zaten kullanılıyor");
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1L);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(1L);
        assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Kullanıcı bulunamadı");
    }

    @Test
    void getUserByUsername_Success() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByUsername(user.getUsername());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void getUserByUsername_NotFound_ThrowsException() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByUsername(user.getUsername()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Kullanıcı bulunamadı");
    }

    @Test
    void getAllUsers_Success() {
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testuser2");

        List<User> users = Arrays.asList(user, user2);
        Page<User> page = new PageImpl<>(users);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<User> foundUsers = userService.getAllUsers(Pageable.unpaged());

        assertThat(foundUsers).isNotNull();
        assertThat(foundUsers.getContent()).hasSize(2);
        assertThat(foundUsers.getContent()).contains(user, user2);
    }

    @Test
    void updateUser_Success() {
        UserDto updateDto = new UserDto();
        updateDto.setUsername("newusername");
        updateDto.setPassword("newpassword");
        updateDto.setEmail("newemail@example.com");
        updateDto.setFullName("New Name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(1L, updateDto);

        assertThat(updatedUser).isNotNull();
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Kullanıcı bulunamadı");

        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void addRoleToUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role));

        userService.addRoleToUser(1L, "ROLE_ADMIN");

        verify(userRepository).save(user);
    }

    @Test
    void removeRoleFromUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        userService.removeRoleFromUser(1L, "ROLE_USER");

        verify(userRepository).save(user);
    }
}
