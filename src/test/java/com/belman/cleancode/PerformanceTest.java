package com.belman.cleancode;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class PerformanceTest {
    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void databaseQueriesShouldUsePreparedStatements() {
        ArchRule rule = noClasses()
                .should().callMethod(Connection.class, "createStatement")
                .because("Database queries should use prepared statements for better performance and security");

        rule.check(importedClasses);
    }

    // Add more performance-related tests
}