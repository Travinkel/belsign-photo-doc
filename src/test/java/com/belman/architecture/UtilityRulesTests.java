package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class UtilityRulesTests {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void utilityClassesShouldBeFinalAndHavePrivateConstructors() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.util..")
                .should().beFinal()
                .andShould().haveOnlyPrivateConstructors();

        rule.check(importedClasses);
    }
}
