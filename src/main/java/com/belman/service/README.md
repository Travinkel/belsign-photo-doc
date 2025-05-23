Great. I’ll map the backlog items from the BelSign Photo Documentation Module to clean architecture use cases.
This will help clarify what business logic belongs in the "use case" layer and what should be kept outside of it.
I’ll get back to you shortly with a breakdown for each epic.

# BelSign Photo Documentation Module – Use Case Layer Mapping

In Clean Architecture, the use case (application) layer contains the core business logic for each user action and *
*excludes** any UI or infrastructure-specific code. Below, each epic’s user stories are mapped to what the **use case
layer** should encapsulate, focusing on business rules and orchestration of user intentions. (UI presentation, device
integration, and database operations are noted as handled in other layers where applicable.)

## Epic 1: Authentication and User Management

* **Login System**: The use case verifies user credentials against the user store and determines the appropriate access
  level (user roles/permissions) upon successful login. It encapsulates password validation (including complexity rules)
  and success/failure handling, while UI handles input and error message display, and infrastructure deals with secure
  password storage and retrieval.
* **Logout Function**: The use case ends the user’s session by invalidating any active tokens or session identifiers and
  clearing relevant session state. Actual session termination mechanisms (e.g. cookie invalidation or token blacklist)
  and redirecting the user to a login screen are handled by the framework/UI, but the use case orchestrates the logout
  intent (ensuring any sensitive in-memory data is purged).
* **User Account Management**: The use case provides operations for an admin to create new user accounts, update user
  details, and deactivate or reactivate accounts according to business rules. It ensures all required information is
  provided and enforces rules like unique identifiers and proper activation/deactivation (only disabling access without
  deleting data), while delegating data persistence to a repository and form input handling to the UI; it also checks
  that only an **Administrator** can perform these actions.
* **Role Assignment**: The use case manages assigning or changing roles for a user and applies access-control rules
  immediately. It encapsulates the logic of updating a user’s roles (ensuring the roles are valid and updated instantly
  for subsequent permission checks) and verifies the actor has admin rights to do so, whereas the UI handles the role
  selection interface and the infrastructure updates the user record.
* **Password Management**: The use case handles password changes and resets. This includes allowing administrators to
  reset a user’s password and users to change their own password, enforcing business rules like password complexity and
  history (e.g. no reuse of recent passwords). The use case orchestrates the reset process (maybe generating a secure
  token or temporary password and marking the credential for update) and validates new passwords, while leaving the
  secure storage (hashing, etc.), notification (emailing reset links), and user interface for entering the new password
  to external layers.

## Epic 2: Order Management

* **Order Creation**: The use case orchestrates creating a new orderAggregate by validating the input details and then
  instantiating the orderAggregate in the system. It checks business rules such as proper orderAggregate number format
  and uniqueness (preventing duplicates) before saving the new orderAggregate, ensuring that once created (and persisted
  via the repository) the orderAggregate is immediately available for associating photos. UI layers handle the form
  input and confirmation display, and database insertion is done through an interface, but the core creation logic
  resides in the use case.
* **Order Search and Filtering**: The use case handles searching and filtering orderAggregates based on criteria like
  orderAggregate number, date, or status. It takes the user’s filter criteria and queries the orderAggregates repository
  with the appropriate business logic (e.g. combining filters, applying pagination or limits for efficiency) to retrieve
  matching orderAggregates. The real-time update of results is a UI concern (calling this use case as the user types or
  changes filters), and handling large data sets efficiently may involve pagination or limits defined in the use case,
  while heavy lifting (like database indexing for performance) is on the infrastructure side.
* **Order Details View**: The use case retrieves all relevant information about a selected orderAggregate, including
  orderAggregate metadata and a summary or list of associated photos (and their statuses). It ensures that a QA user
  viewing the details gets to see additional info like approval status of photos or the orderAggregate if applicable (
  business rule: the content can vary by role). The core logic aggregates the orderAggregate’s data (from various
  repositories if needed), while the UI is responsible for how that information is displayed (e.g. layout, responsive
  design for different screen sizes) and does the actual rendering of photos and metadata.
* **Order Status Tracking**: The use case governs the business rules for an orderAggregate’s status throughout the
  documentation process. It encapsulates how and when the orderAggregate status updates automatically in response to
  events (for example, moving from “New” to “In Progress” when photos are first added, or to “Pending
  Approval”/“Approved” when sent to or passed QA) and records a history of these status changes. The use case ensures
  the current status is always accurate and that status change history is maintained (e.g. logging timestamp and user
  for each status transition), while the UI simply reads the current status and displays it with appropriate
  icons/colors. (The visual indication of status is purely a presentation detail, not part of use-case logic.)

