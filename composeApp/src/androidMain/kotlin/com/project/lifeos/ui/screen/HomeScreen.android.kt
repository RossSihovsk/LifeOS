package com.project.lifeos.ui.screen


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.project.lifeos.R
import com.project.lifeos.data.Task
import com.project.lifeos.data.TaskStatus
import com.project.lifeos.di.AppModule
import com.project.lifeos.ui.view.CalendarView
import com.project.lifeos.utils.generateTasks
import com.project.lifeos.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random
import kotlin.text.Typography.section

@Composable
actual fun HomeScreenContent(
    viewModel: TaskViewModel,
    navigator: Navigator?
) {
    val scrollState = rememberScrollState()
    val ongoingTasksExpanded = remember { mutableStateOf(true) } // Track expansion state
    val completedTasksExpanded = remember { mutableStateOf(false) } // Track expansion state

    val EXPANSION_ANIMATION_DURATION = 300

    val transition = updateTransition(targetState = ongoingTasksExpanded, label = null)

    Column(modifier = Modifier.fillMaxWidth().verticalScroll(state = scrollState)) {


        CalendarView(modifier = Modifier.fillMaxWidth(), onDateClickListener = { date ->
            //do smth
        })

        Spacer(modifier = Modifier.height(10.dp))

        ExpandableSection(
            title = "To complete",
            content = {
                TaskItem(generateTasks(5))
            },
            isExpanded = ongoingTasksExpanded.value, // Bind state to isExpanded
            onToggleClick = { ongoingTasksExpanded.value = !ongoingTasksExpanded.value }
        )

        Spacer(modifier = Modifier.height(20.dp))

        ExpandableSection(
            title = "Completed",
            content = {
                TaskItem(generateTasks(5))
            },
            isExpanded = completedTasksExpanded.value, // Bind state to isExpanded
            onToggleClick = { completedTasksExpanded.value = !completedTasksExpanded.value }
        )

    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExpandableSection(
    title: String,
    content: @Composable () -> Unit,
    isExpanded: Boolean = false, // Optional parameter for initial state
    onToggleClick: () -> Unit,
) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleClick() },
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onToggleClick) {
                Icon(
                    painter = if (isExpanded) painterResource(id = R.drawable.expand_less) else painterResource(id = R.drawable.expand_more),
                    contentDescription = null
                )
            }
        }
        Divider(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp), color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(10.dp))
        if (isExpanded) {
            content()
        }
    }
}

@Composable
fun TaskItem(tasks: List<Task>) {
    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp)) {
        items(tasks) { task ->
            TaskItemRow(task = task) // Call the new TaskItemRow composable
        }
    }
}
@Composable
fun TaskItemRow(task: Task) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.status == TaskStatus.DONE,
            onCheckedChange = {  }
        )
        Spacer(modifier = Modifier.width(8.dp)) // Adjust spacing as needed
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = formatTime(task.time),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
fun formatTime(timeInMillis: Long): String {
    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault()) // Customize format if needed
    return formatter.format(Date(timeInMillis))
}


@Composable
@Preview
fun HomeScreenPreview() {
    HomeScreenContent(viewModel = AppModule.taskViewModel)
}