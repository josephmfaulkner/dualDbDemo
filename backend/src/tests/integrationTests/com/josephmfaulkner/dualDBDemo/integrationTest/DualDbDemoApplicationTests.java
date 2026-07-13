package com.josephmfaulkner.dualDBDemo.integrationTest;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.josephmfaulkner.dualDBDemo.testFixtures._BaseLocalDynamoDbTest;

@SpringBootTest
@ActiveProfiles("test")
@Import(DynamoDbTestConfiguration.class)
class DualDbDemoApplicationTests extends _BaseLocalDynamoDbTest {

	@Test
	void contextLoads() { }

}
