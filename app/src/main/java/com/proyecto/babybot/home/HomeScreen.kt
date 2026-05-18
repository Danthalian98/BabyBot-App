package com.proyecto.babybot.home

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.proyecto.babybot.data.local.model.ActivityRecord
import com.proyecto.babybot.ui.components.ActivityDetailDialog
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.BabyChangingStation
import androidx.compose.ui.platform.LocalContext
import com.proyecto.babybot.notifications.SessionNotificationHelper
import com.proyecto.babybot.ui.components.AppSectionHeader
import com.proyecto.babybot.ui.components.HeaderVariant
import com.proyecto.babybot.navigation.Routes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.proyecto.babybot.ui.components.ActivityCard
import com.proyecto.babybot.ui.components.ActivityCardMode
import com.proyecto.babybot.ui.components.BabyRegisterContent
import com.proyecto.babybot.ui.components.QuickRegisterButton
import com.proyecto.babybot.ui.components.MealRegisterDialog
import com.proyecto.babybot.ui.components.DiaperRegisterDialog
import com.proyecto.babybot.ui.components.HeaderStatusCard
import com.proyecto.babybot.ui.components.SleepRegisterDialog
import com.proyecto.babybot.ui.theme.NavTopColorLight
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun HomeScreen(
    navController: NavHostController,
    rootNavController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.pendingMessage) {
        state.pendingMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumePendingMessage()
        }
    }

    LaunchedEffect(Unit) {
        Log.d("NAVIGATION", "Estoy en HOME")
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    val context = LocalContext.current

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == SessionNotificationHelper.ACTION_SESSION_CHANGED) {
                    viewModel.loadHomeData(showFullScreenLoader = false)
                }
            }
        }

        val filter = IntentFilter(SessionNotificationHelper.ACTION_SESSION_CHANGED)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.registerReceiver(
                context,
                receiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        } else {
            ContextCompat.registerReceiver(
                context,
                receiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }

        onDispose {
            runCatching { context.unregisterReceiver(receiver) }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadHomeData(showFullScreenLoader = false)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NavTopColorLight)
                }
            } else if (!state.hasBaby) {
                BabyRegisterContent(
                    onNotificationsClick = {
                        navController.navigate(Routes.NOTIFICATIONS)
                    },
                    onSettingsClick = {
                        navController.navigate(Routes.SETTINGS)
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
                    onAccountClick = {
                        navController.navigate(Routes.SETTINGS_ACCOUNT)
                    },
                    onBabyInfoClick = {
                        navController.navigate(Routes.createEditAccountInfoRoute(1))
                    },
                    onSettingsClick = {
                        navController.navigate(Routes.SETTINGS)
                    },
                    onNotificationsClick = {
                        navController.navigate(Routes.NOTIFICATIONS)
                    },
                    onLogoutClick = {
                        viewModel.logout()
                        rootNavController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onMealClick = viewModel::openMealDialog,
                    onDiaperClick = viewModel::openDiaperDialog,
                    onSleepClick = viewModel::openSleepDialog,
                    onQuickFinishMeal = viewModel::quickFinishMealTimer,
                    onQuickFinishSleep = viewModel::quickFinishSleepTimer,
                    onActivityClick = { activity ->
                        viewModel.openActivityDetail(activity.record)
                    }
                )

                if (state.showMealDialog) {
                    MealRegisterDialog(
                        onDismiss = viewModel::closeMealDialog,
                        onSave = viewModel::saveMeal,
                        activeStartMillis = state.activeMealStartMillis,
                        onStartTimer = viewModel::startMealTimer,
                        onFinishTimer = viewModel::finishMealTimer,
                        onCancelTimer = viewModel::cancelMealTimer
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
                        onSave = viewModel::saveSleep,
                        activeStartMillis = state.activeSleepStartMillis,
                        onStartTimer = viewModel::startSleepTimer,
                        onFinishTimer = viewModel::finishSleepTimer,
                        onCancelTimer = viewModel::cancelSleepTimer
                    )
                }

                state.selectedActivity?.let { record ->
                    if (state.isEditingActivity) {
                        when (record) {
                            is ActivityRecord.Meal -> {
                                MealRegisterDialog(
                                    initialMeal = record.meal,
                                    onDismiss = viewModel::closeActivityDetail,
                                    onSave = { updated ->
                                        viewModel.updateActivity(ActivityRecord.Meal(updated))
                                    },
                                    activeStartMillis = null,
                                    onStartTimer = {},
                                    onFinishTimer = { _, _, _, _, _, _ -> },
                                    onCancelTimer = {}
                                )
                            }

                            is ActivityRecord.Diaper -> {
                                DiaperRegisterDialog(
                                    initialDiaper = record.diaper,
                                    onDismiss = viewModel::closeActivityDetail,
                                    onSave = { updated ->
                                        viewModel.updateActivity(ActivityRecord.Diaper(updated))
                                    }
                                )
                            }

                            is ActivityRecord.Sleep -> {
                                SleepRegisterDialog(
                                    initialSleep = record.sleep,
                                    onDismiss = viewModel::closeActivityDetail,
                                    onSave = { updated ->
                                        viewModel.updateActivity(ActivityRecord.Sleep(updated))
                                    },
                                    activeStartMillis = null,
                                    onStartTimer = {},
                                    onFinishTimer = { _, _, _, _ -> },
                                    onCancelTimer = {}
                                )
                            }
                        }
                    } else {
                        ActivityDetailDialog(
                            record = record,
                            onDismiss = viewModel::closeActivityDetail,
                            onEdit = viewModel::startEditingSelectedActivity,
                            onDelete = {
                                viewModel.deleteActivity(record)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeMainContent(
    state: HomeState,
    onAccountClick: () -> Unit,
    onBabyInfoClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onMealClick: () -> Unit,
    onDiaperClick: () -> Unit,
    onSleepClick: () -> Unit,
    onQuickFinishMeal: () -> Unit,
    onQuickFinishSleep: () -> Unit,
    onActivityClick: (ActivityData) -> Unit
)  {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        HomeHeader(
            state = state,
            onAccountClick = onAccountClick,
            onBabyInfoClick = onBabyInfoClick,
            onSettingsClick = onSettingsClick,
            onNotificationsClick = onNotificationsClick,
            onLogoutClick = onLogoutClick
        )
        ActiveSessionSection(
            state = state,
            onMealClick = onMealClick,
            onSleepClick = onSleepClick,
            onQuickFinishMeal = onQuickFinishMeal,
            onQuickFinishSleep = onQuickFinishSleep
        )
        QuickRegisterSection(
            onMealClick = onMealClick,
            onDiaperClick = onDiaperClick,
            onSleepClick = onSleepClick
        )
        DailySummarySection(state.summary)
        RecentActivitiesSection(
            activities = state.recentActivities,
            onActivityClick = onActivityClick
        )
    }
}

@Composable
fun HomeHeader(
    state: HomeState,
    onAccountClick: () -> Unit,
    onBabyInfoClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val activeMealElapsed = rememberLiveElapsedTime(state.activeMealStartMillis)
    val activeSleepElapsed = rememberLiveElapsedTime(state.activeSleepStartMillis)

    val statusText = when {
        state.activeMealStartMillis != null ->
            "🍼 Lactancia en curso · ${formatElapsed(activeMealElapsed)}"

        state.activeSleepStartMillis != null ->
            "😴 Sueño en curso · ${formatElapsed(activeSleepElapsed)}"

        state.nextActivityTitle.isBlank() ->
            "Sin próxima actividad programada"

        else ->
            "Próximo: ${state.nextActivityTitle} ${state.nextActivityTime}"
    }

    AppSectionHeader(
        title = state.babyName,
        subtitle = state.babyAge,
        variant = HeaderVariant.HOME,
        onNotificationsClick = onNotificationsClick,
        onSettingsClick = onSettingsClick,
        onLeadingClick = {
            expanded = true
        },
        leadingDropdownContent = {
            HomeAccountDropdownMenu(
                expanded = expanded,
                onDismiss = { expanded = false },
                onAccountClick = {
                    expanded = false
                    onAccountClick()
                },
                onBabyInfoClick = {
                    expanded = false
                    onBabyInfoClick()
                },
                onSettingsClick = {
                    expanded = false
                    onSettingsClick()
                },
                onLogoutClick = {
                    expanded = false
                    onLogoutClick()
                }
            )
        },
        bottomContent = {
            HeaderStatusCard(statusText)
        }
    )
}

@Composable
fun QuickRegisterSection(
    onMealClick: () -> Unit,
    onDiaperClick: () -> Unit,
    onSleepClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = "Registrar actividad",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickRegisterButton(
                text = "Comida",
                icon = Icons.Outlined.Restaurant,
                onClick = onMealClick
            )

            QuickRegisterButton(
                text = "Pañal",
                icon = Icons.Outlined.BabyChangingStation,
                onClick = onDiaperClick
            )

            QuickRegisterButton(
                text = "Sueño",
                icon = Icons.Outlined.Bedtime,
                onClick = onSleepClick
            )
        }
    }
}

@Composable
fun DailySummarySection(summary: List<SummaryData>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Registro del día",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(14.dp))

            summary.forEachIndexed { index, item ->
                SummaryItem(
                    title = item.title,
                    value = item.value
                )

                if (index != summary.lastIndex) {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
fun RecentActivitiesSection(
    activities: List<ActivityData>,
    onActivityClick: (ActivityData) -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Actividades recientes",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (activities.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Text(
                    text = "No hay actividades aún",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)
                )
            }
        } else {
            activities.forEachIndexed { index, activity ->
                HomeActivityCard(
                    activity = activity,
                    onClick = { onActivityClick(activity) }
                )

                if (index != activities.lastIndex) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun HomeActivityCard(
    activity: ActivityData,
    onClick: (() -> Unit)? = null
) {
    ActivityCard(
        icon = activity.icon,
        title = activity.title,
        description = activity.description,
        time = activity.time,
        mode = ActivityCardMode.COMPACT,
        onClick = onClick
    )
}

@Composable
fun ActiveSessionSection(
    state: HomeState,
    onMealClick: () -> Unit,
    onSleepClick: () -> Unit,
    onQuickFinishMeal: () -> Unit,
    onQuickFinishSleep: () -> Unit
) {
    val activeMealElapsed = rememberLiveElapsedTime(state.activeMealStartMillis)
    val activeSleepElapsed = rememberLiveElapsedTime(state.activeSleepStartMillis)

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (state.activeMealStartMillis != null) {
            ActiveSessionCard(
                emoji = "🍼",
                title = "Lactancia en curso",
                subtitle = "Tiempo: ${formatElapsed(activeMealElapsed)}",
                primaryLabel = "Abrir",
                secondaryLabel = "Finalizar",
                onPrimaryClick = onMealClick,
                onSecondaryClick = onQuickFinishMeal
            )
        }

        if (state.activeSleepStartMillis != null) {
            ActiveSessionCard(
                emoji = "😴",
                title = "Sueño en curso",
                subtitle = "Tiempo: ${formatElapsed(activeSleepElapsed)}",
                primaryLabel = "Abrir",
                secondaryLabel = "Finalizar",
                onPrimaryClick = onSleepClick,
                onSecondaryClick = onQuickFinishSleep
            )
        }
    }
}

@Composable
fun ActiveSessionCard(
    emoji: String,
    title: String,
    subtitle: String,
    primaryLabel: String,
    secondaryLabel: String,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = emoji,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onPrimaryClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = primaryLabel,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Button(
                    onClick = onSecondaryClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = secondaryLabel,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun rememberLiveElapsedTime(startMillis: Long?): Long {
    var elapsed by remember(startMillis) { mutableStateOf(0L) }

    LaunchedEffect(startMillis) {
        while (startMillis != null) {
            elapsed = System.currentTimeMillis() - startMillis
            kotlinx.coroutines.delay(1000)
        }
    }

    return elapsed
}

fun formatElapsed(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

@Composable
private fun HomeAccountDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onAccountClick: () -> Unit,
    onBabyInfoClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        HomeDropdownItem(
            icon = Icons.Outlined.AccountCircle,
            text = "Cuenta",
            onClick = onAccountClick
        )

        HomeDropdownItem(
            icon = Icons.Outlined.ChildCare,
            text = "Información del bebé",
            onClick = onBabyInfoClick
        )

        HomeDropdownItem(
            icon = Icons.Outlined.Settings,
            text = "Ajustes",
            onClick = onSettingsClick
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        )

        HomeDropdownItem(
            icon = Icons.Outlined.Logout,
            text = "Cerrar sesión",
            onClick = onLogoutClick,
            isDanger = true
        )
    }
}

@Composable
private fun HomeDropdownItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    isDanger: Boolean = false
) {
    val contentColor = if (isDanger) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    DropdownMenuItem(
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor
            )
        },
        onClick = onClick
    )
}

fun formatElapsedAsMinutes(ms: Long): String {
    val totalMinutes = (ms / 60000L).coerceAtLeast(0L)
    return "$totalMinutes min"
}