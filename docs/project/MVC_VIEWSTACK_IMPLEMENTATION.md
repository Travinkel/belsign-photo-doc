# MVC ViewStack Implementation

## Overview

This document describes the implementation of the MVC ViewStack pattern in the BelSign Photo Documentation application. The pattern is based on Gluon's MVC framework and provides a way to manage views and navigation in a structured and maintainable way.

## Components

### ViewFactory

The `ViewFactory` interface is part of the Factory Method pattern for view creation. It provides a way to create views without specifying their concrete classes.

```java
public interface ViewFactory {
    View createView();
}
```

### AbstractViewFactory

The `AbstractViewFactory` class is an abstract implementation of the `ViewFactory` interface. It provides a base class for view factories that need access to view dependencies.

```java
public abstract class AbstractViewFactory implements ViewFactory {
    protected final ViewDependencies viewDependencies;

    protected AbstractViewFactory(ViewDependencies viewDependencies) {
        this.viewDependencies = viewDependencies;
    }
}
```

### ViewDependencies

The `ViewDependencies` class provides a way to manage dependencies for views. It includes the navigation service and view registry.

```java
public class ViewDependencies {
    private final RoleBasedNavigationService navigationService;
    private final ViewRegistry viewRegistry;

    public ViewDependencies(RoleBasedNavigationService navigationService, ViewRegistry viewRegistry) {
        this.navigationService = navigationService;
        this.viewRegistry = viewRegistry;
    }

    public RoleBasedNavigationService getNavigationService() {
        return navigationService;
    }

    public ViewRegistry getViewRegistry() {
        return viewRegistry;
    }
}
```

### ViewRegistry

The `ViewRegistry` class is a registry for view factories. It provides a way to register and create views.

```java
public class ViewRegistry {
    private static ViewRegistry instance;
    private final Map<String, ViewFactory> viewFactories = new HashMap<>();

    private ViewRegistry() {
        // Private constructor to prevent instantiation
    }

    public static ViewRegistry getInstance() {
        if (instance == null) {
            instance = new ViewRegistry();
        }
        return instance;
    }

    public void registerView(String viewId, ViewFactory factory) {
        viewFactories.put(viewId, factory);
    }

    public Parent createView(String viewId) {
        ViewFactory factory = viewFactories.get(viewId);
        if (factory == null) {
            throw new IllegalArgumentException("No factory registered for view ID: " + viewId);
        }
        return factory.createView();
    }
}
```

### ViewStackManager

The `ViewStackManager` class is responsible for managing view stacks and providing a way to navigate between views. It integrates with Gluon's MVC pattern and provides support for the ViewStack pattern.

```java
public class ViewStackManager {
    private static ViewStackManager instance;
    private final MobileApplication application;
    private final Map<String, ViewFactory> viewFactories = new HashMap<>();
    private final ViewDependencies viewDependencies;
    private final Stack<String> viewStack = new Stack<>();

    private ViewStackManager(MobileApplication application, ViewDependencies viewDependencies) {
        this.application = application;
        this.viewDependencies = viewDependencies;
    }

    public static ViewStackManager getInstance(MobileApplication application, ViewDependencies viewDependencies) {
        if (instance == null) {
            instance = new ViewStackManager(application, viewDependencies);
        }
        return instance;
    }

    public static ViewStackManager initialize(MobileApplication application, 
                                             RoleBasedNavigationService navigationService,
                                             ViewRegistry viewRegistry) {
        // Create ViewDependencies
        ViewDependencies viewDependencies = new ViewDependencies(navigationService, viewRegistry);

        // Get the ViewStackManager instance
        ViewStackManager manager = getInstance(application, viewDependencies);

        // Register all views
        manager.registerAllViews();

        // Set up the Router with the MobileApplication
        Router.setMobileApplication(application);

        return manager;
    }

    public void registerView(String viewId, ViewFactory factory) {
        viewFactories.put(viewId, factory);

        // Register the view with the MobileApplication
        application.addViewFactory(viewId, () -> {
            View view = factory.createView();
            return view;
        });
    }

    public void navigateTo(String viewId) {
        // Push the view ID onto the stack
        viewStack.push(viewId);

        // Switch to the view
        application.switchView(viewId);
    }

    public boolean navigateBack() {
        if (viewStack.size() <= 1) {
            return false;
        }

        // Remove the current view from the stack
        viewStack.pop();

        // Get the previous view
        String previousViewId = viewStack.peek();

        // Switch to the previous view
        application.switchView(previousViewId);

        return true;
    }

    public void registerAllViews() {
        // Register the splash view
        registerView("SplashView", new SplashViewFactory(viewDependencies));

        // Register the login view
        registerView("LoginView", new LoginViewFactory(viewDependencies));

        // Register the admin view
        registerView("AdminView", new AdminViewFactory(viewDependencies));

        // Register the main view
        registerView("MainView", new MainViewFactory(viewDependencies));

        // Register the QA dashboard view
        registerView("QADashboardView", new QADashboardViewFactory(viewDependencies));

        // Register the Photo Review view
        registerView("PhotoReviewView", new PhotoReviewViewFactory(viewDependencies));

        // Register the Photo Upload view
        registerView("PhotoUploadView", new PhotoUploadViewFactory(viewDependencies));

        // Register the Order Gallery view
        registerView("OrderGalleryView", new OrderGalleryViewFactory(viewDependencies));

        // Register the Report Preview view
        registerView("ReportPreviewView", new ReportPreviewViewFactory(viewDependencies));

        // TODO: Register other views as they are migrated to the new structure
    }
}
```

