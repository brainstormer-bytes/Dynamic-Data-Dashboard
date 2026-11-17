package com.axlero.dashboard.Criteria_API.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Describes one column of a dataset and which aggregations are allowed on it (used later for query whitelisting). */
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

    @Column(name = "data_type", nullable = false)
    private String dataType;

    // comma separated, e.g. "SUM,COUNT"
    @Column(name = "allowed_aggregations")
    private String allowedAggregations;
}
