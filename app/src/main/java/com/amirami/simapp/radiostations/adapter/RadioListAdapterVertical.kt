package com.amirami.simapp.radiostations.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
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
import com.amirami.simapp.radiostations.utils.Constatnts.COUNTRY_FLAGS_BASE_URL
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import java.util.*

// class RadioListAdapterVertical (private val listener: OnItemClickListener): RecyclerView.Adapter<RadioListAdapterVertical.MyViewHolder>(), FastScrollRecyclerView.SectionedAdapter {

class RadioListAdapterVertical(private val listener: OnItemClickListener) :
    ListAdapter<RadioVariables, RadioListAdapterVertical.MyViewHolder>(DiffCallback()), FastScrollRecyclerView.SectionedAdapter {

    private val items = ArrayList<RadioVariables>()
    fun setItems(items: MutableList<RadioVariables>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }
    inner class MyViewHolder(val binding: RadioTiketMainBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.apply {
                layoutABorrar.setSafeOnClickListener {
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        listener.onItemClick(items[bindingAdapterPosition])
                    }
                }
            }
        }

        fun bind(radioVariables: RadioVariables) {
            binding.apply {
                ExpandImageView.visibility = View.GONE
                RadioFunction.maintextviewColor(mainTxVw, MainActivity.darkTheme)
                RadioFunction.secondarytextviewColor(descriptionTxVw, MainActivity.darkTheme)

                if (radioVariables.ip != "" && radioVariables.name == RadioFunction.countryCodeToName(radioVariables.name)) {
                    val globalserversJson = arrayOfNulls<String>(items.size + 1)

                    for (i in items.indices) {
                        if (":" !in radioVariables.ip) globalserversJson[i] = "http://" + items[i].ip
                        else globalserversJson[i] = ""
                    }

                    ImageView.visibility = View.GONE

                    if (MainActivity.BASE_URL == globalserversJson[bindingAdapterPosition]) {
                        mainTxVw.setTextColor(Color.parseColor("#FF546d79"))
                    }

                    if (globalserversJson[bindingAdapterPosition] != "") {
                        mainTxVw.text = globalserversJson[bindingAdapterPosition]
                        descriptionTxVw.text = "" // GlobalStationcountsJson[position]
                    } else layoutABorrar.visibility = View.GONE
                } else {
                    ImageView.visibility = View.VISIBLE
                    mainTxVw.text = RadioFunction.countryCodeToName(radioVariables.name)
                    descriptionTxVw.text = radioVariables.stationcount

                    if (radioVariables.name != RadioFunction.countryCodeToName(radioVariables.name)) {
                        if (RadioFunction.isNumber(radioVariables.name/*   globalCountriesJson[position]*/)) {
                            mainTxVw.text = radioVariables.name // globalCountriesJson[position]

                            RadioFunction.loadImageString(
                                root.context,
                                "https://i.ibb.co/B31L5GW/error.jpg",
                                MainActivity.imagedefaulterrorurl,
                                ImageView,
                                Constatnts.CORNER_RADIUS_8F
                            )
                        } else {
                            mainTxVw.text = RadioFunction.countryCodeToName(radioVariables.name)
                            RadioFunction.loadImageString(
                                root.context,
                                COUNTRY_FLAGS_BASE_URL + radioVariables.name.lowercase(Locale.ROOT),
                                MainActivity.imagedefaulterrorurl,
                                ImageView,
                                Constatnts.CORNER_RADIUS_8F
                            )
                        }
                    } else if (radioVariables.country != "") {
                        // flagImageView.setImageResource(R.drawable.states)
                        RadioFunction.loadImageInt(R.drawable.states, MainActivity.imagedefaulterrorurl, ImageView)
                    } else if (radioVariables.iso_639 != "") {
                        // flagImageView.setImageResource(R.drawable.languages)
                        RadioFunction.loadImageInt(R.drawable.languages, MainActivity.imagedefaulterrorurl, ImageView)
                    } else {
                        //  flagImageView.setImageResource(R.drawable.ic_radio_svg)
                        RadioFunction.loadImageInt(R.drawable.ic_radio_svg, MainActivity.imagedefaulterrorurl, ImageView)
                    }

                    /*   else if (argsFrom.msg == root.context.getString(R.string.codecs)) {
                           RadioFunction.funGlideInt(root.context, R.drawable.codec,
                               MainActivity.imagedefaulterrorurl, flagImageView)
                       }

                       else if (argsFrom.msg == root.context.getString(R.string.languages)) {
                           RadioFunction.funGlideInt(root.context, R.drawable.languages,
                               MainActivity.imagedefaulterrorurl, flagImageView)
                       }
                       else if (argsFrom.msg == root.context.getString(R.string.tags)) {
                           RadioFunction.funGlideInt(root.context, R.drawable.tags,
                               MainActivity.imagedefaulterrorurl, flagImageView)
                       }*/
                }
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
    var radioListdffer: List<RadioVariables>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }
*/
    class DiffCallback : DiffUtil.ItemCallback<RadioVariables>() {
        override fun areItemsTheSame(oldItem: RadioVariables, newItem: RadioVariables) =
            oldItem.stationuuid == newItem.stationuuid

        override fun areContentsTheSame(oldItem: RadioVariables, newItem: RadioVariables) =
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
        fun onItemClick(radio: RadioVariables)
    }

    override fun onBindViewHolder(holder: RadioListAdapterVertical.MyViewHolder, position: Int) {
        val currentTvShow = items[position] // radioListdffer[position]
        holder.bind(currentTvShow)
    }

    override fun getSectionName(position: Int): String {
        var strTextview = items[position].name
        strTextview = strTextview.substring(0, 1)
        return strTextview // String.format("%d", position + 1)
    }
}
