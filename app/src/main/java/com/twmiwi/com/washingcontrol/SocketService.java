package com.twmiwi.com.washingcontrol;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * Created by tw on 08.03.2017.
 */

public class SocketService extends Service {

    private final IBinder myBinder = new LocalBinder();
    String serverIP;
    int serverPort;

    LocalBroadcastManager broadcaster;
    static final public String SOCKET_RESULT = "com.twmiwi.com.wachingcontrol.READ_COMPLETED";
    static final public String SOCKET_READ_SUCCESSFUL = "com.twmiwi.com.wachingcontrol.SOCKET_READ_SUCCESSFUL";

    private OutputStream out;
    private InputStream input;
    private Socket socket = new Socket();

    private String command;
    private SocketService service = this;
    byte[] received;
    private int switchCode;
    private String ledCode;
    private int miniSwitchCode;
    private String buttonRow;


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
        broadcaster = LocalBroadcastManager.getInstance(this);
        System.out.println("I am in on create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        System.out.println("I am in on start");


        serverIP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("host", "");
        serverPort = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("port", 1234);

        System.out.println("serverip " + serverIP + " serverport" + serverPort);

        if (socket.isConnected()) {
            try {
                socket.close();
                System.out.println("socket geschlossen");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ConnectSocket connect = new ConnectSocket();
        connect.execute();

        return START_STICKY;
    }

    public void sendCommand(int status) {

        //TODO
        //true if we want to read, false if we want to write

        if (socket.isConnected()) {


            if (status == 0) {

                //TODO
                int command = 120;
                InputReader reader = new InputReader(input, 4, new AsyncResponse() {
                    @Override
                    public void processFinish(Object output) {

                        //4 bytes returned
                        received = (byte[]) output;
                        // first byte is used for the program switch, returns an int for the actual setting
                        switchCode = (checkSwitchCode(received[0]));
                        // second byte must be split; bits 3-8 are for the 6 leds
                        ledCode = getByteBinaryString(received[1]).substring(2);
                        // third bit is split; first 4 bits for the mini switch, returns an int for the actual setting
                        miniSwitchCode = (checkMiniSwitchCode(received[2]));
                        // last 4 bits ot the third byte are used for the 4 buttons.
                        buttonRow = getByteBinaryString(received[2]).substring(4);

                        sendResult("readOK");
                        System.out.println(getByteBinaryString(received[0]));
                        System.out.println(getByteBinaryString(received[1]));
                        System.out.println(getByteBinaryString(received[2]));
                        System.out.println(getByteBinaryString(received[3]));

                    }
                });

                CommandWriter writer = new CommandWriter(out, command, new AsyncResponse() {
                    @Override
                    public void processFinish(Object output) {
                        if ((Boolean) output) {
                            Toast.makeText(service, "Befehl gesendet", Toast.LENGTH_LONG).show();
                        } else {
                            reconnectSocket();
                            Toast.makeText(service, "Befehl konnte nicht gesendet werden", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                writer.execute();
                reader.execute();
            } else if (status == 1) {

                //TODO
                int command = 250;
                InputReader reader = new InputReader(input, 1, new AsyncResponse() {
                    @Override
                    public void processFinish(Object output) {

                        received = (byte[]) output;
                        sendResult("writeOK");
                        System.out.println(getByteBinaryString(received[0]));

                    }
                });

                CommandWriter writer = new CommandWriter(out, command, new AsyncResponse() {
                    @Override
                    public void processFinish(Object output) {
                        if ((Boolean) output) {
                            Toast.makeText(service, "Befehl gesendet", Toast.LENGTH_LONG).show();
                        } else {
                            reconnectSocket();
                            Toast.makeText(service, "Befehl konnte nicht gesendet werden", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                writer.execute();
                reader.execute();




            }
        } else {
            Toast.makeText(service, "Keine Verbindung zu Server", Toast.LENGTH_LONG).show();
        }


    }

    class ConnectSocket extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try {

                Log.e("TCP Client", "C: Connecting...");
                //create a socket to make the connection with the server
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverIP, serverPort), 10000);

                try {
                    //send the message to the server
                    out = socket.getOutputStream();
                    input = socket.getInputStream();

                    input.skip(9);
//                    String test = "test";
//                    out.write(test.getBytes());
                    out.write((byte) 159);

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
            try {
                SocketService.this.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

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

    public void reconnectSocket() {
        try {
            socket.close();
            out.close();
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        ConnectSocket connect = new ConnectSocket();
        connect.execute();
    }

    public void sendResult(String message) {
        Intent intent = new Intent(SOCKET_RESULT);
        if (message != null)
            intent.putExtra(SOCKET_READ_SUCCESSFUL, message);
        broadcaster.sendBroadcast(intent);
    }

    private static String getByteBinaryString(byte b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 7; i >= 0; --i) {
            sb.append(b >>> i & 1);
        }
        return sb.toString();
    }

    public int checkSwitchCode(byte switchCode) {

        String[] greyCodeTable = {"11111", "10111", "10011", "00011", "00100", "01100", "01000", "01010", "01110", "00110", "00010",
                "10010", "10110", "11110", "11010", "11000", "11100", "10100", "10000", "10001", "10101",
                "11101", "11001", "11011"};
        String returnString = getByteBinaryString(switchCode).substring(3);
        int switchValue = 0;

        for (int i = 0; i < 24; i++) {
            if (returnString.equals(greyCodeTable[i])) {
                switchValue = i;
            }
        }
        return switchValue;
    }

    public int checkMiniSwitchCode(byte switchCode) {

        String[] greyCodeTable = {"1100", "0110", "1010", "1001", "0001"};
        String returnString = getByteBinaryString(switchCode).substring(0,4);;
        int switchValue = 0;

        for (int i = 0; i < 5; i++) {
            if (returnString.equals(greyCodeTable[i])) {
                switchValue = i;
            }
        }
        return switchValue;
    }

    public int getSwitchCodeTransformed() {
        return switchCode;
    }
    public String getLedCode() {
        return ledCode;
    }
    public int getMiniSwitchCodeTransformed() {
        return miniSwitchCode;
    }
    public String getButtonRow() { return buttonRow; }
}
