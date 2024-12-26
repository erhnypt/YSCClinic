package com.yscclinic.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.yscclinic.entity.User;
import com.yscclinic.dto.UserDto;

public interface UserService {

    User createUser(UserDto userDto);

    User getUserById(Long id);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    Page<User> getAllUsers(Pageable pageable);

    Page<User> searchUsers(String search, Pageable pageable);

    User updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void addRoleToUser(Long userId, String roleName);

    void removeRoleFromUser(Long userId, String roleName);
}
