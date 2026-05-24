# CareLite Microservices

CareLite is built as independent microservices, not as a modular monolith.

The root `pom.xml` is a shared Maven parent for dependency and plugin defaults. It is
not a Maven reactor build and intentionally does not contain a `<modules>` section.
Running `mvn test` from the repository root only validates the parent POM; it does
not build or test every service.

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

Each service owns its own `pom.xml`, `application.yml`, package namespace, and future
database boundary. Services are built, tested, packaged, deployed, and versioned as
independent deployable units.

## Shared Libraries

- `carelite-common`: API errors and shared header constants.
- `carelite-security`: role names and security contracts.
- `carelite-tenancy`: tenant context helpers.
- `carelite-observability`: correlation/tracing helpers.
- `carelite-events`: shared domain event envelope.
- `carelite-test-support`: reusable test constants and fixtures.

These libraries are versioned JARs used by services. They are not deployable microservices.

## Current Build Model

- Root build: parent POM only.
- Library build: install shared JARs into the local Maven repository.
- Service build: run Maven from the specific service directory.
- Platform verification: run the helper scripts or build services one by one.

This keeps the repository aligned with the microservice goal while still sharing
common dependency versions through the parent POM.

## Build Order

1. Create standalone service skeletons.
2. Add local Docker infrastructure.
3. Build `tenant-service` first.
4. Build `clinic-service` after tenant context exists.
5. Add gateway routing.
6. Add audit events.

## Local Commands

Start local infrastructure:

```powershell
cd infra
docker compose up -d
cd ..
```

Install shared libraries first:

```powershell
.\install-libs.ps1
```

From a service directory:

```powershell
mvn test
mvn spring-boot:run
```

Or install all current service artifacts:

```powershell
.\install-services.ps1
```

Each service is independently buildable and deployable. Shared libraries are normal
Maven JAR dependencies.

Local infrastructure details are documented in [infra/README.md](infra/README.md).

## Recommended Next Steps

1. Finish `tenant-service` tests for tenant onboarding, duplicate slugs, validation,
   and idempotency behavior.
2. Keep local Docker infrastructure limited to Redis and MongoDB. Use Confluent
   Cloud for Kafka and real AWS S3 for document storage.
3. Add tenant context infrastructure for downstream services: read `X-Tenant-ID`,
   set `TenantContextHolder`, clear it after each request, and later apply PostgreSQL
   schema routing.
4. Improve `api-gateway` with correlation ID propagation, tenant header handling,
   Redis rate limiting, and route circuit breakers.
5. Build `clinic-service` next, starting with patient registration and then
   appointment booking.
6. Add domain events and the outbox pattern before building `audit-event-service`.
