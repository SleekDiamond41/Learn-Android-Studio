package com.example.michael.timeclock;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Michael on 6/26/17.
 */


class PunchHistories {


    PunchHistories(FileInputStream fis, FileOutputStream fos) {

        DataInputStream dis = new DataInputStream(fis);
        dos = new DataOutputStream(fos);

        punchTimes = new ArrayList<>();

        //read all punchTimes from file
        try {
            while (dis.available() > 0) {
                //initialize a new PunchTime, if there is one in the file
                punchTimes.add(new PunchTime(dis));

                //if nothing was read from the file, break out and move on
                if(!punchTimes.get(punchTimes.size() - 1).getIsSet()) {
                    punchTimes.remove(punchTimes.size() - 1);
                    break;
                }
            }
        }
        catch (IOException e) {
            Log.d("FILE", "ERROR: PunchHistories(FileInputStream, FileOutputStream)");
        }
        punchesAlreadyInFile = getPunchesCount();
    }


    //method to add new PunchTime
    boolean newTimePunch() {
        punchTimes.add(new PunchTime());

        //don't bother checking how long it has been since the last check in
        if(punchTimes.size() > 1) {
            Integer newestItem;
            Integer previousItem;

            //get the indices of the newest and previous punches
            newestItem = punchTimes.size() - 1;
            previousItem = punchTimes.size() - 2;

            Integer requiredDelayBetweenPunches = 60000;
            if ((punchTimes.get(newestItem).getCalTimeInMillis()
                    - punchTimes.get(previousItem).getCalTimeInMillis())
                    < requiredDelayBetweenPunches) {

                //if enough time has NOT passed, reject new punch
                punchTimes.remove((int) newestItem);
                return false;
            }
        }
        //if enough time has passed, accept new punch
        return true;
    }


    Boolean isPunchIn(int x) {
        return (x%2 == 0);
    }


    void writePunchesToFile() throws IOException, InterruptedException {

        for (int i = punchesAlreadyInFile; i < punchTimes.size(); ++i) {
            punchTimes.get(i).writePunchToFile(dos);
        }

        dos.flush();
    }

    ViewGroup getPunchHistoryViewGroup(ViewGroup tableLayout, ViewGroup tableRow, TextView textCell) {

        //add new cell to table row

//        textCell.setText("TEST");
        tableRow.addView(textCell);

//        textCell.setText("IS WORKING");
        tableRow.addView(textCell);

        tableLayout.addView(tableRow);

        //if there is an odd number of items, set a new cell in the row
        //with a blank string as its text



        //Add new row to table layout

        //Add table layout to viewGroup


        /*for (int i = 0; i < 2; ++i) {


            cell = findViewById(R.id.textViewCellRight);

            ll.addView(viewGroup);


            ll.addView(findViewById(R.id.TableRowShowPunchTimes));


            //TableRow tr = new TableRow();


            ll.addView(tr);

            ll.

                    tr.addView(findViewById(R.id.col1));

            tr.addView(findViewById(R.id.col2));

            ll.addView(tr);
        }*/
        return tableLayout;
    }

    public String printFormattedPunchTime(int x) {
        return punchTimes.get(x).formatPunchTime();
    }

    @NonNull
    private Integer getPunchesCount () {
        return punchTimes.size();
    }

    private Integer punchesAlreadyInFile;
    private List<PunchTime> punchTimes;
    private DataOutputStream dos;
}
