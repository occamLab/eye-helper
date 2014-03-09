package com.fakecompany.udptest;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends ActionBarActivity {
    public Camera camera;
    public CameraPreview preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Describes what's happening when the app starts up

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camera = getCameraInstance();
        preview = new CameraPreview(this, camera);
        ((FrameLayout) findViewById(R.id.camera_preview)).addView(preview);
        final Thread udpSender = new Thread(new UDPSender(this));

        findViewById(R.id.button_capture).setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // We want to start a new thread - in which the socket stuff is always happening
                // (should not interfere with the UI thread)
                udpSender.start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // default thing for now

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // default thing for now

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Camera getCameraInstance(){
        // Get ready for the camera
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            Log.d("UDP", "camera failed to open");
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

}
