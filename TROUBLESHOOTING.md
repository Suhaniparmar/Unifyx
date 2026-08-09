# Smart Hiring System - Troubleshooting Guide

## Backend Issues

### 1. Application Won't Start

**Error:** `Could not find profile placeholder`
```
org.springframework.core.convert.ConversionFailedException: Could not convert class
```

**Solution:**
- Check that all new model classes (Quote, TrustScore, Hire, Recommendation) are in the Model directory
- Run: `mvn clean compile`
- Verify no syntax errors in entity classes

**Error:** `Table 'unifyxproject.quotes' doesn't exist`
```
java.sql.SQLSyntaxErrorException: Table 'unifyxproject.quotes' doesn't exist
```

**Solution:**
- This is normal on first run - Hibernate will create the tables
- Verify in MySQL: `mysql -u root unifyxproject -e "SHOW TABLES;"`
- Should see: quotes, trust_scores, hires, recommendations
- If not created, manually run:
  ```sql
  CREATE TABLE quotes (
    quote_id INT PRIMARY KEY AUTO_INCREMENT,
    post_id INT NOT NULL,
    worker_id INT NOT NULL,
    price DECIMAL(10,2),
    estimated_time VARCHAR(100),
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'PENDING',
    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    FOREIGN KEY (worker_id) REFERENCES worker_profile(worker_id)
  );
  ```

### 2. Recommendations Not Generating

**Problem:** Empty recommendations list after posting job

**Debugging Steps:**

1. **Check if RecommendationService is being called:**
   ```bash
   # Look in logs for "Generating recommendations for post"
   grep -i "recommendation" application.log
   ```

2. **Verify workers exist in database:**
   ```sql
   SELECT * FROM worker_profile;
   ```
   If empty, create test workers first.

3. **Check worker location data:**
   ```sql
   SELECT worker_id, name, latitude, longitude, categories 
   FROM worker_profile;
   ```
   Location fields should be non-null for matching.

4. **Verify post location:**
   ```sql
   SELECT post_id, description, latitude, longitude, worker_category 
   FROM posts 
   WHERE post_id = YOUR_POST_ID;
   ```

5. **Manual API test:**
   ```bash
   curl -X POST http://localhost:8080/recommendations/post/1/generate
   ```
   Should return list of workers.

**Common Causes:**
- Workers have NULL latitude/longitude → Add location to worker profiles
- No workers with matching category → Create workers with matching skills
- Workers are >15km away → Adjust DEFAULT_SEARCH_RADIUS_KM or add workers nearby
- No trust scores for workers → System creates default 5.0 score automatically

### 3. Quotes Not Showing

**Problem:** Quote submitted but doesn't appear for owner

**Debugging Steps:**

1. **Verify quote was saved:**
   ```sql
   SELECT * FROM quotes WHERE post_id = YOUR_POST_ID;
   ```
   If empty, check API response status.

2. **Test quote submission:**
   ```bash
   curl -X POST http://localhost:8080/quotes \
     -H "Content-Type: application/json" \
     -d '{
       "postId": 1,
       "workerId": 1,
       "price": 5000,
       "estimatedTime": "2 days",
       "message": "Test quote"
     }'
   ```

3. **Check quote status:**
   ```sql
   SELECT * FROM quotes WHERE quote_id = YOUR_QUOTE_ID;
   ```
   Status should be "PENDING"

4. **Verify relationships:**
   ```sql
   SELECT q.*, w.name, p.description 
   FROM quotes q
   JOIN worker_profile w ON q.worker_id = w.worker_id
   JOIN posts p ON q.post_id = p.post_id
   WHERE q.post_id = YOUR_POST_ID;
   ```

**Common Causes:**
- Invalid postId or workerId → Verify IDs exist in posts and worker_profile
- Quote already exists → System updates existing, doesn't create duplicate
- API not returning response → Check QuoteController response handling

### 4. Hire Not Creating

**Problem:** Accept quote button doesn't work