## Usage

### Initializing the ViewStackManager

The `ViewStackManager` should be initialized during application startup. This can be done in the `Main` class:

```java
@Override
public void init() {
    // ... other initialization code ...

    // Initialize the ViewStackManager
    ViewRegistry viewRegistry = ViewRegistry.getInstance();
    RoleBasedNavigationService navigationService = new RoleBasedNavigationService(sessionContext);
    ViewStackManager.initialize(this, navigationService, viewRegistry);
}
```

### Creating a View Factory

To create a view factory for a view, extend the `AbstractViewFactory` class:

```java
public class LoginViewFactory extends AbstractViewFactory {
    public LoginViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    @Override
    public View createView() {
        return new LoginView();
    }
}
```

Similarly, for the splash view:

```java
public class SplashViewFactory extends AbstractViewFactory {
    public SplashViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    @Override
    public View createView() {
        return new SplashView();
    }
}
```

Similarly, for the main view:

```java
public class MainViewFactory extends AbstractViewFactory {
    public MainViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    @Override
    public View createView() {
        return new MainView();
    }
}
```

Similarly, for the QA dashboard view:

```java
public class QADashboardViewFactory extends AbstractViewFactory {
    public QADashboardViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    @Override
    public View createView() {
        return new QADashboardView();
    }
}
```

Similarly, for the Photo Review view:

```java
public class PhotoReviewViewFactory extends AbstractViewFactory {
    public PhotoReviewViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    @Override
    public View createView() {
        return new PhotoReviewView();
    }
}
```

Similarly, for the Photo Upload view:

```java
public class PhotoUploadViewFactory extends AbstractViewFactory {
    public PhotoUploadViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    @Override
    public View createView() {
        return new PhotoUploadView();
    }
}
```

Similarly, for the Order Gallery view:

```java
public class OrderGalleryViewFactory extends AbstractViewFactory {
    public OrderGalleryViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    @Override
    public View createView() {
        return new OrderGalleryView();
    }
}
```

Similarly, for the Report Preview view:

```java
public class ReportPreviewViewFactory extends AbstractViewFactory {
    public ReportPreviewViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    @Override
    public View createView() {
        return new ReportPreviewView();
    }
}
```

### Creating a View

To create a view, extend the `BaseView` class:

```java
public class LoginView extends BaseView<LoginViewModel> {
    public LoginView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setTitleText("Login");
    }
}
```

For views that don't need an app bar, like the splash screen:

```java
public class SplashView extends BaseView<SplashViewModel> {
    @Override
    public boolean shouldShowAppBar() {
        return false; // Don't show the app bar on the splash screen
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setVisible(false);
    }
}
```

For views that need a back button, like the main view:

```java
public class MainView extends BaseView<MainViewModel> {
    public MainView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setTitleText("Role Selection");
        appBar.setNavIcon(getBackButton());
    }

    private javafx.scene.Node getBackButton() {
        return com.gluonhq.charm.glisten.visual.MaterialDesignIcon.ARROW_BACK.button(e -> navigateBack());
    }

    @Override
    protected void navigateBack() {
        com.belman.ui.navigation.Router.navigateBack();
    }
}
```

