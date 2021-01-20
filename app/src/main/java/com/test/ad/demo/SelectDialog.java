package com.test.ad.demo;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SelectDialog extends Dialog {

    private Context mContext;

    private View.OnClickListener mListener;

    public SelectDialog(Context context, View.OnClickListener listener) {
        super(context, R.style.popup_dialog_anim);
        mContext = context;
        this.mListener = listener;
        init(context);
    }


    private void init(Context context) {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        View view = View.inflate(context, R.layout.widget_dialog_select, null);
        setContentView(view);
        init(view);
    }

    private void init(View view) {
        TextView tvPhoto = view.findViewById(R.id.tv_photo);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        TextView tvCamera = view.findViewById(R.id.tv_camera);
        tvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onClick(v);
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onClick(v);
            }
        });
        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onClick(v);
            }
        });
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        Window window = getWindow();
        if (window == null) return;
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.alpha = 1.0f;
        layoutParams.width = screenWidth;
        window.setAttributes(layoutParams);
    }
}
