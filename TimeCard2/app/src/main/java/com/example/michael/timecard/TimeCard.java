package com.example.michael.timecard;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Michael on 7/6/17.
 */

class TimeCard {
	TimeCard(Context context, ViewGroup parentScrollView,
	         Button buttonIn, Button buttonOut) throws RuntimeException {
		try {
			//initialize variables
			days = new ArrayList<>(0);
			indexDaysNeedToBeWritten = new ArrayList<>(0);

			layoutInflater = LayoutInflater.from(context);
			scrollingTableView = parentScrollView;
		} catch (Exception e) {
			throw new RuntimeException();
		}

		//initialize data input stream
		try {
			inputStream = new DataInputStream(new BufferedInputStream(
					context.openFileInput(filename)));
			readValuesFromFile();
		} catch (FileNotFoundException e) {
			Log.d("TimeCard", "No data read from input file");
		} finally {
			try {
				indexOfMostRecentDay = days.size()-1;
				if(inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				Log.d("TimeCard", "InputStream could not be closed");
			} catch (NullPointerException e) {
				Log.d("TimeCard", "Null pointer? Maybe ArrayList<Day>" +
						" was never assigned any values?");
			}
		}

		setButton(buttonIn, Day.IN);
		setButton(buttonOut, Day.OUT);
	}


	private void setButton(Button button, int IN_OUT) {
		if(IN_OUT == Day.IN) {
			button.setText(R.string.in);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					try {
						Calendar c = Calendar.getInstance(Locale.US);

//						if(enoughTimeHasPassedSinceLastPunch(c)) {
							addDay(c, Day.IN);
							Snackbar.make(view, "Punch accepted!", Snackbar.LENGTH_LONG)
									.setAction("Action", null).show();
//						} else {
//							Snackbar.make(view, "Punch not accepted: you punched in recently!",
//									Snackbar.LENGTH_LONG).setAction("Action", null).show();
//						}
					} catch (Exception e) {
						Snackbar.make(view, "Punch could not be accepted now...", Snackbar.LENGTH_LONG)
								.setAction("Action", null).show();
						Log.d("TimeCard", "Error assigning PUNCH IN button");
					}
				}
			});
		}

		else {
			button.setText(R.string.out);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					try {
						Calendar currentCal = Calendar.getInstance(Locale.US);
						if (days.size() <= 0) {
							addDay(currentCal, Day.OUT);
						} else {
							indexOfMostRecentDay = days.size() - 1;
							Day mostRecentDay = days.get(indexOfMostRecentDay);
							try {
								//try to add to same day
								if (mostRecentDay.isSameDay(currentCal, Day.IN)) {
									mostRecentDay.addPunch(currentCal, Day.OUT);
								} else {
									addDay(currentCal, Day.OUT);
								}
							} catch (NullPointerException | IllegalArgumentException e) {
								addDay(currentCal, Day.OUT);
							}
						}
						Snackbar.make(view, "Punch accepted!", Snackbar.LENGTH_LONG)
								.setAction("Action", null).show();
					} catch (Exception e) {
						Snackbar.make(view, "Punch could not be accepted now...", Snackbar.LENGTH_LONG)
								.setAction("Action", null).show();
						Log.d("TimeCard", "Error assigning PUNCH OUT button");
					}
				}
			});
		}
	}


	private boolean enoughTimeHasPassedSinceLastPunch(Calendar c) {
		if(days.get(days.size()-1).get(Day.OUT) == null) {
			return((c.getTimeInMillis() - days.get(days.size() - 1)
					.get(Day.IN).getTimeInMillis()) > MIN_PUNCH_WAIT);
		} else {
			return((c.getTimeInMillis() - days.get(days.size() - 1)
					.get(Day.OUT).getTimeInMillis()) > MIN_PUNCH_WAIT);
		}
	}


	/**
	 * This is a unified method to add a new Day object to ArrayList days. This method
	 * ensures that certain standards are met each time an item is added, i.e.
	 * making sure that the new Day object is added to the ArrayList, that the view
	 * of the new row of data is added properly (and added only once), and that
	 * indexOfMostRecentDay is incremented properly.
	 *
	 * @param c The calendar object representing the current time, used to
	 *          set a value for the new Day object.
	 * @param IN_OUT The field in the new Day object that will be filled
	 *               by the given Calendar object "c".
	 */
	private void addDay(Calendar c, int IN_OUT) {
		try {
			days.add(new Day(c, layoutInflater, IN_OUT));
			indexOfMostRecentDay = days.size()-1;
			scrollingTableView.addView(days.get(
					indexOfMostRecentDay).getTableRowView(), 1);

			assignIndexDaysNeedToBeWritten(days.size()-1);

		} catch (ArrayIndexOutOfBoundsException e) {
			Log.d("TimeCard", "Array index out of bounds. Make sure any methods calling" +
					" TimeCard.addDay(Calendar, LayoutInflater, int are sending" +
					" integers that are either 0 or 1. Use Day.IN and Day.OUT" +
					" to be sure");
		}
		//catch some other kind of exception --v
		/*
		Just hear me out on this. This should be some kind of logic that compares the most recent
		punch of the OPPOSITE type of the new Punch (IN->OUT, OUT->IN). If it's been less than
		5 minutes since the most recent punch, don't accept a new punch.
				- This should still be circumventable by adding a punch time manually, rather
				than using one of the "Punch" buttons.

				- If a new Calendar occurs before the most recent punch,
				set needToAppendOnly to false.
		 */
	}


	private int getIndexMostRecentDay() {
		return days.size()-1;
	}

	private void assignIndexDaysNeedToBeWritten(int x) {
		if(x < indexLastWrittenDay) {
			needToAppendOnly = false;
		} else {
			indexDaysNeedToBeWritten.add(x);
		}
	}


	/**
	 * A callable means of reading values from a given DataInputStream.
	 * This has been moved from other areas to improve readability.
	 */
	private void readValuesFromFile() {
		try {
			indexLastWrittenDay = 0;
			while (inputStream.available() > 0) {
				days.add(new Day((DataInputStream) inputStream, layoutInflater));
				scrollingTableView.addView(days.get
						(indexLastWrittenDay).getTableRowView(), 1);
				++indexLastWrittenDay;
			}
		} catch (IOException e) {
			Log.d("TimeCard", "Exception thrown reading from file");
		} finally  {
			indexLastWrittenDay = days.size() - 1;
		}
	}


	/**
	 * Check if two events occurred on the same day, by comparing the
	 * values of the two events: the day of the year, and the year.
	 *
	 * @param cal1 The calendar that is being compared
	 *                 to the requested field.
	 * @param cal2 The field that the passed calendar
	 *              will be compared to.
	 * @return Returns true if the passed event occurred on the same day
	 * as the requested field. Returns false if the events did not
	 * occur on the same day, or if one of the events is null.
	 */
	private boolean sameDay(Calendar cal1, Calendar cal2) {
		try {
			if (cal1.get(Calendar.DAY_OF_YEAR) ==
					cal2.get(Calendar.DAY_OF_YEAR)) {
				if (cal1.get(Calendar.YEAR) ==
						cal2.get(Calendar.YEAR)) {
					return true;
				}
			}
		}
		catch (NullPointerException e) {
			return false;
		}
		return false;
	}


	/*
	 * Create a method for updating an existing Punch. If Punch being updated is not
	 * the most recent one, set needToAppendOnly to false.
	 */



	/**
	 * Write data to a file with the default filename.
	 *
	 * @param context The Context of the program at the moment when this method is called.
	 * @throws Exception Exception is thrown if data cannot be written for any
	 * reason. This signifies that data, especially newly entered data,
	 * may be lost if program is terminated.
	 */
	void saveData(Context context) throws IOException {
		//try to prepare output stream, APPEND or PRIVATE mode, based on needToAppendOnly
		try {
			if(needToAppendOnly) {
				outputStream = new DataOutputStream(new BufferedOutputStream(
						context.openFileOutput(filename, Context.MODE_APPEND)));

				for (int i = 0; i < indexDaysNeedToBeWritten.size(); ++i) {
					days.get(indexDaysNeedToBeWritten.get(i))
							.writeDayToFile((DataOutputStream) outputStream);
				}
			} else {
				outputStream = new DataOutputStream(new BufferedOutputStream(
						context.openFileOutput(filename, Context.MODE_PRIVATE)));

				for (int i = 0; i < days.size(); ++i) {
					days.get(indexDaysNeedToBeWritten.get(i))
							.writeDayToFile((DataOutputStream) outputStream);
				}
			}
		} catch (IOException e) {
			Log.d("TimeCard", "Data could not be written to file");
		} finally {
			if(outputStream != null) {
				indexDaysNeedToBeWritten.clear();
				outputStream.flush();
				outputStream.close();
			}
		}
	}

	private ArrayList<Day> days;
	private ArrayList<Integer> indexDaysNeedToBeWritten;

	private ViewGroup scrollingTableView;
	private LayoutInflater layoutInflater;
	private InputStream inputStream;
	private OutputStream outputStream;

	private final String filename = "Time Punch History4";
	private boolean needToAppendOnly = true;
	private int indexLastWrittenDay = 0;
	private int indexOfMostRecentDay = 0;
	private int MIN_PUNCH_WAIT = 6000;
}




