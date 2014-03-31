package fakecompany.udpsender.camerademo.app;

import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by cypressf on 3/30/14.
 */
public class VideoStreamer implements Runnable {

    // initializing the things here so they're accessible by all the functions in this class :-)
    private DatagramSocket socket;
//    private MediaRecorder recorder;
    private String address_string = "10.7.88.20";
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
        createSocket();
        sendTestPacket();
        startRecording();
    }

    private void createSocket() {
        try {
            // Set up the socket things
            foreign_address  = InetAddress.getByName(address_string);
            socket = new DatagramSocket(local_port);
        } catch (Exception e) {
            e.printStackTrace();
            cleanShutdown();
        }
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

    private void startRecording(){
        // Get the socket file descriptor
        ParcelFileDescriptor pfd = ParcelFileDescriptor.fromDatagramSocket(socket);
        if (pfd == null) {
            cleanShutdown();
            Log.e("UDP","couldn't get file pfd");
            return;
        }
        FileDescriptor fd = pfd.getFileDescriptor();
        activity.startRecording(fd);
    }

    public void cleanShutdown() {
        if (socket != null) {
            socket.close();
        }
    }
}
