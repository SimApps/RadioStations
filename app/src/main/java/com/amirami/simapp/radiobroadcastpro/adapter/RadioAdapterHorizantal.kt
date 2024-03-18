package com.amirami.simapp.radiobroadcastpro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amirami.simapp.radiobroadcastpro.MainActivity
import com.amirami.simapp.radiobroadcastpro.RadioFunction
import com.amirami.simapp.radiobroadcastpro.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiobroadcastpro.databinding.RadioHorizontalrecyclervTiketBinding
import com.amirami.simapp.radiobroadcastpro.model.RadioEntity
import com.amirami.simapp.radiobroadcastpro.utils.Constatnts.CORNER_RADIUS_8F
import com.amirami.simapp.radiobroadcastpro.utils.Constatnts.COUNTRY_FLAGS_BASE_URL
import java.util.*

// class RadioAdapterHorizantal (private val listener: OnItemClickListener): RecyclerView.Adapter<RadioAdapterHorizantal.MyViewHolder>() {

@UnstableApi class RadioAdapterHorizantal(private val listener: OnItemClickListener) :
    ListAdapter<RadioEntity, RadioAdapterHorizantal.MyViewHolder>(DiffCallback()) {


    private val items = ArrayList<RadioEntity>()
    fun setItems(items: MutableList<RadioEntity>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }
    @UnstableApi inner class MyViewHolder(val binding: RadioHorizontalrecyclervTiketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {

                cardViewMainRadio.setSafeOnClickListener {

                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        listener.onItemClick(items[bindingAdapterPosition])
                    }
                }
            }
        }

        fun bind(currentTvShow: RadioEntity) {
            binding.apply {

                descriptionTxV.text = currentTvShow.stationcount // globalStationcountsJson[position]

                mainTxV.text = RadioFunction.countryCodeToName(currentTvShow.name)
                if (currentTvShow.stationuuid == "" && currentTvShow.name != RadioFunction.countryCodeToName(currentTvShow.name)) {
                    if (RadioFunction.isNumber(currentTvShow.name/*   globalCountriesJson[position]*/)) {
                        mainTxV.text = currentTvShow.name // globalCountriesJson[position]

                        RadioFunction.loadImageString(
                            root.context,
                            "https://i.ibb.co/B31L5GW/error.jpg",
                            MainActivity.imagedefaulterrorurl,
                            imageViewRv,
                            CORNER_RADIUS_8F
                        )
                    } else {
                        RadioFunction.loadImageString(
                            root.context,
                            COUNTRY_FLAGS_BASE_URL + currentTvShow.name.lowercase(Locale.ROOT)+".svg",

                            MainActivity.imagedefaulterrorurl,

                            imageViewRv,
                            CORNER_RADIUS_8F
                        )
                    }
                } else if (currentTvShow.stationuuid != "") {
                    RadioFunction.loadImageString(
                        root.context,
                        currentTvShow.favicon,
                        MainActivity.imagedefaulterrorurl, // R.drawable.ic_radio_svg,
                        imageViewRv,
                        CORNER_RADIUS_8F
                    )
                }

                /*     cardViewMainRadio.setOnClickListener {
                         try {
                             RadioFunction.countryCodeToName(globalCountriesJsons)
                             //  intent.putExtra("title_full_name_country", Activity_Main.GlobalCountries_name_formcode)

                             val action = MainFragmentDirections.actionMainFragmentToRadiosFragment(getString(
                                 R.string.countries), globalCountriesJson[position]!! )
                             NavHostFragment.findNavController(requireParentFragment()).navigate(action)

                         } catch (e: IOException) {
                             // Catch the exception
                             e.printStackTrace()
                         } catch (e: IllegalArgumentException) {
                             e.printStackTrace()
                         } catch (e: SecurityException) {
                             e.printStackTrace()
                         } catch (e: IllegalStateException) {
                             e.printStackTrace()
                         }
                     }*/
                //    setAnimation(holder.itemView)
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

      val differ = AsyncListDiffer(this, diffCallback)
*/

 /*   var radioList: List<RadioVariables>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }*/

    class DiffCallback : DiffUtil.ItemCallback<RadioEntity>() {
        override fun areItemsTheSame(oldItem: RadioEntity, newItem: RadioEntity) =
            oldItem.stationuuid == newItem.stationuuid

        override fun areContentsTheSame(oldItem: RadioEntity, newItem: RadioEntity) =
            oldItem == newItem
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
        val currentTvShow = items[position] // differ.currentList[position]
        holder.bind(currentTvShow)
    }

    override fun getItemCount() = items.size // differ.currentList.size

    interface OnItemClickListener {
        fun onItemClick(radio: RadioEntity)
    }
}
