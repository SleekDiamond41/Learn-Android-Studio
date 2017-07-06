package com.example.michael.timeclock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Michael on 6/26/17.
 */

class PunchPair {

    //constructor if new PunchPair
    PunchPair(boolean isPunchIn) {
        if(isPunchIn) {
            punchIn = Calendar.getInstance();
        }
        else {
            punchOut = Calendar.getInstance();
        }
        punchInIsSet = true;
    }

    PunchPair(DataInputStream dis) throws IOException {
        readPunchFromFile(dis, true);
        readPunchFromFile(dis, false);
    }


    //write PunchPair to file
    void writePunchToFile (DataOutputStream dos) throws IOException {
        dos.writeLong(punchIn.getTimeInMillis());
        dos.writeLong(punchOut.getTimeInMillis());
    }


    //read PunchPair from file
    private void readPunchFromFile(DataInputStream dis, boolean isPunchIn) throws IOException {

        if(isPunchIn) {
            try {
                punchIn.setTimeInMillis(dis.readLong());
                punchInIsSet = true;
            }
            catch (EOFException e) {
                punchInIsSet = false;
            }
        }

        else {
            try {
                punchOut.setTimeInMillis(dis.readLong());
                punchOutIsSet = true;
            }
            catch (EOFException e) {
                punchOutIsSet = false;
            }
        }
    }

    String getDateFormatted() {
        return String.format("%1$tm" + "/" + "%1$te" + "/" + "%1$ty", punchIn);
    }

    //method for getting the current time
    String getTimeFormatted() {
        return String.format("%1$tl:%1$tM %1$tp", punchIn);
    }

    boolean getIsSet() { return punchInIsSet; }

    Long getPunchInTimeInMillis () {
        return punchIn.getTimeInMillis();
    }

    Long getPunchOutTimeInMillis () {
        return punchOut.getTimeInMillis();
    }

    private boolean punchInIsSet;
    private boolean punchOutIsSet;
    private Calendar punchIn;
    private Calendar punchOut;
}
