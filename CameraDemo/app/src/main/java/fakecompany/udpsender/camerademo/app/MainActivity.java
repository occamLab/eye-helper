package fakecompany.udpsender.camerademo.app;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.FrameLayout;


public class MainActivity extends Activity {
    private Camera mCamera;
    private CameraPreview mPreview;
    public static final String TAG = "CameraDemo";
    public String serverAddress = "192.168.48.237";
    private TextReceiver mTextReceiver;
    private TextToSpeech speech;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout = (FrameLayout) findViewById(R.id.camera_preview);
        speech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                startTextReceiver();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera, serverAddress);
        frameLayout.addView(mPreview);
    }



    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mPreview.mHolder.removeCallback(mPreview);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (mPreview != null) {
            frameLayout.removeView(mPreview);
            mPreview = null;
        }
    }


    private void startTextReceiver() {
        mTextReceiver = new TextReceiver(this);
        Thread thread = new Thread(mTextReceiver);
        thread.start();
    }

    public void speak(String speakMe) {
        speech.speak(speakMe, TextToSpeech.QUEUE_FLUSH, null);
    }



    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            Log.e(TAG, Log.getStackTraceString(e));

        }
        return c; // returns null if camera is unavailable
    }
}
