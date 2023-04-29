package com.amirami.simapp.radiostations.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.databinding.RadioHorizontalrecyclervTiketBinding
import com.amirami.simapp.radiostations.model.RadioEntity
import com.amirami.simapp.radiostations.utils.Constatnts
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import java.util.ArrayList

@UnstableApi class RadioFavoriteAdapterHorizantal(private val listener: OnItemClickListener) :
    ListAdapter<MutableList<RadioEntity>, RadioFavoriteAdapterHorizantal.FavViewHolder>(DiffCallback()), FastScrollRecyclerView.SectionedAdapter {
    // class RadioFavoriteAdapter (private val listener: OnItemClickListener): RecyclerView.Adapter<RadioFavoriteAdapter.FavViewHolder>(), FastScrollRecyclerView.SectionedAdapter {

    private val items = ArrayList<RadioEntity>()
    private lateinit var ProductShopingRoom: RadioEntity

    fun setItems(items: List<RadioEntity>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val binding = RadioHorizontalrecyclervTiketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        ProductShopingRoom = items[position] // radiodiffer[position]

        holder.bind(ProductShopingRoom)
    }

    override fun getItemCount(): Int = items.size // differ.currentList.size     //fidCardDBList.size

    @UnstableApi inner class FavViewHolder(private val binding: RadioHorizontalrecyclervTiketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                cardViewMainRadio.setSafeOnClickListener {
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        listener.onItemFavClick(items[bindingAdapterPosition])
                    }
                }
            }
        }

        fun bind(radioRoom: RadioEntity) {
            binding.apply {
              /*  CountriesTxV.setTextColor(RadioFunction.parseColor("#FF546d79"))
                NbrradioStationTxV.setTextColor(RadioFunction.parseColor("#FF546d79"))
*/


                mainTxV.text = radioRoom.name
                descriptionTxV.text = root.context.getString(
                    R.string.stationinfo,
                    if (radioRoom.bitrate != "")radioRoom.bitrate + if (radioRoom.language != "" || radioRoom.language != "")" kbps, " else if (radioRoom.language != "") " kbps, " else " kbps " else "",
                    if (radioRoom.country != "")radioRoom.country + if (radioRoom.language != "")", " else "" else "",
                    if (radioRoom.language != "")radioRoom.language else ""
                )

                // root.context.getString(R.string.stationinfo,radioRoom.bitrate,radioRoom.country,radioRoom.language)
                descriptionTxV.visibility = View.GONE
                RadioFunction.loadImageString(
                    root.context,
                    radioRoom.favicon,
                    MainActivity.imagedefaulterrorurl,
                    imageViewRv,
                    Constatnts.CORNER_RADIUS_8F
                )
            }
        }
    }

    interface OnItemClickListener {

        fun onItemFavClick(radioRoom: RadioEntity)
        fun onMoreItemFavClick(radio: RadioEntity)
    }

    class DiffCallback : DiffUtil.ItemCallback<MutableList<RadioEntity>>() {
        override fun areItemsTheSame(oldItem: MutableList<RadioEntity>, newItem: MutableList<RadioEntity>) =
            oldItem[0].stationuuid == newItem[0].stationuuid

        override fun areContentsTheSame(oldItem: MutableList<RadioEntity>, newItem: MutableList<RadioEntity>) =
            oldItem == newItem
    }

    /*
   private val diffCallback = object : DiffUtil.ItemCallback<RadioRoom>() {
       override fun areItemsTheSame(oldItem: RadioRoom, newItem: RadioRoom): Boolean {
           return oldItem.radiouid == newItem.radiouid
       }

       override fun areContentsTheSame(oldItem: RadioRoom, newItem: RadioRoom): Boolean {
           return newItem == oldItem
       }
   }

   private val differ = AsyncListDiffer(this, diffCallback)
   var radiodiffer: List<RadioRoom>
       get() = differ.currentList
       set(value) {
           differ.submitList(value)
       }

    */

    override fun getSectionName(position: Int): String {
        var strTextview = items[position].name
        strTextview = strTextview.substring(0, 1)
        return strTextview
    }
}
