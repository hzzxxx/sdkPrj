package com.dating.sdklib.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;

import androidx.annotation.NonNull;

import com.dating.sdklib.R;


public class LoadingDialog extends BaseDialog {

    private View animView;
    private AnimationDrawable anim;

    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_loading;
    }

    @Override
    protected void onViewCreated(View view) {
        animView = view.findViewById(R.id.anim_view);
        anim = (AnimationDrawable) animView.getBackground();
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void show() {
        super.show();
        if (anim != null && !anim.isRunning()) {
            anim.start();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (anim != null && !anim.isRunning()) {
            anim.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        System.out.println("onDismiss"+getContext());
        if (anim != null) {
            anim.stop();
            anim = null;
        }
    }
}
