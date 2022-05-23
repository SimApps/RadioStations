package com.amirami.simapp.radiostations.ui

import android.os.Bundle
import android.view.*
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
import com.amirami.simapp.radiostations.Exoplayer.initializePlayer
import com.amirami.simapp.radiostations.Exoplayer.startPlayer
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction.setSafeOnClickListener
import com.amirami.simapp.radiostations.adapter.RadioFavoriteAdapterVertical
import com.amirami.simapp.radiostations.databinding.FragmentFavoriteBinding
import com.amirami.simapp.radiostations.model.RadioRoom
import com.amirami.simapp.radiostations.model.RadioVariables
import com.amirami.simapp.radiostations.viewmodel.InfoViewModel
import com.amirami.simapp.radiostations.viewmodel.RadioRoomViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class FavoriteRadioFragment : Fragment(R.layout.fragment_favorite),
    RadioFavoriteAdapterVertical.OnItemClickListener {
    //  private var currentNativeAdFavori:  NativeAd? = null
    private val infoViewModel: InfoViewModel by activityViewModels()
    private val radioRoomViewModel: RadioRoomViewModel by activityViewModels()
    private val radioRoom: MutableList<RadioRoom> = mutableListOf()
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var radioFavoriteAdapterVertical: RadioFavoriteAdapterVertical
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavoriteBinding.bind(view)

        setTheme()
        infoViewModel.putTitleText(getString(R.string.Favoris))
        setupRadioLisRV()
        getFavRadioRoom()

        //  setContentView(R.layout.activity_favorite_main_activity)


        binding.floatingActionAddStream.setSafeOnClickListener {
            val action = FavoriteRadioFragmentDirections.actionFavoriteRadioFragmentToAddDialogueBottomSheetFragment(false)
            this@FavoriteRadioFragment.findNavController().navigate(action) //      NavHostFragment.findNavController(requireParentFragment()).navigate(action)
        }
    }

    private fun setTheme() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                infoViewModel.putTheme.collectLatest {
                    RadioFunction.gradiancolorTransitionConstraint(binding.containerfav, 0,it)

                }
            }
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

    private fun getFavRadioRoom() {
        radioRoomViewModel.getAll(true).observe(viewLifecycleOwner) { list ->
            //    Log.d("MainFragment","ID ${list.map { it.id }}, Name ${list.map { it.name }}")
            if (list.isNotEmpty()) {
                radioRoom.clear()
                radioRoom.addAll(list)

                //    DynamicToast.makeError(requireContext(), list.size.toString(), 3).show()
                populateRecyclerView(radioRoom)
                binding.whenemptyfavImage.visibility = View.GONE
                binding.rv.visibility = View.VISIBLE
            } else {
                binding.whenemptyfavImage.visibility = View.VISIBLE
                binding.rv.visibility = View.INVISIBLE
            }


        }

    }

    private fun populateRecyclerView(radioRoom: MutableList<RadioRoom>) {
        if (radioRoom.isNotEmpty()) {
            //     radioFavoriteAdapter.radiodiffer = radioRoom
            radioFavoriteAdapterVertical.setItems(radioRoom)
            //  adapter.submitList(ArrayList(productShopingRoom))
            //  adapter.notifyDataSetChanged()
        }
    }

    override fun onItemClick(radioRoom: RadioRoom) {
        try {
            GlobalRadiourl = radioRoom.streamurl
            MainActivity.GlobalImage = radioRoom.favicon
            initializePlayer(requireContext())
            startPlayer()
            val radioVariables = RadioVariables()

            radioVariables.name = radioRoom.name
            radioVariables.bitrate = radioRoom.bitrate
            radioVariables.country = radioRoom.country
            radioVariables.stationuuid = radioRoom.radiouid
            radioVariables.favicon = radioRoom.favicon
            radioVariables.language = radioRoom.language
            radioVariables.state = radioRoom.state
            radioVariables.url_resolved = radioRoom.streamurl
            radioVariables.homepage = radioRoom.homepage
            radioVariables.tags = radioRoom.tags
            infoViewModel.putRadiopalyerInfo(radioVariables)


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

    override fun onMoreItemClick(radio: RadioRoom) {
        val radioVariables = RadioVariables()

        radioVariables.name = radio.name
        radioVariables.bitrate = radio.bitrate
        radioVariables.country = radio.country
        radioVariables.stationuuid = radio.radiouid
        radioVariables.favicon = radio.favicon
        radioVariables.language = radio.language
        radioVariables.state = radio.state
        radioVariables.url_resolved = radio.streamurl
        radioVariables.homepage = radio.homepage
        radioVariables.tags = radio.tags
        infoViewModel.putRadioInfo(radioVariables)
        this@FavoriteRadioFragment.findNavController().navigate(R.id.action_favoriteRadioFragment_to_moreBottomSheetFragment) //      NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.action_favoriteRadioFragment_to_moreBottomSheetFragment)
    }
}
