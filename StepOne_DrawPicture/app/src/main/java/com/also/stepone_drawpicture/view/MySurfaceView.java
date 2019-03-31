package com.also.stepone_drawpicture.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.io.InputStream;

/**
 * 描述：
 * 作者：ye.yuan
 * 邮箱：ye.yuan@lingware.cn
 * 创建时间：2019/2/27 10:29 PM
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder mHolder;
    private SurfaceViewThread mThread;
    private Context mContext;
    public MySurfaceView(Context context) {
        super(context);
        init(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    /**
     * 对Surface对象的操作是通过SurfaceHolder来进行的。
     * 所以，在你的SurfaceView类初始化的时候，你需要调用getHolder() 来获得SurfaceHolder对象，
     * 然后用addCallback()加上回调接口
     */
    private void init(Context context) {
        mContext = context;
        mHolder=getHolder();
        mHolder.addCallback(this);
        mThread = new SurfaceViewThread();
    }
    /****
     * :SurfaceView这个类实现了SurfaceHolder.Callback接口
     * 接口中主要有三个回调函数SurfaceChanged,Surfacecreated,Surfaceondestoryed
     * 分别对应Surface更改，创建，销毁
     */
    /****SurfaceHolder.Callback  start *****/
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mThread != null) {
            mThread.exit();
            mThread = null;
        }
    }

    /****SurfaceHolder.Callback  end *****/

    class SurfaceViewThread extends Thread{
        public void exit(){
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            Canvas canvas = mHolder.lockCanvas();
            AssetManager assets = mContext.getResources().getAssets();
            InputStream open = null;
            try {
                open = assets.open("demo.png");
                if (open != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(open);
                    canvas.drawBitmap(bitmap,0,0,null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (open != null) {
                    try {
                        open.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
