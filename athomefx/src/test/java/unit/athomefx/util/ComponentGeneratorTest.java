package unit.athomefx.util;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import util.ComponentGenerator;
import util.NamingConventions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ComponentGeneratorTest {

    @TempDir
    Path tempDir;
    
    private String outputDir;
    private String basePackage;
    private String baseName;
    
    @BeforeEach
    void setUp() {
        outputDir = tempDir.toString();
        basePackage = "com.example.test";
        baseName = "Login";
    }
    
    @AfterEach
    void tearDown() {
        // TempDir is automatically cleaned up after each test
    }
    
    @Test
    void generateFeature_shouldCreateAllComponents() throws IOException {
        // Generate the feature components
        Map<String, String> generatedFiles = ComponentGenerator.generateFeature(basePackage, baseName, outputDir);
        
        // Verify that all expected files were generated
        assertEquals(4, generatedFiles.size());
        
        // Get expected file paths
        String viewName = NamingConventions.getViewName(baseName);
        String controllerName = NamingConventions.getControllerName(baseName);
        String viewModelName = NamingConventions.getViewModelName(baseName);
        
        String viewPath = outputDir + File.separator + 
            basePackage.replace('.', File.separatorChar) + 
            File.separator + viewName + ".java";
        
        String controllerPath = outputDir + File.separator + 
            (basePackage + ".controller").replace('.', File.separatorChar) + 
            File.separator + controllerName + ".java";
        
        String viewModelPath = outputDir + File.separator + 
            (basePackage + ".viewmodel").replace('.', File.separatorChar) + 
            File.separator + viewModelName + ".java";
        
        String fxmlPath = outputDir + File.separator + 
            "resources" + File.separator + "fxml" + 
            File.separator + viewName + ".fxml";
        
        // Verify that the files exist
        assertTrue(Files.exists(Path.of(viewPath)), "View file should exist");
        assertTrue(Files.exists(Path.of(controllerPath)), "Controller file should exist");
        assertTrue(Files.exists(Path.of(viewModelPath)), "ViewModel file should exist");
        assertTrue(Files.exists(Path.of(fxmlPath)), "FXML file should exist");
        
        // Verify file contents
        String viewContent = Files.readString(Path.of(viewPath));
        String controllerContent = Files.readString(Path.of(controllerPath));
        String viewModelContent = Files.readString(Path.of(viewModelPath));
        String fxmlContent = Files.readString(Path.of(fxmlPath));
        
        // Check that the view extends BaseView with the correct view model
        assertTrue(viewContent.contains("extends BaseView<" + viewModelName + ">"), 
            "View should extend BaseView with the correct view model");
        
        // Check that the controller extends BaseController with the correct view model
        assertTrue(controllerContent.contains("extends BaseController<" + viewModelName + ">"), 
            "Controller should extend BaseController with the correct view model");
        
        // Check that the view model extends BaseViewModel
        assertTrue(viewModelContent.contains("extends BaseViewModel<" + viewModelName + ">"), 
            "ViewModel should extend BaseViewModel");
        
        // Check that the FXML file has the correct controller
        assertTrue(fxmlContent.contains("fx:controller=\"" + basePackage + ".controller." + controllerName + "\""), 
            "FXML should have the correct controller");
    }
    
    @Test
    void generateService_shouldCreateServiceClass() throws IOException {
        // Generate the service
        String servicePath = ComponentGenerator.generateService(basePackage, baseName, outputDir);
        
        // Verify that the file exists
        assertTrue(Files.exists(Path.of(servicePath)), "Service file should exist");
        
        // Verify file content
        String serviceContent = Files.readString(Path.of(servicePath));
        
        // Get expected service name
        String serviceName = NamingConventions.getServiceName(baseName);
        
        // Check that the service has the correct class name
        assertTrue(serviceContent.contains("public class " + serviceName), 
            "Service should have the correct class name");
        
        // Check that the service is in the correct package
        assertTrue(serviceContent.contains("package " + basePackage + ".service"), 
            "Service should be in the correct package");
    }
    
    @Test
    void generateFeature_shouldHandleSpecialCharactersInBaseName() throws IOException {
        // Use a base name with special characters
        String specialBaseName = "User_Profile";
        
        // Generate the feature components
        Map<String, String> generatedFiles = ComponentGenerator.generateFeature(basePackage, specialBaseName, outputDir);
        
        // Verify that all expected files were generated
        assertEquals(4, generatedFiles.size());
        
        // Get expected file paths
        String viewName = NamingConventions.getViewName(specialBaseName);
        
        String viewPath = outputDir + File.separator + 
            basePackage.replace('.', File.separatorChar) + 
            File.separator + viewName + ".java";
        
        // Verify that the file exists
        assertTrue(Files.exists(Path.of(viewPath)), "View file should exist");
    }
}