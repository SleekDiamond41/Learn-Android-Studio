package com.example.michael.timecard;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

/**
 * Day is an object which contains an array of two Calendar objects and a ViewGroup
 * object. Its purpose is to track a pair of time punches, one in and one out, each
 * stored in one of the two Calendar objects. The Calendar objects are always null
 * unless instantiated.
 * Created by Michael on 7/6/17.
 */

class Day {
	/**
	 * Construct a Day object, assigning its view and new punch.
	 *
	 * @param layoutInflater The LayoutInflater through which Day may inflate its
	 *                       own instance of a TableRow. This ensures that it is never
	 *                       called to apply to a View other than TableRow.
	 * @param newPunch The time of the new punch, to be added during instantiation.
	 * @param IN_OUT The field of the new punch to be added, either a punch IN or OUT.
	 * @throws ArrayIndexOutOfBoundsException Exception is thrown if IN_OUT is out
	 * of bounds. Day will not be instantiated, must be caught and handled accordingly.
	 */
	Day(Calendar newPunch, LayoutInflater layoutInflater, int IN_OUT)
			throws ArrayIndexOutOfBoundsException {
		if(!fieldIsIn_OUT(IN_OUT)) {
			throw new ArrayIndexOutOfBoundsException();
		}

		punches = new Calendar[2];
		punches[IN_OUT] = newPunch;
		tableRowView = (ViewGroup) layoutInflater.inflate(R.layout.table_row, null);
		updateRowView();
	}


	/**
	 * Construct a new Day object, read in any values possible from a file. Fields of
	 * the Day object that are not filled will remain null.
	 * @param layoutInflater The LayoutInflater through which Day may inflate its
	 *                       own instance of a TableRow. This ensures that it is never
	 *                       called to apply to a View other than TableRow.
	 * @param dis The DataInputStream from which values are to be read.
	 */
	Day(DataInputStream dis, LayoutInflater layoutInflater) {
		punches = new Calendar[2];
		tableRowView = (ViewGroup) layoutInflater.inflate(R.layout.table_row, null);

		Long tempLong;
		for (int i = 0; i <= MAX_INDEX; ++i) {
			try {
				tempLong = dis.readLong();
				if (tempLong != 0) {
					punches[i] = Calendar.getInstance(Locale.US);
					punches[i].setTimeInMillis(tempLong);
				}
			} catch (IOException e) {
				Log.d("DAY", "ERROR reading from file");
			}
		}
		updateRowView();
	}


	/**
	 * Write the calendar values of this Day to a given output stream.
	 *
	 * @param dos The DataInputStream to which the data is written
	 */
	void writeDayToFile(DataOutputStream dos) {

		for(int i = 0; i < 2; ++i) {
			try {
				if (punches[i] != null) {
					dos.writeLong(punches[i].getTimeInMillis());
				} else {
					dos.writeLong(0L);
				}
			} catch (IOException e) {
				Log.d("DAY", "Punch could not be written to file");
			}
		}
	}


	/**
	 * Add a new punch time to an existing Day object.
	 * @param newPunch The new time to be assigned to a given field.
	 * @param IN_OUT The field representing which
	 * @throws IllegalArgumentException Thrown if IN_OUT variable is out of bounds.
	 * Since this Exception can only occur when caused by the program, not the user,
	 * (in theory) it SHOULD NEVER be thrown. Fingers crossed.
	 *      FOR NOW this exception will also be thrown if the value to be added
	 *      would replace an existing value. To replace existing values, call
	 *      updatePunch(Calendar, IN_OUT).
	 *
	 * @throws //some other kind of exception, flagging to the Caller that
	 *         //the user wants to create a new Day object
	 */
	void addPunch(Calendar newPunch, int IN_OUT)
			throws IllegalArgumentException {

		//make sure requested punch field is within acceptable bounds
		if(!fieldIsIn_OUT(IN_OUT)) {
			throw new IllegalArgumentException();
		}

		//if field does not already have a set value, assign new value
		if(punches[IN_OUT] == null) {
			punches[IN_OUT] = newPunch;
			updateRowView();
		} else {
			//for now, throw IllegalArgumentException...

			throw new IllegalArgumentException();

			//...but eventually do this!
			//ask the user if they are sure the REALLY want to overwrite the existing
			//punch time, or if they would rather create a new Day

				//If they want to overwrite it, do so, and call updateRowView()

				//If the user wants to create a new day, throw an Exception to
					//let the parent know to try making a new Day.
		}
	}


