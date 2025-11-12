// JwtResponse.java
package com.rbc.demo.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
    private Set<String> permissions;
    
    public JwtResponse(String token, Long id, String username, String email, 
                       Set<String> roles, Set<String> permissions) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.permissions = permissions;
    }
}