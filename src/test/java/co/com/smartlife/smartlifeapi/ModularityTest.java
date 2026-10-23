package co.com.smartlife.smartlifeapi;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModularityTest {
	
	private final ApplicationModules modules = ApplicationModules.of(SmartlifeApiApplication.class);
	
	@Test
    void respectBoundariesBetweenModules() {
        modules.verify();
    }

    @Test
    void generateModulesDocumentation() {
        new Documenter(modules).writeDocumentation();
    }
}
