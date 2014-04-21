package fakecompany.udpsender.camerademo.app;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VideoStreamer implements Runnable {
    private byte[] mData;
    private String mAddress;

    public VideoStreamer(byte[] data, String address) {
        mData = data;
        mAddress = address;
    }

    @Override
    public void run() {
        Log.d(MainActivity.TAG, "Sending Picture");
        try {
            URL url = new URL(mAddress);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            out.write(mData);
            out.flush();
            out.close();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            in.close();
            urlConnection.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