## Epic 3: Photo Capture and Upload

* **Camera Integration**: *Device/UI Intensive.* The use case for capturing a photo is minimal – it simply invokes a
  camera interface to take a picture and receives the image data for further processing. The core logic might specify
  certain requirements (e.g. request a specific resolution or camera mode appropriate for the task) and handle the flow
  after the photo is taken (such as attaching it to an orderAggregate or saving it temporarily), but the actual camera
  operation and preview functionality are handled by the device and interface adapters (outside the use case layer).
* **Photo Metadata**: The use case attaches contextual metadata to a captured photo. It handles user-provided details
  like component type, angle, etc., ensuring this data is collected and associated with the photo object, and
  automatically adds system metadata (timestamp of capture and the user’s ID who captured it). This logic is contained
  in the use case so that the photo and its metadata move together through the system. The user can edit the metadata
  before finalizing the upload (a rule the use case allows up until a certain point), whereas the UI provides the form
  for editing and the data storage layer persists the metadata alongside the photo.
* **Batch Upload**: The use case orchestrates uploading multiple photos in one operation. It accepts a batch of selected
  images and coordinates their transfer to the server or storage by iterating through each photo (or delegating to a
  batch uploader component), handling success or failure for each. Business rules in this use case include updating the
  state of each upload (e.g. marking photos as “uploading” or “pending”) and maybe queuing retries if an upload fails
  due to a network issue. The UI layer manages the selection of photos and shows the upload progress (including
  continuing the progress indicator if the app is minimized), and the infrastructure handles the actual file transfer
  and background processing (ensuring uploads continue even when the UI is not active).
* **Offline Capture**: The use case enables photo capture and storage to work when the device is offline. It
  encapsulates logic to save captured photos locally (in a persistent queue or cache) along with their metadata when no
  network is available, marking them as pending upload. It also includes the logic to detect or be notified when
  connectivity is restored and then automatically trigger the upload of any queued photos (so the user doesn’t have to
  re-initiate). The user is kept informed about pending uploads via status flags this use case can provide, while the
  actual detection of network status and the mechanism of background syncing (scanning for connectivity, scheduling
  uploads) are handled by the platform and infrastructure.
* **Photo Management**: The use case provides functionality for production users to manage already-uploaded photos for
  an orderAggregate *before* they are finalized by QA. It allows listing all photos attached to a specific
  orderAggregate and permits business-ruled actions like deleting a photo or editing its metadata **only** if the photo
  has not yet passed QA review (e.g. not approved yet). This ensures, for instance, that once a photo is approved or
  under QA review, it can no longer be altered or removed – a rule enforced in the use case logic. Additionally, this
  use case (in conjunction with the QA approval use case) triggers notifications when a photo is rejected by QA, so the
  production worker knows to take action (the use case might raise an event or flag for a rejected photo, and the
  infrastructure/notification service delivers the actual message). The UI simply calls these operations (delete, edit)
  and reflects the results, while the data layer handles actual removal or update of photo records.

## Epic 4: Quality Assurance

* **Photo Review Interface**: *Presentation-Focused.* The use case’s role in the photo review feature is to supply the
  QA engineer with the set of photos (and their metadata) that require review, potentially in the correct orderAggregate
  or grouping. All of the interactive aspects – displaying photos in a gallery or fullscreen, providing zoom and
  navigation between images – are handled in the UI (interface adapter) layer. In short, the use case gathers the data
  needed for review (e.g. fetching the next batch of photos awaiting approval), but the efficient browsing interface and
  image tools are a UI/UX concern outside the use case layer.
* **Approval Workflow**: The use case encapsulates the process of a QA engineer approving or rejecting a photo with an
  optional comment. It implements the core rules: marking a photo’s status as “Approved” or “Rejected,” recording the QA
  user’s comment and decision, and updating any related state (for example, if a photo is rejected, the use case might
  flag the associated orderAggregate or notify the production team of required rework). It ensures only authorized QA
  personnel can perform these actions (per role permissions). Triggering a notification to production on rejection is
  part of the business flow (the use case would call a notification interface or produce an event), whereas the actual
  sending of the notification and any UI elements (buttons to approve/reject, input for comments) are handled by
  external layers.
