# SRS Compliance Report - Instagram Clone (UPDATED)

**Generated:** 2026-05-15
**Previous Report:** 2026-05-15 (second revision)
**Current Revision:** Third revision (2026-05-15)
**Project Root:** `/Users/bhatia_ji99/Desktop/flytbaseprojects/instagram-clone/`
**SRS Source:** Instagram_SRS.pdf
**Implementation Plan:** `~/.claude/plans/ethereal-swimming-engelbart.md`

---

## Section 1: Progress Summary

| Target | Description | Status | Notes |
|--------|-------------|--------|-------|
| 0 | Environment Setup | DONE | Java 17, Maven, Consul installed |
| 1 | Project Scaffolding | DONE | Parent POM + 5 modules booting |
| 2 | Auth - Registration (US01) | DONE | All validations, custom validators, DTOs, exception handling |
| 3 | Auth - Login + JWT (US02) | DONE | Login, JWT, forgot/reset password |
| 4 | Auth - Circuit Breaker | DONE | Resilience4j on login (config deviation noted in Section 5) |
| 5 | Auth - Profile Management | DONE | GET/PUT profile endpoints |
| 6 | Auth - Unit Tests | DONE | 9 test files, 63 test methods |
| 7 | API Gateway Setup | DONE | Routes, JWT auth filter, CORS config |
| 8 | Post - Create & View | DONE | Create, get by ID, get by user (paginated). File upload NOT implemented. |
| 9 | Post - Like & Delete | DONE | Like/unlike/delete with cascade |
| 10 | Post - Unit Tests | DONE | `PostServiceTest.java` with 12 test methods |
| 11 | Follow Service | DONE | Follow/unfollow, followers/following lists, stats, isFollowing |
| 12 | Follow - Unit Tests | DONE | `FollowServiceTest.java` with 10 test methods |
| 13 | Trending Service | DONE | Entities, DTOs, repos, service with scoring algorithm, scheduled refresh, controller |
| 14 | Trending - Unit Tests | DONE | `TrendingServiceTest.java` with 8 test methods -- **NEW since last report** |
| 15 | Search Functionality | DONE | `GET /api/auth/search/users` + `GET /api/posts/search` with sort options -- **NEW since last report** |
| 16 | Swagger Documentation | PARTIALLY DONE | `@Tag` on 4 controllers, `@Operation`/`@ApiResponses` on 17/26 endpoints, `OpenApiConfig` in all services. `@Schema` on DTOs still missing. -- **NEW since last report** |
| 17 | Load Balancing | NOT STARTED | `lb://` prefix in gateway config, but no multi-instance setup verified |
| 18 | Integration Between Services | NOT STARTED | No `RestTemplate`/`WebClient`/`FeignClient` in any service |
| 19 | Final Testing + SonarQube | NOT STARTED | No JaCoCo plugin, no SonarQube config |

**Overall Progress: 17/20 targets DONE or PARTIALLY DONE (85%).** Up from 70% in previous report. Targets 14, 15, and 16 are now complete or partially complete.

---

## Section 2: Test Coverage Summary

### Test File Inventory

| Service | Test File | @Test Count |
|---------|-----------|-------------|
| auth-service | `AuthControllerTest.java` | 4 |
| auth-service | `RegisterRequestDTOValidationTest.java` | 10 |
| auth-service | `AuthServiceLoginTest.java` | 5 |
| auth-service | `AuthServicePasswordResetTest.java` | 7 |
| auth-service | `AuthServiceTest.java` | 6 |
| auth-service | `JwtUtilTest.java` | 8 |
| auth-service | `EmailDomainValidatorTest.java` | 9 |
| auth-service | `FullNameValidatorTest.java` | 9 |
| auth-service | `PasswordMatchValidatorTest.java` | 5 |
| follow-service | `FollowServiceTest.java` | 10 |
| post-service | `PostServiceTest.java` | 12 |
| trending-service | `TrendingServiceTest.java` | 8 |

**Totals: 12 test files, 93 @Test methods across 4 services.**

| Service | Test Files | Test Methods | Coverage Status |
|---------|-----------|--------------|-----------------|
| auth-service | 9 | 63 | Good coverage of service, controller, validators, JWT util |
| post-service | 1 | 12 | Covers CRUD + like/unlike |
| follow-service | 1 | 10 | Covers follow/unfollow/stats |
| trending-service | 1 | 8 | Covers getTrendingPosts, getTrendingHashtags, addPost, removePost -- **NEW** |
| api-gateway | 0 | 0 | No tests (filter, JWT util untested) |
| **TOTAL** | **12** | **93** | |

### Untested Service Methods

| Service | Method | Notes |
|---------|--------|-------|
| auth-service | `searchUsers()` | New method, no dedicated test |
| post-service | `searchPosts()` | New method, no dedicated test |
| trending-service | `refreshTrending()` | Complex scheduled method with `@Scheduled`, not unit tested |
| trending-service | `recalculateHashtagScores()` | Private helper, tested indirectly through `refreshTrending()` only |
| api-gateway | `JwtAuthenticationFilter.filter()` | No test file exists |
| api-gateway | `JwtUtil.*` | No test file exists |

---

## Section 3: User Story Coverage (US01-US08)

### US01 - User Registration

