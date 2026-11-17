# App Engine User Management

A small but complete **Java web application** deployed on Google App Engine: registration, authentication with session tokens, role management, and an admin surface for managing users.


## Features

| Area | Endpoints |
|---|---|
| **Registration** | `POST /register` — create a user, hashed password, default role |
| **Authentication** | `POST /login` · `POST /logout` — issues and revokes an opaque session token |
| **User listing** | `GET /listusers` — role-filtered, admin only |
| **Administration** | `POST /removeuser` · `POST /change` — role, state, password, attributes |
| **Pages** | JSP views for login, register and profile |

## Architecture

```
src/main/java/…/resources/   JAX-RS resources (Jersey 2)
src/main/java/…/util/        Request DTOs + AuthToken
src/main/webapp/             JSP pages, web.xml, App Engine descriptors
pom.xml                      Maven build + App Engine plugin
```

- **Jersey 2.39** for REST, JSON in/out via Gson.
- **Google Cloud Datastore** as the store — users are entities keyed by username, tokens are child entities of the user, so revoking a user's token is a single ancestor query.
- **SHA-512** password hashing (`commons-codec`).
- **Java 11**, packaged as a WAR and deployed with the App Engine Maven plugin.

## Run locally

Requires JDK 11+, Maven and the [Google Cloud SDK](https://cloud.google.com/sdk) with a Datastore emulator if you want persistence without a project.

```bash
mvn package
mvn appengine:run           # local dev server
mvn appengine:deploy        # deploy to App Engine
```

## A note on scope

The scope is intentionally small. It is also **not production-hardened**: passwords use fast SHA-512 rather than a memory-hard KDF, tokens do not rotate, and there is no rate limiting. It is here as a demonstration of servlet/JAX-RS structure and Datastore modelling, not as a template to deploy.

## Contents

| Path | What |
|---|---|
| `src/` | Application source |
| `pom.xml` | Build and deployment configuration |
| `project-brief.pdf` | Project brief |
| `LICENSE` | Apache 2.0 |
