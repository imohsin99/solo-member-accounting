package com.solofunds.memberaccounting.service;

import com.solofunds.memberaccounting.service.config.DatabaseSetupExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(DatabaseSetupExtension.class)
class ServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
