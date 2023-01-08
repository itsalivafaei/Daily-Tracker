package com.example.dailytracker.uiModule.main

import com.example.dailytracker.dataModule.sleep.db.SleepClassifyEventEntity
import com.example.dailytracker.dataModule.sleep.db.SleepSegmentEventEntity

data class MainUiSleepState(
    val subscribedToSleepData: Boolean = false,
    val sleepSegmentEvents: List<SleepSegmentEventEntity> =
        listOf<SleepSegmentEventEntity>(),
    val sleepClassifyEvents: List<SleepClassifyEventEntity> =
        listOf<SleepClassifyEventEntity>()
)