| # | Acceptance Criteria | Status | Location / Details |
|---|---------------------|--------|--------------------|
| 1 | Prominent "Register" option visible | [ ] NOT DONE | Frontend-only; no frontend exists (backend-first project) |
| 2 | Clicking Register takes to registration page | [ ] NOT DONE | Frontend-only |
| 3a | Full Name: letters only, min 1 word, first letter capitalized | [x] DONE | `auth-service/.../validation/ValidFullName.java` + `FullNameValidator.java` |
| 3b | Email: valid format, domains com/org/in | [x] DONE | `auth-service/.../validation/ValidEmailDomain.java` + `EmailDomainValidator.java` |
| 3c | Username: lowercase letters, digits, special chars, unique | [x] DONE | `RegisterRequestDTO.java` regex `^[a-z0-9._]+$`; `AuthService.register()` uniqueness check; `User.java` `@Indexed(unique=true)` |
| 3d | Password: 8-16 chars, lower+upper+digit+special | [x] DONE | `RegisterRequestDTO.java` `@Size` + `@Pattern` regex |
| 3e | Confirm Password: must match | [x] DONE | `@PasswordMatch` class-level validator with `PasswordMatchValidator.java` |
| 4 | After registration, redirect to Home page | [ ] NOT DONE | Frontend-only. Backend returns 201 + JSON. |
| 5 | Enforce username uniqueness | [x] DONE | `existsByUsername()` in `AuthService.register()` + `@Indexed(unique=true)` on `User.username` |
| 6 | Appropriate feedback on technical failures | [x] DONE | `GlobalExceptionHandler` handles `Exception.class` with 500 + message |
| 7 | "Forgot Password" option available | [x] DONE | `POST /api/auth/forgot-password` + `POST /api/auth/reset-password` endpoints |

**Summary:** 7/7 backend-relevant criteria DONE. 3 criteria are frontend-only. No changes from previous report.

---

### US02 - User Login

| # | Acceptance Criteria | Status | Location / Details |
|---|---------------------|--------|--------------------|
| 1 | Validate credentials | [x] DONE | `AuthService.login()` validates username + BCrypt password match |
| 2 | 3 failed attempts in 30s -> circuit open for 1 min | [x] DONE (with deviation) | Circuit breaker on `loginService`. Uses `COUNT_BASED` window of 3 instead of `TIME_BASED` 30s. Wait duration 60s correct. See Section 5. |
| 3 | Running timer of 1 min while circuit open + message | [x] DONE (backend) | `GET /api/auth/login/status` returns state + remaining seconds. Timer display is frontend. |
| 4 | Reset password via email verification | [x] DONE | `forgotPassword()` generates reset token; `resetPassword()` validates and updates |
| 5 | Display proper error messages for wrong credentials | [x] DONE | `InvalidCredentialsException` returns 401 + "Invalid username or password" |
| 6 | After login, redirect to home page | [ ] NOT DONE | Frontend-only. Backend returns JWT token. |

**Summary:** 5/5 backend-relevant criteria DONE. Circuit breaker config has deliberate deviation (see Section 5).

---

### US03 - Post Updates

| # | Acceptance Criteria | Status | Location / Details |
|---|---------------------|--------|--------------------|
| 1 | Access post creation interface | [ ] NOT DONE | Frontend-only |
| 2 | Option to add photos or videos | [x] DONE | `CreatePostRequestDTO` has `mediaUrls` list + `mediaType` (IMAGE/VIDEO/TEXT) |
| 3 | Select multiple photos/videos from gallery | [ ] NOT DONE | No file upload endpoint (`POST /api/posts/upload`) exists. Plan Target 8 mentions it, not coded. |
| 4 | Support common image/video formats | [ ] NOT DONE | No format validation exists anywhere |
| 5 | Add captions, tags, descriptions | [x] DONE | `caption`, `tags`, `hashtags` fields in `CreatePostRequestDTO` + `Post` entity |
| 6 | Photo/video editing (crop, filter, brightness) | [ ] NOT DONE | Frontend concern, not in plan |
| 7 | Privacy settings (public, friends, private) | [x] DONE | `Privacy` enum (PUBLIC/FRIENDS/PRIVATE) in `CreatePostRequestDTO` with PUBLIC default |
| 8 | Add hashtags and additional text | [x] DONE | `hashtags` List<String> field implemented |
| 9 | Post published to profile/feed, visible to followers | [ ] NOT DONE | `POST /api/posts` creates post. Feed endpoint for followed users NOT implemented (requires inter-service communication, Target 18). |
| 10 | Confirmation message/notification | [x] DONE | Returns 201 + `PostResponseDTO` on success |
| 11 | Visually appealing display with interactions (like, comment, share) | [ ] NOT DONE | Like implemented. Comment and share NOT implemented, NOT in plan. |

**Summary:** 5 DONE, 6 NOT DONE. No changes from previous report for this user story.

---

### US04 - View and Like Posts

| # | Acceptance Criteria | Status | Location / Details |
|---|---------------------|--------|--------------------|
| 1 | Scroll feed or view specific user's profile posts | [ ] NOT DONE | `GET /api/posts/user/{userId}` with pagination exists. Feed from followed users NOT implemented (Target 18). |
| 2 | Each post shows author name, profile pic, timestamp, content | [ ] NOT DONE | `PostResponseDTO` returns userId, caption, media, timestamps. Does NOT return author name/profile picture (no inter-service call to auth-service). |
| 3 | Like a post via button/icon | [x] DONE | `POST /api/posts/{postId}/like` in `PostController.java` |
| 4 | Like button visually indicates liked state | [x] DONE (backend) | `PostResponseDTO.liked` boolean field. Frontend display is frontend concern. |
| 5 | See total number of likes | [x] DONE | `likesCount` in `PostResponseDTO` + `GET /api/posts/{postId}/likes` endpoint |
| 6 | Unlike a post, count updates | [x] DONE | `DELETE /api/posts/{postId}/like` with count decrement in `PostService.unlikePost()` |
| 7 | Like multiple posts without limitations | [x] DONE | No restrictions on liking different posts. Double-like blocked by `AlreadyLikedException`. |