* **Batch Operations**: The use case supports approving or rejecting multiple photos in one bulk action. It takes a
  collection of selected photo IDs and a chosen action (approve all or reject all, possibly with comments per photo or a
  general comment) and processes them in a single transaction or loop. This use case ensures that the same validation
  and status updates happen for each photo in the batch, and it may enforce a confirmation step logically (e.g.
  requiring the caller to confirm intent before proceeding, especially for destructive actions). The UI is responsible
  for letting the QA user select multiple photos and initiate the batch action (including a confirmation dialog), while
  the use case executes the bulk update of photo statuses and logs or aggregates any results (such as noting which
  photos were updated).
* **Comparison Tools**: *Mostly UI Utility.* The use case layer has minimal involvement in photo/spec comparison
  functionality. It might retrieve the reference specification data or files needed for comparison (for example, pulling
  a technical diagram or expected image from a repository to be used as reference). However, presenting a spec
  side-by-side with a photo and providing interactive comparison tools (like image overlays or measurement instruments
  on the screen) is entirely in the realm of the interface/presentation layer. In essence, the use case ensures the
  required data (photos and their related specification info) is available, but the toggling between views and any
  on-screen measurement feature are implemented outside the use case layer (likely in a custom UI component optimized
  for the tablet).

## Epic 5: Reporting

* **QC Report Generation**: The use case generates a structured quality control reportAggregate from approved photos and
  their data. It encapsulates the core logic of gathering all necessary content – such as pulling all approved photos
  for a given orderAggregate or batch, along with metadata and any QA comments – and organizing it into a
  reportAggregate format. This use case would apply any business rules about reportAggregate content and structure (for
  example, ensuring company branding and required sections are included by using a reportAggregate template
  configuration) and then invoke an appropriate interface (e.g. a PDF generation service) to actually create the PDF
  file. The formatting details and file generation are handled by that external service/infrastructure, but deciding
  what goes into the reportAggregate (and assembling that data) is the responsibility of the use case.
* **Report Preview**: The use case provides a preview of the QC reportAggregate before finalization. It likely leverages
  the same data compilation logic as the full reportAggregate generation to produce a draft output that can be shown on
  screen. The use case ensures the preview content accurately reflects what the final reportAggregate will contain (so
  the user can trust what they see), possibly generating a temporary PDF or an on-the-fly representation. The UI then
  renders this preview (for instance, showing a PDF in-app or an HTML representation) and allows the user to make any
  adjustments by going back to other use cases (e.g. editing metadata or adding a missing photo if something is wrong).
  The performance aspect (loading quickly even with many photos) would be handled by optimizing the implementation (
  maybe generating a lower-resolution preview or caching), which is more on the infrastructure side; the use case simply
  orchestrates producing the preview data.
* **Report Templates**: The use case allows administrators to manage reportAggregate templates that define the structure
  and formatting of generated reports. It encapsulates the business logic of creating or updating a template (which
  might include setting up sections, placeholders for dynamic fields like dates or names, choosing layouts, etc.),
  validating those template configurations, and saving them for use in reportAggregate generation. Multiple templates
  can be handled by this use case (e.g. selecting a template for a certain reportAggregate type), and it ensures that
  template changes are applied to future reportAggregate generations without requiring code changes (since the use case
  will fetch the latest template data when generating a reportAggregate). The use case would enforce any rules (like
  certain mandatory fields in a template) and store the template via a repository, while the actual application of the
  template to format a PDF is done by the reportAggregate generation engine (infrastructure) during reportAggregate
  creation.
* **Email Integration**: The use case handles sending out a reportAggregate via email directly from the system. It
  encapsulates the logic of taking a generated reportAggregate (likely a PDF file) and preparing an email with that
  reportAggregate attached. This includes collecting the recipient addresses (whether entered by the user or selected
  from contacts) and any custom message the user added, and then calling an email sending interface (SMTP service or
  email API) with those details. The use case also updates the system state to record that an email was sent and expects
  or handles a delivery status update (tracking if the email was delivered or if it failed). The intricacies of email
  protocol, sending, and status callbacks are managed by the infrastructure (email service, API integration), and the UI
  simply provides a form for the user to input email details and perhaps shows the status of the send (which the use
  case can provide as success/failure feedback or via callbacks for delivery confirmation).

## Epic 6: System Administration

