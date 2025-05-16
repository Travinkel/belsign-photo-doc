# UI Structure Implementation Task List

## Overview
This document outlines the tasks required to complete the implementation of the proposed UI structure as described in the SprintBacklog.md file. The proposed structure organizes the UI by business use case, making it easier to understand the application's functionality and find related code.

## Current Status
The directory structure for the proposed UI structure has already been created, but some implementation details are still missing. The following tasks are required to complete the implementation.

## Tasks

### 1. Complete the Migration of Views
- [X] **Move Login View Components**
  - [X] Move LoginViewController.java from src/main/java/com/belman/ui/views/login/ to src/main/java/com/belman/ui/usecases/authentication/login/
  - [X] Move LoginViewModel.java from src/main/java/com/belman/ui/views/login/ to src/main/java/com/belman/ui/usecases/authentication/login/
  - [X] Update imports in all moved files

- [X] **Move Login Flow States**
  - [X] Move AttemptLoginState.java from src/main/java/com/belman/ui/views/login/flow/ to src/main/java/com/belman/ui/usecases/authentication/login/flow/
  - [X] Move CameraScanLoginState.java from src/main/java/com/belman/ui/views/login/flow/ to src/main/java/com/belman/ui/usecases/authentication/login/flow/
  - [X] Move DefaultLoginContext.java from src/main/java/com/belman/ui/views/login/flow/ to src/main/java/com/belman/ui/usecases/authentication/login/flow/
  - [X] Move HandleLoginFailureState.java from src/main/java/com/belman/ui/views/login/flow/ to src/main/java/com/belman/ui/usecases/authentication/login/flow/
  - [X] Move HandlePreferencesState.java from src/main/java/com/belman/ui/views/login/flow/ to src/main/java/com/belman/ui/usecases/authentication/login/flow/
  - [X] Move LoginContext.java from src/main/java/com/belman/ui/views/login/flow/ to src/main/java/com/belman/ui/usecases/authentication/login/flow/
  - [X] Move LoginState.java from src/main/java/com/belman/ui/views/login/flow/ to src/main/java/com/belman/ui/usecases/authentication/login/flow/
  - [X] Move PinLoginState.java from src/main/java/com/belman/ui/views/login/flow/ to src/main/java/com/belman/ui/usecases/authentication/login/flow/
  - [X] Move StartLoginState.java from src/main/java/com/belman/ui/views/login/flow/ to src/main/java/com/belman/ui/usecases/authentication/login/flow/
  - [X] Update imports in all moved files

- [X] **Move Other Views**
  - [X] Move Admin Views from src/main/java/com/belman/ui/views/admin/ to src/main/java/com/belman/ui/usecases/admin/
  - [X] Move Main Views from src/main/java/com/belman/ui/views/main/ to src/main/java/com/belman/ui/usecases/common/main/
  - [X] Move Order Gallery Views from src/main/java/com/belman/ui/views/ordergallery/ to src/main/java/com/belman/ui/usecases/order/gallery/
  - [X] Move Photo Review Views from src/main/java/com/belman/ui/views/photoreview/ to src/main/java/com/belman/ui/usecases/photo/review/
  - [X] Move Photo Upload Views from src/main/java/com/belman/ui/views/photoupload/ to src/main/java/com/belman/ui/usecases/photo/upload/
  - [X] Move QA Dashboard Views from src/main/java/com/belman/ui/views/qadashboard/ to src/main/java/com/belman/ui/usecases/qa/dashboard/
  - [X] Move Report Preview Views from src/main/java/com/belman/ui/views/reportpreview/ to src/main/java/com/belman/ui/usecases/report/preview/
  - [X] Move Splash Views from src/main/java/com/belman/ui/views/splash/ to src/main/java/com/belman/ui/usecases/common/splash/
  - [X] Move User Management Views from src/main/java/com/belman/ui/views/usermanagement/ to src/main/java/com/belman/ui/usecases/admin/usermanagement/
  - [X] Update imports in all moved files

### 2. Implement Design Patterns

- [X] **Implement View Factory Pattern**
  - [X] Create ViewFactory interface in src/main/java/com/belman/ui/core/
  ```java
  public interface ViewFactory {
      View createView();
  }
  ```
  - [X] Implement ViewFactory for each view
  ```java
  public class LoginViewFactory implements ViewFactory {
      @Override
      public View createView() {
          return new LoginView();
      }
  }
  ```

