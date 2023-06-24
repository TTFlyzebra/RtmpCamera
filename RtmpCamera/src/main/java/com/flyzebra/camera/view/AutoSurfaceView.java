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
import android.view.SurfaceView;

public class AutoSurfaceView extends SurfaceView {
    public AutoSurfaceView(Context context) {
        super(context);
    }

    public AutoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AutoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, (int) (width * 16f / 9f));
    }

}
