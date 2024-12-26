package com.yscclinic.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private String username;
    private List<String> roles;

    public JwtResponse(String token, String username, List<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }
}
