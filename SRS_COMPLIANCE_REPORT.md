# SRS Compliance Report - Instagram Clone

**Generated:** 2026-05-15
**Project Root:** `/Users/bhatia_ji99/Desktop/flytbaseprojects/instagram-clone/`
**SRS Source:** Instagram_SRS.pdf
**Implementation Plan:** `~/.claude/plans/ethereal-swimming-engelbart.md`

---

## Section 1: User Story Coverage

### US01 - User Registration

| # | Acceptance Criteria | Status | Notes |
|---|---------------------|--------|-------|
| 1 | Prominent "Register" / "Create an Account" option visible | MISSING | Frontend-only; no frontend exists yet. Not in plan scope (backend-first). |
| 2 | Clicking Register takes user to registration page | MISSING | Frontend-only. |
| 3a | Full Name: letters only, min 1 word, first letter capitalized | DONE | `@ValidFullName` + `FullNameValidator` implemented correctly. |
| 3b | Email: valid format, domains com/org/in | DONE | `@ValidEmailDomain` + `EmailDomainValidator` implemented correctly with `Set.of("com", "org", "in")`. |
| 3c | Username: lowercase letters, digits, special chars, unique | DONE | Regex `^[a-z0-9._]+$` in `RegisterRequestDTO`; uniqueness check in `AuthService.register()`. |
| 3d | Password: 8-16 chars, lowercase + uppercase + digit + special | DONE | Regex + `@Size` annotations in `RegisterRequestDTO`. |
| 3e | Confirm Password: must match, "Passwords do not match" message | DONE | `@PasswordMatch` class-level validator with custom message bound to `confirmPassword` field. |
| 4 | After registration, redirect to Home page | MISSING | Frontend-only. Backend returns 201 + JSON as expected. |
| 5 | Enforce username uniqueness | DONE | `existsByUsername()` check + `@Indexed(unique=true)` on entity. |
| 6 | Appropriate feedback on technical failures | DONE | `GlobalExceptionHandler` handles generic `Exception` with 500 + message. |
| 7 | "Forgot Password" option available | DONE | `POST /api/auth/forgot-password` endpoint implemented. |

**Summary:** 6/7 backend-relevant criteria DONE. 3 criteria are frontend-only (MISSING from current scope but expected since this is backend-first).

---

### US02 - User Login

| # | Acceptance Criteria | Status | Notes |
|---|---------------------|--------|-------|
| 1 | Validate credentials | DONE | `AuthService.login()` validates username existence + password match via BCrypt. |
| 2 | 3 failed attempts in 30s -> circuit open for 1 min (Resilience4j) | PARTIALLY DONE | Circuit breaker configured but uses `COUNT_BASED` sliding window of size 3 instead of SRS-specified `TIME_BASED` 30-second window. The open duration (60s) is correct. |
| 3 | Display running timer of 1 min while circuit open + proper message | PARTIALLY DONE | `GET /api/auth/login/status` endpoint returns state + remaining seconds. However, it returns the config's wait duration, not a live countdown. Timer display is frontend responsibility. |
| 4 | Reset password via email verification | DONE | `forgotPassword()` generates reset token; `resetPassword()` validates token + expiry and updates password. |
| 5 | Display proper error messages for wrong credentials | DONE | `InvalidCredentialsException` returns 401 + "Invalid username or password". |
| 6 | After login, redirect to home page with posts + profile nav | MISSING | Frontend-only. Backend returns JWT token as expected. |

**Summary:** 4/5 backend-relevant criteria DONE. Circuit breaker window type mismatch is a minor deviation.

---

### US03 - Post Updates

