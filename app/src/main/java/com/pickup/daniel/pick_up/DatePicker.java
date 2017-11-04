package com.pickup.daniel.pick_up;

import android.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class DatePicker extends FragmentActivity{
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        //newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}
