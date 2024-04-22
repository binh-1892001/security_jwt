package com.ra.repository;

import com.ra.constants.RoleName;
import com.ra.model.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRoleRepository extends JpaRepository<Roles, Long> {
	Optional<Roles> findByRoleName(RoleName roleName);
}
