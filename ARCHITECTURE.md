# Smart Hiring System - Architecture Diagram

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         UNIFYX SMART HIRING SYSTEM                          │
└─────────────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────┐         ┌──────────────────────────────────┐
│       ANDROID FRONTEND           │         │      SPRING BOOT BACKEND         │
│    (frontend/app/)               │         │   (Unifyx_Backend/)              │
└──────────────────────────────────┘         └──────────────────────────────────┘

┌─ OWNER FLOW ──────────────────────────┐    ┌─ POST CREATION ────────────────────────┐
│                                       │    │                                        │
│  1. PostCreate Activity               │    │  1. PostController.createPost()       │
│     ├─ Input: job details             │    │     ├─ Validates input              │
│     ├─ Input: location (lat/lng)      │    │     └─ Saves Post entity            │
│     └─ Upload images                  │    │                                      │
│                                       │    │  2. PostService.createPostWithImages()│
│  2. [POST /posts/upload]──────────┐   │    │     ├─ Uploads images to Cloudinary  │
│     └─ Success: postId            │   │    │     └─ Sets post.photos URLs        │
│                                   ↓   │    │                                      │
│  3. PostMatchesActivity           │   │    │  3. RecommendationService           │
│     ├─ Shows Recommended Workers ◄───┼────┤     .generateRecommendationsForPost()│
│     │  (Tab 1)                    │   │    │     ├─ Filter by skill match       │
│     │                             │   │    │     ├─ Filter by location (15km)   │
│     │  [GET /recommendations]     │   │    │     ├─ Score by:                   │
│     │                             │   │    │     │  - TrustScore (40%)          │
│     └─ Shows Incoming Quotes      │   │    │     │  - SkillMatch (35%)          │
│        (Tab 2)                    │   │    │     │  - Distance (15%)            │
│                                   │   │    │     │  - CompletionRate (10%)      │
│     [GET /quotes/post/{id}]       │   │    │     └─ Save top 5 as Recommendations│
│                                   │   │    │                                      │
│  4. Action: Owner Hires Worker    │   │    └────────────────────────────────────┘
│     ├─ Via Recommendation:        │   │
│     │  [POST /hires]              │   │    ┌─ QUOTE SUBMISSION ─────────────────────┐
│     │  └─ Creates Hire agreement  │   │    │                                        │
│     │                             │   │    │  1. Worker selects job                 │
│     └─ Via Quote:                 │   │    │     └─ [GET /posts/all] or search      │
│        [PUT /quotes/{id}/accept]  │   │    │                                        │
│        └─ Accepts quote           │   │    │  2. SubmitQuoteActivity                │
│           & creates hire          │   │    │     ├─ Input: price                    │
│                                   │   │    │     ├─ Input: estimated time           │
│  5. After Job Complete:           │   │    │     └─ Input: pitch message            │
│     [PUT /hires/{id}/complete]    │   │    │                                        │
│     [PUT /hires/{id}/rate]        │   │    │  3. [POST /quotes]                     │
│     └─ Rate + Review worker       │   │    │     └─ Saves Quote entity              │
│        (updates TrustScore)       │   │    │                                        │
│                                       │    └────────────────────────────────────┘
└───────────────────────────────────────┘

┌─ WORKER FLOW ─────────────────────────┐    ┌─ QUOTE MANAGEMENT ─────────────────────┐
│                                       │    │                                        │
│  1. Browse Jobs                       │    │  QuoteController:                      │
│     ├─ [GET /posts/all]              │    │  ├─ POST /quotes (submit)              │
│     ├─ Filter by category            │    │  ├─ GET /quotes/post/{id} (for owner)  │
│     └─ See distance & recommendations│    │  ├─ GET /quotes/worker/{id}/pending    │
│                                       │    │  ├─ PUT /quotes/{id}/accept           │
│  2. Select Job                        │    │  └─ PUT /quotes/{id}/reject           │
│     ├─ View full details             │    │                                        │
│     └─ See if recommended (badge)    │    │  QuoteService:                         │
│                                       │    │  ├─ submitQuote() - new or update     │
│  3. SubmitQuoteActivity               │    │  ├─ acceptQuote() - change status     │
│     ├─ Enter: Price                  │    │  ├─ rejectQuote() - decline           │
│     ├─ Enter: Time estimate          │    │  └─ markRecommendationAsContacted()   │
│     └─ Enter: Pitch message           │    │                                        │
│                                       │    └────────────────────────────────────┘
│  4. Submit Quote                      │
│     [POST /quotes]                    │    ┌─ HIRE LIFECYCLE ───────────────────────┐
│     └─ Success confirmation           │    │                                        │
│                                       │    │  HireController:                       │
│  5. Track Pending Quotes              │    │  ├─ POST /hires (create)               │
│     [GET /quotes/worker/{id}/pending] │    │  ├─ GET /hires/worker/{id}            │
│     ├─ See status (PENDING/ACCEPTED) │    │  ├─ PUT /hires/{id}/complete          │
│     └─ If accepted, work begins      │    │  └─ PUT /hires/{id}/rate              │
│                                       │    │                                        │
│  6. Complete Work                     │    │  HireService:                          │
│     [PUT /hires/{id}/complete]        │    │  ├─ createHire() - agreement start    │
│     └─ Notify owner                   │    │  ├─ completeHire() - mark done        │
│                                       │    │  ├─ rateHire() - owner rates worker   │
│                                       │    │  └─ updateWorkerTrustScore()          │
│                                       │    │                                        │
└───────────────────────────────────────┘    └────────────────────────────────────┘

