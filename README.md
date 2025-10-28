# EXE Team Management

---

Backend project for **EXE course at FPT University**.  
This system helps students create, join, and manage interdisciplinary project teams (e.g., SE + IB), 
supporting features like posts, join requests, voting, notifications, and more.

---

## Tech Stack
- **Java 17**
- **Spring Boot 3.5.6**
- **MySQL 8**
- **Spring Data JPA**
- **Spring Security + JWT** (authentication & authorization)
- **Spring Mail** (email sending)
- **Flyway** (database migration)
- **OpenAPI (Swagger UI)** for API documentation

---

##  Requirements
Before running the project, make sure you have installed:

- [Java 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Maven 3.9+](https://maven.apache.org/)
- [MySQL 8](https://dev.mysql.com/downloads/mysql/)
- (Optional) [Docker](https://www.docker.com/) if you want to run DB in a container

---

## Run locally

Clone the repository:

```bash
git clone https://github.com/<your-username>/team-management.git
cd team-management
```

---

## Cloudflare R2 media storage

Set the following environment variables before running the application to enable uploads to Cloudflare R2 (you can copy `.env.example` to `.env` locally and fill in the values):

| Variable | Description |
| --- | --- |
| `R2_ACCOUNT_ID` | Cloudflare account identifier used to compose the S3 endpoint. |
| `R2_ACCESS_KEY_ID` | Access key ID for the R2 bucket. |
| `R2_SECRET_ACCESS_KEY` | Secret access key paired with the access key ID. |
| `R2_BUCKET_NAME` | Target bucket where media files are stored. |
| `R2_PUBLIC_BASE_URL` | Public base URL used to serve media assets. |

### Rotating credentials

1. Generate a new API token in the Cloudflare dashboard with access to the target R2 bucket.
2. Update the secret manager entries (or `.env` when running locally) with the new `R2_ACCESS_KEY_ID` and `R2_SECRET_ACCESS_KEY` values.
3. Redeploy or restart the backend service so that the updated credentials are picked up on startup.
4. Remove the previous credentials from Cloudflare once traffic is verified with the new keys.

The service reads the configuration via Spring Boot configuration properties (`r2.*`), so no code changes are required when rotating keys.
