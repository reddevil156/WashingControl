package com.twmiwi.com.washingcontrol;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by tw on 08.03.2017.
 */

public class SocketService extends Service {

    private final IBinder myBinder = new LocalBinder();
    private String serverip = "192.168.160.35"; //your computer IP address should be written here
    private int serverport = 2001;
//    final String defaultValueHost = "192.168.160.35";
//    final String defaultValuePort = "2001";
//    private String serverip; //your computer IP address should be written here
//    private int serverport;



    private OutputStream out;
    private Socket socket = new Socket();
    private SocketAddress serverAddr;
    private String command;
    private Boolean writingSuccessful = false;
    private SocketService service = this;



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
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        System.out.println("I am in on start");

//        String ipAddress = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("host", "");
//        int port = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("port", ""));



        if (!socket.isConnected()) {
            ConnectSocket connect = new ConnectSocket();
            connect.execute();
        }
        return START_STICKY;
    }

    public void sendCommand(String command) {

        if (socket.isConnected()) {
            CommandWriter writer = new CommandWriter(out, command, new AsyncResponse() {
                @Override
                public void processFinish(Object output) {
                    writingSuccessful = (Boolean) output;
                    if (writingSuccessful) {
                        Toast.makeText(service, "Befehl gesendet", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(service, "Befehl konnte nicht gesendete werden", Toast.LENGTH_LONG).show();
                    }
                }
            });
            writer.execute();
        } else {
            Toast.makeText(service, "Keine Verbindung zu Server", Toast.LENGTH_LONG).show();
        }



    }

    class ConnectSocket extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                //here you must put your computer's IP address.
                serverAddr = new InetSocketAddress(serverip, serverport);
                Log.e("TCP Client", "C: Connecting...");
                //create a socket to make the connection with the server


                socket.connect(serverAddr, 10000);

                try {
                    //send the message to the server
                    out = socket.getOutputStream();
                    String test = "test";
                    out.write(test.getBytes());

                    Log.e("TCP Client", "C: Sent.");

                    Log.e("TCP Client", "C: Done.");


                } catch (Exception e) {

                    Log.e("TCP", "S: Error", e);

                }
            } catch (Exception e) {
//                Toast.makeText(service, "Verbindung kann nicht hergestellt werden", Toast.LENGTH_LONG).show();
                Log.e("TCP", "C: Error", e);

            }
            return "";
        }

        protected void onPostExecute(String values) {

           service.isConnectionEstablished();

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
            Log.e("On Destroy", "destroy");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        socket = null;
    }

    public void IsBoundable() {
        Toast.makeText(this, "I bind like butter", Toast.LENGTH_LONG).show();
    }

    public void isConnectionEstablished() {
        if (socket.isConnected()) {
                Toast.makeText(this, "Verbindung hergestellt", Toast.LENGTH_LONG).show();
        } else {
                Toast.makeText(this, "Verbindung konnte nicht hergestellt werden", Toast.LENGTH_LONG).show();
        }

    }

    public void setIpPort(String ipAdress, int port) {
        this.serverip = ipAdress;
        this.serverport = port;
    }


}
