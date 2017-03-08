package com.twmiwi.com.washingcontrol;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainControl extends AppCompatActivity {

    boolean updateInProgress = false;
    private SocketService mService = null;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            SocketService.LocalBinder binder = (SocketService.LocalBinder) service;
            mService = binder.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_control);
        super.setTitle("Washing Control");

        final MainControl activity = this;
        Button startRequestButton = (Button) findViewById(R.id.startRequestButton);
        final TextView printView = (TextView) findViewById(R.id.printView);


        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String defaultValueHost = "192.168.160.35";
        final String defaultValuePort = "2001";

        ProgramSwitch programSwitch = (ProgramSwitch) findViewById(R.id.imageRotor);
        programSwitch.initialize();

        startRequestButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sharedPref.getString("host", "").equals("") || sharedPref.getString("port", "").equals("")) {

                    Toast.makeText(getApplicationContext(), "Bitte Adresse des Servers einstellen", Toast.LENGTH_LONG).show();

                } else {


                    String ipAddress = sharedPref.getString("host", "");
                    int port = Integer.parseInt(sharedPref.getString("port", ""));
                    Log.d("ipdadresse", ipAddress);
                    Log.d("port", sharedPref.getString("port", defaultValuePort));


                    if (mService!=null) {
                        mService.sendCommand("abc");
                    }

//
//
//                        if (!updateInProgress) {
//                        ServerConnection t = new ServerConnection(activity, ipAddress, port);
//
//                            t.execute();
//                        }

                }
            }

        });
    }

    public void setUpdateOnProgress(Boolean inProgress) {
        updateInProgress = inProgress;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent i = new Intent(MainControl.this, OptionsPanel.class);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, SocketService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mService != null) {
            unbindService(mConnection);
            mService = null;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
