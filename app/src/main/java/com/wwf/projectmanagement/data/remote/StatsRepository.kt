package com.wwf.projectmanagement.data.remote

import com.wwf.projectmanagement.data.Projects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Load state of a project's statistics tiles. */
sealed interface StatsLoad {
    data object Loading : StatsLoad
    data class Ready(val stats: ProjectStats) : StatsLoad
    data object Failed : StatsLoad
}

/**
 * Fetches each project's statistics once (started at app launch by [prefetchAll]) and exposes
 * them as state so the info page shows live numbers, "…" while loading, or "—" on failure.
 */
class StatsRepository(private val api: StatsApi = StatsApi()) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val states = mutableMapOf<String, MutableStateFlow<StatsLoad>>()

    fun stats(projectId: String): StateFlow<StatsLoad> {
        val flow = synchronized(states) { states.getOrPut(projectId) { MutableStateFlow(StatsLoad.Loading) } }
        if (flow.value !is StatsLoad.Ready) fetch(projectId, flow)
        return flow.asStateFlow()
    }

    fun prefetchAll() = Projects.all.forEach { stats(it.id) }

    private fun fetch(projectId: String, flow: MutableStateFlow<StatsLoad>) {
        scope.launch {
            flow.value = try {
                StatsLoad.Ready(
                    when (projectId) {
                        Projects.STRAW_HEADED_BULBUL_ID -> api.strawHeadedBulbul()
                        Projects.RIFLE_RANGE_ROAD_ID -> api.rifleRangeRoad()
                        else -> throw IllegalArgumentException("Unknown project $projectId")
                    },
                )
            } catch (_: Exception) {
                StatsLoad.Failed
            }
        }
    }

    companion object {
        @Volatile
        private var instance: StatsRepository? = null

        fun get(): StatsRepository =
            instance ?: synchronized(this) { instance ?: StatsRepository().also { instance = it } }
    }
}
