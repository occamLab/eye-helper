package fakecompany.udpsender.camerademo.app;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class VideoStreamer implements Runnable {

    // initializing the things here so they're accessible by all the functions in this class :-)
    private DatagramSocket socket;
//    private MediaRecorder recorder;
    private String address_string = "192.168.51.54";
    private int foreign_port = 8888;
    private int local_port = 8888;
    private InetAddress foreign_address;
    private MainActivity activity;

    public VideoStreamer(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        Log.d(MainActivity.TAG, "starting thread");
        try {
            // Set up the socket things
            foreign_address  = InetAddress.getByName(address_string);
            socket = new DatagramSocket(local_port);
        } catch (Exception e) {
            e.printStackTrace();
            cleanShutdown();
        }
        sendTestPacket();
        cleanShutdown();
//        startRecording();
    }

    private void sendTestPacket() {
        //Seeing if the android to webapp communication worked with a "hello world" string. Woot!
        String message = "hello world";
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, foreign_address, foreign_port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cleanShutdown() {
        //A convenient function to tie up loose ends when testing/running the app.
        //Takes care of the recorder, camera, and sockets.

//        if (recorder != null) {
//            recorder.stop();
//            recorder.reset();   // clear recorder configuration
//            recorder.release(); // release the recorder object
//            recorder = null;
//        }
//        if (activity.camera != null) {
//            activity.camera.stopPreview();
//            activity.camera.release();
//        }
        if (socket != null) {
            socket.close();
        }
    }
}
