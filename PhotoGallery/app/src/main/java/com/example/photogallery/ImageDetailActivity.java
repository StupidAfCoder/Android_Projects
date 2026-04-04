package com.example.photogallery;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_activity);

        String imageUriString = getIntent().getStringExtra("image_uri");
        if (imageUriString == null) return;
        Uri imageUri = Uri.parse(imageUriString);

        ImageView ivDetail  = findViewById(R.id.ivDetail);
        TextView  tvDetails = findViewById(R.id.tvDetails);
        Button    btnDelete = findViewById(R.id.btnDelete);

        // Load full image using Glide
        Glide.with(this).load(imageUri).into(ivDetail);

        // DocumentFile.fromSingleUri gives metadata for a specific file URI
        DocumentFile docFile = DocumentFile.fromSingleUri(this, imageUri);
        if (docFile != null) {
            String name         = docFile.getName() != null ? docFile.getName() : "Unknown";
            long   sizeBytes    = docFile.length();
            long   lastModified = docFile.lastModified();
            String sizeStr      = formatSize(sizeBytes);
            String dateStr      = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
                    .format(new Date(lastModified));

            tvDetails.setText(
                    "Name      : " + name + "\n" +
                            "URI       : " + imageUriString + "\n" +
                            "Size      : " + sizeStr + "\n" +
                            "Date Taken: " + dateStr
            );
        }

        // ── Delete with Confirmation Dialog ──────────────────
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to permanently delete this image?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        DocumentFile file = DocumentFile.fromSingleUri(this, imageUri);
                        boolean deleted = file != null && file.delete();
                        if (deleted) {
                            Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();
                            finish(); // go back to gallery
                        } else {
                            Toast.makeText(this, "Could not delete", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private String formatSize(long bytes) {
        if (bytes < 1024)              return bytes + " B";
        else if (bytes < 1024 * 1024)  return String.format("%.1f KB", bytes / 1024.0);
        else                           return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
}