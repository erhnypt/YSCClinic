package com.yscclinic.controller;

import com.yscclinic.dto.RestResponse;
import com.yscclinic.dto.PatientDto;
import com.yscclinic.entity.Patient;
import com.yscclinic.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Hasta Yönetimi", description = "Hasta yönetimi için API")
public class PatientController {

    private final PatientService patientService;

    @Operation(summary = "Yeni hasta oluştur", description = "Yeni bir hasta kaydı oluşturur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hasta başarıyla oluşturuldu",
                content = @Content(schema = @Schema(implementation = Patient.class))),
        @ApiResponse(responseCode = "400", description = "Geçersiz hasta bilgileri",
                content = @Content(schema = @Schema(implementation = com.yscclinic.dto.ErrorResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<RestResponse<Patient>> createPatient(
            @Parameter(description = "Hasta bilgileri", required = true)
            @Valid @RequestBody PatientDto patientDto) {
        Patient patient = new Patient();
        patient.setTcNo(patientDto.getTcNo());
        patient.setFullName(patientDto.getFullName());
        patient.setBirthDate(patientDto.getBirthDate());
        patient.setGender(patientDto.getGender());
        patient.setPhone(patientDto.getPhone());
        patient.setEmail(patientDto.getEmail());
        patient.setAddress(patientDto.getAddress());

        Patient createdPatient = patientService.createPatient(patient);
        return ResponseEntity.ok(RestResponse.success("Hasta başarıyla oluşturuldu", createdPatient));
    }

    @Operation(summary = "Hasta getir", description = "ID'ye göre hasta getirir")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hasta bulundu",
                content = @Content(schema = @Schema(implementation = Patient.class))),
        @ApiResponse(responseCode = "404", description = "Hasta bulunamadı",
                content = @Content(schema = @Schema(implementation = com.yscclinic.dto.ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<RestResponse<Patient>> getPatientById(
            @Parameter(description = "Hasta ID", required = true)
            @PathVariable Long id) {
        Patient patient = patientService.getPatientById(id);
        return ResponseEntity.ok(RestResponse.success(patient));
    }

    @Operation(summary = "TC No ile hasta getir", description = "TC Kimlik numarasına göre hasta getirir")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hasta bulundu",
                content = @Content(schema = @Schema(implementation = Patient.class))),
        @ApiResponse(responseCode = "404", description = "Hasta bulunamadı",
                content = @Content(schema = @Schema(implementation = com.yscclinic.dto.ErrorResponse.class)))
    })
    @GetMapping("/tc/{tcNo}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<RestResponse<Patient>> getPatientByTcNo(
            @Parameter(description = "TC Kimlik No", required = true)
            @PathVariable String tcNo) {
        Patient patient = patientService.getPatientByTcNo(tcNo);
        return ResponseEntity.ok(RestResponse.success(patient));
    }

    @Operation(summary = "Tüm hastaları getir", description = "Sistemdeki tüm hastaları listeler")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hastalar başarıyla getirildi",
                content = @Content(schema = @Schema(implementation = Patient.class)))
    })
    @GetMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<RestResponse<Page<Patient>>> getAllPatients(Pageable pageable) {
        Page<Patient> patients = patientService.getAllPatients(pageable);
        return ResponseEntity.ok(RestResponse.success(patients));
    }

    @Operation(summary = "İsme göre hasta ara", description = "İsme göre hasta arar")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Arama başarılı",
                content = @Content(schema = @Schema(implementation = Patient.class)))
    })
    @GetMapping("/search")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<RestResponse<Page<Patient>>> searchPatients(
            @Parameter(description = "Arama terimi", required = true)
            @RequestParam String query,
            Pageable pageable) {
        Page<Patient> patients = patientService.searchPatients(query, pageable);
        return ResponseEntity.ok(RestResponse.success(patients));
    }

    @Operation(summary = "Hasta güncelle", description = "ID'ye göre hasta günceller")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hasta başarıyla güncellendi",
                content = @Content(schema = @Schema(implementation = Patient.class))),
        @ApiResponse(responseCode = "404", description = "Hasta bulunamadı",
                content = @Content(schema = @Schema(implementation = com.yscclinic.dto.ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<RestResponse<Patient>> updatePatient(
            @Parameter(description = "Hasta ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Güncellenmiş hasta bilgileri", required = true)
            @Valid @RequestBody PatientDto patientDto) {
        Patient patient = new Patient();
        patient.setTcNo(patientDto.getTcNo());
        patient.setFullName(patientDto.getFullName());
        patient.setBirthDate(patientDto.getBirthDate());
        patient.setGender(patientDto.getGender());
        patient.setPhone(patientDto.getPhone());
        patient.setEmail(patientDto.getEmail());
        patient.setAddress(patientDto.getAddress());

        Patient updatedPatient = patientService.updatePatient(id, patient);
        return ResponseEntity.ok(RestResponse.success("Hasta başarıyla güncellendi", updatedPatient));
    }

    @Operation(summary = "Hasta sil", description = "ID'ye göre hasta siler")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hasta başarıyla silindi"),
        @ApiResponse(responseCode = "404", description = "Hasta bulunamadı",
                content = @Content(schema = @Schema(implementation = com.yscclinic.dto.ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<RestResponse<?>> deletePatient(
            @Parameter(description = "Hasta ID", required = true)
            @PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok(RestResponse.success("Hasta başarıyla silindi", null));
    }
}
