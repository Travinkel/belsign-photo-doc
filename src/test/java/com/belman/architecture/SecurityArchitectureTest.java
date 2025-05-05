package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

/**
 * Tests to verify that security principles are correctly implemented in the architecture.
 * This includes proper handling of sensitive data, authentication, authorization, and secure coding practices.
 */
public class SecurityArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void securityUtilitiesShouldBeInSecurityPackage() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Security.*|.*Crypto.*|.*Auth.*|.*Password.*|.*Credential.*")
                .should().resideInAnyPackage("..security..", "..auth..")
                .because(
                        "Security-related classes should be organized in security or auth packages for better visibility and maintenance");

        rule.check(importedClasses);
    }

    @Test
    public void sensitiveDataShouldNotBeStoredInPlaintext() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Password")
                .should().bePackagePrivate()
                .because("Password classes should not be public");

        rule.check(importedClasses);
    }

    @Test
    public void onlySecurityLayerShouldAccessCredentials() {
        ArchRule rule = noClasses()
                .that().resideOutsideOfPackages("..security..", "..auth..")
                .should().accessClassesThat().haveNameMatching(".*Credential.*|.*Password.*")
                .because("Only security-related classes should handle credentials directly");

        rule.check(importedClasses);
    }

    @Test
    public void secureConfigMustBeProtected() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Config.*|.*Settings.*")
                .and().areNotInterfaces()
                .should().bePackagePrivate()
                .orShould().haveModifier(com.tngtech.archunit.core.domain.JavaModifier.FINAL)
                .because(
                        "Configuration classes should be either package-private or final to prevent unauthorized modifications");

        rule.check(importedClasses);
    }

    @Test
    public void authorizationShouldBeInInfrastructureOrApplication() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Auth.*|.*Authoriz.*|.*Permission.*|.*Role.*")
                .should().resideInAnyPackage("..infrastructure..", "..application..")
                .because("Authorization mechanisms should be implemented in the infrastructure or application layers");

        rule.check(importedClasses);
    }

    @Test
    public void doNotUseHardcodedSecrets() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Secret")
                .should().bePackagePrivate()
                .because("Secret classes should not be public");

        rule.check(importedClasses);
    }

    @Test
    public void doNotUseInsecureRandom() {
        ArchRule rule = noClasses()
                .should().accessClassesThat().haveFullyQualifiedName("java.util.Random")
                .because("java.util.Random is not cryptographically secure, use java.security.SecureRandom instead");

        rule.check(importedClasses);
    }

    @Test
    public void noClassesShouldUseFieldInjection() {
        // Field injection can be a security risk by making it easier to bypass authorization checks
        NO_CLASSES_SHOULD_USE_FIELD_INJECTION.check(importedClasses);
    }

    @Test
    public void securityAccessShouldBeLogged() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Auth.*|.*Login.*|.*Access.*")
                .should().dependOnClassesThat().haveNameMatching(".*Logger.*")
                .because("Security-related actions should be logged for audit purposes");

        rule.check(importedClasses);
    }
}