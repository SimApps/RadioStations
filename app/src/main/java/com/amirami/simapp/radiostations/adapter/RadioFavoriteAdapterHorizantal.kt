package com.amirami.simapp.radiostations.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.databinding.RadioHorizontalrecyclervTiketBinding
import com.amirami.simapp.radiostations.model.RadioRoom
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import java.util.ArrayList




class RadioFavoriteAdapterHorizantal(private val listener: OnItemClickListener) :
    ListAdapter<MutableList<RadioRoom>, RadioFavoriteAdapterHorizantal.FavViewHolder>(DiffCallback()), FastScrollRecyclerView.SectionedAdapter {
    //class RadioFavoriteAdapter (private val listener: OnItemClickListener): RecyclerView.Adapter<RadioFavoriteAdapter.FavViewHolder>(), FastScrollRecyclerView.SectionedAdapter {


    private val items = ArrayList<RadioRoom>()
    private lateinit var ProductShopingRoom: RadioRoom

    fun setItems(items: MutableList<RadioRoom>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val binding = RadioHorizontalrecyclervTiketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {

        ProductShopingRoom = items[position]//radiodiffer[position]


        holder.bind(ProductShopingRoom)
    }

    override fun getItemCount(): Int =items.size// differ.currentList.size     //fidCardDBList.size


    inner class FavViewHolder(private val binding: RadioHorizontalrecyclervTiketBinding) :
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

        fun bind(radioRoom: RadioRoom) {

            binding.apply {
              /*  CountriesTxV.setTextColor(RadioFunction.parseColor("#FF546d79"))
                NbrradioStationTxV.setTextColor(RadioFunction.parseColor("#FF546d79"))
*/
                RadioFunction.maintextviewColor(mainTxV, MainActivity.darkTheme)
                RadioFunction.secondarytextviewColor(descriptionTxV, MainActivity.darkTheme)

                mainTxV.text=radioRoom.name
                descriptionTxV.text=radioRoom.country  //root.context.getString(R.string.stationinfo,radioRoom.bitrate,radioRoom.country,radioRoom.language)
                descriptionTxV.visibility = View.GONE
                RadioFunction.loadImageString( root.context,radioRoom.favicon,
                    MainActivity.imagedefaulterrorurl,imageViewRv)

            }
        }
    }

    interface OnItemClickListener {

        fun onItemFavClick(radioRoom: RadioRoom)
        fun onMoreItemFavClick(radio: RadioRoom)
    }




    class DiffCallback : DiffUtil.ItemCallback<MutableList<RadioRoom>>() {
        override fun areItemsTheSame(oldItem: MutableList<RadioRoom>, newItem: MutableList<RadioRoom>) =
            oldItem[0].radiouid == newItem[0].radiouid

        override fun areContentsTheSame(oldItem: MutableList<RadioRoom>, newItem: MutableList<RadioRoom>) =
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