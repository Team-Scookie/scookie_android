package com.example.scookie.ui.Calendar

import android.graphics.Color
import android.opengl.Visibility
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import com.example.scookie.R
import com.kizitonwose.calendarview.ui.ViewContainer
import de.hdodenhof.circleimageview.CircleImageView

class CalendarDayViewContainer(view: View) : ViewContainer(view) {
    val calendarDateTxt = view.findViewById<TextView>(R.id.calendarDateText)
    val calendarDateCircleBackground = view.findViewById<CircleImageView>(R.id.calendarDateCircleBackground)

    // Will be set when this container is bound. See the dayBinder.
//    lateinit var day: CalendarDay

    init {

    }
}