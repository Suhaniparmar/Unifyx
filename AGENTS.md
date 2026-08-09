# Unifyx - AI Agent Guidelines

**Unifyx** is a two-sided marketplace platform connecting service providers (workers/contractors) with service seekers (owners). The system uses a **monorepo structure** with separate backend (Spring Boot) and frontend (Android) applications.

## Architecture Overview

### Backend (Spring Boot REST API)
- **Location:** `Unifyx_Backend/`
- **Stack:** Spring Boot 3.4.2, Java 17, MySQL, Maven
- **Port:** 8080 (configured at `http://0.0.0.0:8080`)
- **Database:** MySQL at `jdbc:mysql://localhost:3306/unifyxproject`
- **Credentials:** `root` user with empty password (local dev on Mac). Update if needed in `application.properties`

**Core Architecture Pattern:** Layered MVC with Service-Repository pattern
```
Controller → Service → Repository → JPA/Database
```

### Frontend (Android)
- **Location:** `frontend/app/`
- **Stack:** Android (API 24+), Gradle, Firebase Auth, Retrofit2, Gson
- **Client Configuration:** Retrofit base URL = `http://10.0.2.2:8080/` (Android emulator localhost translation)

**Communication:** REST API via Retrofit2 HTTP client with GSON JSON serialization

## Key Domain Model

The platform has three primary user roles with corresponding profile entities:

1. **Users** (Base entity)
   - `uid` (unique, Firebase-managed)
   - `email` (unique)
   - `role` ("owner", "worker", "contractor")
   - Optimistic locking via `@Version` annotation

2. **OwnerProfile** - Service seekers who post jobs
3. **WorkerProfile** - Individual service providers
4. **ContractorProfile** - Contractor service providers

**Related Entities:**
- `Post` - Job listings created by owners
- `BidRaise` - Bids submitted by workers/contractors on posts
- `BidReceive` - Received bids (inverse relationship)
- `Negotiate` - Negotiation data between parties
- `Portfolio` - Worker/contractor portfolios
- `Payment` - Payment records

## Build & Deployment

### Backend Build & Run
```bash
cd Unifyx_Backend
mvn clean install
mvn spring-boot:run
# OR run the JAR directly:
java -jar target/Unifyx_Backend-0.0.1-SNAPSHOT.jar
```
- Maven compiler targets Java 17 with Lombok annotation processing
- Spring Boot Maven Plugin handles application packaging
- Auto-creates/updates DB schema via `spring.jpa.hibernate.ddl-auto=update`
- Application starts on `http://localhost:8080` after ~5 seconds
- Test endpoint: `curl http://localhost:8080/users`

### Frontend Build
```bash
cd frontend
./gradlew clean build
./gradlew assembleDebug
```
- Uses Gradle version management via `libs.versions.toml` (see `gradle/libs.versions.toml`)
- Requires Firebase configuration (`app/google-services.json`)
- Android compilation SDK: 34, min SDK: 24
- Java compatibility: 1.8

## MySQL Database Setup (CRITICAL FOR FIRST RUN)

### Fresh Setup on New Machine
```bash
# 1. Install MySQL (if not already)
brew install mysql

# 2. Start MySQL service
brew services start mysql

# 3. Create the database
mysql -u root -e "CREATE DATABASE IF NOT EXISTS unifyxproject CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 4. Verify database exists
mysql -u root -e "SHOW DATABASES;"

# 5. Update backend credentials in application.properties to use empty password for root

# 6. Run backend - Hibernate will auto-create all tables
cd Unifyx_Backend && mvn spring-boot:run
```

**Important:** When MySQL is freshly installed via Homebrew, the root user has **no password** by default. You must update `Unifyx_Backend/src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=
```
(Leave password blank, or remove the line entirely)

### Verify Database Tables
```bash
mysql -u root -e "USE unifyxproject; SHOW TABLES;"
# Should show 13 tables: users, posts, owner_profile, worker_profile, contractor_profile, 
# bid_raise, bid_receive, negotiate, portfolio, payment, post_images, worker_categories, 
# contractor_categories
```

### Restore from Old Backup
If you have a backup from your old laptop:
```bash
# Export from old laptop:
mysqldump -u root -p[old_password] unifyxproject > unifyx_backup.sql

# Import on new Mac:
mysql -u root unifyxproject < unifyx_backup.sql
```

## Critical Developer Workflows

### 1. Adding a New API Endpoint
1. Create model in `Unifyx_Backend/src/main/java/org/example/unifyx/Model/`
2. Create `@Entity` class with JPA annotations (see `Users.java` pattern)
3. Create repository in `repository/` extending `JpaRepository<T, ID>`
4. Create service in `service/` with `@Service` annotation, use `@Transactional` for writes
5. Create controller in `controller/` with `@RestController` and `@RequestMapping`
6. Add corresponding Retrofit interface method in `frontend/app/src/main/java/com/example/unifyx/network/ApiService.java`

**Example Pattern from UserController:**
```java
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    
    @GetMapping("/{uid}")
    public ResponseEntity<Users> getUserById(@PathVariable String uid) {
        // Use service layer, not repository directly in controllers
    }
}
```

### 2. Cloudinary Image Uploads
- Configured via `CloudinaryConfig.java` bean
- Credentials in `application.properties` (loaded from env via `dotenv-java`)
- Used by controllers for profile/portfolio image handling
- Backend validates and uploads, returns URLs to frontend

### 3. Firebase Authentication Flow
- Android frontend uses Firebase Auth for user login/signup
- UID from Firebase stored in backend `Users.uid` field
- Frontend redirects based on user role after authentication (see `MainActivity.java` pattern)
- Controllers accept UID as path parameter for user identification