┌─ MATCHING ALGORITHM ───────────────────┐
│                                        │
│  RecommendationService.scoreWorker()  │
│                                        │
│  For each worker:                      │
│                                        │
│  1. Get TrustScore (default: 5.0/10)  │
│  2. Check Category Match (100% or 50%)│
│  3. Calculate Distance (Haversine)     │
│  4. Get CompletionRate from TrustScore│
│                                        │
│  Score = (TS × 0.40) +                │
│           (SM × 0.35) +               │
│           (100-D × 0.15) +            │
│           (CR × 0.10)                 │
│                                        │
│  Save top 5 → Recommendations entity  │
│                                        │
└────────────────────────────────────────┘

┌─ TRUST SCORE SYSTEM ────────────────────┐
│                                         │
│  TrustScore Entity (per worker)         │
│  ├─ score (0-10)                       │
│  ├─ ratingAvg (from owner ratings)     │
│  ├─ completionRate (completed/total)   │
│  ├─ repeatClientsCount                 │
│  └─ totalJobsCompleted                 │
│                                         │
│  Updates on:                            │
│  1. Hire completion                     │
│     └─ Increment totalJobsCompleted    │
│     └─ Recalculate completionRate      │
│                                         │
│  2. Owner rates hire (1-5 stars)        │
│     └─ Update ratingAvg                │
│     └─ Recalculate overall score       │
│                                         │
│  Formula:                               │
│  score = (ratingAvg × 0.40) +          │
│           (completionRate × 0.35) +    │
│           (repeatClients × 0.15) +     │
│           (baseline 5.0 × 0.10)        │
│                                         │
└─────────────────────────────────────────┘

┌────────────────────── DATABASE ────────────────────────────┐
│                                                            │
│  Core Tables (existing):                                  │
│  ├─ users                                                 │
│  ├─ posts (+ latitude, longitude)                         │
│  ├─ owner_profile                                         │
│  ├─ worker_profile (+ lat, lng, verified, hourly_rate)   │
│  └─ post_images                                           │
│                                                            │
│  New Tables (smart hiring):                               │
│  ├─ quotes ────────┐                                      │
│  ├─ trust_scores ──┼─ Relationships via Foreign Keys     │
│  ├─ hires ─────────┤                                      │
│  └─ recommendations┘                                      │
│                                                            │
│  Schema: MySQL 8+ with InnoDB, utf8mb4 collation         │
│  Auto-increment: Enabled for all ID fields                │
│  Indexes: On frequently queried fields (post_id, worker_id)│
│                                                            │
└────────────────────────────────────────────────────────────┘

┌──────────────────── DATA FLOW ─────────────────────────────┐
│                                                             │
│  POST /posts/upload (owner creates job)                    │
│        ↓                                                    │
│  POST entity saved with location                           │
│        ↓                                                    │
│  RecommendationService.generateRecommendationsForPost()    │
│        ↓                                                    │
│  [Filter workers by category, location, availability]     │
│        ↓                                                    │
│  [Score each worker by matching algorithm]                │
│        ↓                                                    │
│  [Save top 5 as Recommendation entities]                  │
│        ↓                                                    │
│  GET /recommendations/post/{id} (owner views matches)     │
│        ↓                                                    │
│  Owner can: [Hire Now] or [View Profile]                 │
│        ↓                                                    │
│  POST /hires (creates Hire agreement)                     │
│        ↓                                                    │
│  PUT /hires/{id}/complete (mark job done)                │
│        ↓                                                    │
│  PUT /hires/{id}/rate (owner rates worker)               │
│        ↓                                                    │
│  HireService updates TrustScore                           │
│        ↓                                                    │
│  Worker TrustScore improves for future jobs               │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Component Interactions

### Sequence: Post Job → Get Recommendations → Hire Worker

