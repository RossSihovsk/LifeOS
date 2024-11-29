package com.project.lifeos.ui.screen


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import com.project.lifeos.data.Priority
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.Task
import com.project.lifeos.ui.view.calendar.CalendarUiModel

import com.project.lifeos.utils.formatTime
import com.project.lifeos.viewmodel.HomeScreenViewModel
import com.project.lifeos.viewmodel.HomeUiState
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

const val TO_COMPLETE_TITLE = "To complete"
const val COMPLETED_TITLE = "Completed"

@Composable
actual fun HomeScreenContent(
    viewModel: HomeScreenViewModel, navigator: Navigator?
) {
    val logger = Logger.withTag("HomeScreenContent")
    val scrollState = rememberScrollState()
    val ongoingTasksExpanded = remember { mutableStateOf(true) } // Track expansion state
    val completedTasksExpanded = remember { mutableStateOf(false) } // Track expansion state

    val uiState by viewModel.uiState.collectAsState()
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource("bg.png"),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize().graphicsLayer(alpha = 0.5f),
            contentScale = ContentScale.FillBounds // Scales the image to cover the whole background
        )
    }
Box{
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(state = scrollState)) {

        CalendarView(modifier = Modifier.fillMaxWidth(),
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
                TaskExpandedSection(title = TO_COMPLETE_TITLE, content = {

                    TasksContent(state.unCompletedTasks, onTaskStatusChanged = viewModel::onTaskStatusChanged,completed = false)

                }, isExpanded = ongoingTasksExpanded.value, // Bind state to isExpanded
                    onToggleClick = { ongoingTasksExpanded.value = !ongoingTasksExpanded.value })

                Spacer(modifier = Modifier.height(20.dp))

                TaskExpandedSection(title = COMPLETED_TITLE, content = {
                    TasksContent(state.completedTasks, onTaskStatusChanged = viewModel::onTaskStatusChanged,completed = false)
                }, isExpanded = completedTasksExpanded.value, // Bind state to isExpanded
                    onToggleClick = { completedTasksExpanded.value = !completedTasksExpanded.value })
            }
        }
    }}
    LaunchedEffect(Unit) {
        // Call your ViewModel function
        viewModel.init()
    }
}

@Composable
fun DisplayNoDataImage() {
    Column(
        modifier = Modifier.padding(top = 60.dp, start = 20.dp, end = 20.dp).fillMaxWidth().fillMaxHeight(),
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
            painter = painterResource("no_data.png"), // Load the image from the drawable folder
            contentDescription = "No data available",
            modifier = Modifier.width(500.dp), // Adjust the size or any modifiers as needed
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
            modifier = Modifier.fillMaxWidth().clickable { onToggleClick() }, horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onToggleClick) {
                if (isExpanded) Icon(
                    Icons.Rounded.ExpandLess, contentDescription = null
                ) else Icon(
                    Icons.Rounded.ExpandMore, contentDescription = null
                )
            }
        }
        Divider(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp), color = Color.LightGray, thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(10.dp))

        AnimatedVisibility(visible = isExpanded) {
            content()
        }
    }
}

@Composable
fun TasksContent(tasks: List<Task>, onTaskStatusChanged: (status: Boolean, task: Task) -> Unit, completed: Boolean) {
    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp)) {
        items(tasks) { task ->
            TaskCard(task = task, onTaskStatusChanged = onTaskStatusChanged, completed)
            HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            thickness = 1.dp,
            color = Color.Black
            )
            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}

@Composable
fun TaskCard(task: Task, onTaskStatusChanged: (status: Boolean, task: Task) -> Unit, completed: Boolean) {
    Column(
        modifier = Modifier.wrapContentSize().padding(horizontal = 16.dp), horizontalAlignment = Alignment.Start
    ) {
        MainInfoCard(task = task, onTaskStatusChanged = onTaskStatusChanged, completed)
        Spacer(modifier = Modifier.height(4.dp))
        TaskCardDescription(description = task.description)
        Spacer(modifier = Modifier.height(6.dp))
        AdditionalInfoCard(task = task)
    }
}

@Composable
fun MainInfoCard(task: Task, onTaskStatusChanged: (status: Boolean, task: Task) -> Unit, completed: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(), verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = completed,
            onCheckedChange = { status ->
                onTaskStatusChanged(!status, task)
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
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        if (task.reminder != Reminder.NONE) {
            with(task.reminder) {
                TaskInfoItem {
                    Text(
                        text = title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier
                    )
                    Image(
                        painter = painterResource(getReminderImageId(task.reminder)), // Replace with your image resource
                        contentDescription = "Reminder Icon", // Content description for accessibility
                        modifier = Modifier.size(25.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))
            }
        }

        if (task.checkItems?.isNotEmpty() == true) {
            TaskInfoItem {
                Text(
                    text = "Check list:",
                    style = MaterialTheme.typography.bodySmall,
                )
                Icon(
                    modifier = Modifier.size(25.dp),
                    imageVector = Icons.Rounded.Checklist,
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

private fun getReminderImageId(reminder: Reminder): String {
    return when (reminder) {
        Reminder.NONE -> throw IllegalArgumentException("Do not show if there is no reminder")
        Reminder.ON_TIME -> "on_time.png"
        Reminder.FIVE_MIN_BEFORE -> "five_minutes_before.png"
        Reminder.THIRTY_MIN_BEFORE -> "thirty_minites_before.png"
        Reminder.HOUR_BEFORE -> "one_hour_before.png"
        Reminder.DAY_BEFORE -> "one_day_before.png"
    }
}

@Composable
fun CalendarView(
    modifier: Modifier = Modifier, calendarUiModel: CalendarUiModel, onDateClickListener: (CalendarUiModel.Date) -> Unit
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
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = data.selectedDate.date.month.name, style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (data.selectedDate.isToday) {
                "Today"
            } else {
                data.selectedDate.date.format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).localizedBy(Locale.ENGLISH)
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
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp).clickable {
            onDateClickListener(date)
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
    ) {
        Column(
            modifier = Modifier.width(45.dp).padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            Text(
                text = date.day, // day "Mon", "Tue"
                modifier = Modifier.align(Alignment.CenterHorizontally), style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(style = MaterialTheme.typography.bodyLarge,
                text = date.date.dayOfMonth.toString(), // date "15", "16",
                color = if (date.isSelected) Color.White else Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally).drawBehind {
                    if (date.isSelected) drawCircle(color = primaryColor, radius = (this.size.height / 1.3).toFloat())
                })
        }
    }
}
