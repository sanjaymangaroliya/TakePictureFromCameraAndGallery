package com.takepicturefromcameraandgallery.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.takepicturefromcameraandgallery.R;
import com.takepicturefromcameraandgallery.adapter.GridViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MultipleImageActivity extends AppCompatActivity {

    private GridView grid_view;
    private ArrayList<HashMap<String, String>> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple_image);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        initUI();
    }

    private void initUI() {
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(getResources().getString(R.string.multiple_image));
        grid_view = findViewById(R.id.grid_view);
    }

    //---------------------- Image Dialog ----------------------//
    public void onClickMultipleImage(View view) {
        if (Build.VERSION.SDK_INT >= 23) {
            requestStoragePermission();
        } else {
            imagePicker();
        }
    }

    private void imagePicker() {
        ImagePicker.with(this)
                .setFolderMode(true)
                .setCameraOnly(false)
                .setFolderTitle("Album")
                .setMultipleMode(true)
                .setMaxSize(9)
                .setKeepScreenOn(true)
                .start();

        /*ImagePicker.with(this)                         //  Initialize ImagePicker with activity or fragment context
           .setToolbarColor("#212121")         //  Toolbar color
           .setStatusBarColor("#000000")       //  StatusBar color (works with SDK >= 21  )
           .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
           .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
           .setProgressBarColor("#4CAF50")     //  ProgressBar color
           .setBackgroundColor("#212121")      //  Background color
           .setCameraOnly(false)               //  Camera mode
           .setMultipleMode(true)              //  Select multiple images or single image
           .setFolderMode(true)                //  Folder mode
           .setShowCamera(true)                //  Show camera button
           .setFolderTitle("Albums")           //  Folder title (works with FolderMode = true)
           .setImageTitle("Galleries")         //  Image title (works with FolderMode = false)
           .setDoneTitle("Done")               //  Done button title
           .setLimitMessage("You have reached selection limit")    // Selection limit message
           .setMaxSize(10)                     //  Max images can be selected
           .setSavePath("ImagePicker")         //  Image capture folder name
           .setSelectedImages(images)          //  Selected images
           .setKeepScreenOn(true)              //  Keep screen on when selecting images
           .start();                           //  Start ImagePicker */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                imageList.clear();
                for (Image image : images) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("image_path", image.getPath());
                    imageList.add(hashMap);
                }
                grid_view.setAdapter(new GridViewAdapter(this, imageList));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // request storage permission
    private void requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            imagePicker();
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature." +
                " You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