**Error in logs:**
```
Failed to create hire agreement
```

**Debugging Steps:**

1. **Check POST endpoint:**
   ```bash
   curl -X POST http://localhost:8080/hires \
     -H "Content-Type: application/json" \
     -d '{
       "postId": 1,
       "workerId": 1,
       "agreedPrice": 5000
     }'
   ```

2. **Verify database state:**
   ```sql
   -- Check quote exists and is accepted
   SELECT * FROM quotes WHERE post_id = 1 AND status = 'ACCEPTED';
   
   -- Check hire was created
   SELECT * FROM hires WHERE post_id = 1;
   ```

3. **Check post status:**
   ```sql
   SELECT post_id, status FROM posts WHERE post_id = 1;
   ```
   Should change to "HIRED" after hire creation.

**Common Causes:**
- Quote not accepted first → Run PUT /quotes/{id}/accept first
- Post already has active hire → Check hires table for active entries
- Transaction rolled back → Check HireService for business logic errors

### 5. Trust Score Not Updating

**Problem:** Worker's trust score doesn't change after job completion

**Debugging Steps:**

1. **Check trust score exists:**
   ```sql
   SELECT * FROM trust_scores WHERE worker_id = YOUR_WORKER_ID;
   ```
   If empty, system should create it on first completion.

2. **Check hire completion:**
   ```sql
   SELECT hire_id, status, completed_at FROM hires 
   WHERE worker_id = YOUR_WORKER_ID;
   ```
   Status should be "COMPLETED" with completed_at timestamp.

3. **Check rating was applied:**
   ```sql
   SELECT hire_id, owner_rating, owner_review 
   FROM hires 
   WHERE worker_id = YOUR_WORKER_ID 
   AND owner_rating IS NOT NULL;
   ```

4. **Manually recalculate:**
   ```sql
   -- Check trust score values
   SELECT worker_id, score, rating_avg, completion_rate, total_jobs_completed
   FROM trust_scores
   WHERE worker_id = YOUR_WORKER_ID;
   ```

**Common Causes:**
- HireService.completeHire() not called → Call PUT /hires/{id}/complete
- Rating not submitted → Call PUT /hires/{id}/rate with rating and review
- Transaction issues → Check logs for exceptions during update

---

## Frontend (Android) Issues

### 1. PostMatchesActivity Crashes

**Error:** `NullPointerException` in PostMatchesActivity

**Debugging:**

1. **Check intent data:**
   ```java
   int postId = getIntent().getIntExtra("postId", -1);
   Log.d("DEBUG", "postId = " + postId);
   ```

2. **Verify layout exists:**
   - File: `res/layout/activity_post_matches.xml`
   - Check all IDs referenced are defined

3. **Check adapters:**
   - RecommendationAdapter.java exists
   - QuoteAdapter.java exists
   - PostMatchesAdapter.java exists

4. **Verify dependencies:**
   - ViewPager2 added to build.gradle
   - Material library added

**Fix:**
```java
public class PostMatchesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_matches);
        
        postId = getIntent().getIntExtra("postId", -1);
        if (postId == -1) {
            Toast.makeText(this, "Invalid post ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // ... rest of code
    }
}
```

### 2. API Calls Not Working

**Error:** `Failed to load recommendations`

**Debugging Steps:**

