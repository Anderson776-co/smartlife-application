package co.com.smartlife.smartlifeapi;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "co.com.smartlife.smartlifeapi", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {
	@ArchTest
    static final ArchRule DependencyRule =
            layeredArchitecture().consideringOnlyDependenciesInLayers()
                    .withOptionalLayers(true)   // many folders are still empty
                    .layer("Domain").definedBy("..domain..")
                    .layer("Services").definedBy("..service..")
                    .layer("Infrastructure").definedBy("..infrastructure..")
                    .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Services").mayOnlyBeAccessedByLayers("Infrastructure")
                    .whereLayer("Domain").mayOnlyBeAccessedByLayers("Services", "Infrastructure");

    @ArchTest
    static final ArchRule DoNotUseLibrariesInDomain =
            classes().that().resideInAPackage("..domain..")
                    .should().onlyDependOnClassesThat().resideInAnyPackage("java..", "..domain..");

    @ArchTest
    static final ArchRule ServicesDoNotUsePersistenceDirectly =
            noClasses().that().resideInAPackage("..infrastructure.input..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..service.usecase..", "..service.output..", "..infrastructure.output..").allowEmptyShould(true);
}
