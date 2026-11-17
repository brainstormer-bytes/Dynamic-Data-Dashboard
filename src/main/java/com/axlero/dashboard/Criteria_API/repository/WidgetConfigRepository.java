
package com.axlero.dashboard.Criteria_API.repository;

import com.axlero.dashboard.Criteria_API.entity.WidgetConfigs;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WidgetConfigRepository extends JpaRepository<WidgetConfigs, Long> {

    List<WidgetConfigs> findByDashboardId(Long dashboardId);

    List<WidgetConfigs> findByWorkspaceId(Long workspaceId);
}