| # | Acceptance Criteria | Status | Notes |
|---|---------------------|--------|-------|
| 1 | Access post creation interface | MISSING | Frontend-only. |
| 2 | Option to add photos or videos | DONE | `CreatePostRequestDTO` supports `mediaUrls` list + `mediaType` (IMAGE/VIDEO/TEXT). |
| 3 | Select multiple photos/videos from gallery | PLANNED | Plan (Target 8) mentions file upload endpoint `POST /api/posts/upload`, but it is NOT implemented in code. |
| 4 | Support common image/video formats | PLANNED | No format validation exists. Plan defers media handling. |
| 5 | Add captions, tags, descriptions | DONE | `caption`, `tags`, `hashtags` fields all present in `CreatePostRequestDTO` and `Post` entity. |
| 6 | Photo/video editing (crop, filter, brightness) | MISSING | Not in the plan at all. |
| 7 | Privacy settings (public, friends, private) | DONE | `Privacy` enum with PUBLIC/FRIENDS/PRIVATE. `CreatePostRequestDTO` includes privacy field with PUBLIC default. |
| 8 | Add hashtags and additional text | DONE | `hashtags` List<String> field implemented. |
| 9 | Post published to profile/feed, visible to followers | PARTIALLY DONE | `POST /api/posts` creates post. Feed endpoint for followed users' posts is NOT implemented (planned in Target 18). |
| 10 | Confirmation message/notification | DONE | Returns 201 + PostResponseDTO on success. |
| 11 | Visually appealing display with interaction (like, comment, share) | PARTIALLY DONE | Like is implemented. Comment and share are NOT implemented and NOT in the plan. |

**Summary:** 5 DONE, 3 PARTIALLY DONE, 2 PLANNED, 1 MISSING entirely (photo editing).

---

### US04 - View and Like Posts

| # | Acceptance Criteria | Status | Notes |
|---|---------------------|--------|-------|
| 1 | Scroll feed or view specific user's profile posts | PARTIALLY DONE | `GET /api/posts/user/{userId}` with pagination exists. Feed from followed users NOT implemented (planned Target 18). |
| 2 | Each post shows author name, profile pic, timestamp, content | PARTIALLY DONE | `PostResponseDTO` returns userId, caption, media, timestamps. Does NOT return author name/profile picture (no inter-service call to auth-service). |
| 3 | Like a post via button/icon | DONE | `POST /api/posts/{postId}/like` implemented. |
| 4 | Like button visually indicates liked state | DONE (backend) | `PostResponseDTO.liked` boolean field returned. Frontend display is frontend-only. |
| 5 | See total number of likes | DONE | `likesCount` in PostResponseDTO + `GET /api/posts/{postId}/likes` endpoint. |
| 6 | Unlike a post, count updates | DONE | `DELETE /api/posts/{postId}/like` implemented with count decrement. |
| 7 | Like multiple posts without limitations | DONE | No restrictions on liking multiple different posts. Double-like on same post properly blocked. |

**Summary:** 5 DONE, 2 PARTIALLY DONE. Feed and author enrichment are planned but not coded.

---

### US05 - Following Users

| # | Acceptance Criteria | Status | Notes |
|---|---------------------|--------|-------|
| 1 | Search for other users on the platform | MISSING | Search is NOT implemented. Planned in Target 15 but not coded. |
| 2 | View user's profile before following | DONE | `GET /api/auth/profile/{userId}` returns profile data. |
| 3 | Follow a user via button/icon | DONE | `POST /api/follow/{targetUserId}` implemented. |
| 4 | Follow button visual indication | DONE (backend) | `isFollowing` field in `FollowResponseDTO` + `GET /api/follow/check/{targetUserId}`. |
| 5 | See total number of followers | DONE | `GET /api/follow/{userId}/stats` returns followerCount/followingCount. |
| 6 | Unfollow a user, count updates | DONE | `DELETE /api/follow/{targetUserId}` implemented. |
| 7 | Receive updates/notifications from followed users | MISSING | Not implemented. Not in the plan. |
| 8 | Real-time follower count updates | MISSING | No WebSocket or SSE implementation. Not in the plan. |
| 9 | Follow multiple users without limitations | DONE | No restrictions in code. Self-follow properly blocked. |