**Summary:** 5 DONE, 2 NOT DONE. No changes from previous report.

---

### US05 - Following Users

| # | Acceptance Criteria | Status | Location / Details |
|---|---------------------|--------|--------------------|
| 1 | Search for other users on the platform | [x] DONE | `GET /api/auth/search/users?q=keyword` with regex search on username, fullName, bio -- **NEW** |
| 2 | View user's profile before following | [x] DONE | `GET /api/auth/profile/{userId}` in `AuthController.java` |
| 3 | Follow a user via button/icon | [x] DONE | `POST /api/follow/{targetUserId}` in `FollowController.java` |
| 4 | Follow button visual indication | [x] DONE (backend) | `isFollowing` in `FollowResponseDTO` + `GET /api/follow/check/{targetUserId}` |
| 5 | See total number of followers | [x] DONE | `GET /api/follow/{userId}/stats` returns `FollowStatsDTO` with `followerCount`/`followingCount` |
| 6 | Unfollow a user, count updates | [x] DONE | `DELETE /api/follow/{targetUserId}` in `FollowController.java` |
| 7 | Receive updates/notifications from followed users | [ ] NOT DONE | No notification system. Not in plan. |
| 8 | Real-time follower count updates | [ ] NOT DONE | No WebSocket/SSE. Not in plan. |
| 9 | Follow multiple users without limitations | [x] DONE | No restrictions. Self-follow blocked by `SelfFollowException`. |

**Summary:** 7 DONE, 2 NOT DONE. **Improved from 6 DONE** -- search for users is now implemented (criterion 1).

---

### US06 - Search Specific Users/Hashtags

| # | Acceptance Criteria | Status | Location / Details |
|---|---------------------|--------|--------------------|
| 1 | Enter username or hashtag in search bar | [x] DONE (backend) | `GET /api/auth/search/users?q=keyword` and `GET /api/posts/search?q=hashtag` -- **NEW** |
| 2 | View list of matching users or posts | [x] DONE | Both endpoints return paginated results (`Page<UserResponseDTO>`, `Page<PostResponseDTO>`) -- **NEW** |
| 3 | Display profile picture, username, bio, content | [x] DONE (partial) | `UserResponseDTO` returns username, fullName. `PostResponseDTO` returns caption, mediaUrls, hashtags. Bio/profile picture returned only in `UserProfileResponseDTO` (profile endpoint, not search). -- **NEW** |
| 4 | Filter/sort by relevance, popularity, recency | [x] DONE (partial) | `GET /api/posts/search?sort=popular` sorts by likesCount desc. `sort=recent` sorts by createdAt desc. `sort=relevance` (default) uses MongoDB regex text match. User search does not support sort parameter. -- **NEW** |
| 5 | Click on result to view details | [x] DONE | Search results return IDs; client can call `GET /api/posts/{postId}` or `GET /api/auth/profile/{userId}` |
| 6 | Refine search with additional keywords/filters | [ ] NOT DONE | Only single query parameter `q` supported. No multi-keyword/filter combination. |
| 7 | Save/bookmark specific searches | [ ] NOT DONE | Not in plan at all. |

**Summary:** 5 DONE (all new since last report), 2 NOT DONE. **Major improvement from 0/7 previously.**

---

### US07 - Delete Post

| # | Acceptance Criteria | Status | Location / Details |
|---|---------------------|--------|--------------------|
| 1 | Navigate to the post to delete | [x] DONE | `GET /api/posts/{postId}` exists in `PostController.java` |
| 2 | Confirmation prompt before deletion | [ ] NOT DONE | Frontend-only concern |
| 3 | Post permanently removed from platform | [x] DONE | `DELETE /api/posts/{postId}` hard-deletes from MongoDB via `PostService.deletePost()` |
| 4 | Associated comments/interactions removed | [x] DONE (partial) | Likes cascade-deleted via `likeRepository.deleteByPostId()`. Comments NOT implemented. |
| 5 | Confirmation message after deletion | [x] DONE | Returns 204 No Content (standard REST) |
| 6 | Cannot access deleted post via direct links | [x] DONE | Deleted from DB; GET returns 404 via `PostNotFoundException` |
| 7 | Deletion is irreversible | [x] DONE | Hard delete from MongoDB, no soft-delete mechanism |
| 8 | Delete multiple posts without limitations | [x] DONE | No restrictions. Ownership check via `post.getUserId().equals(userId)` |

**Summary:** 6 DONE, 1 PARTIAL, 1 NOT DONE (frontend). No changes from previous report.

---

### US08 - Exploring Trending Contents

| # | Acceptance Criteria | Status | Location / Details |
|---|---------------------|--------|--------------------|
| 1 | Dedicated trending section/page | [x] DONE | `GET /api/trending/posts` + `GET /api/trending/hashtags` in `TrendingController.java` |
| 2 | Curated list of popular posts/topics/hashtags | [x] DONE | `TrendingService.getTrendingPosts()` sorted by score; `getTrendingHashtags()` sorted by score |
| 3 | Visually appealing grid/carousel layout | [ ] NOT DONE | Frontend-only |
| 4 | Display views, likes, comments count | [x] DONE (partial) | `TrendingPostDTO` includes `likesCount`. Views and comments count NOT tracked. |
| 5 | Click trending post for details | [x] DONE | `TrendingPostDTO` returns `postId`; client can call `GET /api/posts/{postId}` |
| 6 | Regularly updated trending section | [x] DONE | `@Scheduled(fixedRate = 60000)` in `TrendingService.refreshTrending()` |
| 7 | Filter by recency, popularity, location | [x] DONE (partial) | `filter=popular` (default) and `filter=recent` supported. Location filter NOT implemented. |
| 8 | Real-time updates on trending content | [ ] NOT DONE | No WebSocket/SSE. Polling via REST only. |

