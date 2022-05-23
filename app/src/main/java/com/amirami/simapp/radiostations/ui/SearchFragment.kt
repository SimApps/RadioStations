package com.amirami.simapp.radiostations.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirami.simapp.radiostations.*
import com.amirami.simapp.radiostations.MainActivity.Companion.GlobalRadiourl
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.adapter.RadioAdapterVertical
import com.amirami.simapp.radiostations.databinding.FragmentSearchBinding
import com.amirami.simapp.radiostations.model.RadioVariables
import com.amirami.simapp.radiostations.model.Status
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.amirami.simapp.radiostations.viewmodel.RetrofitRadioViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search), RadioAdapterVertical.OnItemClickListener {

    private lateinit var binding: FragmentSearchBinding
    private val infoViewModel: InfoViewModel by activityViewModels()
    private val retrofitRadioViewModel: RetrofitRadioViewModel by activityViewModels()
    private lateinit var radioAdapterHorizantal: RadioAdapterVertical


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                infoViewModel.putTheme.collectLatest {
                    RadioFunction.gradiancolorTransitionConstraint(binding.containerseach, 0,it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                infoViewModel.putSearchQuery.collectLatest { query->
                    //   DynamicToast.makeError(requireContext(), query , 3).show()

                    binding.itemErrorMessage.btnRetry.setOnClickListener {
                        if (query!="") {
                            retrofitRadioViewModel.changeBseUrl()
                            infoViewModel.putPutDefServerInfo(MainActivity.BASE_URL)
                            retrofitRadioViewModel.getRadiosByName(query)
                        }
                        else {
                            binding.itemErrorMessage.root.visibility= View.INVISIBLE
                        }
                    }

                }

            }
        }



        setUpRv()

    }


    private fun setUpRv() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                retrofitRadioViewModel.responseRadioSreach.collectLatest { response ->
                    when (response.status) {
                        Status.SUCCESS -> {
                            if(response.data!=null){
                                hideProgressBar()
                                binding.itemErrorMessage.root.visibility= View.INVISIBLE
                                //radioAdapterHorizantal.radiodiffer = response.data as List<RadioVariables>
                                setupRadioLisRV()
                                // DynamicToast.makeError(requireContext(), "query" , 3).show()
                                radioAdapterHorizantal.setItems(response.data as MutableList<RadioVariables>)
                            }
                            else showErrorConnection(response.message!!)

                        }
                        Status.ERROR -> {
                            hideProgressBar()
                            showErrorConnection(response.message!!)
                        }
                        Status.LOADING -> {

                            displayProgressBar()
                        }
                    }
                }

            }
        }






    }
    fun showErrorConnection(msg:String){
        binding.itemErrorMessage.root.visibility= View.VISIBLE
        binding.itemErrorMessage.tvErrorMessage.text = msg
    }

    private fun displayProgressBar() {
        binding.spinKitsearsh.visibility = View.VISIBLE
        binding.ExpandablelistViewRadioSeachActivity.visibility = View.GONE
    }

    private fun hideProgressBar() {
        binding.spinKitsearsh.visibility = View.GONE
        binding.ExpandablelistViewRadioSeachActivity.visibility = View.VISIBLE
    }

    private fun setupRadioLisRV() {
        radioAdapterHorizantal = RadioAdapterVertical(this)
        binding.ExpandablelistViewRadioSeachActivity.apply {
            adapter = radioAdapterHorizantal
            layoutManager = LinearLayoutManager(requireContext())

            setHasFixedSize(true)
        }
    }
    //exoplayer begin
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






    /*
    public override fun onStop() {
        Glide.with(this).clear(view)
        super.onStop()
        /* if (Util.SDK_INT > 23) {
             ExoPlayer.releasePlayer()
         }*/
    }
*/
    //exoplayer end*/

    // receive data form fragments
    //  override fun iAmMSG(msg: String) {
    //tv_activity.text = msg
    //}

    private fun closeKeyboard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onResume() {

        super.onResume()


            closeKeyboard()

          //  GlobalQuery =""
    }


    override fun onItemClick(radio: RadioVariables) {


        try {
            GlobalRadiourl=radio.url_resolved
            MainActivity.GlobalImage = radio.favicon
            Exoplayer.initializePlayer(requireContext())
            Exoplayer.startPlayer()
            infoViewModel.putRadiopalyerInfo(radio)
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

    override fun onMoreItemClick(radio: RadioVariables) {
        infoViewModel.putRadioInfo(radio)
        this@SearchFragment.findNavController().navigate(R.id.action_searchFragment_to_moreBottomSheetFragment) //     NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.action_searchFragment_to_moreBottomSheetFragment)

    }

/*
    fun <T> Fragment.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(collect)
            }
        }
    }*/
}