**Summary:** 5 DONE, 1 DONE (backend-only), 3 MISSING.

---

### US06 - Search Specific Users/Hashtags

| # | Acceptance Criteria | Status | Notes |
|---|---------------------|--------|-------|
| 1 | Enter username or hashtag in search bar | PLANNED | Target 15 in plan, NOT coded. No search endpoints exist. |
| 2 | View list of matching users or posts | PLANNED | Target 15. |
| 3 | Display profile picture, username, bio, content | PLANNED | Target 15. |
| 4 | Filter/sort by relevance, popularity, recency | PLANNED | Target 15. |
| 5 | Click on result to view details | PLANNED | Underlying GET endpoints exist (profile, post by ID). |
| 6 | Refine search with additional keywords/filters | PLANNED | Target 15. |
| 7 | Save/bookmark specific searches | MISSING | Not in the plan at all. |

**Summary:** 0 DONE, 5 PLANNED, 2 MISSING (bookmark/save searches not planned).

---

### US07 - Delete Post

| # | Acceptance Criteria | Status | Notes |
|---|---------------------|--------|-------|
| 1 | Navigate to the post to delete | DONE | `GET /api/posts/{postId}` exists. |
| 2 | Confirmation prompt before deletion | MISSING | Frontend-only concern. |
| 3 | Post permanently removed from platform | DONE | `DELETE /api/posts/{postId}` deletes post from MongoDB. |
| 4 | Associated comments/interactions removed | PARTIALLY DONE | Likes are cascade-deleted (`likeRepository.deleteByPostId()`). Comments are NOT implemented. |
| 5 | Confirmation message after deletion | DONE | Returns 204 No Content (standard REST). |
| 6 | Cannot access deleted post via direct links | DONE | Deleted from DB, `GET` returns 404. |
| 7 | Deletion is irreversible | DONE | Hard delete from MongoDB, no soft-delete mechanism. |
| 8 | Delete multiple posts without limitations | DONE | No restrictions. Ownership check ensures only post owner can delete. |

**Summary:** 6 DONE, 1 PARTIALLY DONE, 1 MISSING (frontend confirmation prompt).

---

### US08 - Exploring Trending Contents

| # | Acceptance Criteria | Status | Notes |
|---|---------------------|--------|-------|
| 1 | Dedicated trending section/page | PLANNED | Target 13 in plan. Only skeleton (HealthController) exists in code. |
| 2 | Curated list of popular posts/topics/hashtags | PLANNED | Target 13. |
| 3 | Visually appealing grid/carousel layout | MISSING | Frontend-only. |
| 4 | Display views, likes, comments count | PLANNED | Target 13. |
| 5 | Click trending post for details | PLANNED | Post detail endpoint exists. |
| 6 | Regularly updated trending section | PLANNED | Target 13 plans `@Scheduled(fixedRate = 60000)`. |
| 7 | Filter by recency, popularity, location | PLANNED | Target 13 (location filtering NOT planned). |
| 8 | Real-time updates on trending content | PLANNED | Target 13 mentions real-time but no WebSocket/SSE planned. |

**Summary:** 0 DONE, 6 PLANNED, 2 MISSING (location filter, real-time WebSocket).

---

## Section 2: Non-Functional Requirements Coverage

### Performance

| Requirement | Status | Details |
|-------------|--------|---------|
| Fast and responsive, minimal lag | PARTIALLY DONE | Basic service implementations exist. No performance tuning. |
| Caching mechanisms | NOT IMPLEMENTED | No `@Cacheable`, `@CacheEvict`, or any caching layer (Redis, Caffeine, etc.) found anywhere. Plan does not address caching. |
| Lazy loading for images | NOT IMPLEMENTED | Frontend concern, but no backend support (pagination exists for posts, which helps). |

### Scalability

