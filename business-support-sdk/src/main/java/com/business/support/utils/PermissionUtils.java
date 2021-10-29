package com.business.support.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.business.support.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by jiantao.tu on 2018/9/20.
 */
public class PermissionUtils {

    private static final String TAG = PermissionUtils.class.getSimpleName() + ":";
    public static final int CODE_RECORD_AUDIO = 0;
    public static final int CODE_GET_ACCOUNTS = 1;
    public static final int CODE_READ_PHONE_STATE = 2;
    public static final int CODE_CALL_PHONE = 3;
    public static final int CODE_CAMERA = 4;
    public static final int CODE_ACCESS_FINE_LOCATION = 5;
    public static final int CODE_ACCESS_COARSE_LOCATION = 6;
    public static final int CODE_READ_EXTERNAL_STORAGE = 7;
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 8;
    public static final int CODE_PERMISSION_WRITE_SECURE_SETTINGS = 9;
    public static final int CODE_MULTI_PERMISSION = 100;

    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_WRITE_SECURE_SETTINGS = Manifest.permission.WRITE_SECURE_SETTINGS;


    private static final String[] requestPermissions = {
            PERMISSION_RECORD_AUDIO,
            PERMISSION_GET_ACCOUNTS,
            PERMISSION_READ_PHONE_STATE,
            PERMISSION_CALL_PHONE,
            PERMISSION_CAMERA,
            PERMISSION_ACCESS_FINE_LOCATION,
            PERMISSION_ACCESS_COARSE_LOCATION,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE,
            PERMISSION_WRITE_SECURE_SETTINGS
    };

    public interface PermissionGrant {
        void onPermissionGranted(int requestCode);
    }

    public interface PermissionOver {
        /**
         *
         * @param overCode 1等于成功，2等于失败，3重新请求我
         */
        void over(int overCode);
    }

    /**
     * Requests permission.
     *
     * @param activity
     * @param requestCode request code, e.g. if you need request CAMERA permission,parameters is PermissionUtils.CODE_CAMERA
     */
    public static void requestPermission(final Activity activity, final int requestCode, PermissionGrant permissionGrant, PermissionOver
            permissionOver) {
        if (activity == null) {
            return;
        }

        Log.i(TAG, "requestPermission requestCode:" + requestCode);
        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            Log.w(TAG, "requestPermission illegal requestCode:" + requestCode);
            return;
        }

        final String requestPermission = requestPermissions[requestCode];

        //如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED，
        // 但是，如果用户关闭了你申请的权限，ActivityCompat.checkSelfPermission(),会导致程序崩溃(java.lang.RuntimeException: Unknown exception code: 1 msg null)，
        // 你可以使用try{}catch(){},处理异常，也可以在这个地方，低于23就什么都不做，
        // 个人建议try{}catch(){}单独处理，提示用户开启权限。
//        if (Build.VERSION.SDK_INT < 23) {
//            return;
//        }

        int checkSelfPermission;
        try {
            checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
        } catch (RuntimeException e) {
            Toast.makeText(activity, "please open this permission", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "RuntimeException:" + e.getMessage());
            if (permissionOver != null) permissionOver.over(2);
            return;
        }

        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "ActivityCompat.checkSelfPermission != PackageManager.PERMISSION_GRANTED");


            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                Log.i(TAG, "requestPermission shouldShowRequestPermissionRationale");
                shouldShowRationale(activity, requestCode, requestPermission, permissionOver);
            } else {
                Log.d(TAG, "requestCameraPermission else");
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
            }
        } else {
            Log.d(TAG, "ActivityCompat.checkSelfPermission ==== PackageManager.PERMISSION_GRANTED");
            permissionGrant.onPermissionGranted(requestCode);
            if (permissionOver != null) permissionOver.over(1);
        }
    }

    private static void requestMultiResult(Activity activity, String[] permissions, int[] grantResults, PermissionGrant permissionGrant,
                                           PermissionOver permissionOver, String explain) {

        if (activity == null) {
            return;
        }

        //TODO
        Log.d(TAG, "onRequestPermissionsResult permissions length:" + permissions.length);
        Map<String, Integer> perms = new HashMap<>();

        ArrayList<String> notGranted = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            Log.d(TAG, "permissions: [i]:" + i + ", permissions[i]" + permissions[i] + ",grantResults[i]:" + grantResults[i]);
            perms.put(permissions[i], grantResults[i]);
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                notGranted.add(permissions[i]);
            }
        }

        if (notGranted.size() == 0) {
//            Toast.makeText(activity, "all permission success" + notGranted, Toast.LENGTH_SHORT)
//                    .show();
            permissionGrant.onPermissionGranted(CODE_MULTI_PERMISSION);
            if (permissionOver != null) permissionOver.over(1);
        } else {
            openSettingActivity(activity, explain, permissionOver, permissions);
        }

    }

