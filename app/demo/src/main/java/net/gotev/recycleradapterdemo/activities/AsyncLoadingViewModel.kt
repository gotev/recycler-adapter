package net.gotev.recycleradapterdemo.activities

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import net.gotev.recycleradapterdemo.App
import net.gotev.recycleradapterdemo.network.api.SWAPIPaginatedResponse
import net.gotev.recycleradapterdemo.network.fetcher
import java.lang.RuntimeException

class AsyncLoadingViewModel: ViewModel() {
    private var response = 0

    val people = fetcher {
        delay(1000)

        response += 1
        if (response > 2) response = 0

        when (response) {
            1 -> throw RuntimeException("Exception content")
            2 -> SWAPIPaginatedResponse(count = 0, next = null, previous = null, results = emptyList())
            else -> App.starWarsClient.getPeople()
        }
    }
}
