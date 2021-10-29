package com.business.support.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.business.support.R;
import com.business.support.config.Assets;
import com.business.support.config.Const;
import com.business.support.utils.BeanUtils;
import com.business.support.utils.ContextHolder;
import com.business.support.utils.PermissionUtils;
import com.business.support.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class InnerWebViewActivity2 extends Activity {

    private static final int PROGRESSBAR = Utils.generateViewId();
    private static final int WEB_VIEW = Utils.generateViewId();
    public static final String KEY_URL = "link";
    public static final String KEY_IS_LOAD_BAR_HIDE = "isLoadBarHide";
    public static final String KEY_CLOSE_LINEAR = "closeLinear";
    private static final String TAG = "InnerWebViewActivity2";

    private ProgressBar progressBar;
//    private WebView webView;

    private View btnClose;

    private int currentIndex = -1;

    private boolean isBack = false;

    private boolean is302 = false;

    private long impTimestamp = 0;

    private AdVideoMediation mediationHelper = null;

    private boolean isLoadBarHide;

    @SuppressLint("StaticFieldLeak")
    private CacheWebView webView = null;

    public static void launch(Context context, String loadUrl, boolean isLoadBarHide) {
        Intent intent = new Intent(context, InnerWebViewActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, loadUrl);
        intent.putExtras(bundle);
        intent.putExtra(KEY_IS_LOAD_BAR_HIDE, isLoadBarHide);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant, permissionOver, true, permissionExplain);
        Log.i(TAG, "PermissionUtils:onRequestPermissionsResult");
    }

    PermissionUtils.PermissionOver permissionOver = overCode -> {
        String loadStr = String.format(Locale.getDefault(), "javascript:%s(%d)", "permissionResult", overCode);
        Log.d(AdVideoInterface.class.getName(), "permissionResult loaStr=" + loadStr);
        webView.loadUrl(loadStr);
    };

    private String permissionExplain = null;

    public void requestPermissions(String[] permissions, String explain) {
        permissionExplain = explain;
        PermissionUtils.requestMultiPermissions((Activity) webView.getCustomContext(), permissions, mPermissionGrant, permissionOver, explain);
    }

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
                Log.w(TAG, "Result Permission Grant CODE_CAMERA");
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

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        AppInstallReceiver.registerReceiver(getApplicationContext());
        View view = generateLayout(this);
        setContentView(view);
        String link = getIntent().getStringExtra(KEY_URL);  //跳转链接

        isLoadBarHide = getIntent().getBooleanExtra(KEY_IS_LOAD_BAR_HIDE, false);

        progressBar = findViewById(PROGRESSBAR);
        if (isLoadBarHide) {
            progressBar.setVisibility(View.GONE);
        }

        webView = findViewById(WEB_VIEW);

        //webview显示加载进度，WebChromeClient是WebView的辅助类，用来处理js，favicon和标题等一些操作
        webView.setWebChromeClient(new ChromeClient());
        // 让浏览器显示一个网页
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                consoleClose();
                super.onPageStarted(view, url, favicon);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                Const.HANDLER.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (is302) return;
                        consoleClose();
                        isBack = true;
                    }
                }, 1000);

                super.onPageFinished(view, url);
                impTimestamp = System.currentTimeMillis();
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //start 添加内开调转gp
//                if (LandingManager.isGooglePlayUrl(url)) {
//                    LandingManager.openGooglePlayWithExistUrl(requestHolder);
//                    return true;
//                }
                //end

                WebView.HitTestResult hit = webView.getHitTestResult();
                int hitType = hit.getType();
                if ((hitType == WebView.HitTestResult.SRC_ANCHOR_TYPE || hitType == WebView.HitTestResult.UNKNOWN_TYPE) &&
                        (url.contains(".html") || url.contains(".htm") || url.contains(".shtm"))) {//点击超链接或者js localhost.href
                    return false;
                }
                if (url.startsWith("http:") || url.startsWith("https:")) {

                    if (hitType == 0 && !isBack && !is302) {
                        is302 = true;
                        if (btnClose != null) btnClose.setVisibility(View.GONE);
                    }
                    if (url.contains(".apk")) {
                        try {
                            Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(viewIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    return false;
                } else {
                    try { // 以下固定写法
                        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    } catch (Exception e) {
                    }
                    return true;
                }
            }
        });

//        mediationHelper = AdVideoMediation.getInstance();
//
//        AdVideoInterface adVideoInterface = new AdVideoInterface(webView, mediationHelper);
//        mediationHelper.setContext(this);
//        mediationHelper.setAdVideoInterface(adVideoInterface);
//        mediationHelper.loadVideo();
//        webView.addJavascriptInterface(adVideoInterface, "android");
        webView.loadUrl(link);
        AppInstallReceiver.addInstallCallback(installCallback);
    }

    AppInstallReceiver.InstallCallback installCallback = new AppInstallReceiver.InstallCallback() {
        @Override
        public void success(String pkg) {
            webView.notifyDownStated(pkg, DownloadState.INSTALL_OK, 0);
        }
    };

