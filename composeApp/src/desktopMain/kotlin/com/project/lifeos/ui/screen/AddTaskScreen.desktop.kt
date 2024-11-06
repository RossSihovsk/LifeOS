package com.project.lifeos.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.touchlab.kermit.Logger
import com.project.lifeos.data.TaskStatus
import com.project.lifeos.viewmodel.AddTaskViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun AddTaskScreenContent(viewModel: AddTaskViewModel, logger: Logger) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val taskTitle = remember { mutableStateOf("") }
        val taskDescription = remember { mutableStateOf("") }
        val taskTime = remember { mutableStateOf<Long?>(null) }
        val taskDate = remember { mutableStateOf<Long?>(null) }

        val keyboardController = LocalSoftwareKeyboardController.current

        val timePickerState = rememberTimePickerState()
        val showTimePicker = remember { mutableStateOf(false) }

        val isMenuOpen = remember { mutableStateOf(false) }
        val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
        val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = Instant.now().toEpochMilli()
        )
        val showDatePicker = remember { mutableStateOf(false) }

        Text(
            modifier = Modifier.padding(top = 40.dp),
            text = "AddTaskScreen",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily
        )
        Spacer(modifier = Modifier.padding(40.dp))

        ShowTitleTextField(taskTitle = taskTitle, keyboardController = keyboardController)
        Spacer(modifier = Modifier.padding(20.dp))
        ShowDescriptionTextField(taskDescription = taskDescription)

        Spacer(modifier = Modifier.padding(30.dp))
        Text(
            text = getFormattedTimeText(taskTime, timeFormatter),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
        )
        Text(
            text = getFormattedDateText(taskDate, dateFormatter),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
        )
        Spacer(modifier = Modifier.padding(60.dp))


        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { showDatePicker.value = true }) {
                Icon(
                    Icons.Rounded.DateRange,
                    contentDescription = null
                )
            }
            Button(onClick = { showTimePicker.value = true }) {
//                Icon(
//                    painterResource(id = R.drawable.time),
//                    contentDescription = null
//                )
            }
            Button(onClick = { isMenuOpen.value = true }) {
//                Icon(
//                    painterResource(id = R.drawable.add_activity),
//                    contentDescription = null
//                )
//            }
        }
        ShowTimePickerDialog(showTimePicker, timePickerState, taskTime)
        ShowDatePickerDialog(showDatePicker, datePickerState, taskDate)


        Spacer(modifier = Modifier.height(60.dp))

        EnableSaveButton(taskDate, taskDescription, taskTime, taskTitle) {
            logger.i(
                "Save task with title: ${taskTitle.value}, description: ${taskDescription.value}, time: ${taskTime.value}, date: ${taskDate.value}"
            )
            viewModel.saveTask(
                title = taskTitle.value,
                description = taskDescription.value,
                time = taskTime.value,
                dates = dateFormatter.format(Date(taskDate.value!!)),
                status = TaskStatus.PENDING
            )
        }
    }
}}

@Composable
fun EnableSaveButton(
    taskDate: MutableState<Long?>,
    taskDescription: MutableState<String>,
    taskTime: MutableState<Long?>,
    taskTitle: MutableState<String>,
    onClick: () -> Unit
) {
    val enabled =
        taskDate.value != null && taskDescription.value.isNotEmpty() && taskTime.value != null && taskTitle.value.isNotEmpty()

    Button(enabled = enabled, onClick = onClick) {
        Text("Save Task")
    }
}

fun getFormattedDateText(taskDate: MutableState<Long?>, dateFormatter: SimpleDateFormat): String {
    return taskDate.value?.let { time ->
        "Entered Date: ${dateFormatter.format(time)}"
    } ?: "Date has not selected yet"
}

fun getFormattedTimeText(taskTime: MutableState<Long?>, timeFormatter: SimpleDateFormat): String {
    return taskTime.value?.let { time ->
        "Entered Time: ${timeFormatter.format(time)}"
    } ?: "Time has not selected yet"
}

@Composable
fun ShowTitleTextField(taskTitle: MutableState<String>, keyboardController: SoftwareKeyboardController?) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .onFocusChanged { focusState ->
                if (!focusState.isFocused) keyboardController?.hide()
            },
        shape = RoundedCornerShape(20.dp),
        value = taskTitle.value,
        onValueChange = { taskTitle.value = it },
        label = { Text("What are you going to achieve") },
       // leadingIcon = { Icon(painterResource(R.drawable.title), contentDescription = "Localized description") },
        placeholder = { Text("Enter title for your task") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
    )
}

@Composable
fun ShowDescriptionTextField(taskDescription: MutableState<String>) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        value = taskDescription.value,
        onValueChange = { taskDescription.value = it },
        label = { Text("Add some description about it") },
       // leadingIcon = { Icon(painterResource(R.drawable.align), contentDescription = "Localized description") },
        placeholder = { Text("Enter description for your task") }
    )
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
    taskDate: MutableState<Long?>
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
                        showDatePicker.value = false
                        taskDate.value = datePickerState.selectedDateMillis
                    },
                    enabled = confirmEnabled.value
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker.value = false }
                ) { Text("Cancel") }
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
    }}


