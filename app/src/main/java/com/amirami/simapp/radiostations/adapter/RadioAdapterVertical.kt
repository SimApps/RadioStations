package com.amirami.simapp.radiostations.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.databinding.RadioTiketMainBinding
import com.amirami.simapp.radiostations.model.RadioVariables
import com.amirami.simapp.radiostations.utils.Constatnts
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import java.util.*

//class RadioAdapterVertical (private val listener: OnItemClickListener): RecyclerView.Adapter<RadioAdapterVertical.MyViewHolder>(DiffCallback()), FastScrollRecyclerView.SectionedAdapter {


    class RadioAdapterVertical(private val listener: OnItemClickListener) :
        ListAdapter<RadioVariables, RadioAdapterVertical.MyViewHolder>(DiffCallback()), FastScrollRecyclerView.SectionedAdapter {


        private val items = ArrayList<RadioVariables>()
    fun setItems(items: MutableList<RadioVariables>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }
    inner class MyViewHolder(val binding: RadioTiketMainBinding) :
        RecyclerView.ViewHolder(binding.root){

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


            }
        }




        fun bind(currentTvShow: RadioVariables) {
            binding.apply {
                RadioFunction.maintextviewColor(mainTxVw, MainActivity.darkTheme)
                RadioFunction.secondarytextviewColor(descriptionTxVw, MainActivity.darkTheme)

                    RadioFunction.maintextviewColor(mainTxVw, MainActivity.darkTheme)
                    RadioFunction.secondarytextviewColor(descriptionTxVw, MainActivity.darkTheme)


                mainTxVw.text=currentTvShow.name
                descriptionTxVw.text= root.context.getString(
                    R.string.stationinfo,
                  if (currentTvShow.bitrate!="")currentTvShow.bitrate +if (currentTvShow.language!="" || currentTvShow.language != "")" kbps, " else " kbps " else ""  ,
                    if(currentTvShow.country!="")currentTvShow.country +if (currentTvShow.language!="")", " else "" else "",
                    if(currentTvShow.language!="")currentTvShow.language  else "")

                    /*    fun stationInfo():String{
                 return if (currentTvShow.bitrate!=""&&currentTvShow.country!= "" && currentTvShow.language!= "")
                     currentTvShow.bitrate + "kbps, " + currentTvShow.country+ ", " + currentTvShow.language
                   else if (currentTvShow.bitrate != "" && currentTvShow.country != "" && currentTvShow.language == "")
                     currentTvShow.bitrate + "kbps, " + currentTvShow.country
                   else if (currentTvShow.bitrate!= "" && currentTvShow.country=="" && currentTvShow.language != "")
                     currentTvShow.bitrate +"kbps, "+ currentTvShow.language!=""
                   else if (currentTvShow.bitrate==""&&currentTvShow.country!="" &&currentTvShow.language!="")
                     currentTvShow.country+ ", "+currentTvShow.language
                   else if (currentTvShow.bitrate!=""&&currentTvShow.country==""&& currentTvShow.language=="")
                     currentTvShow.bitrate +"kbps"
                   else if(currentTvShow.bitrate==""&&currentTvShow.country!=""&& currentTvShow.language=="")
                     currentTvShow.country
                   else if (currentTvShow.bitrate==""&&currentTvShow.country==""&& currentTvShow.language!="")
                     currentTvShow.language
                   else ""
               }*/







                    RadioFunction.loadImageString(
                        root.context,
                        currentTvShow.favicon ,
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


    class DiffCallback : DiffUtil.ItemCallback<RadioVariables>() {
        override fun areItemsTheSame(oldItem: RadioVariables, newItem: RadioVariables) =
            oldItem.stationuuid == newItem.stationuuid

        override fun areContentsTheSame(oldItem: RadioVariables, newItem:RadioVariables) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            RadioTiketMainBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }



    override fun getItemCount() = items.size//differ.currentList.size

    interface OnItemClickListener {
        fun onItemClick(radio: RadioVariables)
        fun onMoreItemClick(radio: RadioVariables)
    }

    override fun onBindViewHolder(holder: RadioAdapterVertical.MyViewHolder, position: Int) {
        val currentTvShow = items[position]//radiodiffer[position]
        holder.bind(currentTvShow)
    }


    override fun getSectionName(position: Int): String {
        var strTextview = items[position].name
        strTextview = strTextview.substring(0, 1)
        return strTextview//String.format("%d", position + 1)
    }
}