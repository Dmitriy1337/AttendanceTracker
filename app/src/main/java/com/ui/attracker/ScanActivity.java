package com.ui.attracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;
import android.view.TextureView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class ScanActivity extends AppCompatActivity {

    private static final String CAMERA_ID = "0";
    private TextureView mTextureView = null;
    private CameraManager mCameraManager = null;
    private CameraService mCamera = null;

    private Intent scannedIntent;

    private static BarcodeDetector barcodeDetector = null;
    volatile static boolean decoderBusy = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mTextureView = findViewById(R.id.textureView);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        }


        scannedIntent = new Intent(this, SuccessfullyScannedActivity.class);
        scannedIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);

        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                mCamera = new CameraService(mCameraManager, CAMERA_ID);

                mCamera.openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                //
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
                if (!decoderBusy) {
                    decoderBusy = true;
                    new BitmapDecoder(mTextureView.getBitmap());
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera != null && !mCamera.isOpen())
            mCamera.openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera.isOpen())
            mCamera.closeCamera();
    }

    static boolean checkBarcodeValidity(String value) {
        // TODO check validity
        return value.matches("^[a-zA-Z0-9_\\-]+/[a-z-A-Z0-9\\s]+$");
    }



    class BitmapDecoder implements Runnable {

        Bitmap bitmapToDecode;

        public BitmapDecoder(Bitmap bitmapToDecode) {
            this.bitmapToDecode = bitmapToDecode;
            new Thread(this).start();
        }

        @Override
        public void run() {
            if (barcodeDetector == null)
                barcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

            Frame frame = new Frame.Builder().setBitmap(bitmapToDecode).build();
            SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);

            String value = barcodes.size() > 0 ? barcodes.valueAt(0).rawValue : "";
            if (checkBarcodeValidity(value)) {
                scannedIntent.putExtra("BARCODE_VALUE", value);
                startActivity(scannedIntent);
                finish();
            }
            decoderBusy = false;
        }
    }




    public class CameraService
    {
        
        private static final String LOG_TAG = "CameraService";

        private String mCameraID;
        private CameraDevice mCameraDevice = null;
        private CameraCaptureSession mCaptureSession;


        public CameraService(CameraManager cameraManager, String cameraID) {
            mCameraManager = cameraManager;
            mCameraID = cameraID;
        }


        private void createCameraPreviewSession() {

            SurfaceTexture texture = mTextureView.getSurfaceTexture();

            texture.setDefaultBufferSize(1920,1080);
            Surface surface = new Surface(texture);

            try {
                final CaptureRequest.Builder builder =
                        mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                builder.addTarget(surface);

                mCameraDevice.createCaptureSession(Collections.singletonList(surface),
                        new CameraCaptureSession.StateCallback() {

                            @Override
                            public void onConfigured(CameraCaptureSession session) {
                                mCaptureSession = session;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            mCaptureSession.setRepeatingRequest(builder.build(),null,null);
                                        } catch (CameraAccessException e) {
                                            Log.e(LOG_TAG, "Failed to start camera preview because it couldn't access camera", e);
                                        } catch (IllegalStateException e) {
                                            Log.e(LOG_TAG, "Failed to start camera preview.", e);
                                        }
                                    }
                                }, 500);

                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession session) { }},
                        null );

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }


        private CameraDevice.StateCallback mCameraCallback = new CameraDevice.StateCallback() {

            @Override
            public void onOpened(CameraDevice camera) {
                mCameraDevice = camera;
                Log.i(LOG_TAG, "Open camera  with id:"+ mCameraDevice.getId());

                createCameraPreviewSession();
            }

            @Override
            public void onDisconnected(CameraDevice camera) {
                mCameraDevice.close();

                Log.i(LOG_TAG, "disconnect camera  with id:"+ mCameraDevice.getId());
                mCameraDevice = null;
            }

            @Override
            public void onError(CameraDevice camera, int error) {
                Log.i(LOG_TAG, "error! camera id:"+camera.getId()+" error:"+error);
            }
        };


        public void openCamera() {
            try {

                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    mCameraManager.openCamera(mCameraID, mCameraCallback, null);

            } catch (CameraAccessException e) {
                Log.i(LOG_TAG, e.getMessage());

            }
        }

        public void closeCamera() {
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }

        public boolean isOpen() {
            return mCameraDevice != null;
        }
    }
}