```
Owner                    Frontend               Backend               Database
 │                          │                      │                      │
 ├─ Post Job──────────────────>                    │                      │
 │  (location + details)      │                    │                      │
 │                            ├─ POST /posts/upload->                     │
 │                            │                    │                      │
 │                            │                    ├─ Save Post entity──> │
 │                            │                    │                      │
 │                            │                    ├─ Generate Recommendations
 │                            │                    │  (filter + score)    │
 │                            │                    │                      │
 │                            │                    ├─ Save 5 Recs ─────> │
 │                            │  <─ postId ────────┤                      │
 │                            │  onSuccess         │                      │
 │                            │                    │                      │
 ├─ View Matches─────────────────>                 │                      │
 │  (navigate PostMatches)     │                   │                      │
 │                            ├─ GET /recommendations/post/{id}──>        │
 │                            │                    │                      │
 │                            │                    ├─ Query Recs ──────> │
 │                            │  <─ [5 Workers]────┤  order by rank      │
 │                            │                    │                      │
 ├─ Select Worker────────────────>                 │                      │
 │  (Click Hire)               │                   │                      │
 │                            ├─ POST /hires ────->                      │
 │                            │                    │                      │
 │                            │                    ├─ Create Hire ─────> │
 │                            │                    │                      │
 │                            │                    ├─ Update Post ──────>│
 │                            │  <─ Hired! ────────┤  status="HIRED"     │
 │                            │                    │                      │
 └─ Confirmation msg          │                    │                      │
    (Worker hired)            │                    │                      │
```

### Sequence: Worker Submits Quote → Owner Reviews → Accepts

```
Worker                   Frontend               Backend               Database
 │                          │                      │                      │
 ├─ Find Job─────────────────>                    │                      │
 │                            ├─ GET /posts/all──> │                      │
 │                            │                    ├─ Fetch posts ─────> │
 │                            │  <─ [Jobs]─────────┤                      │
 │                            │                    │                      │
 ├─ Click Send Quote─────────────>                │                      │
 │ (SubmitQuoteActivity)       │                   │                      │
 │                            │                    │                      │
 ├─ Enter: Price──────────────────>               │                      │
 ├─ Enter: Time                 │                 │                      │
 ├─ Enter: Message──────────────────>             │                      │
 │                            │                    │                      │
 ├─ Submit Quote─────────────────>                │                      │
 │                            ├─ POST /quotes ──-> │                      │
 │                            │                    │                      │
 │                            │                    ├─ Create Quote ────> │
 │                            │                    │                      │
 │                            │                    ├─ Mark Rec ────────> │
 │                            │  <─ Success ───────┤  isContacted=true   │
 │                            │                    │                      │
 │ ✓ Quote Sent!              │                    │                      │
 │                            │                    │                      │
 │                          Owner                  │                      │
 │                            │                    │                      │
 │                            ├─ GET /quotes ────->│                      │
 │                            │                    ├─ Fetch Quotes ────> │
 │                            │  <─ [Quotes]───────┤  sorted by trust    │
 │                            │                    │                      │
 │                            ├─ PUT /quotes/{id}/accept─>                │
 │                            │                    │                      │
 │                            │                    ├─ Update Quote ────> │
 │                            │  <─ Accepted ──────┤  status="ACCEPTED"  │
 │                            │                    │                      │
 │ <─ Notification──────────────                  │                      │
 │    (Quote Accepted)        │                    │                      │
 │                            │                    │                      │
 └─ Begin Work                │                    │                      │
    (Hire Agreement Active)   │                    │                      │
```

## API Endpoint Summary

```
RECOMMENDATIONS
├─ GET  /recommendations/post/{postId}         → List top 5 workers
└─ POST /recommendations/post/{postId}/generate → Refresh recommendations

QUOTES
├─ POST   /quotes                              → Submit quote
├─ GET    /quotes/post/{postId}                → Get all quotes for post
├─ GET    /quotes/worker/{workerId}/pending    → Get pending quotes
├─ PUT    /quotes/{quoteId}/accept             → Accept quote
└─ PUT    /quotes/{quoteId}/reject             → Reject quote

HIRES
├─ POST   /hires                               → Create hire agreement
├─ GET    /hires/post/{postId}/active          → Active hires for post
├─ GET    /hires/worker/{workerId}             → Worker's hire history
├─ PUT    /hires/{hireId}/complete             → Mark job complete
├─ PUT    /hires/{hireId}/rate                 → Rate and review hire
└─ PUT    /hires/{hireId}/cancel               → Cancel hire
```

## Key Features

✅ **Instant Matching** - Recommendations generated on job post creation
✅ **Smart Scoring** - Weighted algorithm (trust + skill + distance + completion)
✅ **Location-Based** - Haversine distance calculation (15km default radius)
✅ **Quote System** - Workers can submit competitive quotes
✅ **Trust Building** - Automatic score updates after job completion
✅ **Rating System** - Owners can rate workers (impacts trust score)
✅ **Responsive UI** - ViewPager2 tabs for smooth navigation
✅ **Data Persistence** - Full transaction management
✅ **Clean Architecture** - Service layer, Repository pattern, DTOs

