package com.moriarty.user.runningman.Fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import com.moriarty.user.runningman.Dialog.MyTimePickerDialog;

import java.util.Calendar;

/**
 * Created by user on 17-9-13.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
       /* int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);*/
        return new MyTimePickerDialog(getActivity(), this, hour, minute,true);
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}
