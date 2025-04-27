# AtHomeFX

*"Honey, we have Afterburner at home."*

AtHomeFX is a lightweight, Clean Architecture-compliant micro-framework for JavaFX,
designed to automate and standardize the View–Controller–ViewModel relationship in modular JavaFX applications.

Inspired by Afterburner.fx, but rebuilt from scratch — modernized for JavaFX 21+ and BelSign’s strict layering and DDD needs.

## Core Features

- Automatic FXML loading
- Automatic linking: View ↔ Controller ↔ ViewModel
- Simple, clean View routing (SPA-style)
- Lifecycle support (`onShow()`, `onHide()`) for Views/ViewModels
- No magic threads: runs safely inside the JavaFX Application Thread
- Built-in support for KISS, DRY, and SOLID principles

## How to Create a New Screen

1. Extend `BaseViewModel` for state and actions
2. Extend `BaseController` to wire the ViewModel and handle user input
3. Extend `BaseView` to autoload the FXML and Controller
4. Write the matching FXML file (e.g., `LoginView.fxml`)
5. Use the Router:

```java
Router.navigateTo(LoginView.class);
Router.navigateTo(UploadPhotoView.class);
