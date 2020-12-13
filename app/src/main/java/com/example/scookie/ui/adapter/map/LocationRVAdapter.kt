package com.example.scookie.ui.adapter.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.scookie.MapActivity
import com.example.scookie.R
import com.example.scookie.model.PlaceData

class LocationRVAdapter(val ctx: FragmentActivity, var dataList: MutableList<PlaceData>) : RecyclerView.Adapter<LocationRVAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View= LayoutInflater.from(ctx).inflate(R.layout.rv_item_place_card, parent, false)

        // 부모 대비 비율 정하는 방법은 artic의 BigImageArticleAdapter 참고

        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size;

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.date.text = dataList[position].date
        holder.title.text = dataList[position].title
        holder.address.text = dataList[position].address
        holder.minute.text = dataList[position].minute

        holder.nearBy.setOnClickListener {
            (ctx as MapActivity).setDrawer()
        }
// 나중에 이미지 사용시 참
//        Glide.with(ctx)
//            .load(dataList.thumbnail[p1])
//            .into(p0.img)
//        p0.cancleBtn.visibility = View.VISIBLE
//        p0.title.text = dataList.title[p1]
//        p0.description.text = dataList.content[p1]
//
//        if (p1 == 0) {
//            val dp = ctx.resources.displayMetrics.density
//            val rootLayoutParams = p0.root.layoutParams as RelativeLayout.LayoutParams
//            rootLayoutParams.leftMargin = (25 * dp).toInt()
//            p0.root.layoutParams = rootLayoutParams
//        } else if (p1 == dataList.thumbnail.size - 1) {
//            val dp = ctx.resources.displayMetrics.density
//            val rootLayoutParams = p0.root.layoutParams as RelativeLayout.LayoutParams
//            rootLayoutParams.rightMargin = (19 * dp).toInt()
//            p0.root.layoutParams = rootLayoutParams
//        }

//        p0.img.clipToOutline = true
//
//        p0.cancleBtn.setOnClickListener {
//            removeDataList(p1)
//            //지워진 작품 인덱스 값 액티비티로 보내기
//            removeIndexToActivity(p1)
//            notifyDataSetChanged()
//        }
    }

    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var date = itemView.findViewById(R.id.place_date_tv) as TextView
        var title = itemView.findViewById(R.id.place_title_tv) as TextView
        var address = itemView.findViewById(R.id.place_address_tv) as TextView
        var minute = itemView.findViewById(R.id.place_minute_tv) as TextView
        var nearBy = itemView.findViewById(R.id.place_near_by_address_btn) as TextView
    }


//    private fun removeDataList(position: Int) {
//        dataList.thumbnail.removeAt(position)
//        dataList.url.removeAt(position)
//        dataList.title.removeAt(position)
//        dataList.content.removeAt(position)
//    }
//
//    private fun removeIndexToActivity(position: Int) {
//        removeIndexList.add(dataList.workIdx[position])
//        dataList.workIdx.removeAt(position)
//        if(dataList.thumbnail.isEmpty())
//            (ctx as ModifyPortFolioActivity).setRemoveIndexListFromAdapter(removeIndexList,0)
//        else
//            (ctx as ModifyPortFolioActivity).setRemoveIndexListFromAdapter(removeIndexList,1)
//    }
}