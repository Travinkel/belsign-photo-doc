package unit.athomefx;


import di.ServiceLocator;
import exceptions.ServiceInjectionException;
import navigation.Router;
import org.junit.jupiter.api.AfterEach;
import unit.athomefx.dummy.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class AtHomeFXFrameworkTest {
    @AfterEach
    void tearDown() {
        ServiceLocator.clear();
    }


    @BeforeAll
    static void setup() throws Exception {
        // This initializes JavaFX. JFXPanel triggers the JavaFX runtime to start.
        new JFXPanel();
        // Initialize the JavaFX application thread
        // Now, JavaFX is ready and Platform.runLater() will work.

        Platform.runLater(() -> {
            Stage stage = new Stage();
            Router.setPrimaryStage(stage);
        });

        // Optional: Sleep briefly to allow JavaFX thread to fully initialize
        Thread.sleep(500);

        // Register DummyService in the ServiceLocator
        ServiceLocator.registerService(DummyService.class, new DummyService());
    }

    @Test
    void testServiceLocator() {
        ServiceLocator.clear();
        assertThrows(ServiceInjectionException.class, () -> ServiceLocator.getService(DummyService.class));
    }

    @Test
    void testServiceLocatorSuccessfulInjection() {
        ServiceLocator.clear();
        DummyService service = new DummyService();
        ServiceLocator.registerService(DummyService.class, service);
        DummyService locatedService = ServiceLocator.getService(DummyService.class);
        assertNotNull(locatedService);
        assertEquals("Hello, World!", locatedService.sayHello());
    }

    @Test
    void testServiceLocatorNullRegistration() {
        assertThrows(IllegalArgumentException.class, () ->
                ServiceLocator.registerService(DummyService.class, null)
        );
    }

    @Test
    void testServiceLocatorConcurrentAccess() throws InterruptedException {
        final int threadCount = 10;
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final DummyService dummyService = new DummyService();

        ServiceLocator.registerService(DummyService.class, dummyService);

        // Create multiple threads to retrieve the service
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    DummyService retrievedService = ServiceLocator.getService(DummyService.class);
                    assertNotNull(retrievedService);
                    assertEquals("Hello, World!", retrievedService.sayHello());
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // Wait for all threads to complete
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    void testRouterNavigationToNullView() {
        Router router = Router.getInstance();
        assertThrows(RuntimeException.class, () -> router.navigateTo(null));
    }

    @Test
    void testRouterMultipleNavigations() throws InterruptedException {
        ServiceLocator.clear();
        ServiceLocator.registerService(DummyService.class, new DummyService());

        final CountDownLatch latch = new CountDownLatch(1);


        Platform.runLater(() -> {
            try {
                Router router = Router.getInstance();
                router.navigateTo(DummyView.class);
                assertNotNull(router);

                router.navigateTo(DummyView.class); // Navigate again
                assertNotNull(router);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void testRouterNavigationToUnregisteredView() {
        Router router = Router.getInstance();
        assertThrows(RuntimeException.class, () -> router.navigateTo(UnregisteredView.class));
    }

    @Test
    void testRouterNavigationSuccess() throws Exception {
        ServiceLocator.clear();
        ServiceLocator.registerService(DummyService.class, new DummyService());

        final CountDownLatch latch = new CountDownLatch(1);


        Platform.runLater(() -> {
            DummyView dummyView = new DummyView();
            Router.getInstance().navigateTo(DummyView.class);
            assertNotNull(Router.getInstance());
            assertNotNull(dummyView.getRoot());
            latch.countDown(); // Root must not be null
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS));

    }

    @Test
    void testRouterNavigation() {
        Router router = Router.getInstance();
        assertNotNull(router);
        assertThrows(RuntimeException.class, () -> router.navigateTo(DummyView.class)); // Updated to RuntimeException
    }

    @Test
    void testDummyViewModelInjection() {
        DummyService service = new DummyService();
        ServiceLocator.registerService(DummyService.class, service); // <--- VIGTIG LINJE

        DummyViewModel viewModel = new DummyViewModel(); // Kan nu lave sig selv via ServiceLocator
        assertNotNull(viewModel.getInjectedServiceMessage());
        assertEquals("Hello, World!", viewModel.getInjectedServiceMessage());
    }


    @Test
    void testDummyViewModelLifecycle() {
        ServiceLocator.clear();
        ServiceLocator.registerService(DummyService.class, new DummyService());
        DummyViewModel viewModel = new DummyViewModel();

        assertFalse(viewModel.isShown());
        assertFalse(viewModel.isHidden());

        viewModel.onShow();
        assertTrue(viewModel.isShown());
        assertFalse(viewModel.isHidden());

        viewModel.onHide();
        assertTrue(viewModel.isShown());
        assertTrue(viewModel.isHidden());
    }

    @Test
    void testServiceLocatorDuplicateRegistration() {
        DummyService service1 = new DummyService();
        DummyService service2 = new DummyService();
        ServiceLocator.registerService(DummyService.class, service1);

        assertThrows(ServiceInjectionException.class, () ->
            ServiceLocator.registerService(DummyService.class, service2)
        );
    }

    @Test
    void testDummyViewModelWithoutServiceInjection() {
        ServiceLocator.clear();
        assertThrows(ServiceInjectionException.class, DummyViewModel::new);
    }

    @Test
    void testDummyControllerInitialization() {
        DummyController controller = new DummyController();
        assertDoesNotThrow(controller::initializeBinding);
    }

    @Test
    void testDummyViewRootNotNull() {
        ServiceLocator.clear();
        ServiceLocator.registerService(DummyService.class, new DummyService());
        DummyView dummyView = new DummyView();
        assertNotNull(dummyView.getRoot());
    }

    @Test
    void testRouterNavigationFailure() {
        Router router = Router.getInstance();
        assertThrows(RuntimeException.class, () -> router.navigateTo(null));
    }

    @Test
    void testDummyServiceBehavior() {
        DummyService service = new DummyService();
        assertEquals("Hello, World!", service.sayHello());
    }
}
