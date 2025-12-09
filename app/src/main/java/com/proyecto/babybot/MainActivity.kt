package com.proyecto.babybot

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- PRUEBA REALTIME DATABASE ---
        val db = Firebase.database.reference
        db.child("test_connection").setValue("Hola desde Android Studio!")
            .addOnSuccessListener {
                Log.d("BabyBotCheck", "Realtime DB: Escritura Exitosa")
            }
            .addOnFailureListener { e ->
                Log.e("BabyBotCheck", "Realtime DB Error: " + e.message)
            }

        // --- PRUEBA FIRESTORE ---
        val fs = Firebase.firestore
        val testData = hashMapOf(
            "mensaje" to "Conexión operativa",
            "fecha" to java.util.Date()
        )
        fs.collection("test_connection").add(testData)
            .addOnSuccessListener {
                Log.d("BabyBotCheck", "Firestore: Documento Creado")
            }
            .addOnFailureListener { e ->
                Log.e("BabyBotCheck", "Firestore Error: " + e.message)
            }

        setContent {
            Text(text = "Revisando conexión Firebase...")
        }
    }
}

/*package com.proyecto.babybot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.proyecto.babybot.ui.theme.BabyBotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BabyBotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BabyBotTheme {
        Greeting("Android")
    }
}

 */