package com.project.lifeos.ui.screen


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import com.project.lifeos.R
import com.project.lifeos.data.Priority
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.Task
import com.project.lifeos.data.TaskStatus
import com.project.lifeos.ui.view.CalendarView
import com.project.lifeos.utils.formatTime
import com.project.lifeos.viewmodel.HomeScreenViewModel
import com.project.lifeos.viewmodel.HomeUiState

const val TO_COMPLETE_TITLE = "To complete"
const val COMPLETED_TITLE = "Completed"
private val logger = Logger.withTag("HomeScreenContent")

@Composable
actual fun HomeScreenContent(
    viewModel: HomeScreenViewModel,
    navigator: Navigator?
) {
    val scrollState = rememberScrollState()
    val ongoingTasksExpanded = remember { mutableStateOf(true) } // Track expansion state
    val completedTasksExpanded = remember { mutableStateOf(false) } // Track expansion state

    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().verticalScroll(state = scrollState)) {

        CalendarView(
            modifier = Modifier.fillMaxWidth(),
            calendarUiModel = viewModel.calendarUiModel,
            onDateClickListener = { date ->
                viewModel.onDateClicked(date)
            })

        Spacer(modifier = Modifier.height(10.dp))

        when (val state = uiState) {
            is HomeUiState.Loading -> {
                //Display some loading indicator
            }

            is HomeUiState.NoTaskForSelectedDate -> {
                DisplayNoDataImage()
            }

            is HomeUiState.TaskUpdated -> {
                logger.i("UpdateTaskStatus")
                TaskExpandedSection(
                    title = TO_COMPLETE_TITLE,
                    content = {
                        TasksContent(state.unCompletedTasks, onTaskStatusChanged = viewModel::onTaskStatusChanged)
                    },
                    isExpanded = ongoingTasksExpanded.value, // Bind state to isExpanded
                    onToggleClick = { ongoingTasksExpanded.value = !ongoingTasksExpanded.value }
                )

                Spacer(modifier = Modifier.height(20.dp))

                TaskExpandedSection(
                    title = COMPLETED_TITLE,
                    content = {
                        TasksContent(state.completedTasks, onTaskStatusChanged = viewModel::onTaskStatusChanged)
                    },
                    isExpanded = completedTasksExpanded.value, // Bind state to isExpanded
                    onToggleClick = { completedTasksExpanded.value = !completedTasksExpanded.value }
                )
            }
        }
    }
    LaunchedEffect(Unit) {
        // Call your ViewModel function
        viewModel.init()
    }
}

@Composable
fun DisplayNoDataImage() {
    Column(
        modifier = Modifier
            .padding(top = 60.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "There is nothing in TODO list for this day",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(60.dp))
        Image(
            painter = painterResource(id = R.drawable.no_data), // Load the image from the drawable folder
            contentDescription = "No data available",
            modifier = Modifier.fillMaxWidth(), // Adjust the size or any modifiers as needed
            contentScale = ContentScale.Fit // Adjust content scaling if necessary
        )
    }
}

@Composable
fun TaskExpandedSection(
    title: String,
    content: @Composable () -> Unit,
    isExpanded: Boolean = false,
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
        Divider(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            color = Color.LightGray,
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(10.dp))

        AnimatedVisibility(visible = isExpanded) {
            content()
        }
    }
}

@Composable
fun TasksContent(tasks: List<Task>, onTaskStatusChanged: (status: Boolean, task: Task) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp)) {
        items(tasks) { task ->
            TaskCard(task = task, onTaskStatusChanged = onTaskStatusChanged)
            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}

@Composable
fun TaskCard(task: Task, onTaskStatusChanged: (status: Boolean, task: Task) -> Unit) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        MainInfoCard(task = task, onTaskStatusChanged = onTaskStatusChanged)
        Spacer(modifier = Modifier.height(4.dp))
        TaskCardDescription(description = task.description)
        Spacer(modifier = Modifier.height(6.dp))
        AdditionalInfoCard(task = task)
    }
}

@Composable
fun MainInfoCard(task: Task, onTaskStatusChanged: (status: Boolean, task: Task) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            modifier = Modifier.height(20.dp),
            checked = task.status == TaskStatus.DONE,
            onCheckedChange = { status ->
                onTaskStatusChanged(status, task)
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f)
        )
        task.time?.let { time ->
            Text(
                text = formatTime(time),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun TaskCardDescription(description: String?) {
    description?.let { taskDescription ->
        Text(
            text = taskDescription,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 15.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun AdditionalInfoCard(task: Task) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (task.reminder != Reminder.NONE) {
            with(task.reminder) {
                TaskInfoItem {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                    )
                    Image(
                        painter = painterResource(id = getReminderImageId(this)), // Replace with your image resource
                        contentDescription = "Reminder Icon", // Content description for accessibility
                        modifier = Modifier.size(25.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))
            }
        }

        if (task.checkItems.isNotEmpty()) {
            TaskInfoItem {
                Text(
                    text = "Check list:",
                    style = MaterialTheme.typography.bodySmall,
                )
                Icon(
                    modifier = Modifier.size(25.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.checked_items),
                    contentDescription = null,
                )
            }

            Spacer(modifier = Modifier.width(10.dp))
        }

        if (task.priority != Priority.NO_PRIORITY) {
            with(task.priority) {
                TaskInfoItem {
                    Text(
                        text = "Priority: ",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
fun TaskInfoItem(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.background(Color.Transparent).wrapContentWidth().height(28.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
    ) {
        Row(
            modifier = Modifier.wrapContentSize().padding(horizontal = 3.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}


private fun getReminderImageId(reminder: Reminder): Int {
    return when (reminder) {
        Reminder.NONE -> throw IllegalArgumentException("Do not show if there is no reminder")
        Reminder.ON_TIME -> R.drawable.on_time
        Reminder.FIVE_MIN_BEFORE -> R.drawable.five_minutes_before
        Reminder.THIRTY_MIN_BEFORE -> R.drawable.thirty_minites_before
        Reminder.HOUR_BEFORE -> R.drawable.one_hour_before
        Reminder.DAY_BEFORE -> R.drawable.one_day_before
    }
}

@Composable
@Preview
fun HomeScreenPreview() {
//    HomeScreenContent()
}