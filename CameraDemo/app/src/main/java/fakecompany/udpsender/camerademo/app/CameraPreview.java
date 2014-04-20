package fakecompany.udpsender.camerademo.app;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int mFrameCount;
    private Camera.Size mPreviewSize;
    private VideoStreamer mVideoStreamer;

    public CameraPreview(Context context, Camera camera, VideoStreamer videoStreamer) {
        super(context);
        mVideoStreamer = videoStreamer;
        mCamera = camera;
        Camera.Parameters param = mCamera.getParameters();
        param.setPreviewSize(320, 240);
        //List<Integer> lislis = param.getSupportedPreviewFormats();
        //param.setPreviewFormat(ImageFormat.JPEG);
        mCamera.setParameters(param);
        //int form = param.getPreviewFormat();
        mPreviewSize = mCamera.getParameters().getPreviewSize();

        // Install a SurfaceHolder. Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            // TODO: Sometimes the app crashes here. Fix.
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("CameraDemo", "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
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
                        Log.d(MainActivity.TAG, "another frame will be sent");
                        mVideoStreamer.sendFrame(data, mPreviewSize.width, mPreviewSize.height);
                    }
                }
            });
            mCamera.startPreview();

        } catch (Exception e){
            Log.d("CameraDemo", "Error starting camera preview: " + e.getMessage());
        }
    }
}