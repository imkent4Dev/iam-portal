package com.rbc.demo.userservice.config;

import com.rbc.demo.userservice.model.Permission;
import com.rbc.demo.userservice.model.Role;
import com.rbc.demo.userservice.model.User;
import com.rbc.demo.userservice.repository.PermissionRepository;
import com.rbc.demo.userservice.repository.RoleRepository;
import com.rbc.demo.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Initialize permissions
        initializePermissions();
        
        // Initialize roles
        initializeRoles();
        
        // Initialize users
        initializeUsers();
        
        System.out.println("=================================");
        System.out.println("Data initialization completed!");
        System.out.println("=================================");
        System.out.println("Test Users Created:");
        System.out.println("1. Admin User - username: admin, password: admin123");
        System.out.println("2. Manager User - username: manager, password: manager123");
        System.out.println("3. Regular User - username: user, password: user123");
        System.out.println("4. Guest User - username: guest, password: guest123");
        System.out.println("=================================");
    }
    
    private void initializePermissions() {
        if (permissionRepository.count() > 0) return;
        
        Arrays.stream(Permission.PermissionName.values()).forEach(permName -> {
            Permission permission = Permission.builder()
                .name(permName)
                .description("Permission for " + permName.name())
                .build();
            permissionRepository.save(permission);
        });
        
        System.out.println("Permissions initialized");
    }
    
    private void initializeRoles() {
        if (roleRepository.count() > 0) return;
        
        // Admin Role - all permissions
        Role adminRole = Role.builder()
            .name(Role.RoleName.ROLE_ADMIN)
            .description("Administrator with full access")
            .permissions(new HashSet<>(permissionRepository.findAll()))
            .build();
        roleRepository.save(adminRole);
        
        // Manager Role - limited permissions
        Set<Permission> managerPermissions = new HashSet<>();
        managerPermissions.add(permissionRepository.findByName(Permission.PermissionName.USER_READ).orElseThrow());
        managerPermissions.add(permissionRepository.findByName(Permission.PermissionName.USER_UPDATE).orElseThrow());
        managerPermissions.add(permissionRepository.findByName(Permission.PermissionName.ROLE_READ).orElseThrow());
        managerPermissions.add(permissionRepository.findByName(Permission.PermissionName.VIEW_DASHBOARD).orElseThrow());
        
        Role managerRole = Role.builder()
            .name(Role.RoleName.ROLE_MANAGER)
            .description("Manager with limited access")
            .permissions(managerPermissions)
            .build();
        roleRepository.save(managerRole);
        
        // User Role - basic permissions
        Set<Permission> userPermissions = new HashSet<>();
        userPermissions.add(permissionRepository.findByName(Permission.PermissionName.USER_READ).orElseThrow());
        userPermissions.add(permissionRepository.findByName(Permission.PermissionName.VIEW_DASHBOARD).orElseThrow());
        
        Role userRole = Role.builder()
            .name(Role.RoleName.ROLE_USER)
            .description("Regular user with basic access")
            .permissions(userPermissions)
            .build();
        roleRepository.save(userRole);
        
        // Guest Role - minimal permissions
        Set<Permission> guestPermissions = new HashSet<>();
        guestPermissions.add(permissionRepository.findByName(Permission.PermissionName.USER_READ).orElseThrow());
        
        Role guestRole = Role.builder()
            .name(Role.RoleName.ROLE_GUEST)
            .description("Guest with minimal access")
            .permissions(guestPermissions)
            .build();
        roleRepository.save(guestRole);
        
        System.out.println("Roles initialized");
    }
    
    private void initializeUsers() {
        if (userRepository.count() > 0) return;
        
        // Create Admin User
        Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN).orElseThrow();
        User admin = User.builder()
            .username("admin")
            .email("admin@example.com")
            .password(passwordEncoder.encode("admin123"))
            .firstName("Admin")
            .lastName("User")
            .enabled(true)
            .roles(new HashSet<>(Arrays.asList(adminRole)))
            .build();
        userRepository.save(admin);
        
        // Create Manager User
        Role managerRole = roleRepository.findByName(Role.RoleName.ROLE_MANAGER).orElseThrow();
        User manager = User.builder()
            .username("manager")
            .email("manager@example.com")
            .password(passwordEncoder.encode("manager123"))
            .firstName("Manager")
            .lastName("User")
            .enabled(true)
            .roles(new HashSet<>(Arrays.asList(managerRole)))
            .build();
        userRepository.save(manager);
        
        // Create Regular User
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER).orElseThrow();
        User user = User.builder()
            .username("user")
            .email("user@example.com")
            .password(passwordEncoder.encode("user123"))
            .firstName("Regular")
            .lastName("User")
            .enabled(true)
            .roles(new HashSet<>(Arrays.asList(userRole)))
            .build();
        userRepository.save(user);
        
        // Create Guest User
        Role guestRole = roleRepository.findByName(Role.RoleName.ROLE_GUEST).orElseThrow();
        User guest = User.builder()
            .username("guest")
            .email("guest@example.com")
            .password(passwordEncoder.encode("guest123"))
            .firstName("Guest")
            .lastName("User")
            .enabled(true)
            .roles(new HashSet<>(Arrays.asList(guestRole)))
            .build();
        userRepository.save(guest);
        
        System.out.println("Users initialized");
    }
}