| Requirement | Status | Details |
|-------------|--------|---------|
| Handle large traffic, scale as needed | PARTIALLY DONE | Microservice architecture + Consul discovery in place. |
| Load balancing | PLANNED | Gateway uses `lb://` prefix. Plan Target 17 describes running 2 instances but NOT implemented. |
| Multiple instances | PLANNED | Target 17. Configuration supports it but not tested/verified. |

### Security

| Requirement | Status | Details |
|-------------|--------|---------|
| BCrypt password hashing | DONE | `BCryptPasswordEncoder` bean configured and used in `AuthService`. |
| JWT authentication | DONE | `JwtUtil` generates/validates tokens with HMAC-SHA256, 24h expiry. |
| OWASP protection (SQL injection, XSS) | PARTIALLY DONE | MongoDB (NoSQL) mitigates SQL injection. No explicit XSS protection (input sanitization, Content-Security-Policy headers). Spring Boot provides basic XSS protection via Jackson serialization. |
| OAuth/Spring Security | PARTIALLY DONE | Spring Security dependency included but auto-configuration DISABLED (`SecurityAutoConfiguration` excluded). No `SecurityFilterChain` configured. |
| Gateway JWT filter | NOT IMPLEMENTED | API Gateway has no `GatewayFilterFactory` or JWT authentication filter. Routes forward requests without authentication. **Critical security gap.** |

### Reliability

| Requirement | Status | Details |
|-------------|--------|---------|
| Circuit breaker | PARTIALLY DONE | Only on `loginService` in auth-service. SRS says "all critical services" should have circuit breaker. No circuit breaker on post-service, follow-service, or trending-service inter-service calls. |
| Fallback behaviors | PARTIALLY DONE | Only `loginFallback()` exists. No fallbacks on inter-service calls. |
| Minimal downtime | PARTIALLY DONE | Consul health checks configured. No retry mechanisms. |

### Session Management

| Requirement | Status | Details |
|-------------|--------|---------|
| Secure session management | PARTIALLY DONE | JWT-based (stateless). 24-hour token expiry configured. No token refresh mechanism. No token blacklisting/revocation. |
| Short-lived sessions with automatic timeouts | PARTIALLY DONE | 24h is relatively long for "short-lived." No sliding session or refresh token pattern. |

### UI/UX Requirements

| Requirement | Status | Notes |
|-------------|--------|-------|
| Intuitive interface, seamless experience | NOT APPLICABLE | Backend-only project currently. No frontend. |
| Responsive across devices (desktop, mobile, tablet) | NOT APPLICABLE | No frontend. |

---

## Section 3: Implementation Guidelines Compliance