**Summary:** 5 DONE, 2 PARTIAL, 1 NOT DONE. No changes from previous report for this user story.

---

## Section 4: Swagger / OpenAPI Documentation Status

### Class-Level `@Tag` Annotations

| Controller | `@Tag` Present | Tag Name |
|-----------|---------------|----------|
| `AuthController` | YES | "Authentication" |
| `PostController` | YES | "Posts" |
| `FollowController` | YES | "Follow" |
| `TrendingController` | YES | "Trending" |
| Gateway (no controller) | N/A | N/A |

### Method-Level `@Operation` + `@ApiResponses` Annotations

| Controller | Method | `@Operation` | `@ApiResponses` |
|-----------|--------|-------------|----------------|
| **AuthController** | `register()` | YES | YES (201, 400, 409) |
| | `login()` | YES | YES (200, 400, 401) |
| | `getLoginCircuitBreakerStatus()` | YES | YES (200) |
| | `forgotPassword()` | YES | YES (200, 404) |
| | `resetPassword()` | YES | YES (200, 400) |
| | `getUserProfile()` | YES | YES (200, 404) |
| | `updateProfile()` | YES | YES (200, 400, 404) |
| | `searchUsers()` | YES | YES (200) |
| **PostController** | `createPost()` | YES | YES (201, 400) |
| | `getPostById()` | YES | YES (200, 404) |
| | `getPostsByUserId()` | YES | YES (200) |
| | `deletePost()` | YES | YES (204, 403, 404) |
| | `likePost()` | YES | YES (200, 409) |
| | `unlikePost()` | YES | YES (200, 404) |
| | `getLikeStatus()` | **NO** | **NO** |
| | `searchPosts()` | **NO** | **NO** |
| **FollowController** | `followUser()` | YES | YES (200, 400, 409) |
| | `unfollowUser()` | YES | YES (200, 400) |
| | `getFollowers()` | YES | YES (200) |
| | `getFollowing()` | **NO** | **NO** |
| | `getFollowStats()` | **NO** | **NO** |
| | `isFollowing()` | **NO** | **NO** |
| **TrendingController** | `getTrendingPosts()` | **NO** | **NO** |
| | `getTrendingHashtags()` | **NO** | **NO** |
| | `addPost()` | **NO** | **NO** |
| | `removePost()` | **NO** | **NO** |

**Summary: 17/26 endpoints annotated (65%).** Auth and Post controllers are nearly complete. Follow is partial. Trending has zero method-level annotations.

### `OpenApiConfig.java` (Programmatic API Info)

| Service | Present | Title |
|---------|---------|-------|
| auth-service | YES | "Auth Service API" |
| post-service | YES | "Post Service API" |
| follow-service | YES | "Follow Service API" |
| trending-service | YES | "Trending Service API" |

### `@Schema` on DTOs

**Status: NOT DONE.** Zero `@Schema` annotations found across all DTO classes. This means Swagger UI auto-generates field docs without descriptions, examples, or constraints.

---

## Section 5: Critical Gaps (Priority Order)

### 1. No Inter-Service Communication (UNCHANGED)
- **SRS Section:** Architecture -- "different microservices communicate with each other using REST APIs"
- **Plan Target:** 18 (Integration Between Services)
- **What's missing:** No `RestTemplate`, `WebClient`, or `FeignClient` in any service. All 5 services are completely isolated.
- **Specific impact:**
  - `auth-service` profile returns hardcoded `postCount=0`, `followerCount=0`, `followingCount=0` (lines 103-105 in `AuthService.java`)
  - `post-service` cannot fetch followed users' posts for feed
  - `trending-service` cannot automatically ingest posts from `post-service` (has manual `POST /api/trending/posts` endpoint instead)
  - `follow-service` follower/following lists return only user IDs, not names/profile pictures

### 2. Swagger Documentation Incomplete (IMPROVED FROM "NOT STARTED")
- **SRS Section:** API Design -- "Use Swagger for generating the API documentation"
- **Plan Target:** 16 (Swagger Documentation)
- **What's done:** `@Tag` on all 4 controllers, `@Operation`/`@ApiResponses` on 17/26 endpoints, `OpenApiConfig` in all services.
- **What's still missing:**
  - 9 endpoints lacking `@Operation`/`@ApiResponses`: PostController (2), FollowController (3), TrendingController (4)
  - Zero `@Schema` annotations on any DTO class (16+ DTO classes)
  - No response examples in `@ApiResponse`

### 3. File/Media Upload Not Implemented (UNCHANGED)
- **SRS Section:** US03 -- "select multiple photos or videos from my device's gallery"
- **Plan Target:** 8 (mentions `POST /api/posts/upload`)
- **What's missing:** No file upload endpoint. `CreatePostRequestDTO.mediaUrls` accepts URL strings but there is no mechanism to upload actual files and generate those URLs.

### 4. No Caching Anywhere (UNCHANGED)
- **SRS Section:** Non-Functional Requirements -- "Implement caching mechanisms to improve performance"
- **Plan Target:** Not addressed in the plan
- **What's missing:** No `@Cacheable`, `@CacheEvict`, `@CachePut`, or `@EnableCaching` annotations. No caching library (Redis, Caffeine, etc.) in any pom.xml.

