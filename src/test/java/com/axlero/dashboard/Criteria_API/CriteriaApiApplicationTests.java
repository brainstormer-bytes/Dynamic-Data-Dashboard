package com.axlero.dashboard.Criteria_API;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Runs against the test Postgres (TEST_DB_URL/USER/PASSWORD) with the db/migration/test scripts. No H2 anymore.
@SpringBootTest
@ActiveProfiles("test")
class CriteriaApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
