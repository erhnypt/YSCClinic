package com.yscclinic.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yscclinic.dto.RestResponse;
import com.yscclinic.dto.UserDto;
import com.yscclinic.entity.User;
import com.yscclinic.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RestResponse<User>> registerUser(@Valid @RequestBody UserDto userDto) {
        User user = userService.createUser(userDto);
        return ResponseEntity.ok(RestResponse.success("Kullanıcı başarıyla oluşturuldu", user));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(RestResponse.success(user));
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<User>> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(RestResponse.success(user));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<Page<User>>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(RestResponse.success(users));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<Page<User>>> searchUsers(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<User> users = userService.searchUsers(query, pageable);
        return ResponseEntity.ok(RestResponse.success(users));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<User>> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        User updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(RestResponse.success("Kullanıcı başarıyla güncellendi", updatedUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<?>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(RestResponse.success("Kullanıcı başarıyla silindi", null));
    }

    @PostMapping("/{userId}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<?>> addRoleToUser(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        userService.addRoleToUser(userId, roleName);
        return ResponseEntity.ok(RestResponse.success("Rol başarıyla eklendi", null));
    }

    @DeleteMapping("/{userId}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<?>> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        userService.removeRoleFromUser(userId, roleName);
        return ResponseEntity.ok(RestResponse.success("Rol başarıyla kaldırıldı", null));
    }
}
