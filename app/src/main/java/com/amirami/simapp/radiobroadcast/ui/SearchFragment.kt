package com.amirami.simapp.radiobroadcast.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirami.simapp.radiobroadcast.*
import com.amirami.simapp.radiobroadcast.R
import com.amirami.simapp.radiobroadcast.RadioFunction.collectLatestLifecycleFlow
import com.amirami.simapp.radiobroadcast.adapter.RadioAdapterVertical
import com.amirami.simapp.radiobroadcast.databinding.FragmentSearchBinding
import com.amirami.simapp.radiobroadcast.model.FavoriteFirestore
import com.amirami.simapp.radiobroadcast.model.RadioEntity
import com.amirami.simapp.radiobroadcast.model.Status
import com.amirami.simapp.radiobroadcast.viewmodel.FavoriteFirestoreViewModel
import com.amirami.simapp.radiobroadcast.viewmodel.InfoViewModel
import com.amirami.simapp.radiobroadcast.viewmodel.RetrofitRadioViewModel
import com.amirami.simapp.radiobroadcast.viewmodel.SimpleMediaViewModel
import com.amirami.simapp.radiobroadcast.viewmodel.UIEvent
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@UnstableApi @AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search), RadioAdapterVertical.OnItemClickListener {
   lateinit var filteredList: MutableList<RadioEntity>
    private lateinit var binding: FragmentSearchBinding
    private val infoViewModel: InfoViewModel by activityViewModels()
    private val retrofitRadioViewModel: RetrofitRadioViewModel by activityViewModels()
    private val simpleMediaViewModel: SimpleMediaViewModel by activityViewModels()
    private lateinit var radioAdapterHorizantal: RadioAdapterVertical
    private val favoriteFirestoreViewModel: FavoriteFirestoreViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)




                    //   DynamicToast.makeError(requireContext(), query , 3).show()
                    collectLatestLifecycleFlow(lifecycleOwner = this,infoViewModel.putSearchQuery) { query ->

                    binding.itemErrorMessage.btnRetry.setOnClickListener {
                        if (query != "") {
                            retrofitRadioViewModel.changeBseUrl()
                            infoViewModel.putDefServerInfo(MainActivity.BASE_URL)
                            retrofitRadioViewModel.getRadiosByName(query)
                        } else {
                            binding.itemErrorMessage.root.visibility = View.INVISIBLE
                        }

            }
        }

        setUpRv()
    }

    private fun setUpRv() {

                   collectLatestLifecycleFlow(lifecycleOwner = this,retrofitRadioViewModel.responseRadioSreach) { response ->
                    when (response.status) {
                        Status.SUCCESS -> {
                            if (response.data != null) {

                                hideProgressBar()
                                binding.itemErrorMessage.root.visibility = View.INVISIBLE
                                // radioAdapterHorizantal.radiodiffer = response.data as List<RadioVariables>
                                setupRadioLisRV()


                                            collectLatestLifecycleFlow(lifecycleOwner = this,infoViewModel.favList) { favList ->

                                                filteredList = response.data as MutableList<RadioEntity>
                                                if(favList.isNotEmpty()){
                                                    val favlist = favList as ArrayList<RadioEntity>



                                                    for ((index, value) in filteredList.withIndex()) {

                                                        if(favlist.any { it.stationuuid == value.stationuuid })
                                                            filteredList[index].fav = true

                                                    }
                                                }

                                                radioAdapterHorizantal.setItems(filteredList)

                                            }



                            } else showErrorConnection(response.message!!)
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
    private fun showErrorConnection(msg: String) {
        binding.itemErrorMessage.root.visibility = View.VISIBLE
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

    /*
    public override fun onStop() {
        Glide.with(this).clear(view)
        super.onStop()
        /* if (Util.SDK_INT > 23) {
             ExoPlayer.releasePlayer()
         }*/
    }
*/
    // exoplayer end*/

    // receive data form fragments
    //  override fun iAmMSG(msg: String) {
    // tv_activity.text = msg
    // }

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
        Log.d("jjdnsqq","ss"+radio.toString())

        infoViewModel.putRadioInfo(radio)
        this@SearchFragment.findNavController().navigate(R.id.action_searchFragment_to_moreBottomSheetFragment) //     NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.action_searchFragment_to_moreBottomSheetFragment)
    }

    override fun onFavClick(radio: RadioEntity) {
        handleFavClick(radio)
    }
    private fun handleFavClick(radioVar : RadioEntity){

            val isFav = infoViewModel.isFavRadio(radioVar)

            if (!isFav && radioVar.stationuuid != "") {
                addFavoriteRadioIdInArrayFirestore(radioVar.stationuuid)
            } else if (isFav) {
                deleteFavoriteRadioFromArrayinfirestore(radioVar.stationuuid)
            }

            infoViewModel.setFavRadio(radioVar)


            //  simpleMediaViewModel.setRadioVar(radioVar)



    }
    private fun addFavoriteRadioIdInArrayFirestore(radioUid: String) {
        val addFavoritRadioIdInArrayFirestore =
            favoriteFirestoreViewModel.addFavoriteRadioidinArrayFirestore(
                radioUid,
                RadioFunction.getCurrentDate()
            )
        addFavoritRadioIdInArrayFirestore.observe(this) {
            // if (it != null)  if (it.data!!)  prod name array updated
            RadioFunction.interatialadsShow(requireContext())
            if (it.e != null) {
                // prod bame array not updated
                RadioFunction.errorToast(requireContext(), it.e!!)

                if(it.e!!.contains( "NOT_FOUND") ){
                    val isProductAddLiveData = favoriteFirestoreViewModel.addUserDocumentInFirestore(
                        FavoriteFirestore()
                    )

                    isProductAddLiveData.observe(this) { dataOrException ->
                        val isProductAdded = dataOrException.data
                        if (isProductAdded != null) {
                            //   hideProgressBar()

                        }
                        if (dataOrException.e != null) {
                            RadioFunction.errorToast(requireContext(), dataOrException.e!!)
                            /*   if(dataOrException.e=="getRadioUID"){

                               }*/

                        }
                    }
                }

            }
        }
    }

    private fun deleteFavoriteRadioFromArrayinfirestore(radioUid: String) {
        val deleteFavoriteRadiofromArrayInFirestore =
            favoriteFirestoreViewModel.deleteFavoriteRadioFromArrayinFirestore(radioUid)
        deleteFavoriteRadiofromArrayInFirestore.observe(this) {
            RadioFunction.interatialadsShow(requireContext())
            // if (it != null)  if (it.data!!)  prod name array updated
            if (it.e != null) {
                // prod bame array not updated
                RadioFunction.dynamicToast(requireContext(), it.e!!)
            }
        }
    }


}


