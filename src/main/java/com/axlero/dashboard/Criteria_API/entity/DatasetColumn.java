package com.axlero.dashboard.Criteria_API.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Query-engine allowlist entry. columnName is the API key AND the backing entity attribute name (e.g. "saleDate").
 * A column may be the metric / group-by / filter only if listed here with matching allowedAggregations, groupable, filterable.
 */
@Entity
@Table(name = "dataset_columns")
@Getter
@Setter
@NoArgsConstructor
public class DatasetColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // R5: DATASETS 1 : M DATASET_COLUMNS
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dataset_id", nullable = false)
    private Dataset dataset;

    @Column(name = "column_name", nullable = false)
    private String columnName;

    @Column(nullable = false, length = 150)
    private String label;

    /** NUMBER, STRING or DATE (CHECK constraint in V2). */
    @Column(name = "data_type", nullable = false)
    private String dataType;

    @Column(nullable = false)
    private boolean groupable = false;

    @Column(nullable = false)
    private boolean filterable = false;

    // comma separated, e.g. "SUM,COUNT"
    @Column(name = "allowed_aggregations")
    private String allowedAggregations;
}
