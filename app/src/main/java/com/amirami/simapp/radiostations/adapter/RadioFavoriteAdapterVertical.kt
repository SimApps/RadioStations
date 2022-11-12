package com.amirami.simapp.radiostations.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.* // ktlint-disable no-wildcard-imports
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.databinding.RadioTiketMainBinding
import com.amirami.simapp.radiostations.model.RadioRoom
import com.amirami.simapp.radiostations.utils.Constatnts
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import java.util.ArrayList

class RadioFavoriteAdapterVertical(private val listener: OnItemClickListener) :
    ListAdapter<MutableList<RadioRoom>, RadioFavoriteAdapterVertical.FavViewHolder>(DiffCallback()),
    FastScrollRecyclerView.SectionedAdapter {
    // class RadioFavoriteAdapter (private val listener: OnItemClickListener): RecyclerView.Adapter<RadioFavoriteAdapter.FavViewHolder>(), FastScrollRecyclerView.SectionedAdapter {

    private val items = ArrayList<RadioRoom>()
    private lateinit var productShopingRoom: RadioRoom

    fun setItems(items: MutableList<RadioRoom>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val binding =
            RadioTiketMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        productShopingRoom = items[position] // radiodiffer[position]

        holder.bind(productShopingRoom)
    }

    override fun getItemCount(): Int = items.size // differ.currentList.size     //fidCardDBList.size

    inner class FavViewHolder(private val binding: RadioTiketMainBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                layoutABorrar.setSafeOnClickListener {
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        listener.onItemClick(items[bindingAdapterPosition])
                    }
                }

                ExpandImageView.setSafeOnClickListener {
                    listener.onMoreItemClick(items[bindingAdapterPosition])
                }
            }
        }

        fun bind(radioRoom: RadioRoom) {
            binding.apply {
                RadioFunction.maintextviewColor(mainTxVw, MainActivity.darkTheme)
                RadioFunction.secondarytextviewColor(descriptionTxVw, MainActivity.darkTheme)
                mainTxVw.text = radioRoom.name
                descriptionTxVw.text = root.context.getString(
                    R.string.stationinfo,
                    if (radioRoom.bitrate != "")radioRoom.bitrate + if (radioRoom.language != "")" kbps, " else " kbps " else "",
                    if (radioRoom.country != "")radioRoom.country + if (radioRoom.language != "")", " else "" else "",
                    if (radioRoom.language != "")radioRoom.language else ""
                )

                RadioFunction.loadImageString(
                    root.context,
                    radioRoom.favicon,
                    MainActivity.imagedefaulterrorurl,
                    ImageView,
                    Constatnts.CORNER_RADIUS_8F
                )
            }
        }
    }

    interface OnItemClickListener {

        fun onItemClick(radioRoom: RadioRoom)
        fun onMoreItemClick(radio: RadioRoom)
    }

    class DiffCallback : DiffUtil.ItemCallback<MutableList<RadioRoom>>() {
        override fun areItemsTheSame(
            oldItem: MutableList<RadioRoom>,
            newItem: MutableList<RadioRoom>
        ) =
            oldItem[0].radiouid == newItem[0].radiouid

        override fun areContentsTheSame(
            oldItem: MutableList<RadioRoom>,
            newItem: MutableList<RadioRoom>
        ) =
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
