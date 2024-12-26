package com.yscclinic.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yscclinic.entity.Role;
import com.yscclinic.entity.User;
import com.yscclinic.repository.UserRepository;
import com.yscclinic.service.RoleService;
import com.yscclinic.service.UserService;
import com.yscclinic.dto.UserDto;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kullanıcı bulunamadı: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Kullanıcı bulunamadı: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Kullanıcı bulunamadı: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String search, Pageable pageable) {
        return userRepository.searchUsers(search, pageable);
    }

    @Override
    public User updateUser(Long id, UserDto userDto) {
        User user = getUserById(id);
        user.setUsername(userDto.getUsername());
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Kullanıcı bulunamadı: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void addRoleToUser(Long userId, String roleName) {
        User user = getUserById(userId);
        Role role = roleService.getRoleByName(roleName);
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public void removeRoleFromUser(Long userId, String roleName) {
        User user = getUserById(userId);
        Role role = roleService.getRoleByName(roleName);
        user.getRoles().remove(role);
        userRepository.save(user);
    }
}
