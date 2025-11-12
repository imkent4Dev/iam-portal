// UserController.java
package com.rbc.demo.userservice.controller;

import com.rbc.demo.userservice.dto.UserResponse;
import com.rbc.demo.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    @GetMapping("/username/{username}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }
    
    @PostMapping("/{id}/roles/{roleName}")
    @PreAuthorize("hasAuthority('USER_UPDATE') and hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<UserResponse> assignRole(@PathVariable Long id, @PathVariable String roleName) {
        return ResponseEntity.ok(userService.assignRole(id, roleName));
    }
    
    @DeleteMapping("/{id}/roles/{roleName}")
    @PreAuthorize("hasAuthority('USER_UPDATE') and hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<UserResponse> removeRole(@PathVariable Long id, @PathVariable String roleName) {
        return ResponseEntity.ok(userService.removeRole(id, roleName));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
