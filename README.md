# CareLite Microservices

CareLite will be built as independent microservices, not as one modular monolith.

## Microservices

- `api-gateway`: public entry point and routing layer.
- `identity-service`: identity provider integration boundary.
- `tenant-service`: clinic tenant onboarding and tenant lifecycle.
- `clinic-service`: patients, doctors, and appointments.
- `billing-service`: invoices and payment status.
- `document-service`: document upload and storage boundary.
- `notification-service`: notification commands and reminders.
- `audit-event-service`: audit/event history for tenant activity.
- `batch-service`: import, export, replay, and archive jobs.

Each service owns its own `pom.xml`, `application.yml`, package namespace, and future database boundary.

## Shared Libraries

- `carelite-common`: API errors and shared header constants.
- `carelite-security`: role names and security contracts.
- `carelite-tenancy`: tenant context helpers.
- `carelite-observability`: correlation/tracing helpers.
- `carelite-events`: shared domain event envelope.
- `carelite-test-support`: reusable test constants and fixtures.

These libraries are versioned JARs used by services. They are not deployable microservices.

## Build Order

1. Create standalone service skeletons.
2. Add local Docker infrastructure.
3. Build `tenant-service` first.
4. Build `clinic-service` after tenant context exists.
5. Add gateway routing.
6. Add audit events.

## Local Commands

From a service directory:

```powershell
mvn test
mvn spring-boot:run
```
