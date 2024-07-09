package com.mfthc.gotogalleryjava;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.mfthc.gotogalleryjava.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Uri chosenUri;
    private Bitmap chosenBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        registerLauncher();
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void goToGallery(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(v, "Permission required to go to the gallery!", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Give Permission", v1 -> {
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                            }).show();
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            } else {
                Intent goToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(goToGallery);
            }
        } else {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(v, "Permission required to go to the gallery!", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Give Permission", v12 -> {
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }).show();
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            } else {
                Intent goToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(goToGallery);
            }
        }
    }

    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                Intent intent = result.getData();
                if (intent != null) {
                    chosenUri = intent.getData();
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), chosenUri);
                            chosenBitmap = ImageDecoder.decodeBitmap(source);
                            binding.imageView.setImageBitmap(chosenBitmap);
                        } else {
                            chosenBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), chosenUri);
                            binding.imageView.setImageBitmap(chosenBitmap);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getLocalizedMessage());
                    }
                }
            }
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                Intent goToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(goToGallery);

            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied by User", Toast.LENGTH_SHORT).show();
            }
        });
    }
}