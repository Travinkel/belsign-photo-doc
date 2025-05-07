package com.belman.cleancode;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CommentQualityTest {
    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void publicMethodsShouldHaveJavadoc() {
        ArchRule rule = methods()
                .that().arePublic()
                .should().beAnnotatedWith(Javadoc.class)
                .because("Public methods should have Javadoc comments");

        rule.check(importedClasses);
    }

    @Test
    public void todoCommentsShouldNotExistInProductionCode() {
        ArchRule rule = ArchRuleDefinition.noClasses()
                .should().containCodeUnitWithCommentContaining("TODO")
                .because("TODO comments should not exist in production code");

        rule.check(importedClasses);
    }
}