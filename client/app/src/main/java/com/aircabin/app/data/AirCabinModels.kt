package com.aircabin.app.data

enum class FlightSyncMode(
    val label: String,
    val caption: String,
    val sourceChip: String,
) {
    Simulated("模拟航线", "计划参考", "Plan only"),
    LiveAdjusted("实时修正", "已同步", "Synced 15:02"),
    OfflineFallback("离线回退", "离线延续", "Offline cache"),
}

enum class FlightPhase(val label: String) {
    Boarding("登机中"),
    Taxi("滑行中"),
    Climb("爬升中"),
    Cruise("巡航中"),
    Descend("下降中"),
    Arrived("已落地"),
}

enum class ConnectivityState(val label: String) {
    Online("Cabin Wi-Fi"),
    Weak("弱网"),
    Offline("离线"),
}

data class FlightRecord(
    val id: String,
    val airline: String,
    val flightNo: String,
    val departureCode: String,
    val arrivalCode: String,
    val departureCity: String,
    val arrivalCity: String,
    val seatNo: String,
    val cabin: String,
    val departureTime: String,
    val arrivalTime: String,
)

data class CurrentFlightUiModel(
    val record: FlightRecord,
    val mode: FlightSyncMode,
    val phase: FlightPhase,
    val connectivity: ConnectivityState,
    val progress: Float,
    val remaining: String,
    val elapsed: String,
    val total: String,
    val eta: String,
    val altitude: String,
    val speed: String,
    val distanceLeft: String,
)

data class StatSummary(
    val totalHours: String,
    val totalFlights: String,
    val totalDistance: String,
    val cachedAt: String,
)

data class GallerySummary(
    val reclaimableSpace: String,
    val candidateCount: String,
    val categories: List<String>,
)

data class ChatSummary(
    val roomLabel: String,
    val participants: String,
    val roomNotice: String,
    val networkHint: String,
)

data class ProfileSummary(
    val travelerName: String,
    val yearlyFlights: String,
    val preferenceTag: String,
)

data class ImportDraft(
    val flightNo: String = "CA1234",
    val departureCode: String = "PEK",
    val arrivalCode: String = "SZX",
    val departureCity: String = "北京",
    val arrivalCity: String = "深圳",
    val seatNo: String = "12A",
    val cabin: String = "经济舱",
)

sealed interface LoadState<out T> {
    data object Loading : LoadState<Nothing>
    data class Success<T>(val value: T) : LoadState<T>
    data class Empty(val title: String, val body: String) : LoadState<Nothing>
    data class Error(val title: String, val body: String) : LoadState<Nothing>
}
