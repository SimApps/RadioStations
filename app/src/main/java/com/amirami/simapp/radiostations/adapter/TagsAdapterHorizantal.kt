package com.amirami.simapp.radiostations.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.databinding.RadioHorizontalrecyclervTiketBinding
import java.util.*




class TagsAdapterHorizantal (private val item: ArrayList<String>, private val images:ArrayList<Int>,private val listener: OnItemClickListener): RecyclerView.Adapter<TagsAdapterHorizantal.MyViewHolder>() {


    inner class MyViewHolder(val binding: RadioHorizontalrecyclervTiketBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.apply {
                cardViewMainRadio.setSafeOnClickListener {
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) listener.onItemTagsClick(item[bindingAdapterPosition],images[bindingAdapterPosition])
                }

            }
        }

        fun bind(position:Int) {
            binding.apply {

                RadioFunction.maintextviewColor(mainTxV, MainActivity.darkTheme)
                //   RadioFunction.secondarytextviewColor(NbrradioStationTxV)
                mainTxV.text = item[position].replaceFirstChar { it.uppercase() }

               // countryFlagImageView.setImageResource(images[position])
                RadioFunction.loadImageInt(images[position], MainActivity.imagedefaulterrorurl, imageViewRv)




            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            RadioHorizontalrecyclervTiketBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = item.size

    interface OnItemClickListener {
        fun onItemTagsClick(item : String, image : Int)
    }

}