### 4. Database Schema Management
- Hibernate auto-updates schema via `ddl-auto=update`
- **Caution:** This is development-friendly but requires manual migration planning for production
- Entity relationships use `@OneToMany`, `@ManyToOne` with foreign key constraints
- Optimistic locking via `@Version` prevents concurrent update conflicts

## Project-Specific Conventions

### Java Code Style
- **Lombok Used:** Getters/setters auto-generated via `@Getter @Setter`
- **Service Layer Pattern:** All business logic goes in `service/`, repositories are data access only
- **Transactional Operations:** Mark multi-step operations with `@Transactional` in service layer
- **Error Handling:** Return `ResponseEntity` with appropriate HTTP status (e.g., `404 NOT_FOUND` for missing resources)

### API Endpoint Naming
- Resource-based URLs: `/users`, `/posts`, `/bids`, `/owners`, `/contractors`, `/workers`
- Filtering by ID: `/{id}` or `/{uid}` depending on entity's primary key
- Role-based queries: `/users/role/{uid}` pattern
- **Backend Controllers:** UserController, PostController, BidRaiseController, OwnerProfileController, WorkerProfileController, ContractorProfileController, SearchController, OwnerHomeController, WorkerHomeController, ContractorHomeController
- **Backend Services:** UserService, PostService, BidRaiseService, OwnerProfileService, WorkerProfileService, ContractorProfileService, SearchService, HomepageService, OwnerHomeService, WorkerHomeService, ContractorHomeService
- **Backend Repositories:** UserRepository, PostRepository, BidRaiseRepository, OwnerProfileRepository, WorkerProfileRepository, ContractorProfileRepository

### Frontend Activity Structure
- Base activities for each user role: `MainActivity.java` → `choose_role.java` → role-specific homes
- Role-specific packages: `owner/`, `worker/`, `contractor/`
- Use `RetrofitClient` singleton for HTTP client initialization
- Pass data via `Intent.putExtra()` for activity navigation

### MySQL Connection
- Host: `localhost:3306` (development)
- Database: `unifyxproject`
- Credentials in `application.properties` (default: `root`/empty password on Mac)
- **Note:** Update credentials for production; don't commit to version control

## Integration Points & Dependencies

### External Services
1. **Firebase Auth** - User authentication (Android frontend)
2. **Cloudinary** - Image storage and CDN (API keys in `application.properties`)
3. **Razorpay** - Payment processing (Android dependency: `com.razorpay:checkout:1.6.19`)

### Key Dependencies
- **Backend:** Spring Data JPA, MySQL Connector, Cloudinary SDK, Jackson JSON, HttpClient5, Lombok
- **Frontend:** Firebase Auth, Retrofit2, Gson, Glide (image loading), OkHttp3, Material Design

### API Communication Pattern
**Frontend → Backend:**
```
Retrofit API Call → ApiService interface → RetrofitClient → HTTP request → Backend Controller
```
**Response Flow:**
```
Service → Repository → JPA → MySQL ↔ Controller → ResponseEntity → Retrofit Call → Android Activity
```

## Common Gotchas & Patterns

1. **Android Emulator Localhost:** Use `10.0.2.2:8080` not `localhost:8080` in Retrofit base URL
2. **Firebase UID vs Database ID:** Use Firebase UID as primary lookup key; database auto-increment ID is secondary
3. **Service Layer Required:** Don't inject repositories directly into controllers; always use service layer
4. **Optimistic Locking:** `Users` entity uses `@Version` for concurrency; update operations may fail if entity is stale
5. **Environment Variables:** Cloudinary credentials loaded via `dotenv-java`; ensure `.env` file exists or use `application.properties`
6. **MySQL Password on Local:** After `brew install mysql`, root user has **NO PASSWORD**. Update `application.properties` to use empty password: `spring.datasource.password=`
7. **Repository Query Methods Must Match Entity Fields:** Method names like `findBySiteAddressIgnoreCase` must match actual entity field `siteAddress`. Using wrong field names causes startup errors. Example: ContractorProfile has `address` not `siteAddress`
8. **Hibernate DDL Auto:** Set to `update` for development (auto-creates tables). Change to `validate` for production.

## Debugging Tips

### Backend Won't Start
1. Check MySQL is running: `brew services list`
2. Verify database exists: `mysql -u root -e "SHOW DATABASES;"`
3. Check logs for compilation errors: `mvn clean compile`
4. **Common issue:** Repository query methods reference non-existent entity fields (e.g., `siteAddress` vs `address`)
   - Search for `@Query` annotations in repository files
   - Verify field names match entity class property names exactly
5. Check application.properties has correct MySQL credentials (usually `root` with empty password on Mac)

## File References for Key Patterns
- **Entity modeling:** `Unifyx_Backend/src/main/java/org/example/unifyx/Model/Users.java`
- **Service layer:** `Unifyx_Backend/src/main/java/org/example/unifyx/service/UserService.java`
- **REST controller:** `Unifyx_Backend/src/main/java/org/example/unifyx/controller/UserController.java`
- **Cloudinary config:** `Unifyx_Backend/src/main/java/org/example/unifyx/cloudinary/CloudinaryConfig.java`
- **Android API client:** `frontend/app/src/main/java/com/example/unifyx/network/RetrofitClient.java`
- **API endpoints:** `frontend/app/src/main/java/com/example/unifyx/network/ApiService.java`
- **Build config:** `Unifyx_Backend/pom.xml` and `frontend/app/build.gradle`

