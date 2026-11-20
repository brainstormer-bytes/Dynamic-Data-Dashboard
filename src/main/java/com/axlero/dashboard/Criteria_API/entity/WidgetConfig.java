package com.axlero.dashboard.Criteria_API.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "widget_configs")
@Getter
@Setter
@NoArgsConstructor
public class WidgetConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // R8: DASHBOARDS 1 : M WIDGET_CONFIGS
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dashboard_id", nullable = false)
    private Dashboard dashboard;

    // kept for tenant isolation checks (note 3 in the ER diagram)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    // R9: DATASETS 1 : M WIDGET_CONFIGS
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dataset_id", nullable = false)
    private Dataset dataset;

    @Column(name = "widget_name", nullable = false)
    private String widgetName;

    @Column(name = "chart_type", nullable = false)
    private String chartType;

    @Column(name = "metric_column_key", nullable = false)
    private String metricColumnKey;

    @Column(nullable = false)
    private String aggregation;

    // null for KPI widgets (no grouping)
    @Column(name = "group_by_column_key")
    private String groupByColumnKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
