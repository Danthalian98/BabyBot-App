package com.proyecto.babybot.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BabyDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getBabyByUserId(userId: String): Map<String, Any>? {
        return try {
            val result = firestore.collection("babies")
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .await()

            result.documents.firstOrNull()?.data
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveBaby(babyData: Map<String, Any>): Boolean {
        return try {
            firestore.collection("babies").add(babyData).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}