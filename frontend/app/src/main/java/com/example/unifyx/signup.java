package com.example.unifyx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unifyx.auth.AuthErrorMapper;
import com.example.unifyx.common.NetworkUtils;
import com.example.unifyx.contractor.contractor_home;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.owner.owner_home;
import com.example.unifyx.worker.WorkerHome;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class signup extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Button signup_btn, googleSignupBtn;
    private EditText mobile_et, otp_et;
    private TextView login_txt, info_txt;
    private ApiService apiService;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private boolean otpSent = false;
    private boolean isAuthInProgress = false;

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth == null) {
            return;
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getUid() != null) {
            cacheIdentity(currentUser);
            fetchUserRoleFromServer(currentUser.getUid());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        signup_btn = findViewById(R.id.btn_submit);
        googleSignupBtn = findViewById(R.id.btn_google_signup);
        mobile_et = findViewById(R.id.et_mobile);
        otp_et = findViewById(R.id.et_password);
        login_txt = findViewById(R.id.tv_log_in);
        info_txt = findViewById(R.id.info_text);
        progressBar = findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() == null) {
                        setLoading(false);
                        Toast.makeText(signup.this, "Google sign-up cancelled", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                                .getResult(ApiException.class);
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        setLoading(false);
                        Log.e(TAG, "Google sign-up failed", e);
                        Toast.makeText(signup.this, "Google sign-up failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        login_txt.setOnClickListener(view -> {
            Intent intent = new Intent(signup.this, login.class);
            startActivity(intent);
            finish();
        });

        googleSignupBtn.setOnClickListener(view -> startGoogleSignUp());

        signup_btn.setOnClickListener(view -> {
            if (!otpSent) {
                sendOtp();
            } else {
                verifyOtp();
            }
        });
    }

    private void sendOtp() {
        if (isAuthInProgress) {
            return;
        }
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String mobile = mobile_et.getText().toString().trim();

        if (TextUtils.isEmpty(mobile)) {
            Toast.makeText(signup.this, "Enter mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        String normalizedMobile = normalizeMobile(mobile);
        if (!isValidMobile(normalizedMobile)) {
            Toast.makeText(signup.this, "Enter a valid 10-digit Indian mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNumber = "+91" + normalizedMobile;
        setLoading(true);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        setLoading(false);
                        Log.e(TAG, "onVerificationFailed", e);
                        String reason = AuthErrorMapper.mapPhoneVerificationFailure(e);
                        info_txt.setText(reason);
                        Toast.makeText(signup.this, reason, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        setLoading(false);
                        signup.this.verificationId = verificationId;
                        signup.this.resendToken = token;
                        otpSent = true;

                        otp_et.setVisibility(View.VISIBLE);
                        info_txt.setText("OTP sent to " + phoneNumber);
                        signup_btn.setText("Verify OTP");
                        mobile_et.setEnabled(false);

                        Toast.makeText(signup.this, "OTP sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String verificationId) {
                        super.onCodeAutoRetrievalTimeOut(verificationId);
                        signup.this.verificationId = verificationId;
                        setLoading(false);
                        Toast.makeText(signup.this, "Auto-retrieval timed out. Enter OTP manually.", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOtp() {
        if (isAuthInProgress) {
            return;
        }
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        String otp = otp_et.getText().toString().trim();

        if (TextUtils.isEmpty(otp) || otp.length() != 6) {
            Toast.makeText(signup.this, "Enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(verificationId)) {
            Toast.makeText(this, "OTP session expired. Please request OTP again.", Toast.LENGTH_SHORT).show();
            resetOtpState();
            return;
        }

        setLoading(true);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);

                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null && user.getUid() != null) {
                            cacheIdentity(user);
                            fetchUserRoleFromServer(user.getUid());
                        } else {
                            Toast.makeText(signup.this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(signup.this, AuthErrorMapper.mapOtpSignInFailure(task.getException()), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startGoogleSignUp() {
        if (isAuthInProgress) {
            return;
        }
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isGoogleConfigured()) {
            Toast.makeText(this, "Configure Google Web Client ID in strings.xml first", Toast.LENGTH_LONG).show();
            return;
        }
        setLoading(true);
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        if (TextUtils.isEmpty(idToken)) {
            setLoading(false);
            Toast.makeText(this, "Google sign-up failed. Please retry.", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.getUid() != null) {
                            cacheIdentity(user);
                            fetchUserRoleFromServer(user.getUid());
                        } else {
                            Toast.makeText(signup.this, "Google sign-up failed. Please retry.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(signup.this, AuthErrorMapper.mapGoogleSignInFailure(task.getException()), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUserRoleFromServer(String uid) {
        if (TextUtils.isEmpty(uid)) {
            Toast.makeText(this, "Session error. Please sign up again.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getUserRole(uid).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().containsKey("role")) {
                    openRoleHome(response.body().get("role"));
                    return;
                }

                if (response.code() == 404 || (response.body() != null && response.body().containsKey("error"))) {
                    Toast.makeText(signup.this, "Account verified. Please select your role.", Toast.LENGTH_SHORT).show();
                    redirectToChooseRole();
                    return;
                }

                if (response.code() >= 500) {
                    Toast.makeText(signup.this, "Server unavailable. Please try again shortly.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(signup.this, "Unable to fetch role. Please retry.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e(TAG, "fetchUserRoleFromServer failed", t);
                Toast.makeText(signup.this, "Could not connect to server. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openRoleHome(String role) {
        String normalizedRole = role == null ? "" : role.trim().toLowerCase();
        Intent intent;
        switch (normalizedRole) {
            case "owner":
                intent = new Intent(signup.this, owner_home.class);
                break;
            case "worker":
                intent = new Intent(signup.this, WorkerHome.class);
                break;
            case "contractor":
                intent = new Intent(signup.this, contractor_home.class);
                break;
            default:
                Toast.makeText(this, "Unknown role. Please choose role again.", Toast.LENGTH_SHORT).show();
                redirectToChooseRole();
                return;
        }
        startActivity(intent);
        finish();
    }

    private void redirectToChooseRole() {
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user != null ? user.getUid() : null;
        String email = getSafeEmail(user);

        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "User data missing. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
        editor.putString("uid", uid);
        editor.putString("email", email);
        editor.apply();

        Intent intent = new Intent(signup.this, choose_role.class);
        intent.putExtra("uid", uid);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    private void cacheIdentity(FirebaseUser user) {
        if (user == null || TextUtils.isEmpty(user.getUid())) {
            return;
        }
        String email = getSafeEmail(user);
        if (TextUtils.isEmpty(email)) {
            return;
        }
        SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
        editor.putString("uid", user.getUid());
        editor.putString("email", email);
        editor.apply();
    }

    private String getSafeEmail(FirebaseUser user) {
        if (user == null) {
            return null;
        }
        String phoneNumber = user.getPhoneNumber();
        if (!TextUtils.isEmpty(phoneNumber)) {
            return phoneNumber;
        }
        String accountEmail = user.getEmail();
        if (!TextUtils.isEmpty(accountEmail)) {
            return accountEmail;
        }
        return user.getUid() + "@unifyx.app";
    }

    private void setLoading(boolean loading) {
        isAuthInProgress = loading;
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        signup_btn.setEnabled(!loading);
        googleSignupBtn.setEnabled(!loading);
        otp_et.setEnabled(!loading);
        if (!otpSent) {
            mobile_et.setEnabled(!loading);
        }
    }

    private void resetOtpState() {
        otpSent = false;
        verificationId = null;
        resendToken = null;
        otp_et.setText("");
        otp_et.setVisibility(View.GONE);
        mobile_et.setEnabled(true);
        signup_btn.setText("Send OTP");
        info_txt.setText("");
    }

    private String normalizeMobile(String mobile) {
        return mobile.replaceAll("\\D", "");
    }

    private boolean isValidMobile(String mobileDigits) {
        return mobileDigits.length() == 10 && mobileDigits.matches("[6-9]\\d{9}");
    }

    private boolean isGoogleConfigured() {
        String webClientId = getString(R.string.default_web_client_id);
        return !TextUtils.isEmpty(webClientId) && !webClientId.startsWith("YOUR_WEB_CLIENT_ID");
    }
}
