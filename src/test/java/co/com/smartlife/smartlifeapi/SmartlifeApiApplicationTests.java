package co.com.smartlife.smartlifeapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SmartlifeApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
