package com.axlero.dashboard.Criteria_API.seed;

import com.axlero.dashboard.Criteria_API.entity.Dataset;
import com.axlero.dashboard.Criteria_API.entity.SalesRecord;
import com.axlero.dashboard.Criteria_API.repository.DatasetRepository;
import com.axlero.dashboard.Criteria_API.repository.SalesRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final String[] REGIONS = {"North", "South", "East", "West", "Central"};
    private static final String[] PRODUCTS = {"Laptop", "Phone", "Tablet", "Monitor", "Keyboard", "Headset"};
    private static final int ROW_COUNT = 200;

    private final DatasetRepository datasetRepository;
    private final SalesRecordRepository salesRecordRepository;

    @Override
    public void run(String... args) {
        if (datasetRepository.count() > 0) {
            return;
        }

        Dataset dataset = new Dataset();
        dataset.setName("Demo Sales 2026");
        dataset.setDescription("Seeded sample data for Criteria API testing");
        dataset = datasetRepository.save(dataset);

        Random random = new Random(42); // fixed seed = same data every run
        List<SalesRecord> rows = new ArrayList<>(ROW_COUNT);

        for (int i = 0; i < ROW_COUNT; i++) {
            SalesRecord r = new SalesRecord();
            r.setDataset(dataset);
            r.setRegion(REGIONS[random.nextInt(REGIONS.length)]);
            r.setProduct(PRODUCTS[random.nextInt(PRODUCTS.length)]);
            r.setAmount(BigDecimal.valueOf(1_000 + random.nextInt(500_000), 2));
            r.setQuantity(1 + random.nextInt(20));
            r.setSaleDate(LocalDate.of(2026, 1, 1).plusDays(random.nextInt(300)));
            rows.add(r);
        }

        salesRecordRepository.saveAll(rows);
    }
}
