package com.twmiwi.com.washingcontrol;


import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainControl extends AppCompatActivity {

    boolean updateInProgress = false;
    private SocketService mService = null;
    Boolean mIsBound = false;
    Boolean readyToProgram = true;
    //    String ipAddress;
//    int port;
    BroadcastReceiver receiver;



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

    private void doBindService() {
        //mService.setIpPort(ipAddress, port);
        bindService(new Intent(this, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        if (mService != null) {
            mService.IsBoundable();
        }
    }


    private void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_control);
        super.setTitle("Washing Control");

        final MainControl activity = this;
        Button startRequestButton = (Button) findViewById(R.id.startRequestButton);
        Button startProgramButton = (Button) findViewById(R.id.startProgramButton);


//        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        ipAddress = sharedPref.getString("host", "192.168.160.35");
//        port = sharedPref.getInt("port", 2001);

        final RadioButton radioButtonGroup[] = {(RadioButton) findViewById(R.id.ledEnd), (RadioButton) findViewById(R.id.ledPumpen),
                (RadioButton) findViewById(R.id.ledSpuelen), (RadioButton) findViewById(R.id.ledHauptWaesche),
                (RadioButton) findViewById(R.id.ledVorwaesche), (RadioButton) findViewById(R.id.ledOn)};


        final ToggleButton toggleButtonGroup[] = {(ToggleButton) findViewById(R.id.toggleWaterPlus), (ToggleButton) findViewById(R.id.toggleVorwäsche),
                (ToggleButton) findViewById(R.id.toggleEinweichen), (ToggleButton) findViewById(R.id.toggleShort)};

        final ProgramSwitch programSwitch = (ProgramSwitch) findViewById(R.id.imageRotor);
        programSwitch.initialize();
        final MiniSwitch miniSwitch = (MiniSwitch) findViewById(R.id.imageMiniSwitch);
        miniSwitch.initialize();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(SocketService.SOCKET_READ_SUCCESSFUL);
                if (s.equals("readOK")) {
                    programSwitch.setActualStatus(mService.getSwitchCodeTransformed());
                    miniSwitch.setActualStatus(mService.getMiniSwitchCodeTransformed());

                    String ledCode = mService.getLedCode();
                    String buttonRow = mService.getButtonRow();

                    for (int i = 0; i < 6; i++) {
                        if ((ledCode.charAt(i)) == '1') {
                            radioButtonGroup[i].setChecked(true);
                        } else {
                            radioButtonGroup[i].setChecked(false);
                        }
                    }

                    for (int i = 0; i < 4; i++) {
                        if (buttonRow.charAt(i) == '1') {
                            toggleButtonGroup[i].setChecked(true);
                        } else {
                            toggleButtonGroup[i].setChecked(false);
                        }
                    }

                } else if (s.equals("writeOK")) {
                    Toast.makeText(getApplicationContext(), "Programmierung erfolgreich", Toast.LENGTH_LONG).show();
                }
            }
        };


        startRequestButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mService != null) {
                    mService.sendCommand(0);
                }
            }

        });
        startProgramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i<5; i++) {
                    if (radioButtonGroup[i].isChecked()){
                        readyToProgram = false;
                        break;
                    }
                }
                if (mService != null && readyToProgram) {
                    mService.sendCommand(1);
                } else
                    Toast.makeText(getApplicationContext(),"Maschine kann nicht programmiert werden", Toast.LENGTH_LONG).show();
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
//                startActivity(i);
                startActivityForResult(i, 222);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        if (requestCode == 222) {
//            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//            ipAddress = sharedPref.getString("host", "");
//            port = Integer.parseInt(sharedPref.getString("port", ""));
//            mService.unbindService(mConnection);
//            Intent intent = new Intent(this, SocketService.class);
//            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//            startService(intent);
        }

    }
    /*

                if (resultCode == RESULT_OK) {
                String myValue = data.getStringExtra("valueName");

                in der anderen activity:

                setResult(int, Intent)
                Intent resultData = new Intent();
                resultData.putExtra("valueName", "valueData");
                setResult(Activity.RESULT_OK, resultData);
                finish();


     */

    @Override
    protected void onStart() {
        super.onStart();
        if (!mIsBound) {
            Intent intent = new Intent(this, SocketService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            startService(intent);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(SocketService.SOCKET_RESULT)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mService != null) {
            unbindService(mConnection);
            mService = null;
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
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
