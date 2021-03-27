package net.gotev.recycleradapterdemo.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

sealed class State<out SuccessType : Any> {
    object Loading : State<Nothing>()
    class Success<out SuccessType : Any>(val data: SuccessType) : State<SuccessType>()
    class Error(val error: Throwable) : State<Nothing>()
}

typealias CoroutineService<Input, Output> = suspend (Input) -> Output

class CoroutineFetcher<Input : Any?, Output : Any>(
    private val coroutineScope: CoroutineScope,
    private val service: CoroutineService<Input, Output>,
    private val context: CoroutineContext = Dispatchers.IO
) {
    private val internalStatus = MutableLiveData<State<Output>>()

    val status: LiveData<State<Output>>
        get() = internalStatus

    fun fetch(input: Input) {
        coroutineScope.launch(context) {
            internalStatus.postValue(State.Loading)
            runCatching { service(input) }
                .onSuccess { internalStatus.postValue(State.Success(it)) }
                .onFailure { internalStatus.postValue(State.Error(it)) }
        }
    }
}

fun <Input : Any?, Output : Any> ViewModel.inputFetcher(service: CoroutineService<Input, Output>) =
    CoroutineFetcher(viewModelScope, service)

fun <Output : Any> ViewModel.fetcher(service: CoroutineService<Unit, Output>) =
    CoroutineFetcher(viewModelScope, service)

fun <Output : Any> CoroutineFetcher<Unit, Output>.fetch() {
    fetch(Unit)
}
