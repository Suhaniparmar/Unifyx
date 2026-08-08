package com.example.unifyx.owner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private EditText etDescription, etSiteAddress, etSiteLocation, etDuration;
    private Button btnSelectImages, btnSubmit;
    private Spinner spinnerCategory, spinnerSubCategory;
    private ProgressBar progressBar;
    private ApiService apiService;
    private String uid;
    private Map<String, List<String>> subCategoryMap;
    private String selectedCategory = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_create);

        RetrofitClient retrofitClient = new RetrofitClient();
        apiService = retrofitClient.getRetrofit().create(ApiService.class);

        etDescription = findViewById(R.id.et_description);
        etSiteAddress = findViewById(R.id.et_site_address);
        etSiteLocation = findViewById(R.id.et_site_location);
        etDuration = findViewById(R.id.et_duration);
        btnSelectImages = findViewById(R.id.btn_select_images);
        btnSubmit = findViewById(R.id.btn_submit);
        ImageView backButton = findViewById(R.id.btn_back);
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerSubCategory = findViewById(R.id.spinner_sub_category);
        imagePreviewContainer = findViewById(R.id.image_preview_container);
        progressBar = findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.GONE);

        backButton.setOnClickListener(v -> finish());
        btnSelectImages.setOnClickListener(v -> selectImages());
        setupCategoryDropdown();
        btnSubmit.setOnClickListener(v -> submitPost());
    }
    private void setupCategoryDropdown() {
        // Main categories
        List<String> categories = new ArrayList<>();
        categories.add("Select Category");
        categories.add("Worker");
        categories.add("Contractor");

        // Subcategory mapping
        subCategoryMap = new HashMap<>();
        subCategoryMap.put("Worker", List.of("Select worker category","Painter", "Electrician", "Plaster Worker", "Tile Maker", "Carpenter", "Mason", "Welder", "Roofer", "Steel Fixer", "Concrete Laborer", "Pipefitter", "Scaffolder", "Glazier", "Insulation Worker", "Landscaper", "Paver", "Demolition Worker", "Crane Operator", "Heavy Equipment Operator", "Surveyor"));
        subCategoryMap.put("Contractor", List.of("Select contractor category","Painting Contractor", "Electrical Contractor", "General Contractor", "Plumbing Contractor", "HVAC Contractor", "Roofing Contractor", "Flooring Contractor", "Masonry Contractor", "Carpentry Contractor", "Steel Erection Contractor", "Concrete Contractor", "Landscaping Contractor", "Demolition Contractor", "Insulation Contractor", "Glazing Contractor", "Scaffolding Contractor", "Excavation Contractor", "Drywall Contractor", "Tiling Contractor", "Fire Protection Contractor"));

        // Set main category adapter
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(categoryAdapter);

        // Handle category selection
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Ignore "Select Category" option
                    selectedCategory = categories.get(position); // Store main category
                    updateSubCategoryDropdown(subCategoryMap.get(selectedCategory));
                    spinnerSubCategory.setVisibility(View.VISIBLE);
                } else {
                    selectedCategory = ""; // Reset if no valid category is selected
                    spinnerSubCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Handle subcategory selection
        spinnerSubCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Ignore "Select Subcategory" option
                    selectedCategory = subCategoryMap.get(spinnerCategory.getSelectedItem().toString()).get(position);
                } else {
                    selectedCategory = ""; // Reset if no valid subcategory is selected
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateSubCategoryDropdown(List<String> subCategories) {
        ArrayAdapter<String> subCategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subCategories);
        spinnerSubCategory.setAdapter(subCategoryAdapter);
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
        SharedPreferences sharedPreferences2 = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        this.uid = sharedPreferences2.getString("uid", null);

        String descriptionText = etDescription.getText().toString().trim();
        String siteAddressText = etSiteAddress.getText().toString().trim();
        String siteLocationText = etSiteLocation.getText().toString().trim();
        String durationText = etDuration.getText().toString().trim();

        if (uid == null || uid.trim().isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (descriptionText.isEmpty() || siteAddressText.isEmpty() || siteLocationText.isEmpty() || durationText.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCategory == null || selectedCategory.trim().isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), descriptionText);
        RequestBody workerCategory = RequestBody.create(MediaType.parse("text/plain"), selectedCategory);
        RequestBody siteAddress = RequestBody.create(MediaType.parse("text/plain"), siteAddressText);
        RequestBody siteLocation = RequestBody.create(MediaType.parse("text/plain"), siteLocationText);
        RequestBody duration = RequestBody.create(MediaType.parse("text/plain"), durationText);
        RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), this.uid.trim()); // Non-null after validation


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
        Log.d(TAG, "Owner UID: " + this.uid);

        apiService.createPost(uid,description, workerCategory, siteAddress, siteLocation, duration, imageParts)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Toast.makeText(PostCreate.this, "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PostCreate.this, OwnerProfilePage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            try {
                                Log.e(TAG, "Post upload failed: " + response.errorBody().string());
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading response", e);
                            }
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