### Lombok Usage
**Status: DONE**
- All entities, DTOs, and services use Lombok extensively: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@RequiredArgsConstructor`, `@Slf4j`.
- Used for logging (`@Slf4j`) throughout controllers, services, and aspects.

### Lambdas and Streams Usage
**Status: DONE**
- `FollowService.getFollowers()` and `getFollowing()` use `.stream().map().collect()`.
- `PostService.getPostsByUserId()` uses `posts.map(post -> ...)` on `Page<Post>`.
- `GlobalExceptionHandler` uses lambda in `forEach` for validation errors.

### Logical Layers (controller, service, DTO, entity)
**Status: DONE**
- All services follow: `controller/` -> `service/` -> `repository/` -> `entity/`
- DTOs in `dto/` package. Entities in `entity/` package.
- Exceptions in `exception/` package. Config in `config/` package.
- Validators in `validation/` package (auth-service).

### Package Naming
**Status: DONE**
- `com.instagram.auth`, `com.instagram.post`, `com.instagram.follow`, `com.instagram.trending`, `com.instagram.gateway`
- Meaningful, domain-reflecting package names.

### Spring Dependency Injection
**Status: DONE**
- Constructor injection via `@RequiredArgsConstructor` throughout.
- `@Bean` methods in `AppConfig` for `ModelMapper` and `PasswordEncoder`.
- `@Component`, `@Service`, `@Repository`, `@Configuration` properly used.

### Error Handling

| Requirement | Status | Details |
|-------------|--------|---------|
| Handle unauthorized access cases | PARTIALLY DONE | `UnauthorizedAccessException` exists in post-service. BUT no gateway JWT filter means all routes are publicly accessible. |
| Custom exceptions with user-defined messages | DONE | `UsernameAlreadyExistsException`, `EmailAlreadyExistsException`, `InvalidCredentialsException`, `UserNotFoundException`, `InvalidResetTokenException`, `PostNotFoundException`, `UnauthorizedAccessException`, `AlreadyLikedException`, `SelfFollowException`, `AlreadyFollowingException`, `NotFollowingException`. |
| Centralized exception handler | DONE | `@RestControllerAdvice GlobalExceptionHandler` in auth-service, post-service, and follow-service. Handles specific + generic exceptions. |
| LoggingAspect | DONE | AOP-based `LoggingAspect` in auth-service, post-service, and follow-service. `@Around` + `@AfterThrowing` on service layer methods. |
| Proper success/error responses | DONE | Appropriate HTTP status codes (201, 200, 204, 400, 401, 403, 404, 409, 500). Error responses include message, status, timestamp. |

**Note:** Trending-service has NO `LoggingAspect` since it only has a skeleton.

### DTOs and Mapping

| Requirement | Status | Details |
|-------------|--------|---------|
| DTOs for API request/response | DONE | All endpoints use DTOs. Entities never exposed directly. |
| Entities not exposed directly | DONE | `User` entity contains password; `UserResponseDTO` and `UserProfileResponseDTO` exclude it. |
| ModelMapper for entity-DTO conversion | DONE | `ModelMapper` bean in each service's `AppConfig`. Used in `AuthService.register()`, `PostService.mapToPostResponseDTO()`. |

### Database Interaction

| Requirement | Status | Details |
|-------------|--------|---------|
| Spring Data Repository | DONE | `UserRepository`, `PostRepository`, `LikeRepository`, `FollowRepository` all extend `MongoRepository`. |
| Appropriate DB properties | DONE | `application.yml` files configure MongoDB URIs per service with separate databases. |
| Indexes | DONE | `@Indexed(unique=true)` on User.email and User.username. `@CompoundIndex` on Like(postId, userId) and Follow(followerId, followingId). |

### API Design

| Requirement | Status | Details |
|-------------|--------|---------|
| Base URI mapping | DONE | `/api/auth`, `/api/posts`, `/api/follow`, `/api/trending`. |
| Server port configuration | DONE | auth:8081, post:8082, follow:8083, trending:8084, gateway:8080. |
| Appropriate HTTP methods | DONE | GET for reads, POST for creates, PUT for updates, DELETE for removals. |
| Appropriate status codes | DONE | 201 Created, 200 OK, 204 No Content, 400, 401, 403, 404, 409, 500. |

### Swagger Documentation
**Status: PARTIALLY DONE**
- `springdoc-openapi-starter-webmvc-ui` dependency present in all 4 service pom.xml files.
- However, NO Swagger annotations (`@Tag`, `@Operation`, `@ApiResponse`, `@Schema`) exist in ANY controller or DTO.
- Swagger UI will auto-generate basic docs from endpoints, but they lack descriptions, parameter docs, and response examples.
- Plan Target 16 covers this but is NOT yet implemented.

### Circuit Breaker Configuration (SRS: 3s timeout, 50% threshold, 10 failures, 60s open)

| SRS Requirement | Actual Implementation | Compliant? |
|-----------------|----------------------|------------|
| Timeout: 3 seconds | Not configured (no timeout, uses `slowCallDurationThreshold` default) | NO |
| Error threshold: 50% | `failureRateThreshold: 50` | YES |
| Open after 10 consecutive failures | `slidingWindowSize: 3`, `minimumNumberOfCalls: 3` | NO - opens after 3 calls, not 10 |
| Remain open for 60 seconds | `waitDurationInOpenState: 60000` | YES |
| Fallback mechanism | `loginFallback()` returns generic message | YES |
| All critical services | Only on auth-service login | NO - should be on inter-service calls too |

**Note:** The SRS says "10 consecutive failures" but the plan overrides this to 3 (to match US02 "3 failed attempts"). This is a deliberate deviation for login, but other services lack circuit breakers entirely.

### Validation

| Requirement | Status | Details |
|-------------|--------|---------|
| Bean validation for all inputs | DONE | `@Valid` on request bodies. `@NotBlank`, `@Size`, `@Pattern`, `@NotNull` on DTO fields. |
| Custom validators for complex validation | DONE | `@ValidFullName`, `@ValidEmailDomain`, `@PasswordMatch` with validator classes. |
| Null/empty value validation with message format | DONE | Messages follow format "Please provide a valid <attribute>" as specified in SRS. |
| Date/time values should not start with zero | NOT VERIFIED | No date/time input fields in current implementation. `@CreatedDate` is auto-generated by MongoDB auditing. |

### Testing

| Requirement | Status | Details |
|-------------|--------|---------|
| JUnit test cases for all service methods | PARTIALLY DONE | auth-service has 9 test files covering registration, login, password reset, validators, controller, and JwtUtil. post-service and follow-service have NO tests. |
| Mockito usage | DONE | `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`, `@MockBean` used properly. |
| 80% code coverage | UNKNOWN | No JaCoCo plugin configured in any pom.xml. Cannot verify coverage. Only auth-service has tests. |

### SonarQube Metrics

| Metric | Required | Status |
|--------|----------|--------|
| Security: A | NOT VERIFIED | No SonarQube configuration found. |
| Reliability: A | NOT VERIFIED | No SonarQube analysis. |
| Issues: <= 5 | NOT VERIFIED | No SonarQube analysis. |
| Coverage: >= 80% | NOT VERIFIED | No JaCoCo + SonarQube setup. |
| Duplications: <= 3% | NOT VERIFIED | Duplicate patterns visible (e.g., identical `LoggingAspect`, `GlobalExceptionHandler`, `ErrorResponseDTO` across services). |
| Security Hotspots: A | NOT VERIFIED | No SonarQube analysis. |

### Microservice Communication Patterns

| Requirement | Status | Details |
|-------------|--------|---------|
| Circuit breaker on all critical services (3s, 50%, 10, 60s) | PARTIALLY DONE | Only login circuit breaker. No circuit breaker on any inter-service call. |
| Fallback behaviors for all critical operations | PARTIALLY DONE | Only login fallback exists. |
| Two instances of any microservice + load balancing | NOT IMPLEMENTED | Gateway config supports `lb://` but no two instances have been set up. Plan Target 17 covers this. |
| Inter-service communication (REST) | NOT IMPLEMENTED | No `RestTemplate`, `WebClient`, or `FeignClient` found anywhere. Services are completely isolated. Plan Target 18 covers this. |

