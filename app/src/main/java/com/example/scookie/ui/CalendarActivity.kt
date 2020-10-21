package com.example.scookie.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.scookie.ui.adapter.CalendarRVAdapter
import com.example.scookie.R
import com.example.scookie.ui.adapter.deco.HorizontalSpaceItemDecoration
import com.example.scookie.util.dpToPx
import kotlinx.android.synthetic.main.activity_calendar.*

class CalendarActivity : AppCompatActivity() {

    lateinit var adapter: CalendarRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        var dataList = mutableListOf<String>("hi", "helo", "ed", "ㅇㅇ")
        adapter =
            CalendarRVAdapter(this, dataList)

        LinearSnapHelper().attachToRecyclerView(act_calendar_rv_calendarCard) // RecyclerView SnapHelper (조금만 돌려도 자동으로 가운데로 오도록)
        act_calendar_rv_calendarCard.adapter = adapter
        act_calendar_rv_calendarCard.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        // RecyclerView Space 조절
        val horizontalSpaceItemDecoration = HorizontalSpaceItemDecoration(this, 30.dpToPx(), 30.dpToPx()) // 코드 상에서 view 조절할 때는 Pixel로 변환해서 작업해야 한다.
        act_calendar_rv_calendarCard.addItemDecoration(horizontalSpaceItemDecoration)
    }
}