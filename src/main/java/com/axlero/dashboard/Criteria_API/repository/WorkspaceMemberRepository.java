package com.axlero.dashboard.Criteria_API.repository;

import com.axlero.dashboard.Criteria_API.entity.User;
import com.axlero.dashboard.Criteria_API.entity.Workspace;
import com.axlero.dashboard.Criteria_API.entity.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {
    boolean existsByWorkspaceAndUser(Workspace w, User u);
}
