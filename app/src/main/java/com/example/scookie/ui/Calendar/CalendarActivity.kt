package com.example.scookie.ui.Calendar

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import kotlinx.android.synthetic.main.calendar_date_layout.view.*
import kotlinx.android.synthetic.main.calendar_day_string.*
import java.security.AccessController.getContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*


class CalendarActivity : AppCompatActivity() {

    lateinit var adapter: CalendarRVAdapter

    private var selectedDate = LocalDate.now() // 선택한 날

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

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

        /**
         * Recyclerview의마지막에 위치했는지 체크하는 방법
         * @TODO
         */
        act_calendar_rv_calendarCard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var lastItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                var itemTotalCount = recyclerView.adapter!!.itemCount - 1
                /**
                 * RecyclerView의 가장 마지막 카드에 도달했다
                 * @TODO -> 달력을 다음으로 넘기는거,,? (일단 보류)
                 */
                if (lastItemPosition == itemTotalCount) {
                    Toast.makeText(applicationContext, "Last Position", Toast.LENGTH_SHORT).show()
                }
            }
        })

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
//        act_calendar_calendarView.setup(currentMonth, lastMonth, firstDayOfWeek)
        act_calendar_calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        act_calendar_calendarView.scrollToDate(LocalDate.now())

        Toast.makeText(this, "current : " + LocalDate.now().toString(), Toast.LENGTH_LONG).show()

        // week mode로 설정 (한 줄)
        act_calendar_calendarView.updateMonthConfiguration(
            inDateStyle = InDateStyle.ALL_MONTHS,
            maxRowCount = 1,
            hasBoundaries = false
        )

        // 날짜를 눌렀을 때
//        var selectedDate/

        act_calendar_calendarView.dayBinder = object : DayBinder<CalendarDayViewContainer> {
            override fun create(view: View) = CalendarDayViewContainer(view)

            override fun bind(container: CalendarDayViewContainer, day: CalendarDay) {
                container.calendarDateTxt.text = day.date.dayOfMonth.toString()

                container.view.setOnClickListener {
                    if (selectedDate != day.date) {
                        val oldDate = selectedDate
                        selectedDate = day.date

                        Toast.makeText(applicationContext, "selected : " + selectedDate.toString(), Toast.LENGTH_LONG).show()
                        act_calendar_calendarView.notifyDateChanged(day.date)
                        oldDate?.let { act_calendar_calendarView.notifyDateChanged(it) } // 이건 뭐지,,
                    }
                    /** 이 부분을 따로 빼는게?
                    Toast.makeText(it.context, day.date.dayOfWeek.toString(), Toast.LENGTH_SHORT).show()

                    if (it.calendarDateCircleBackground.visibility == View.VISIBLE)
                        it.calendarDateCircleBackground.visibility = View.INVISIBLE
                    else
                        it.calendarDateCircleBackground.visibility = View.VISIBLE
                    if (it.calendarDateText.currentTextColor == Color.parseColor("#373636"))
                        it.calendarDateText.setTextColor(Color.WHITE)
                    else
                        it.calendarDateText.setTextColor(Color.parseColor("#373636"))
                    */
//            if (day.owner == DayOwner.THIS_MONTH) {
//                Toast.makeText(view.context, "clickclick", Toast.LENGTH_SHORT).show()
//                        if (selectedDates.contains(day.date)) {
//                            selectedDates.remove(day.date)
//                        } else {
//                            selectedDates.add(day.date)
//                        }
//                        binding.exOneCalendar.notifyDayChanged(day)


//            }

                    /**
                     * calendarView를 누르면 Recyclerview가 해당하는 요일 순서에 맞는 곳으로 간다. (smooth하게)
                     */
                    when (day.date.dayOfWeek.toString()) {
                        "SUNDAY" -> {
                            act_calendar_rv_calendarCard.smoothScrollToPosition(0)
                        }
                        "MONDAY" -> {
                            act_calendar_rv_calendarCard.smoothScrollToPosition(1)
                        }
                        "TUESDAY" -> {
                            act_calendar_rv_calendarCard.smoothScrollToPosition(2)
                        }
                        "WEDNESDAY" -> {
                            act_calendar_rv_calendarCard.smoothScrollToPosition(3)
                        }
                        "THURSDAY" -> {
                            act_calendar_rv_calendarCard.smoothScrollToPosition(4)
                        }
                        "FRIDAY" -> {
                            act_calendar_rv_calendarCard.smoothScrollToPosition(5)
                        }
                        "SATURDAY" -> {
                            act_calendar_rv_calendarCard.smoothScrollToPosition(6)
                        }
                        else -> {

                        }
                    }
                }
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

                current_year_tv.text = firstDate.yearMonth.year.toString()
            } else {
                current_month_tv.text =
                    "${firstDate.monthValue.toString()} - ${lastDate.monthValue.toString()}"
                if (firstDate.year == lastDate.year) {
//                    binding.exOneYearText.text = firstDate.yearMonth.year.toString()
                    current_year_tv.text = firstDate.yearMonth.year.toString()
                } else {
//                    binding.exOneYearText.text = "${firstDate.yearMonth.year} - ${lastDate.yearMonth.year}"
                    current_year_tv.text = "${firstDate.yearMonth.year} - ${lastDate.yearMonth.year}"
                }
            }
        }
    }
}