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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger

import com.project.lifeos.data.Task

import com.project.lifeos.utils.formatTime
import com.project.lifeos.viewmodel.HomeScreenViewModel
import com.project.lifeos.viewmodel.HomeUiState

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

//        CalendarView(
//            modifier = Modifier.fillMaxWidth(),
//            calendarUiModel = viewModel.calendarUiModel,
//            onDateClickListener = { date ->
//                viewModel.onDateClicked(date)
//            })

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
                    title = "TO_COMPLETE_TITLE",
                    content = {
                        TasksContent(state.unCompletedTasks, onTaskStatusChanged = viewModel::onTaskStatusChanged,completed = false)
                    },
                    isExpanded = ongoingTasksExpanded.value, // Bind state to isExpanded
                    onToggleClick = { ongoingTasksExpanded.value = !ongoingTasksExpanded.value }
                )

                Spacer(modifier = Modifier.height(20.dp))

                TaskExpandedSection(
                    title = "COMPLETED_TITLE",
                    content = {
                        TasksContent(state.completedTasks, onTaskStatusChanged = viewModel::onTaskStatusChanged, completed = true)
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
    Column(modifier = Modifier
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
//            painter = painterResource(id = 4), // Load the image from the drawable folder
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
//                Icon(
//                    painter = if (isExpanded) painterResource(id = R.drawable.expand_less) else painterResource(id = R.drawable.expand_more),
//                    contentDescription = null
//                )
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
fun TasksContent(tasks: List<Task>, onTaskStatusChanged: (status: Boolean, task: Task) -> Unit, completed: Boolean) {
    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp)) {
        items(tasks) { task ->
            TaskCard(task = task, onTaskStatusChanged = onTaskStatusChanged, completed)
        }
    }
}

@Composable
fun TaskCard(task: Task, onTaskStatusChanged: (status: Boolean, task: Task) -> Unit, completed: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
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
}