package com.twmiwi.com.washingcontrol;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by tw on 05.03.2017.
 */

public class OptionsPanel extends AppCompatActivity {

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_options_panel);

        Button applyValuesButton = (Button) findViewById(R.id.applyValues);
        final EditText serverAddress = (EditText) findViewById(R.id.serverAddress);
        final EditText portAddress = (EditText) findViewById(R.id.serverPort);

        //preferences
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = sharedPref.edit();


        serverAddress.setText(sharedPref.getString("host",""));
        portAddress.setText(sharedPref.getString("port", ""));

        applyValuesButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                editor.putString("host", serverAddress.getText().toString());
                Log.d("host button",serverAddress.getText().toString() );
                editor.putString("port", portAddress.getText().toString());
                editor.apply();

            }


        });

    }


}