package com.belman.integration.report;

import com.belman.application.usecase.order.OrderService;
import com.belman.application.usecase.photo.PhotoService;
import com.belman.application.usecase.report.ReportService;
import com.belman.common.session.SessionContext;
import java.util.HashMap;
import java.util.Map;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.photo.Photo;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.report.ReportBusiness;
import com.belman.domain.report.ReportFormat;
import com.belman.domain.report.ReportId;
import com.belman.domain.report.ReportType;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.ApprovalState;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.Username;
import com.belman.domain.user.UserRole;
import com.belman.presentation.usecases.qa.done.QADoneViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.*;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * End-to-End test for the report generation flow.
 * This test verifies the complete report generation workflow from generating a report
 * for an approved order to sending it via email.
 */
public class ReportGenerationFlowTest {

    @Mock
    private OrderService orderService;

    @Mock
    private PhotoService photoService;

    @Mock
    private ReportService reportService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private SessionContext sessionContext;

    @Mock
    private com.belman.domain.services.LoggerFactory loggerFactory;

    @Mock
    private com.belman.domain.order.OrderRepository orderRepository;

    @Mock
    private com.belman.domain.photo.PhotoRepository photoRepository;

    private UserBusiness testQAEngineer;
    private OrderBusiness testOrder;
    private List<PhotoDocument> testPhotos;
    private ReportBusiness testReport;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("[DEBUG_LOG] Setting up ReportGenerationFlowTest");

        // Clear ServiceLocator and register necessary services
        com.belman.bootstrap.di.ServiceLocator.clear();

