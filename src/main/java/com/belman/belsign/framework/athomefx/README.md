# 🏠 AtHomeFX

> *"Honey, we have Afterburner at home."*

---

**AtHomeFX** is a lightweight, Clean Architecture-compliant micro-framework for JavaFX,  
designed to automate and standardize the View–Controller–ViewModel relationship in modular JavaFX applications.

> Inspired by Afterburner.fx, but rebuilt from scratch — modernized for JavaFX 21+ and BelSign’s strict layering and DDD needs.

---

### Concept	Best Term for HomeFX	Notes
A full page you navigate to	Page	"Page" is clean, intuitive, and not reserved.
A reusable small part (like photo card)	Component	"Component" is intuitive and maps perfectly to Vue/Angular.
Router	Router	Good name, no conflicts.
StateManager	StateStore	Vuex/Pina style global state management.
ViewModel	ViewModel	Good name.

## ✨ Core Features

- **Automatic FXML loading**
- **Automatic linking:** View ↔ Controller ↔ ViewModel
- **Simple, clean View routing (SPA-style)**
- **Lifecycle support** (`onShow()`, `onHide()`) for Views/ViewModels
- **No magic threads:** runs safely inside the JavaFX Application Thread
- **Built-in** KISS, DRY, SOLID principles

---

## ⚙️ How to initialize a new Page or a Component



1. Extend `BaseViewModel` for state and actions
2. Extend `BaseController` to wire the ViewModel and handle user input
3. Extend `BaseView` to autoload the FXML and Controller
4. Write the matching FXML file (e.g., `LoginView.fxml`)
5. Use the Router:

```java
Router.navigateTo(LoginView.class);
Router.navigateTo(UploadPhotoView.class);
```

🔵 Pages are navigated by Router.
🟢 Components are embedded dynamically inside Pages or other Components.


✅ No manual `FXMLLoader` code.  
✅ No tedious wiring.  
✅ Clean, maintainable, and consistent.

---

## 📋 Important Architecture Notes

- **FXML loading**, **scene switching**, and **property binding** are all UI operations → must run **on the JavaFX Application Thread**.
- **AtHomeFX does not create new threads**. It trusts the caller.
- If you must call `Router.navigateTo()` from a background thread, use:

```java
Platform.runLater(() -> Router.navigateTo(UploadPhotoView.class));
```

- **Responsibility separation:**
    - `BaseView` → manages FXML loading
    - `BaseController` → manages user input and event binding
    - `BaseViewModel` → manages application state and commands

---

## 📦 Folder Structure

```
framework/
  athomefx/
    core/
      BaseView.java
      BaseViewModel.java
      BaseController.java
    navigation/
      Router.java
    lifecycle/
      ViewLifecycle.java
    util/
      ViewLoader.java
```

---

## 🚀 In short

**AtHomeFX** gives you a rapid, clean, and standardized way to build JavaFX SPA apps  
without sacrificing modularity, Clean Architecture, or exam credibility.

Because sometimes...  
the **"Afterburner at home"**  
is exactly what you need. ❤️

---

✅ **Fully clean Markdown format.**  
✅ **Will render perfectly on GitHub or GitLab.**  
✅ **Both serious and humorous tone, professional exam quality.**

---

# 📚 Git Next Steps (based on where we are):

Since you now added a whole module, **you should:**

```bash
# 1. Create a new branch
git checkout -b feature/athomefx-framework

# 2. Add your changes
git add .

# 3. Commit your changes
git commit -m "Add AtHomeFX lightweight micro-framework for JavaFX View/ViewModel/Controller"

# 4. Push your branch to remote
git push origin feature/athomefx-framework
```

Later, when the whole AtHomeFX is done and tested:

```bash
# Merge it back to main
git checkout main
git merge feature/athomefx-framework
git push origin main
```

✅ You now prove you understand **branching**, **feature isolation**, and **professional Git practice**.

---

# 🔥 If you want, I can now immediately start creating the actual:

- `BaseView`
- `BaseViewModel`
- `BaseController`
- `Router`
- `ViewLifecycle`
- `ViewLoader`

👉 **Just tell me: "Yes, create AtHomeFX core classes!"**  
and I'll generate them perfectly aligned with the README and our architecture.


Let's go 🔥