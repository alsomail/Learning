package com.also.stepone_drawpicture;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.also.stepone_drawpicture.view.MySurfaceView;

/**
 * 描述：
 * 作者：ye.yuan
 * 邮箱：ye.yuan@lingware.cn
 * 创建时间：2019/2/27 10:28 PM
 */
public class SurfaceViewActivity extends AppCompatActivity {

    private MySurfaceView mMsvSurface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surfaceview);
    }

    private void init() {
        mMsvSurface = findViewById(R.id.msv_surface);
    }
}
