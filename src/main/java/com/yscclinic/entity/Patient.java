package com.yscclinic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "TC Kimlik numarası boş olamaz")
    @Size(min = 11, max = 11, message = "TC Kimlik numarası 11 haneli olmalıdır")
    @Column(unique = true)
    private String tcNo;

    @NotBlank(message = "Ad soyad boş olamaz")
    @Size(min = 2, max = 100, message = "Ad soyad 2-100 karakter arasında olmalıdır")
    private String fullName;

    @NotNull(message = "Doğum tarihi boş olamaz")
    @Past(message = "Doğum tarihi geçmiş bir tarih olmalıdır")
    private LocalDate birthDate;

    @NotNull(message = "Cinsiyet boş olamaz")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotBlank(message = "Telefon numarası boş olamaz")
    @Pattern(regexp = "^[0-9]{10}$", message = "Telefon numarası 10 haneli olmalıdır")
    private String phone;

    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;

    @NotBlank(message = "Adres boş olamaz")
    private String address;

    public enum Gender {
        ERKEK, KADIN
    }
}
