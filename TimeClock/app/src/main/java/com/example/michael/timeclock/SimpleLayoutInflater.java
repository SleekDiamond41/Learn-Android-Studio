package com.example.michael.timeclock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Michael on 6/30/17.
 */

class SimpleLayoutInflater {

    private LayoutInflater inflater;

    SimpleLayoutInflater(Context c) {
        inflater = LayoutInflater.from(c);
    }

    View inflate(int resource) {
        return inflater.inflate(resource, null);
    }
    public LayoutInflater getLayoutInflater() {
        return inflater;
    }
}
