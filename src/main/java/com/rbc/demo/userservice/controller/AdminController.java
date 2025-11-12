// AdminController.java
package com.rbc.demo.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> getAdminDashboard() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Admin Dashboard");
        response.put("access", "Admin Only");
        return ResponseEntity.ok(response);
    }
}