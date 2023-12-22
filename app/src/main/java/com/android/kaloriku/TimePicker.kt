package com.android.kaloriku

import android.text.format.DateFormat
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class TimePicker: DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hourOFDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return TimePickerDialog(requireActivity(), activity as OnTimeSetListener,hourOFDay,minute,DateFormat.is24HourFormat(activity))
    }
}