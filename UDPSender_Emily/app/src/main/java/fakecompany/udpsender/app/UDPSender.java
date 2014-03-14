package fakecompany.udpsender.app;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by greenteawarrior on 3/10/14.
 */

public class UDPSender implements Runnable{
    private DatagramSocket socket;
    private MediaRecorder recorder;
    private String address_string = "192.168.51.219"; //use ip addr command in linux
    private int foreign_port = 8888;
    private int local_port = 8888;
    private InetAddress foreign_address;

    @Override
    public void run() {
        try {
            foreign_address  = InetAddress.getByName(address_string);
            socket = new DatagramSocket(local_port);
            sendTestPacket();
            cleanShutdown();
        } catch (Exception e) {
            e.printStackTrace();
            cleanShutdown();
        }
    }


    private void sendTestPacket() throws IOException {
        String message = "hello world";
        byte[] data = message.getBytes();
        Log.d("UDP", "trying to hello world");
        DatagramPacket packet = new DatagramPacket(data, data.length, foreign_address, foreign_port);
        socket.send(packet);

    }

    private void cleanShutdown() {
        if (socket != null) {
            socket.close();
            Log.d("UDP", "socket closed");
        }
    }

}
