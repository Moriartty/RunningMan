package com.moriarty.user.runningman.Dialog;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import com.moriarty.user.runningman.R;

/**
 * Created by user on 17-8-17.
 */
public class MyDatePickerDialog extends DatePickerDialog {
    public MyDatePickerDialog (Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
        this.setTitle(context.getResources().getString(R.string.datePickerTitle));
        this.setButton2(context.getResources().getString(R.string.cancel), (OnClickListener)null);
        this.setButton(context.getResources().getString(R.string.ok), this);  //setButton和this参数组合表示这个按钮是确定按钮
    }
    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
        this.setTitle(view.getResources().getString(R.string.datePickerTitle));
    }

}


