# CareLite Local Infrastructure

This directory contains the local development infrastructure for the CareLite
microservices. Only Redis and MongoDB run locally in Docker for now.

Kafka uses Confluent Cloud, S3 uses real AWS S3, and relational database settings
come from the target environment.

## Services

- Redis on `localhost:6379`
- MongoDB on `localhost:27017`

## Start

```powershell
cd infra
docker compose up -d
```

## Stop

```powershell
cd infra
docker compose down
```

## External Service Configuration

Configure Kafka and S3 with environment variables. Do not commit credentials to
the repository.

Kafka:

```text
CARELITE_KAFKA_BOOTSTRAP_SERVERS=<confluent-bootstrap-server>
CARELITE_KAFKA_API_KEY=<confluent-api-key>
CARELITE_KAFKA_API_SECRET=<confluent-api-secret>
CARELITE_KAFKA_CLIENT_ID=carelite-dev-client
```

AWS S3:

```text
AWS_REGION=<aws-region>
AWS_ACCESS_KEY_ID=<aws-access-key-id>
AWS_SECRET_ACCESS_KEY=<aws-secret-access-key>
CARELITE_S3_BUCKET=<bucket-name>
```
