package com.fakecompany.udptest;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by cypressf on 3/7/14.
 */
public class UDPSender implements Runnable {

    private Camera camera;
    private DatagramSocket socket;
    private MediaRecorder recorder;
    private String address_string = "10.7.88.32";
    private int foreign_port = 8888;
    private int local_port = 8888;
    private InetAddress foreign_address;

    @Override
    public void run() {
        try {
            foreign_address  = InetAddress.getByName(address_string);
            socket = new DatagramSocket(local_port);
            sendTestPacket();
            startRecording();
        } catch (Exception e) {
            e.printStackTrace();
            cleanShutdown();
        }
    }

    public static Camera getCameraInstance(){
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

    private void sendTestPacket() throws IOException {
        String message = "hello world";
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, foreign_address, foreign_port);
        socket.send(packet);
    }

    private void startRecording() throws IOException {
        ParcelFileDescriptor pfd = ParcelFileDescriptor.fromDatagramSocket(socket);
        if (pfd == null) {
            cleanShutdown();
            Log.e("UDP","couldn't get file pfd");
            return;
        }

        FileDescriptor fd = pfd.getFileDescriptor();

        camera = getCameraInstance();
        recorder = new MediaRecorder();
        recorder.setCamera(camera);
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        recorder.setOutputFile(fd);
        recorder.prepare();
        recorder.start();   // Recording is now started

    }

    private void cleanShutdown() {
        if (recorder != null) {
            recorder.stop();
            recorder.reset();   // You can reuse the object by going back to setAudioSource() step
            recorder.release(); // Now the object cannot be reused
        }
        if (camera != null) {
            camera.release();
        }
        if (socket != null) {
            socket.close();
        }
    }

}