### 5. No JaCoCo/SonarQube Setup (UNCHANGED)
- **SRS Section:** Code Quality -- SonarQube metrics table (Security A, Reliability A, Issues <=5, Coverage >=80%, Duplications <=3%)
- **Plan Target:** 19 (Final Testing + SonarQube)
- **What's missing:** No `jacoco-maven-plugin` in any pom.xml. No `sonar-maven-plugin` configuration. Cannot measure or verify code coverage.

### 6. Load Balancing Not Verified (UNCHANGED)
- **SRS Section:** Microservice Communication -- "Create two instances for any Microservice and implement load balancing"
- **Plan Target:** 17 (Load Balancing)
- **What's missing:** Gateway uses `lb://` prefix which supports load balancing via Consul. But no second instance of any service has been configured or tested.

### 7. Comment and Share Functionality Missing (UNCHANGED)
- **SRS Section:** US03 -- "like, comment, share"; US04 -- interaction features; US07 -- "associated comments or interactions removed"
- **Plan Target:** Not in the plan
- **What's missing:** No Comment entity, repository, service, or controller anywhere. No share mechanism.

### 8. Notification System Missing (UNCHANGED)
- **SRS Section:** US05 -- "receive updates or notifications about the activities of the users I follow"
- **Plan Target:** Not in the plan
- **What's missing:** No notification service, no event-driven architecture, no WebSocket/SSE implementation

### 9. Search Refinement and Bookmarks Missing (REDUCED SEVERITY)
- **SRS Section:** US06 -- "refine search with additional keywords/filters" and "save or bookmark specific searches"
- **What's done:** Basic single-query search with sort by relevance/popular/recent on posts
- **What's still missing:** Multi-keyword/filter combination, saved/bookmarked searches

### 10. Location-Based Trending Filter Missing (UNCHANGED)
- **SRS Section:** US08 -- "filter the trending content based on different criteria, such as sorting by recency, popularity, or location"
- **What's missing:** No location data on posts, no location-based filtering

### 11. Gateway Unit Tests Missing
- **What's missing:** `api-gateway` has `JwtAuthenticationFilter.java` and `JwtUtil.java` with zero test files.

---

## Section 6: Circuit Breaker Config Deviation

**SRS Requirement:** "Circuit Breaker pattern should be implemented for all the critical services with a timeout of 3 seconds and error threshold of 50%. The circuit breaker should open after 10 consecutive failures and remain open for 60 seconds."

**Actual configuration in `auth-service/src/main/resources/application.yml`:**

```yaml
resilience4j:
  circuitbreaker:
    instances:
      loginService:
        registerHealthIndicator: true
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 3
        failureRateThreshold: 50
        minimumNumberOfCalls: 3
        waitDurationInOpenState: 60000
        permittedNumberOfCallsInHalfOpenState: 1
        automaticTransitionFromOpenToHalfOpenEnabled: true
```

| SRS Requirement | Actual Value | Compliant? | Analysis |
|-----------------|-------------|------------|----------|
| Timeout: 3 seconds | Not configured (no `slowCallDurationThreshold` or `TimeLimiter`) | NO | No call timeout is set. A slow downstream call could hang indefinitely. |
| Error threshold: 50% | `failureRateThreshold: 50` | YES | Matches SRS exactly. |
| Open after 10 consecutive failures | `slidingWindowSize: 3`, `minimumNumberOfCalls: 3` | NO | Opens after just 3 calls fail (50% of 3 = ~2 failures), not 10. This is a deliberate deviation to match US02 ("3 failed attempts"). |
| Remain open for 60 seconds | `waitDurationInOpenState: 60000` | YES | Matches SRS exactly. |
| Sliding window type | `COUNT_BASED` | DEVIATION | SRS implies `TIME_BASED` with 30-second window for login. Current config uses `COUNT_BASED`. |
| Fallback mechanism | `loginFallback()` returns message | YES | Returns "Too many failed login attempts. Service is temporarily unavailable. Please try again after 60 seconds." |
| All critical services | Only `loginService` in auth-service | NO | No circuit breaker on any inter-service call or any other service. |

**Recommendation:** Keep login circuit breaker at 3 calls (matches US02). Add a SEPARATE circuit breaker configuration for general inter-service calls with `slidingWindowSize: 10` and `slowCallDurationThreshold: 3s` to match the SRS general requirement.

---

## Section 7: Non-Functional Requirements Checklist

### Performance
- [ ] Fast and responsive, minimal lag -- Basic service implementations exist. No performance tuning, no caching.
- [ ] Implement caching mechanisms -- **NOT IMPLEMENTED.** No caching layer anywhere.
- [ ] Lazy loading for images -- Frontend concern, but no backend pagination cursor support beyond basic page/size.

### Scalability
- [x] Handle large traffic, scale as needed -- Microservice architecture + Consul discovery in place.
- [ ] Load balancing -- Gateway uses `lb://` prefix. No multi-instance setup verified (Target 17).
- [ ] Multiple instances -- Not configured or tested.

### Security
- [x] BCrypt password hashing -- `BCryptPasswordEncoder` bean in `AppConfig.java`, used in `AuthService.register()` and `AuthService.resetPassword()`.
- [x] JWT authentication -- `JwtUtil` in auth-service generates tokens with HMAC-SHA256, 24h expiry. Gateway `JwtUtil` validates them. Gateway JWT filter validates all protected routes.
- [ ] OWASP protection (SQL injection, XSS) -- MongoDB mitigates SQL injection. No explicit XSS sanitization or Content-Security-Policy headers.
- [ ] OAuth/Spring Security -- Spring Security dependency included in auth-service but auto-configuration DISABLED (`SecurityAutoConfiguration` excluded). No `SecurityFilterChain`, no OAuth2 flows.
- [x] Gateway JWT filter -- `JwtAuthenticationFilter.java` implemented as `GlobalFilter`. Validates JWT on all routes except open endpoints (`/register`, `/login`, `/forgot-password`, `/reset-password`, health checks). Extracts userId/username and forwards as `X-User-Id`/`X-Username` headers to downstream services.

