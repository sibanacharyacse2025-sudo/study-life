package com.stdili.services;

import android.content.Context;
import android.hardware.Camera;
import android.graphics.SurfaceTexture;
import android.view.TextureView;
import android.util.Log;

public class CameraMonitoringService implements TextureView.SurfaceTextureListener {
    private static final String TAG = "CameraMonitoringService";
    private Camera camera;
    private TextureView textureView;

    public interface OnFrameListener {
        void onFrameAvailable(byte[] frameData);
    }

    private OnFrameListener listener;

    public CameraMonitoringService(Context context) {
        Log.d(TAG, "CameraMonitoringService initialized");
    }

    public void initializeCamera(TextureView textureView) {
        this.textureView = textureView;
        this.textureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        try {
            camera = Camera.open(1); // Front camera
            camera.setPreviewTexture(surfaceTexture);
            camera.startPreview();
            Log.d(TAG, "Camera preview started");
        } catch (Exception e) {
            Log.e(TAG, "Error starting camera", e);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {}

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        return true;
    }

    public void setListener(OnFrameListener listener) {
        this.listener = listener;
    }

    public void stop() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
