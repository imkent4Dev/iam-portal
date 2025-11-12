package com.rbc.demo.userservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private PermissionName name;
    
    @Column(length = 500)
    private String description;
    
    public enum PermissionName {
        // User permissions
        USER_READ,
        USER_CREATE,
        USER_UPDATE,
        USER_DELETE,
        
        // Role permissions
        ROLE_READ,
        ROLE_CREATE,
        ROLE_UPDATE,
        ROLE_DELETE,
        
        // Permission permissions
        PERMISSION_READ,
        PERMISSION_CREATE,
        PERMISSION_UPDATE,
        PERMISSION_DELETE,
        
        // General permissions
        VIEW_DASHBOARD,
        MANAGE_SETTINGS
    }
}