package net.gotev.recycleradapterdemo.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

sealed class AsyncState<out SuccessType : Any> {
    object Loading : AsyncState<Nothing>()
    class Success<out SuccessType : Any>(val data: SuccessType) : AsyncState<SuccessType>()
    class Error(val error: Throwable) : AsyncState<Nothing>()
}

typealias CoroutineService<Input, Output> = suspend (Input) -> Output

class CoroutineFetcher<Input : Any?, Output : Any>(
    private val coroutineScope: CoroutineScope,
    private val service: CoroutineService<Input, Output>,
    private val context: CoroutineContext = Dispatchers.IO
) {
    private val internalStatus = MutableLiveData<AsyncState<Output>>()

    val status: LiveData<AsyncState<Output>>
        get() = internalStatus

    fun fetch(input: Input) {
        coroutineScope.launch(context) {
            internalStatus.postValue(AsyncState.Loading)
            runCatching { service(input) }
                .onSuccess { internalStatus.postValue(AsyncState.Success(it)) }
                .onFailure { internalStatus.postValue(AsyncState.Error(it)) }
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
