package com.belman.architecture.rules.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests to verify that the project follows mobile architecture principles for the Gluon
 * Mobile application. These tests focus on proper separation of mobile-specific code from
 * core business logic, ensuring that the app can run on multiple platforms.
 */
public class PlatformAndGluonRulesTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void mobileSpecificCodeShouldBeInMobileOrPresentationPackages() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Mobile")
                .or().haveSimpleNameContaining("Device")
                .or().haveSimpleNameContaining("Screen")
                .or().haveSimpleNameContaining("UI")
                .or().haveSimpleNameContaining("Attach")
                .should().resideInAnyPackage("com.belman.infrastructure.mobile..", "com.belman.presentation..")
                .because("Mobile-specific code must be isolated in dedicated packages to ensure modularity");

        rule.check(importedClasses);
    }

    @Test
    public void domainLayerShouldNotDependOnMobileLibraries() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.domain..")
                .should().accessClassesThat().resideInAnyPackage(
                        "com.gluonhq.attach..",
                        "com.gluonhq.charm..",
                        "javafx.."
                )
                .because("Domain layer must remain independent of mobile-specific and UI libraries");

        rule.check(importedClasses);
    }

    @Test
    public void cameraAccessCodeShouldBeEncapsulated() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Camera.*|.*Picture.*|.*Photo.*")
                .should().resideInAPackage("com.belman.infrastructure.camera..")
                .because("Camera access code must be encapsulated in a dedicated package for maintainability");

        rule.check(importedClasses);
    }

    @Test
    public void storageAccessCodeShouldBeEncapsulated() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Storage")
                .or().haveSimpleNameContaining("FileService")
                .or().haveSimpleNameContaining("DataStore")
                .should().resideInAPackage("com.belman.infrastructure.storage..")
                .because("Storage access code must be encapsulated in a dedicated package for maintainability");

        rule.check(importedClasses);
    }

    @Test
    public void mobileServicesAccessShouldBeEncapsulated() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Service")
                .and().haveSimpleNameContaining("Mobile")
                .should().resideInAPackage("com.belman.infrastructure..")
                .because("Access to mobile services should be encapsulated in the infrastructure layer");

        rule.check(importedClasses);
    }

    @Test
    public void screenNavigationShouldBeCentralized() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*View")
                .should().accessClassesThat().haveNameMatching(".*Coordinator")
                .because("Screen navigation should be centralized using the coordinator pattern");

        rule.check(importedClasses);
    }

    @Test
    public void lifecycleMethodsShouldBeInPresentationLayer() {
        ArchRule rule = classes()
                .that().areAnnotatedWith("com.gluonhq.attach.lifecycle.LifecycleEvent")
                .should().resideInAPackage("com.belman.presentation..")
                .because("Lifecycle methods should be in the presentation layer to manage application state");

        rule.check(importedClasses);
    }

    @Test
    public void platformSpecificCodeShouldBeIsolatedInPlatformPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Android").or().haveSimpleNameContaining("IOS")
                .should().resideInAPackage("com.belman.infrastructure.platform..")
                .because(
                        "Platform-specific code must be isolated in a dedicated package to ensure cross-platform compatibility");

        rule.check(importedClasses);
    }

    @Test
    public void glistenShouldOnlyBeUsedInPresentationLayer() {
        ArchRule rule = noClasses()
                .that().resideOutsideOfPackage("..presentation..")
                .should().dependOnClassesThat().resideInAnyPackage("com.gluonhq.charm.glisten..")
                .because(
                        "Glisten UI components must only be used in the presentation layer to maintain separation of concerns");

        rule.check(importedClasses);
    }

    @Test
    public void attachShouldNotBeUsedInDomainLayer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("com.gluonhq.attach..")
                .because("Domain layer must remain independent of platform-specific services");

        rule.check(importedClasses);
    }

    @Test
    public void javafxShouldNotBeUsedInDomainLayer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("javafx..")
                .because("Domain layer must not depend on UI frameworks like JavaFX");

        rule.check(importedClasses);
    }

    @Test
    public void lifecycleAnnotationsShouldOnlyBeInInfrastructureOrPresentationLayers() {
        ArchRule rule = classes()
                .that().areAnnotatedWith("javax.annotation.PostConstruct")
                .or().areAnnotatedWith("javax.annotation.PreDestroy")
                .should().resideInAnyPackage("..infrastructure..", "..presentation..")
                .because("Lifecycle annotations should only exist in outer layers to manage application lifecycle");

        rule.check(importedClasses);
    }

    @Test
    public void reflectionConfigFileShouldExist() {
        File configFile = new File(CONFIG_PATH);
        assertTrue(configFile.exists() && configFile.isFile(),
                "Reflection configuration file is missing or not a valid file: " + CONFIG_PATH);
    }

    @Test
    public void shouldContainSplashViewController() throws Exception {
        File configFile = new File(CONFIG_PATH);
        assertTrue(configFile.exists(), "Reflection configuration file does not exist: " + CONFIG_PATH);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(configFile);

        Set<String> reflectedClasses = collectClassNames(root);

        assertTrue(reflectedClasses.contains("com.belman.presentation.views.splash.SplashViewController"),
                "Missing SplashViewController in reflect-config.json");
    }

    private Set<String> collectClassNames(JsonNode root) {
        return root.findValues("name").stream()
                .map(JsonNode::asText)
                .collect(Collectors.toSet());
    }
}
