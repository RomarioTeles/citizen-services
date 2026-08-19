-- V1: baseline migration
-- Validates Flyway connectivity and initializes flyway_schema_history.
-- Business tables will be added in future increments.

-- Ensure the public schema exists (idempotent on PostgreSQL)
CREATE SCHEMA IF NOT EXISTS public;
