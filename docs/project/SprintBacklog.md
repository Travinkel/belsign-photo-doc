## Initial Product Backlog

We have identified the following major epics and user stories from the project requirements. Each story is estimated in story points (Fibonacci scale) for relative effort. The backlog is prioritized by business value:

- **Epic: User Authentication & Splash Screen**
    - *As any user, I want to log in with secure credentials so that I can access the system.* (2 points)
    - *As a user, I want a logout function so I can end my session securely.* (1 point)
- **Epic: Upload Photos (Production Worker)**
    - *As a Production Worker, I want to take pictures directly from the tablet* (5 pts) – connects the tablet camera to the app.
    - *As a Production Worker, I want to select or enter the orderAggregate number before upload* (2 pts) – ensures photos are linked to the correct orderAggregate.
    - *As a Production Worker, I want to upload multiple pictures at once* (3 pts) – batch upload efficiency.
    - *As a Production Worker, I want to see a list of my uploaded images for the current orderAggregate* (3 pts) – a confirmation view.
    - *As a Production Worker, I want to delete wrongly uploaded images* (3 pts) – correct mistakes before QA review.
- **Epic: Approve Photos (QA)**
    - *As a QA Engineer, I want to view images attached to each orderAggregate* (2 pts) – browse and inspect uploads.
    - *As a QA Engineer, I want to approve or reject uploaded images* (3 pts) – to validate weld quality.
    - *As a QA Engineer, I want to add comments to image documentation* (1 pt) – annotate issues or approvals.
    - *As a QA Engineer, I want to generate a preview of the QC reportAggregate* (2 pts) – see the final reportAggregate format before sending.
- **Epic: Admin Management**
    - *As an Admin, I want to create and delete user accounts* (2 pts) – manage system access.
    - *As an Admin, I want to assign roles to users* (2 pts) – control permissions (e.g. QA vs Operator).
    - *As an Admin, I want to reset user passwords* (2 pts) – handle account recovery.
- **Epic: System – Reports & Storage**
    - *As the System, I want to save images and metadata in a database linked to the orderAggregate number* (8 pts) – core data storage.
    - *As the System, I want to auto-generate QC reports from approved images* (8 pts) – combine images/comments into a PDF.
    - *As the System, I want to send QC reports to customers via email* (3 pts) – automatic reportAggregate distribution.

### Acceptance Criteria (Examples)

- **Story: Upload Pictures (Production Worker):** Given a logged-in Production Worker on a tablet, when they activate the camera and take photos of an expansion joint, *then* the app should allow selecting or entering an orderAggregate number and uploading all images. *And* each uploaded image is stored with its metadata (timestamp, user ID, orderAggregate number)file-s5auxkkhgo6pcbnbuf98dd.
- **Story: Approve/Reject Images (QA):** Given a logged-in QA user viewing an orderAggregate’s gallery, when they approve or reject an image, *then* the system records this decision and updates the image status. Comments can be added, and the status (approved/rejected) is visible in the reportAggregate previewfile-s5auxkkhgo6pcbnbuf98ddfile-s5auxkkhgo6pcbnbuf98dd.
- **Story: Generate QC Report (System):** Given a set of approved images and QA comments for an orderAggregate, when a reportAggregate is generated, *then* the system produces a formatted PDF containing all images and comments, ready to be sent to the customerfile-s5auxkkhgo6pcbnbuf98dd.
- **Story: Create User (Admin):** Given an Admin on the user management screen, when they create a new user with a role, *then* the user appears in the user list with the correct role assignment. The new user can log in with credentials, and their role controls their access level