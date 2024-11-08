package com.project.lifeos.ui.screen


import androidx.compose.animation.AnimatedVisibility

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.Task
import com.project.lifeos.data.TaskStatus
import com.project.lifeos.ui.view.calendar.CalendarUiModel
import com.project.lifeos.utils.formatTime
import com.project.lifeos.viewmodel.HomeScreenViewModel
import com.project.lifeos.viewmodel.HomeUiState
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

const val TO_COMPLETE_TITLE = "To complete"
const val COMPLETED_TITLE = "Completed"

@Composable
actual fun HomeScreenContent(
    viewModel: HomeScreenViewModel,
    navigator: Navigator?
) {
    val logger = Logger.withTag("HomeScreenContent")
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
//        Image(
//            painter = painterResource(id = R.drawable.no_data), // Load the image from the drawable folder
//            contentDescription = "No data available",
//            modifier = Modifier.fillMaxWidth(), // Adjust the size or any modifiers as needed
//            contentScale = ContentScale.Fit // Adjust content scaling if necessary
//        )
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
                    Icons.Rounded.DirectionsCar, contentDescription = null
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
        //Title and checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.status == TaskStatus.DONE,
                onCheckedChange = { status -> onTaskStatusChanged(status, task) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            task.time?.let { time ->
                Text(
                    text = formatTime(time),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        //description
        task.description?.let { taskDescription ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = taskDescription,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(0.8f)
                )

                //Reminder
                if (task.reminder != Reminder.NONE) {
                    Text(
                        text = "Remind:",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.width(5.dp))
//                    Image(
//                        painter = painterResource(id = getReminderImageId(task.reminder)), // Replace with your image resource
//                        contentDescription = "Reminder Icon", // Content description for accessibility
//                        modifier = Modifier.size(30.dp)
//                    )
                }
            }
        }

        //priority
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.priority.title,
                style = MaterialTheme.typography.bodySmall,
                color = task.priority.color,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

//private fun getReminderImageId(reminder: Reminder): Int {
//    return when (reminder) {
//        Reminder.NONE -> throw IllegalArgumentException("Do not show if there is no reminder")
//        Reminder.ON_TIME -> R.drawable.on_time
//        Reminder.FIVE_MIN_BEFORE -> R.drawable.five_minutes_before
//        Reminder.THIRTY_MIN_BEFORE -> R.drawable.thirty_minites_before
//        Reminder.HOUR_BEFORE -> R.drawable.one_hour_before
//        Reminder.DAY_BEFORE -> R.drawable.one_day_before
//    }
//}
//
@Composable
fun CalendarView(
    modifier: Modifier = Modifier,
    calendarUiModel: CalendarUiModel,
    onDateClickListener: (CalendarUiModel.Date) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Header(data = calendarUiModel)
        Content(data = calendarUiModel, onDateClickListener = { date ->
            onDateClickListener(date)
        })
    }
}

@Composable
fun Header(data: CalendarUiModel) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = data.selectedDate.date.month.name,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (data.selectedDate.isToday) {
                "Today"
            } else {
                data.selectedDate.date.format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
                )
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun Content(data: CalendarUiModel, onDateClickListener: (CalendarUiModel.Date) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(items = data.visibleDates) { date ->
            ContentItem(date, onDateClickListener)
        }
    }
}

@Composable
fun ContentItem(date: CalendarUiModel.Date, onDateClickListener: (CalendarUiModel.Date) -> Unit) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .clickable {
                onDateClickListener(date)
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
    ) {
        Column(
            modifier = Modifier
                .width(45.dp)
                .padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            Text(
                text = date.day, // day "Mon", "Tue"
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = date.date.dayOfMonth.toString(), // date "15", "16",
                color = if (date.isSelected) Color.White else Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally).drawBehind {
                    if (date.isSelected) drawCircle(color = primaryColor, radius = (this.size.height / 1.3).toFloat())
                }
            )
        }
    }
}
