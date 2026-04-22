package com.aircabin.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AirCabinRepository {
    var importDraft by mutableStateOf(ImportDraft())
        private set

    var flightState: LoadState<CurrentFlightUiModel> by mutableStateOf(
        LoadState.Success(sampleFlight()),
    )
        private set

    var statsState: LoadState<StatSummary> by mutableStateOf(
        LoadState.Success(
            StatSummary(
                totalHours = "186 h",
                totalFlights = "42",
                totalDistance = "98,214 km",
                cachedAt = "Cached 14:58",
            ),
        ),
    )
        private set

    var galleryState: LoadState<GallerySummary> by mutableStateOf(
        LoadState.Success(
            GallerySummary(
                reclaimableSpace = "12.8 GB",
                candidateCount = "428 项",
                categories = listOf("截图", "大视频", "相似照片", "当天旅拍"),
            ),
        ),
    )
        private set

    var chatState: LoadState<ChatSummary> by mutableStateOf(
        LoadState.Success(
            ChatSummary(
                roomLabel = "CA1234 · 经济舱",
                participants = "28 人在线",
                roomNotice = "封闭匿名，仅限同舱位",
                networkHint = "弱网下可保留草稿，离线不可进入",
            ),
        ),
    )
        private set

    var profileState: LoadState<ProfileSummary> by mutableStateOf(
        LoadState.Success(
            ProfileSummary(
                travelerName = "Traveler C",
                yearlyFlights = "42 次飞行",
                preferenceTag = "Wi-Fi only",
            ),
        ),
    )
        private set

    fun updateDraft(draft: ImportDraft) {
        importDraft = draft
    }

    fun confirmImport() {
        val record = FlightRecord(
            id = "current",
            airline = "中国国际航空",
            flightNo = importDraft.flightNo,
            departureCode = importDraft.departureCode,
            arrivalCode = importDraft.arrivalCode,
            departureCity = importDraft.departureCity,
            arrivalCity = importDraft.arrivalCity,
            seatNo = importDraft.seatNo,
            cabin = importDraft.cabin,
            departureTime = "13:38",
            arrivalTime = "17:06",
        )
        flightState = LoadState.Success(
            sampleFlight(record = record),
        )
    }

    fun useMode(mode: FlightSyncMode) {
        val current = (flightState as? LoadState.Success)?.value ?: return
        val connectivity = when (mode) {
            FlightSyncMode.Simulated -> ConnectivityState.Weak
            FlightSyncMode.LiveAdjusted -> ConnectivityState.Online
            FlightSyncMode.OfflineFallback -> ConnectivityState.Offline
        }
        flightState = LoadState.Success(current.copy(mode = mode, connectivity = connectivity))
    }

    fun clearCurrentFlight() {
        flightState = LoadState.Empty(
            title = "还没有当前航班",
            body = "从登机牌、截图或手动录入开始建立本次飞行记录。",
        )
    }

    fun restoreCurrentFlight() {
        flightState = LoadState.Success(sampleFlight())
    }

    fun simulateStatsError() {
        statsState = LoadState.Error(
            title = "统计暂时不可用",
            body = "离线缓存仍保留最近一次聚合结果，联网后会自动刷新。",
        )
    }

    fun restoreStats() {
        statsState = LoadState.Success(
            StatSummary(
                totalHours = "186 h",
                totalFlights = "42",
                totalDistance = "98,214 km",
                cachedAt = "Cached 14:58",
            ),
        )
    }

    companion object {
        fun sampleFlight(record: FlightRecord = sampleRecord()): CurrentFlightUiModel {
            return CurrentFlightUiModel(
                record = record,
                mode = FlightSyncMode.Simulated,
                phase = FlightPhase.Cruise,
                connectivity = ConnectivityState.Weak,
                progress = 0.72f,
                remaining = "1h 18m",
                elapsed = "2h 12m",
                total = "3h 28m",
                eta = "17:06",
                altitude = "10,600 m",
                speed = "842 km/h",
                distanceLeft = "1,164 km",
            )
        }

        private fun sampleRecord() = FlightRecord(
            id = "sample",
            airline = "中国国际航空",
            flightNo = "CA1234",
            departureCode = "PEK",
            arrivalCode = "SZX",
            departureCity = "北京",
            arrivalCity = "深圳",
            seatNo = "12A",
            cabin = "经济舱",
            departureTime = "13:38",
            arrivalTime = "17:06",
        )
    }
}
