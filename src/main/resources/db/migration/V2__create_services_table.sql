-- V2: create services table
CREATE TABLE services (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(150)              NOT NULL,
    description VARCHAR(500),
    active      BOOLEAN                   NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP WITH TIME ZONE  NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE  NOT NULL
);