	/*
	* This method will be used to update an existing punch.
	* Prompt the user if they are sure they want to change it. If so,
	* do it.
	* */
	void updatePunch(Calendar newPunch, int IN_OUT) {

	}


	/**
	 * A method for getting the tableRowView, available so that a parent may add
	 * this as a child. Adoption is a beautiful thing.
	 * @return tableRowView, the viewable object representing this Day object
	 */
	TableRow getTableRowView() {
		return (TableRow) tableRowView;
	}


	/**
	 * Update tableRowView, try/catch block ensures that events out of bounds
	 * will not break the app. Attempting to
	 */
	private void updateRowView() {
		TextView textView;

		for (int i = 0; i <= 2; ++i) {
			try {
				textView = (TextView) tableRowView.getChildAt(i);
				textView.setText(getStringField(i-1));
			} catch (Exception e) {
				Log.d("DAY", "ERROR updating tableRowView");
			}
		}
	}


	/**
	 * Check whether a passed calendar occurred after a requested field, IN
	 * or OUT.
	 *
	 * @param calendar Calendar object. If this instance occurred after
	 *                 the instance it is compared to, method will return true.
	 * @param IN_OUT Index of the field to be compared. If the Calendar that was
	 *               passed to the method occurred after the field that was requested,
	 *               then the method will return true.
	 * @return returns true when calendar occurred after the requested field. Else
	 * returns false.
	 * @throws NullPointerException Thrown if the requested field IN_OUT is null.
	 */
	boolean isAfter(Calendar calendar, int IN_OUT) throws NullPointerException {
		return calendar.after(punches[IN_OUT]);
	}


	/**
	 * Check if two events occurred on the same day.
	 * @param calendar The calendar that is being compared
	 *                 to the requested field.
	 * @param field The field that the passed calendar
	 *              will be compared to.
	 * @return Returns true if the passed event occurred on the same day
	 * as the requested field.
	 * @throws NullPointerException thrown if the requested field is currently
	 *              null.
	 */
	boolean isSameDay(Calendar calendar, int field) throws NullPointerException {
		if(calendar.get(Calendar.DAY_OF_YEAR) ==
				punches[field].get(Calendar.DAY_OF_YEAR)) {
			if(calendar.get(Calendar.YEAR) ==
					punches[field].get(Calendar.YEAR)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Get the string value of a given field, either punches[IN], punches[OUT],
	 * or the DATE.
	 *
	 * @param field The index of the field requested, either IN, OUT, or DATE
	 * @return The formatted String, representing the value requested by "field"
	 * parameter. If field calls for a value that does not exist, the returned
	 * String is empty.
	 */
	private String getStringField(int field) {
		try {
			if (punches[field] == null) {
				return "";
			} else {
				return String.format(Locale.US, "%1$tl:%1$tM%1$tp", punches[field]);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			if(punches[IN] != null) {
				return String.format(Locale.US, "%1$tm/%1$te/%1$ty", punches[IN]);
			} else {
				return String.format(Locale.US, "%1$tm/%1$te/%1$ty", punches[OUT]);
			}
		}
	}


	/**
	 * Make sure the given field refers to either an IN or an OUT.
	 * @param field The field in question.
	 * @return True: when the field entered is within bounds of punches array.
	 *         False: when the field entered is outside the bounds of punches array.
	 */
	private boolean fieldIsIn_OUT(int field) {
		try {
			return (0 <= field && field <= MAX_INDEX);
		} catch (Exception e) {
			return false;
		}
	}


	Calendar get(int field) throws NullPointerException {
		return punches[field];
	}


	public static final int IN = 0;
	public static final int OUT = 1;
	public static final int DATE = -1;

	private static final int MAX_INDEX = 1;
	private Calendar[] punches; //array should always be initialized to 2 items
	private ViewGroup tableRowView;
}