* **System Configuration**: The use case allows an administrator to change global system settings (e.g. email server
  configs, file storage paths, and other parameters) at runtime. It encapsulates logic to validate new configuration
  values and apply them – for example, updating an email server setting should cause the system to start using the new
  server for subsequent emails. The use case ensures these changes take effect immediately by informing the relevant
  components or by re-loading configuration in memory (business rule: no reboot/downtime needed). Actual reading/writing
  of configuration files or database entries, as well as any low-level reinitialization of connections (like
  reconnecting to a new storage location), is handled by the infrastructure layer. The use case simply coordinates the
  update and makes sure only an authorized admin can perform this action.
* **Metadata Configuration**: The use case enables administrators to define which metadata fields are used for photos in
  the system. It manages adding new fields, editing or removing existing fields, and setting attributes like field
  type (text, number, dropdown) and whether the field is required. Business rules enforced include ensuring that changes
  to the metadata schema do not break existing records – for instance, if a field is removed or changed, it only affects
  new uploads going forward, and adding a required field might only apply to future photos. The use case handles the
  integrity of the metadata schema (making sure field names are unique, types are valid, etc.) and updates the central
  configuration of metadata fields. The UI provides the interface for admins to make these changes, and the persistence
  of these configurations (and propagation to forms used in the Photo Upload module) is handled through repositories or
  configuration files, not within the use case itself.
* **Audit Logging**: This concern is implemented partly as cross-cutting logic and partly as an admin feature. The use
  case responsibility is to ensure that every significant action in the system results in a log entry capturing key
  details (timestamp, which user performed it, what was done, and on what entity). In practice, that means each relevant
  use case (such as Order Creation, Photo Approval, etc.) would call a logging interface as part of its execution to
  record the event. Additionally, there would be a use case for viewing the audit logs: an administrator can request a
  filtered list of log entries (by date, user, action type, etc.), which this use case would retrieve from the log
  store. The business rules include what events to log and how long to retain logs (retention policy), but the
  enforcement of retention (purging old logs) might be handled by an infrastructure scheduler. The actual storage of
  logs (file system, database, etc.) and low-level retrieval is done by the infrastructure, while the use case focuses
  on formatting log entries and filtering them for display, ensuring only authorized admins can access this sensitive
  information.
* **Performance Monitoring**: This feature is largely a technical/infrastructure concern, but the application can
  provide a use case to expose performance data to administrators. The use case might aggregate key performance
  metrics (CPU usage, memory, response times, etc.) from various sources or monitoring services and deliver them in a
  readable form to the admin interface. It could also include the logic for checking these metrics against predefined
  thresholds and flagging any issues (which could trigger alerts). However, the continuous tracking of performance and
  raising of alerts is typically done by the system’s monitoring tools or frameworks outside the core application logic.
  Essentially, the use case acts as a bridge to fetch and display performance stats and possibly current alerts for the
  admin on demand, while the heavy lifting (collecting metrics over time, storing historical data, sending real-time
  alerts) is handled by underlying infrastructure and only the summarized results or queries are handled in the use case
  layer.

## Epic 7: Mobile Optimization

* **Responsive Design**: *UI Concern.* There is no special use case logic for responsive design, as this is handled
  entirely by the presentation layer. The same use cases (business logic for orderAggregates, photos, etc.) are invoked
  regardless of device; it’s the UI (interface adapters) that must adapt layouts and controls for different screen sizes
  and input methods. In short, this user story does not introduce new use case layer behavior – it ensures the front-end
  renders properly on tablets vs. desktops without changing the underlying application logic.
* **Offline Mode**: *Cross-cutting Concern.* This story entails that multiple use cases are designed to work in the
  absence of network connectivity. Instead of a single use case, it’s a quality of the application: the use case layer
  operations should be able to function using local data when offline and sync with the server when a connection is
  restored. For example, creating an orderAggregate or capturing photos while offline would actually write to a local
  cache via the use case, which would later synchronize (through another use case or background process) to the server.
  The business rule here is to prevent data loss – every action the user takes offline is queued or saved so that it can
  be committed once online. The use case layer might provide a synchronization routine to push/pull updates when
  connectivity is back. The detection of offline/online state and initiation of sync processes is managed by the device
  and infrastructure (e.g. a background service), but each relevant use case ensures it doesn’t break when offline and
  properly merges changes later.
