package com.proyecto.babybot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.proyecto.babybot.R

enum class HeaderVariant {
    HOME,
    DAILY_LOG,
    FORUM,
    CHATBOT,
    SIMPLE
}

@Composable
fun AppSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    variant: HeaderVariant = HeaderVariant.SIMPLE,
    showNotifications: Boolean = true,
    showSettings: Boolean = true,
    onNotificationsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    bottomContent: (@Composable () -> Unit)? = null
) {
    val shape = when (variant) {
        HeaderVariant.HOME -> RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
        else -> RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundBrush)
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                HeaderLeadingContent(
                    title = title,
                    subtitle = subtitle,
                    variant = variant
                )

                HeaderActions(
                    showNotifications = showNotifications,
                    showSettings = showSettings,
                    onNotificationsClick = onNotificationsClick,
                    onSettingsClick = onSettingsClick,
                    actionText = actionText,
                    onActionClick = onActionClick
                )
            }

            if (bottomContent != null) {
                Spacer(modifier = Modifier.height(18.dp))
                bottomContent()
            }
        }
    }
}

@Composable
private fun HeaderActions(
    showNotifications: Boolean,
    showSettings: Boolean,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    actionText: String?,
    onActionClick: (() -> Unit)?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (!actionText.isNullOrBlank() && onActionClick != null) {
            Button(
                onClick = onActionClick,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 14.dp,
                    vertical = 10.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        if (showNotifications) {
            HeaderIconButton(
                icon = Icons.Filled.Notifications,
                contentDescription = "Notificaciones",
                onClick = onNotificationsClick
            )
        }

        if (showSettings) {
            HeaderIconButton(
                icon = Icons.Filled.Settings,
                contentDescription = "Ajustes",
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
private fun HeaderIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier.size(42.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun HeaderLeadingContent(
    title: String,
    subtitle: String?,
    variant: HeaderVariant
) {
    when (variant) {
        HeaderVariant.HOME -> {
            val imageRes = R.drawable.baby_boy

            HeaderWithImage(
                imageRes = imageRes,
                title = title,
                subtitle = subtitle
            )
        }

        HeaderVariant.CHATBOT -> {
            HeaderWithImage(
                imageRes = R.drawable.ic_app,
                title = title,
                subtitle = subtitle
            )
        }

        HeaderVariant.DAILY_LOG,
        HeaderVariant.FORUM,
        HeaderVariant.SIMPLE -> {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                if (!subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderWithImage(
    imageRes: Int,
    title: String,
    subtitle: String?
) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        // 👇 CONTENEDOR DE LA IMAGEN (esto es lo importante)
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape
                )
                .padding(3.dp)
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f)
                )
            }
        }
    }
}

@Composable
fun HeaderStatusCard(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        )
    }
}

@Composable
private fun HeaderWithCircleIcon(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    highlighted: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = if (highlighted) 0.95f else 0.22f),
            modifier = Modifier.size(58.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .then(
                        if (highlighted) {
                            Modifier.border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                shape = CircleShape
                            )
                        } else {
                            Modifier
                        }
                    )
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (highlighted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    },
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f)
                )
            }
        }
    }
}