//    private boolean isBackLoad() {
//        WebBackForwardList list = webView.copyBackForwardList();
//        currentIndex = list.getCurrentIndex();
//        return (list.getCurrentIndex() != list.getSize() - 1);
//    }

    private void consoleClose() {
        if (isBack) return;

        if (webView.canGoBack()) {
//            if (btnClose != null && !is302) btnClose.setVisibility(View.GONE);
        } else {
//            if (btnClose != null) btnClose.setVisibility(View.VISIBLE);
        }
    }


//    @SuppressLint("DefaultLocale")
//    private void sendTrackCloseLinear(long duration) {
//        List<String> trackUrls = getIntent().getStringArrayListExtra(KEY_CLOSE_LINEAR);
//        if (trackUrls == null || trackUrls.size() == 0) {
//            return;
//        }
//        SLog.i("InnerWebview", "entered Processing Event: closeLinear" + "-->" + trackUrls.toString());
//
//        List<String> urlsWithDuration = new ArrayList<>();
//        for (String url : trackUrls) {
//            if (url != null) {
//                url = String.format("%s&t=%d", url, duration);
//                urlsWithDuration.add(url);
//            }
//        }
//        TrackManager.sendVideoTrackUrls(urlsWithDuration);
//    }

    class ChromeClient extends WebChromeClient {


        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //动态在标题栏显示进度条
            progressBar.setProgress(newProgress);
            if (newProgress == 100) {  //加载完成，进度条消失
                progressBar.setVisibility(View.GONE);
            } else {
                if (!isLoadBarHide) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        // For Android >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            uploadMessageAboveL = filePathCallback;
            showSelectDialog();
            return true;
        }

    }

    private SelectDialog mSelectPhotoDialog;

    /**
     * 显示相册/拍照选择对话框
     */
    private void showSelectDialog() {
        mSelectPhotoDialog = new SelectDialog(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.tv_camera) {
                    dispatchTakePictureIntent();
                } else if (id == R.id.tv_photo) {
                    takePhoto();
                    //不管选择还是不选择，必须有返回结果，否则就会调用一次
                } else if (id == R.id.tv_cancel) {
                    if (uploadMessageAboveL == null) return;
                    uploadMessageAboveL.onReceiveValue(null);
                    uploadMessageAboveL = null;
                }
            }

        });
        mSelectPhotoDialog.show();

    }


    @Override
    public void onBackPressed() {
        if (!isBack) {
            isBack = true;
//            if (btnClose != null) btnClose.setVisibility(View.VISIBLE);
        }
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }


    public View generateLayout(Context context) {
        //最外层
        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(layoutParams);

        //进度条
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setId(PROGRESSBAR);
        BeanUtils.setFieldValue(progressBar, "mOnlyIndeterminate", Boolean.FALSE);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgressDrawable(Utils.getDrawable(android.R.drawable.progress_horizontal));
        progressBar.setIndeterminateDrawable(Utils.getDrawable(android.R.drawable.progress_indeterminate_horizontal));
        layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, Utils.dp2px(3));
        relativeLayout.addView(progressBar, layoutParams);

        //webview
        webView = new CacheWebView(context);
        webView.setContext(this);
        webView.setId(WEB_VIEW);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams1.addRule(RelativeLayout.BELOW, PROGRESSBAR);
        relativeLayout.addView(webView, layoutParams1);

        btnClose = new View(this);
        btnClose.setBackground(Assets.getDrawableFromBase64(getResources(), Assets.close_button_normal));
        btnClose.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnClose.setVisibility(View.GONE);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(
                Utils.dp2px(28), Utils.dp2px(28));
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams2.topMargin = Utils.dp2px(5);
        layoutParams2.rightMargin = Utils.dp2px(5);
        relativeLayout.addView(btnClose, layoutParams2);
        return relativeLayout;
    }

    public void takePhoto() {
//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        i.addCategory(Intent.CATEGORY_OPENABLE);
//        i.setType("image/*");
//        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);

        Intent albumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        albumIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, FILE_CHOOSER_RESULT_CODE);
    }


    //图片
    private final static int FILE_CHOOSER_RESULT_CODE = 128;
    //拍照
    private final static int FILE_CAMERA_RESULT_CODE = 129;

    //5.0以下使用
    private ValueCallback<Uri> uploadMessage;
    // 5.0及以上使用
    private ValueCallback<Uri[]> uploadMessageAboveL;

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = BSFileProvider.getUriForFile(this,
                        getPackageName() + ".takePhotoFileProvider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, FILE_CAMERA_RESULT_CODE);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == uploadMessage && null == uploadMessageAboveL) return;
        if (resultCode != RESULT_OK) {//同上所说需要回调onReceiveValue方法防止下次无法响应js方法
            if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(null);
                uploadMessageAboveL = null;
            }
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }
            return;
        }
        Uri result = null;
        if (requestCode == FILE_CAMERA_RESULT_CODE) {
            if (null != data && null != data.getData()) {
                result = data.getData();
            }
            if (result == null && hasFile(currentPhotoPath)) {
                result = Uri.fromFile(new File(currentPhotoPath));
            }
            if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(new Uri[]{result});
                uploadMessageAboveL = null;
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        } else if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (data != null) {
                result = data.getData();
            }
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

