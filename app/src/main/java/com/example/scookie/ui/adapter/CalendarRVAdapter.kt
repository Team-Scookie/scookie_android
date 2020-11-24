package com.example.scookie.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.scookie.R

class CalendarRVAdapter(val ctx: FragmentActivity, var dataList: MutableList<String>) : RecyclerView.Adapter<CalendarRVAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View= LayoutInflater.from(ctx).inflate(R.layout.rv_item_calendar_card, parent, false)

        // 부모 대비 비율 정하는 방법은 artic의 BigImageArticleAdapter 참고

        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size;

    override fun onBindViewHolder(holder: Holder, position: Int) {
//        Toast.makeText(ctx, "ㅗㅑ", Toast.LENGTH_SHORT).show()
    }


    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }
}