For views with action items, like the QA dashboard view:

```java
public class QADashboardView extends BaseView<QADashboardViewModel> {
    public QADashboardView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> navigateBack()));
        appBar.setTitleText("QA Dashboard");
        appBar.getActionItems().add(MaterialDesignIcon.POWER_SETTINGS_NEW.button(e -> getViewModel().logout()));
    }

    @Override
    protected void navigateBack() {
        Router.navigateBack();
    }
}
```

Similarly, for the Photo Review view:

```java
public class PhotoReviewView extends BaseView<PhotoReviewViewModel> {
    public PhotoReviewView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> navigateBack()));
        appBar.setTitleText("Photo Review");
        appBar.getActionItems().add(MaterialDesignIcon.POWER_SETTINGS_NEW.button(e -> getViewModel().logout()));
    }

    @Override
    protected void navigateBack() {
        Router.navigateBack();
    }
}
```

Similarly, for the Photo Upload view:

```java
public class PhotoUploadView extends BaseView<PhotoUploadViewModel> {
    public PhotoUploadView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> Router.navigateBack()));
        appBar.setTitleText("Photo Upload");
        appBar.getActionItems().add(MaterialDesignIcon.POWER_SETTINGS_NEW.button(e -> getViewModel().logout()));
    }
}
```

Similarly, for the Order Gallery view:

```java
public class OrderGalleryView extends BaseView<OrderGalleryViewModel> {
    public OrderGalleryView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> Router.navigateBack()));
        appBar.setTitleText("OrderBusiness Gallery");
        appBar.getActionItems().add(MaterialDesignIcon.POWER_SETTINGS_NEW.button(e -> getViewModel().logout()));
    }
}
```

Similarly, for the Report Preview view:

```java
public class ReportPreviewView extends BaseView<ReportPreviewViewModel> {
    public ReportPreviewView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> Router.navigateBack()));
        appBar.setTitleText("Report Preview");
        appBar.getActionItems().add(MaterialDesignIcon.POWER_SETTINGS_NEW.button(e -> getViewModel().logout()));
    }
}
```

### Navigating Between Views

To navigate between views, use the `ViewStackManager`:

```java
// In a controller or view model
public void navigateToLogin() {
    ViewStackManager.getInstance().navigateTo("LoginView");
}

// To navigate to the main view
public void navigateToMain() {
    ViewStackManager.getInstance().navigateTo("MainView");
}

// To navigate to the QA dashboard view
public void navigateToQADashboard() {
    ViewStackManager.getInstance().navigateTo("QADashboardView");
}

// To navigate to the Photo Review view
public void navigateToPhotoReview() {
    ViewStackManager.getInstance().navigateTo("PhotoReviewView");
}

// To navigate to the Photo Upload view
public void navigateToPhotoUpload() {
    ViewStackManager.getInstance().navigateTo("PhotoUploadView");
}

// To navigate to the Order Gallery view
public void navigateToOrderGallery() {
    ViewStackManager.getInstance().navigateTo("OrderGalleryView");
}

// To navigate to the Report Preview view
public void navigateToReportPreview() {
    ViewStackManager.getInstance().navigateTo("ReportPreviewView");
}

// In the Main class, to show the splash view at startup
public void postInit(Scene scene) {
    // Apply platform-specific styling and load CSS
    // ...

    // Show the splash view
    ViewStackManager.getInstance().navigateTo("SplashView");
}

// To navigate back
public void goBack() {
    ViewStackManager.getInstance().navigateBack();
}
```

## Benefits

The MVC ViewStack pattern provides several benefits:

1. **Separation of Concerns**: The pattern separates the view creation logic from the view itself, making the code more maintainable.

2. **Dependency Injection**: The pattern provides a way to inject dependencies into views, making them more testable.

3. **Navigation History**: The pattern maintains a navigation history, making it easy to navigate back to previous views.

4. **Structured Navigation**: The pattern provides a structured way to navigate between views, making the code more maintainable.

5. **View Stack Management**: The pattern provides a way to manage view stacks, making it easy to implement complex navigation flows.

## Conclusion

The MVC ViewStack pattern provides a structured and maintainable way to manage views and navigation in a Gluon Mobile application. It integrates with Gluon's MVC pattern and provides support for the ViewStack pattern, making it easy to implement complex navigation flows.
