/**
 * FileName: FlySurfaceView
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2022/7/18 15:24
 * Description:
 */
package com.flyzebra.camera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

public class AutoTextureView extends TextureView {
    public AutoTextureView(Context context) {
        super(context);
    }

    public AutoTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AutoTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, (int) (width * 16f / 9f));
    }

}
