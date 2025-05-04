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
    public void shouldContainSplashViewController() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(CONFIG_PATH));

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
