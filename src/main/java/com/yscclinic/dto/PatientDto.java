package com.yscclinic.dto;

import java.time.LocalDate;

import com.yscclinic.entity.Patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PatientDto {

    @NotBlank(message = "TC kimlik numarası boş olamaz")
    @Pattern(regexp = "^[0-9]{11}$", message = "TC kimlik numarası 11 haneli olmalıdır")
    private String tcNo;

    @NotBlank(message = "Ad Soyad boş olamaz")
    private String fullName;

    @NotNull(message = "Doğum tarihi boş olamaz")
    @Past(message = "Doğum tarihi geçmiş bir tarih olmalıdır")
    private LocalDate birthDate;

    @NotNull(message = "Cinsiyet boş olamaz")
    private Patient.Gender gender;

    @NotBlank(message = "Telefon numarası boş olamaz")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Geçerli bir telefon numarası giriniz")
    private String phone;

    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;

    private String address;
}
