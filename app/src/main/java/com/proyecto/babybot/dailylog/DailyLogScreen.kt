package com.proyecto.babybot.dailylog

import android.util.Log
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

@Composable
fun DailyLogScreen(
    modifier: Modifier = Modifier,
    viewModel: DailyLogViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("NAVIGATION", "Estoy en DAILY LOG")
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
            .background(BackPantallas)
            //.verticalScroll(rememberScrollState())
    ) {

        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavTopColorLight)
                .padding(24.dp)
        ) {
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = state.title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = {

                        Log.d("NAVIGATION", "Click en Ajustes")
                    }) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = "Ajustes",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    state.resumen.forEach { item ->
                        SummaryTopCard(item)
                    }
                }
            }
        }

        // LISTA
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {

            state.sections.forEach { section ->

                item {
                    DateHeader(section.date)
                }

                items(section.activities) { activity ->
                    ActivityLogCard(activity)
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
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp)) // Recorta el efecto click a los bordes
            .background(Color.White.copy(alpha = 0.2f))
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(item.icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(item.value, color = Color.White, fontWeight = FontWeight.Bold)
        Text(item.label, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
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
fun ActivityLogCard(activity: DailyActivity) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(activity.type.color, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = activity.type.icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = activity.title,
                    color = TxtColorDark,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = activity.information,
                    color = TxtColorDark.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }

            Text(
                text = activity.time,
                fontWeight = FontWeight.Medium,
                color = TxtColorDark
            )
        }
    }
}