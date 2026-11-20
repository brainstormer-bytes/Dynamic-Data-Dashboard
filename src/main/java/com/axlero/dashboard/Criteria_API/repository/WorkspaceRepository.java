package com.axlero.dashboard.Criteria_API.repository;

import com.axlero.dashboard.Criteria_API.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    Optional<Workspace> findByName(String name);
}
