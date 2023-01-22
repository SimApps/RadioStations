package com.amirami.simapp.radiostations.utils.connectivity.internet


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amirami.simapp.radiostations.IoDispatcher
import com.amirami.simapp.radiostations.model.Resource
import com.amirami.simapp.radiostations.model.Status
import com.amirami.simapp.radiostations.utils.connectivity.internet.ListenNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val listenNetwork: ListenNetwork
) : ViewModel() {

    private val _isConnected = MutableStateFlow(true)
    val isConnected = _isConnected.asStateFlow()

  //  private val _isConnected = mutableStateOf(false)
  //  val isConnected: State<Boolean> = _isConnected
    init {
        getNetworkState()
    }
    fun getNetworkState() = viewModelScope.launch {
        listenNetwork.isConnected.collect {
            _isConnected.value = it
        }
    }
}