- [X] **Implement UI Flow with State Pattern**
  - [X] Create generic state context in src/main/java/com/belman/ui/core/
  ```java
  public abstract class FlowContext<T extends FlowState> {
      protected T currentState;

      public void setState(T state) {
          this.currentState = state;
          onStateChanged();
      }

      protected abstract void onStateChanged();
  }
  ```
  - [X] Create generic state interface in src/main/java/com/belman/ui/core/
  ```java
  public interface FlowState {
      void enter();
      void exit();
  }
  ```
  - [X] Update existing state implementations to use the new interfaces

- [X] **Implement Observer Pattern for View Updates**
  - [X] Create ViewObserver interface in src/main/java/com/belman/ui/core/
  ```java
  public interface ViewObserver {
      void onViewUpdated(ViewEvent event);
  }
  ```
  - [X] Create ObservableView class in src/main/java/com/belman/ui/base/
  ```java
  public abstract class ObservableView extends BaseView {
      private List<ViewObserver> observers = new ArrayList<>();

      public void addObserver(ViewObserver observer) {
          observers.add(observer);
      }

      protected void notifyObservers(ViewEvent event) {
          observers.forEach(observer -> observer.onViewUpdated(event));
      }
  }
  ```
  - [X] Update existing views to use ObservableView

- [X] **Implement Factory Method for View Creation**
  - [X] Create ViewRegistry class in src/main/java/com/belman/ui/core/
  ```java
  public abstract class ViewRegistry {
      private Map<String, ViewFactory> viewFactories = new HashMap<>();

      public void registerView(String viewId, ViewFactory factory) {
          viewFactories.put(viewId, factory);
      }

      public View createView(String viewId) {
          return viewFactories.get(viewId).createView();
      }
  }
  ```
  - [X] Update existing code to use ViewRegistry

- [X] **Implement Command Pattern for UI Actions**
  - [X] Create UICommand interface in src/main/java/com/belman/ui/core/
  ```java
  public interface UICommand {
      void execute();
      void undo();
  }
  ```
  - [X] Create CommandManager class in src/main/java/com/belman/ui/core/
  ```java
  public class CommandManager {
      private Stack<UICommand> undoStack = new Stack<>();

      public void executeCommand(UICommand command) {
          command.execute();
          undoStack.push(command);
      }

      public void undo() {
          if (!undoStack.isEmpty()) {
              undoStack.pop().undo();
          }
      }
  }
  ```
  - [X] Implement UICommand for common UI actions

### 3. Additional Improvements

- [X] **Add Dependency Injection Container**
  - [X] Create src/main/java/com/belman/ui/di/ directory
  - [X] Implement dependency injection container

- [X] **Add Event Bus System**
  - [X] Create src/main/java/com/belman/ui/events/ directory
  - [X] Implement event bus system

- [X] **Add UI Utilities**
  - [X] Create src/main/java/com/belman/ui/utils/ directory
  - [X] Implement UI utilities

- [X] **Add Input Validation**
  - [X] Create src/main/java/com/belman/ui/validation/ directory
  - [X] Implement input validation

- [X] **Implement Error Boundaries**
  - [X] Create ErrorBoundaryView class in src/main/java/com/belman/ui/base/
  ```java
  public abstract class ErrorBoundaryView extends BaseView {
      protected void handleError(Throwable error) {
          // Error handling logic
      }

      protected abstract View renderFallback();
  }
  ```
  - [X] Update existing views to use ErrorBoundaryView

### 4. Update Navigation

- [X] **Update RoleBasedNavigationService**
  - [X] Update RoleBasedNavigationService to work with the new structure
  - [X] Update navigation paths in all views

### 5. Update Documentation

- [X] **Update Documentation**
  - [X] Update documentation to reflect the new structure
  - [X] Create diagrams showing the new structure

### 6. Testing

- [X] **Test the Application**
  - [X] Test all views to ensure they work with the new structure
  - [X] Test navigation to ensure it works with the new structure
  - [X] Test all use cases to ensure they work with the new structure

## Conclusion

Completing these tasks will fully implement the proposed UI structure, making the codebase more maintainable, scalable, and easier to understand. The new structure follows Gluon best practices by organizing the UI by business use case and promoting component reuse.
