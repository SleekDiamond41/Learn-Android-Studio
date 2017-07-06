package com.example.michael.timecard;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        {
            String filename = "Punch History1.txt";
            try {


                //inflate R.layout.activity_main
                SimpleLayoutInflater SLI = new SimpleLayoutInflater(this);
                ViewGroup fullView = (ViewGroup) SLI.inflate(R.layout.activity_main);
                setContentView(fullView);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);


                //set buttons "OnClick" to record new punches, as necessary

                //add new rows as needed







                //fis = new FileInputStream(filename);
                Integer i = fis.available();
                Log.d("FILE", i.toString());
                fos = new FileOutputStream(filename);
                //fis = this.openFileInput(filename);
                //fos = this.openFileOutput(filename, MODE_APPEND);
                timeCard = new TimeCard(fis, fos, this);
                fis.close();
                PrintWriter pw = new PrintWriter(filename);
                pw.write("");
                pw.flush();
                pw.close();

                ViewGroup nextView = (ViewGroup) timeCard.getViewableTimeCard();
                setContentView(nextView);


                Button buttonPunchIn = (Button) findViewById(R.id.buttonPunchIn);
                buttonPunchIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            timeCard.recordNewPunch(true);
                            Snackbar.make(view, "Punch Accepted", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } catch (Exception e) {
                            Snackbar.make(view, "Punch could not be recorded", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });
                Button buttonPunchOut = (Button) findViewById(R.id.buttonPunchOut);
                buttonPunchOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            timeCard.recordNewPunch(false);
                            Snackbar.make(view, "Punch Accepted", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } catch (Exception e) {

                            Log.d("NEW", e.getCause().toString());

                            Snackbar.make(view, "Punch could not be recorded", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });

            } catch (Exception e) {
                Log.d("FILE", "Error creating, reading from, or writing to file");
            }
        }


    }


    @Override
    public void onStop() {
        //timeCard.writeDaysToFile();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        //timeCard.writeDaysToFile();
        super.onDestroy();
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    TimeCard timeCard;
    FileInputStream fis;
    FileOutputStream fos;
}
