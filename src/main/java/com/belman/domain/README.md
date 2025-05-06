# Domain Layer Organization

## Intention-Based Structure

The domain layer is organized by business capability/context following Domain-Driven Design principles. Each subdomain
represents a bounded context that aligns with a business capability rather than technical artifact types.

### Bounded Contexts

- **user:** User management, authentication, authorization, and roles
- **order:** Order processing, tracking, and fulfillment
- **photo:** Photo documents, metadata, and categorization
- **report:** Report generation, formatting, and delivery
- **customer:** Customer information and relationships
- **common:** Shared value objects, specifications, and exceptions used across contexts
- **security:** Security-related components that apply across multiple contexts

### Structure Within Each Context

Each context contains its own:

- Aggregates (root entities)
- Entities (non-root entities)
- Value Objects (specific to the context)
- Domain Events
- Repositories (interfaces)
- Domain Services
- Specifications
- Exceptions (specific to the context)
- Commands (for command-based operations)

### Benefits of Context-Based Organization

1. **Domain Alignment:** Structure mirrors the business domain
2. **Loose Coupling:** Contexts are isolated with clear dependencies
3. **Cohesion:** Related domain concepts stay together
4. **Evolution:** Contexts can evolve independently
5. **Team Autonomy:** Teams can own specific contexts

### Rules for Context Interaction

1. Contexts should not depend on other contexts (except common)
2. Inter-context communication should be explicit via events or services
3. Shared concepts should be in the common package
4. Context-specific value objects stay within their context
5. Security concerns that span contexts remain in the security package

### Mapping to Clean Architecture

This domain organization still conforms to Clean Architecture principles:

- Domain layer remains independent of infrastructure and presentation
- Domain interfaces (repositories, services) are implemented in outer layers
- Business rules are encapsulated within each context