1. **Check backend URL:**
   ```java
   // In RetrofitClient.java
   String BASE_URL = "http://10.0.2.2:8080/";  // Android emulator localhost
   ```
   NOT `http://localhost:8080/` (that's host localhost, not device)

2. **Verify network permission:**
   ```xml
   <!-- In AndroidManifest.xml -->
   <uses-permission android:name="android.permission.INTERNET" />
   ```

3. **Test endpoint in Postman:**
   ```
   GET http://localhost:8080/recommendations/post/1
   ```
   (Use localhost on your dev machine)

4. **Check network request:**
   ```java
   // Add interceptor to log requests
   HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
   logging.setLevel(HttpLoggingInterceptor.Level.BODY);
   ```

5. **Verify backend is running:**
   ```bash
   curl http://localhost:8080/posts
   ```
   Should return list of posts

**Common Causes:**
- Backend not running → Start with `mvn spring-boot:run`
- Wrong URL (localhost vs 10.0.2.2) → Use 10.0.2.2 for emulator
- Firewall blocking port 8080 → Check network settings
- JSON deserialization error → Check model classes match API response

### 3. RecyclerView Not Showing Items

**Problem:** Fragment loads but no items displayed

**Debugging:**

1. **Check data fetched:**
   ```java
   @Override
   public void onResponse(Call<List<Recommendation>> call, Response<List<Recommendation>> response) {
       if (response.isSuccessful() && response.body() != null) {
           Log.d("DEBUG", "Received " + response.body().size() + " recommendations");
       }
   }
   ```

2. **Verify adapter:**
   ```java
   RecommendationAdapter adapter = new RecommendationAdapter(getContext(), recommendations, postId);
   recyclerView.setAdapter(adapter);
   Log.d("DEBUG", "Adapter set with " + recommendations.size() + " items");
   ```

3. **Check layout manager:**
   ```java
   recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
   ```

4. **Verify list is not empty:**
   ```java
   if (recommendations.isEmpty()) {
       Log.w("DEBUG", "Recommendations list is empty");
   }
   ```

**Fix:**
```java
private void setupAdapter() {
    if (recommendations.isEmpty() && otherQuotes.isEmpty()) {
        Toast.makeText(this, "No matches found yet", Toast.LENGTH_SHORT).show();
        return;
    }
    PostMatchesAdapter adapter = new PostMatchesAdapter(this, recommendations, otherQuotes, postId);
    viewPager.setAdapter(adapter);
    new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
        tab.setText(position == 0 ? "Recommended" : "Quotes");
    }).attach();
}
```

### 4. Fragment Arguments Issue

**Error:** `Parcelable too large` or `TransactionTooLargeException`

**Solution:** Don't pass large lists via Bundle

**Before (❌ Wrong):**
```java
Bundle args = new Bundle();
args.putParcelableArrayList("recommendations", new ArrayList<>(largeList));
```

**After (✅ Correct):**
```java
// Option 1: Pass only IDs and fetch in fragment
Bundle args = new Bundle();
args.putInt(ARG_POST_ID, postId);
fragment.setArguments(args);

// In fragment onCreate:
postId = getArguments().getInt(ARG_POST_ID);
// Fetch data in onCreateView
```

### 5. Location Permissions

**Error:** `Permission Denied` when accessing location

**Fix:** Add to AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

**Request at runtime:**
```java
if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
        PERMISSION_REQUEST_CODE);
}
```

---

## Database Issues

### 1. Connection Refused

**Error:** `Communications link failure`

**Solution:**
```bash
# Start MySQL
brew services start mysql

# Verify it's running
brew services list

# Test connection
mysql -u root -e "SELECT 1"
```

### 2. Tables Not Created

**Error:** `Table doesn't exist`

**Solution:**
1. Delete database and let Hibernate recreate:
   ```bash
   mysql -u root -e "DROP DATABASE unifyxproject;"
   mysql -u root -e "CREATE DATABASE unifyxproject CHARACTER SET utf8mb4;"
   ```

2. Restart Spring Boot:
   ```bash
   mvn clean spring-boot:run
   ```

3. Verify tables:
   ```bash
   mysql -u root unifyxproject -e "SHOW TABLES;"
   ```

### 3. Foreign Key Constraint Error

**Error:** `Integrity constraint violation`

**Solution:**
- Ensure referenced entity exists before creating dependent entity
- Example: Create Post before creating Quote
- Check foreign key constraints:
  ```sql
  SELECT CONSTRAINT_NAME, TABLE_NAME, REFERENCED_TABLE_NAME 
  FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
  WHERE TABLE_SCHEMA = 'unifyxproject';
  ```

---

## Testing Commands

### Backend API Testing

```bash
# Create a test post
curl -X POST http://localhost:8080/posts/upload \
  -F "uid=test_owner" \
  -F "description=Test Job" \
  -F "worker_category=Plumbing" \
  -F "site_address=123 Main St" \
  -F "latitude=40.7128" \
  -F "longitude=-74.0060"

# Get recommendations
curl http://localhost:8080/recommendations/post/1

# Submit quote
curl -X POST http://localhost:8080/quotes \
  -H "Content-Type: application/json" \
  -d '{"postId":1,"workerId":1,"price":5000,"estimatedTime":"2 days","message":"Test"}'

# Get quotes
curl http://localhost:8080/quotes/post/1

# Accept quote
curl -X PUT http://localhost:8080/quotes/1/accept

# Create hire
curl -X POST http://localhost:8080/hires \
  -H "Content-Type: application/json" \
  -d '{"postId":1,"workerId":1,"agreedPrice":5000}'

# Complete hire
curl -X PUT http://localhost:8080/hires/1/complete

# Rate hire
curl -X PUT http://localhost:8080/hires/1/rate \
  -H "Content-Type: application/json" \
  -d '{"rating":4.5,"review":"Great work!"}'
```

### Database Verification

```bash
# Check all tables
mysql -u root unifyxproject -e "SHOW TABLES;"

# Count records
mysql -u root unifyxproject -e "SELECT 'posts' as table_name, COUNT(*) as count FROM posts UNION ALL SELECT 'quotes', COUNT(*) FROM quotes UNION ALL SELECT 'hires', COUNT(*) FROM hires UNION ALL SELECT 'recommendations', COUNT(*) FROM recommendations;"

# View all data
mysql -u root unifyxproject -e "SELECT * FROM posts \G"
mysql -u root unifyxproject -e "SELECT * FROM quotes \G"
mysql -u root unifyxproject -e "SELECT * FROM hires \G"
mysql -u root unifyxproject -e "SELECT * FROM recommendations \G"
```

---

## Performance Optimization

### If Recommendations are Slow

1. **Add indexes:**
   ```sql
   CREATE INDEX idx_worker_category ON worker_profile(worker_id);
   CREATE INDEX idx_post_category ON posts(worker_category);
   CREATE INDEX idx_recommendation_post ON recommendations(post_id);
   ```

2. **Limit workers queried:**
   Edit RecommendationService.java:
   ```java
   // Limit to recently active workers instead of all
   List<WorkerProfile> activeWorkers = workerProfileRepository
       .findByLastActiveAfter(LocalDateTime.now().minusDays(30));
   ```

3. **Cache results:**
   Add to RecommendationService:
   ```java
   @Cacheable(value = "recommendations", key = "#postId")
   public List<Recommendation> getTopRecommendationsForPost(int postId)
   ```

### If Frontend is Sluggish

1. **Use pagination:**
   ```java
   @GetMapping("/quotes/post/{postId}")
   public Page<Quote> getQuotesForPost(@PathVariable int postId, Pageable pageable)
   ```

2. **Lazy load images:**
   Use Glide library for worker avatars

3. **Pagination in RecyclerView:**
   Implement `PaginationScrollListener`

---

## Getting Help

1. **Check Logs:**
   - Backend: `tail -f nohup.out`
   - Android: Logcat in Android Studio
   - MySQL: `/var/log/mysql/error.log`

2. **Enable Debug Logging:**
   ```properties
   # In application.properties
   logging.level.root=INFO
   logging.level.org.example.unifyx=DEBUG
   logging.level.org.hibernate.SQL=DEBUG
   logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
   ```

3. **Use Debugger:**
   - Set breakpoints in Android Studio / IntelliJ
   - Step through code to verify logic

4. **Common Mistakes:**
   - Wrong localhost (localhost vs 10.0.2.2)
   - Missing location data (latitude/longitude)
   - Mismatched entity field names
   - Missing foreign key relationships
   - Not starting MySQL/backend before running app

