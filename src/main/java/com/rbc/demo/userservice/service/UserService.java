// UserService.java
package com.rbc.demo.userservice.service;

import com.rbc.demo.userservice.dto.UserResponse;
import com.rbc.demo.userservice.model.Role;
import com.rbc.demo.userservice.model.User;
import com.rbc.demo.userservice.repository.RoleRepository;
import com.rbc.demo.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToResponse(user);
    }
    
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return convertToResponse(user);
    }
    
    @Transactional
    public UserResponse createUser(String username, String password, String email, 
                                   String firstName, String lastName, String name,
                                   String nid, String phone) {
        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (nid == null || nid.trim().isEmpty()) {
            throw new IllegalArgumentException("NID cannot be empty");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be empty");
        }
        
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }
        
        // Create new user
        User user = User.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .email(email)
            .firstName(firstName)
            .lastName(lastName)
            .name(name)
            .nid(nid)
            .phone(phone)
            .status(User.UserStatus.ACTIVE)
            .enabled(true)
            .roles(new HashSet<>())
            .build();
        
        // Assign default role (ROLE_USER)
        Role defaultRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));
        user.getRoles().add(defaultRole);
        
        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }
    
    @Transactional
    public UserResponse assignRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Role role = roleRepository.findByName(Role.RoleName.valueOf(roleName))
            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        
        user.getRoles().add(role);
        User updatedUser = userRepository.save(user);
        
        return convertToResponse(updatedUser);
    }
    
    @Transactional
    public UserResponse removeRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.getRoles().removeIf(role -> role.getName().name().equals(roleName));
        User updatedUser = userRepository.save(user);
        
        return convertToResponse(updatedUser);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    private UserResponse convertToResponse(User user) {
        Set<String> roles = user.getRoles().stream()
            .map(role -> role.getName().name())
            .collect(Collectors.toSet());
        
        Set<String> permissions = new HashSet<>();
        user.getRoles().forEach(role -> 
            permissions.addAll(role.getPermissions().stream()
                .map(permission -> permission.getName().name())
                .collect(Collectors.toSet()))
        );
        
        return UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .name(user.getName())
            .nid(user.getNid())
            .phone(user.getPhone())
            .status(user.getStatus())
            .enabled(user.getEnabled())
            .roles(roles)
            .permissions(permissions)
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}