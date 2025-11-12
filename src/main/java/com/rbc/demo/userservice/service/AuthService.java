// AuthService.java
package com.rbc.demo.userservice.service;

import com.rbc.demo.userservice.dto.*;
import com.rbc.demo.userservice.model.Permission;
import com.rbc.demo.userservice.model.Role;
import com.rbc.demo.userservice.model.User;
import com.rbc.demo.userservice.repository.RoleRepository;
import com.rbc.demo.userservice.repository.UserRepository;
import com.rbc.demo.userservice.security.JwtUtil;
import com.rbc.demo.userservice.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Set<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .filter(auth -> auth.startsWith("ROLE_"))
            .collect(Collectors.toSet());
        
        Set<String> permissions = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .filter(auth -> !auth.startsWith("ROLE_"))
            .collect(Collectors.toSet());
        
        return new JwtResponse(
            jwt,
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            roles,
            permissions
        );
    }
    
    @Transactional
    public ApiResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return new ApiResponse(false, "Username is already taken!");
        }
        
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return new ApiResponse(false, "Email is already in use!");
        }
        
        User user = User.builder()
            .username(registerRequest.getUsername())
            .email(registerRequest.getEmail())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .firstName(registerRequest.getFirstName())
            .lastName(registerRequest.getLastName())
            .enabled(true)
            .roles(new HashSet<>())
            .build();
        
        // Assign default ROLE_USER
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.getRoles().add(userRole);
        
        userRepository.save(user);
        
        return new ApiResponse(true, "User registered successfully!");
    }
}