package com.example.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.shared.SystemIcon
import com.example.shared.TaskType
import com.example.shared.UserTask
import com.example.ui.ColorMapper
import com.example.ui.Gray
import com.example.ui.IconMapper
import com.example.ui.Orange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private fun getRemainingTimeInfo(taskTimestamp: Long): Pair<String, Color> {
    if (taskTimestamp == 0L) return Pair("Выполнено", Gray)
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val currentDateMidnight = calendar.timeInMillis

    calendar.timeInMillis = taskTimestamp
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val taskDateMidnight = calendar.timeInMillis

    val diffDays = (taskDateMidnight - currentDateMidnight) / (24 * 60 * 60 * 1000)

    return when {
        diffDays < 0 -> Pair("Просрочено", Color.Red)
        diffDays == 0L -> Pair("Сегодня", Orange)
        diffDays == 1L -> Pair("Завтра", Orange)
        diffDays in 2L..4L -> Pair("$diffDays дня", Orange)
        else -> {
            val taskCal = Calendar.getInstance().apply { timeInMillis = taskDateMidnight }
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val taskYear = taskCal.get(Calendar.YEAR)
            val format = if (currentYear == taskYear) "dd MMM" else "dd MMM yyyy"
            val sdf = SimpleDateFormat(format, Locale("ru", "RU"))
            Pair(
                sdf.format(taskDateMidnight),
                Color.Unspecified
            )
        }
    }
}

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: UserTask,
    selected: Boolean = false,
    isCompleted: Boolean = false,
    onCheckClick: (UserTask) -> Unit
) {
    val (dateText, dateColor) = rememberSaveable(task.date, isCompleted) {
        getRemainingTimeInfo(if (isCompleted) 0L else task.date)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {

    }
    ListItem(
        modifier = modifier
            .alpha(if (isCompleted) 0.6f else 1f),
        headlineContent = {
            Text(
                text = task.name,
                style = onCompletedText(isCompleted)
            )
        },
        supportingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.size(13.dp),
                    painter = painterResource(
                        IconMapper.itemIconToDrawable(SystemIcon.CalendarEvent)
                    ),
                    tint =
                        if (dateColor == Color.Unspecified) LocalContentColor.current
                        else dateColor,
                    contentDescription = null
                )
                Text(text = dateText, color = dateColor)
                Spacer(Modifier.padding(horizontal = 2.dp))
                Icon(
                    modifier = Modifier.size(13.dp),
                    painter = painterResource(
                        IconMapper.itemIconToDrawable(SystemIcon.Tag)
                    ),
                    contentDescription = null
                )
                Text(text = TaskType.valueOf(task.type).ru)
            }
        },
        leadingContent = {
            Icon(
                modifier = Modifier.size(36.dp),
                painter = painterResource(IconMapper.itemIconToDrawable(task.icon)),
                tint = ColorMapper.mapToColor(task.color),
                contentDescription = null
            )
        },
        trailingContent = {
            RadioButton(
                selected = isCompleted || selected,
                onClick = { onCheckClick(task) }
            )
        }
    )
    HorizontalDivider()
}

@Composable
private fun onCompletedText(isCompleted: Boolean) =
    if (isCompleted) {
        MaterialTheme.typography.bodyLarge.copy(
            textDecoration = TextDecoration.LineThrough
        )
    } else {
        MaterialTheme.typography.bodyLarge
    }
