# BelSign Photo Documentation Module

A modern SPA-style JavaFX + Gluon Mobile application for Belman A/S, developed as part of the 2nd semester exam project at EASV.

## Overview

BelSign is a quality control system used to trace and document welding and production steps.  
This module handles **photo documentation** tied to orders, with user role control, report generation, and email dispatch.

The system is developed using:

- Java 23
- JavaFX 21 (Gluon Mobile / Glisten UI)
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

## Goals

- User-friendly, tablet-optimized interface
- High reliability for critical industries (naval, nuclear, offshore)
- Complete traceability and auditability
- Future scalability (API integration, multi-user support)

---

© 2025 Belman A/S - Developed by EASV Students
