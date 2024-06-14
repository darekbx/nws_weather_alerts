package com.darekbx.nwsweatheralerts.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.nwsweatheralerts.repository.remote.NWSService
import com.darekbx.nwsweatheralerts.repository.remote.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class UiState {
    class Done(val result: Response) : UiState()
    class Failed(val e: Exception) : UiState()
    object Loading : UiState()
    object Idle : UiState()
}

class NWSViewModel(
    private val nwsService: NWSService
) : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState: State<UiState>
        get() = _uiState

    fun getAlerts() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val result = nwsService.getActiveAlerts(STATUS, MESSAGE_TYPE, LIMIT)
                    _uiState.value = UiState.Done(result)
                } catch (e: Exception) {
                    _uiState.value = UiState.Failed(e)
                }
            }
        }
    }

    companion object {
        private val STATUS = "actual"
        private val MESSAGE_TYPE = "alert"
        private val LIMIT = 10
    }
}