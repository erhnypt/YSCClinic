package com.yscclinic.controller;

import com.yscclinic.dto.RestResponse;
import com.yscclinic.dto.ErrorResponse;
import com.yscclinic.entity.Role;
import com.yscclinic.service.RoleService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Rol Yönetimi", description = "Rol yönetimi için API")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Yeni rol oluştur", description = "Yeni bir rol oluşturur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol başarıyla oluşturuldu",
                content = @Content(schema = @Schema(implementation = Role.class))),
        @ApiResponse(responseCode = "400", description = "Geçersiz rol bilgileri",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<Role>> createRole(
            @Parameter(description = "Rol bilgileri", required = true)
            @Valid @RequestBody Role role) {
        Role createdRole = roleService.createRole(role);
        return ResponseEntity.ok(RestResponse.success("Rol başarıyla oluşturuldu", createdRole));
    }

    @Operation(summary = "Rol getir", description = "ID'ye göre rol getirir")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol bulundu",
                content = @Content(schema = @Schema(implementation = Role.class))),
        @ApiResponse(responseCode = "404", description = "Rol bulunamadı",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<Role>> getRoleById(
            @Parameter(description = "Rol ID", required = true)
            @PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(RestResponse.success(role));
    }

    @Operation(summary = "Rol getir", description = "İsme göre rol getirir")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol bulundu",
                content = @Content(schema = @Schema(implementation = Role.class))),
        @ApiResponse(responseCode = "404", description = "Rol bulunamadı",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<Role>> getRoleByName(
            @Parameter(description = "Rol adı", required = true)
            @PathVariable String name) {
        Role role = roleService.getRoleByName(name);
        return ResponseEntity.ok(RestResponse.success(role));
    }

    @Operation(summary = "Tüm rolleri getir", description = "Sistemdeki tüm rolleri listeler")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Roller başarıyla getirildi",
                content = @Content(schema = @Schema(implementation = Role.class)))
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<List<Role>>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(RestResponse.success(roles));
    }

    @Operation(summary = "Rol güncelle", description = "ID'ye göre rol günceller")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol başarıyla güncellendi",
                content = @Content(schema = @Schema(implementation = Role.class))),
        @ApiResponse(responseCode = "404", description = "Rol bulunamadı",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<Role>> updateRole(
            @Parameter(description = "Rol ID", required = true) @PathVariable Long id,
            @Parameter(description = "Güncellenmiş rol bilgileri", required = true)
            @Valid @RequestBody Role role) {
        Role updatedRole = roleService.updateRole(id, role);
        return ResponseEntity.ok(RestResponse.success("Rol başarıyla güncellendi", updatedRole));
    }

    @Operation(summary = "Rol sil", description = "ID'ye göre rol siler")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol başarıyla silindi"),
        @ApiResponse(responseCode = "404", description = "Rol bulunamadı",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<?>> deleteRole(
            @Parameter(description = "Rol ID", required = true)
            @PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(RestResponse.success("Rol başarıyla silindi", null));
    }
}
