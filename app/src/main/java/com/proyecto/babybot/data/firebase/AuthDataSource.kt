package com.proyecto.babybot.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthDataSource @Inject constructor() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun isUserLogged(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
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

            val uid = result.user?.uid ?: throw Exception("UID no encontrado")

            val fechaRegistro = System.currentTimeMillis()
            val fechaExpiracion = fechaRegistro + (7 * 24 * 60 * 60 * 1000)

            // Documento Usuario
            val usuario = hashMapOf(
                "idUsuario" to uid,
                "nombre" to name,
                "correo" to email,
                "fechaRegistro" to fechaRegistro,
                "estadoCuenta" to "TRIAL"
            )

            firestore.collection("usuarios")
                .document(uid)
                .set(usuario)
                .await()

            // Documento Licencia
            val licencia = hashMapOf(
                "idLicencia" to uid,
                "idUsuario" to uid,
                "fechaCompra" to fechaRegistro,
                "fechaExpiracion" to fechaExpiracion,
                "estado" to "ACTIVA"
            )

            firestore.collection("licencias")
                .document(uid)
                .set(licencia)
                .await()

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

            val doc = firestore.collection("licencias")
                .document(uid)
                .get()
                .await()

            val fechaExpiracion = doc.getLong("fechaExpiracion") ?: return false

            val now = System.currentTimeMillis()

            now < fechaExpiracion

        } catch (e: Exception) {
            false
        }
    }

    fun logout() {
        auth.signOut()
    }
}