---

## Section 4: Gaps & Recommendations (Prioritized)

### Priority 1: Critical / Security

| # | Gap | SRS Reference | Recommendation |
|---|-----|---------------|----------------|
| 1 | **No JWT authentication filter in API Gateway** | Architecture: Gateway handles auth/authorization; US02 | Implement `GatewayFilterFactory` that validates JWT on all routes except `/api/auth/register`, `/api/auth/login`, `/api/auth/forgot-password`. Forward userId header to downstream services. This is the MOST critical gap. |
| 2 | **No CORS configuration** in API Gateway | Architecture: Gateway handles traffic management | Add `CorsConfiguration` bean in gateway for React frontend. |
| 3 | **No inter-service communication** | Architecture: microservices communicate via REST APIs | Implement `WebClient` (preferred for reactive gateway) or `RestTemplate` calls between services as planned in Target 18. |

### Priority 2: Major Functional Gaps

| # | Gap | SRS Reference | Recommendation |
|---|-----|---------------|----------------|
| 4 | **Trending service is only a skeleton** | US08: Exploring trending contents | Implement TrendingService, TrendingPost entity, trending algorithm, scheduled job, and TrendingController as planned in Target 13. |
| 5 | **Search functionality not implemented** | US06: Search specific users/hashtags | Implement text indexes and search endpoints in auth-service and post-service as planned in Target 15. |
| 6 | **Feed endpoint missing** | US03/US04: View posts from followed users | Implement post feed that fetches followed user IDs from follow-service and queries their posts (Target 18). |
| 7 | **Profile enrichment incomplete** | US05: Profile with post/follower/following counts | Auth-service profile returns hardcoded 0 for postCount, followerCount, followingCount. Needs inter-service calls (Target 18). |
| 8 | **File/media upload not implemented** | US03: Add photos/videos | Implement `POST /api/posts/upload` endpoint for actual file handling (Target 8, not yet done). |

