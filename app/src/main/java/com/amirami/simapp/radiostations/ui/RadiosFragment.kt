package com.amirami.simapp.radiostations.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirami.simapp.radiostations.*
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction.collectLatestLifecycleFlow
import com.amirami.simapp.radiostations.RadioFunction.countryCodeToName
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.adapter.RadioAdapterVertical
import com.amirami.simapp.radiostations.databinding.ActivityOnAlarmBinding
import com.amirami.simapp.radiostations.databinding.FragmentRadiosBinding
import com.amirami.simapp.radiostations.model.RadioEntity
import com.amirami.simapp.radiostations.model.Status
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.amirami.simapp.radiostations.viewmodel.RetrofitRadioViewModel
import com.amirami.simapp.radiostations.viewmodel.SimpleMediaViewModel
import com.amirami.simapp.radiostations.viewmodel.UIEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

@UnstableApi @AndroidEntryPoint
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RadiosFragment : Fragment(R.layout.fragment_radios), RadioAdapterVertical.OnItemClickListener {
    private lateinit var binding: FragmentRadiosBinding

    private val infoViewModel: InfoViewModel by activityViewModels()
    private val retrofitRadioViewModel: RetrofitRadioViewModel by activityViewModels()
    private val simpleMediaViewModel: SimpleMediaViewModel by activityViewModels()

    private lateinit var radioAdapterHorizantal: RadioAdapterVertical
    val argsFrom: RadiosFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRadiosBinding.bind(view)

        setupRadioLisRV()

                    collectLatestLifecycleFlow(lifecycleOwner = this,retrofitRadioViewModel.queryString) { queryString ->
                        setUpRv(queryString)
                    }



        binding.itemErrorMessage.btnRetry.setSafeOnClickListener {
            if (argsFrom.msg != "Empty") {
                retrofitRadioViewModel.changeBseUrl()
                infoViewModel.putDefServerInfo(MainActivity.BASE_URL)
                retrofitRadioViewModel.getRadios(argsFrom.msg, argsFrom.secondmsg)
            } else binding.itemErrorMessage.root.visibility = View.INVISIBLE
        }

        if (argsFrom.secondmsg == "Empty" || argsFrom.secondmsg == "300") {
            infoViewModel.putTitleText(countryCodeToName(argsFrom.msg).replaceFirstChar { it.uppercase() })
        } else infoViewModel.putTitleText(countryCodeToName(argsFrom.secondmsg).replaceFirstChar { it.uppercase() })
    }

    // exoplayer begin
    /* public override fun onStart() {
         super.onStart()

         if(MainActivity.firstInitPlayer){
             MainActivity.firstInitPlayer =false
             if (Util.SDK_INT > 23) {
                 ExoPlayer.initializePlayer(ctx = this)
             }
         }

     }
 */



    private fun setUpRv(queryString : String?) {

                    collectLatestLifecycleFlow(lifecycleOwner = this,retrofitRadioViewModel.responseRadio) { response ->
                        when (response.status) {
                            Status.SUCCESS -> {
                                if (response.data != null) {
                                    //     radioAdapterHorizantal.radiodiffer = response.data as List<RadioVariables>

                                    val list = response.data as MutableList<RadioEntity>



                                    collectLatestLifecycleFlow(lifecycleOwner = this,infoViewModel.favList) {favList ->
                                        val filteredList = list.filter { it.name.contains(queryString.toString(),
                                            ignoreCase = true) } as MutableList<RadioEntity>
                                        if(favList.isNotEmpty()){
                                            val favlist = favList as ArrayList<RadioEntity>




                                            for ((index, value) in filteredList.withIndex()) {

                                                if(favlist.any { it.stationuuid == value.stationuuid })
                                                    filteredList[index].fav = true

                                            }

                                        }
                                        radioAdapterHorizantal.setItems(items =filteredList)

                                    }





                                    hideProgressBar()
                                    binding.itemErrorMessage.root.visibility = View.INVISIBLE
                                }
                                else showErrorConnection(response.message!!)
                            }
                            Status.ERROR -> {
                                hideProgressBar()
                                showErrorConnection(response.message!!)
                            }
                            Status.LOADING -> { displayProgressBar() }
                        }

                    }


        //  DynamicToast.makeError(requireContext(),argsFrom.msg+" , "+argsFrom.secondmsg, 3).show()
    }
    private fun showErrorConnection(msg: String) {
        binding.itemErrorMessage.root.visibility = View.VISIBLE
        binding.itemErrorMessage.tvErrorMessage.text = msg
    }

    private fun displayProgressBar() {
        binding.spinKitcontentradio.visibility = View.VISIBLE
        binding.ExpandablelistViewRadioRadioActivity.visibility = View.GONE
    }

    private fun hideProgressBar() {
        binding.spinKitcontentradio.visibility = View.GONE
        binding.ExpandablelistViewRadioRadioActivity.visibility = View.VISIBLE
    }
    private fun setupRadioLisRV() {
        radioAdapterHorizantal = RadioAdapterVertical(this)
        binding.ExpandablelistViewRadioRadioActivity.apply {
            adapter = radioAdapterHorizantal
            layoutManager = LinearLayoutManager(requireContext())

            setHasFixedSize(true)
        }
    }
    override fun onItemClick(radio: RadioEntity) {
        try {
            simpleMediaViewModel.loadData(listOf(radio)as MutableList<RadioEntity>)
            simpleMediaViewModel.onUIEvent(UIEvent.PlayPause)
         // jsonCall=api.addclick(idListJson[holder.absoluteAdapterPosition]!!)

            //   startServices(context)
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
    }

    override fun onMoreItemClick(radio: RadioEntity) {
        infoViewModel.putRadioInfo(radio)
        this@RadiosFragment.findNavController().navigate(R.id.action_radiosFragment_to_moreBottomSheetFragment) //   NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.action_radiosFragment_to_moreBottomSheetFragment)
    }

    override fun onFavClick(radio: RadioEntity) {
        TODO("Not yet implemented")
    }



}
