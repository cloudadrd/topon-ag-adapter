package com.test.ad.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.business.support.utils.StatusBarUtils;

/**
 * Created by jiantao.tu on 2018/9/21.
 */
public class PermissionActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {


    public final static String PERMISSION_KEY = "PERMISSION_KEY";
    private static final String TAG = "PermissionActivity";


    public static void launch(Context context, int permissionCode) {
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PERMISSION_KEY, permissionCode);
        context.getApplicationContext().startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTransparent(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0f);
        int code = getIntent().getIntExtra(PERMISSION_KEY, -1);
        if (code == -1) return;
        PermissionUtils.requestPermission(this, code, mPermissionGrant, permissionOver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant, permissionOver,false);
        Log.i(TAG, "PermissionUtils:onRequestPermissionsResult");
    }

    PermissionUtils.PermissionOver permissionOver = this::finish;

    public PermissionUtils.PermissionGrant mPermissionGrant = requestCode -> {
        switch (requestCode) {
            case PermissionUtils.CODE_RECORD_AUDIO:
                Log.w(TAG, "Result Permission Grant CODE_RECORD_AUDIO");
                break;
            case PermissionUtils.CODE_GET_ACCOUNTS:
                Log.w(TAG, "Result Permission Grant CODE_GET_ACCOUNTS");
                break;
            case PermissionUtils.CODE_READ_PHONE_STATE:
                Log.w(TAG, "Result Permission Grant CODE_READ_PHONE_STATE");
                break;
            case PermissionUtils.CODE_CALL_PHONE:
                Log.w(TAG, "Result Permission Grant CODE_CALL_PHONE");
                break;
            case PermissionUtils.CODE_CAMERA:
                Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case PermissionUtils.CODE_ACCESS_FINE_LOCATION:
                Log.w(TAG, "Result Permission Grant CODE_ACCESS_FINE_LOCATION");
                break;
            case PermissionUtils.CODE_ACCESS_COARSE_LOCATION:
                Log.w(TAG, "Result Permission Grant CODE_ACCESS_COARSE_LOCATION");
                break;
            case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                Log.w(TAG, "Result Permission Grant CODE_READ_EXTERNAL_STORAGE");
                break;
            case PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE:
                Log.w(TAG, "Result Permission Grant CODE_WRITE_EXTERNAL_STORAGE");

//                    overridePendingTransition(0, 0);
                break;
            case PermissionUtils.CODE_PERMISSION_WRITE_SECURE_SETTINGS:
                Log.w(TAG, "Result Permission Grant CODE_PERMISSION_WRITE_SECURE_SETTINGS");
                break;
            default:
                break;
        }
    };

}
