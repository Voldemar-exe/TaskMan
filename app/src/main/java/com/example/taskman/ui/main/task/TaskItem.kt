package com.example.taskman.ui.main.task

import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskType
import com.example.taskman.ui.theme.Gray
import com.example.taskman.ui.theme.Orange
import com.example.taskman.ui.utils.ItemIcon
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
        else -> Pair(
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(taskDateMidnight),
            Color.Unspecified
        )
    }
}

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: MyTask,
    selected: Boolean = false,
    isCompleted: Boolean = false,
    onCheckClick: (MyTask) -> Unit
) {
    val (dateText, textColor) = remember(task.date, isCompleted) {
        getRemainingTimeInfo(if (isCompleted) 0L else task.date)
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
        overlineContent = {
            Text(text = dateText, color = textColor)
        },
        supportingContent = {
            Text(text = TaskType.valueOf(task.type).ru)
        },
        leadingContent = {
            Icon(
                modifier = Modifier.size(36.dp),
                painter = painterResource(ItemIcon.valueOf(task.icon).id),
                tint = Color(task.color),
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
