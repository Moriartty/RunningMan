package com.moriarty.user.runningman.Dialog;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.moriarty.user.runningman.R;

/**
 * Created by user on 17-9-13.
 */
public class MyTimePickerDialog extends TimePickerDialog {

    public MyTimePickerDialog(Context context, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, listener, hourOfDay, minute, is24HourView);
        this.setTitle(context.getResources().getString(R.string.timePickerTitle));
        this.setButton2(context.getResources().getString(R.string.cancel), (OnClickListener)null);
        this.setButton(context.getResources().getString(R.string.ok), this);  //setButton和this参数组合表示这个按钮是确定按钮
    }
    @Override
    public void onTimeChanged(TimePicker view, int hour, int minute) {
        super.onTimeChanged(view, hour, minute);
        this.setTitle(view.getResources().getString(R.string.timePickerTitle));
    }

}
