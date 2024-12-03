package com.project.lifeos.ui.screen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import com.project.lifeos.data.Priority
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.Task
import com.project.lifeos.utils.stringToDate
import com.project.lifeos.viewmodel.AddTaskViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.util.*
private const val GOAL_SCREEN_KEY = "com.project.lifeos.ui.screen.HomeScreen"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun AddTaskScreenContent(navigator: Navigator?,
    viewModel: AddTaskViewModel?, task: Task?, logger: Logger?, onDone: (
    title: String,
    description: String?,
    time: Long?,
    dates: List<String>,
    checkItems: List<String>,
    reminder: Reminder,
    priority: Priority,
) -> Unit) {


    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource("bg.png"),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize().graphicsLayer(alpha = 0.5f),
            contentScale = ContentScale.FillBounds // Scales the image to cover the whole background
        )
    }
   Box{
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        val timePickerState = rememberTimePickerState()
        val showTimePicker = remember { mutableStateOf(false) }
        val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
        val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = Instant.now().toEpochMilli()
        )
        val showDatePicker = remember { mutableStateOf(false) }

        val taskTitle = remember { mutableStateOf(task?.title ?: "") }
        val taskDescription = remember { mutableStateOf(task?.description ?: "") }
        val taskPriority = remember { mutableStateOf(task?.priority ?: Priority.NO_PRIORITY) }
        val taskTime = remember { mutableStateOf(task?.time) }
        val taskReminder = remember { mutableStateOf(task?.reminder ?: Reminder.NONE) }

        val taskDate = remember {
            mutableStateListOf(
                *if (task == null || task.dateStatuses.isEmpty()) {
                    listOf(System.currentTimeMillis()).toTypedArray()
                } else {
                    task.dateStatuses.map {
                        stringToDate(it.date).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
                    }.toTypedArray()
                }
            )
        }

        val taskCheckItems = remember {
            mutableStateListOf(
                *if (task == null || task.checkItems.isNullOrEmpty()) {
                    emptyArray<String>()
                } else {
                    task.checkItems.toTypedArray()
                }
            )
        }
        Text(
            modifier = Modifier.padding(top = 10.dp).padding(start = 30.dp),
            text = "AddTaskScreen",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily
        )
        Spacer(modifier = Modifier.padding(10.dp))
        ShowTitleTextField(taskTitle = taskTitle)
        Spacer(modifier = Modifier.padding(5.dp))
        ShowDescriptionTextField(taskDescription = taskDescription)
        Spacer(modifier = Modifier.padding(10.dp))
        Column {
        Row {
            PriorityPicker(taskPriority)

            Column(modifier = Modifier.padding(start = 120.dp)) {
                Column {
                    Text(
                        text = "Select Date",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    Button(onClick = { showDatePicker.value = true },shape = RoundedCornerShape(10.dp)) {
                        Icon(
                            Icons.Rounded.DateRange,
                            contentDescription = null
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(10.dp))
                Column {
                    Text(
                        text = "Select Time",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    Button(onClick = { showTimePicker.value = true }, shape = RoundedCornerShape(10.dp)) {
                        Icon(
                            Icons.Rounded.Timer, contentDescription = null
                        )
                    }
                    Text(
                        text = getFormattedTimeText(taskTime, timeFormatter),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
                ShowTimePickerDialog(showTimePicker, timePickerState, taskTime)
                ShowDatePickerDialog(showDatePicker, datePickerState, taskDate.toMutableList())
                Spacer(modifier = Modifier.height(5.dp))

            }
            Column(modifier = Modifier.padding(horizontal = 30.dp)) {
                Text(text = "Check list items",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
                    modifier = Modifier.padding(start = 2.dp, bottom = 5.dp))

                CheckList(taskCheckItems)
                Button(
                    onClick = { if (taskCheckItems.isNotEmpty() && taskCheckItems.last().isEmpty())return@Button
                        taskCheckItems.add("") },

                    shape = RoundedCornerShape(10.dp),
                ){Icon(Icons.Rounded.List,contentDescription = null)
                }
            }

        }

            ReminderPicker(taskReminder)
            Row(Modifier.padding(start = 30.dp)) {
            EnableSaveButton(taskDescription, taskTime, taskTitle, navigator) {
            logger?.i(
                "Save task with title: ${taskTitle.value}, description: ${taskDescription.value}, time: ${taskTime.value}, date: $taskDate"
            )
                if (task==null){
                    viewModel?.saveTask(
                        title = taskTitle.value,
                        description = taskDescription.value,
                        time = taskTime.value,
                        dates = taskDate.map { dateFormatter.format(Date(it)) } ,
                        reminder = taskReminder.value,
                        priority = taskPriority.value,
                        checkItems = taskCheckItems.toList(),
                    )
                }
                else {
                    onDone(
                        taskTitle.value,
                        taskDescription.value,
                        taskTime.value,
                        taskDate.map { dateFormatter.format(Date(it)) },
                        taskCheckItems.toList(),
                        Reminder.DAY_BEFORE,
                        taskPriority.value
                    )
                }
        }}

        }

        Spacer(modifier = Modifier.padding(10.dp))
    }}
}

@Composable
fun EnableSaveButton(
    taskDescription: MutableState<String>,
    taskTime: MutableState<Long?>,
    taskTitle: MutableState<String>,
    navigator: Navigator?,
    onClick: () -> Unit

) {
    val enabled =
        taskDescription.value.isNotEmpty() && taskTime.value != null && taskTitle.value.isNotEmpty()

    Button(enabled = enabled, onClick = {onClick()
        navigator?.popUntil { it.key == GOAL_SCREEN_KEY }
    },shape = RoundedCornerShape(10.dp)) {
        Text("Save Task")
    }
}



fun getFormattedTimeText(taskTime: MutableState<Long?>, timeFormatter: SimpleDateFormat): String {
    return taskTime.value?.let { time ->
        "Entered Time: ${timeFormatter.format(time)}"
    } ?: "Time has not selected yet"
}
@Composable
fun ReminderPicker(taskReminder: MutableState<Reminder>) {
    Column(modifier = Modifier.padding(start = 32.dp)) {
        Text(
            text = "Select Reminder",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
            modifier = Modifier.padding(start = 2.dp)
        )
        Row {
        Row {
            Checkbox(
                checked = taskReminder.value == Reminder.NONE,
                onCheckedChange = { taskReminder.value = Reminder.NONE },
            )
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = Reminder.NONE.title,
            )
        }
        Row {
            Checkbox(
                checked = taskReminder.value == Reminder.FIVE_MIN_BEFORE,
                onCheckedChange = { taskReminder.value = Reminder.FIVE_MIN_BEFORE },
            )
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = Reminder.FIVE_MIN_BEFORE.title,
            )
        }
        Row {
            Checkbox(
                checked = taskReminder.value == Reminder.THIRTY_MIN_BEFORE,
                onCheckedChange = { taskReminder.value = Reminder.THIRTY_MIN_BEFORE },
            )
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = Reminder.THIRTY_MIN_BEFORE.title,
            )
        }
        Row {
            Checkbox(
                checked = taskReminder.value == Reminder.HOUR_BEFORE,
                onCheckedChange = { taskReminder.value = Reminder.HOUR_BEFORE },
            )
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = Reminder.HOUR_BEFORE.title,
            )
        }
        Row {
            Checkbox(
                checked = taskReminder.value == Reminder.DAY_BEFORE,
                onCheckedChange = { taskReminder.value = Reminder.DAY_BEFORE },
            )
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = Reminder.DAY_BEFORE.title,
            )
        }}
    }
}
@Composable
fun ShowTitleTextField(taskTitle: MutableState<String>) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        value = taskTitle.value,
        onValueChange = { taskTitle.value = it },
        label = { Text("What are you going to achieve") },
        leadingIcon = {
            Icon(
                Icons.Rounded.Title, contentDescription = null
            )
        },
        placeholder = { Text("Enter title for your task") }
    )
}

