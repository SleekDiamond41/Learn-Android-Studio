package com.example.michael.timecard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

/**
 * Created by Michael on 7/6/17.
 */

public class TimeCard {
    public TimeCard(Context context) throws Exception {
        try {
            layoutInflater = LayoutInflater.from(context);
        } catch (Exception e) {
            throw new Exception();
        }
        //try
        //instantiate and assign data streams
        //catch

        //try
        //generate scrollingTableView
        //catch

        //try
        //define actions for buttons
        //catch
    }

    void saveData() {

    }

    private ArrayList<Day> days;
    private ViewGroup scrollingTableView;
    private LayoutInflater layoutInflater;
    private final String filename = "Time Punch History";
    private DataInputStream dis;
    private DataOutputStream dos;
}
