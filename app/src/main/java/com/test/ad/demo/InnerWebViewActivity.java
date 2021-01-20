package com.test.ad.demo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.adsgreat.base.Assets;
import com.adsgreat.base.config.Const;
import com.adsgreat.base.utils.ContextHolder;
import com.adsgreat.base.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.adsgreat.base.utils.Utils.dp2px;

public class InnerWebViewActivity extends Activity {

    private static final int PROGRESSBAR = Utils.generateViewId();
    private static final int WEB_VIEW = Utils.generateViewId();
    public static final String KEY_URL = "link";
    public static final String KEY_CLOSE_LINEAR = "closeLinear";

    private ProgressBar progressBar;
//    private WebView webView;

    private View btnClose;

    private int currentIndex = -1;

    private boolean isBack = false;

    private boolean is302 = false;

    private long impTimestamp = 0;

    private AdVideoMediation mediationHelper = null;

    @SuppressLint("StaticFieldLeak")
    private static CacheWebView webView = null;

    public static void launch(Context context, CacheWebView webView) {
        Intent intent = new Intent(context, InnerWebViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        InnerWebViewActivity.webView = webView;
//        Bundle bundle = new Bundle();
//        bundle.putString(KEY_URL, loadUrl);
//        intent.putExtras(bundle);
        ContextHolder.getGlobalAppContext().startActivity(intent);
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        View view = generateLayout(this);
        setContentView(view);

//        String link = getIntent().getStringExtra(KEY_URL);  //跳转链接

        progressBar = findViewById(PROGRESSBAR);
        webView = findViewById(WEB_VIEW);

//        // 得到浏览器的设置对象
//        WebSettings ws = webView.getSettings();
//        // 设置浏览器是否缓存数据.true表示缓存,false表示不缓存
//        ws.setJavaScriptEnabled(true);
//        ws.setDomStorageEnabled(true);
//        ws.setDatabaseEnabled(true);
//        ws.setAppCacheMaxSize(1024 * 1024 * 8);//设置缓冲大小，设的是8M
//        String appCacheDir = getApplicationContext().getDir("cache", MODE_PRIVATE).getPath();
//        ws.setAppCachePath(appCacheDir);
//        ws.setAllowFileAccess(true);
//        ws.setAppCacheEnabled(true);
//        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
//        //允许加载http与https混合内容
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            ws.setMediaPlaybackRequiresUserGesture(false);
//        }
//        // api 11以上有个漏洞，要remove
//        webView.removeJavascriptInterface("searchBoxJavaBredge_");
//        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

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

                if (url.startsWith("http:") || url.startsWith("https:")) {
                    WebView.HitTestResult hit = webView.getHitTestResult();
                    int hitType = hit.getType();
                    if (hitType == WebView.HitTestResult.SRC_ANCHOR_TYPE) {//点击超链接
                    }
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
        webView.reload();
    }

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
                progressBar.setVisibility(View.VISIBLE);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        // For Android >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
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
                switch (v.getId()) {
                    case R.id.tv_camera:
                        dispatchTakePictureIntent();

                        break;
                    case R.id.tv_photo:
                        takePhoto();
                        break;
                    //不管选择还是不选择，必须有返回结果，否则就会调用一次
                    case R.id.tv_cancel:
                        if (uploadMessageAboveL == null) return;
                        uploadMessageAboveL.onReceiveValue(null);
                        uploadMessageAboveL = null;
                        break;
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
                RelativeLayout.LayoutParams.MATCH_PARENT, dp2px(3));
        relativeLayout.addView(progressBar, layoutParams);

        //webview
//        WebView webView = new WebView(context);
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
                dp2px(28), dp2px(28));
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams2.topMargin = dp2px(5);
        layoutParams2.rightMargin = dp2px(5);
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
                Uri photoURI = FileProvider.getUriForFile(this,
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mediationHelper.setAdVideoInterface(null);
        webView.setContext(null);
        webView = null;
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
