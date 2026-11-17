
package com.axlero.dashboard.Criteria_API.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "widget_configs")
public class WidgetConfigs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dashboard_id", nullable = false)
    private Long dashboardId;

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;

    @Column(name = "dataset_id", nullable = false)
    private Long datasetId;

    @Column(name = "widget_name", nullable = false, length = 150)
    private String widgetName;

    @Column(name = "chart_type", nullable = false, length = 50)
    private String chartType;

    @Column(name = "metric_column_key", length = 150)
    private String metricColumnKey;

    @Column(name = "aggregation", length = 50)
    private String aggregation;

    @Column(name = "group_by_column_key", length = 150)
    private String groupByColumnKey;

    @Column(name = "created_at", nullable = false,
            insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public WidgetConfigs() {
    }

    public Long getId() {
        return id;
    }

    public Long getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Long dashboardId) {
        this.dashboardId = dashboardId;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public String getWidgetName() {
        return widgetName;
    }

    public void setWidgetName(String widgetName) {
        this.widgetName = widgetName;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public String getMetricColumnKey() {
        return metricColumnKey;
    }

    public void setMetricColumnKey(String metricColumnKey) {
        this.metricColumnKey = metricColumnKey;
    }

    public String getAggregation() {
        return aggregation;
    }

    public void setAggregation(String aggregation) {
        this.aggregation = aggregation;
    }

    public String getGroupByColumnKey() {
        return groupByColumnKey;
    }

    public void setGroupByColumnKey(String groupByColumnKey) {
        this.groupByColumnKey = groupByColumnKey;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

