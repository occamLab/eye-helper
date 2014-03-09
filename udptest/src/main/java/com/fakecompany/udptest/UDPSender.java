package com.fakecompany.udptest;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/* Code for device communication between an android smartphone (hopefully Google Glass too!)
 * and a webapp (for now, we're using node.js). *
 * Currently can send strings of data, now we're trying to figure out how to stream video. whee!
 *
 * Created by cypressf and greenteawarrior on 3/7/14. Yay for collaborative code!
 *
 * resources:
 * http://developer.android.com/reference/java/net/DatagramSocket.html
 * http://developer.android.com/guide/topics/media/camera.html
 */

public class UDPSender implements Runnable {

    // initializing the things here so they're accessible by all the functions in this class :-)
    private DatagramSocket socket;
    private MediaRecorder recorder;
    private String address_string = "192.168.51.219";
    private int foreign_port = 8888;
    private int local_port = 8888;
    private InetAddress foreign_address;
    private MainActivity activity;

    public UDPSender(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        try {
            // Set up the socket things
            foreign_address  = InetAddress.getByName(address_string);
            socket = new DatagramSocket(local_port);
        } catch (Exception e) {
            e.printStackTrace();
            cleanShutdown();
        }
        sendTestPacket();
        startRecording();
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

    private void startRecording() {
        // Strategy: Record video and instead of saving it to a filepath, save it to the existing socket

        // Getting the socket "filepath"
        ParcelFileDescriptor pfd = ParcelFileDescriptor.fromDatagramSocket(socket);
        if (pfd == null) {
            cleanShutdown();
            Log.e("UDP","couldn't get file pfd");
            return;
        }

        FileDescriptor fd = pfd.getFileDescriptor();
        // Video record/stream time!
        recorder = new MediaRecorder();
        recorder.setCamera(activity.camera);
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        recorder.setOutputFile(fd);
        recorder.setPreviewDisplay(activity.preview.mHolder.getSurface());

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("UDP", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            cleanShutdown();
        } catch (IOException e) {
            Log.d("UDP", "IOException preparing MediaRecorder: " + e.getMessage());
            cleanShutdown();
        }

        recorder.start();   // Recording is now started

    }

    private void cleanShutdown() {
        //A convenient function to tie up loose ends when testing/running the app.
        //Takes care of the recorder, camera, and sockets.

        if (recorder != null) {
            recorder.stop();
            recorder.reset();   // clear recorder configuration
            recorder.release(); // release the recorder object
            recorder = null;
        }
        if (activity.camera != null) {
            activity.camera.stopPreview();
            activity.camera.release();
        }
        if (socket != null) {
            socket.close();
        }
    }

}
