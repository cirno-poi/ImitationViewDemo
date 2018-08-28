package com.example.dell.customviewdemo.customview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.example.dell.customviewdemo.R;

public class CustomLayout extends FrameLayout {

    Button animateBt;
    Button setNumberBt;
    JikeLikeView jikeLikeView;
    EditText numberEt;
    private boolean clickFlag = false;

    public CustomLayout(Context context) {
        super(context);
    }

    public CustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        jikeLikeView = findViewById(R.id.jikeLikeView);
        animateBt = findViewById(R.id.animate_bt);
        setNumberBt = findViewById(R.id.set_number_bt);
        numberEt = findViewById(R.id.set_number_et);

        setNumberBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!numberEt.getText().toString().isEmpty()) {
                    jikeLikeView.setContentInt(Integer.parseInt(numberEt.getText().toString()));
                }
            }
        });

        animateBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator;
                ObjectAnimator animatorAlpha;
                if (clickFlag) {
                    animator = ObjectAnimator.ofFloat(jikeLikeView, "progressY", 100, 0);
                    animatorAlpha = ObjectAnimator.ofInt(jikeLikeView, "alphaInt", 255, 0);
                } else {
                    animator = ObjectAnimator.ofFloat(jikeLikeView, "progressY", 0, 100);
                    animatorAlpha = ObjectAnimator.ofInt(jikeLikeView, "alphaInt", 0, 255);
                }

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setDuration(600);
                animatorSet.setInterpolator(new FastOutSlowInInterpolator());
                animatorSet.playTogether(animator, animatorAlpha);
                animatorSet.start();
                clickFlag = !clickFlag;

            }
        });
    }
}
