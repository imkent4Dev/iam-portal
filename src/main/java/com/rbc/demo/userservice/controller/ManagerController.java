// ManagerController.java
package com.rbc.demo.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/manager")
public class ManagerController {
    
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, String>> getManagerDashboard() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Manager Dashboard");
        response.put("access", "Admin and Manager Only");
        return ResponseEntity.ok(response);
    }
}