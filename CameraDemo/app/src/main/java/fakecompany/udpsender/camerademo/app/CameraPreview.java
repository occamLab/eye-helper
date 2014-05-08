package fakecompany.udpsender.camerademo.app;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    public SurfaceHolder mHolder;
    private Camera mCamera;
    private int mFrameCount;
    private int mPreviewWidth, mPreviewHeight;
    private String mVideoAddress;
    private String serverAddress;


    public CameraPreview(Context context, Camera camera, String serverAddress, String videoAddress) {
        super(context);
        mCamera = camera;
        this.serverAddress = serverAddress;
        mVideoAddress = videoAddress;
        Camera.Parameters param = mCamera.getParameters();
        param.setPreviewSize(320, 240);
        mCamera.setParameters(param);
        Camera.Size size = mCamera.getParameters().getPreviewSize();
        mPreviewHeight = size.height;
        mPreviewWidth = size.width;
        // Install a SurfaceHolder. Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("CameraDemo", "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mHolder.removeCallback(this);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    // Log.d(TAG, "Preview Callback in CameraPreview.java. mFrameCount = " + mFrameCount);
                    if (mFrameCount++ >= 3) {
                        mFrameCount = 0;
                        YuvImage img = new YuvImage(data, ImageFormat.NV21, mPreviewWidth, mPreviewHeight, null);
                        VideoStreamer videoStreamer = new VideoStreamer(img, mVideoAddress, mPreviewWidth, mPreviewHeight);
                        Thread imageSenderThread = new Thread(videoStreamer);
                        imageSenderThread.start();
                    }
                }
            });
            mCamera.startPreview();

        } catch (Exception e){
            Log.d("CameraDemo", "Error starting camera preview: " + e.getMessage());
        }
    }
}