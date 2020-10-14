package com.example.scookie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_calendar.*

class CalendarActivity : AppCompatActivity() {

    lateinit var adapter: CalendarRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        var dataList = mutableListOf<String>("hi", "helo", "ed")
        adapter = CalendarRVAdapter(this, dataList)

        LinearSnapHelper().attachToRecyclerView(rv_frag_home_artic_pick)
        act_calendar_rv_calendarCard.adapter = adapter
        act_calendar_rv_calendarCard.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
    }
}