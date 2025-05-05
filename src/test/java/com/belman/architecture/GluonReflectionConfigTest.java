package com.belman.architecture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GluonReflectionConfigTest {

    private static final String CONFIG_PATH = "src/main/resources/META-INF/native-image/com/belman/ExamProjectBelman/reflect-config.json";

    @Test
    public void reflectionConfigFileShouldExist() {
        File configFile = new File(CONFIG_PATH);
        assertTrue(configFile.exists() && configFile.isFile(),
                "Reflection configuration file is missing or not a valid file: " + CONFIG_PATH);
    }

    @Test
    public void shouldContainSplashViewController() throws Exception {
        File configFile = new File(CONFIG_PATH);
        assertTrue(configFile.exists(), "Reflection configuration file does not exist: " + CONFIG_PATH);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(configFile);

        Set<String> reflectedClasses = collectClassNames(root);

        assertTrue(reflectedClasses.contains("com.belman.presentation.views.splash.SplashViewController"),
                "Missing SplashViewController in reflect-config.json");
    }

    private Set<String> collectClassNames(JsonNode root) {
        return root.findValues("name").stream()
                .map(JsonNode::asText)
                .collect(Collectors.toSet());
    }
}
