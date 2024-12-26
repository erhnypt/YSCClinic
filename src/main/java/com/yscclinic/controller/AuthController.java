package com.yscclinic.controller;

import com.yscclinic.dto.RestResponse;
import com.yscclinic.dto.JwtResponse;
import com.yscclinic.dto.LoginRequest;
import com.yscclinic.dto.UserDto;
import com.yscclinic.entity.Role;
import com.yscclinic.entity.User;
import com.yscclinic.security.JwtTokenProvider;
import com.yscclinic.service.RoleService;
import com.yscclinic.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Kimlik Doğrulama", description = "Kimlik doğrulama ve kayıt işlemleri için API")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final RoleService roleService;

    @Operation(
            summary = "Kullanıcı girişi",
            description = "Kullanıcı adı ve şifre ile giriş yaparak JWT token alır"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Başarılı giriş",
                content = @Content(schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "401", description = "Geçersiz kimlik bilgileri",
                content = @Content(schema = @Schema(implementation = com.yscclinic.dto.ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<RestResponse<JwtResponse>> login(
            @Parameter(description = "Giriş bilgileri", required = true)
            @Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        JwtResponse response = new JwtResponse(jwt, loginRequest.getUsername(), roles);
        return ResponseEntity.ok(RestResponse.success("Giriş başarılı", response));
    }

    @Operation(
            summary = "Yeni kullanıcı kaydı",
            description = "Yeni bir kullanıcı oluşturur"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Başarılı kayıt",
                content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Geçersiz bilgiler",
                content = @Content(schema = @Schema(implementation = com.yscclinic.dto.ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<RestResponse<User>> register(
            @Parameter(description = "Kullanıcı bilgileri", required = true)
            @Valid @RequestBody UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        user.setEnabled(true);

        Set<Role> roles = new HashSet<>();
        roles.add(roleService.getRoleByName("ROLE_STAFF"));
        user.setRoles(roles);

        User createdUser = userService.createUser(userDto);
        return ResponseEntity.ok(RestResponse.success("Kayıt başarılı", createdUser));
    }
}
