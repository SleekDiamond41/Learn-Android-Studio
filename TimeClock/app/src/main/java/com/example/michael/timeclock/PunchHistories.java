package com.example.michael.timeclock;

import android.content.Context;
import android.support.annotation.NonNull;
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
import java.util.List;


/**
 * Created by Michael on 6/26/17.
 */


class PunchHistories {


    PunchHistories(FileInputStream fis, FileOutputStream fos) {

        DataInputStream dis = new DataInputStream(fis);
        dos = new DataOutputStream(fos);

        punchPairsList = new ArrayList<>();

        //read all punchPairsList from file
        try {
            while (dis.available() > 0) {
                //initialize a new PunchPair, if there is one in the file
                punchPairsList.add(new PunchPair(dis));

                //if nothing was read from the file, break out and move on
                if(!punchPairsList.get(punchPairsList.size() - 1).getIsSet()) {
                    punchPairsList.remove(punchPairsList.size() - 1);
                    break;
                }
            }
        }
        catch (IOException e) {
            Log.d("FILE", "ERROR: PunchHistories(FileInputStream, FileOutputStream)");
        }
        numberOfPunchesInFile = getPunchPairCount();
    }


    //method to add new PunchPair
    boolean newPunch(boolean isPunchIn) {
        punchPairsList.add(new PunchPair(isPunchIn));

        //don't bother checking how long it has been since the last check in
        if(punchPairsList.size() > 1) {
            Integer newestItem;

            //get the indices of the newest and previous punches
            newestItem = punchPairsList.size() - 1;

            Integer requiredDelayBetweenPunches = 60000;
            if ((punchPairsList.get(newestItem).getPunchInTimeInMillis()
                    - punchPairsList.get(newestItem).getPunchOutTimeInMillis())
                    < requiredDelayBetweenPunches) {

                //if enough time has NOT passed, reject new punch
                punchPairsList.remove((int) newestItem);
                return false;
            }
        }
        //if enough time has passed, accept new punch
        return true;
    }


    Boolean isPunchIn(int x) {
        return (x%2 == 0);
    }


    void writePunchesToFile() throws IOException {

        while(numberOfPunchesInFile < punchPairsList.size())
        {
            punchPairsList.get(numberOfPunchesInFile).writePunchToFile(dos);
            ++numberOfPunchesInFile;
        }
        dos.flush();
    }

    private String getFormattedDate(int x) {
        return punchPairsList.get(x).getDateFormatted();
    }

    private String getFormattedTime(int x) {
        return punchPairsList.get(x).getTimeFormatted();
    }

    View getPunchHistoryView(Context context) {

        SimpleLayoutInflater inflater = new SimpleLayoutInflater(context);

        ViewGroup fullTableLayout = (ViewGroup) inflater.inflate(R.layout.table_layout_full_layout);
        ViewGroup tableRowLayout;
        TextView tv;

        for (Integer i = 0; i < getPunchPairCount(); ++i) {

            //fill a row with cells
            tableRowLayout = (ViewGroup) inflater.inflate(R.layout.practice_layout_tablerow);

            //assign date for newest PUNCHPAIR
            punchPairsList.get(i).getDateFormatted();

            //check for punch in

            //if there is a punch in, assign date to tableRowlayout child (0)
            //and assign time to tableRowLayout child (1)

            //check for punch out

            //if there is a punch out, assign time to tableRowLayout child (2)





            if (getFormattedDate(i).equals(getFormattedDate(i + 1))) {
                tv = (TextView) tableRowLayout.getChildAt(0);
                tv.setText(getFormattedDate(i));

                tv = (TextView) tableRowLayout.getChildAt(1);
                tv.setText(getFormattedTime(i));
            }

            ++i;
            tv = (TextView) tableRowLayout.getChildAt(2);
            if (!i.equals(getPunchPairCount())) {
                tv.setText(getFormattedTime(i));
            } else {
                tv.setText("");
            }
            fullTableLayout.addView(tableRowLayout);
        }

        return fullTableLayout;
    }

    @NonNull
    private Integer getPunchPairCount() {
        return punchPairsList.size();
    }

    private Integer numberOfPunchesInFile;
    private List<PunchPair> punchPairsList;
    private DataOutputStream dos;
}
