package com.yscclinic.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDto {

    @NotBlank(message = "Kullanıcı adı boş olamaz")
    private String username;

    @NotBlank(message = "Şifre boş olamaz")
    private String password;

    @NotBlank(message = "E-posta boş olamaz")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;

    @NotBlank(message = "Ad soyad boş olamaz")
    private String fullName;
}
