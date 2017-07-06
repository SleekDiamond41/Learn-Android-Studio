package com.example.michael.timecard;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Michael on 7/4/17.
 */

class Day {
    Day(boolean isPunchIn, SimpleLayoutInflater simpleLayoutInflater) throws IOException {
        punches = new Calendar[2];
        punches[0] = Calendar.getInstance();
        punches[1] = Calendar.getInstance();
        this.simpleLayoutInflater = simpleLayoutInflater;
        if(isPunchIn) {
            addPunch(IN);
        } else {
            addPunch(OUT);
        }
    }
    Day(boolean isPunchIn, SimpleLayoutInflater simpleLayoutInflater, Calendar calendar) throws IOException {
        punches = new Calendar[2];
        punches[0] = Calendar.getInstance();
        punches[1] = Calendar.getInstance();
        this.simpleLayoutInflater = simpleLayoutInflater;
        if(isPunchIn) {
            addPunch(IN, calendar);
        } else {
            addPunch(OUT, calendar);
        }
    }

    Day(DataInputStream dis, SimpleLayoutInflater simpleLayoutInflater) {
        punches = new Calendar[2];
        punches[0] = Calendar.getInstance();
        punches[1] = Calendar.getInstance();
        this.simpleLayoutInflater = simpleLayoutInflater;

        try {
            readDayFromFile(dis);
        }
        catch (Exception e) {
            Log.d("FILE", "Error reading input from file");
        }
    }

    void writeFullDayToFile (DataOutputStream dos) {
        try {
            writeSinglePunchToFile(dos, IN);
        } catch (IOException e) {
            Log.d("FILE", "Error writing full day to file");
        }
        try {
            writeSinglePunchToFile(dos, OUT);
        } catch (IOException e) {
            Log.d("FILE", "Error writing full day to file");
        }
    }

    private void writeSinglePunchToFile(DataOutputStream dos, int i) throws IOException {
        if (punches[i] != null) {
            dos.writeLong(punches[i].getTimeInMillis());
        } else {
            dos.writeLong(0L);
        }
    }

    void addPunch(int i) throws IOException {
        try {
            punches[i] = Calendar.getInstance();
        }
        catch (RuntimeException e) {
            punches[i] = null;
        }
        updateDayView();
    }
    void addPunch(int i, Calendar calendar) throws IOException {
        try {
            punches[i] = calendar;
        }
        catch (RuntimeException e) {
            punches[i] = null;
        }
        updateDayView();
    }

    private void readDayFromFile(DataInputStream dis) throws IOException {
        for (int i = 0; i < 2; ++i) {
            try {
                punches[i].setTimeInMillis(dis.readLong());
            } catch (IOException e) {
                punches[i] = null;
            }
        }
    }


    View getDayView () {
        updateDayView();

        return viewOfSelf;
    }


    private void updateDayView () {
        if(viewOfSelf == null) {
            viewOfSelf = (ViewGroup) simpleLayoutInflater.inflate(R.layout.table_row_content_main);
        }

        TextView textView;

        //set text for date
        textView = (TextView) viewOfSelf.getChildAt(0);
        setTextView(getFormattedString(DATE), textView);

        //set text for punch in
        textView = (TextView) viewOfSelf.getChildAt(1);
        setTextView(getFormattedString(IN), textView);

        //set text for punch out
        textView = (TextView) viewOfSelf.getChildAt(2);
        setTextView(getFormattedString(OUT), textView);
    }


    private void setTextView(String string, TextView tv) {
        tv.setText((string != null) ? string : "ERROR");
    }


    @Nullable
    private String getFormattedString(int field) {
        if(field <= 1 && (punches[field] == null || punches[field].getTimeInMillis() == 0)) {
            return "";
        }
        try {
            if (field == IN) {
                if(punches[IN] != null)
                    return String.format(Locale.US, "%1$tl:%1$tM%1$tp", punches[IN]);
                else
                    return "";
            } else if (field == OUT) {
                if(punches[OUT] != null)
                    return String.format(Locale.US, "%1$tl:%1$tM%1$tp", punches[OUT]);
                else
                    return "";
            } else {
                if (punches[OUT] == null) {
                    return String.format(Locale.US, "%1$tm/%1$te/%1$ty", punches[IN]);
                } else {
                    return String.format(Locale.US, "%1$tm/%1$te/%1$ty", punches[OUT]);
                }
            }
        }
        catch (Exception e) {
            return "";
        }
    }



    Calendar punches[];
    static final int IN = 0;
    static final int OUT = 1;
    static final int DATE = 2;
    private ViewGroup viewOfSelf;
    SimpleLayoutInflater simpleLayoutInflater;
}
