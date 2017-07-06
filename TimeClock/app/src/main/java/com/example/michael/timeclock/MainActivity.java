package com.example.michael.timeclock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    PunchHistories punchHistories;
    FileInputStream fis;
    FileOutputStream fos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        String filename = "PunchPair History.txt";

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


        setContentView(punchHistories.getPunchHistoryView(this));

/*
        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
            }
        });
*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_sheet, menu);
        return true;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            punchHistories.writePunchesToFile();
        } catch (IOException e) {
            Log.d("FILE", "IO Exception in writing file");
        }
    }


    @Override
    public void onStop() {
        try {
            punchHistories.writePunchesToFile();
        } catch (IOException e) {
            Log.d("FILE", "IO Exception in writing file");
        }

        super.onStop();
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


}
