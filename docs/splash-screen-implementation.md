# Splash Screen Implementation

## Overview
The splash screen is the first screen that appears when the application is launched. It displays the BelSign logo, a progress bar, and a loading message. The splash screen is implemented using JavaFX and is compatible with Gluon, allowing it to run on desktop, tablet, and smartphone platforms.

## Implementation Details

### Files
- `src/main/resources/fxml/SplashView.fxml`: FXML file defining the UI layout
- `src/main/resources/styles/app.css`: CSS file containing styles for the splash screen and other UI components
- `src/main/java/com/belman/belsign/presentation/views/splash/SplashView.java`: View class
- `src/main/java/com/belman/belsign/presentation/views/splash/SplashViewController.java`: Controller class
- `src/main/java/com/belman/belsign/presentation/views/splash/SplashViewModel.java`: ViewModel class
- `src/main/java/com/belman/belsign/com.belman.Main.java`: com.belman.Main application class

### Design
The splash screen follows the BelSign UI style guide, using the specified colors, typography, and layout. The design is responsive and adapts to different screen sizes and platforms.

#### Colors
- Background: Light grey (#f2f2f2)
- Title: Belman Blue (#004b88)
- Subtitle: Dark Grey (#333535)
- Progress Bar: Light Blue (#7fa8c5)
- Message: Grey (#575757)

#### Typography
- Title: 36px bold (desktop), 32px bold (tablet), 28px bold (smartphone)
- Subtitle: 20px (desktop), 20px (tablet), 18px (smartphone)
- Message: 16px (desktop), 16px (tablet), 14px (smartphone)

### Responsiveness
The splash screen is designed to be responsive and work well on different platforms:
- Desktop: Full-size layout with large logo and text
- Tablet: Slightly reduced size for the logo and text
- Smartphone: Further reduced size for the logo and text

This is achieved through CSS media queries and platform detection in the controller.

### Platform Detection
The application detects the platform it's running on (desktop, tablet, smartphone) and applies the appropriate styling. This is done in two places:
1. In the `com.belman.Main.java` file, which detects the platform and applies the appropriate CSS class to the root element
2. In the `SplashViewController.java` file, which adjusts UI elements based on the platform

### Error Handling
The splash screen includes error handling to gracefully handle any issues that may occur during initialization or navigation. If an error occurs, an error message is displayed to the user.

## Running the Application

### Desktop
To run the application on desktop:
```
mvn javafx:run
```

### Mobile (Android/iOS)
To build and run the application on mobile devices, use the Gluon plugin:
```
mvn gluonfx:build
mvn gluonfx:run
```

## Future Improvements
- Add animation to the logo or text for a more engaging splash screen
- Implement actual loading of resources during the splash screen instead of simulating loading
- Add a version number to the splash screen
- Consider adding a dark mode option