package com.amirami.asm.core.utils.connectivity.internet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amirami.simapp.radiostations.utils.connectivity.internet.ListenNetwork

class NetworkViewModelFactory(
    private val listenNetwork: ListenNetwork? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ListenNetwork::class.java).newInstance(listenNetwork)
    }
}
