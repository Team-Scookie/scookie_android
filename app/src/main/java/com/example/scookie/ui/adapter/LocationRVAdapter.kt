package com.example.scookie.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.scookie.R

class LocationRVAdapter(val ctx: FragmentActivity, var dataList: MutableList<String>) : RecyclerView.Adapter<LocationRVAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View= LayoutInflater.from(ctx).inflate(R.layout.rv_item_place_card, parent, false)

        // 부모 대비 비율 정하는 방법은 artic의 BigImageArticleAdapter 참고

        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size;

    override fun onBindViewHolder(holder: Holder, position: Int) {

    }


    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }
}