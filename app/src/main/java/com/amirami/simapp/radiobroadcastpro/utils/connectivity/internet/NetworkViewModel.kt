package com.amirami.simapp.radiobroadcastpro.utils.connectivity.internet


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amirami.simapp.radiobroadcastpro.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
