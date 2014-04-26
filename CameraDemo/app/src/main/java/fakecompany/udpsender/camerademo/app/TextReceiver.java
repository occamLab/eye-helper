package fakecompany.udpsender.camerademo.app;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by cypressf on 4/6/14.
 * TextReceiver initiates a TCP socket connection to our server and receives
 * text from the server. Upon receiving the text, we will simply log it for now.
 */
public class TextReceiver implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private MainActivity activity;
    public boolean connected;

    public TextReceiver(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        Log.d(MainActivity.TAG, "Started text receiver");
        try {
            String message;
            Log.d(MainActivity.TAG, "connecting to text sender");
            socket = new Socket(activity.serverAddress, 9999);
            Log.d(MainActivity.TAG, "connected to text sender");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            connected = true;
            while(connected) {
                Log.d(MainActivity.TAG, "reading line");
                message = in.readLine();
                Log.d(MainActivity.TAG, "read line");
                if (message != null) {
                    Log.d(MainActivity.TAG, message);
                    activity.speak(message);
                }
                else {
                    disconnect();
                }
            }
        } catch (IOException e) {
            disconnect();
            e.printStackTrace();
        }
        Log.d(MainActivity.TAG, "TextReceiver has ended");
    }

    public void disconnect() {
        Log.d(MainActivity.TAG, "trying to disconnect");
        connected = false;
        if (socket != null) {
            if (socket.isConnected()) {
                try {
                    in.close();
                    out.close();
                    socket.close();
                    Log.d(MainActivity.TAG, "stopped textreceiver");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
