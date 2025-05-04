package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit5.AnalyzeClasses;
import com.tngtech.archunit.junit5.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "com.belman")
public class GluonArchitectureTest {

    @ArchTest
    static final ArchRule glisten_only_used_in_presentation = classes()
            .that().resideOutsideOfPackage("..presentation..")
            .should().onlyDependOnClassesThat().resideOutsideOfPackage("com.gluonhq.charm.glisten..")
            .because("Glisten UI components should only be used in the presentation layer");

    @ArchTest
    static final ArchRule attach_only_used_in_infrastructure = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("com.gluonhq.attach..")
            .because("Attach services should not be accessed from the domain layer");

    @ArchTest
    static final ArchRule javafx_not_in_domain = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("javafx..")
            .because("Domain should be independent of UI frameworks");

    @ArchTest
    static final ArchRule reflection_annotations_in_allowed_layers = classes()
            .that().areAnnotatedWith("javax.annotation.PostConstruct")
            .or().areAnnotatedWith("javax.annotation.PreDestroy")
            .should().resideInAnyPackage("..infrastructure..", "..presentation..")
            .because("Lifecycle methods should only exist in outer layers");
}
