package unit.athomefx.ui;

import com.belman.belsign.framework.athomefx.ui.*;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.Test;

/**
 * Test class for the sealed interfaces in the AtHomeFX framework.
 */
class SealedInterfacesTest {

    /**
     * Tests the Stage interface and its implementations.
     */
    @Test
    void testStageInterface() {
        // Create a mock JavaFX Stage
        Stage javaFxStage = new Stage();

        // Create instances of the Stage implementations
        com.belman.belsign.framework.athomefx.ui.Stage desktopStage = new DesktopStage(javaFxStage);
        com.belman.belsign.framework.athomefx.ui.Stage iPadStage = new IPadStage(javaFxStage);
        com.belman.belsign.framework.athomefx.ui.Stage smartPhoneStage = new SmartPhoneStage(javaFxStage);

        // Test that the implementations are of the correct type
        assertTrue(desktopStage instanceof DesktopStage);
        assertTrue(iPadStage instanceof IPadStage);
        assertTrue(smartPhoneStage instanceof SmartPhoneStage);

        // Test that the implementations have the correct behavior
        desktopStage.setTitle("Desktop Stage");
        assertEquals("Desktop Stage", desktopStage.getTitle());

        iPadStage.setTitle("iPad Stage");
        assertEquals("iPad Stage", iPadStage.getTitle());

        smartPhoneStage.setTitle("Smartphone Stage");
        assertEquals("Smartphone Stage", smartPhoneStage.getTitle());

        // Test platform-specific behavior
        desktopStage.setWidth(1280);
        assertEquals(1280, desktopStage.getWidth());

        iPadStage.setWidth(1280); // Should be ignored
        assertEquals(1024, iPadStage.getWidth()); // Default iPad width

        smartPhoneStage.setWidth(1280); // Should be ignored
        assertEquals(390, smartPhoneStage.getWidth()); // Default smartphone width
    }

    /**
     * Tests the Scene interface and its implementations.
     */
    @Test
    void testSceneInterface() {
        // Create a mock JavaFX Parent
        VBox root = new VBox();

        // Create instances of the Scene implementations
        com.belman.belsign.framework.athomefx.ui.Scene loginScene = new LoginScene(root);
        com.belman.belsign.framework.athomefx.ui.Scene photoScene = new PhotoScene(root);
        com.belman.belsign.framework.athomefx.ui.Scene adminScene = new AdminScene(root);

        // Test that the implementations are of the correct type
        assertTrue(loginScene instanceof LoginScene);
        assertTrue(photoScene instanceof PhotoScene);
        assertTrue(adminScene instanceof AdminScene);

        // Test that the implementations have the correct behavior
        assertEquals(root, loginScene.getRoot());
        assertEquals(root, photoScene.getRoot());
        assertEquals(root, adminScene.getRoot());

        // Test scene-specific behavior
        assertTrue(((PhotoScene) photoScene).takePhoto());
        assertTrue(((PhotoScene) photoScene).savePhoto("test.jpg"));

        assertTrue(((AdminScene) adminScene).addUser("admin", "ADMIN"));
        assertTrue(((AdminScene) adminScene).removeUser("admin"));
        assertTrue(((AdminScene) adminScene).changeUserRole("admin", "USER"));
    }

    /**
     * Tests pattern matching with the Stage interface.
     */
    @Test
    void testStagePatternMatching() {
        // Create a mock JavaFX Stage
        Stage javaFxStage = new Stage();

        // Create instances of the Stage implementations
        com.belman.belsign.framework.athomefx.ui.Stage desktopStage = new DesktopStage(javaFxStage);
        com.belman.belsign.framework.athomefx.ui.Stage iPadStage = new IPadStage(javaFxStage);
        com.belman.belsign.framework.athomefx.ui.Stage smartPhoneStage = new SmartPhoneStage(javaFxStage);

        // Test pattern matching with the Stage interface
        assertEquals("Desktop", getStageType(desktopStage));
        assertEquals("iPad", getStageType(iPadStage));
        assertEquals("Smartphone", getStageType(smartPhoneStage));
    }

    /**
     * Tests pattern matching with the Scene interface.
     */
    @Test
    void testScenePatternMatching() {
        // Create a mock JavaFX Parent
        VBox root = new VBox();

        // Create instances of the Scene implementations
        com.belman.belsign.framework.athomefx.ui.Scene loginScene = new LoginScene(root);
        com.belman.belsign.framework.athomefx.ui.Scene photoScene = new PhotoScene(root);
        com.belman.belsign.framework.athomefx.ui.Scene adminScene = new AdminScene(root);

        // Test pattern matching with the Scene interface
        assertEquals("Login", getSceneType(loginScene));
        assertEquals("Photo", getSceneType(photoScene));
        assertEquals("Admin", getSceneType(adminScene));
    }

    /**
     * Gets the type of a Stage using pattern matching.
     * 
     * @param stage the stage
     * @return the stage type
     */
    private String getStageType(com.belman.belsign.framework.athomefx.ui.Stage stage) {
        return switch (stage) {
            case DesktopStage ignored -> "Desktop";
            case IPadStage ignored -> "iPad";
            case SmartPhoneStage ignored -> "Smartphone";
        };
    }

    /**
     * Gets the type of a Scene using pattern matching.
     * 
     * @param scene the scene
     * @return the scene type
     */
    private String getSceneType(com.belman.belsign.framework.athomefx.ui.Scene scene) {
        return switch (scene) {
            case LoginScene ignored -> "Login";
            case PhotoScene ignored -> "Photo";
            case AdminScene ignored -> "Admin";
        };
    }
}
