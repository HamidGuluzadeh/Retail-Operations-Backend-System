package com.retail.ops;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class RetailOperationsApplicationTests {

	@Test
	void verifiesModularStructure() {
		ApplicationModules.of(RetailOperationsApplication.class).verify();
	}

}
