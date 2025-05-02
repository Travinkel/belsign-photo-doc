# 2. Pre-Game / Background

## 2.1 Project Organization

This project is developed by Stefan and Lucas, in close collaboration with Belman A/S. Scrum methodology is followed, with roles adapted to a solo developer setup.

### **Scrum Roles**

- **Product Owner (PO):** [John Doe/Belman Representative], representing Belman’s QA/Product Management, defines priorities and acceptance criteria based on business value. The PO clarifies requirements (e.g. safety standards for weld inspections) and approves completed features.
- **Scrum Master (SM):** [Stefan Ankersø/Developer] facilitates the Scrum process, removes impediments, and coaches the team on agile practices. The SM ensures ceremonies happen and shields the team from distractions.
- **Development Team:** A cross-functional team of developers and testers (including UI/JavaFX developers, a backend/database developer, and a QA engineer). The team is empowered to decide how best to implement features (using the SPA JavaFX app and DDD architecture). They collectively own sprint commitments.
- **Stakeholders:** In addition to PO, stakeholders include the QA team lead, production supervisor, and an IT/DevOps representative. They provide domain knowledge and feedback during reviews.

### **Collaboration Tools:**

- **Version Control**: Git + GitHub
- **Time Boxing:** I use Productivity Challenge Timer on my Android device to timebox time spent on the project
- **Project Management**: Scrumwise (user stories, sprint planning, retrospectives)
- **IDE**: IntelliJ Ultimate + Liberica JDK 23
- **Database**: Microsoft SQL Server (via VPN).

### GitHub Repository Setup and Project Organization

The project repository was structured following professional Git Flow principles:

