package net.gotev.recycleradapterdemo.network.api

data class SWAPIPaginatedResponse<T>(
        val count: Int,
        val next: String?,
        val previous: String?,
        val results: List<T>
)

data class SWAPIPerson(
        val name: String,
        val height: String
)
