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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;
import android.view.TextureView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.Arrays;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class ScanActivity extends AppCompatActivity {

    private static final String CAMERA_ID = "0";
    private TextureView mTextureView = null;
    private CameraManager mCameraManager = null;
    private CameraService mCamera = null;

    private Intent scannedIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mTextureView = findViewById(R.id.textureView);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        }


        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        mCamera = new CameraService(mCameraManager, CAMERA_ID);

        mCamera.openCamera();


        scannedIntent = new Intent(this, SuccessfullyScannedActivity.class);
        scannedIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);

        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                //
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
                new BitmapDecoder().execute(mTextureView.getBitmap());
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!mCamera.isOpen())
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
        return value.length() > 0;
    }



    class BitmapDecoder extends AsyncTask<Bitmap, Void, String> {

        public BitmapDecoder() {
            super();
        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                    .setBarcodeFormats(Barcode.QR_CODE)
                    .build();
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);
            return barcodes.size() > 0 ? barcodes.valueAt(0).rawValue : "";
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            if (checkBarcodeValidity(value)) {
                scannedIntent.putExtra("BARCODE_VALUE", value);
                startActivity(scannedIntent);
                finish();
            }
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


        private void createCameraPreviewSession() {

            SurfaceTexture texture = mTextureView.getSurfaceTexture();

            texture.setDefaultBufferSize(1920,1080);
            Surface surface = new Surface(texture);

            try {
                final CaptureRequest.Builder builder =
                        mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                builder.addTarget(surface);

                mCameraDevice.createCaptureSession(Arrays.asList(surface),
                        new CameraCaptureSession.StateCallback() {

                            @Override
                            public void onConfigured(CameraCaptureSession session) {
                                mCaptureSession = session;
                                try {
                                    mCaptureSession.setRepeatingRequest(builder.build(),null,null);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession session) { }},
                        null );

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        public boolean isOpen() {
            return mCameraDevice != null;
        }

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
    }
}