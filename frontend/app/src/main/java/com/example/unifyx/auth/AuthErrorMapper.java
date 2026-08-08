package com.example.unifyx.auth;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.Locale;

public final class AuthErrorMapper {

    private AuthErrorMapper() {
    }

    public static String mapPhoneVerificationFailure(Exception e) {
        if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
            return "Unable to start app verification screen. Restart the app and try again.";
        }
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return "Invalid phone number format. Please check and try again.";
        }
        if (e instanceof FirebaseTooManyRequestsException) {
            return "Too many attempts. Please wait a few minutes and try again.";
        }
        if (e instanceof FirebaseNetworkException) {
            return "No internet connection. Please check your network and retry.";
        }
        if (e instanceof FirebaseAuthException) {
            String code = ((FirebaseAuthException) e).getErrorCode();
            if ("ERROR_INVALID_PHONE_NUMBER".equals(code)) {
                return "Invalid phone number. Enter a valid 10-digit Indian mobile number.";
            }
            if ("ERROR_QUOTA_EXCEEDED".equals(code) || "ERROR_SMS_QUOTA_EXCEEDED".equals(code)) {
                return "SMS quota exceeded. Try again later or use a test number in Firebase.";
            }
            if ("ERROR_CAPTCHA_CHECK_FAILED".equals(code)) {
                return "App verification failed. Please retry with stable internet.";
            }
            if ("ERROR_WEB_CONTEXT_CANCELED".equals(code)) {
                return "Verification was canceled. Please try again.";
            }
            if ("ERROR_APP_NOT_AUTHORIZED".equals(code)
                    || "ERROR_INVALID_APP_CREDENTIAL".equals(code)
                    || "ERROR_MISSING_APP_CREDENTIAL".equals(code)) {
                return "Phone auth is not configured correctly. Check Firebase SHA keys and Phone sign-in settings.";
            }
            return "Phone verification failed. Please try again.";
        }

        String raw = e != null && e.getMessage() != null ? e.getMessage() : "";
        String normalized = raw.toLowerCase(Locale.US);
        if (normalized.contains("app verification")
                || normalized.contains("play integrity")
                || normalized.contains("recaptcha")
                || normalized.contains("safetynet")) {
            return "App verification failed. Use an emulator image with Play Store or a real device, then retry.";
        }
//        if (normalized.contains("quota") || normalized.contains("too many")) {
//            return "Too many OTP requests from this device/project. Please wait and try again later.";
//        }
        if (normalized.contains("app_not_authorized")
                || normalized.contains("invalid app credential")
                || normalized.contains("missing app credential")
                || normalized.contains("sha")) {
            return "Firebase phone auth setup is incomplete. Add SHA-1/SHA-256 in Firebase and download updated google-services.json.";
        }
        if (!raw.isEmpty()) {
            return "Verification failed: " + raw;
        }
        return "Verification failed. Please try again.";
    }

    public static String mapOtpSignInFailure(Exception e) {
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return "Invalid OTP. Please enter the correct 6-digit code.";
        }
        if (e instanceof FirebaseTooManyRequestsException) {
            return "Too many OTP attempts. Please wait and retry.";
        }
        if (e instanceof FirebaseNetworkException) {
            return "No internet connection. Please try again once connected.";
        }
        if (e instanceof FirebaseAuthInvalidUserException) {
            return "Session expired. Please request OTP again.";
        }
        return "OTP verification failed. Please try again.";
    }

    public static String mapGoogleSignInFailure(Exception e) {
        if (e instanceof FirebaseAuthUserCollisionException) {
            return "Account already exists with different sign-in method. Please use login.";
        }
        if (e instanceof FirebaseNetworkException) {
            return "No internet connection. Please check your network and retry.";
        }
        if (e instanceof FirebaseTooManyRequestsException) {
            return "Too many attempts. Please try Google sign-in later.";
        }
        if (e instanceof FirebaseAuthException) {
            return "Google authentication failed. Please try again.";
        }
        return "Unable to continue with Google. Please try again.";
    }
}

