package com.amirami.simapp.radiostations.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import java.io.IOException
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amirami.simapp.radiostations.*
import com.amirami.simapp.radiostations.MainActivity.Companion.GlobalRadiourl
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction.countryCodeToName
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.adapter.RadioAdapterHorizantal
import com.amirami.simapp.radiostations.adapter.RadioAdapterVertical
import com.amirami.simapp.radiostations.databinding.FragmentRadiosBinding
import com.amirami.simapp.radiostations.model.RadioVariables
import com.amirami.simapp.radiostations.model.Status
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.amirami.simapp.radiostations.viewmodel.RetrofitRadioViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RadiosFragment : Fragment(R.layout.fragment_radios), RadioAdapterVertical.OnItemClickListener  {
    private lateinit var binding: FragmentRadiosBinding

    private val infoViewModel: InfoViewModel by activityViewModels()
    private val retrofitRadioViewModel: RetrofitRadioViewModel by activityViewModels()
    private lateinit var radioAdapterHorizantal: RadioAdapterVertical
    val argsFrom: RadiosFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRadiosBinding.bind(view)

        setTheme()
        setupRadioLisRV()
        setUpRv()
        binding.itemErrorMessage.btnRetry.setSafeOnClickListener {
            if(argsFrom.msg!= "Empty") {
                retrofitRadioViewModel.changeBseUrl()
                infoViewModel.putPutDefServerInfo(MainActivity.BASE_URL)
                retrofitRadioViewModel.getRadios(argsFrom.msg,argsFrom.secondmsg)
            }
            else binding.itemErrorMessage.root.visibility= View.INVISIBLE
        }


        if(argsFrom.secondmsg== "Empty" || argsFrom.secondmsg== "300")
            infoViewModel.putTitleText(countryCodeToName(argsFrom.msg).replaceFirstChar { it.uppercase() })
        else infoViewModel.putTitleText(countryCodeToName(argsFrom.secondmsg).replaceFirstChar { it.uppercase() })

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


    private fun setTheme(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                infoViewModel.putTheme.collectLatest {
                    RadioFunction.gradiancolorTransitionConstraint(binding.contentradio, 0,it)
                }
            }
        }




    }



    private fun setUpRv() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                retrofitRadioViewModel.responseRadio.collectLatest { response ->
                    when (response.status) {
                        Status.SUCCESS -> {
                            if(response.data!=null){
                                //     radioAdapterHorizantal.radiodiffer = response.data as List<RadioVariables>
                                radioAdapterHorizantal.setItems(response.data as MutableList<RadioVariables>)

                                hideProgressBar()
                                binding.itemErrorMessage.root.visibility= View.INVISIBLE
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

            }
        }


        //  DynamicToast.makeError(requireContext(),argsFrom.msg+" , "+argsFrom.secondmsg, 3).show()


    }
    fun showErrorConnection(msg:String){
        binding.itemErrorMessage.root.visibility= View.VISIBLE
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
    override fun onItemClick(radio: RadioVariables) {


            try {
                GlobalRadiourl=radio.url_resolved
                MainActivity.GlobalImage = radio.favicon
                Exoplayer.initializePlayer(requireContext(),false)
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
        this@RadiosFragment.findNavController().navigate(R.id.action_radiosFragment_to_moreBottomSheetFragment) //   NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.action_radiosFragment_to_moreBottomSheetFragment)
    }


}