* **Battery Optimization**: *Non-functional Requirement.* There is no specific use case to implement here; rather, all
  use cases should be executed efficiently to conserve battery on mobile devices. This means the way use cases are
  implemented should avoid unnecessary computations or network calls (especially in the background), and possibly
  throttle background activities. For instance, the use case for batch upload might defer or slow down uploads when on
  battery power. These are design considerations in use case implementation and system scheduling, but not a distinct
  piece of business logic. The actual management of device sleep, limiting CPU usage, and other low-level optimizations
  are handled by the OS and hardware – developers ensure the app’s use cases don’t counteract those by doing more work
  than necessary.
* **Touch Optimization**: *UI/UX Concern.* This story is about making the interface touch-friendly (larger touch
  targets, intuitive gestures, spacing, and confirmation dialogs for critical actions). The use case layer remains
  unaffected – the business logic for any given action doesn’t change because of touch input. The only slight
  intersection is that for certain dangerous actions, the UI will ask for confirmation (e.g. “Are you sure you want to
  delete this?”) before calling the use case; the requirement for confirmation can be considered a UI responsibility,
  though it stems from a business desire to prevent mistakes. In summary, the use cases stay the same, and the interface
  adapters are adjusted to improve touch interactions and ensure the user confirms critical actions before the use case
  is executed.

## Epic 8: Help and Support

* **In-App Documentation**: The use case here would involve retrieving help documentation content relevant to the user’s
  context or query. For instance, if the user invokes help on a certain screen, the use case could fetch the appropriate
  help article or tips for that screen from a documentation repository. If a search is performed, the use case handles
  the search query logic (looking up keywords or topics in the help content index). Essentially, it encapsulates how the
  app finds the right documentation (ensuring it’s context-sensitive and possibly up-to-date). The actual content (text,
  images, etc.) is authored outside the use case and stored (maybe in a file or database or online), and the UI is
  responsible for displaying the help content in an overlay or help section with proper formatting. Keeping
  documentation updated is a content management task (outside the code), so the use case simply pulls whatever content
  is current from the source.
* **Tutorials**: Tutorial flows are largely implemented in the UI as guided sequences, but the use case layer may play a
  small role in determining when to show a tutorial and recording if it’s been completed or skipped. For example, on a
  user’s first login (especially per role), a use case could check a flag in the user profile to decide if a tutorial
  should be presented. It can also set a flag once the user finishes or opts out of the tutorial so that it doesn’t show
  repeatedly. These are simple business rules around tutorials (e.g. “show tutorial X to first-time QA users”). The
  content of the tutorial (steps, interactive tips) and the ability to skip or replay it are handled in the presentation
  layer. In short, the use case might trigger the tutorial and note completion in user settings, but the walkthrough
  itself is a front-end behavior.
* **Error Handling**: This is a systematic concern: each use case should produce clear and meaningful error information
  when something goes wrong, but not necessarily a separate use case on its own. The idea is that the use case layer
  should catch predictable errors in business operations and return standardized error responses or codes that the UI
  can translate into user-friendly messages. For example, if a photo upload fails due to size limits, the use case might
  return an error like “PhotoTooLarge” which the UI knows to show as “The photo exceeds the size limit.” The use case
  should also contain logic to attempt recovery or offer next steps when possible (for instance, if a service is down,
  the use case could queue the action for later or advise a retry). Additionally, the use cases should ensure that
  partial data is not lost on errors – e.g., if step 3 of a 5-step process fails, perhaps steps 1-2 are rolled back or
  stored so the user can resume. All critical errors or exceptions that occur in the use case are passed to a logging
  service (as per audit logging). The UI and framework handle the actual display of error dialogs and any
  platform-specific recovery (like providing a “Retry” button that calls the use case again), but the logic behind those
  errors and recoveries lives in the use case layer.
* **Feedback Mechanism**: The use case supports users submitting feedback from within the app. It gathers the feedback
  details the user provides (comments, ratings, etc.) and automatically attaches relevant context (such as the user’s
  ID, current screen or workflow, timestamp, and possibly application version). If the user attaches a screenshot or
  other file, the use case will include that in the feedback package. The use case then routes this feedback to the
  appropriate recipient or system: for example, it might categorize the feedback (bug reportAggregate, feature
  suggestion, general comment) and send it to a designated email or insert it into a tracking system via an interface.
  The rule “routed to the appropriate team” suggests there’s logic to direct the feedback based on its type or context (
  perhaps different email addresses or project queues). All this orchestration is in the use case, while the actual
  sending (emailing the feedback or calling an external API) is handled by the infrastructure layer, and the UI provides
  the form and screenshot capture capability for the user to submit their feedback.
