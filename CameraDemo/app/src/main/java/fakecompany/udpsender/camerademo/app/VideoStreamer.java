package fakecompany.udpsender.camerademo.app;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VideoStreamer implements Runnable {
    private YuvImage mImg;
    private String mAddress;
    private int mWidth;
    private int mHeight;

    public VideoStreamer(YuvImage img, String address, int width, int height) {
        mImg = img;
        mAddress = address;
        mWidth = width;
        mHeight = height;
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
            mImg.compressToJpeg(new Rect(0,0,mWidth,mHeight), 20, out);
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
