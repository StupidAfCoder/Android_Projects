package com.example.photogallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private String currentPhotoPath = "";

    // Launcher 1: TakePicture — you provide a destination URI, camera writes photo there
    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            success -> {
                if (success) Toast.makeText(this, "Photo saved!\n" + currentPhotoPath, Toast.LENGTH_LONG).show();
            }
    );

    // Launcher 2: OpenDocumentTree — shows Android's folder picker
    private final ActivityResultLauncher<Uri> folderPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocumentTree(),
            uri -> {
                if (uri != null) {
                    Intent intent = new Intent(this, GalleryActivity.class);
                    intent.putExtra("folder_uri", uri.toString());
                    startActivity(intent);
                }
            }
    );

    // Launcher 3: Permission request
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> {
                if (granted) launchCamera();
                else Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCamera = findViewById(R.id.btnCamera);
        Button btnBrowse = findViewById(R.id.btnBrowse);

        btnCamera.setOnClickListener(v -> checkPermissionAndLaunchCamera());
        btnBrowse.setOnClickListener(v -> folderPickerLauncher.launch(null));
    }

    private void checkPermissionAndLaunchCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        try {
            // Step 1: Create unique filename with timestamp
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            // Step 2: Get app's external Pictures folder
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            // Step 3: Create an empty .jpg file there
            File photoFile  = File.createTempFile("IMG_" + timeStamp + "_", ".jpg", storageDir);
            currentPhotoPath = photoFile.getAbsolutePath();

            // Step 4: FileProvider converts file path → content:// URI
            // The camera app needs this URI to know where to write the photo
            Uri photoUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    photoFile);
            // Step 5: Launch camera with the destination URI
            takePictureLauncher.launch(photoUri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating photo file", Toast.LENGTH_SHORT).show();
        }
    }
}