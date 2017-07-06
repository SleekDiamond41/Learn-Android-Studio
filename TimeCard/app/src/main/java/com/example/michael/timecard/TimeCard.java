package com.example.michael.timecard;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Michael on 7/4/17.
 */

class TimeCard {
    TimeCard(FileInputStream fis, FileOutputStream fos, Context context) throws IOException {
        DataInputStream dis = new DataInputStream(fis);
        this.dos = new DataOutputStream(fos);
        simpleLayoutInflater = new SimpleLayoutInflater(context);

        days = new ArrayList<>();
        while (dis.available() > 0) {
            days.add(new Day(dis, simpleLayoutInflater));
        }
    }

    private boolean calendarFieldsAreTheSame(Calendar cal, int IN_OUT, int field, Day day) {
        return ((cal.get(field) - day.punches[IN_OUT].get(field)) == 0);
    }

    void recordNewPunch(boolean isPunchIn) {
        ViewGroup tableBlockForRows = (ViewGroup)((ViewGroup) timeCardView.
                getChildAt(1)).getChildAt(0);
        int indexOfMostRecentDay = days.size() - 1;
        try {
            Calendar temp = Calendar.getInstance();
            if (isPunchIn) {
                days.add(new Day(true, simpleLayoutInflater, temp));
            } else {
                //check if same day as most recent punch in
                try {
                    if(days.get(indexOfMostRecentDay).punches[Day.OUT] != null) {
                        throw new Exception();
                    }
                    if(calendarFieldsAreTheSame(temp, Day.IN, Calendar.DAY_OF_YEAR,
                            days.get(indexOfMostRecentDay))) {
                        if(calendarFieldsAreTheSame(temp, Day.IN, Calendar.YEAR,
                                days.get(indexOfMostRecentDay))) {
                            days.get(indexOfMostRecentDay).addPunch(Day.OUT, temp);
                            if(tableBlockForRows.getChildCount() > 1) {
                                tableBlockForRows.removeViewAt(1);
                            }
                        }
                    }
                } catch (Exception EXC) {
                    days.add(new Day(false, simpleLayoutInflater));
                }
            }
            tableBlockForRows.addView(days.get(indexOfMostRecentDay).
                    getDayView(), 1);
        } catch (Exception e) {
            Log.d("NEW", "Error recording new punch");
        }
    }


    View getViewableTimeCard () throws RuntimeException {
        timeCardView = (ViewGroup) simpleLayoutInflater.inflate(R.layout.activity_main);
        ViewGroup scrollableTableLayout = (ViewGroup) ((ViewGroup)
                timeCardView.getChildAt(1)).getChildAt(0);

        scrollableTableLayout.addView(addFirstRowToView(simpleLayoutInflater));

        if(days != null) {
            for (int i = 0; i < days.size(); ++i) {
                scrollableTableLayout.addView(days.get(i).getDayView(), 1);
            }
        }

        return timeCardView;
    }

    private View addFirstRowToView (SimpleLayoutInflater simpleLayoutInflater) {
        ViewGroup rowGroup = (ViewGroup) simpleLayoutInflater.inflate(R.layout.table_row_content_main);
        String firstRowText[] = {"Date", "Punch In", "Punch Out"};

        for(int i = 0; i < 3; ++i) {
            setTextView(firstRowText[i],
                    (TextView) rowGroup.getChildAt(i));
        }

        return rowGroup;
    }
    private void setTextView(String string, TextView tv) {
        tv.setText(string);
    }


    //call this when super does "onStop" or "onDestroy"
    void writeDaysToFile() {
        for (int i = 0; i < days.size(); ++i) {
            days.get(i).writeFullDayToFile(dos);
        }
    }


    private DataOutputStream dos;
    private ArrayList<Day> days;
    private ViewGroup timeCardView;
    private SimpleLayoutInflater simpleLayoutInflater;
    private static final int DATE_VIEW = 0;
    private static final int IN_VIEW = 1;
    private static final int OUT_VIEW = 2;
}
