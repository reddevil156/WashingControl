package com.twmiwi.com.washingcontrol;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by tobia on 08.03.2017.
 */

class CommandWriter extends AsyncTask<String, String, Boolean> {

    OutputStream outputStream;
    String command;
    AsyncResponse delegate = null;


    public CommandWriter(OutputStream out, String command, AsyncResponse delegate) {
        this.outputStream = out;
        this.command = command;
        this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            outputStream.write(command.getBytes());

        } catch (Exception e) {
            Log.e("TCP", "Fehler beim schreiben", e);

//            if (e.getMessage().contains("Broken Pipe")) {
//                try {
//                    outputStream.close();
//                } catch (IOException e1) {
//                    Log.e("TCP", "Fehler beim schließen des OutputStreams", e1);
//                }
//            }

            return false;


        }
        return true;
    }

            @Override
            protected void onPostExecute (Boolean values){
                delegate.processFinish(values);
            }
        }