package com.also.stepone_drawpicture;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

public class ImageViewActivity extends AppCompatActivity {

    private ImageView mIvImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        init();

    }

    private void init() {
        mIvImage = findViewById(R.id.iv_image);
        mIvImage.setImageBitmap(getBitmap());
    }

    /**
     * Drawable 是一个抽象的概念, 凡是可以被画出来的东西都称之为Drawable,而 Bitmap 是其存在的实体之一。
     * 可以简单地理解为 Bitmap 储存的是 像素信息，Drawable 储存的是 对 Canvas 的一系列操作。
     * 而 BitmapDrawable 储存的是「把 Bitmap 渲染到 Canvas 上」这个操作。
     *
     * Drawable在内存占用和绘制速度这两个非常关键的点上胜过Bitmap
     *
     * Drawable 的资源占用和加载速度最优，BitmapFactory.decodeStream 优于 BitmapFactory.decodeResource
     * @return
     */
    private Bitmap getBitmap() {
        AssetManager assets = getAssets();

        InputStream open = null;
        Bitmap bm = null;
        try {
            open = assets.open("demo.png");
            if (open != null) {
                bm= BitmapFactory.decodeStream(open);
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
        }

        return bm;
    }

}
