package com.twmiwi.com.washingcontrol;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import android.app.Fragment;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

class ServerConnection extends AsyncTask<String, String, String> {

    private MainControl activity;
    private Boolean errorOccured = false;
    private String errorString;
    private String ip;
    private int port;
    private byte[] received = new byte[3];
    private Socket socket;
    private InputStream input;
    private OutputStream output = null;



    ServerConnection(MainControl a, String ip, int port) {
        this.activity = a;
        this.ip = ip;
        this.port = port;
        errorString = "";
        socket = new Socket();
        input = null;
    }
    /*
        Stellt im Hintergrund die Datenbankverbidung her, sendet und liest die Daten
     */
    protected String doInBackground(String... params) {

        if (!errorOccured) {
            try {
                SocketAddress host = new InetSocketAddress(ip, port);
                socket.connect(host, 10000);
                Log.d("socket init", ip + port);
            } catch (IOException e) {
                errorOccured = true;
                errorString = activity.getString(R.string.errorHostNotReachable);
                Log.d("error", "server nicht erreichbar");
            }
            if (socket.isConnected()) {
                try {
                    input = socket.getInputStream();
                    output = socket.getOutputStream();
                    Thread.sleep(2000);
                    socket.setSoTimeout(10000);
                    //TODO Test output ändern
                    output.write(128);
                } catch (IOException | InterruptedException ex) {
                    errorOccured = true;
                    errorString = activity.getString(R.string.errorOnDataReading);
                }
                try {

                    Byte lineReader;
                    int zaehler = 0;
                    // 9 Steuerzeichen kommen vor dem eigentlichen Versand, diese werden verworfen!
                    while (zaehler < 12) {
                        lineReader = (byte) input.read();
                        if (zaehler > 8) {
                            received[zaehler-9] = lineReader;
                            Log.d("schleife", getByteBinaryString(lineReader));
                        }
                        zaehler++;
                    }

                } catch (SocketTimeoutException e) {
                    errorOccured = true;
                    errorString = "SocketTimeout";
                    Log.d("error", "Socket timeout");
                } catch (IOException e) {
                    errorOccured = true;
                    errorString = "IOException";
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //TODO Test Output ändern
                try {
                    output.write(received[0]);
                    output.write(received[1]);
                    output.write(received[2]);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (socket.isConnected()) {
                    try {
                        socket.shutdownInput();
                        socket.shutdownOutput();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return errorString;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        // You can track you progress update here

    }

    @Override
    protected void onPreExecute() {
        // Here you can show progress bar or something on the similar lines.
        // Since you are in a UI thread here.
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String values) {

        super.onPostExecute(values);

        if (errorOccured) {
            TextView textFeld = (TextView) activity.findViewById(R.id.printView);
            textFeld.setText(errorString);
        } else {
            TextView textFeld = (TextView) activity.findViewById(R.id.printView);
            textFeld.setText("funktion ausgeführt");
        }
        if (socket.isConnected()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        activity.setUpdateOnProgress(false);

        // After completing execution of given task, control will return here.
        // Hence if you want to populate UI elements with fetched data, do it here.
    }

    private static String getByteBinaryString(byte b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 7; i >= 0; --i) {
            sb.append(b >>> i & 1);
        }
        return sb.toString();
    }

    public static byte getStringtoByte(String s) {
        byte b = 0, pot = 1;
        for (int i = 7; i >= 0; i--) {
            // -48: the character '0' is No. 48 in ASCII table,
            // so substracting 48 from it will result in the int value 0!
            b += (s.charAt(i)-48) * pot;
            pot <<= 1;    // equals pot *= 2 (to create the multiples of 2 (1,2,3,8,16,32)
        }

        return b;
    }

}