# Belsign Photo Doc

This project is organized using a simple three-layer architecture.

- **Presentation**: user interface classes, view models, and navigation. Everything the user sees lives here.
- **Business**: application logic such as services, use cases, and session management. The presentation layer calls into this layer.
- **Data**: persistence, email, camera, and other infrastructure adapters. The business layer uses these classes to talk to the outside world.

Layers depend only inward:
Presentation → Business → Data.

This structure helps keep responsibilities clear and makes the code easier to understand for beginners.
