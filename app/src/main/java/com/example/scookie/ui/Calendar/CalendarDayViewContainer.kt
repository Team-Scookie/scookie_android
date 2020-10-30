package com.example.scookie.ui.Calendar

import android.view.View
import android.widget.TextView
import com.example.scookie.R
import com.kizitonwose.calendarview.ui.ViewContainer

class CalendarDayViewContainer(view: View) : ViewContainer(view) {
    val calendarDayTxt = view.findViewById<TextView>(R.id.calendarDayText)
}