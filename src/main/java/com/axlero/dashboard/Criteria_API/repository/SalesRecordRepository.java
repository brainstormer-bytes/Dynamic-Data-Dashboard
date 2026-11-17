
package com.axlero.dashboard.Criteria_API.repository;

import com.axlero.dashboard.Criteria_API.entity.SalesRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SalesRecordRepository
        extends JpaRepository<SalesRecord, Long> {

    List<SalesRecord> findByDatasetId(Long datasetId);
}
