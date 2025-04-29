# Splash Screen Improvements

## Overview
This document outlines the improvements made to the splash screen in the BelSign application.

## Issues Addressed
1. **Logo Display**: The logo was being displayed as a static image without any animation.
2. **Navigation Error**: There was an error when navigating from the splash screen to the main view.

## Changes Made

### 1. Logo Animation
- Made the logo smaller (150px width) to better signify that the application is loading
- Added a flickering animation to the logo that cycles between full opacity (1.0) and half opacity (0.5)
- The animation runs continuously until the loading is complete

### 2. Navigation Fix
- Created the directory structure for the MainView.fxml file in the correct location:
  ```
  belsign\src\main\resources\presentation\views\main
  ```
- Created a copy of the MainView.fxml file with the correct controller path:
  ```xml
  fx:controller="presentation.views.main.MainViewController"
  ```
- This ensures that the ViewLoader can find the FXML file when it calls `viewClass.getResource(fxmlFileName)`

## Implementation Details

### Logo Animation
The flickering animation is implemented using a JavaFX Timeline that animates the opacity property of the logo image:

```java
// Create a flickering animation for the logo
Timeline flickerTimeline = new Timeline(
    new KeyFrame(Duration.ZERO, new KeyValue(logoImage.opacityProperty(), 1.0)),
    new KeyFrame(Duration.seconds(0.7), new KeyValue(logoImage.opacityProperty(), 0.5)),
    new KeyFrame(Duration.seconds(1.4), new KeyValue(logoImage.opacityProperty(), 1.0))
);
flickerTimeline.setCycleCount(Timeline.INDEFINITE);
flickerTimeline.play();
```

The animation is stopped when the loading is complete:

```java
loadingTimeline.setOnFinished(event -> {
    try {
        // Stop the flickering animation
        flickerTimeline.stop();
        
        // Navigate to the main view after splash screen finishes
        getViewModel().onLoadingComplete();
    } catch (Exception e) {
        handleNavigationError(e);
    }
});
```

### Navigation Fix
The navigation fix is similar to the fix applied to the SplashView.fxml file in a previous update. The ViewLoader class in the AtHomeFX framework uses `viewClass.getResource(fxmlFileName)` to locate the FXML file. This method looks for the resource in the same package as the class. By placing the FXML file in the same package as the MainView.java file, we ensure that the ViewLoader can find it.

## Testing
The changes have been tested by running the application and verifying that:
1. The splash screen displays correctly with the smaller logo
2. The logo flickers on and off slowly
3. Navigation to the main view works correctly after the loading animation completes

## Future Improvements
- Consider adding more sophisticated animations to the splash screen
- Add a version number to the splash screen
- Implement actual loading of resources during the splash screen instead of simulating loading