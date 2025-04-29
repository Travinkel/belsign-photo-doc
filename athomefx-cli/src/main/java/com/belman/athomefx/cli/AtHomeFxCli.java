package com.belman.athomefx.cli;

import util.ComponentGenerator;

import java.io.IOException;
import java.util.Scanner;

/**
 * Command-line interface for generating components in the AtHomeFX framework.
 */
public class AtHomeFxCli {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to AtHomeFX CLI!");
        System.out.println("What would you like to generate?");
        System.out.println("1. Feature (View + Controller + ViewModel + FXML)");
        System.out.println("2. Service");
        System.out.print("Enter choice (1 or 2): ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter base package (e.g., com.example.features.login): ");
        String basePackage = scanner.nextLine();

        System.out.print("Enter base name (e.g., Login, Dashboard, UserProfile): ");
        String baseName = scanner.nextLine();

        System.out.print("Enter output directory (e.g., ./src/main/java): ");
        String outputDir = scanner.nextLine();

        try {
            if (choice == 1) {
                ComponentGenerator.generateFeature(basePackage, baseName, outputDir);
                System.out.println("✅ Feature generated successfully.");
            } else if (choice == 2) {
                ComponentGenerator.generateService(basePackage, baseName, outputDir);
                System.out.println("✅ Service generated successfully.");
            } else {
                System.out.println("❌ Invalid choice.");
            }
        } catch (IOException e) {
            System.err.println("❌ Error generating component: " + e.getMessage());
        }

        scanner.close();
    }
}
