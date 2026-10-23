package co.com.smartlife.smartlifeapi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class SmartlifeApiApplicationTests {

	@Autowired
	private ApplicationContext context;

	
	@Test
	void contextLoads() {
		assertThat(context).isNotNull();
	}

}
