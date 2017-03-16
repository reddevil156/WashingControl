package com.twmiwi.com.washingcontrol;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

/**
 * Created by tobia on 12.03.2017.
 */

public class InputReader extends AsyncTask<String, String, byte[]> {

    private InputStream input;
    AsyncResponse delegate = null;
    private byte[] received = new byte[4];
    private int loopCounter;



    public InputReader(InputStream input, int loopCounter, AsyncResponse delegate) {
        this.input = input;
        this.delegate = delegate;
        this.loopCounter = loopCounter;

    }


    @Override
    protected byte[] doInBackground(String... params) {


        try {

            Byte lineReader;
            int zaehler = 0;
            System.out.println("loopcounter"+loopCounter);
            // 9 Steuerzeichen kommen vor dem eigentlichen Versand, diese werden verworfen!
            while (zaehler < loopCounter) {
                lineReader = (byte) input.read();
                received[zaehler] = lineReader;
//                Log.e("schleife",getByteBinaryString(received[zaehler]));

                zaehler++;
            }


        } catch (SocketTimeoutException e) {
            Log.e("error inputstream", "Socket timeout",e);
        } catch (IOException e) {
            Log.e("error inputstream", "IOException",e);
        }


        return received;
    }

    @Override
    protected void onPostExecute (byte[] values){
        delegate.processFinish(values);
    }



}
