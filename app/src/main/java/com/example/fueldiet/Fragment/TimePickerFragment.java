package com.example.fueldiet.Fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * Custom Dialog for Time Picker
 */
public class TimePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle setDate = this.getArguments();
        Long customDate = setDate.getLong("date");

        Calendar calendar = Calendar.getInstance();
        if (customDate != null)
            calendar.setTimeInMillis(customDate);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), hour, min, DateFormat.is24HourFormat(getActivity()));
    }
}
