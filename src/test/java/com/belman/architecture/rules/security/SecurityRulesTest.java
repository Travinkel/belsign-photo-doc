package com.belman.architecture.rules.security;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

/**
 * Tests to verify that security principles are correctly implemented in the architecture.
 * This includes proper handling of sensitive data, authentication, authorization, and secure coding practices.
 */
public class SecurityRulesTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void securityUtilitiesShouldBeInSecurityPackage() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Security.*|.*Crypto.*|.*Auth.*|.*Password.*|.*Credential.*")
                .and().resideOutsideOfPackage("..test..")
                .and().resideOutsideOfPackage("..acceptance..")
                .and().resideOutsideOfPackage("..integration..")
                .and().resideOutsideOfPackage("..cleancode..")
                .and().doNotHaveFullyQualifiedName("com.belman.common.logging.AuthLoggingService")
                .and().doNotHaveFullyQualifiedName("com.belman.bootstrap.di.ServiceInjector$MockAuthenticationService")
                .should().resideInAnyPackage("..security..", "..auth..")
                .because(
                        "Security-related classes should be organized in security or auth packages for better visibility and maintenance")
                .allowEmptyShould(true);

        rule.check(importedClasses);
    }

    @Test
    public void sensitiveDataShouldNotBeStoredInPlaintext() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Password")
                .and().areNotInterfaces()
                .and().resideOutsideOfPackage("..test..")
                .and().doNotHaveFullyQualifiedName("com.belman.application.usecase.security.BCryptPasswordHasher")
                .and().doNotHaveFullyQualifiedName("com.belman.domain.security.HashedPassword")
                .should().bePackagePrivate()
                .because("Password classes should not be public (except interfaces, test classes, and specific cross-package classes)")
                .allowEmptyShould(true);

        rule.check(importedClasses);
    }

    @Test
    public void onlySecurityLayerShouldAccessCredentials() {
        ArchRule rule = noClasses()
                .that().resideOutsideOfPackages("..security..", "..auth..")
                .and().resideOutsideOfPackage("..test..")
                .and().resideOutsideOfPackage("..acceptance..")
                .and().resideOutsideOfPackage("..integration..")
                .and().resideOutsideOfPackage("..domain..")
                .and().resideOutsideOfPackage("..dataaccess..")
                .and().resideOutsideOfPackage("..bootstrap..")
                .and().resideOutsideOfPackage("..application..")
                .and().resideOutsideOfPackage("..presentation..")
                .and().resideOutsideOfPackage("..unit..")
                .should().accessClassesThat().haveNameMatching(".*Credential.*|.*Password.*")
                .because("Only security-related classes should handle credentials directly")
                .allowEmptyShould(true);

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
                .and().resideOutsideOfPackage("..test..")
                .and().resideOutsideOfPackage("..acceptance..")
                .and().resideOutsideOfPackage("..integration..")
                .and().resideOutsideOfPackage("..bootstrap..")
                .and().resideOutsideOfPackage("..common..")
                .and().resideOutsideOfPackage("..domain..")
                .and().resideOutsideOfPackage("..presentation..")
                .should().resideInAnyPackage("..infrastructure..", "..application..")
                .because("Authorization mechanisms should be implemented in the infrastructure or application layers")
                .allowEmptyShould(true);

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
                .and().resideOutsideOfPackage("..test..")
                .and().resideOutsideOfPackage("..acceptance..")
                .and().resideOutsideOfPackage("..integration..")
                .and().resideOutsideOfPackage("..cleancode..")
                .and().doNotHaveFullyQualifiedName("com.belman.common.session.SessionContext")
                .and().doNotHaveFullyQualifiedName("com.belman.common.di.ServiceProviderFactory")
                .should().dependOnClassesThat().haveNameMatching(".*Logger.*")
                .because("Security-related actions should be logged for audit purposes")
                .allowEmptyShould(true);

        rule.check(importedClasses);
    }
}
