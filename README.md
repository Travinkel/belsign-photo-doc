# BelSign Photo Documentation Module

A modern SPA-style JavaFX + Gluon Mobile application for Belman A/S, developed as part of the 2nd semester exam project at EASV.

## Overview

BelSign is a quality control system used to trace and document welding and production steps.  
This module handles **photo documentation** tied to orderAggregates, with user role control, reportAggregate generation, and email dispatch.

### Company Background

Belman A/S is a Danish company specializing in the design and manufacture of expansion joints and flexible pipe solutions for various industries, including energy, offshore, chemical, and power generation. Founded in 1994 and headquartered in Esbjerg, Denmark, Belman provides custom-engineered solutions to accommodate thermal movements, vibrations, and pressure loads in piping systems.

### Problem Statement

Before shipping items from production, Belman needs to document joints, weldings, etc. through photo documentation. Currently, they take photos with a camera and upload them to a server in one big folder, making it difficult to find images upon customer requests or in case of quality issues.

### Solution Requirements

- Attach images to an orderAggregate number and save them to a database
- Autogenerate QC reports
- Enable sending of emails with QC documentation directly to customers
- User-friendly GUI for non-technical production workers
- Tablet-friendly design
- Support for multiple user roles (Production worker, Quality assurance, Admin)

The system is developed using:

- Java 23
- JavaFX 21 (Gluon Mobile / Glisten UI)
- Backbone (a Gluon Mobile-friendly, opinionated JavaFX micro-framework)
- Maven
- MSSQL database
- Apache PDFBox (for PDF reports)
- JavaMail (for sending documentation)
- Clean Architecture and Domain-Driven Design (DDD)

## Architecture

We follow strict software design principles:

- Three-Layer Architecture (Presentation, BLL, DAL)
- Clean Architecture (Dependency Inversion)
- Domain-Driven Design (Rich models, Aggregates, Value Objects)
- MVC + ViewModel (MVVMC) in GUI
- SPA Navigation using a custom Router
- Mock Repositories (for development and testing)

### Branch Strategy

| Branch | Purpose |
|:-------|:--------|
| `main` | Only release-ready stable versions |
| `develop` | Integration of feature branches |
| `feature/domain-layer` | Domain modeling and rich entity design |
| `feature/vertical-slice-uploadphoto` | First full feature slice |
| `feature/orm-querybuilder` | ORM and LINQ-like fluent querying |

## Development Process

- **SCRUM / Agile** methodology
- Daily commits and Pull Requests
- Pull Request templates and code reviews
- Unit Testing with JUnit
- Mock Repositories to enable test-driven development
- Prepared for future Gluon iOS/Android deployment

## Sprint Backlog

We have identified the following major epics and user stories from the project requirements. Each story is estimated in story points (Fibonacci scale) for relative effort:

### Epic: User Authentication & Splash Screen
- *As any user, I want to log in with secure credentials so that I can access the system.* (2 points)
- *As a user, I want a logout function so I can end my session securely.* (1 point)

### Epic: Upload Photos (Production Worker)
- *As a Production Worker, I want to take pictures directly from the tablet* (5 pts)
- *As a Production Worker, I want to select or enter the orderAggregate number before upload* (2 pts)
- *As a Production Worker, I want to upload multiple pictures at once* (3 pts)
- *As a Production Worker, I want to see a list of my uploaded images for the current orderAggregate* (3 pts)
- *As a Production Worker, I want to delete wrongly uploaded images* (3 pts)

### Epic: Approve Photos (QA)
- *As a QA Engineer, I want to view images attached to each orderAggregate* (2 pts)
- *As a QA Engineer, I want to approve or reject uploaded images* (3 pts)
- *As a QA Engineer, I want to add comments to image documentation* (1 pt)
- *As a QA Engineer, I want to generate a preview of the QC reportAggregate* (2 pts)

### Epic: Admin Management
- *As an Admin, I want to create and delete user accounts* (2 pts)
- *As an Admin, I want to assign roles to users* (2 pts)
- *As an Admin, I want to reset user passwords* (2 pts)

### Epic: System – Reports & Storage
- *As the System, I want to save images and metadata in a database linked to the orderAggregate number* (8 pts)
- *As the System, I want to auto-generate QC reports from approved images* (8 pts)
- *As the System, I want to send QC reports to customers via email* (3 pts)

### Key Acceptance Criteria
- **Upload Pictures:** Production Worker can take photos, select orderAggregate number, and upload images with metadata.
- **Approve/Reject Images:** QA can review, approve/reject images, and add comments.
- **Generate QC Report:** System produces formatted PDF with approved images and comments.
- **User Management:** Admin can create users with appropriate roles and permissions.

## Goals

- User-friendly, tablet-optimized interface
- High reliability for critical industries (naval, nuclear, offshore)
- Complete traceability and auditability
- Future scalability (API integration, multi-user support)

## Developer Guidelines

Detailed documentation for developers is available in the `docs/project` directory:

- [Project Overview](docs/project/guidelines/01_project-overview.md)
- [Architecture](docs/project/guidelines/02_architecture.md)
- [Frameworks and Libraries](docs/project/guidelines/03_frameworks-and-libraries.md)
- [Design Guidelines](docs/project/guidelines/04_design-guidelines.md)
- [Testing Guidelines](docs/project/guidelines/05_testing-guidelines.md)
- [Build and Run Instructions](docs/project/guidelines/06_build-and-run.md)
- [Mobile Compatibility](docs/project/guidelines/07_mobile-compatibility.md)

---

© 2025 Belman A/S - Developed by EASV Students