- `main` branch only contains stable, release-ready versions.
- `develop` branch aggregates all feature branches before merging into `main`.
- Each new functionality or refactoring effort is developed in isolated `feature/*` branches.
- Pull Requests are used for code review and quality control.
- A [README.md](http://readme.md/) file was added to describe the project purpose, architecture, development process, and technical stack.
- Pull Request templates and commit message guidelines were introduced to maintain consistency.

This approach ensures that the system is maintainable, scalable, and ready for integration into Belman's full BelSign system.

By working modularly and following Clean Architecture principles, we ensure the product is stable enough to be deployed on tablets, smartphones, and desktops without needing major restructuring.

### **Working Agreement:**

- **Communication:** The team holds a short Daily Scrum every weekday at 9am (physical or video) to synchronize. Key tools include Discord for quick messages, student email for formal communication, and Confluence for documentation. All backlog items and tasks are tracked in ScrumWise. Decisions and notes are documented promptly.
- **Collaboration:** We follow agile values of openness and respect. Code is shared via Git with mandatory code reviews (pair programming encouraged). Task ownership is flexible – any team member can pick up high-priority items.
- **Meetings:** We have Sprint Planning at the start of each sprint, a mid-sprint backlog grooming session, a Sprint Review to demo functionality to stakeholders, and a Retrospective to improve team processes.
- **Team Norms:** We agree to meet commitments and update estimates transparently. If impediments arise, we flag them immediately. Quality is a priority: we write automated tests for critical features. We respect work hours but remain responsive for urgent issues.

### Definition of Done (DoD)

- **Code:** Feature code is implemented, peer-reviewed, and merged into the main branch.
- **Testing:** All automated unit/integration tests pass. Critical paths are covered by tests. Manual QA (if needed) is performed for this story.
- **Deployment:** The increment is deployed to the development environment, and a demo build is verified.
- **Documentation:** Relevant documentation (user guide, release notes, updated backlog items) is complete.
- **Approval:** The Product Owner has reviewed and accepted the story during Sprint Review, confirming all acceptance criteria are met. The feature is potentially shippable.

### Agile/Scrum Process Summary

We follow Scrum with **two-week sprints**. Each sprint begins with Planning, where we define the Sprint Goal and pull the highest-priority user stories into the Sprint Backlog. The team holds a 15-minute **Daily Scrum** each morning to synchronize work and raise impediments. Work tasks are self-assigned from the sprint backlog in ScrumWise and updated in real time. At sprint’s end, we conduct a **Sprint Review** to demo new features (e.g. photo upload) to stakeholders for feedback. A **Sprint Retrospective** follows to improve our process (e.g. tool enhancements, communication). Roles are respected: the Product Owner prioritizes needs and clarifies requirements, the Scrum Master facilitates the process, and the cross-functional team delivers increments. We emphasize the Agile principles of collaboration, adaptability, and delivering working software, all within Belman’s domain of safety-critical steel production and a strong technical foundation (DDD architecture and JavaFX SPA) to meet the users’ real-world needs.

## 2.2 Overall Project Schedule

| **Timeframe** | **Milestone** |
| --- | --- |
| **April 2025** | Sprint 1: System scaffolding, database connectivity, basic image upload |
| **May 2025** | Sprint 2: Role-based workflows, report generation, email integration |
| **Late May** | Final refinements, user testing, report writing |
| **June 2nd** | Final submission of software + technical documentation |

## 2.3 Initial Product Backlog

We have identified the following major epics and user stories from the project requirements. Each story is estimated in story points (Fibonacci scale) for relative effort. The backlog is prioritized by business value:

- **Epic: User Authentication & Splash Screen**
    - *As any user, I want to log in with secure credentials so that I can access the system.* (2 points)
    - *As a user, I want a logout function so I can end my session securely.* (1 point)
- **Epic: Upload Photos (Production Worker)**
    - *As a Production Worker, I want to take pictures directly from the tablet* (5 pts) – connects the tablet camera to the app.
    - *As a Production Worker, I want to select or enter the order number before upload* (2 pts) – ensures photos are linked to the correct order.
    - *As a Production Worker, I want to upload multiple pictures at once* (3 pts) – batch upload efficiency.
    - *As a Production Worker, I want to see a list of my uploaded images for the current order* (3 pts) – a confirmation view.
    - *As a Production Worker, I want to delete wrongly uploaded images* (3 pts) – correct mistakes before QA review.
- **Epic: Approve Photos (QA)**
    - *As a QA Engineer, I want to view images attached to each order* (2 pts) – browse and inspect uploads.
    - *As a QA Engineer, I want to approve or reject uploaded images* (3 pts) – to validate weld quality.
    - *As a QA Engineer, I want to add comments to image documentation* (1 pt) – annotate issues or approvals.
    - *As a QA Engineer, I want to generate a preview of the QC report* (2 pts) – see the final report format before sending.
- **Epic: Admin Management**
    - *As an Admin, I want to create and delete user accounts* (2 pts) – manage system access.
    - *As an Admin, I want to assign roles to users* (2 pts) – control permissions (e.g. QA vs Operator).
    - *As an Admin, I want to reset user passwords* (2 pts) – handle account recovery.
- **Epic: System – Reports & Storage**
    - *As the System, I want to save images and metadata in a database linked to the order number* (8 pts) – core data storage.
    - *As the System, I want to auto-generate QC reports from approved images* (8 pts) – combine images/comments into a PDF.
    - *As the System, I want to send QC reports to customers via email* (3 pts) – automatic report distribution.

### Acceptance Criteria (Examples)

- **Story: Upload Pictures (Production Worker):** Given a logged-in Production Worker on a tablet, when they activate the camera and take photos of an expansion joint, *then* the app should allow selecting or entering an order number and uploading all images. *And* each uploaded image is stored with its metadata (timestamp, user ID, order number)file-s5auxkkhgo6pcbnbuf98dd.
- **Story: Approve/Reject Images (QA):** Given a logged-in QA user viewing an order’s gallery, when they approve or reject an image, *then* the system records this decision and updates the image status. Comments can be added, and the status (approved/rejected) is visible in the report previewfile-s5auxkkhgo6pcbnbuf98ddfile-s5auxkkhgo6pcbnbuf98dd.
- **Story: Generate QC Report (System):** Given a set of approved images and QA comments for an order, when a report is generated, *then* the system produces a formatted PDF containing all images and comments, ready to be sent to the customerfile-s5auxkkhgo6pcbnbuf98dd.
- **Story: Create User (Admin):** Given an Admin on the user management screen, when they create a new user with a role, *then* the user appears in the user list with the correct role assignment. The new user can log in with credentials, and their role controls their access level

### Burndown Chart Tracking Plan

During each sprint, we update the burndown chart daily by subtracting completed story points from the total sprint effort. The burndown shows the remaining work (vertical axis) versus time (horizontal axis). We compare the actual progress line to the ideal work-burn line to identify any deviations. For example, if tasks take longer or blockers arise, the curve will lag and prompt a conversation in the Daily Scrum. For the exam, we plan to present a final burndown chart illustrating the expected trajectory and actual progress for Sprint 1. This chart (automatically generated by ScrumWise or manually) will clearly show how work was completed over time, validating the team’s planning and execution.

**Sources:** Project and requirements details are based on Belman’s Photo Documentation brieffile-s5auxkkhgo6pcbnbuf98ddfile-s5auxkkhgo6pcbnbuf98dd and Agile/Scrum best practices. Each cited requirement is aligned with the official needs (e.g. traceability, roles, tablet use) specified in the project description

## 2.4 Architecture

The architecture for the BelSign Photo Documentation module is based on a combination of **Clean Architecture principles**, **MVC + VM (Model-View-Controller + ViewModel, known also as MVVM + C)**, and **SPA-style navigation** adapted for JavaFX and Gluon Mobile platforms. The system is designed to be modular, scalable, and optimized for tablet and desktop devices, following Belman's strict demands for traceability, reliability, and ease of use. Although Scrum is more of a process concern, the architecture can also support agile development by being modular and testable.

### 2.4.1 High-Level System Overview

The project follows the **Three-Layered Architecture** (GUI → BLL → DAL) principle taught at EASV, but the internal separation follows Clean Architecture by distinguishing between Application Services, Domain Models, and Infrastructure. It should not be layout concerns such as folder naming and folder structure that defines architectural layers, but rather dependency direction.

It has been further refined using **Domain-Driven Design (DDD)** practices:

- **Entities** such as `Order`, `User`, and `PhotoDocument` encapsulate domain rules and maintain consistency.
- **Value Objects** like `Username`, `OrderNumber`, and `PhotoAngle` ensure strong typing and validation.
- **Aggregates** like `Order` enforce consistency boundaries (an Order owns its PhotoDocuments).
- **Services** (e.g., `AuthService`, `QCReportService`) encapsulate use-case logic across aggregates.

**Dependency Direction.** Higher-level layers (Presentation/Application) depend on lower-level abstractions (Domain), not on Infrastructure. Infrastructure implements interfaces defined by the Application Layer. This layering isolates business rules from external technologies, ensuring flexibility and testability.

### 2.4.2 Mapping Three-Layered Architecture To Clean Architecture

The flow is from Presentation → Application → Domain → Infrastructure layers and the system follows a three-layer architecture:

---

| Layer | Presentation Layer (View + Controller + ViewModel) | Responsibilities | Technologies Used |
| --- | --- | --- | --- |
| GUI Layer (Presentation) | **Presentation Layer (View + Controller + ViewModel)** | Handles user interface (Views and ViewModels) and user interactions. Views are FXML-based with separate ViewModels to manage UI state and commands. Navigation between views is dynamic (SPA style) using a custom Router | JavaFX 21, FXML, Gluon Glisten |
| Business Logic Layer (BLL) | **Application Layer**   | Contains services implementing business use-cases like authentication, photo uploading, QC report generation, and emailing. ViewModels interact with this layer through clearly defined service interfaces. | Plain Java services |
| Business Logic Layer (BLL) | **Domain Layer** | Defines the core business entities (User, Order, PhotoDocument, QCReport) and their rules. This layer is pure Java and independent of external frameworks. | Java 23 (Records, Sealed Classes, etc.) |
| Data Access Layer (DAL) | **Infrastructure Layer** | Implements technical details like database persistence (MSSQL), PDF generation (Apache PDFBox), and email sending. Also contains the Router for navigation control. | MSSQL, JDBC, PDFBox, JavaMail, Gluon Attach modules
**Note**: This depends on the Application layer, not the other way around (Dependency Inversion). |

### 2.4.3 Layer Reponsibilities

### **GUI Layer (JavaFX) →**

- **Purely responsible** for user interaction.
- Follows **MVC + VM (MVVMC)** within the GUI:
    - `FXML View` + `Controller` + `ViewModel`.
- **ViewModels** expose observable properties and commands to the UI.
- **Controllers** orchestrate user interactions and delegate to Application Services.
- **GUI depends only on the Application Layer**, never directly on Domain or Infrastructure.
- **Never contains business logic**: only captures, presents, and sends user intents.

### **Business Logic Layer (BLL) → Application + Domain Layer**

- **Application Layer**:
    - Coordinates domain entities and infrastructure services to realize business use cases.
    - Encapsulates technical workflows that do not belong in the pure domain model.
    - Examples: `ImageUploadService`, `ApprovalWorkflowService`, `ReportGenerationService`.
    - **QCReport** and **ReportBuilder** live here, because they **orchestrate outputs** (reports) without being part of core domain logic.
- **Domain Layer**:
    - Models the **real business world** of Belman.
    - Contains **Entities** (`Order`, `PhotoDocument`, `User`), **Value Objects** (`OrderNumber`, `Timestamp`, etc.), and **Domain Services** (if pure domain behavior is needed).
    - **Strictly independent** of infrastructure concerns (DB, GUI, files, reports).

### **Data Access Layer (DAL) → Infrastructure Layer**

- Provides **technical implementations** for persistence, file storage, emailing, and navigation.
- Contains **Repositories** that interact with the MSSQL database using the DAO pattern.
- Stores image files externally and saves metadata into SQL.
- Uses **Mappers and Assemblers** to transform database rows into Domain Entities and vice versa.
- **Depends on interfaces** declared in the Application Layer.
- Infrastructure **never contains business logic** — only technical concerns.

### Flow Diagram

```java
[View (FXML)]
		⇅
[Controller] → Calls →
		⇅
[ViewModel] → Calls →
		⇅
[Application Service] → Coordinates →
		⇅
[Domain Entities] → Saved/Retrieved via →
		⇅
[Repository (Infrastructure)]
```

### 2.4.4 Navigation Strategy (SPA-like)

The application uses a single JavaFX `Scene`, with dynamic view injection managed by a `Router` class. Screens like SplashView, LoginView, UploadPhotoView, and ApprovePhotoView are loaded as FXML nodes and switched inside the root scene dynamically, without recreating the Scene.

Each ViewModel implements lifecycle hooks (`onShow()`, `onHide()`) to manage state transitions cleanly.

---

### 2.4.5 Lifecycle and State Management

A global `UserSession` singleton manages the logged-in user's information, current working order, and permissions.

Views and ViewModels can query the `UserSession` to determine access rights and personalize the UI.

Each ViewModel is responsible for:

- Preparing its data when `onShow()` is triggered (e.g., loading photos for an Order).
- Releasing resources if needed on `onHide()`.

---

### 2.4.6 Scalability and Future Proofing

The architecture anticipates future extensions, including:

- Integration of REST APIs for remote database access.
- Multi-user concurrency support.
- New modules (e.g., weld approval flows, audit logging).
- Deployment to iPads, smartphones, and Windows tablets through Gluon Mobile.

Because of strict layering and DDD-inspired modeling, adding new features like a "Weld Approval History" module will be straightforward without major refactoring.

### 2.4.7 Domain-Driven Design In Practice

This system applies **Domain-Driven Design (DDD)** to model the core business logic in a way that reflects the real-world workflow of Belman’s production and quality control processes. The domain layer is designed to be rich in behavior, cohesive, and independent of infrastructure or presentation frameworks.

In the spirit of Domain-Driven Design, the quality control component of Belsign represents a **Core Domain** for Belman A/S. It directly impacts production workflows, customer communication, and documentation integrity. This area demands rich modeling and precise business rules. Other potential subdomains (such as report formatting or authentication) may be considered **supporting** or **generic**, but the quality validation and image lifecycle form the **heart of the business logic**.

### Entities

Entities have a unique identity and lifecycle. They encapsulate behavior and domain rules:

| Entity | Description |
| --- | --- |
| `Order` | Root of the image approval aggregate. Contains metadata and QCImages. |
| `QCImage` | Attached to an order. Contains timestamp, storage reference, and status. |
| `User` | Represents a system user with a defined role: `PRODUCTION`, `QA`, `ADMIN`. |
| `Report` | Generated from an order. May be previewed or emailed to the customer. |
| `Approval` | Tracks a QA employee’s decision on a QC image or report. |

### Value Objects

Value Objects are immutable types that model fine-grained concepts with no identity:

- `EmailAddress` – ensures valid format and encapsulates logic like domain validation.
- `ImagePath` – wraps image file system location or DB reference.
- `Timestamp` – represents creation or approval time with no identity of its own.

---

### Aggregates and Aggregate Roots

An **Aggregate** is a cluster of domain objects treated as a unit. The **Aggregate Root** ensures consistency and enforces invariants.

- The `Order` entity is the **Aggregate Root**.
- All interactions with `QCImage` go through the `Order` entity.
- Invariants like “max image count” or “cannot approve an image twice” are enforced at the root level.

> This ensures transactional integrity and encapsulates the rules for how images relate to orders.
>

---

### Domain Services

Some business logic doesn’t belong to a single entity. Instead, it operates across multiple entities or value objects.

- `ImageValidationService` – applies rules like max image size, supported formats, or corruption checks.
- `ReportBuilderService` – aggregates order + image data into a structured report.

These are **stateless** and used by application services to execute workflows.

---

### Repositories

Repositories abstract persistence logic and return full aggregates:

- `OrderRepository` – retrieves and stores `Order` aggregates, including their images.
- `UserRepository` – fetches user data by username or role.

The domain layer **depends only on interfaces**. Implementations are defined in the infrastructure layer.

---

### Mapping and DTO Use

To bridge the domain with the GUI or persistence, **DTOs** and **Mappers** are used:

- `OrderDTO`, `ImageDTO`, `UserDTO`
- `DomainToDTOMapper` handles conversions from and to domain types.

> This avoids exposing domain internals to the outside world and ensures flexibility if GUI or storage needs change.
>

---

### Application Services

Application services **coordinate domain logic** and act as a bridge between the GUI and the domain model. They handle use cases but **do not contain domain rules** — they delegate to domain entities or services.

| Service | Responsibility |
| --- | --- |
| `ImageUploadService` | Accepts image input, validates, and stores it via the `Order` aggregate and repository. |
| `ApprovalWorkflowService` | Handles QA approval/rejection of images and maintains audit trails. |
| `ReportGenerationService` | Delegates to `ReportBuilderService` and optionally sends the result via email. |
| `AuthService` | Validates credentials and returns authenticated user + role. |

These services depend on interfaces and are injected via dependency injection, allowing for easy mocking and testing.

---

### ViewModel as a DDD Boundary

To preserve separation of concerns, the **ViewModel** acts as a boundary object between the GUI and the application layer.

- It binds observable properties (`StringProperty`, `IntegerProperty`, `ObservableList`) for JavaFX controls.
- It transforms `DTO`s into GUI-bound objects and vice versa.
- It exposes **commands** (like `submitImage()`, `generateReport()`) which internally call Application Services.

> Example: OrderViewModel holds data for order ID, customer name, image list, and approval state. It communicates with ImageUploadService without accessing domain logic directly.
>

---

### Role-Based Access Control (RBAC)

Authorization logic is handled in the **Application Layer**, not the GUI or domain layer.

Roles:

- `PRODUCTION`: Upload images only
- `QA`: Review, approve, reject, generate reports
- `ADMIN`: Manage users and assign roles

Each service method checks the **user’s role** before proceeding. This centralizes permission logic and simplifies the UI.

---

### Infrastructure Details (Handled Outside Domain)

To keep the domain model pure, infrastructure logic is handled in the outermost layer:

- `SqlOrderRepository`, `SqlUserRepository`: JDBC-based persistence
- `ImageStorageService`: Stores image binaries to filesystem or DB, injected via interface
- `EmailSender`: Sends PDF reports via SMTP or secure mail API

The domain and application layers **never know** about SQL, SMTP, or the file system.

### Sequence Diagram

```
sequenceDiagram
participant User
participant GUI as GUI Layer (View + Controller + ViewModel)
participant App as Application Layer (ImageUploadService)
participant Domain as Domain Layer (Order Aggregate)
participant Repo as Infrastructure (OrderRepository)
User ->> GUI: Selects Order and Uploads Photo
GUI ->> App: submitPhoto(orderId, photoFile)
App ->> Domain: loadOrder(orderId)
Domain ->> App: returns Order Aggregate
App ->> Domain: addPhoto(photoFile)
App ->> Repo: saveOrder(updated Order with Photo)
Repo -->> App: Confirmation
App -->> GUI: Upload successful message
GUI -->> User: Show success notification
```

### 2.4.8 Technology Stack

### Clean Architecture And DDD

| Area | Technology |
| --- | --- |
| GUI Framework | JavaFX 21 + Gluon Mobile (Glisten UI components) |
| Backend Persistence | MSSQL (Microsoft SQL Server) |
| Dependency Management | Maven |
| Build Tool for Mobile | GluonFX Maven Plugin |
| PDF Generation | Apache PDFBox |
| Email Sending | JavaMail API |
| Java Version | Java 23 (Liberica JDK) |
| UI Responsiveness | Glisten responsive layouts, touch-friendly components |

| Clean Architecture Concept | My Implementation |
| --- | --- |
| Entity | Order, QCImage, User, Report |
| Value Object | EmailAddress, Timestamp, FilePath |
| Factory | ReportFactory (creates Report from QCImages), has optional arguments |
| Domain Service | ImageValidation, ReportBuilder |
| Application Service | ImageUploadService, ApprovalWorkflowService |
| Repository Interface | OrderRepository, ImageRepository |
| Repository Implementation | SqlOrderRepository, FileImageRepository |
| Mapper / Assembler | OrderMapper, ImageAssembler |
| DTO | OrderDTO, ImageDTO |
| ViewModel | OrderViewModel, LoginViewModel |
| Controller | JavaFX controller classes |

**Note on Aggregates:**

In accordance with Domain-Driven Design, some entities (e.g., Order) act as **Aggregate Roots**, controlling consistency within their boundaries. For instance, Order encapsulates a list of QCImages and governs how and when they are added. All changes to images must pass through the root entity, maintaining business rule integrity and transactional consistency.

Having established the architecture, we now describe the design patterns used to implement it.

### 2.4.9 Design Patterns and Best Practices

### Patterns and Principles

- **MVC Pattern** in GUI
- **VM:** Added to assist in data presentation and data binding
- **DDD**: Entities, Value Objects, Aggregates, Repositories
- **SRP & SOLID** for all classes
- **KISS**
- **YAGNI**
- **Effective Java**: Use best practices for constructors, immutability, exceptions, etc.
- **Law of Demeter**:"Don't talk to strangers" – reduce coupling.

### MVC + VM → MVVMC Pattern

To better facilitate the architecture and flow between layers, I have opted to refine the MVC Pattern by adding a ViewModel. So while the overall architecture is Clean Architecture with a 3-layer separation, the GUI follows a hybrid of MVC and MVVM patterns, using Controllers to delegate actions and ViewModels to support JavaFX bindings.

To avoid exposing domain internals to the GUI, the ViewModel prepares data for display and binds to observable UI controls. I split concerns more cleanly by doing this, especially since I am using rich models, that contain logic, and not anemic models that are procedural programming disguised as OOP. The ViewModel usage keeps UI decoupled from domain logic. Leaning into rich models better supports Clean Code principles.

To summarize it as follows:

> **MVC handles userflow,**
>

> **ViewModel handles dataflow,**
>

> **DTOs handle system boundary flows**
>

Therefore MVC organizes the GUI layer. **ViewModel** decouples GUI from logic and supports **JavaFX data binding.** It is necessary to extend the MVC pattern for Clean Architecture concerns.

| MVVMC Role | DDD Layer | Responsibility |
| --- | --- | --- |
| **Model** | **Domain Layer** | Entities, Value Objects, Aggregates, Services (business logic only) → business entities |
| **View** | **GUI Layer** | JavaFX interface (tablet-friendly), only reflects user interactions → FXML layout + UI controls (JavaFX Scene) |
| ViewModel | **GUI Layer** | Exposes observable properties for binding, wraps DTO/domain where needed |
| **Controller** | **GUI Layer** | Handles button clicks, field input, calls methods on ViewModel → orchestrates logic and delegates to domain & persistence |

### The ViewModel And Why It Was Added

The Model (Entity) should contain business logic, not UI binding logic. In practice this means that the **Domain Model** represents business rules and real-world concepts (e.g., `Order`, `QCImage`, `User`) and importantly that it should work **regardless of UI framework**, database, or runtime environment. If I put StringProperty into my Order entity then I pollute my domain with GUI tech/code and I couple my model to JavaFX, meaning it is now completely useless in a rest API, test runner or a CLI. In Clean Code, Domain Objects should be pure Java, no framework dependency.

### Singleton and Factory Pattern

Inspired by *Effective Java*, the configuration manager and services follow the Singleton or Factory pattern where appropriate, using lazy initialization to ensure efficiency and control over resource access.

### Dependency Injection Framework

Dependency Injection is used throughout to decouple components, improve testability, and follow the Dependency Inversion Principle — especially when injecting repositories and services into controllers or view models.

### Best Practices to Reinforce

- **Avoid placing business logic in Controllers or Repositories**. Instead, encapsulate it in Services or Entities.
- **Domain-driven design precedes the database schema** — not the other way around.
- **Domain models are not database models**. They represent behavior, not just structure.
- **Follow the Law of Demeter**: Don’t call `.getX().getY().getZ()`. Keep interactions clean and directed.
- **Dependency Injection.** I use a lightweight, custom-built dependency injection framework tailored to the project scope. While not as robust as Spring or Guice, it has been tested and is sufficient for this context.

---

### 2.4.10 Architecture Diagrams

![image.png](attachment:340e70d3-319c-45ca-814a-b94b9d98754f:image.png)

- The GUI depends only on Application Services, never on Domain or Infrastructure directly.
- Displays results/data from domain objects (DTOs or view models).
- Sends commands or requests to the **Application Layer** via a JavaFX controller class (in the GUI layer), which delegates to application services
- Follows MVC within the GUI: `FXML View` + `Controller` + ViewModel.

```
[GUI Layer (JavaFX View + Controller + ViewModel)]
											↓
[Application Layer (Services, QCReport, ReportBuilder)]
											↓
[Domain Layer (Entities, Value Objects, Domain Services)]
											↓
[Infrastructure Layer (Repositories, Database, Storage, Email)]
```

### Component Diagram

```
flowchart TD
```

```
SplashView[SplashView Component]
LoginView[LoginView Component]
MainView[MainView Component]
UploadPhotoView[UploadPhotoView Component]
ApprovePhotoView[ApprovePhotoView Component]
AdminView[AdminView Component]

Router[Router Class]
UserSession[UserSession Singleton]

SplashView --> Router
LoginView --> Router
MainView --> Router
UploadPhotoView --> Router
ApprovePhotoView --> Router
AdminView --> Router

LoginView --> UserSession
UploadPhotoView --> UserSession
ApprovePhotoView --> UserSession
AdminView --> UserSession
```

NOTES:

Yes — in your actual BelSign application **(especially services)** you *might* need concurrency.

For example:

| Situation | Solution |
| --- | --- |
| Uploading large photo files | Background thread (`Task` or `Service` class) |
| Loading many Orders from database | Background thread |
| Sending emails | Background thread |
| Generating PDF reports | Background thread |

**But that's business logic / application logic**,

**not AtHomeFX internal responsibility.**

---

# 📋 Clear Rule:

| Layer | Threading Needed? | Notes |
| --- | --- | --- |
| AtHomeFX (View, Controller, ViewModel, Router) | ❌ No threading inside |  |
| Application Layer (Services like Upload, Email) | ✅ Yes, background threads where needed |  |
| Domain Layer (Entities, ValueObjects) | ❌ No threading inside |  |
| Infrastructure Layer (Repositories) | ✅ Possibly async DB access or file IO |  |

# 1. **Threading and Concurrency in the BelSign project (overall)**

| Context | Need concurrency? | Reason |
| --- | --- | --- |
| **UI Layer** (Views, ViewModels, Router) | ❌ No | Must always work *only* on the JavaFX Application Thread (JavaFX requirement) |
| **Application Layer** (Services) | ✅ Yes | If you do heavy tasks (upload photo, send email, generate PDF), you must use background threads |
| **Domain Layer** | ❌ No | Domain Objects (Entities, ValueObjects) must be pure and simple — no threading, no side-effects |
| **Infrastructure Layer** (Repositories) | ✅ Optional | For example, database queries could be async if performance needed, but at school-level: **not required**. |

**Main rule for your project:**

> Only the Application Layer will create JavaFX Task objects (background threads) where necessary.
>

---

# **Examples of where concurrency is needed:**

| Operation | How to handle it |
| --- | --- |
| Uploading a large photo file | JavaFX `Task` in `ImageUploadService` |
| Generating a QC report (PDF) | JavaFX `Task` in `ReportGenerationService` |
| Sending an email with attachment | JavaFX `Task` in `EmailService` |
| Loading lots of orders | JavaFX `Task` in `OrderRepository` (optional) |

**Thread safety best practice:**

- **Always** update the UI via `Platform.runLater()` if you need to after a background thread finishes.
- **Never** touch JavaFX UI objects directly from a background thread.

---

# ⚡ **Concurrency management plan for BelSign**

- Only Services spawn background Tasks.
- Services expose callbacks: **onSuccess()**, **onFailure()**.
- ViewModel listens and updates observable properties.
- Controller refreshes UI via binding.

# 2. **Modern Java (Java 23) Functional Interfaces (Predicate, Consumer, Callback)**

**These are extremely useful — and we absolutely should use them!**

| Interface | Meaning | Example usage in BelSign |
| --- | --- | --- |
| `Predicate<T>` | Function that takes a T and returns true/false | Check if user has permission (`Predicate<User>`) |
| `Consumer<T>` | Takes a T and does something (no return) | Register event listeners (`Consumer<DomainEvent>`) |
| `Function<T,R>` | Takes a T and returns R | Map Order to DTO (`Function<Order, OrderDTO>`) |
| `Callback<P,R>` | More general, like Function but often used for async | Provide callbacks for success/failure of upload |

---

## 📋 Practical ways we'll use them:

| Where | Which Functional Interface | Example |
| --- | --- | --- |
| DomainEventPublisher | `Consumer<DomainEvent>` | `registerListener(Consumer<DomainEvent>)` |
| UploadPhotoService | `Callback<UploadResult>` | Call `onSuccess(result)` or `onError(exception)` |
| UserSession | `Predicate<User>` | Check if user can approve (`user -> user.getRole() == Role.QA`) |
| DTO mapping | `Function<Order, OrderDTO>` | Transform Domain models into GUI-ready DTOs |

---

#