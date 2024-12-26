package com.yscclinic.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yscclinic.dto.LoginRequest;
import com.yscclinic.entity.Patient;
import com.yscclinic.entity.Role;
import com.yscclinic.entity.User;
import com.yscclinic.repository.PatientRepository;
import com.yscclinic.repository.RoleRepository;
import com.yscclinic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Timeout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.HashSet;
import java.util.Collections;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PatientIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Patient patient;
    private User user;
    private String token;

    @BeforeEach
    void setUp() {
        // Admin rolü oluştur
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        role = roleRepository.save(role);

        // Admin kullanıcısı oluştur
        user = new User();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("admin@example.com");
        user.setFullName("Admin User");
        user.setEnabled(true);
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        user = userRepository.save(user);

        // Test hastası oluştur
        patient = new Patient();
        patient.setTcNo("12345678901");
        patient.setFullName("Test Patient");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setGender(Patient.Gender.ERKEK);
        patient.setPhone("5551234567");
        patient.setEmail("patient@example.com");
        patient.setAddress("Test Address");
        patient = patientRepository.save(patient);

        // Login yap ve token al
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("password");

        try {
            String result = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.token").exists())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            token = objectMapper.readTree(result).get("data").get("token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Token alınamadı", e);
        }
    }

    @Test
    void getAllPatients_Success() throws Exception {
        mockMvc.perform(get("/api/patients")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].tcNo").value(patient.getTcNo()));
    }

    @Test
    void getPatientById_Success() throws Exception {
        mockMvc.perform(get("/api/patients/" + patient.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tcNo").value(patient.getTcNo()));
    }

    @Test
    void getPatientByTcNo_Success() throws Exception {
        mockMvc.perform(get("/api/patients/tc/" + patient.getTcNo())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tcNo").value(patient.getTcNo()));
    }

    @Test
    void createPatient_Success() throws Exception {
        Patient newPatient = new Patient();
        newPatient.setTcNo("98765432109");
        newPatient.setFullName("New Patient");
        newPatient.setBirthDate(LocalDate.of(1995, 1, 1));
        newPatient.setGender(Patient.Gender.KADIN);
        newPatient.setPhone("5559876543");
        newPatient.setEmail("new.patient@example.com");
        newPatient.setAddress("New Address");

        mockMvc.perform(post("/api/patients")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tcNo").value(newPatient.getTcNo()));
    }

    @Test
    void updatePatient_Success() throws Exception {
        Patient updatedPatient = new Patient();
        updatedPatient.setTcNo(patient.getTcNo());
        updatedPatient.setFullName("Updated Patient");
        updatedPatient.setBirthDate(patient.getBirthDate());
        updatedPatient.setGender(patient.getGender());
        updatedPatient.setPhone("5553334444");
        updatedPatient.setEmail("updated.patient@example.com");
        updatedPatient.setAddress("Updated Address");

        mockMvc.perform(put("/api/patients/" + patient.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPatient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.firstName").value(updatedPatient.getFullName()));
    }

    @Test
    void deletePatient_Success() throws Exception {
        mockMvc.perform(delete("/api/patients/" + patient.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Hasta başarıyla silindi"));
    }

    @Test
    void createPatient_DuplicateTcNo_Failure() throws Exception {
        Patient duplicatePatient = new Patient();
        duplicatePatient.setTcNo(patient.getTcNo()); // Var olan TC No
        duplicatePatient.setFullName("Duplicate Patient");
        duplicatePatient.setBirthDate(LocalDate.of(1995, 1, 1));
        duplicatePatient.setGender(Patient.Gender.ERKEK);
        duplicatePatient.setPhone("5559876543");
        duplicatePatient.setEmail("duplicate@example.com");
        duplicatePatient.setAddress("Duplicate Address");

        mockMvc.perform(post("/api/patients")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicatePatient)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Bu TC Kimlik numarası ile kayıtlı bir hasta zaten mevcut"));
    }

    @Test
    void getPatientById_NotFound_Failure() throws Exception {
        mockMvc.perform(get("/api/patients/999999")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Hasta bulunamadı"));
    }

    @Test
    void createPatient_InvalidTcNo_Failure() throws Exception {
        Patient invalidPatient = new Patient();
        invalidPatient.setTcNo("123"); // 11 haneden az
        invalidPatient.setFullName("Test Patient");
        invalidPatient.setBirthDate(LocalDate.of(1990, 1, 1));
        invalidPatient.setGender(Patient.Gender.ERKEK);
        invalidPatient.setPhone("5551234567");
        invalidPatient.setEmail("test@example.com");
        invalidPatient.setAddress("Test Address");

        mockMvc.perform(post("/api/patients")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("TC Kimlik numarası 11 haneli olmalıdır"));
    }

    @Test
    void createPatient_InvalidPhoneNumber_Failure() throws Exception {
        Patient invalidPatient = new Patient();
        invalidPatient.setTcNo("12345678901");
        invalidPatient.setFullName("Test Patient");
        invalidPatient.setBirthDate(LocalDate.of(1990, 1, 1));
        invalidPatient.setGender(Patient.Gender.ERKEK);
        invalidPatient.setPhone("123"); // Geçersiz format
        invalidPatient.setEmail("test@example.com");
        invalidPatient.setAddress("Test Address");

        mockMvc.perform(post("/api/patients")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Telefon numarası 10 haneli olmalıdır"));
    }

    @Test
    void createPatient_InvalidEmail_Failure() throws Exception {
        Patient invalidPatient = new Patient();
        invalidPatient.setTcNo("12345678901");
        invalidPatient.setFullName("Test Patient");
        invalidPatient.setBirthDate(LocalDate.of(1990, 1, 1));
        invalidPatient.setGender(Patient.Gender.ERKEK);
        invalidPatient.setPhone("5551234567");
        invalidPatient.setEmail("invalid-email"); // Geçersiz e-posta
        invalidPatient.setAddress("Test Address");

        mockMvc.perform(post("/api/patients")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Geçerli bir e-posta adresi giriniz"));
    }

    @Test
    void createPatient_FutureBirthDate_Failure() throws Exception {
        Patient invalidPatient = new Patient();
        invalidPatient.setTcNo("12345678901");
        invalidPatient.setFullName("Test Patient");
        invalidPatient.setBirthDate(LocalDate.now().plusDays(1)); // Gelecek tarih
        invalidPatient.setGender(Patient.Gender.ERKEK);
        invalidPatient.setPhone("5551234567");
        invalidPatient.setEmail("test@example.com");
        invalidPatient.setAddress("Test Address");

        mockMvc.perform(post("/api/patients")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Doğum tarihi geçmiş bir tarih olmalıdır"));
    }

    @Test
    void getAllPatients_WithoutToken_Failure() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllPatients_WithExpiredToken_Failure() throws Exception {
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYxNjE1MzIwMCwiZXhwIjoxNjE2MTUzMjAxfQ.8h3Yu_ETQQCYqqn5xYw5afjDEoZ5r_Jh6HSz0Aq5PGE";

        mockMvc.perform(get("/api/patients")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllPatients_Pagination_Success() throws Exception {
        // İkinci hasta ekle
        Patient secondPatient = new Patient();
        secondPatient.setTcNo("98765432109");
        secondPatient.setFullName("Second Patient");
        secondPatient.setBirthDate(LocalDate.of(1995, 1, 1));
        secondPatient.setGender(Patient.Gender.KADIN);
        secondPatient.setPhone("5559876543");
        secondPatient.setEmail("second@example.com");
        secondPatient.setAddress("Second Address");
        patientRepository.save(secondPatient);

        // Sayfalama parametreleriyle sorgu
        mockMvc.perform(get("/api/patients")
                .header("Authorization", "Bearer " + token)
                .param("page", "0")
                .param("size", "1")
                .param("sort", "firstName,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(2));
    }

    @Test
    void createPatient_EmptyFields_Failure() throws Exception {
        Patient invalidPatient = new Patient();
        // Tüm alanlar boş

        mockMvc.perform(post("/api/patients")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors[?(@.field == 'tcNo')].message").value("TC Kimlik numarası boş olamaz"))
                .andExpect(jsonPath("$.errors[?(@.field == 'firstName')].message").value("Ad boş olamaz"))
                .andExpect(jsonPath("$.errors[?(@.field == 'lastName')].message").value("Soyad boş olamaz"))
                .andExpect(jsonPath("$.errors[?(@.field == 'dateOfBirth')].message").value("Doğum tarihi boş olamaz"))
                .andExpect(jsonPath("$.errors[?(@.field == 'gender')].message").value("Cinsiyet boş olamaz"))
                .andExpect(jsonPath("$.errors[?(@.field == 'phone')].message").value("Telefon numarası boş olamaz"))
                .andExpect(jsonPath("$.errors[?(@.field == 'address')].message").value("Adres boş olamaz"));
    }

    @Test
    @Tag("performance")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void bulkInsertPatients_Performance() throws Exception {
        int numberOfPatients = 100;
        List<Patient> patients = new ArrayList<>();

        // Test verileri oluştur
        for (int i = 0; i < numberOfPatients; i++) {
            Patient patient = new Patient();
            String suffix = String.format("%03d", i);
            patient.setTcNo("1234567" + suffix); // Unique TC No
            patient.setFullName("Test" + suffix);
            patient.setBirthDate(LocalDate.of(1990, 1, 1));
            patient.setGender(Patient.Gender.ERKEK);
            patient.setPhone("555123" + suffix);
            patient.setEmail("test" + suffix + "@example.com");
            patient.setAddress("Test Address " + suffix);
            patients.add(patient);
        }

        long startTime = System.currentTimeMillis();

        // Toplu kayıt işlemi
        for (Patient patient : patients) {
            mockMvc.perform(post("/api/patients")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patient)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true));
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Performans metriklerini kontrol et
        assertThat(duration).isLessThan(10000); // 10 saniyeden az sürmeli

        // Kayıtların başarıyla oluşturulduğunu kontrol et
        long count = patientRepository.count();
        assertThat(count).isGreaterThanOrEqualTo(numberOfPatients);
    }

    @Test
    @Tag("performance")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void concurrentRequests_Performance() throws Exception {
        int numberOfRequests = 50;
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        // Eşzamanlı istekler oluştur
        for (int i = 0; i < numberOfRequests; i++) {
            String suffix = String.format("%03d", i);
            Patient patient = new Patient();
            patient.setTcNo("9876543" + suffix);
            patient.setFullName("Concurrent" + suffix);
            patient.setBirthDate(LocalDate.of(1990, 1, 1));
            patient.setGender(Patient.Gender.ERKEK);
            patient.setPhone("555987" + suffix);
            patient.setEmail("concurrent" + suffix + "@example.com");
            patient.setAddress("Concurrent Address " + suffix);

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    mockMvc.perform(post("/api/patients")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patient)))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.success").value(true));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            futures.add(future);
        }

        // Tüm isteklerin tamamlanmasını bekle
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Performans metriklerini kontrol et
        assertThat(duration).isLessThan(15000); // 15 saniyeden az sürmeli

        // Kayıtların başarıyla oluşturulduğunu kontrol et
        long count = patientRepository.count();
        assertThat(count).isGreaterThanOrEqualTo(numberOfRequests);
    }

    @Test
    @Tag("performance")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void paginationPerformance_LargeDataset() throws Exception {
        // Çok sayıda test verisi oluştur
        IntStream.range(0, 1000).forEach(i -> {
            Patient patient = new Patient();
            String suffix = String.format("%04d", i);
            patient.setTcNo("1111111" + suffix);
            patient.setFullName("Perf" + suffix);
            patient.setBirthDate(LocalDate.of(1990, 1, 1));
            patient.setGender(Patient.Gender.ERKEK);
            patient.setPhone("555111" + suffix);
            patient.setEmail("perf" + suffix + "@example.com");
            patient.setAddress("Performance Test Address " + suffix);
            patientRepository.save(patient);
        });

        long startTime = System.currentTimeMillis();

        // Farklı sayfa ve boyutlarla sorgular yap
        for (int page = 0; page < 5; page++) {
            mockMvc.perform(get("/api/patients")
                    .header("Authorization", "Bearer " + token)
                    .param("page", String.valueOf(page))
                    .param("size", "50")
                    .param("sort", "firstName,asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(50));
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Performans metriklerini kontrol et
        assertThat(duration).isLessThan(5000); // 5 saniyeden az sürmeli
    }
}
