package com.amirami.simapp.radiostations.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.MainActivity.Companion.imagedefaulterrorurl
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.RadioFunction.indexesOf
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.databinding.RadioTiketMainBinding
import com.amirami.simapp.radiostations.model.RecordInfo
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import java.util.ArrayList

class RecordedFilesAdapter(private val listener: OnItemClickListener) :
    ListAdapter<MutableList<RecordInfo>, RecordedFilesAdapter.RecViewHolder>(DiffCallback()), FastScrollRecyclerView.SectionedAdapter {

    private val items = ArrayList<RecordInfo>()
    private lateinit var productShopingRoom: RecordInfo

    fun setItems(items: MutableList<RecordInfo>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewHolder {
        val binding = RadioTiketMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecViewHolder, position: Int) {
        productShopingRoom = items[position]

        holder.bind(productShopingRoom)
    }

    override fun getItemCount(): Int {
        return items.size // fidCardDBList.size
    }

    inner class RecViewHolder(private val binding: RadioTiketMainBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                layoutABorrar.setSafeOnClickListener {
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        listener.onRecItemClick(items[bindingAdapterPosition])
                    }
                }

                ExpandImageView.setSafeOnClickListener {
                    listener.onRecMoreItemClick(items[bindingAdapterPosition], bindingAdapterPosition)
                }
            }
        }

        fun bind(recordInfo: RecordInfo) {
            binding.apply {
                RadioFunction.maintextviewColor(mainTxVw, MainActivity.darkTheme)
                RadioFunction.secondarytextviewColor(descriptionTxVw, MainActivity.darkTheme)

                //   val scale: Float = root.context.resources.displayMetrics.density
                //   val pixels = (32 * scale + 0.5f).toInt()
                //    val layoutParams = TableRow.LayoutParams(pixels, pixels)

                //    ExpandImageView.layoutParams = layoutParams

                ExpandImageView.setImageResource(R.drawable.delete)
                //  RadioFunction.loadImageInt(R.drawable.delete, imagedefaulterrorurl, ExpandImageView)

                if (recordInfo.name.contains("_ _", true) && recordInfo.name.contains("___", true)) {
                    if (mainTxVw.text.isNotEmpty()) {
                        mainTxVw.text = recordInfo.name.substring(0, recordInfo.name.indexesOf("_ _", true)[0])
                    }
                    if (descriptionTxVw.text.isNotEmpty()) {
                        descriptionTxVw.text = recordInfo.name.substring(
                            recordInfo.name.indexesOf("_ _", true)[0] + 3,
                            recordInfo.name.indexesOf("___", true)[0]
                        ) + " " +
                            RadioFunction.shortformateDate(
                                recordInfo.name.substring(
                                    recordInfo.name.indexesOf("___", true)[0] + 3,
                                    recordInfo.name.length - 4
                                )
                            ) + ".mp3"
                            /*RadioFunction.removeWord(recordInfo.name
                            .substring(recordInfo.name.indexesOf("_ _", true)[0] + 3,
                                recordInfo.name.length),  ".mp3")*/

                        // RadioFunction.shortformateDate()//s.name.substring(s.name.indexesOf("_ _", true)[0] + 2, s.name.length)
                    }
                } else {
                    mainTxVw.text = recordInfo.name
                    descriptionTxVw.text = ""
                }

                //  faviconImageView.setImageResource(R.drawable.recordings)
                RadioFunction.loadImageInt(R.drawable.rec_on, imagedefaulterrorurl, ImageView)
            }
        }
    }

    interface OnItemClickListener {

        fun onRecItemClick(recordInfo: RecordInfo)
        fun onRecMoreItemClick(recordInfo: RecordInfo, position: Int)
    }

    class DiffCallback : DiffUtil.ItemCallback<MutableList<RecordInfo>>() {
        override fun areItemsTheSame(oldItem: MutableList<RecordInfo>, newItem: MutableList<RecordInfo>) =
            oldItem[0].name == newItem[0].name

        override fun areContentsTheSame(oldItem: MutableList<RecordInfo>, newItem: MutableList<RecordInfo>) =
            oldItem == newItem
    }

    override fun getSectionName(position: Int): String {
        var strTextview = items[position].name
        strTextview = strTextview.substring(0, 1)
        return strTextview
    }
}
