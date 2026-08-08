# Firebase PhoneAuth + Google Sign-In Implementation Guide

## ✅ What's Been Implemented

### 1. **PhoneAuth OTP Verification Flow**
   - **Login Screen** (`login.java` + `login.xml`)
     - Enter mobile number → System sends OTP via SMS
     - User enters OTP → Verified with Firebase PhoneAuth
     - On success → Fetch user role → Redirect to home screen
   
   - **Signup Screen** (`signup.java` + `signup.xml`)
     - Enter mobile number → System sends OTP via SMS
     - User enters OTP → New phone-auth account created
     - On success → Go to `choose_role` screen

### 2. **Google Sign-In Integration**
   - Both login and signup screens have "Continue with Google" button
   - Uses Firebase GoogleAuthProvider for credential-based sign-in
   - Works alongside PhoneAuth (users can choose either method)

### 3. **Updated Dependencies**
   - Added: `com.google.android.gms:play-services-auth:21.2.0`
   - Already had: Firebase Auth, Retrofit, Material, Glide

### 4. **AndroidManifest Permissions**
   - `android.permission.SEND_SMS`
   - `android.permission.READ_SMS`
   - `android.permission.RECEIVE_SMS`
   - (Note: SMS permissions are requested at runtime in Android 6.0+)

### 5. **UI/Layout Changes**
   - Removed password field from both login/signup
   - Added OTP field (hidden initially, shown after OTP is sent)
   - Info text displays: "OTP sent to +91XXXXXXXXXX"
   - Button label changes dynamically: "Send OTP" → "Verify OTP"

---

## 🔴 What You MUST Do Next

### **Step 1: Configure Google Web Client ID**
1. Go to **Firebase Console** → Select project `unifyx-ee9f8`
2. **Authentication** → **Sign-in method** → Enable **Google**
3. Go to **Project Settings** → **Service Accounts** → **Generate New Private Key** (JSON)
4. Extract the `client_id` from the JSON file (it looks like: `XXXXX-XXXXX.apps.googleusercontent.com`)
5. Open `app/src/main/res/values/strings.xml` and replace:
   ```xml
   <string name="default_web_client_id">YOUR_WEB_CLIENT_ID.apps.googleusercontent.com</string>
   ```
   with your actual client ID

### **Step 2: Get SHA-1 Certificate Fingerprint**
1. Run in terminal:
   ```bash
   cd /Users/suhaniparmar/Documents/Unifyx/Unifyx/frontend
   ./gradlew signingReport
   ```
2. Copy the `SHA1` value under `debugAndroidTest` or `debug`
3. Go to **Firebase Console** → **Project Settings** → **Your apps** → **Android app**
4. Add the SHA-1 fingerprint to the app configuration
5. Re-download `google-services.json` and replace `app/google-services.json`

### **Step 3: Test on Android Device/Emulator**
- **Important**: SMS verification only works on real devices, not all emulators
- If using emulator, use **Firebase Console** → **Authentication** → **Phone** tab to test with a test phone number
- Or test on a physical Android device with SMS enabled

---

## 📱 User Flow Diagram

### **Login via OTP:**
```
Login Screen
    ↓
Enter Mobile Number (10 digits)
    ↓
Click "Send OTP" button
    ↓
Firebase sends SMS with 6-digit code
    ↓
OTP field appears, user enters code
    ↓
Click "Verify OTP"
    ↓
[User authenticated]
    ↓
Fetch role from backend
    ↓
Redirect to Owner/Worker/Contractor Home
```

### **Signup via OTP:**
```
Signup Screen
    ↓
Enter Mobile Number (10 digits)
    ↓
Click "Send OTP" button
    ↓
Firebase sends SMS with 6-digit code
    ↓
OTP field appears, user enters code
    ↓
Click "Verify OTP"
    ↓
[New phone-auth account created]
    ↓
Save mobile + UID to SharedPreferences
    ↓
Redirect to choose_role screen
```

### **Google Sign-In (Both Screens):**
```
Click "Continue with Google"
    ↓
Google Sign-In consent dialog
    ↓
User selects Google account
    ↓
Firebase authenticates with Google credential
    ↓
For Login: Fetch role → Redirect to home
For Signup: Redirect to choose_role
```

---

## 🔧 Key Code Details

### **PhoneAuth OTP Flow (login.java)**
```java
// Send OTP
PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
    .setPhoneNumber("+91" + phoneNumber)  // Indian format
    .setTimeout(60L, TimeUnit.SECONDS)
    .setActivity(this)
    .setCallbacks(callbacks)  // Handles onCodeSent, onVerificationCompleted, onVerificationFailed
    .build();
PhoneAuthProvider.verifyPhoneNumber(options);

// Verify OTP
PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
mAuth.signInWithCredential(credential);
```

### **Google Sign-In Flow**
```java
// Setup
GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestIdToken(getString(R.string.default_web_client_id))
    .requestEmail()
    .build();

// Sign in
Intent signInIntent = googleSignInClient.getSignInIntent();
googleSignInLauncher.launch(signInIntent);

// Authenticate with Firebase
mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null));
```

---

## 📊 Firebase Console Checks

After Step 1-2, verify these in Firebase Console:

1. **Authentication** → **Users** tab should show:
   - Phone numbers (for PhoneAuth users)
   - Google accounts (for Google sign-in users)

2. **Authentication** → **Phone** tab:
   - Option to test with test phone numbers (for development)

3. **Authentication** → **Settings**:
   - Make sure "Email enumeration protection" is handled correctly

---

## ⚠️ Important Notes

1. **SMS Delivery**: 
   - India uses +91 country code (already hardcoded in login.java)
   - If supporting other countries, you'll need to detect country code
   - OTP valid for 60 seconds (configurable)

2. **Session Management**:
   - Phone number stored in Firebase Auth
   - Also cached in SharedPreferences for offline access
   - Used by backend to identify users

3. **Error Handling**:
   - Invalid mobile: "Enter a valid 10-digit mobile number"
   - Invalid OTP: "Enter a valid 6-digit OTP"
   - Verification failed: "OTP verification failed. Try again."
   - Network error: Handled by Firebase callbacks

4. **Edge Cases**:
   - Auto-verification: If SMS received on same device, OTP auto-fills
   - Resend OTP: Currently user must start over (can add resend button later)
   - Duplicate phone: Firebase prevents same phone number for new signups (optional email-based recovery can be added)

---

## 🚀 Next Steps (Optional Features)

1. **Add Resend OTP button** (after 30 seconds)
2. **Add Change Phone Number** option after OTP is sent
3. **Implement Email-based backup** (phone auth + email for password recovery)
4. **Add SMS provider selection** (Twilio vs Firebase built-in)
5. **Implement User Phone Verification** endpoint on backend to store verified phone

---

## ✅ Testing Checklist

- [ ] Mobile number validation (10 digits, no spaces/dashes)
- [ ] OTP sending on real device
- [ ] OTP verification success
- [ ] Error on wrong OTP
- [ ] Google sign-in works
- [ ] User redirects correctly after auth
- [ ] SharedPreferences stores phone + UID correctly
- [ ] Role fetching works after login
- [ ] Auto-logout if Firebase session expires

---

## 📞 Support

If you encounter issues:
1. Check Firebase Console → Authentication → Logs for errors
2. Check Logcat in Android Studio for detailed error messages
3. Verify SHA-1 fingerprint in Firebase Console matches device
4. Ensure Google Web Client ID is correctly set in strings.xml

