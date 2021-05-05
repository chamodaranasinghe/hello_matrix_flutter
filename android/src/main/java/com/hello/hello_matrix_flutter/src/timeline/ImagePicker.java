package com.hello.hello_matrix_flutter.src.timeline;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class ImagePicker {

    private static ImagePicker INSTANCE = null;

    private ImagePicker() {
    }

    public static ImagePicker getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImagePicker();
        }
        return (INSTANCE);
    }

    final int PICK_IMAGE = 1;
    public Activity activity;

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(activity, intent, PICK_IMAGE, null);
        
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