//    public static void requestMultiPermissions(final Activity activity, PermissionGrant grant, PermissionOver permissionOver) {
//        requestMultiPermissions(activity, requestPermissions, grant, permissionOver);
//    }

    /**
     * 一次申请多个权限
     */
    public static void requestMultiPermissions(final Activity activity, String[] permissions, PermissionGrant grant, PermissionOver permissionOver, String explain) {

        final List<String> permissionsList = getNoGrantedPermission(activity, permissions, false);
        final List<String> shouldRationalePermissionsList = getNoGrantedPermission(activity, permissions, true);

        //TODO checkSelfPermission
        if (permissionsList == null || shouldRationalePermissionsList == null) {
            return;
        }
        Log.d(TAG, "requestMultiPermissions permissionsList:" + permissionsList.size() + ",shouldRationalePermissionsList:" +
                shouldRationalePermissionsList.size());

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
                    CODE_MULTI_PERMISSION);
            Log.d(TAG, "showMessageOKCancel requestPermissions");

        } else if (shouldRationalePermissionsList.size() > 0) {
            showMessageOKCancel(activity, explain,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, shouldRationalePermissionsList.toArray(new
                                            String[shouldRationalePermissionsList.size()]),
                                    CODE_MULTI_PERMISSION);
                            Log.d(TAG, "showMessageOKCancel requestPermissions");
                        }
                    }, permissionOver);
        } else {
            grant.onPermissionGranted(CODE_MULTI_PERMISSION);
            if (permissionOver != null) permissionOver.over(1);
        }

    }


    private static void shouldShowRationale(final Activity activity, final int requestCode, final String requestPermission, PermissionOver
            permissionOver) {
        //TODO
        String[] permissionsHint = activity.getResources().getStringArray(R.array.permissions);
        showMessageOKCancel(activity, "Rationale: " + permissionsHint[requestCode], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
                Log.d(TAG, "showMessageOKCancel requestPermissions:" + requestPermission);
            }
        }, permissionOver);
    }

    private static void showMessageOKCancel(final Activity context, String message, DialogInterface.OnClickListener okListener
            , final PermissionOver permissionOver) {


        Dialog dialog = new AlertDialog.Builder(context, context.getResources().getIdentifier("Theme_AppCompat_Light_Dialog_Alert", "style", context.getPackageName()))
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (permissionOver != null) permissionOver.over(2);
                    }
                })
                .create();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Objects.requireNonNull(dialog.getWindow()).setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//        } else {
//            Objects.requireNonNull(dialog.getWindow()).setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        }
        dialog.show();
    }

    /**
     * @param activity
     * @param requestCode  Need consistent with requestPermission
     * @param permissions
     * @param grantResults
     */
    public static void requestPermissionsResult(final Activity activity, final int requestCode, @NonNull String[] permissions,
                                                @NonNull int[] grantResults, PermissionGrant permissionGrant, PermissionOver permissionOver, boolean isAgain, String explain) {

        if (activity == null) {
            return;
        }
        Log.d(TAG, "requestPermissionsResult requestCode:" + requestCode);

        if (requestCode == CODE_MULTI_PERMISSION) {
            requestMultiResult(activity, permissions, grantResults, permissionGrant, permissionOver, explain);
            return;
        }

        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            Log.w(TAG, "requestPermissionsResult illegal requestCode:" + requestCode);
            Toast.makeText(activity, "illegal requestCode:" + requestCode, Toast.LENGTH_SHORT).show();
            if (permissionOver != null) permissionOver.over(2);
            return;
        }

        Log.i(TAG, "onRequestPermissionsResult requestCode:" + requestCode + ",permissions:" + Arrays.toString(permissions)
                + ",grantResults:" + Arrays.toString(grantResults) + ",length:" + grantResults.length);

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "onRequestPermissionsResult PERMISSION_GRANTED");
            //TODO success, do something, can use callback
            permissionGrant.onPermissionGranted(requestCode);
            if (permissionOver != null) permissionOver.over(1);
        } else {
            if (isAgain) {
                //TODO hint user this permission function
                Log.i(TAG, "onRequestPermissionsResult PERMISSION NOT GRANTED");
                //TODO
                String[] permissionsHint = activity.getResources().getStringArray(R.array.permissions);
                openSettingActivity(activity, permissionsHint[requestCode], permissionOver, permissions);
            } else {
                if (permissionOver != null) permissionOver.over(2);
            }

        }


    }

    private static void openSettingActivity(final Activity activity, String message, PermissionOver permissionOver, final String[] permissions) {

        showMessageOKCancel(activity, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Arrays.binarySearch(permissions, PERMISSION_WRITE_SECURE_SETTINGS) > -1) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                            Uri.parse("package:" + activity.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    return;
                }
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Log.d(TAG, "getPackageName(): " + activity.getPackageName());
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
                if (permissionOver != null) {
                    permissionOver.over(3);
                }
            }
        }, permissionOver);
    }


    /**
     * @param activity
     * @param isShouldRationale true: return no granted and shouldShowRequestPermissionRationale permissions, false:return no granted and
     *                          !shouldShowRequestPermissionRationale
     * @return
     */
    private static ArrayList<String> getNoGrantedPermission(Activity activity, String[] requestPermissions, boolean isShouldRationale) {

        ArrayList<String> permissions = new ArrayList<>();

        for (String requestPermission : requestPermissions) {
            //TODO checkSelfPermission
            int checkSelfPermission;
            try {
                checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
            } catch (RuntimeException e) {
                Toast.makeText(activity, "please open those permission", Toast.LENGTH_SHORT)
                        .show();
                Log.e(TAG, "RuntimeException:" + e.getMessage());
                return null;
            }

            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "getNoGrantedPermission ActivityCompat.checkSelfPermission != PackageManager.PERMISSION_GRANTED:" + requestPermission);

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                    Log.d(TAG, "shouldShowRequestPermissionRationale if");
                    if (isShouldRationale) {
                        permissions.add(requestPermission);
                    }

                } else {

                    if (!isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                    Log.d(TAG, "shouldShowRequestPermissionRationale else");
                }

            }
        }

        return permissions;
    }
}