//    /**
//     * webview没有选择文件也要传null，防止下次无法执行
//     */
//    private void clearUploadMessage() {
//        if (uploadMessageAboveL != null) {
//            uploadMessageAboveL.onReceiveValue(null);
//            uploadMessageAboveL = null;
//        }
//        if (uploadMessage != null) {
//            uploadMessage.onReceiveValue(null);
//            uploadMessage = null;
//        }
//    }

    /**
     * 判断文件是否存在
     */
    public static boolean hasFile(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(Intent intent) {
        Uri[] results = null;
        if (intent != null) {
            String dataString = intent.getDataString();
            ClipData clipData = intent.getClipData();
            if (clipData != null) {
                results = new Uri[clipData.getItemCount()];
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    results[i] = item.getUri();
                }
            }
            if (dataString != null)
                results = new Uri[]{Uri.parse(dataString)};
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public View compat(Activity activity, int statusColor) {
        final int INVALID_VAL = -1;
        int color = ContextCompat.getColor(activity, R.color.colorPrimaryDark);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (statusColor != INVALID_VAL) {
                color = statusColor;
            }
            activity.getWindow().setStatusBarColor(color);
            return null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
            if (statusColor != INVALID_VAL) {
                color = statusColor;
            }
            View statusBarView = contentView.getChildAt(0);
            int barHeight = getNavigationBarHeight(activity);
            if (statusBarView != null && statusBarView.getMeasuredHeight() == barHeight) {
                statusBarView.setBackgroundColor(color);
                return statusBarView;
            }
            statusBarView = new View(activity);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, barHeight);
            statusBarView.setBackgroundColor(color);
            contentView.addView(statusBarView, lp);
            return statusBarView;
        }
        return null;

    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int getNavigationBarHeight(Activity activity) {
        if (!isNavigationBarShow(activity)) {
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        return resources.getDimensionPixelSize(resourceId);
    }

    private boolean isNavigationBarShow(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            Log.e("tjt852", "y1=" + size.y + ",y2=" + realSize.y);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !(menu || back);
        }
    }

    /**
     * 横屏可通过 widthPixels - widthPixels2 > 0 来判断底部导航栏是否存在
     *
     * @param windowManager
     * @return true表示有虚拟导航栏 false没有虚拟导航栏
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public boolean isNavigationBarShow2(WindowManager windowManager) {
        Display defaultDisplay = windowManager.getDefaultDisplay();
        //获取屏幕高度
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getRealMetrics(outMetrics);
        int heightPixels = outMetrics.heightPixels;

        if (isStatusBarShown(this)) {
            heightPixels = heightPixels - getStatusBarHeight(this);
        }

        View root = getWindow().getDecorView().findViewById(android.R.id.content);
        //获取内容高度
        int heightPixels2 = root.getHeight();

        Log.e("tjt852", "heightPixels=" + heightPixels + ",heightPixels2=" + heightPixels2);

        return heightPixels - heightPixels2 > 0;
    }


    public static boolean isStatusBarShown(Activity context) {
        WindowManager.LayoutParams params = context.getWindow().getAttributes();
        int paramsFlag = params.flags & (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return paramsFlag == params.flags;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mediationHelper.setAdVideoInterface(null);
        AppInstallReceiver.removeInstallCallback(installCallback);
        if (webView != null) {
            webView.destroy();
            webView.setContext(null);
            webView = null;
        }

        if (mSelectPhotoDialog != null) {
            mSelectPhotoDialog.dismiss();
        }
//        try {
//            long closeTimestamp = System.currentTimeMillis();
//            long duration = 0;
//            if (impTimestamp > 0 && closeTimestamp > 0)
//                duration = closeTimestamp - impTimestamp;
//            sendTrackCloseLinear(duration);
//            if (requestHolder != null) {
//                RequestCache.remove(requestHolder.getRequestId());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


}
