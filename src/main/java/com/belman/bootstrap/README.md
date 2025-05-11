# Bootstrap Package

This package contains bootstrap code that initializes the application. It is part of the infrastructure layer, not a
separate layer.

Bootstrap code is responsible for:

- Initializing the application
- Setting up services and repositories
- Configuring the application
- Starting the application

All bootstrap code should be placed in this package, not in a separate bootstrap layer. This package is *not* a layer,
it is a package.