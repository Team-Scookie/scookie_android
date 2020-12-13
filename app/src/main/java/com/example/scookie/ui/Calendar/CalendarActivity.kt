package com.example.scookie.ui.Calendar

import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.scookie.R
import com.example.scookie.databinding.ActivityCalendarBinding
import com.example.scookie.databinding.CalendarDateLayoutBinding
import com.example.scookie.ui.adapter.CalendarRVAdapter
import com.example.scookie.ui.adapter.deco.HorizontalSpaceItemDecoration
import com.example.scookie.util.dpToPx
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.yearMonth
import kotlinx.android.synthetic.main.calendar_day_string.*
import org.koin.android.ext.android.bind
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

/**
 * @author soomin
 * using View Binding
 */
class CalendarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarBinding
    private lateinit var rvAdapter: CalendarRVAdapter

    // @TODO 연습용
    private var selectedDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater) // for using ViewBinding
        setContentView(binding.root) // for using ViewBinding

        /**
         * @feature RecyclerView
         */
        // recyclerview 데이터 설정
        var dataList = mutableListOf<String>("1", "2", "3", "4", "5", "6", "7") // @TODO 데이터 바꿔야 함
        rvAdapter = CalendarRVAdapter(this, dataList)
        binding.actCalendarRvCalendarCard.adapter = rvAdapter
        binding.actCalendarRvCalendarCard.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        // recyclerview를 스와이프할 때 조금만 해도 자동으로 가운데로 오도록
        var linearSnapHelper = LinearSnapHelper()
        linearSnapHelper.attachToRecyclerView(binding.actCalendarRvCalendarCard)

        // recyclerview Space 조절
        var horizontalSpaceItemDecoration = HorizontalSpaceItemDecoration(this, 30.dpToPx(), 30.dpToPx()) // 코드 상에서 view 조절을 할 때는 Pixel로 변환해서 작업해야 한다.
        binding.actCalendarRvCalendarCard.addItemDecoration(horizontalSpaceItemDecoration)

        // @TODO Recyclerview의 마지막에 도달했을 경우, 그 다음주 월요일로 날짜를 옮긴다
        binding.actCalendarRvCalendarCard.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var lastItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                var itemTotalCount = recyclerView.adapter!!.itemCount - 1

                if (lastItemPosition == itemTotalCount) {
                    // 마지막에 도달했을 경우 이 if문 안으로 들어온다.
                    // @TODO 위와 동일
                }
            }
        })

        // @TODO 이 부분은 왜 있는건지,, 사실 잘 모르겠음
        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
//            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        /**
         * @feature CalendarView
         */
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(11)
        val lastMonth = currentMonth.plusMonths(11)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

        binding.actCalendarCalendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        binding.actCalendarCalendarView.scrollToDate(LocalDate.now()) // 캘린더뷰를 켰을 때 현재 날짜가 보인다. @TODO 왜 현재 날짜로 설정이 안되지?ㅜ

        // week mode로 설정(주별로 보여주기 위해)
        binding.actCalendarCalendarView.updateMonthConfiguration(inDateStyle = InDateStyle.ALL_MONTHS, maxRowCount = 1, hasBoundaries = false)

        // CalendarView의 dayBinder 설정
        class DayViewContainer(view: View) : ViewContainer(view) {
            val bind = CalendarDateLayoutBinding.bind(view)
            lateinit var day: CalendarDay

            init {
                view.setOnClickListener {
                    Toast.makeText(applicationContext, day.date.toString(), Toast.LENGTH_SHORT).show()

                    // RecyclerView가 해당하는 요일 순서에 맞는 곳으로 간다. (자연스럽게 이동 -> smoothScroll 사용)
                    when (day.date.dayOfWeek.toString()) {
                        "SUNDAY" -> binding.actCalendarRvCalendarCard.smoothScrollToPosition(0)
                        "MONDAY" -> binding.actCalendarRvCalendarCard.smoothScrollToPosition(1)
                        "TUESDAY" -> binding.actCalendarRvCalendarCard.smoothScrollToPosition(2)
                        "WEDNESDAY" -> binding.actCalendarRvCalendarCard.smoothScrollToPosition(3)
                        "THURSDAY" -> binding.actCalendarRvCalendarCard.smoothScrollToPosition(4)
                        "FRIDAY" -> binding.actCalendarRvCalendarCard.smoothScrollToPosition(5)
                        "SATURDAY" -> binding.actCalendarRvCalendarCard.smoothScrollToPosition(6)
                    }

                    if (selectedDate != day.date) {
                        val oldDate = selectedDate
                        selectedDate = day.date

                        binding.actCalendarCalendarView.notifyDateChanged(day.date)
                        oldDate?.let { binding.actCalendarCalendarView.notifyDateChanged(it) }
                    }
                }
            }

            fun bind(day: CalendarDay) {
                this.day = day

                bind.calendarDateText.text = day.date.dayOfMonth.toString()

                if (day.date == selectedDate) {
                    bind.calendarDateText.setTextColor(Color.WHITE)
                    bind.calendarDateCircleBackground.visibility = View.VISIBLE
                }
                else {
                    bind.calendarDateText.setTextColor(Color.BLACK)
                    bind.calendarDateCircleBackground.visibility = View.GONE
                }
            }
        }

        binding.actCalendarCalendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) = container.bind(day)
        }

        // 캘린더뷰를 스크롤하면 월과 년도를 나타내는 부분도 바뀌어야 한다.
        binding.actCalendarCalendarView.monthScrollListener = {
            val firstDate = it.weekDays.first().first().date
            val lastDate = it.weekDays.last().last().date

            // 현재 나타나는 날짜가 모두 같은 월, 년도이면
            if (firstDate.yearMonth == lastDate.yearMonth) {
                current_month_tv.text = firstDate.monthValue.toString()
                current_year_tv.text = firstDate.yearMonth.year.toString()
            }
            else { // 다른 월, 년도이면
                current_month_tv.text = "${firstDate.monthValue.toString()} - ${lastDate.monthValue.toString()}"
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
