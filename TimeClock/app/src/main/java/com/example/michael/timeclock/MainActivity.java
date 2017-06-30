package com.example.michael.timeclock;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String filename = "PunchTime History.txt";

        try {
            fis = this.openFileInput(filename);
        } catch (FileNotFoundException e) {
            Log.d("FILE", "FileInputStream was not initialized.");
        }
        try {
            fos = this.openFileOutput(filename, MODE_APPEND);
        } catch (FileNotFoundException e) {
            Log.d("FILE", "FileOutputStream was not initialized");
        }

        punchHistories = new PunchHistories(fis, fos);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(punchHistories.newTimePunch()) {
                    Snackbar.make(view, "Punch Accepted", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    Snackbar.make(view, "You already punched in recently.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }


                try {
                    punchHistories.writePunchesToFile();
                }
                catch (IOException e) {
                    Log.d("WRITE", "IO Exception in writing file");
                }
                catch (InterruptedException e) {
                    Log.d("WRITE", "Interrupted exception in writing file.");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_sheet, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    PunchHistories punchHistories;
    FileInputStream fis;
    FileOutputStream fos;
}
