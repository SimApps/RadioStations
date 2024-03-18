package com.amirami.simapp.radiobroadcastpro.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirami.simapp.radiobroadcastpro.R
import com.amirami.simapp.radiobroadcastpro.RadioFunction
import com.amirami.simapp.radiobroadcastpro.RadioFunction.collectLatestLifecycleFlow
import com.amirami.simapp.radiobroadcastpro.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiobroadcastpro.adapter.RadioFavoriteAdapterVertical
import com.amirami.simapp.radiobroadcastpro.databinding.FragmentFavoriteBinding
import com.amirami.simapp.radiobroadcastpro.model.RadioEntity
import com.amirami.simapp.radiobroadcastpro.viewmodel.InfoViewModel
import com.amirami.simapp.radiobroadcastpro.viewmodel.RetrofitRadioViewModel
import com.amirami.simapp.radiobroadcastpro.viewmodel.SimpleMediaViewModel
import com.amirami.simapp.radiobroadcastpro.viewmodel.UIEvent
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@UnstableApi @AndroidEntryPoint
class FavoriteRadioFragment :
    Fragment(R.layout.fragment_favorite),
    RadioFavoriteAdapterVertical.OnItemClickListener {
    //  private var currentNativeAdFavori:  NativeAd? = null
    private val infoViewModel: InfoViewModel by activityViewModels()
    private val simpleMediaViewModel: SimpleMediaViewModel by activityViewModels()
    private val retrofitRadioViewModel: RetrofitRadioViewModel by activityViewModels()

    private val radioRoom: MutableList<RadioEntity> = mutableListOf()
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var radioFavoriteAdapterVertical: RadioFavoriteAdapterVertical


    lateinit var filterredList: MutableList<RadioEntity>



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavoriteBinding.bind(view)


        infoViewModel.putTitleText(getString(R.string.Favoris))
        setupRadioLisRV()


        //  setContentView(R.layout.activity_favorite_main_activity)

        binding.floatingActionAddStream.setSafeOnClickListener {
            val action = FavoriteRadioFragmentDirections.actionFavoriteRadioFragmentToAddDialogueBottomSheetFragment(false)
            this@FavoriteRadioFragment.findNavController().navigate(action) //      NavHostFragment.findNavController(requireParentFragment()).navigate(action)
        }




                    collectLatestLifecycleFlow(lifecycleOwner = this,retrofitRadioViewModel.queryString) {queryString ->


                    getFavRadioRoom(queryString)
                }
    }



    private fun setupRadioLisRV() {
        radioFavoriteAdapterVertical = RadioFavoriteAdapterVertical(this)
        binding.rv.apply {
            adapter = radioFavoriteAdapterVertical
            layoutManager = LinearLayoutManager(requireContext())

            setHasFixedSize(true)
        }
    }

    private fun getFavRadioRoom(queryString : String?) {


                    collectLatestLifecycleFlow(lifecycleOwner = this,infoViewModel.favList) {list ->
                        //    Log.d("MainFragment","ID ${list.map { it.id }}, Name ${list.map { it.name }}")
                        filterredList = (list.filter { it.name.contains(queryString.toString() ,
                            ignoreCase = true) }as MutableList<RadioEntity>)

                        if (filterredList.isNotEmpty()) {
                            radioRoom.clear()
                            radioRoom.addAll(filterredList)

                            //    DynamicToast.makeError(requireContext(), list.size.toString(), 3).show()
                            populateRecyclerView(filterredList)
                            binding.whenemptyfavImage.visibility = View.GONE
                            binding.rv.visibility = View.VISIBLE
                        } else {
                            binding.whenemptyfavImage.visibility = View.VISIBLE
                            binding.rv.visibility = View.INVISIBLE
                        }
                    }




    }

    private fun populateRecyclerView(radioRoom: MutableList<RadioEntity>) {
        if (radioRoom.isNotEmpty()) {
            //     radioFavoriteAdapter.radiodiffer = radioRoom
            radioFavoriteAdapterVertical.setItems(radioRoom)
            //  adapter.submitList(ArrayList(productShopingRoom))
            //  adapter.notifyDataSetChanged()
        }
    }

    override fun onItemClick(radioRoom: RadioEntity) {
        try {
            val list = RadioFunction.moveItemToFirst(
                array = filterredList,
                item = radioRoom
            )
            simpleMediaViewModel.loadData(list)
             simpleMediaViewModel.onUIEvent(UIEvent.PlayPause)

            //   jsonCall=api.addclick(radioRoom[position].radiouid)
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
        Log.d("jjdnsqq","ff"+radio.toString())

        infoViewModel.putRadioInfo(radio)
        this@FavoriteRadioFragment.findNavController().navigate(R.id.action_favoriteRadioFragment_to_moreBottomSheetFragment) //      NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.action_favoriteRadioFragment_to_moreBottomSheetFragment)
    }


  /*  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_APP_UPDATE) {
            if (resultCode == RESULT_CANCELED) {
                errorToast(requireContext(), "Update canceled!")
                //  checkUpdate()
            } else if (resultCode != RESULT_IN_APP_UPDATE_FAILED) {
                errorToast(requireContext(), resultCode.toString())
                checkUpdate()
            } else if (resultCode != RESULT_OK) {
                errorToast(requireContext(), resultCode.toString())
                checkUpdate()
            }
        }
    }
*/





}
