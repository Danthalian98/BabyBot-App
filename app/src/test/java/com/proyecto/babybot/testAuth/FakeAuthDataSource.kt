package com.proyecto.babybot.testAuth

import com.proyecto.babybot.data.firebase.AuthDataSource
import com.proyecto.babybot.data.firebase.LicenseInfo

// Heredamos de la clase original pasando nulls para evitar los errores de parámetros
class FakeAuthDataSource : AuthDataSource(null, null) {

    var shouldSucceed = true

    // Usamos 'override' para cambiar el comportamiento original por el de prueba
    override suspend fun login(email: String, psw: String): Result<Unit> {
        return if (shouldSucceed) Result.success(Unit)
        else Result.failure(Exception("invalid-credentials"))
    }

    override suspend fun getLicenseInfo(): LicenseInfo {
        return LicenseInfo(isPremium = true, isTrialActive = true)
    }

    override fun getCurrentUserId(): String? {
        return if (shouldSucceed) {
            "user_123"
        } else {
            null
        }
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = Result.success(Unit)
}