@Composable
fun ShowDescriptionTextField(taskDescription: MutableState<String>) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        value = taskDescription.value,
        onValueChange = { taskDescription.value = it },
        label = { Text("Add some description about it") },
        leadingIcon = {
            Icon(
                Icons.Rounded.Description, contentDescription = null
            )
        },
        placeholder = { Text("Enter description for your task") }
    )
}

@Composable
fun PriorityPicker(taskPriority: MutableState<Priority>) {
    Column(modifier = Modifier.padding(start = 32.dp)) {
        Text(
            text = "Select Priority",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
            modifier = Modifier.padding(start = 2.dp)
        )
        Row {
            Checkbox(
                checked = taskPriority.value == Priority.NO_PRIORITY,
                onCheckedChange = { taskPriority.value = Priority.NO_PRIORITY },
            )
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = Priority.NO_PRIORITY.title,
                color = Priority.NO_PRIORITY.color
            )
        }
        Row {
            Checkbox(
                checked = taskPriority.value == Priority.HIGH,
                onCheckedChange = { taskPriority.value = Priority.HIGH },
            )
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = Priority.HIGH.title,
                color = Priority.HIGH.color
            )
        }
        Row {
            Checkbox(
                checked = taskPriority.value == Priority.MEDIUM,
                onCheckedChange = { taskPriority.value = Priority.MEDIUM },
            )
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = Priority.MEDIUM.title,
                color = Priority.MEDIUM.color
            )
        }
        Row {
            Checkbox(
                checked = taskPriority.value == Priority.LOW,
                onCheckedChange = { taskPriority.value = Priority.LOW },
            )
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = Priority.LOW.title,
                color = Priority.LOW.color
            )
        }
    }
}
@Composable
fun CheckList(taskCheckItems: MutableList<String>) {
    LazyColumn {
        items(taskCheckItems) { textFieldValue ->
            InputCheckItem(
                textFieldValue = textFieldValue
            ) { newValue ->
                val index = taskCheckItems.indexOf(textFieldValue)
                if (index != -1) {
                    taskCheckItems[index] = newValue
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun InputCheckItem(textFieldValue: String, onValueChange: (String) -> Unit) {
    HorizontalDivider(Modifier.height(1.dp))
    Row(modifier = Modifier.padding(horizontal = 0.dp), verticalAlignment = Alignment.CenterVertically) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            value = textFieldValue,
            onValueChange = { onValueChange(it) },
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default
            ),
            //leadingIcon = {Icon(Icons.Rounded.Close, contentDescription = null)},
            )
    }
    HorizontalDivider(Modifier.height(1.dp))
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowTimePickerDialog(
    showTimePicker: MutableState<Boolean>,
    timePickerState: TimePickerState,
    taskTime: MutableState<Long?>
) {
    if (showTimePicker.value) {
        TimePickerDialog(
            onCancel = { showTimePicker.value = false },
            onConfirm = {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                cal.set(Calendar.MINUTE, timePickerState.minute)
                cal.isLenient = false
                taskTime.value = cal.time.time
                showTimePicker.value = false
            },
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDatePickerDialog(
    showDatePicker: MutableState<Boolean>,
    datePickerState: DatePickerState,
    taskDate: MutableList<Long?>
) {
    if (showDatePicker.value) {
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }

        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskDate.add(datePickerState.selectedDateMillis)
                    },
                    enabled = confirmEnabled.value
                ) { Text("To List") }
            },

            dismissButton = {
                TextButton(
                    onClick = { showDatePicker.value = false }
                ) { Text("Exit") }
            }

        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Cancel") }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("OK") }
                }
            }
        }
    }
}