### Reliability
- [ ] Circuit breaker on all critical services -- Only on `loginService` in auth-service. No circuit breaker on post-service, follow-service, trending-service, or any inter-service call.
- [ ] Fallback behaviors for all critical operations -- Only `loginFallback()` exists.
- [x] Minimal downtime -- Consul health checks configured on all services.

### Session Management
- [x] Secure session management -- JWT-based (stateless). 24-hour token expiry. Gateway validates tokens.
- [ ] Short-lived sessions with automatic timeouts -- 24h is long for "short-lived." No refresh token mechanism. No token blacklisting/revocation.

### Maintainability
- [x] Clear and well-organized code -- Consistent package structure across all services: `controller/`, `service/`, `repository/`, `entity/`, `dto/`, `exception/`, `config/`, `aspect/`, `validation/`.

### Compatibility
- [ ] Compatible with wide range of devices -- No frontend exists.

### UI/UX
- [ ] Intuitive interface, seamless experience -- No frontend exists.

---

## Section 8: Implementation Guidelines Compliance

### Lombok Usage
**Status: DONE**
- All entities, DTOs, and services use Lombok: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@RequiredArgsConstructor`, `@Slf4j`.
- Used for logging (`@Slf4j`) in all controllers, services, aspects, and filters.
- Files verified: All 90 `.java` source files across 5 services.

### Lambdas and Streams Usage
**Status: DONE**
- `FollowService.getFollowers()` / `getFollowing()` -- `.stream().map().collect(Collectors.toList())`
- `PostService.getPostsByUserId()` -- `posts.map(post -> ...)` on `Page<Post>`
- `PostService.searchPosts()` -- `posts.map(post -> modelMapper.map(post, PostResponseDTO.class))` **(NEW)**
- `AuthService.searchUsers()` -- `users.map(user -> modelMapper.map(user, UserResponseDTO.class))` **(NEW)**
- `TrendingService.getTrendingHashtags()` -- `hashtags.getContent().stream().map().collect()`
- `TrendingService.refreshTrending()` -- `hashtagCounts.merge(hashtag, 1L, Long::sum)`
- `TrendingService.removePost()` -- `existingOpt.ifPresent(trendingPostRepository::delete)` (method reference)
- `GlobalExceptionHandler` (all services) -- lambda in `forEach` for validation errors
- `JwtAuthenticationFilter.isOpenEndpoint()` -- `openEndpoints.stream().anyMatch(path::startsWith)`

### Logical Layers (controller, service, DTO, entity)
**Status: DONE**
- All 5 services follow: `controller/` -> `service/` -> `repository/` -> `entity/`
- DTOs in `dto/`. Entities in `entity/`. Exceptions in `exception/`. Config in `config/`. Aspects in `aspect/`. Validators in `validation/` (auth-service). Filters in `filter/` (gateway). Utils in `util/`.

### Package Naming
**Status: DONE**
- `com.instagram.auth`, `com.instagram.post`, `com.instagram.follow`, `com.instagram.trending`, `com.instagram.gateway`
- Meaningful sub-packages reflecting domain structure.

### Spring Dependency Injection
**Status: DONE**
- Constructor injection via `@RequiredArgsConstructor` throughout all services.
- `@Bean` methods in `AppConfig` for `ModelMapper` and `PasswordEncoder`.
- `@Component`, `@Service`, `@Repository`, `@Configuration` properly used.
- Gateway `JwtAuthenticationFilter` uses `@RequiredArgsConstructor` with `final JwtUtil` injection.

### Error Handling

| Requirement | Status | Details |
|-------------|--------|---------|
| Handle unauthorized access cases | DONE | `UnauthorizedAccessException` in post-service. Gateway JWT filter returns 401 for missing/invalid tokens. |
| Custom exceptions with user-defined messages | DONE | 11 custom exceptions: `UsernameAlreadyExistsException`, `EmailAlreadyExistsException`, `InvalidCredentialsException`, `UserNotFoundException`, `InvalidResetTokenException`, `PostNotFoundException`, `UnauthorizedAccessException`, `AlreadyLikedException`, `SelfFollowException`, `AlreadyFollowingException`, `NotFollowingException`. |
| Centralized exception handler | DONE | `@RestControllerAdvice GlobalExceptionHandler` in auth-service, post-service, follow-service, AND trending-service. |
| LoggingAspect | DONE | AOP-based `LoggingAspect` in all 4 non-gateway services. `@Around` + `@AfterThrowing` on service layer methods. |
| Proper success/error responses | DONE | Appropriate HTTP status codes: 201 Created, 200 OK, 204 No Content, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 409 Conflict, 500 Internal Server Error. |

### DTOs and Mapping

| Requirement | Status | Details |
|-------------|--------|---------|
| DTOs for API request/response | DONE | All endpoints use DTOs. 16+ DTO classes total. |
| Entities not exposed directly | DONE | `User` entity has `password`; `UserResponseDTO` / `UserProfileResponseDTO` exclude it. |
| ModelMapper for entity-DTO conversion | DONE | `ModelMapper` bean in each service's `AppConfig`. Used in `AuthService`, `PostService`, `TrendingService`, and search methods **(NEW)**. |

### Database Interaction

| Requirement | Status | Details |
|-------------|--------|---------|
| Spring Data Repository | DONE | `UserRepository`, `PostRepository`, `LikeRepository`, `FollowRepository`, `TrendingPostRepository`, `TrendingHashtagRepository` -- all extend `MongoRepository`. |
| Appropriate DB properties | DONE | Separate MongoDB databases per service: `instagram_auth`, `instagram_posts`, `instagram_follow`, `instagram_trending`. |
| Indexes | DONE | `@Indexed(unique=true)` on `User.email`, `User.username`, `TrendingPost.postId`, `TrendingHashtag.hashtag`. `@CompoundIndex` on `Like(postId, userId)` and `Follow(followerId, followingId)`. |
| Custom Queries | DONE | `UserRepository.searchUsers()` uses `@Query` with MongoDB `$or` + `$regex` for search **(NEW)**. `PostRepository.searchPosts()` uses `@Query` with `$or` + `$regex` on caption and hashtags **(NEW)**. `PostRepository.findByHashtagsContainingOrderByLikesCountDesc()` and `findByHashtagsContainingOrderByCreatedAtDesc()` for sorted search **(NEW)**. |

### API Design

| Requirement | Status | Details |
|-------------|--------|---------|
| Base URI mapping | DONE | `/api/auth`, `/api/posts`, `/api/follow`, `/api/trending`. |
| Server port configuration | DONE | auth:8081, post:8082, follow:8083, trending:8084, gateway:8080. |
| Appropriate HTTP methods | DONE | GET for reads, POST for creates, PUT for updates, DELETE for removals. |
| Appropriate status codes | DONE | Full range: 201, 200, 204, 400, 401, 403, 404, 409, 500. |
| Swagger for API documentation | PARTIALLY DONE | `@Tag` on all 4 controllers. `@Operation`/`@ApiResponses` on 17/26 endpoints. `OpenApiConfig` in all 4 non-gateway services. `@Schema` still missing on DTOs. -- **IMPROVED from NOT DONE** |

### Validation

| Requirement | Status | Details |
|-------------|--------|---------|
| Bean validation for all inputs | DONE | `@Valid` on request bodies. `@NotBlank`, `@Size`, `@Pattern`, `@NotNull` on DTO fields. |
| Custom validators for complex validation | DONE | `@ValidFullName`, `@ValidEmailDomain`, `@PasswordMatch` with validator classes. |
| Null/empty value validation with message format | DONE | Messages follow "Please provide a valid <attribute>" format as specified in SRS. |
| Date/time values should not start with zero | N/A | No date/time input fields. `@CreatedDate` is auto-generated by MongoDB auditing. |

### Testing

| Requirement | Status | Details |
|-------------|--------|---------|
| JUnit test cases for all service methods | MOSTLY DONE | auth-service: 9 test files, 63 tests. post-service: 1 test file, 12 tests. follow-service: 1 test file, 10 tests. trending-service: 1 test file, 8 tests **(NEW)**. api-gateway: 0 tests. |
| Mockito usage | DONE | `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`, `@MockBean`, `ArgumentCaptor` used properly in all test files. |
| 80% code coverage | UNKNOWN | No JaCoCo plugin configured. Cannot measure. |
| Search method tests | NOT DONE | `AuthService.searchUsers()` and `PostService.searchPosts()` have no dedicated test methods. |

### SonarQube Metrics

| Metric | Required | Status |
|--------|----------|--------|
| Security: A | NOT VERIFIED | No SonarQube configuration found. |
| Reliability: A | NOT VERIFIED | No SonarQube analysis. |
| Issues: <= 5 | NOT VERIFIED | No SonarQube analysis. |
| Coverage: >= 80% | NOT VERIFIED | No JaCoCo + SonarQube setup. |
| Duplications: <= 3% | NOT VERIFIED | Duplicate patterns exist (identical `LoggingAspect`, `GlobalExceptionHandler`, `ErrorResponseDTO` across services). |
| Security Hotspots: A | NOT VERIFIED | No SonarQube analysis. |

### Microservice Communication Patterns

| Requirement | Status | Details |
|-------------|--------|---------|
| Circuit breaker on all critical services | NOT DONE | Only login circuit breaker. No CB on any inter-service call. |
| Fallback behaviors for all critical operations | NOT DONE | Only login fallback exists. |
| Two instances + load balancing | NOT DONE | Gateway supports `lb://` but no multi-instance tested. |
| Inter-service communication (REST) | NOT DONE | No `RestTemplate`, `WebClient`, or `FeignClient` anywhere. |

