// PermissionRepository.java
package com.rbc.demo.userservice.repository;

import com.rbc.demo.userservice.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(Permission.PermissionName name);
    Boolean existsByName(Permission.PermissionName name);
}