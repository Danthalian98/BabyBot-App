package com.proyecto.babybot.testBaby

import com.proyecto.babybot.data.firebase.Baby
import com.proyecto.babybot.data.firebase.BabyDataSource

// Heredamos y pasamos null para evitar el error de "Method myPid not mocked"
class FakeBabyDataSource : BabyDataSource(null) {
    var lastSavedBaby: Baby? = null
    var shouldReturnSuccess = true

    override suspend fun saveBaby(baby: Baby): Boolean {
        return if (shouldReturnSuccess) {
            lastSavedBaby = baby
            true
        } else {
            false
        }
    }

    override suspend fun getBabyByUserId(idUsuario: String): Baby? {
        return lastSavedBaby
    }
}