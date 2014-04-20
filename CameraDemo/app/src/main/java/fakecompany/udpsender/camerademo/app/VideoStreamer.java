package fakecompany.udpsender.camerademo.app;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.Buffer;

public class VideoStreamer implements Runnable {

    // initializing the things here so they're accessible by all the functions in this class :-)
    private DatagramSocket socket;
//    private MediaRecorder recorder;
    private String address_string = "10.7.88.80";
    private int foreign_port = 8888;
    private int local_port = 8888;
    private InetAddress foreign_address;
    private MainActivity activity;
    private BufferedWriter mWriter;
    private Socket mSocket;
    private boolean canSend = false;

    public VideoStreamer(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        Log.d(MainActivity.TAG, "starting thread");
        createSocket();
        sendTestPacket();
    }

    private void createSocket() {
        try {
            // Set up the socket things
            foreign_address  = InetAddress.getByName(address_string);
            mSocket = new Socket(foreign_address, foreign_port);
            mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
            canSend = true;
        } catch (Exception e) {
            e.printStackTrace();
            cleanShutdown();
        }
    }
    private void sendTestPacket() {
        //Seeing if the android to webapp communication worked with a "hello world" string. Woot!
        String message = "hello world";
        byte[] data = message.getBytes();
        try {
            mWriter.write(message);
            mWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFrame(byte[] data, int width, int height){
        if (canSend) {
            Log.d(MainActivity.TAG, "Sending Frame");
            try {
                //Log.d(TAG, "Sending frame via mWriter.");
                mWriter.write("<<<FRAME>>>");
                mWriter.flush();

                YuvImage img = new YuvImage(data, ImageFormat.NV21, width, height, null);
                img.compressToJpeg(new Rect(0,0,width,height), 20, mSocket.getOutputStream());
                mSocket.getOutputStream().flush();
                mWriter.write("<<<END>>>");
                mWriter.flush();
            } catch (IOException e) {
                Log.d(activity.TAG, "Could not send via mWriter.");
            }
        }
    }

    public void cleanShutdown() {
        if (socket != null) {
            socket.close();
        }
        canSend = false;
    }
}
