package com.example.scookie.ui.Calendar

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.scookie.R
import com.example.scookie.ui.adapter.CalendarRVAdapter
import com.example.scookie.ui.adapter.deco.HorizontalSpaceItemDecoration
import com.example.scookie.util.dpToPx
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.utils.yearMonth
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.calendar_day_string.*
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*


class CalendarActivity : AppCompatActivity() {

    lateinit var adapter: CalendarRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
//            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        /**
         * @author ChoSooMin
         * @feature CalendarView
         */
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        act_calendar_calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        act_calendar_calendarView.scrollToMonth(currentMonth)

        // One row calendar for week mode
        act_calendar_calendarView.updateMonthConfiguration(
            inDateStyle = InDateStyle.ALL_MONTHS,
            maxRowCount = 1,
            hasBoundaries = false
        )

        act_calendar_calendarView.dayBinder = object : DayBinder<CalendarDayViewContainer> {
            override fun create(view: View) = CalendarDayViewContainer(view)

            override fun bind(container: CalendarDayViewContainer, day: CalendarDay) {
                container.calendarDayTxt.text = day.date.dayOfMonth.toString()
            }
        }

        act_calendar_calendarView.monthScrollListener = {
            // In week mode, we show the header a bit differently.
            // We show indices with dates from different months since
            // dates overflow and cells in one index can belong to different
            // months/years.
            val firstDate = it.weekDays.first().first().date
            val lastDate = it.weekDays.last().last().date
            if (firstDate.yearMonth == lastDate.yearMonth) {
                current_month_tv.text = firstDate.monthValue.toString()
            } else {
                current_month_tv.text =
                    "${firstDate.monthValue.toString()} - ${lastDate.monthValue.toString()}"
                if (firstDate.year == lastDate.year) {
//                    binding.exOneYearText.text = firstDate.yearMonth.year.toString()
                } else {
//                    binding.exOneYearText.text = "${firstDate.yearMonth.year} - ${lastDate.yearMonth.year}"
                }
            }
        }

        /**
         * @author ChoSooMin
         * @feature RecyclerView
         */
        var dataList = mutableListOf<String>("1", "2", "3", "4", "5", "6", "7")
        adapter = CalendarRVAdapter(this, dataList)
        act_calendar_rv_calendarCard.adapter = adapter
        act_calendar_rv_calendarCard.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        var linearSnapHelper = LinearSnapHelper()
        linearSnapHelper.attachToRecyclerView(act_calendar_rv_calendarCard) // RecyclerView SnapHelper (조금만 돌려도 자동으로 가운데로 오도록)

        // RecyclerView Space 조절
        val horizontalSpaceItemDecoration = HorizontalSpaceItemDecoration(this, 30.dpToPx(), 30.dpToPx()) // 코드 상에서 view 조절할 때는 Pixel로 변환해서 작업해야 한다.
        act_calendar_rv_calendarCard.addItemDecoration(horizontalSpaceItemDecoration)
    }
}