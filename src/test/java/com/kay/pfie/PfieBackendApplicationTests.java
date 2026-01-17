package com.kay.pfie;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:pfie;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.flyway.enabled=false",
		"pfie.auth.jwt.secret=test-only-change-me-change-me-change-me",
		"pfie.auth.google.client-id=test-google-client-id"
})
class PfieBackendApplicationTests {

	@Test
	void contextLoads() {
	}
}
