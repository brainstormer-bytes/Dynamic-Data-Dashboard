
package com.axlero.dashboard.Criteria_API.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dataset_columns")
public class DatasetColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataset_id", nullable = false)
    private Dataset dataset;

    @Column(name = "column_name", nullable = false, length = 150)
    private String columnName;

    @Column(name = "data_type", nullable = false, length = 50)
    private String dataType;

    @Column(name = "allowed_aggregations", length = 500)
    private String allowedAggregations;

    public DatasetColumn() {
    }

    public Long getId() {
        return id;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getAllowedAggregations() {
        return allowedAggregations;
    }

    public void setAllowedAggregations(String allowedAggregations) {
        this.allowedAggregations = allowedAggregations;
    }
}
