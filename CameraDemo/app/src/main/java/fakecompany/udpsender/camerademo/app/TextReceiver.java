package fakecompany.udpsender.camerademo.app;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    public boolean connected;

    @Override
    public void run() {
        try {
            String message;
            socket = new Socket("10.7.88.117", 9999);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            connected = true;
            while(connected) {
                message = in.readLine();
                if (message != null) {
                    Log.d(MainActivity.TAG, message);
                }
                else {
                    disconnect();
                }
            }
        } catch (IOException e) {
            disconnect();
            e.printStackTrace();
        }

    }
    public void send(String message) {
        if (message != null) {
            out.write(message);
            out.flush();
        }
    }

    private void disconnect() {
        connected = false;
        if (socket != null) {
            if (socket.isConnected()) {
                try {
                    in.close();
                    out.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
