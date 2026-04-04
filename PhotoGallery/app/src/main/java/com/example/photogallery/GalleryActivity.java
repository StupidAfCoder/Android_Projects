package com.example.photogallery;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        String folderUriString = getIntent().getStringExtra("folder_uri");
        if (folderUriString == null) { Toast.makeText(this, "No folder", Toast.LENGTH_SHORT).show(); finish(); return; }

        Uri folderUri = Uri.parse(folderUriString);
        List<Uri> images = loadImagesFromFolder(folderUri);

        if (images.isEmpty()) Toast.makeText(this, "No images found", Toast.LENGTH_LONG).show();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewGallery);
        // GridLayoutManager(context, 3) = 3 columns
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        ImageAdapter adapter = new ImageAdapter(images, imageUri -> {
            Intent intent = new Intent(this, ImageDetailActivity.class);
            intent.putExtra("image_uri", imageUri.toString());
            intent.putExtra("folder_uri", folderUriString);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    // Use DocumentFile API to list files in the chosen folder
    // Filter to files whose MIME type starts with "image/"
    private List<Uri> loadImagesFromFolder(Uri folderUri) {
        List<Uri> images = new ArrayList<>();
        try {
            DocumentFile folder = DocumentFile.fromTreeUri(this, folderUri);
            if (folder == null) return images;
            for (DocumentFile file : folder.listFiles()) {
                String mimeType = file.getType();
                if (mimeType != null && mimeType.startsWith("image/")) {
                    images.add(file.getUri());
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return images;
    }
}