
package com.axlero.dashboard.Criteria_API.repository;

import com.axlero.dashboard.Criteria_API.entity.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DashboardRepository extends JpaRepository<Dashboard, Long> {

    List<Dashboard> findByWorkspaceId(Long workspaceId);

    List<Dashboard> findByCreatedById(Long userId);
}
