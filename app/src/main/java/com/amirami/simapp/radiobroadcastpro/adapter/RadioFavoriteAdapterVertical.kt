package com.amirami.simapp.radiobroadcastpro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.* // ktlint-disable no-wildcard-imports
import com.amirami.simapp.radiobroadcastpro.MainActivity
import com.amirami.simapp.radiobroadcastpro.R
import com.amirami.simapp.radiobroadcastpro.RadioFunction
import com.amirami.simapp.radiobroadcastpro.RadioFunction.setFavIcon
import com.amirami.simapp.radiobroadcastpro.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiobroadcastpro.databinding.RadioTiketMainBinding
import com.amirami.simapp.radiobroadcastpro.model.RadioEntity
import com.amirami.simapp.radiobroadcastpro.utils.Constatnts
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import java.util.ArrayList

@UnstableApi class RadioFavoriteAdapterVertical(private val listener: OnItemClickListener) :
    ListAdapter<MutableList<RadioEntity>, RadioFavoriteAdapterVertical.FavViewHolder>(DiffCallback()),
    FastScrollRecyclerView.SectionedAdapter {
    // class RadioFavoriteAdapter (private val listener: OnItemClickListener): RecyclerView.Adapter<RadioFavoriteAdapter.FavViewHolder>(), FastScrollRecyclerView.SectionedAdapter {

    private val items = ArrayList<RadioEntity>()
    private lateinit var productShopingRoom: RadioEntity

    fun setItems(items: MutableList<RadioEntity>) {
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

    @UnstableApi inner class FavViewHolder(private val binding: RadioTiketMainBinding) :
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

        fun bind(radioRoom: RadioEntity) {
            binding.apply {
                binding.favListIcon.setFavIcon(radioRoom.fav)

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

        fun onItemClick(radioRoom: RadioEntity)
        fun onMoreItemClick(radio: RadioEntity)
    }

    class DiffCallback : DiffUtil.ItemCallback<MutableList<RadioEntity>>() {
        override fun areItemsTheSame(
            oldItem: MutableList<RadioEntity>,
            newItem: MutableList<RadioEntity>
        ) =
            oldItem[0].stationuuid == newItem[0].stationuuid

        override fun areContentsTheSame(
            oldItem: MutableList<RadioEntity>,
            newItem: MutableList<RadioEntity>
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
