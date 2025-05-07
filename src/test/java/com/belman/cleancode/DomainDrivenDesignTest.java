package com.belman.cleancode;

public class DomainDrivenDesignTest {
    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void aggregateRootsShouldBeProperlyDefined() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(AggregateRoot.class)
                .should().haveOnlyFinalFields()
                .andShould().haveAtLeastOneMethodAnnotatedWith(DomainEvent.class)
                .because("Aggregate roots should be properly defined with final fields and domain events");

        rule.check(importedClasses);
    }

    @Test
    public void repositoriesShouldOnlyAccessAggregateRoots() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Repository")
                .should().onlyAccessClassesThat().areAnnotatedWith(AggregateRoot.class)
                .orShould().beAssignableTo(AggregateRoot.class)
                .because("Repositories should only access aggregate roots");

        rule.check(importedClasses);
    }
}
