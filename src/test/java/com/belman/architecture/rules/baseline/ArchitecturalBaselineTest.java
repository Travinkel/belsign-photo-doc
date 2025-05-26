package com.belman.architecture.rules.baseline;

import com.belman.architecture.rules.BaseArchUnitTest;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * This class establishes a baseline of current architectural violations.
 * It will fail if new violations are introduced, but allow existing violations.
 * This approach allows for incremental improvement of the architecture without
 * breaking existing code.
 */
public class ArchitecturalBaselineTest extends BaseArchUnitTest {

    /**
     * Known god classes that are allowed to violate the Single Responsibility Principle.
     * This list should be reduced over time as the codebase is refactored.
     */
    private static final Set<String> KNOWN_GOD_CLASSES = new HashSet<>(Arrays.asList(
            "com.belman.application.usecase.security.DefaultAuthenticationService",
            "com.belman.common.logging.EmojiLogger",
            "com.belman.dataaccess.persistence.sql.SqlOrderRepository",
            "com.belman.dataaccess.persistence.sql.SqlUserRepository",
            "com.belman.dataaccess.repository.BaseRepository",
            "com.belman.dataaccess.repository.memory.InMemoryPhotoTemplateRepository",
            "com.belman.domain.order.OrderBusiness",
            "com.belman.domain.photo.PhotoDocument",
            "com.belman.domain.report.ReportBusiness",
            "com.belman.domain.user.UserBusiness",
            "com.belman.presentation.base.BaseView",
            "com.belman.presentation.components.PhotoGalleryComponent",
            "com.belman.presentation.usecases.admin.components.UserDialog",
            "com.belman.presentation.usecases.qa.review.PhotoReviewViewModel",
            "com.belman.presentation.usecases.worker.assignedorder.AssignedOrderViewModel",
            "com.belman.presentation.usecases.worker.capture.CaptureViewController",
            "com.belman.presentation.usecases.worker.photocube.PhotoCubeViewController",
            "com.belman.presentation.usecases.worker.photocube.PhotoCubeViewModel",
            "com.belman.presentation.usecases.worker.photocube.managers.TemplateManager",
            "com.belman.presentation.usecases.worker.summary.SummaryViewController"
    ));

    /**
     * Known anemic domain classes that are allowed to have no behavior.
     * This list should be reduced over time as the codebase is refactored.
     */
    private static final Set<String> KNOWN_ANEMIC_DOMAIN_CLASSES = new HashSet<>(Arrays.asList(
            "com.belman.domain.common.base.BusinessComponent",
            "com.belman.domain.common.base.Entity",
            "com.belman.domain.order.OrderAssignment",
            "com.belman.domain.photo.PhotoMetadata",
            "com.belman.domain.specification.AbstractSpecification$AndSpecification",
            "com.belman.domain.specification.AbstractSpecification$NotSpecification",
            "com.belman.domain.specification.AbstractSpecification$OrSpecification",
            "com.belman.domain.specification.OrderStatusSpecification",
            "com.belman.domain.user.UserBusiness$1"
    ));

    /**
     * Tests that no new god classes are introduced.
     * A god class is a class with too many responsibilities, indicated by a large number of methods or fields.
     */
    @Test
    public void noNewGodClasses() {
        ArchCondition<JavaClass> notBeNewGodClass = new ArchCondition<JavaClass>("not be a new god class") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                // Count methods and fields
                int methodCount = javaClass.getMethods().size();
                int fieldCount = javaClass.getFields().size();

                // Define thresholds for god classes
                int methodThreshold = 20;
                int fieldThreshold = 15;

                boolean isGodClass = methodCount > methodThreshold || fieldCount > fieldThreshold;

                // Only report violations for classes that are not in the known god classes list
                if (isGodClass && !KNOWN_GOD_CLASSES.contains(javaClass.getName())) {
                    String message = String.format(
                            "Class %s is a new god class with %d methods and %d fields",
                            javaClass.getName(), methodCount, fieldCount);
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                }
            }
        };

        ArchRule rule = classes()
                .that().areNotInterfaces()
                .and().areNotEnums()
                .and().areNotAnnotatedWith("org.junit.jupiter.api.Test")
                .and().haveNameNotMatching(".*Test")
                .should(notBeNewGodClass)
                .because("New god classes should not be introduced");

        rule.check(importedClasses);
    }

    /**
     * Tests that no new anemic domain classes are introduced.
     * An anemic domain class is a domain class without behavior methods.
     */
    @Test
    public void noNewAnemicDomainClasses() {
        ArchCondition<JavaClass> notBeNewAnemicDomainClass = new ArchCondition<JavaClass>("not be a new anemic domain class") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                // Get all methods defined in the class (not inherited)
                Set<String> methods = new HashSet<>();
                javaClass.getMethods().forEach(method -> {
                    if (method.getOwner().equals(javaClass)) {
                        methods.add(method.getName());
                    }
                });

                // Check if there's at least one method that is not a getter, setter, or standard Object method
                boolean hasBehavior = methods.stream().anyMatch(methodName -> {
                    return !methodName.equals("equals") &&
                           !methodName.equals("hashCode") &&
                           !methodName.equals("toString") &&
                           !methodName.startsWith("get") &&
                           !methodName.startsWith("set") &&
                           !methodName.startsWith("is");
                });

                // Only report violations for classes that are not in the known anemic domain classes list
                if (!hasBehavior && !KNOWN_ANEMIC_DOMAIN_CLASSES.contains(javaClass.getName())) {
                    String message = String.format(
                            "Domain class %s is a new anemic domain class (has no behavior methods)",
                            javaClass.getName());
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                }
            }
        };

        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain..")
                .and().areNotInterfaces()
                .and().areNotEnums()
                .and().areNotAnnotatedWith("org.junit.jupiter.api.Test")
                .and().haveNameNotMatching(".*Test")
                .and().haveNameNotMatching(".*Event")
                .and().haveNameNotMatching(".*Exception")
                .and().haveNameNotMatching(".*Factory")
                .should(notBeNewAnemicDomainClass)
                .because("New anemic domain classes should not be introduced");

        rule.check(importedClasses);
    }
}