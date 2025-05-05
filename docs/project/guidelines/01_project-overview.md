# Project Overview: BelSign Photo Documentation Module

## Introduction

BelSign is a quality control system developed for Belman A/S, a Danish company specializing in the design and
manufacture of expansion joints and flexible pipe solutions. This module specifically handles photo documentation tied
to orders, with user role control, report generation, and email dispatch capabilities.

## Company Background

Belman A/S was founded in 1994 and is headquartered in Esbjerg, Denmark. The company provides custom-engineered
solutions to accommodate thermal movements, vibrations, and pressure loads in piping systems for various industries,
including:

- Energy
- Offshore
- Chemical
- Power generation

## Problem Statement

Before shipping items from production, Belman needs to document joints, weldings, and other components through photo
documentation. The current process involves:

1. Taking photos with a camera
2. Uploading them to a server in one large folder

This approach makes it difficult to:

- Find specific images upon customer requests
- Access documentation in case of quality issues
- Associate images with specific orders
- Generate quality control reports efficiently
- Time-consuming searches for specific images.
- Inefficient handling of quality documentation.
- Limited association between images and specific orders.

## Solution: BelSign Photo Documentation Module

The BelSign Photo Documentation Module addresses these challenges by providing a structured system for managing photo
documentation throughout the production and quality control process.

### Key Features

- **Order-Based Photo Management**: Attach images to specific order numbers and save them to a database
- **QC Report Generation**: Automatically generate quality control reports with approved photos
- **Customer Communication**: Send emails with QC documentation directly to customers
- **User-Friendly Interface**: Designed for non-technical production workers
- **Tablet Compatibility**: Optimized for use on tablets in production environments
- **Role-Based Access Control**: Support for multiple user roles:
    - Production Worker: Upload and manage photos
    - Quality Assurance: Review and approve photos, generate reports
    - Admin: Manage user accounts and system settings

### Benefits

- **Improved Traceability**: All photos are linked to specific orders
- **Enhanced Quality Control**: Structured approval process for documentation
- **Better Customer Service**: Quick access to documentation when needed
- **Increased Efficiency**: Streamlined workflow for photo documentation
- **Reduced Errors**: Systematic approach to documentation management

## Target Users

The system is designed for three primary user roles:

1. **Production Workers**
    - Take and upload photos
    - Associate photos with order numbers
    - Review uploaded photos

2. **Quality Assurance Personnel**
    - Review photos uploaded by production workers
    - Approve or reject photos
    - Add comments to documentation
    - Generate QC reports

3. **Administrators**
    - Manage user accounts
    - Assign roles to users
    - Configure system settings

## Project Goals

- Create a user-friendly, tablet-optimized interface
- Ensure high reliability for critical industries (naval, nuclear, offshore)
- Provide complete traceability and auditability
- Build for future scalability (API integration, multi-user support)
- Here’s exactly where and how to add these extra details into `01_project-overview.md` clearly:

**Development Methodology**

- The project strictly follows Scrum methodology, including:
    - Regular Sprint Planning, Sprint Reviews, and Sprint Retrospectives.
    - Detailed Sprint Backlog management using ScrumWise.
    - Active participation from the Product Owner and teaching staff during sprint reviews, focusing on functionality
      and usability.

**Technical Implementation Requirements**

- Implemented as a Java desktop application, delivered as an IntelliJ project.
- JavaFX used for GUI development.
- Persistent storage handled by the school's MSSQL database.
- Mandatory demonstration of at least one automated JUnit test covering a core class.

**Project Documentation**

- Comprehensive documentation required, clearly detailing:
    - Project planning and execution.
    - Architectural decisions and rationale.
    - Implementation details, including code examples across all layers.
    - Testing strategy and outcomes.
    - Installation guides and credentials provided for examiners.

**Examination and Evaluation**

- Final assessment includes:
    - A group presentation (10 minutes) demonstrating overall system functionality.
    - An individual oral examination (30 minutes) focusing on the student's specific contributions and understanding of
      curriculum-related topics.