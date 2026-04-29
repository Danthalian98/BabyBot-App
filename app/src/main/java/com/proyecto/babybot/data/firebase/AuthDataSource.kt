package com.proyecto.babybot.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.auth.userProfileChangeRequest

// Ahora pasamos FirebaseAuth y FirebaseFirestore por el constructor
class AuthDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    // ELIMINAMOS ESTO:
    // private val auth = FirebaseAuth.getInstance()
    // private val firestore = FirebaseFirestore.getInstance()

    fun isUserLogged(): Boolean = auth.currentUser != null

    fun getCurrentUserId(): String? = auth.currentUser?.uid
    fun getCurrentUserEmail(): String? = auth.currentUser?.email

    fun getCurrentUserName(): String? = auth.currentUser?.displayName

    suspend fun getCurrentUserProfile(): Result<Map<String, Any?>> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Usuario no encontrado")

            val doc = firestore.collection("usuarios")
                .document(uid)
                .get()
                .await()

            Result.success(doc.data.orEmpty())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()

            val user = auth.currentUser ?: throw Exception("Usuario no encontrado")

            if (user.displayName.isNullOrBlank()) {
                val doc = firestore.collection("usuarios")
                    .document(user.uid)
                    .get()
                    .await()

                val nombre = doc.getString("nombre")

                if (!nombre.isNullOrBlank()) {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = nombre
                    }
                    user.updateProfile(profileUpdates).await()
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Usuario no encontrado")
            val uid = user.uid

            val profileUpdates = userProfileChangeRequest {
                displayName = name
            }
            user.updateProfile(profileUpdates).await()

            val fechaRegistro = System.currentTimeMillis()
            val fechaExpiracion = fechaRegistro + (7 * 24 * 60 * 60 * 1000)

            val usuario = hashMapOf(
                "idUsuario" to uid,
                "nombre" to name,
                "correo" to email,
                "fechaRegistro" to fechaRegistro,
                "estadoCuenta" to "TRIAL"
            )

            firestore.collection("usuarios").document(uid).set(usuario).await()

            val licencia = hashMapOf(
                "idLicencia" to uid,
                "idUsuario" to uid,
                "fechaCompra" to fechaRegistro,
                "fechaExpiracion" to fechaExpiracion,
                "estado" to "TRIAL",
                "avisoTrialMostrado" to false
            )

            firestore.collection("licencias").document(uid).set(licencia).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isTrialActive(): Boolean {
        return try {
            val uid = auth.currentUser?.uid ?: return false
            val doc = firestore.collection("licencias").document(uid).get().await()
            val fechaExpiracion = doc.getLong("fechaExpiracion") ?: return false
            System.currentTimeMillis() < fechaExpiracion
        } catch (e: Exception) {
            false
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getLicenseInfo(): LicenseInfo {
        val uid = auth.currentUser?.uid ?: return LicenseInfo(isLoggedIn = false)

        return try {
            val doc = firestore.collection("licencias").document(uid).get().await()

            if (!doc.exists()) {
                LicenseInfo(isLoggedIn = true)
            } else {
                val fechaExpiracion = doc.getLong("fechaExpiracion") ?: 0L
                val estado = doc.getString("estado") ?: ""
                val avisoTrialMostrado = doc.getBoolean("avisoTrialMostrado") ?: false
                val now = System.currentTimeMillis()

                val isPremium = estado == "PREMIUM"
                val isTrialActive = !isPremium && now < fechaExpiracion

                LicenseInfo(
                    isLoggedIn = true,
                    isTrialActive = isTrialActive,
                    isTrialNoticeShown = avisoTrialMostrado,
                    isPremium = isPremium
                )
            }
        } catch (e: Exception) {
            LicenseInfo(isLoggedIn = true)
        }
    }

    suspend fun markTrialNoticeAsShown() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("licencias")
            .document(uid)
            .update("avisoTrialMostrado", true)
            .await()
    }

}