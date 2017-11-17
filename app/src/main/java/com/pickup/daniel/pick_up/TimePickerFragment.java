package com.pickup.daniel.pick_up;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Daniel on 11/10/2017.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    // https://stackoverflow.com/questions/24558835/how-can-i-pass-the-date-chosen-in-a-date-picker-to-the-activity-which-contains-t

    // Member variable to hold the reference to the listener
    private TimePickerFragment.TimePickerFragmentListener timePickerListener;

    // Nested interface used to pass the date back to the activity
    public interface TimePickerFragmentListener {
        public void onTimeSet(int hourOfDay, int minute);
    }

    public TimePickerFragment.TimePickerFragmentListener getTimePickerListener() {
        return this.timePickerListener;
    }

    public void setTimePickerListener(TimePickerFragment.TimePickerFragmentListener listener) {
        this.timePickerListener = listener;
    }

    protected void notifyDatePickerListener(int hourOfDay, int minute) {
        if(this.timePickerListener != null) {
            this.timePickerListener.onTimeSet(hourOfDay, minute);
        }
    }

    // Factory method to create new instances of the date picker with the listeners set up correctly
    public static TimePickerFragment newInstance(TimePickerFragment.TimePickerFragmentListener listener) {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setTimePickerListener(listener);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        int style = 2;

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), style,this, hour, minute, true);
                //DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Call the listener to pass the time back to it
        notifyDatePickerListener(hourOfDay, minute);
    }
}
