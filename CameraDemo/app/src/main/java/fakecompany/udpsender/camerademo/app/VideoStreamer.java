package fakecompany.udpsender.camerademo.app;

import android.util.Log;

/**
 * Created by cypressf on 3/30/14.
 */
public class VideoStreamer implements Runnable {

    private MainActivity activity;

    public VideoStreamer(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        Log.d(MainActivity.TAG, "starting thread (and stopping it. it's a short thread)");
    }
}
