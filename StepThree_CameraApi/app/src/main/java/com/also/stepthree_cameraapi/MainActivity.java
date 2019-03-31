package com.also.stepthree_cameraapi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.SessionConfiguration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity{

    private final static String TAG = "CameraAPI";

    private CameraManager mCameraManager;

    private boolean mFlashAvailable;

    private String mCameraId;

    private CameraDevice mCameraDevice;


    private CameraDevice.StateCallback mStateCallback=new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };
    private CameraSurface mCameraSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        try {
            init();

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mCameraSurfaceView = findViewById(R.id.cs_camera);
        mCameraSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {

                    createCameraPreviewSession(mCameraSurfaceView.getHolder().getSurface());
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    private void init() throws CameraAccessException {
        mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        if (mCameraManager == null) {
            return;
        }
        String[] cameraIdList = mCameraManager.getCameraIdList();
        for (String cameraId : cameraIdList) {
            Log.i(TAG, "cameraId=" + cameraId);
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics
                    (cameraId);

            //查看硬件支持的等级
            Integer integer = characteristics.get(CameraCharacteristics
                    .INFO_SUPPORTED_HARDWARE_LEVEL);
            //hardwareLevel==1为FULL，hardwareLevel==2为LEGACY，hardwareLevel==0为LIMIT，
            //hardwareLevel==3为LEVEL_3。
            //LEVEL_3 > FULL > LIMIT > LEGACY

            Log.i(TAG, "硬件支持等级：" + integer);

            Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
            if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                continue;
            }
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics
                    .SCALER_STREAM_CONFIGURATION_MAP);
            if (map == null) {
                continue;
            }

            Size[] outputSizes = map.getOutputSizes(SurfaceHolder.class);
            Log.i(TAG, "支持的预览尺寸:" + Arrays.toString(outputSizes));

            Log.i(TAG, "设置的预览尺寸：" + mCameraSurfaceView.getWidth() + " x "
                    + mCameraSurfaceView.getHeight());
            //比例的设置要在创建surface之前，不然无效，setFixed到surface的过程待查看
            mCameraSurfaceView.getHolder().setFixedSize(2160,
                    1080);
            Boolean aBoolean = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            mFlashAvailable = aBoolean == null ? false : aBoolean;

            mCameraId = cameraId;
            Log.i(TAG, "相机可用");
            openCamera();
            return;
        }
        mCameraId = "";
    }



    private void openCamera() {
        if (mCameraManager == null) {
            return;
        }

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mCameraManager.openCamera(mCameraId, mStateCallback, new Handler(getMainLooper()));
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreviewSession(Surface surface) throws CameraAccessException {
        if (mCameraDevice == null) {
            return;
        }

        final CaptureRequest.Builder captureRequest = mCameraDevice.createCaptureRequest(CameraDevice
                .TEMPLATE_PREVIEW);
        captureRequest.addTarget(surface);
        
        mCameraDevice.createCaptureSession(Arrays.asList(surface),
                new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        if (mCameraDevice == null) {
                            return;
                        }

                        captureRequest.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                        try {
                            session.setRepeatingRequest(captureRequest.build(), null, new Handler(getMainLooper()));
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG," 开启相机预览并添加事件");
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                    }
                }, null);
    }
}
