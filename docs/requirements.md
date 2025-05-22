# Belsign Photo Documentation System - Requirements

## Project Overview
Belsign is a quality control system for Belman A/S, a Danish company specializing in the design and manufacture of expansion joints and flexible pipe solutions. This document outlines the key goals and constraints for the photo documentation component of the Belsign system.

## Key Goals

### Functional Goals
1. **Photo Documentation Management**
   - Attach images to an order number and save them to a database
   - Organize photos in a structured way to replace the current system of storing in one big folder
   - Enable easy retrieval of images upon customer request or for quality issue investigation

2. **Quality Control**
   - Autogenerate QC (Quality Control) reports based on the photo documentation
   - Generate report previews for review before finalization

3. **Customer Communication**
   - Enable sending of emails with QC documentation directly to customers

4. **User Management**
   - Support multiple user roles with different permissions:
     - Production workers (Taking the pictures)
     - Quality assurance employees (Approving the documentation)
     - Administrators (Assigning roles to employees)

### Non-Functional Goals
1. **Usability**
   - Create a highly user-friendly interface for non-technical production workers
   - Design an intuitive workflow for adding pictures and following the documentation process
   - Implement a tablet-friendly interface

2. **Quality**
   - Ensure reliable storage and retrieval of photo documentation
   - Maintain high standards for QC report generation

## Constraints

### Technical Constraints
1. **Development**
   - Implement the system in Java as a desktop application
   - Use JavaFX for the GUI
   - Use MSSQL database for persistence
   - Follow 3-layered architecture
   - Implement appropriate design patterns
   - Include automated JUnit tests for core classes

2. **Design**
   - The system must be designed to be tablet-friendly (requirement)
   - The system should ultimately run on a tablet (not a strict requirement yet)

3. **Code Quality**
   - Source code must be readable and well-structured
   - Documentation must be comprehensive

### Project Constraints
1. **Methodology**
   - Follow the software development methodology taught during the 2nd semester
   - Document the development process
   - Use ScrumWise throughout the project

2. **Deliverables**
   - Produce a report (20-40 pages) documenting the project
   - Include installation guides and other necessary information for examiners
   - Submit the final report and software project by the deadline

## Success Criteria
The system will be considered successful if it:
1. Effectively replaces the current system of storing photos in one big folder
2. Makes it easy to find images upon customer request or for quality issues
3. Streamlines the QC documentation process
4. Is adopted by production workers without significant training or resistance
5. Meets all technical requirements and constraints