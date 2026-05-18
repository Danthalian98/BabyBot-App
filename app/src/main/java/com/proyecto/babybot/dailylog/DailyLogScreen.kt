package com.proyecto.babybot.dailylog

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.proyecto.babybot.data.local.model.ActivityRecord
import com.proyecto.babybot.ui.components.ActivityCard
import com.proyecto.babybot.ui.components.ActivityCardMode
import com.proyecto.babybot.ui.components.ActivityDetailDialog
import com.proyecto.babybot.ui.components.AppSectionHeader
import com.proyecto.babybot.ui.components.DiaperRegisterDialog
import com.proyecto.babybot.ui.components.HeaderVariant
import com.proyecto.babybot.ui.components.MealRegisterDialog
import com.proyecto.babybot.ui.components.SleepRegisterDialog
import com.proyecto.babybot.ui.theme.TxtColorDark

@Composable
fun DailyLogScreen(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    viewModel: DailyLogViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        Log.d("NAVIGATION", "Estoy en DAILY LOG")
    }

    LaunchedEffect(state.pendingMessage) {
        state.pendingMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.consumePendingMessage()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadDailyLog()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            DailyLogContent(
                state = state,
                modifier = Modifier.fillMaxSize(),
                onSettingsClick = onSettingsClick,
                onNotificationsClick = onNotificationsClick,
                onActivityClick = { activity ->
                    viewModel.openActivityDetail(activity.record)
                }
            )

            state.selectedActivity?.let { record ->
                if (state.isEditingActivity) {
                    when (record) {
                        is ActivityRecord.Meal -> {
                            MealRegisterDialog(
                                initialMeal = record.meal,
                                onDismiss = viewModel::closeActivityDetail,
                                onSave = { updatedMeal ->
                                    viewModel.updateActivity(
                                        ActivityRecord.Meal(updatedMeal)
                                    )
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
                                onSave = { updatedDiaper ->
                                    viewModel.updateActivity(
                                        ActivityRecord.Diaper(updatedDiaper)
                                    )
                                }
                            )
                        }

                        is ActivityRecord.Sleep -> {
                            SleepRegisterDialog(
                                initialSleep = record.sleep,
                                onDismiss = viewModel::closeActivityDetail,
                                onSave = { updatedSleep ->
                                    viewModel.updateActivity(
                                        ActivityRecord.Sleep(updatedSleep)
                                    )
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

@Composable
fun DailyLogContent(
    state: DailyLogState,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onActivityClick: (DailyActivity) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        AppSectionHeader(
            title = state.title,
            variant = HeaderVariant.DAILY_LOG,
            onNotificationsClick = onNotificationsClick,
            onSettingsClick = onSettingsClick,
            bottomContent = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    state.resumen.forEach { item ->
                        SummaryTopCard(
                            item = item,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {

            if (state.sections.isEmpty() || state.sections.all { it.activities.isEmpty() }) {
                item {
                    Text(
                        text = "Aún no hay actividades registradas en los últimos 7 días",
                        color = TxtColorDark.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }

            state.sections.forEach { section ->

                item {
                    DateHeader(section.date)
                }

                items(
                    items = section.activities,
                    key = { activity -> activity.id }
                ) { activity ->
                    ActivityCard(
                        icon = activityIcon(activity),
                        title = activity.title,
                        description = activity.information,
                        time = activity.time,
                        mode = ActivityCardMode.COMPACT,
                        onClick = {
                            onActivityClick(activity)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryTopCard(
    item: DailySummary,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Text(
                text = item.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun DateHeader(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.CalendarToday,
            contentDescription = null,
            tint = TxtColorDark,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            color = TxtColorDark,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.width(8.dp))

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = TxtColorDark.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun ActivityLogCard(
    activity: DailyActivity,
    onClick: (() -> Unit)? = null
) {
    ActivityCard(
        icon = activityIcon(activity),
        title = activity.title,
        description = activity.information,
        time = activity.time,
        mode = ActivityCardMode.DETAILED,
        modifier = Modifier.padding(bottom = 12.dp),
        onClick = onClick
    )
}