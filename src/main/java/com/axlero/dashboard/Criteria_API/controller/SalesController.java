package com.axlero.dashboard.Criteria_API.controller;

import com.axlero.dashboard.Criteria_API.entity.SalesRecord;
import com.axlero.dashboard.Criteria_API.repository.SalesRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** Throwaway controller to prove wiring: Postman -> controller -> JPA -> H2. */
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesRecordRepository salesRecordRepository;

    public record SalesRecordResponse(
            Long id,
            Long datasetId,
            String region,
            String product,
            BigDecimal amount,
            Integer quantity,
            LocalDate saleDate) {

        static SalesRecordResponse from(SalesRecord r) {
            return new SalesRecordResponse(
                    r.getId(),
                    r.getDataset().getId(),
                    r.getRegion(),
                    r.getProduct(),
                    r.getAmount(),
                    r.getQuantity(),
                    r.getSaleDate());
        }
    }

    @GetMapping
    public List<SalesRecordResponse> list(@RequestParam(defaultValue = "50") int limit) {
        return salesRecordRepository.findAll(PageRequest.of(0, Math.min(limit, 200)))
                .getContent()
                .stream()
                .map(SalesRecordResponse::from)
                .toList();
    }

    @GetMapping("/count")
    public Map<String, Long> count() {
        return Map.of("count", salesRecordRepository.count());
    }
}
