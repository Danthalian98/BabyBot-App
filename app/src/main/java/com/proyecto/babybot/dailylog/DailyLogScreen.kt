package com.proyecto.babybot.dailylog

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.proyecto.babybot.ui.theme.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.proyecto.babybot.ui.components.ActivityCard
import com.proyecto.babybot.ui.components.ActivityCardMode
import com.proyecto.babybot.ui.components.AppSectionHeader
import com.proyecto.babybot.ui.components.HeaderVariant

@Composable
fun DailyLogScreen(
    modifier: Modifier = Modifier,
    viewModel: DailyLogViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        Log.d("NAVIGATION", "Estoy en DAILY LOG")
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

    DailyLogContent(state = state, modifier = modifier)
}

@Composable
fun DailyLogContent(
    state: DailyLogState,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            //.verticalScroll(rememberScrollState())
    ) {

        // HEADER
        AppSectionHeader(
            title = state.title,
            variant = HeaderVariant.DAILY_LOG,
            onNotificationsClick = { },
            onSettingsClick = {
                Log.d("NAVIGATION", "Click en Ajustes")
            },
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
            }
        )

        // LISTA
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
                    key = { "${it.title}_${it.time}_${it.information}" }
                ) { activity ->
                    ActivityLogCard(
                        activity = activity,
                        onClick = { }
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
                item.icon,
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
            Icons.Outlined.CalendarToday,
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