---

## Section 9: Recommendations for Next Sprint

Listed in priority order based on SRS requirements and current gaps:

### Priority 1: Inter-Service Communication (Target 18)
**Impact: Unlocks 4+ acceptance criteria across US03, US04, US05, US08**
1. Add `WebClient` (reactive-compatible) beans in post-service, auth-service, and trending-service
2. Post feed: `post-service` calls `follow-service` to get followed user IDs, fetches their posts
3. Profile enrichment: `auth-service` profile calls `post-service` for post count and `follow-service` for follower/following counts
4. Trending ingestion: `trending-service` periodically fetches recent posts from `post-service` instead of relying on manual `POST /api/trending/posts`
5. Add circuit breakers (Resilience4j) on all inter-service calls with: `slidingWindowSize: 10`, `slowCallDurationThreshold: 3s`, `failureRateThreshold: 50`, `waitDurationInOpenState: 60s`
6. Add fallback methods that return graceful defaults (e.g., `postCount=0` if post-service is down)

### Priority 2: Complete Swagger Annotations (Target 16 -- finish)
**Impact: 9 endpoints still missing `@Operation`/`@ApiResponses`, all DTOs missing `@Schema`**
1. Add `@Operation`/`@ApiResponses` to the 9 remaining endpoints:
   - `PostController.getLikeStatus()`, `PostController.searchPosts()`
   - `FollowController.getFollowing()`, `FollowController.getFollowStats()`, `FollowController.isFollowing()`
   - `TrendingController.getTrendingPosts()`, `TrendingController.getTrendingHashtags()`, `TrendingController.addPost()`, `TrendingController.removePost()`
