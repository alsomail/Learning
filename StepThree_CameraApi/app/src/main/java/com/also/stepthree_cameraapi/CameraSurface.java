package com.also.stepthree_cameraapi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 描述：
 * 作者：ye.yuan
 * 邮箱：ye.yuan@lingware.cn
 * 创建时间：2019/3/24 2:30 PM
 */
public class CameraSurface extends SurfaceView {
    private Context mContext;

    public CameraSurface(Context context) {
        super(context);
        init(context);
    }

    public CameraSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CameraSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CameraSurface(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }


}
