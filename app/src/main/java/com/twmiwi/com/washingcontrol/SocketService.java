package com.twmiwi.com.washingcontrol;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by tobia on 08.03.2017.
 */

public class SocketService extends Service {

    private final IBinder myBinder = new LocalBinder();
    public static final String SERVERIP = "192.168.160.35"; //your computer IP address should be written here
    public static final int SERVERPORT = 2001;
    OutputStream out;
    Socket socket;
    public SocketAddress serverAddr;
    String command;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class LocalBinder extends Binder {
        public SocketService getService() {
            System.out.println("I am in Localbinder ");
            return SocketService.this;

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("I am in on create");
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        System.out.println("I am in on start");
        //  Toast.makeText(this,"Service created ...", Toast.LENGTH_LONG).show();
        Runnable connect = new connectSocket();
        new Thread(connect).start();
        return START_STICKY;
    }

    public void sendCommand(String command) {

        this.command = command;
        CommandWriter writer = new CommandWriter();
        writer.execute();


    }

    class connectSocket implements Runnable {

        @Override
        public void run() {


            try {
                //here you must put your computer's IP address.
                serverAddr = new InetSocketAddress(SERVERIP,SERVERPORT);
                Log.e("TCP Client", "C: Connecting...");
                //create a socket to make the connection with the server

                socket = new Socket();
                socket.connect(serverAddr,10000);

                try {


                    //send the message to the server
                    out = socket.getOutputStream();
                    String test = "test";
                    out.write(test.getBytes());

                    Log.e("TCP Client", "C: Sent.");

                    Log.e("TCP Client", "C: Done.");


                }
                catch (Exception e) {

                    Log.e("TCP", "S: Error", e);

                }
            } catch (Exception e) {

                Log.e("TCP", "C: Error", e);

            }

        }

    }

    class CommandWriter extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                out.write(command.getBytes());

            } catch (Exception e) {

                Log.e("TCP", "Fehler beim schreiben", e);

            }
            return "";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        socket = null;
    }


}
