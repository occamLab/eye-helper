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
    private TextReceiver textReceiver;
    private TextToSpeech speech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                startTextReceiver();
            }
        });
        setupCamera();

    }

    private void startTextReceiver() {
        textReceiver = new TextReceiver(this);
        Thread thread = new Thread(textReceiver);
        thread.start();
    }

    public void speak(String speakme) {
        speech.speak(speakme, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void setupCamera(){
        // Create an instance of Camera
        mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera, serverAddress);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
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

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

}
