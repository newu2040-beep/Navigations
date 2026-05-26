package com.example.database

import kotlinx.coroutines.flow.Flow

class GestureRepository(private val gestureDao: GestureDao) {
    val allGestures: Flow<List<GestureEntity>> = gestureDao.getAllGestures()
    val allRules: Flow<List<AutomationRuleEntity>> = gestureDao.getAllAutomationRules()
    val aggregatedStats: Flow<List<GestureStatsEntity>> = gestureDao.getAggregatedStats()

    suspend fun insertGesture(gesture: GestureEntity) {
        gestureDao.insertGesture(gesture)
    }

    suspend fun deleteGesture(gesture: GestureEntity) {
        gestureDao.deleteGesture(gesture)
    }

    suspend fun deleteGestureById(id: Int) {
        gestureDao.deleteGestureById(id)
    }

    suspend fun insertRule(rule: AutomationRuleEntity) {
        gestureDao.insertAutomationRule(rule)
    }

    suspend fun deleteRule(rule: AutomationRuleEntity) {
        gestureDao.deleteAutomationRule(rule)
    }

    suspend fun incrementStat(identifier: String, category: String) {
        gestureDao.incrementOrInsertStat(identifier, category)
    }
}
