# Smart Hiring System - Implementation Guide

## Overview

This document describes the hybrid smart hiring system that has been implemented in Unifyx. The system intelligently recommends workers to job owners based on matching algorithm, while also allowing workers to submit quotes for jobs they're interested in.

## Architecture

### Backend Components

#### 1. **Database Entities**

**Quote**
- Stores quotes submitted by workers for specific job posts
- Fields: quoteId, postId, workerId, price, estimatedTime, message, createdAt, status
- Status values: PENDING, ACCEPTED, REJECTED

**TrustScore**
- Maintains worker reputation metrics
- Calculated based on: ratings (40%), completion rate (35%), repeat clients (15%), baseline (10%)
- Range: 0-10 scale

**Hire**
- Represents an active employment agreement between owner and worker
- Tracks completion status, ratings, and reviews
- Status values: ACTIVE, COMPLETED, CANCELLED

**Recommendation**
- Auto-generated recommendations for each posted job
- Ranked by matching algorithm score
- Tracks if worker has been contacted

#### 2. **Matching Algorithm**

Located in `RecommendationService.java`

**Scoring Formula:**
```
Score = (TrustScore × 0.40) + 
        (SkillMatch × 0.35) + 
        (Distance⁻¹ × 0.15) + 
        (CompletionRate × 0.10)
```

**Filtering Criteria:**
1. **Category Match**: Worker must have job category in their skill list
2. **Location**: Worker within 15km radius of job location (configurable)
3. **Availability**: No existing active hires for same post

**Output:** Top 5 ranked workers per job

#### 3. **REST API Endpoints**

**Recommendations**
```
GET /recommendations/post/{postId}
POST /recommendations/post/{postId}/generate
```

**Quotes**
```
POST /quotes
GET /quotes/post/{postId}
GET /quotes/worker/{workerId}/pending
PUT /quotes/{quoteId}/accept
PUT /quotes/{quoteId}/reject
```

**Hires**
```
POST /hires
GET /hires/post/{postId}/active
GET /hires/worker/{workerId}
PUT /hires/{hireId}/complete
PUT /hires/{hireId}/rate
PUT /hires/{hireId}/cancel
```

### Frontend Components

#### 1. **Activities**

**PostMatchesActivity**
- Displays recommended workers (Tab 1)
- Displays incoming quotes (Tab 2)
- Owner can hire directly from recommendations
- Owner can accept/reject quotes

**SubmitQuoteActivity**
- Worker submits price quote, estimated time, and pitch message
- Simple form with validation
- Success confirmation with result callback

#### 2. **Fragments**

**RecommendationsFragment**
- RecyclerView of recommended workers
- Shows trust score, rating, distance, match reason
- "View Profile" and "Hire Now" buttons

**QuotesFragment**
- RecyclerView of received quotes
- Shows worker name, price, time estimate, message
- "Accept & Hire" and "Reject" buttons

#### 3. **Adapters**

**RecommendationAdapter**
- Displays recommendation cards
- Handles hire action
- Shows matching score and reason

**QuoteAdapter**
- Displays quote cards with worker details
- Handles accept/reject actions
- Updates status in real-time

**PostMatchesAdapter**
- ViewPager2 adapter for tab navigation
- Manages both recommendations and quotes fragments

## Integration Steps

### Step 1: Backend Setup

1. **Database Migration**
   ```bash
   # Hibernate will auto-create new tables on startup
   cd Unifyx_Backend
   mvn clean compile
   mvn spring-boot:run
   ```

   New tables created:
   - `quotes`
   - `trust_scores`
   - `hires`
   - `recommendations`

2. **Update Existing Entities**
   - `WorkerProfile`: Added latitude, longitude, isVerified, hourlyRate, trustScore relationship
   - `Post`: Added latitude, longitude for location-based matching

### Step 2: Frontend Setup

1. **Add Dependencies** (if not already in build.gradle)
   ```gradle
   implementation 'androidx.viewpager2:viewpager2:1.0.0'
   implementation 'com.google.android.material:material:1.6.0'
   implementation 'androidx.recyclerview:recyclerview:1.2.1'
   ```

2. **Update Activities**

   Update `PostCreate.java` to navigate to `PostMatchesActivity` after successful post creation:
   
   ```java
   // After post creation success
   Intent intent = new Intent(PostCreate.this, PostMatchesActivity.class);
   intent.putExtra("postId", createdPostId);
   startActivity(intent);
   finish();
   ```

3. **Update Worker Activities**

   Add button in worker job list to trigger `SubmitQuoteActivity`:
   
   ```java
   Button quoteBtn = findViewById(R.id.sendQuoteBtn);
   quoteBtn.setOnClickListener(v -> {
       Intent intent = new Intent(WorkerJobsActivity.this, SubmitQuoteActivity.class);
       intent.putExtra("postId", postId);
       intent.putExtra("workerId", workerId);
       startActivity(intent);
   });
   ```

### Step 3: Worker Profile Updates

Workers need to update their profiles with:
- **Latitude/Longitude**: For location-based matching
- **Skills**: In `categories` field
- **Hourly Rate**: Optional, for quote suggestions

Update `WorkerProfileActivity.java` to capture location:

```java
// Get user's current location
LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

if (location != null) {
    workerProfile.setLatitude(location.getLatitude());
    workerProfile.setLongitude(location.getLongitude());
}
```

### Step 4: Post Creation with Location