        // Register SessionContext
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.common.session.SessionContext.class,
            sessionContext
        );

        // Register LoggerFactory
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.domain.services.LoggerFactory.class,
            loggerFactory
        );

        // Set up logger factory mock
        when(loggerFactory.getLogger(any())).thenReturn(mock(com.belman.domain.services.Logger.class));

        // Register AuthenticationService
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.domain.security.AuthenticationService.class,
            authenticationService
        );

        // Register OrderService
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.application.usecase.order.OrderService.class,
            orderService
        );

        // Register PhotoService
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.application.usecase.photo.PhotoService.class,
            photoService
        );

        // Register ReportService
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.application.usecase.report.ReportService.class,
            reportService
        );

        // Register OrderRepository
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.domain.order.OrderRepository.class,
            orderRepository
        );

        // Register PhotoRepository
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.domain.photo.PhotoRepository.class,
            photoRepository
        );

        // Create test QA engineer
        testQAEngineer = createTestQAEngineer();

        // Create test order
        testOrder = createTestOrder();

        // Create test photos
        testPhotos = createTestPhotos();

        // Create test report
        testReport = createTestReport();

        // Set up mocks
        setupMocks();
    }

    /**
     * Tests the complete report generation flow.
     * This test verifies that a QA engineer can generate a report for an approved order
     * and send it via email.
     */
    @Test
    void testCompleteReportGenerationFlow() {
        System.out.println("[DEBUG_LOG] Running complete report generation flow test");

        // Set the current user in the session
        SessionContext.setCurrentUser(testQAEngineer);

        // Configure sessionContext mock to return the test QA engineer
        when(sessionContext.getUser()).thenReturn(Optional.of(testQAEngineer));

        // 1. Create the QADoneViewModel
        QADoneViewModel qaDoneViewModel = new QADoneViewModel();
        injectMocks(qaDoneViewModel);

        // Set up Router parameters
        Map<String, Object> params = new HashMap<>();
        params.put("orderNumber", testOrder.getOrderNumber().toString());
        params.put("approved", true);

        // Use reflection to set the routeParameters field in Router
        try {
            java.lang.reflect.Field routeParametersField = com.belman.presentation.navigation.Router.class.getDeclaredField("routeParameters");
            routeParametersField.setAccessible(true);
            Map<String, Object> routeParams = (Map<String, Object>) routeParametersField.get(null);
            routeParams.clear();
            routeParams.putAll(params);
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] Error setting Router parameters: " + e.getMessage());
            e.printStackTrace();
        }

        // 2. Initialize the view model
        qaDoneViewModel.onShow();

        // Verify that a report was generated
        verify(reportService).generateReport(
            eq(testOrder.getId()),
            eq(ReportType.PHOTO_DOCUMENTATION),
            eq(ReportFormat.PDF),
            any(UserBusiness.class)
        );

        // 3. Test sending the report via email
        String recipientEmail = "customer@example.com";
        String subject = "QC Report for Order " + testOrder.getOrderNumber();
        String message = "Please find attached the Quality Control report for your order.";
        boolean attachReport = true;

        // Mock the report service to return true for sendReport
        when(reportService.sendReport(
            any(ReportId.class),
            anyString(),
            anyString(),
            anyString(),
            any(UserBusiness.class)
        )).thenReturn(true);

        // Send the email
        boolean sendResult = qaDoneViewModel.sendEmail(recipientEmail, subject, message, attachReport);

        // Verify that the email was sent
        assertTrue(sendResult, "Email should be sent successfully");
        verify(reportService).sendReport(
            any(ReportId.class),
            eq(recipientEmail),
            eq(subject),
            eq(message),
            any(UserBusiness.class)
        );

        System.out.println("[DEBUG_LOG] Complete report generation flow test completed successfully");
    }

    /**
     * Tests error handling during report generation.
     * This test verifies that errors during report generation are properly handled.
     */
    @Test
    void testErrorHandlingDuringReportGeneration() {
        System.out.println("[DEBUG_LOG] Running error handling during report generation test");

        // Set the current user in the session
        SessionContext.setCurrentUser(testQAEngineer);

        // Configure sessionContext mock to return the test QA engineer
        when(sessionContext.getUser()).thenReturn(Optional.of(testQAEngineer));

        // 1. Create the QADoneViewModel
        QADoneViewModel qaDoneViewModel = new QADoneViewModel();
        injectMocks(qaDoneViewModel);

        // 2. Test error handling when report generation fails
        // Mock the report service to throw an exception when generating a report
        when(reportService.generateReport(
            any(OrderId.class),
            any(ReportType.class),
            any(ReportFormat.class),
            any(UserBusiness.class)
        )).thenThrow(new RuntimeException("Failed to generate report"));

        // Set up Router parameters
        Map<String, Object> params = new HashMap<>();
        params.put("orderNumber", testOrder.getOrderNumber().toString());
        params.put("approved", true);

        // Use reflection to set the routeParameters field in Router
        try {
            java.lang.reflect.Field routeParametersField = com.belman.presentation.navigation.Router.class.getDeclaredField("routeParameters");
            routeParametersField.setAccessible(true);
            Map<String, Object> routeParams = (Map<String, Object>) routeParametersField.get(null);
            routeParams.clear();
            routeParams.putAll(params);
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] Error setting Router parameters: " + e.getMessage());
            e.printStackTrace();
        }

        // Initialize the view model
        qaDoneViewModel.onShow();

        // 3. Test error handling when sending email without a report
        String recipientEmail = "customer@example.com";
        String subject = "QC Report for Order " + testOrder.getOrderNumber();
        String message = "Please find attached the Quality Control report for your order.";
        boolean attachReport = true;

        // Send the email
        boolean sendResult = qaDoneViewModel.sendEmail(recipientEmail, subject, message, attachReport);

        // Verify that the email was not sent
        assertFalse(sendResult, "Email should not be sent when report generation failed");

        // 4. Test sending email without attaching the report
        attachReport = false;

        // Mock the report service to return true for sendReport
        when(reportService.sendReport(
            isNull(),
            anyString(),
            anyString(),
            anyString(),
            any(UserBusiness.class)
        )).thenReturn(true);

        // Send the email without attaching the report
        sendResult = qaDoneViewModel.sendEmail(recipientEmail, subject, message, attachReport);

        // Verify that the email was sent
        assertTrue(sendResult, "Email should be sent successfully without attaching the report");

        System.out.println("[DEBUG_LOG] Error handling during report generation test completed successfully");
    }

    // Helper methods

    private UserBusiness createTestQAEngineer() {
        return new UserBusiness.Builder()
            .id(new UserId("qa-123"))
            .username(new Username("testqa"))
            .password(new HashedPassword("hashedpassword"))
            .email(new EmailAddress("qa@example.com"))
            .approvalState(ApprovalState.createApproved())
            .addRole(UserRole.QA)
            .build();
    }

    private OrderBusiness createTestOrder() {
        OrderBusiness order = new OrderBusiness(
            new OrderId("order-123"),
            new UserReference(new UserId("admin-123"), new Username("admin")),
            new Timestamp(Instant.now())
        );
        order.setOrderNumber(new OrderNumber("ORD-78-230625-PIP-0003"));
        order.setStatus(OrderStatus.APPROVED); // Order must be approved to generate a report
        return order;
    }

    private List<PhotoDocument> createTestPhotos() {
        List<PhotoDocument> photos = new ArrayList<>();

        // Create a few test photos with different templates
        photos.add(PhotoDocument.builder()
            .photoId(new PhotoId("photo-1"))
            .orderId(testOrder.getId())
            .template(PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY)
            .imagePath(new Photo("test-photo-1.jpg"))
            .uploadedBy(new UserBusiness.Builder()
                .id(new UserId("worker-123"))
                .username(new Username("testworker"))
                .password(new HashedPassword("hashedpassword"))
                .email(new EmailAddress("worker@example.com"))
                .build())
            .uploadedAt(new Timestamp(Instant.now().minusSeconds(3600)))
            .build());

        photos.add(PhotoDocument.builder()
            .photoId(new PhotoId("photo-2"))
            .orderId(testOrder.getId())
            .template(PhotoTemplate.BACK_VIEW_OF_ASSEMBLY)
            .imagePath(new Photo("test-photo-2.jpg"))
            .uploadedBy(new UserBusiness.Builder()
                .id(new UserId("worker-123"))
                .username(new Username("testworker"))
                .password(new HashedPassword("hashedpassword"))
                .email(new EmailAddress("worker@example.com"))
                .build())
            .uploadedAt(new Timestamp(Instant.now().minusSeconds(3000)))
            .build());

        photos.add(PhotoDocument.builder()
            .photoId(new PhotoId("photo-3"))
            .orderId(testOrder.getId())
            .template(PhotoTemplate.TOP_VIEW_OF_JOINT)
            .imagePath(new Photo("test-photo-3.jpg"))
            .uploadedBy(new UserBusiness.Builder()
                .id(new UserId("worker-123"))
                .username(new Username("testworker"))
                .password(new HashedPassword("hashedpassword"))
                .email(new EmailAddress("worker@example.com"))
                .build())
            .uploadedAt(new Timestamp(Instant.now().minusSeconds(2400)))
            .build());

        return photos;
    }

    private ReportBusiness createTestReport() {
        return ReportBusiness.builder()
            .id(new ReportId("report-123"))
            .orderId(testOrder.getId())
            .approvedPhotos(testPhotos)
            .generatedBy(testQAEngineer)
            .generatedAt(new Timestamp(Instant.now()))
            .build();
    }

    private void setupMocks() {
        // Set up OrderRepository mock
        when(orderRepository.findByOrderNumber(any(OrderNumber.class))).thenReturn(Optional.of(testOrder));

        // Set up PhotoRepository mock
        when(photoRepository.findByOrderId(any(OrderId.class))).thenReturn(testPhotos);

        // Set up AuthenticationService mock
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(testQAEngineer));

        // Set up ReportService mock
        when(reportService.generateReport(
            any(OrderId.class),
            any(ReportType.class),
            any(ReportFormat.class),
            any(UserBusiness.class)
        )).thenReturn(testReport);
    }

    private void injectMocks(QADoneViewModel viewModel) {
        try {
            // Use reflection to inject mocks into the view model
            java.lang.reflect.Field reportServiceField = viewModel.getClass().getDeclaredField("reportService");
            reportServiceField.setAccessible(true);
            reportServiceField.set(viewModel, reportService);

            java.lang.reflect.Field sessionContextField = viewModel.getClass().getDeclaredField("sessionContext");
            sessionContextField.setAccessible(true);
            sessionContextField.set(viewModel, sessionContext);
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] Error injecting mocks: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
