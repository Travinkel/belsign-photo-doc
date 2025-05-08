package com.belman.cleancode;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

public class FunctionalProgrammingTest {
    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    private static ArchCondition<JavaMethod> useStreamAPI() {
        return new ArchCondition<JavaMethod>("use Stream API") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                // Simple check: method name contains "stream" or class name contains "Stream"
                String methodName = method.getName().toLowerCase();
                String className = method.getOwner().getName().toLowerCase();

                boolean usesStreamAPI = methodName.contains("stream") || 
                                        className.contains("stream") ||
                                        method.getRawParameterTypes().stream()
                                            .anyMatch(type -> type.getName().contains("Stream"));

                if (!usesStreamAPI) {
                    events.add(SimpleConditionEvent.violated(method, 
                        "Method " + method.getFullName() + " does not use Stream API"));
                }
            }
        };
    }

    @Test
    public void shouldUseStreamAPIWhereAppropriate() {
        ArchRule rule = methods()
                .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Service")
                .should(useStreamAPI())
                .because("Service methods should use Stream API for collections processing where appropriate");

        rule.check(importedClasses);
    }
}
