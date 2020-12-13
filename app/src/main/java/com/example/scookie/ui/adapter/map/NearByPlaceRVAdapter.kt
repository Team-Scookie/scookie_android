package com.example.scookie.ui.adapter.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.scookie.R
import com.example.scookie.model.NearByPlaceData

class NearByPlaceRVAdapter(val ctx: FragmentActivity, var dataList: MutableList<NearByPlaceData>) : RecyclerView.Adapter<NearByPlaceRVAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View= LayoutInflater.from(ctx).inflate(R.layout.rv_item_near_by_place, parent, false)

        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size;

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.category.text = dataList[position].category
        holder.title.text = dataList[position].title
        holder.address.text = dataList[position].address
    }

    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var category = itemView.findViewById(R.id.near_by_category_tv) as TextView
        var title = itemView.findViewById(R.id.near_by_title_tv) as TextView
        var address = itemView.findViewById(R.id.near_by_address_tv) as TextView
    }
}