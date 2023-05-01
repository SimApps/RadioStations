package com.amirami.simapp.radiostations.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.RecyclerView
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.databinding.RadioHorizontalrecyclervTiketBinding
import java.util.*
data class Tags(
    val name : String,
    val image: Int,

)
@UnstableApi class TagsAdapterHorizantal(private val item: ArrayList<Tags>, private val listener: OnItemClickListener) : RecyclerView.Adapter<TagsAdapterHorizantal.MyViewHolder>() {

    @UnstableApi inner class MyViewHolder(val binding: RadioHorizontalrecyclervTiketBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                cardViewMainRadio.setSafeOnClickListener {
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) listener.onItemTagsClick(item[bindingAdapterPosition])
                }
            }
        }

        fun bind(position: Int) {
            binding.apply {
                //   RadioFunction.secondarytextviewColor(NbrradioStationTxV)
                mainTxV.text = item[position].name.replaceFirstChar { it.uppercase() }

                // countryFlagImageView.setImageResource(images[position])
                RadioFunction.loadImageInt(item[position].image, MainActivity.imagedefaulterrorurl, imageViewRv)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            RadioHorizontalrecyclervTiketBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = item.size

    interface OnItemClickListener {
        fun onItemTagsClick(item: Tags)
    }
}
