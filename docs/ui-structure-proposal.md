# UI Structure Proposal

## Current Structure

The current UI structure is organized as follows:

```
src/main/java/com/belman/ui/
├── base/
│   ├── BaseController.java
│   ├── BaseView.java
│   └── BaseViewModel.java
├── components/
│   └── TouchFriendlyDialog.java
├── core/
│   └── ViewLoader.java
├── lifecycle/
│   ├── ControllerLifecycle.java
│   ├── ViewLifecycle.java
│   └── ViewModelLifecycle.java
├── navigation/
│   └── RoleBasedNavigationService.java
└── views/
    ├── admin/
    ├── login/
    │   ├── flow/
    │   │   ├── AttemptLoginState.java
    │   │   ├── CameraScanLoginState.java
    │   │   ├── DefaultLoginContext.java
    │   │   ├── HandleLoginFailureState.java
    │   │   ├── HandlePreferencesState.java
    │   │   ├── LoginContext.java
    │   │   ├── LoginState.java
    │   │   ├── PinLoginState.java
    │   │   └── StartLoginState.java
    │   ├── LoginView.java
    │   ├── LoginViewController.java
    │   └── LoginViewModel.java
    ├── main/
    ├── ordergallery/
    ├── photoreview/
    ├── photoupload/
    ├── qadashboard/
    ├── reportpreview/
    ├── splash/
    └── usermanagement/
```

## Proposed Structure

Based on Gluon best practices and the need to improve the UI flow, I propose the following structure:

```
src/main/java/com/belman/ui/
├── base/                      # Base classes (unchanged)
│   ├── BaseController.java
│   ├── BaseView.java
│   └── BaseViewModel.java
├── components/                # Shared UI components
│   ├── dialog/                # Dialog components
│   │   ├── ConfirmDialog.java
│   │   └── TouchFriendlyDialog.java
│   ├── form/                  # Form components
│   │   ├── FormField.java
│   │   └── ValidationLabel.java
│   ├── list/                  # List components
│   │   └── SelectableList.java
│   └── media/                 # Media components
│       ├── CameraPreview.java
│       └── PhotoThumbnail.java
├── core/                      # Core UI infrastructure (unchanged)
│   └── ViewLoader.java
├── lifecycle/                 # Lifecycle interfaces (unchanged)
│   ├── ControllerLifecycle.java
│   ├── ViewLifecycle.java
│   └── ViewModelLifecycle.java
├── navigation/                # Navigation services (unchanged)
│   └── RoleBasedNavigationService.java
└── usecases/                  # Organized by business use case
    ├── authentication/        # Authentication use case
    │   ├── components/        # Components specific to authentication
    │   │   ├── PinPad.java
    │   │   └── ScannerView.java
    │   ├── login/             # Login feature
    │   │   ├── flow/          # Login flow states
    │   │   ├── LoginView.java
    │   │   ├── LoginViewController.java
    │   │   └── LoginViewModel.java
    │   └── logout/            # Logout feature
    │       ├── LogoutView.java
    │       ├── LogoutViewController.java
    │       └── LogoutViewModel.java
    ├── admin/                 # Admin use case
    │   ├── components/        # Components specific to admin
    │   ├── dashboard/         # Admin dashboard feature
    │   └── usermanagement/    # User management feature
    ├── order/                 # Order management use case
    │   ├── components/        # Components specific to orders
    │   ├── gallery/           # Order gallery feature
    │   └── details/           # Order details feature
    ├── photo/                 # Photo management use case
    │   ├── components/        # Components specific to photos
    │   ├── upload/            # Photo upload feature
    │   └── review/            # Photo review feature
    ├── qa/                    # Quality assurance use case
    │   ├── components/        # Components specific to QA
    │   └── dashboard/         # QA dashboard feature
    ├── report/                # Report management use case
    │   ├── components/        # Components specific to reports
    │   ├── preview/           # Report preview feature
    │   └── export/            # Report export feature
    └── common/                # Common use case components
        ├── splash/            # Splash screen
        └── main/              # Main application shell
```

## Benefits of the Proposed Structure

1. **Organized by Business Use Case**: The new structure organizes the UI by business use case, making it easier to understand the application's functionality and find related code.

2. **Shared Components**: Common UI components are extracted into a shared `components` directory, promoting reuse and consistency across the application.

3. **Use Case-Specific Components**: Components that are specific to a use case are kept within that use case's directory, making it clear which components are used where.

4. **Improved Flow**: The new structure makes it easier to implement and understand UI flows within each use case, as related features are grouped together.

5. **Scalability**: The structure is more scalable, as new use cases can be added without affecting existing ones, and new features can be added to existing use cases without cluttering the codebase.

6. **Maintainability**: The clear separation of concerns makes the codebase more maintainable, as changes to one use case are less likely to affect others.

## Implementation Plan

1. **Create the New Directory Structure**: Create the new directories according to the proposed structure.

2. **Move Shared Components**: Identify and move shared components to the appropriate directories in the `components` folder.

3. **Reorganize Views**: Move existing views to the appropriate use case directories, updating imports as needed.

4. **Update Navigation**: Update the navigation service to work with the new structure.

5. **Create Use Case-Specific Components**: Identify and extract use case-specific components from existing views.

6. **Update Documentation**: Update documentation to reflect the new structure.

7. **Test**: Thoroughly test the application to ensure that the restructuring hasn't introduced any bugs.

## Conclusion

The proposed structure follows Gluon best practices by organizing the UI by business use case and promoting component reuse. It will make the codebase more maintainable, scalable, and easier to understand, while also improving the UI flow.