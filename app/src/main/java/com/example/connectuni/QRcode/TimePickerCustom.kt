package com.example.connectuni.QRcode

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.TimePicker

class TimePickerCustom : TimePicker {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    override fun setHour(hour: Int) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> super.setHour(hour)
            else -> super.setCurrentHour(hour)
        }
    }

    override fun setMinute(minute: Int) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> super.setMinute(minute)
            else -> super.setCurrentMinute(minute)
        }
    }
}