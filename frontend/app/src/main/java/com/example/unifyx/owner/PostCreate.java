package com.example.unifyx.owner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unifyx.R;
import com.example.unifyx.network.ApiService;
import com.example.unifyx.network.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostCreate extends AppCompatActivity {
    private static final String TAG = "PostCreate";
    private static final int PICK_IMAGES_REQUEST = 1;
    private List<Uri> imageUris = new ArrayList<>();
    private LinearLayout imagePreviewContainer;
    private EditText etDescription, etWorkerCategory, etSiteAddress, etSiteLocation, etDuration;
    private Button btnSelectImages, btnSubmit;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_create);

        apiService = RetrofitClient.getRetrofit().create(ApiService.class);

        etDescription = findViewById(R.id.et_description);
        etWorkerCategory = findViewById(R.id.et_worker_category);
        etSiteAddress = findViewById(R.id.et_site_address);
        etSiteLocation = findViewById(R.id.et_site_location);
        etDuration = findViewById(R.id.et_duration);
        btnSelectImages = findViewById(R.id.btn_select_images);
        btnSubmit = findViewById(R.id.btn_submit);
        imagePreviewContainer = findViewById(R.id.image_preview_container);
        progressBar = findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.GONE);

        btnSelectImages.setOnClickListener(v -> selectImages());
        btnSubmit.setOnClickListener(v -> submitPost());
    }

    private void selectImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGES_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUris.clear();
            imagePreviewContainer.removeAllViews();

            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                    displayImagePreview(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
                displayImagePreview(imageUri);
            }
        }
    }

    private void displayImagePreview(Uri uri) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            imageView.setPadding(10, 10, 10, 10);
            imageView.setImageBitmap(bitmap);
            imagePreviewContainer.addView(imageView);
        } catch (Exception e) {
            Log.e(TAG, "Error displaying image preview", e);
        }
    }

    private void submitPost() {
        progressBar.setVisibility(View.VISIBLE);
        createPostWithImages();
    }

    private void createPostWithImages() {
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), etDescription.getText().toString());
        RequestBody workerCategory = RequestBody.create(MediaType.parse("text/plain"), etWorkerCategory.getText().toString());
        RequestBody siteAddress = RequestBody.create(MediaType.parse("text/plain"), etSiteAddress.getText().toString());
        RequestBody siteLocation = RequestBody.create(MediaType.parse("text/plain"), etSiteLocation.getText().toString());
        RequestBody duration = RequestBody.create(MediaType.parse("text/plain"), etDuration.getText().toString());

        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (Uri uri : imageUris) {
            String filePath = getPathFromUri(this, uri);
            if (filePath != null) {
                File file = new File(filePath);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
                imageParts.add(body);
            }
        }

        apiService.createPost(description, workerCategory, siteAddress, siteLocation, duration, imageParts)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Toast.makeText(PostCreate.this, "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Post upload failed: " + response.errorBody());
                            Toast.makeText(PostCreate.this, "Post upload failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "Post creation failed", t);
                    }
                });
    }

    public String getPathFromUri(Context context, Uri uri) {
        String filePath = null;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            File tempFile = File.createTempFile("upload", ".jpg", context.getCacheDir());
            OutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            filePath = tempFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "Error copying file", e);
        }
        return filePath;
    }
}
