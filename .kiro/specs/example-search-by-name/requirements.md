# Requirements Document

## Introduction

This document defines the requirements for adding service search by name or description functionality to the Citizen Services API.

**Feature Name**: Service Search by Name  
**Version**: 1.0  
**Last Updated**: 2026-09-16  
**Author**: Development Team

## Background

Adding the ability to search services by name or description using a query parameter enables citizens to easily find the services they need without browsing through all available services.

## Glossary

| Term | Definition |
|------|------------|
| Service | A public service offered by the government to citizens |
| Search Term | User-provided text used to filter services |
| Partial Match | Match where the search term appears anywhere within the name or description |
| Case-Insensitive | Match that ignores letter case (e.g., "CPF" matches "cpf") |
| Pagination | Division of results into pages with configurable size and offset |

## Requirements

### Functional Requirements

- [ ] **FR-01**: GET /api/v1/services?search=<term> returns matching services
- [ ] **FR-02**: Search is case-insensitive
- [ ] **FR-03**: Partial match supported in name or description
- [ ] **FR-04**: Only active services are returned
- [ ] **FR-05**: Results are paginated

### Non-Functional Requirements

- [ ] **NFR-01**: Response time is acceptable (< 500ms)
- [ ] **NFR-02**: Authentication required (401 if not authenticated)

### Acceptance Criteria

- [ ] User can search services by typing a term
- [ ] Search matches name OR description (partial)
- [ ] Search is case-insensitive (e.g., "cpf" matches "CPF")
- [ ] Only active services are returned
- [ ] Pagination applies (page, size, total)
- [ ] Response time is acceptable (< 500ms)
- [ ] Authentication required (401 if not authenticated)

## Assumptions

- Database is available and connected
- User is authenticated (via HTTP Basic)
- Existing CitizenService entity can be queried
- PostgreSQL database supports LIKE queries with wildcards

## Dependencies

- Existing CitizenService entity
- Existing ServiceController, ServiceApplicationService, ServiceRepository
- No external dependencies required
