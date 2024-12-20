package com.project.lifeos.ui.screen.addTask

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.project.lifeos.R
import com.project.lifeos.data.DateStatus
import com.project.lifeos.data.Duration
import com.project.lifeos.data.Priority
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.Task
import com.project.lifeos.utils.formatDateFromLocalDate
import com.project.lifeos.utils.stringToDate
import com.project.lifeos.viewmodel.AddTaskViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

private val logger: Logger = Logger.withTag("AddTaskBottomSheetView")

@Composable
fun AddTaskBottomSheetView(
    addTaskViewModel: AddTaskViewModel?,
    duration: Duration = Duration.THREE_MONTH,
    onDismiss: () -> Unit, onDone: (
        title: String,
        description: String?,
        time: Long?,
        dates: List<DateStatus>,
        checkItems: List<String>,
        reminder: Reminder,
        priority: Priority,
    ) -> Unit,
    task: Task? = null
) {

    var showBottomSheet by remember { mutableStateOf(true) }
    logger.d("AddTaskBottomSheetView showBottomSheet $showBottomSheet")
    if (showBottomSheet) {
        AddTaskView(
            addTaskViewModel,
            duration,
            onDismiss = {
                logger.i("Navigate back")
                showBottomSheet = false
                onDismiss()
            }, onDone = onDone,
            task
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddTaskView(
    addTaskViewModel: AddTaskViewModel?,
    duration: Duration,
    onDismiss: () -> Unit, onDone: (
        title: String,
        description: String?,
        time: Long?,
        dates: List<DateStatus>,
        checkItems: List<String>,
        reminder: Reminder,
        priority: Priority,
    ) -> Unit,
    task: Task?
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var taskTitle by remember { mutableStateOf(task?.title ?: "") }
    var taskDescription by remember { mutableStateOf(task?.description ?: "") }
    var taskPriority by remember { mutableStateOf(task?.priority ?: Priority.NO_PRIORITY) }
    var taskTime by remember { mutableStateOf(task?.time) }
    var taskReminder by remember { mutableStateOf(task?.reminder ?: Reminder.NONE) }

    val taskDates = remember {
        mutableStateListOf(
            *if (task == null || task.dateStatuses.isEmpty()) {
                listOf(CalendarDay(date = LocalDate.now(), position = DayPosition.InDate)).toTypedArray()
            } else {
                task.dateStatuses.map {
                    CalendarDay(
                        date = stringToDate(it.date),
                        position = DayPosition.InDate
                    )
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

    val scope = rememberCoroutineScope()

    var showDatePicker by remember { mutableStateOf(false) }
    var showPriorityPicker by remember { mutableStateOf(false) }


    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets.ime
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
        ) {

            BasicTextField2(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable {
                    scope.launch {
                        sheetState.expand()
                    }
                },
                value = taskTitle,
                onValueChange = { taskTitle = it },
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Default
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                decorator = { innerTextField ->
                    if (taskTitle.isEmpty()) {
                        Text(
                            "What would you like to do", style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Default
                            ), color = Color.LightGray
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            BasicTextField2(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable {
                    scope.launch {
                        sheetState.expand()
                    }
                },
                value = taskDescription,
                onValueChange = { taskDescription = it },
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Default
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                decorator = { innerTextField ->
                    if (taskDescription.isEmpty()) {
                        Text(
                            "Description", style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Default
                            ), color = Color.LightGray
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Display the list of input fields
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

                    LaunchedEffect(Unit) {
                        sheetState.expand()
                    }
                }
            }

            if (showPriorityPicker) {
                Column {
                    Priority.entries.forEach {
                        DropdownMenuItem(
                            modifier = Modifier.wrapContentSize(),
                            leadingIcon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.flag),
                                    contentDescription = null,
                                    tint = it.color
                                )
                            },
                            text = { Text(it.title) },
                            onClick = {
                                taskPriority = it
                                showPriorityPicker = false
                            }
                        )
                    }
                }
                LaunchedEffect(Unit) {
                    sheetState.expand()
                }
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    with(taskDates.toList()) {
                        val text = if (isEmpty()) "Today"
                        else formatDateFromLocalDate(first().date)
                        TaskParameters(title = text, Icons.Default.DateRange) { showDatePicker = true }
                    }
                }
                item {
                    TaskParameters(
                        "Priority",
                        ImageVector.vectorResource(R.drawable.flag),
                        textItemColor = taskPriority.color
                    ) {
                        showPriorityPicker = !showPriorityPicker
                    }
                }
                item {
                    TaskParameters("Check Item", ImageVector.vectorResource(R.drawable.list)) {
                        if (taskCheckItems.isNotEmpty() && taskCheckItems.last().isEmpty()) return@TaskParameters
                        taskCheckItems.add("")
                    }
                }
                item {
                    TaskParameters("Add photo", ImageVector.vectorResource(R.drawable.photo)) {}
                }
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )

            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp),
                horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
            ) {

                if (taskTitle.isNotBlank()) {
                    Button(
                        onClick = {
                            addTaskViewModel?.saveTask(
                                title = taskTitle,
                                description = taskDescription,
                                time = taskTime,
                                dates = taskDates.toList().map { it.date.toString() },
                                checkItems = taskCheckItems.toList(),
                                reminder = taskReminder,
                                priority = taskPriority
                            )
                            onDone(
                                taskTitle,
                                taskDescription,
                                taskTime,
                                taskDates.toList().map { DateStatus(it.date.toString(), false) },
                                taskCheckItems.toList(),
                                taskReminder,
                                taskPriority
                            )
                        },
                        modifier = Modifier.wrapContentSize(),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    )
                    {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("Done")
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                modifier = Modifier.size(25.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.baseline_arrow_circle_up_24),
                                contentDescription = null
                            )
                        }

                    }
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {}
            ) {
                DateTimeSelectorView(
                    duration,
                    onDone = { dates, time, reminder ->
                        taskDates.clear()
                        taskDates.addAll(dates)
                        taskTime = time
                        taskReminder = reminder
                        showDatePicker = false

                    },
                    onCanceled = { showDatePicker = false }
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InputCheckItem(textFieldValue: String, onValueChange: (String) -> Unit) {
    HorizontalDivider(Modifier.height(1.dp))
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = false, enabled = false, onCheckedChange = {})
        BasicTextField2(
            modifier = Modifier.fillMaxWidth(0.9f).height(20.dp),
            value = textFieldValue,
            onValueChange = { onValueChange(it) },
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default
            )
        )
        Icon(imageVector = Icons.Default.Close, contentDescription = null)
    }
    HorizontalDivider(Modifier.height(1.dp))
}


@Composable
fun TaskParameters(title: String, icon: ImageVector, textItemColor: Color = Color.Black, onClick: () -> Unit) {
    Card(
        modifier = Modifier.background(Color.Transparent).wrapContentSize(),
        border = BorderStroke(1.dp, Color.LightGray),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.wrapContentWidth().padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = icon,
                contentDescription = null,
                tint = textItemColor
            )
            Text(text = title, style = MaterialTheme.typography.labelLarge, color = textItemColor)
        }
    }
}