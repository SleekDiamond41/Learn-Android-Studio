package com.example.michael.timeclock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Michael on 6/26/17.
 */

class PunchTime {

    //constructor if new PunchTime
    PunchTime() {
        cal = Calendar.getInstance();
        isSet = true;
    }

    PunchTime(DataInputStream dis) throws IOException {
        cal = Calendar.getInstance();
        readPunchFromFile(dis);
    }


    //write PunchTime to file
    void writePunchToFile (DataOutputStream dos) throws IOException {
        dos.writeLong(cal.getTimeInMillis());
    }


    //read PunchTime from file
    private void readPunchFromFile(DataInputStream dis) throws IOException {

        try {
            cal.setTimeInMillis(dis.readLong());
            isSet = true;
        }
        catch (EOFException e) {
            isSet = false;
        }
    }


    //method for getting the current time
    String formatPunchTime() {
        Integer H = cal.get(Calendar.HOUR_OF_DAY);
        Integer M = cal.get(Calendar.MINUTE);

        return H.toString() + ":" + M.toString() + Calendar.AM_PM;
    }

    boolean getIsSet() { return isSet; }

    Long getCalTimeInMillis () {
        return cal.getTimeInMillis();
    }

    private boolean isSet;
    private Calendar cal;
}
