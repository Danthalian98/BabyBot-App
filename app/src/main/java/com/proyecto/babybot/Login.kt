package com.proyecto.babybot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.babybot.ui.theme.BabyBotTheme
import com.proyecto.babybot.ui.theme.BlueSkyeLight
import com.proyecto.babybot.ui.theme.HardBlueText
import com.proyecto.babybot.ui.theme.LightBlueButton

class Login: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BabyBotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginApp(
                        name = "",
                        modifier = Modifier.padding(innerPadding)

                    )
                }
            }
        }
    }
}

@Composable
fun LoginApp(name: String, modifier: Modifier = Modifier){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueSkyeLight)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ){
            //Logo (placeholder)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(LightBlueButton),
                contentAlignment = Alignment.Center
            ){
                //Puse un icono como si fuera el logo, luego hago el cambio con el verdadero
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "BabyBot",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )

            //Apartado de Login
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ){
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = HardBlueText
                    )

                    CustomInputField(
                        label = "Correo electrónico",
                        placeholder = "Ingrese su correo electrónico",
                        icon = Icons.Outlined.Email
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    CustomInputField(
                        label = "Contraseña",
                        placeholder = "Ingrese su contraseña",
                        icon = Icons.Outlined.Lock,
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    //Boton para inicar sesion
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(LightBlueButton),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Iniciar Sesión",
                                fontSize = 18.sp,
                                color = HardBlueText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    //Boton recordar contraseña
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = HardBlueText,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable{}
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 24.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )

                    val registro = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = HardBlueText)){
                            append("¿No tienes una cuenta? ")
                        }
                        withStyle(style = SpanStyle(color = HardBlueText, fontWeight = FontWeight.Bold)){
                            append("Regístrate")
                        }
                    }
                    Text(
                        text = registro,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable{}
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomInputField(
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    darkPurple: Color = Color.Black,
    fieldBackground: Color = Color.White,
    fieldBorder: Color = Color.Gray,
    lightPurple: Color = Color.Magenta
) {
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = darkPurple,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text(text = placeholder, color = Color.Gray) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = lightPurple
                )
            },
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            /*colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedContainerColor = fieldBackground,
                unfocusedContainerColor = fieldBackground,
                unfocusedBorderColor = fieldBorder,
                focusedBorderColor = lightPurple,
                focusedTextColor = darkPurple,
                unfocusedTextColor = darkPurple
            )*/
        )
    }
}