### Priority 3: Non-Functional / Quality

| # | Gap | SRS Reference | Recommendation |
|---|-----|---------------|----------------|
| 9 | **Circuit breaker config mismatch** | Microservice Communication: 3s timeout, 10 failures | Update `slidingWindowSize` to 10 for general circuit breakers. Keep login at 3 (per US02). Add circuit breakers to all inter-service calls. |
| 10 | **No caching anywhere** | NFR: Implement caching mechanisms | Add Spring Cache with a provider (Caffeine or Redis) for frequently accessed data (user profiles, trending posts). |
| 11 | **Tests only for auth-service** | Testing: JUnit for all service methods, 80% coverage | Write unit tests for PostService, PostController, FollowService, FollowController (Targets 10, 12, 14). |
| 12 | **No JaCoCo plugin configured** | Code Quality: 80% coverage | Add `jacoco-maven-plugin` to parent pom.xml for coverage reports. |
| 13 | **No SonarQube integration** | Code Quality: SonarQube metrics table | Set up SonarQube and configure `sonar-maven-plugin` (Target 19). |
| 14 | **Swagger annotations missing** | API Design: Use Swagger for documentation | Add `@Tag`, `@Operation`, `@ApiResponse` to all controllers (Target 16). |

### Priority 4: Minor / Enhancement

| # | Gap | SRS Reference | Recommendation |
|---|-----|---------------|----------------|
| 15 | **No load balancing verification** | Microservice Communication: Two instances + load balancing | Run 2 instances of post-service and verify round-robin (Target 17). |
| 16 | **Comment/share functionality missing** | US03/US04: Interaction features | Not in current plan. Comments and shares would be additional features. |
| 17 | **Notifications system missing** | US05: Receive updates from followed users | Not in current plan. Would need a notification service or event-driven architecture. |
| 18 | **Bookmark/save searches missing** | US06: Save specific searches for quick access | Not in the plan. Would need a saved-search entity/endpoint. |
| 19 | **Photo/video editing features missing** | US03: Cropping, filters, brightness | Frontend-only feature. Not in the plan. |
| 20 | **Location-based trending filter missing** | US08: Filter by location | Not in the plan. Would need location data on posts. |

---

## Section 5: Plan Accuracy

### What the Plan Covers Well

1. **All 5 microservices** are identified with correct ports and databases.
2. **User registration (US01)** is thoroughly covered with all validations (Target 2).
3. **Login + JWT (US02)** including circuit breaker (Targets 3-4).
4. **Profile management** (Target 5).
5. **Post CRUD + likes (US03, US04, US07)** (Targets 8-9).
6. **Follow/unfollow (US05)** (Target 11).
7. **Trending (US08)** (Target 13).
8. **Search (US06)** (Target 15).
9. **Swagger, load balancing, integration, testing** (Targets 16-19).
10. **Implementation guidelines** (Lombok, ModelMapper, bean validation, LoggingAspect, custom validators) are all addressed.

### What the SRS Requires but the Plan Misses

