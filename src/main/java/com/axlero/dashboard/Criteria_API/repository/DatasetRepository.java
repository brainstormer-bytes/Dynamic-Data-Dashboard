package com.axlero.dashboard.Criteria_API.repository;

import com.axlero.dashboard.Criteria_API.entity.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatasetRepository extends JpaRepository<Dataset, Long> {
    boolean existsByName(String name);
}