Update `PostCreate.java` to capture job location:

```java
// Capture location when creating post
LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

post.setLatitude(location.getLatitude());
post.setLongitude(location.getLongitude());
```

## User Flows

### Owner Flow

1. **Post a Job**
   - Fill job details (title, category, location, budget, duration)
   - Submit with images
   - ✓ Post created

2. **View Matches**
   - Immediately see 5 recommended workers
   - Can hire directly from recommendations
   - Can view incoming quotes from other workers
   - Can accept/reject individual quotes

3. **Manage Hire**
   - Can complete hire when job is done
   - Can rate and review worker (1-5 stars)
   - Rating updates worker's trust score

### Worker Flow

1. **Browse Jobs**
   - View job listings in their skill categories
   - See distance from their location
   - See budget and duration

2. **Submit Quote**
   - Click "Send Quote" on job of interest
   - Fill: price, estimated completion time, pitch message
   - Submit quote to owner

3. **Track Quotes**
   - View pending quotes in worker dashboard
   - See status when owner accepts/rejects
   - If accepted, shows hire agreement

4. **Complete Work**
   - Mark hire as complete when done
   - Receive owner's rating/review
   - Rating improves trust score

## Trust Score System

**Calculation Formula:**
```
Trust Score = (RatingAvg × 0.40) + 
              (CompletionRate × 0.35) + 
              (RepeatClients × 0.15) + 
              (Baseline 5.0 × 0.10)
```

**Initial Score:** 5.0 / 10.0 (neutral baseline)

**Updates:**
- On job completion: Recalculate completion rate
- On owner rating: Update rating average
- On hire completion: Increment total jobs

**Impact on Recommendations:**
- Higher trust score = More recommended jobs
- Score used in matching algorithm (40% weight)

## Configuration

### Matching Algorithm Parameters

Edit `RecommendationService.java`:

```java
// Default search radius (km)
private static final double DEFAULT_SEARCH_RADIUS_KM = 15.0;

// Max recommendations per post
private static final int MAX_RECOMMENDATIONS = 5;

// Scoring weights
// TrustScore: 0.40
// SkillMatch: 0.35
// Distance: 0.15
// CompletionRate: 0.10
```

### Database Configuration

Edit `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/unifyxproject
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

## Testing

### Backend Testing

1. **Create a Post**
   ```bash
   curl -X POST http://localhost:8080/posts/upload \
     -F "uid=test_owner" \
     -F "worker_category=Plumbing" \
     -F "site_address=123 Main St" \
     -F "latitude=40.7128" \
     -F "longitude=-74.0060"
   ```

2. **View Recommendations**
   ```bash
   curl http://localhost:8080/recommendations/post/{postId}
   ```

3. **Submit Quote**
   ```bash
   curl -X POST http://localhost:8080/quotes \
     -H "Content-Type: application/json" \
     -d '{
       "postId": 1,
       "workerId": 1,
       "price": 5000,
       "estimatedTime": "2 days",
       "message": "I can do this quickly"
     }'
   ```

### Android Testing

1. Ensure backend is running on `http://10.0.2.2:8080`
2. Create test user accounts (owner + worker)
3. Update worker profile with location
4. Post a job → Check PostMatchesActivity
5. Open job as worker → SubmitQuoteActivity
6. Accept quote in PostMatchesActivity

## Common Issues & Solutions

### Issue: No Recommendations Showing
**Solution:**
- Check worker profiles have location data (latitude, longitude)
- Check post has location data
- Verify workers have matching job categories
- Check if workers are within 15km radius

### Issue: Recommendations Always Same
**Solution:**
- Update trust scores for workers
- Complete some hires to affect completion rates
- Add more workers to database

### Issue: Quote Not Appearing
**Solution:**
- Verify workerId exists in database
- Check quote status is "PENDING"
- Ensure quote is for correct post

## Performance Optimization

### For Large Datasets

1. **Add Database Indexes** (in migration script):
   ```sql
   CREATE INDEX idx_worker_category ON worker_profile(worker_id);
   CREATE INDEX idx_recommendation_post ON recommendations(post_id);
   CREATE INDEX idx_quote_post ON quotes(post_id);
   CREATE INDEX idx_hire_status ON hires(status);
   ```

2. **Pagination** (for quote lists):
   ```java
   @GetMapping("/quotes/post/{postId}")
   public Page<Quote> getQuotesForPost(@PathVariable int postId, Pageable pageable)
   ```

3. **Caching** (for recommendations):
   ```java
   @Cacheable(value = "recommendations", key = "#postId")
   public List<Recommendation> getTopRecommendationsForPost(int postId)
   ```

## Future Enhancements

1. **Notification System**
   - Push notifications when recommended
   - Alerts for new quotes
   - Hire status updates

2. **Advanced Matching**
   - Machine learning based scoring
   - Historical performance prediction
   - Budget-based filtering

3. **Payment Integration**
   - Razorpay integration for quotes
   - Escrow system
   - Secure payments

4. **Analytics**
   - Worker performance dashboards
   - Owner spending analytics
   - Completion rate trends

## Support

For issues or questions:
1. Check AGENTS.md for project setup
2. Review backend logs: `tail -f Unifyx_Backend/spring.log`
3. Check frontend logs: Android Studio Logcat
4. Verify database connection: `mysql -u root -e "USE unifyxproject; SHOW TABLES;"`