| # | SRS Requirement | Plan Gap |
|---|-----------------|----------|
| 1 | **Save/bookmark searches** (US06) | Not mentioned anywhere in the plan. |
| 2 | **Comment functionality** (US03, US04, US07) | SRS mentions comments and interactions. Plan only covers likes. |
| 3 | **Share functionality** (US03) | SRS mentions "like, comment, share." Plan only covers likes. |
| 4 | **Notifications/updates** from followed users (US05) | SRS explicitly says "receive updates or notifications." Plan has no notification system. |
| 5 | **Real-time follower count updates** (US05) | SRS says "real-time updates on follower count." No WebSocket/SSE in the plan. |
| 6 | **Caching mechanisms** (NFR) | SRS explicitly requires caching. Plan does not address any caching strategy. |
| 7 | **Photo/video editing** (US03) | SRS mentions "cropping, applying filters, adjusting brightness." Plan does not cover this (frontend concern but not acknowledged). |
| 8 | **Circuit breaker config: 3s timeout + 10 failures** (Microservice Communication) | Plan uses 3 failures for login (matching US02) but does not address the general 10-failure/3s-timeout requirement for all services. |
| 9 | **Database DDL Script** (Final Deliverables) | SRS lists this as a deliverable. Plan does not mention generating DDL scripts (MongoDB collections are auto-created, but a script documenting schema would be needed). |
| 10 | **Sample screenshots** (Final Deliverables) | SRS lists this as a deliverable. Plan is backend-only, so no screenshots planned. |
| 11 | **OAuth** (Technology Stack) | SRS mentions "OAuth/Spring Security." Plan uses JWT but not OAuth2 flows. |
| 12 | **Location-based trending** (US08) | SRS mentions filtering by "location." Plan only covers recency, popularity, and hashtag filters. |

### Plan vs Implementation Progress

| Plan Target | Status |
|-------------|--------|
| Target 0: Environment Setup | DONE |
| Target 1: Project Scaffolding | DONE |
| Target 2: Auth - Registration | DONE |
| Target 3: Auth - Login + JWT | DONE |
| Target 4: Auth - Circuit Breaker | DONE |
| Target 5: Auth - Profile Management | DONE |
| Target 6: Auth - Unit Tests | DONE |
| Target 7: API Gateway Setup | PARTIALLY DONE (routes configured, JWT filter NOT implemented, CORS NOT configured) |
| Target 8: Post - Create & View | DONE (except file upload endpoint) |
| Target 9: Post - Like & Delete | DONE |
| Target 10: Post - Unit Tests | NOT STARTED |
| Target 11: Follow Service | DONE |
| Target 12: Follow - Unit Tests | NOT STARTED |
| Target 13: Trending Service | NOT STARTED (skeleton only) |
| Target 14: Trending - Unit Tests | NOT STARTED |
| Target 15: Search Functionality | NOT STARTED |
| Target 16: Swagger Documentation | NOT STARTED (dependency present, annotations missing) |
| Target 17: Load Balancing | NOT STARTED |
| Target 18: Integration Between Services | NOT STARTED |
| Target 19: Final Testing + SonarQube | NOT STARTED |

**Overall Progress: Targets 0-9 and 11 are substantially complete (~58% of the plan). Targets 7, 10, 12-19 remain.**

---

## Summary

The project has a solid foundation with auth-service, post-service, and follow-service functionally implemented with proper architectural patterns (Lombok, DTOs, ModelMapper, MongoDB repositories, custom validators, centralized error handling, AOP logging). The most critical gaps are:

1. **API Gateway JWT filter** -- without this, all routes are unprotected
2. **Inter-service communication** -- services are completely isolated
3. **Trending service** -- only a skeleton
4. **Search functionality** -- not started
5. **Testing for post-service and follow-service** -- no tests exist
6. **Caching** -- SRS requires it but neither plan nor code addresses it
7. **Comment/share features** -- SRS mentions them but plan omits them entirely
