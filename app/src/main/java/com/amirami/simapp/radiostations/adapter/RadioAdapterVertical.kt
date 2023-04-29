package com.amirami.simapp.radiostations.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.RadioFunction.setFavIcon
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.databinding.RadioTiketMainBinding
import com.amirami.simapp.radiostations.model.RadioEntity
import com.amirami.simapp.radiostations.utils.Constatnts
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import java.util.*

// class RadioAdapterVertical (private val listener: OnItemClickListener): RecyclerView.Adapter<RadioAdapterVertical.MyViewHolder>(DiffCallback()), FastScrollRecyclerView.SectionedAdapter {

@UnstableApi class RadioAdapterVertical(private val listener: OnItemClickListener) :
    ListAdapter<RadioEntity, RadioAdapterVertical.MyViewHolder>(DiffCallback()), FastScrollRecyclerView.SectionedAdapter {

    private val items = ArrayList<RadioEntity>()
    fun setItems(items: MutableList<RadioEntity>) {
        this.items.clear()
        this.items.addAll(items)

        notifyDataSetChanged()
    }
    @UnstableApi inner class MyViewHolder(val binding: RadioTiketMainBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.apply {
                layoutABorrar.setSafeOnClickListener {
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        listener.onItemClick(items[bindingAdapterPosition])
                    }
                }

                ExpandImageView.setSafeOnClickListener {
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        listener.onMoreItemClick(items[bindingAdapterPosition])
                    }
                }
                favListIcon.setSafeOnClickListener {
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                     //   favListIcon.setFavIcon(!items[bindingAdapterPosition].fav)
                        Log.d("jjhnbv","nn "+items[bindingAdapterPosition].fav.toString())
                        listener.onFavClick(items[bindingAdapterPosition])
                        Log.d("jjhnbv",items[bindingAdapterPosition].fav.toString())
                    }
                }
                Log.d("iikjnhb","xx")
            }
        }

        fun bind(radioEntity: RadioEntity) {
            binding.apply {

Log.d("iikjnhb","aa")
                if (bindingAdapterPosition != RecyclerView.NO_POSITION)
                    favListIcon.setFavIcon(items[bindingAdapterPosition].fav)


                mainTxVw.text = radioEntity.name
                descriptionTxVw.text = root.context.getString(
                    R.string.stationinfo,
                    if (radioEntity.bitrate != "")radioEntity.bitrate + if (radioEntity.language != "")" kbps, " else " kbps " else "",
                    if (radioEntity.country != "")radioEntity.country + if (radioEntity.language != "")", " else "" else "",
                    if (radioEntity.language != "")radioEntity.language else ""
                )



                RadioFunction.loadImageString(
                    root.context,
                    radioEntity.favicon,
                    MainActivity.imagedefaulterrorurl,
                    ImageView,
                    Constatnts.CORNER_RADIUS_8F
                )
            }
        }
    }

/*
    private val diffCallback = object : DiffUtil.ItemCallback<RadioVariables>() {
        override fun areItemsTheSame(oldItem: RadioVariables, newItem: RadioVariables): Boolean {
            return oldItem.stationuuid == newItem.stationuuid
        }

        override fun areContentsTheSame(oldItem: RadioVariables, newItem: RadioVariables): Boolean {
            return newItem == oldItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var radiodiffer: List<RadioVariables>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }
    */

    class DiffCallback : DiffUtil.ItemCallback<RadioEntity>() {
        override fun areItemsTheSame(oldItem: RadioEntity, newItem: RadioEntity) =
            oldItem.stationuuid == newItem.stationuuid

        override fun areContentsTheSame(oldItem: RadioEntity, newItem: RadioEntity) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            RadioTiketMainBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size // differ.currentList.size

    interface OnItemClickListener {
        fun onItemClick(radio: RadioEntity)
        fun onMoreItemClick(radio: RadioEntity)

        fun onFavClick(radio: RadioEntity)
    }

    override fun onBindViewHolder(holder: RadioAdapterVertical.MyViewHolder, position: Int) {

        holder.bind(items[position])
    }

    override fun getSectionName(position: Int): String {
        var strTextview = items[position].name
        strTextview = strTextview.substring(0, 1)
        return strTextview // String.format("%d", position + 1)
    }
}
