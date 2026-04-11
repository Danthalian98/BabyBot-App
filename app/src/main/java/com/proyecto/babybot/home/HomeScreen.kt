package com.proyecto.babybot.home

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.proyecto.babybot.data.local.entity.DiaperEntity
import com.proyecto.babybot.data.local.entity.MealEntity
import com.proyecto.babybot.data.local.entity.SleepEntity
import com.proyecto.babybot.navigation.Routes
import com.proyecto.babybot.ui.components.BabyRegisterContent
import com.proyecto.babybot.ui.components.QuickRegisterButton
import com.proyecto.babybot.ui.components.MealRegisterDialog
import com.proyecto.babybot.ui.components.DiaperRegisterDialog
import com.proyecto.babybot.ui.components.SleepRegisterDialog
import com.proyecto.babybot.ui.theme.BackPantallas
import com.proyecto.babybot.ui.theme.BtnTextoColorLight
import com.proyecto.babybot.ui.theme.NavTopColorLight
import com.proyecto.babybot.ui.theme.TxtColorContent
import com.proyecto.babybot.ui.theme.TxtColorDark
import com.proyecto.babybot.ui.theme.TxtColorTitle

@Composable
fun HomeScreen(
    rootNavController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("NAVIGATION", "Estoy en HOME")
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = NavTopColorLight)
        }
    } else if (!state.hasBaby) {
        BabyRegisterContent(
            onLogoutClick = {
                viewModel.logout()
                rootNavController.navigate(Routes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onSave = { name, gender, birthDate, weight, height, bloodType, pediatrician, notes, allergies ->
                viewModel.createBaby(
                    name = name,
                    gender = gender,
                    birthDate = birthDate,
                    weight = weight,
                    height = height,
                    bloodType = bloodType,
                    pediatrician = pediatrician,
                    notes = notes,
                    allergies = allergies
                )
            }
        )
    } else {
        HomeMainContent(
            state = state,
            onLogoutClick = {
                viewModel.logout()
                rootNavController.navigate(Routes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onMealClick = viewModel::openMealDialog,
            onDiaperClick = viewModel::openDiaperDialog,
            onSleepClick = viewModel::openSleepDialog
        )

        if (state.showMealDialog) {
            MealRegisterDialog(
                onDismiss = viewModel::closeMealDialog,
                onSave = viewModel::saveMeal
            )
        }

        if (state.showDiaperDialog) {
            DiaperRegisterDialog(
                onDismiss = viewModel::closeDiaperDialog,
                onSave = viewModel::saveDiaper
            )
        }

        if (state.showSleepDialog) {
            SleepRegisterDialog(
                onDismiss = viewModel::closeSleepDialog,
                onSave = viewModel::saveSleep
            )
        }
    }
}

@Composable
fun HomeMainContent(
    state: HomeState,
    onLogoutClick: () -> Unit,
    onMealClick: () -> Unit,
    onDiaperClick: () -> Unit,
    onSleepClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackPantallas)
            .verticalScroll(rememberScrollState())
    ) {
        HomeHeader(state, onLogoutClick)
        QuickRegisterSection(
            onMealClick = onMealClick,
            onDiaperClick = onDiaperClick,
            onSleepClick = onSleepClick
        )
        DailySummarySection(state.summary)
        RecentActivitiesSection(state.recentActivities)
    }
}

@Composable
fun HomeHeader(
    state: HomeState,
    onLogoutClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavTopColorLight)
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Person, contentDescription = null)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = state.babyName,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.babyAge,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }

                Row {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (state.nextActivityTitle.isBlank()) {
                        "Sin próxima actividad programada"
                    } else {
                        "Próximo: ${state.nextActivityTitle} ${state.nextActivityTime}"
                    },
                    color = Color.White,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun QuickRegisterSection(
    onMealClick: () -> Unit,
    onDiaperClick: () -> Unit,
    onSleepClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Registrar actividad",
            color = BtnTextoColorLight,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickRegisterButton("Comida", onClick = onMealClick)
            QuickRegisterButton("Pañal", onClick = onDiaperClick)
            QuickRegisterButton("Siesta", onClick = onSleepClick)
        }
    }
}

@Composable
fun DailySummarySection(summary: List<SummaryData>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
        border = BorderStroke(1.dp, Color(0xFF7EA1FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Registro del día",
                color = BtnTextoColorLight,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            summary.forEach { item ->
                SummaryItem(title = item.title, value = item.value)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun RecentActivitiesSection(activities: List<ActivityData>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Actividades recientes",
            color = BtnTextoColorLight,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (activities.isEmpty()) {
            Text(
                text = "No hay actividades aún",
                color = TxtColorDark.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
        } else {
            activities.forEach { activity ->
                ActivityCard(activity)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SummaryItem(
    title: String,
    value: String
) {
    Box {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = value,
                    fontSize = 18.sp,
                    color = TxtColorContent
                )
            }
        }

        Text(
            text = title,
            modifier = Modifier
                .padding(start = 14.dp)
                .offset(y = (-10).dp)
                .background(Color(0xFFF8F8F8)),
            fontSize = 12.sp,
            color = TxtColorTitle
        )
    }
}

@Composable
fun ActivityCard(activity: ActivityData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
        border = BorderStroke(1.dp, Color(0xFFE2DDEA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = activity.icon,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = activity.title,
                    fontSize = 16.sp,
                    color = TxtColorDark,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = activity.description,
                    fontSize = 14.sp,
                    color = TxtColorDark.copy(alpha = 0.75f)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = activity.time,
                fontSize = 14.sp,
                color = TxtColorDark
            )
        }
    }
}

