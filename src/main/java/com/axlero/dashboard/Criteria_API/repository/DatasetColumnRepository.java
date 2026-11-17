

package com.axlero.dashboard.Criteria_API.repository;

import com.axlero.dashboard.Criteria_API.entity.DatasetColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DatasetColumnRepository extends JpaRepository<DatasetColumn, Long> {

    List<DatasetColumn> findByDatasetId(Long datasetId);

    Optional<DatasetColumn> findByDatasetIdAndColumnName(
            Long datasetId, String columnName);
}
