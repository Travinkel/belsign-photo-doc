package util;

import util.NamingConventions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for generating framework components (views, controllers, view models, services, FXML files).
 * This class helps developers create new components that follow the framework's naming conventions and structure.
 */
public class ComponentGenerator {

    // Templates for different component types
    private static final String VIEW_TEMPLATE =
            "package %s;\n\n" +
            "import com.belman.belsign.framework.athomefx.core.BaseView;\n\n" +
            "public class %s extends BaseView<%s> {\n" +
            "    public %s() {\n" +
            "        super();\n" +
            "    }\n" +
            "}\n";

    private static final String CONTROLLER_TEMPLATE =
            "package %s;\n\n" +
            "import com.belman.belsign.framework.athomefx.core.BaseController;\n" +
            "import javafx.fxml.FXML;\n\n" +
            "public class %s extends BaseController<%s> {\n" +
            "    @Override\n" +
            "    public void initializeBinding() {\n" +
            "        // Bind view model properties to UI elements\n" +
            "    }\n" +
            "}\n";

    private static final String VIEWMODEL_TEMPLATE =
            "package %s;\n\n" +
            "import com.belman.belsign.framework.athomefx.core.BaseViewModel;\n" +
            "import com.belman.belsign.framework.athomefx.di.Inject;\n" +
            "import com.belman.belsign.framework.athomefx.di.ServiceLocator;\n\n" +
            "public class %s extends BaseViewModel<%s> {\n" +
            "    public %s() {\n" +
            "        ServiceLocator.injectServices(this);\n" +
            "    }\n" +
            "    \n" +
            "    @Override\n" +
            "    public void onShow() {\n" +
            "        super.onShow();\n" +
            "        // Initialize view model when shown\n" +
            "    }\n" +
            "    \n" +
            "    @Override\n" +
            "    public void onHide() {\n" +
            "        super.onHide();\n" +
            "        // Clean up resources when hidden\n" +
            "    }\n" +
            "}\n";

    private static final String SERVICE_TEMPLATE =
            "package %s;\n\n" +
            "public class %s {\n" +
            "    public %s() {\n" +
            "        // Initialize service\n" +
            "    }\n" +
            "}\n";

    private static final String FXML_TEMPLATE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" +
            "<?import java.lang.*?>\n" +
            "<?import java.util.*?>\n" +
            "<?import javafx.scene.*?>\n" +
            "<?import javafx.scene.control.*?>\n" +
            "<?import javafx.scene.layout.*?>\n\n" +
            "<AnchorPane xmlns=\"http://javafx.com/javafx\"\n" +
            "            xmlns:fx=\"http://javafx.com/fxml\"\n" +
            "            fx:controller=\"%s\"\n" +
            "            prefHeight=\"400.0\" prefWidth=\"600.0\">\n" +
            "    <children>\n" +
            "        <!-- Add your UI components here -->\n" +
            "        <Label text=\"%s\" layoutX=\"200.0\" layoutY=\"180.0\"/>\n" +
            "    </children>\n" +
            "</AnchorPane>\n";

    /**
     * Generates a complete set of components for a feature (view, controller, view model, FXML).
     *
     * @param basePackage the base package for the components
     * @param baseName the base name for the components (e.g., "Login" for "LoginView", "LoginController", etc.)
     * @param outputDir the output directory for the generated files
     * @return a map of generated file paths to their content
     * @throws IOException if there is an error creating the files
     */
    public static Map<String, String> generateFeature(String basePackage, String baseName, String outputDir) throws IOException {
        Map<String, String> generatedFiles = new HashMap<>();

        // Generate view model
        String viewModelName = NamingConventions.getViewModelName(baseName);
        String viewModelPackage = basePackage + ".viewmodel";
        String viewModelContent = String.format(VIEWMODEL_TEMPLATE,
                viewModelPackage, viewModelName, viewModelName, viewModelName);
        String viewModelPath = createFile(outputDir, viewModelPackage, viewModelName + ".java", viewModelContent);
        generatedFiles.put(viewModelPath, viewModelContent);

        // Generate controller
        String controllerName = NamingConventions.getControllerName(baseName);
        String controllerPackage = basePackage + ".controller";
        String controllerContent = String.format(CONTROLLER_TEMPLATE,
                controllerPackage, controllerName, viewModelName);
        String controllerPath = createFile(outputDir, controllerPackage, controllerName + ".java", controllerContent);
        generatedFiles.put(controllerPath, controllerContent);

        // Generate view
        String viewName = NamingConventions.getViewName(baseName);
        String viewPackage = basePackage;
        String viewContent = String.format(VIEW_TEMPLATE,
                viewPackage, viewName, viewModelName, viewName);
        String viewPath = createFile(outputDir, viewPackage, viewName + ".java", viewContent);
        generatedFiles.put(viewPath, viewContent);

        // Generate FXML
        String fxmlContent = String.format(FXML_TEMPLATE,
                controllerPackage + "." + controllerName, baseName + " View");
        String fxmlDir = outputDir + File.separator + "resources" + File.separator + "fxml";
        String fxmlPath = fxmlDir + File.separator + viewName + ".fxml";
        createDirectory(fxmlDir);
        Files.writeString(Paths.get(fxmlPath), fxmlContent);
        generatedFiles.put(fxmlPath, fxmlContent);

        return generatedFiles;
    }

    /**
     * Generates a service class.
     *
     * @param basePackage the base package for the service
     * @param baseName the base name for the service (e.g., "Auth" for "AuthService")
     * @param outputDir the output directory for the generated file
     * @return the path to the generated file
     * @throws IOException if there is an error creating the file
     */
    public static String generateService(String basePackage, String baseName, String outputDir) throws IOException {
        String serviceName = NamingConventions.getServiceName(baseName);
        String servicePackage = basePackage + ".service";
        String serviceContent = String.format(SERVICE_TEMPLATE,
                servicePackage, serviceName, serviceName);
        return createFile(outputDir, servicePackage, serviceName + ".java", serviceContent);
    }

    /**
     * Creates a file with the given content.
     *
     * @param baseDir the base directory
     * @param packageName the package name
     * @param fileName the file name
     * @param content the file content
     * @return the path to the created file
     * @throws IOException if there is an error creating the file
     */
    private static String createFile(String baseDir, String packageName, String fileName, String content) throws IOException {
        String packagePath = packageName.replace('.', File.separatorChar);
        String dirPath = baseDir + File.separator + packagePath;
        createDirectory(dirPath);

        Path filePath = Paths.get(dirPath, fileName);
        Files.writeString(filePath, content);
        return filePath.toString();
    }

    /**
     * Creates a directory if it doesn't exist.
     *
     * @param dirPath the directory path
     * @throws IOException if there is an error creating the directory
     */
    private static void createDirectory(String dirPath) throws IOException {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create directory: " + dirPath);
            }
        }
    }
}