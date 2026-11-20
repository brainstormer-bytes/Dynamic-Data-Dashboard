package com.axlero.dashboard.Criteria_API.seed;

import com.axlero.dashboard.Criteria_API.entity.*;
import com.axlero.dashboard.Criteria_API.enums.UserStatus;
import com.axlero.dashboard.Criteria_API.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile("dev")       // never runs in production
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final String[] REGIONS = {"North", "South", "East", "West", "Central"};
    private static final String[] PRODUCTS = {"Laptop", "Phone", "Tablet", "Monitor", "Keyboard", "Headset"};
    private static final int ROW_COUNT = 200;

    private final SeedProperties props;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final DatasetRepository datasetRepository;
    private final DatasetColumnRepository datasetColumnRepository;
    private final SalesRecordRepository salesRecordRepository;
    private final DashboardRepository dashboardRepository;
    private final WidgetConfigRepository widgetConfigRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional                 // all-or-nothing
    public void run(String... args) {
        if (datasetRepository.existsByName(props.datasetName())) {
            return;                // only skip if the demo data exists
        }

        // 1. users (first user in the list becomes the workspace owner)
        List<User> users = new ArrayList<>();
        for (SeedProperties.SeedUser su : props.users()) {
            users.add(findOrCreateUser(su));
        }
        User owner = users.get(0);

        // 2. workspace + membership (junction)
        Workspace workspace = workspaceRepository.findByName(props.workspaceName())
                .orElseGet(() -> {
                    Workspace w = new Workspace();
                    w.setName(props.workspaceName());
                    w.setCreatedBy(owner);
                    return workspaceRepository.save(w);
                });

        for (int i = 0; i < users.size(); i++) {
            saveMemberIfAbsent(workspace, users.get(i), props.users().get(i).role());
        }

        // 3. dataset + its column metadata
        Dataset dataset = new Dataset();
        dataset.setWorkspace(workspace);
        dataset.setName(props.datasetName());
        dataset.setDescription("Seeded sample data for Criteria API testing");
        dataset = datasetRepository.save(dataset);

        saveColumn(dataset, "region", "STRING", "COUNT");
        saveColumn(dataset, "product", "STRING", "COUNT");
        saveColumn(dataset, "amount", "DECIMAL", "SUM,COUNT");
        saveColumn(dataset, "quantity", "INTEGER", "SUM,COUNT");
        saveColumn(dataset, "sale_date", "DATE", "COUNT");

        // 4. random sales rows (fixed seed = same data every run)
        Random random = new Random(42);
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

        // 5. dashboard + widgets
        Dashboard dashboard = new Dashboard();
        dashboard.setWorkspace(workspace);
        dashboard.setCreatedBy(owner);
        dashboard.setName("Sales Overview");
        dashboard.setDescription("Revenue and order breakdowns");
        dashboard = dashboardRepository.save(dashboard);

        saveWidget(dashboard, dataset, "Revenue by Region", "BAR", "amount", "SUM", "region");
        saveWidget(dashboard, dataset, "Orders by Product", "BAR", "amount", "COUNT", "product");
        saveWidget(dashboard, dataset, "Total Units Sold", "KPI", "quantity", "SUM", null);
    }

    private User findOrCreateUser(SeedProperties.SeedUser su) {
        return userRepository.findByUsername(su.username())
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername(su.username());
                    u.setEmail(su.email());
                    u.setPassword(passwordEncoder.encode(props.demoPassword()));
                    u.setStatus(UserStatus.ACTIVE);
                    return userRepository.save(u);
                });
    }

    private void saveMemberIfAbsent(Workspace workspace, User user, String role) {
        if (workspaceMemberRepository.existsByWorkspaceAndUser(workspace, user)) {
            return;
        }
        WorkspaceMember m = new WorkspaceMember();
        m.setWorkspace(workspace);
        m.setUser(user);
        m.setRole(role);
        workspaceMemberRepository.save(m);
    }

    private void saveColumn(Dataset dataset, String name, String type, String aggregations) {
        DatasetColumn c = new DatasetColumn();
        c.setDataset(dataset);
        c.setColumnName(name);
        c.setDataType(type);
        c.setAllowedAggregations(aggregations);
        datasetColumnRepository.save(c);
    }

    private void saveWidget(Dashboard dashboard, Dataset dataset, String name, String chartType,
                            String metric, String aggregation, String groupBy) {
        WidgetConfig w = new WidgetConfig();
        w.setDashboard(dashboard);
        w.setWorkspace(dashboard.getWorkspace());
        w.setDataset(dataset);
        w.setWidgetName(name);
        w.setChartType(chartType);
        w.setMetricColumnKey(metric);
        w.setAggregation(aggregation);
        w.setGroupByColumnKey(groupBy);
        widgetConfigRepository.save(w);
    }
}
