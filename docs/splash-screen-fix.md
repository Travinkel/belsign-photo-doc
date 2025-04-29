# Splash Screen Fix

## Issue
The application was failing to start with the following error:
```
Failed to navigate to: SplashView
java.lang.IllegalStateException: Location is not set.
```

This error occurred because the ViewLoader was looking for the FXML file in the same package as the SplashView.java file, but the FXML file was located in a different directory.

## Solution
The solution was to create a copy of the SplashView.fxml file in the same package as the SplashView.java file. This ensures that the ViewLoader can find the FXML file when it calls `viewClass.getResource(fxmlFileName)`.

### Changes Made
1. Created the directory structure for the FXML file:
   ```
   belsign\src\main\resources\presentation\views\splash
   ```

2. Created a copy of the SplashView.fxml file in the new location with the following modifications:
   - Changed the controller path from "com.belman.belsign.presentation.views.splash.SplashViewController" to "presentation.views.splash.SplashViewController"
   - Changed the stylesheets path from "@../../../../../src/main/resources/styles/app.css" to "/styles/app.css"
   - Changed the image path from "@../../../../../src/main/resources/images/logo.png" to "/images/logo.png"

### How It Works
The ViewLoader class in the AtHomeFX framework uses `viewClass.getResource(fxmlFileName)` to locate the FXML file. This method looks for the resource in the same package as the class. By placing the FXML file in the same package as the SplashView.java file, we ensure that the ViewLoader can find it.

### Alternative Solutions
An alternative solution would be to modify the ViewLoader class to allow customizing the FXML file location, but this would require changes to the framework code, which is not ideal.

Another alternative would be to modify the SplashView.java file to override the default behavior of the BaseView class, but this would require adding a method to the BaseView class to allow customizing the FXML file location.

## Testing
The fix was tested by running the application and verifying that the splash screen appears correctly.