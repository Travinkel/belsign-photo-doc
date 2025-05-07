package com.belman.cleancode;

public class LayeredArchitectureTest {
    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void layeredArchitectureShouldBeRespected() {
        LayeredArchitecture architecture = layeredArchitecture()
                .layer("Domain").definedBy("com.belman.domain..")
                .layer("Application").definedBy("com.belman.application..")
                .layer("Infrastructure").definedBy("com.belman.infrastructure..")
                .layer("Presentation").definedBy("com.belman.presentation..")

                .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Presentation")
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
                .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();

        architecture.check(importedClasses);
    }
}