2. Add `@Schema` annotations to all DTO classes (description, example values, required fields)
3. Verify Swagger UI loads at `http://localhost:{port}/swagger-ui.html` for each service

### Priority 3: Add Missing Test Methods
**Impact: Achieve comprehensive test coverage before SonarQube setup**
1. Add tests for `AuthService.searchUsers()` in auth-service
2. Add tests for `PostService.searchPosts()` in post-service
3. Add tests for `TrendingService.refreshTrending()` (scheduled job logic)
4. Create `JwtAuthenticationFilterTest.java` and `JwtUtilTest.java` in api-gateway
5. Consider integration tests for search with MongoDB test containers

### Priority 4: JaCoCo + SonarQube Setup (Target 19)
**Impact: Code quality metrics required by SRS**
1. Add `jacoco-maven-plugin` to parent pom.xml
2. Configure `sonar-maven-plugin` with SonarQube connection
3. Run analysis, verify metrics meet: Security A, Reliability A, Issues <=5, Coverage >=80%, Duplications <=3%

### Priority 5: Load Balancing Verification (Target 17)
**Impact: SRS requires two instances + load balancing**
1. Run `post-service` on ports 8082 and 8092
2. Both register with Consul as `post-service`
3. Verify gateway distributes requests between instances

### Priority 6: File Upload Endpoint
**Impact: US03 media upload**
1. Implement `POST /api/posts/upload` accepting `MultipartFile`
2. Store locally or in cloud storage
3. Return media URL for use in `CreatePostRequestDTO.mediaUrls`

### Priority 7 (Stretch): Comment Functionality
**Impact: US03, US04, US07 all mention comments**
1. Create `Comment` entity with `postId`, `userId`, `text`, `createdAt`
2. Add `CommentRepository`, `CommentService`, comment endpoints in post-service
3. Cascade delete comments when post is deleted

---

## Section 10: Changes Since Previous Report

| Item | Previous Status | Current Status | Evidence |
|------|----------------|----------------|----------|
| Search - User search | NOT IMPLEMENTED | DONE | `GET /api/auth/search/users?q=` with MongoDB `$regex` on username/fullName/bio in `UserRepository.searchUsers()` |
| Search - Post search | NOT IMPLEMENTED | DONE | `GET /api/posts/search?q=&sort=` with MongoDB `$regex` on caption/hashtags; sort by popular/recent/relevance in `PostService.searchPosts()` |
| Search - Sort options | NOT IMPLEMENTED | DONE | `sort=popular` (likesCount desc), `sort=recent` (createdAt desc), default=relevance (regex match) |
| Trending Unit Tests | NOT STARTED | DONE | `TrendingServiceTest.java` with 8 tests: getTrendingPosts (3 tests), getTrendingHashtags (1), addPost (2), removePost (2) |
| Swagger - @Tag | NOT PRESENT | DONE (all 4 controllers) | `@Tag` on AuthController, PostController, FollowController, TrendingController |
| Swagger - @Operation | NOT PRESENT | PARTIALLY DONE (17/26) | AuthController 8/8, PostController 6/8, FollowController 3/6, TrendingController 0/4 |
| Swagger - @ApiResponses | NOT PRESENT | PARTIALLY DONE (17/26) | Same distribution as @Operation above |
| Swagger - OpenApiConfig | NOT PRESENT | DONE (all 4 services) | Programmatic OpenAPI info with title, description, version, contact |
| Swagger - @Schema on DTOs | NOT PRESENT | NOT DONE | Still zero @Schema annotations |
| US05 criterion 1 (search users) | NOT DONE | DONE | Search users now implemented via `GET /api/auth/search/users` |
| US06 (entire story) | 0/7 DONE | 5/7 DONE | User search + post/hashtag search + sort options + view details all implemented |
| PostRepository custom queries | Basic CRUD only | NEW methods added | `searchPosts()`, `findByHashtagsContainingOrderByLikesCountDesc()`, `findByHashtagsContainingOrderByCreatedAtDesc()`, `findByUserIdIn()` |
| UserRepository custom query | Basic finders only | NEW method added | `searchUsers()` with $or regex on username, fullName, bio |

**Previous critical gaps resolved:**
1. ~~Search functionality not implemented (US06)~~ -- RESOLVED (basic search done, 5/7 criteria met)
2. ~~Trending service unit tests missing~~ -- RESOLVED (8 tests covering core methods)
3. ~~Swagger annotations missing~~ -- PARTIALLY RESOLVED (17/26 endpoints annotated, DTOs still need @Schema)

**Remaining critical gaps (top 3):**
1. No inter-service communication (services completely isolated)
2. Swagger still incomplete (9 endpoints + all DTOs missing annotations)
3. No JaCoCo/SonarQube (cannot verify 80% coverage requirement)

---

## Section 11: Source File Counts

| Service | Main Java Files | Test Java Files | Total |
|---------|----------------|-----------------|-------|
| auth-service | 36 | 9 | 45 |
| post-service | 17 | 1 | 18 |
| follow-service | 14 | 1 | 15 |
| trending-service | 13 | 1 | 14 |
| api-gateway | 4 | 0 | 4 |
| **TOTAL** | **90** (up from 86) | **12** (up from 11) | **102** |
