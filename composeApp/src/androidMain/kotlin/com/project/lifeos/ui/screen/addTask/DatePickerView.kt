package com.project.lifeos.ui.screen.addTask

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.project.lifeos.R
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.Repeat
import com.project.lifeos.ui.screen.TimePickerDialog
import com.project.lifeos.ui.view.recalculateDays
import com.project.lifeos.ui.view.rememberFirstMostVisibleMonth
import com.project.lifeos.utils.formatTime
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

@Composable
fun DateTimeSelectorView(
    onDone: (dates: List<CalendarDay>, time: Long?, reminder: Reminder) -> Unit,
    onCanceled: () -> Unit
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(2) }
    val endMonth = remember { currentMonth.plusMonths(6) }
    val selections = remember { mutableStateListOf<CalendarDay>() }
    val daysOfWeek = remember { daysOfWeek() }


    var taskReminder by remember { mutableStateOf(Reminder.NONE) }
    var taskTime by remember { mutableStateOf<Long?>(null) }

    val today = remember { LocalDate.now() }

    selections.add(CalendarDay(today, DayPosition.MonthDate))



    Column(
        modifier = Modifier
            .wrapContentSize()
            .background(Color.White)
            .padding(vertical = 10.dp)
//            .clip(shape = RoundedCornerShape(10.dp))
        ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val state = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = currentMonth.atDay(1).dayOfWeek,
        )
        val visibleMonth = rememberFirstMostVisibleMonth(state, viewportPercent = 90f)
        SimpleCalendarTitle(
            modifier = Modifier.padding(horizontal = 8.dp),
            currentMonth = visibleMonth.yearMonth,
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            HorizontalCalendar(
                modifier = Modifier.testTag("Calendar")
                    .height(325.dp),
                state = state,

                dayContent = { day ->
                    Day(day, isSelected = selections.contains(day)) { clicked ->
                        if (selections.contains(clicked)) {
                            selections.remove(clicked)
                        } else {
                            selections.add(clicked)
                        }
                    }
                },
                monthHeader = {
                    MonthHeader(daysOfWeek = daysOfWeek)
                }
            )
        }

        MonthFooter(
            onTimeSelected = { time -> taskTime = time },
            onReminderSelected = { reminder ->
                taskReminder = reminder
            },
            onRepeatSelected = { repeat ->
                if (repeat != Repeat.CUSTOM && repeat != Repeat.NONE) with(selections) {
                    val updatedList = recalculateDays(selections.first(), repeat)
                    clear()
                    addAll(updatedList)
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Text("Clear", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(
                    "Cancel",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onCanceled() })
            }
            Text(
                "Done",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    onDone(selections.toList(), taskTime, taskReminder)
                })
        }
    }
}

@Composable
fun SimpleCalendarTitle(
    modifier: Modifier,
    currentMonth: YearMonth,
) {
    Row(
        modifier = modifier.height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .testTag("MonthTitle"),
            text = currentMonth.month.name,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthFooter(
    onTimeSelected: (time: Long) -> Unit,
    onReminderSelected: (reminder: Reminder) -> Unit,
    onRepeatSelected: (repeat: Repeat) -> Unit
) {

    var showTimePicker by remember { mutableStateOf(false) }
    var showReminderPicker by remember { mutableStateOf(false) }
    var showRepeatPicker by remember { mutableStateOf(false) }

    var timeInfo by remember { mutableStateOf("None") }
    var reminderInfo by remember { mutableStateOf("None") }
    var repeatInfo by remember { mutableStateOf("None") }

    var dropdownMenuOffset by remember { mutableStateOf(Offset.Zero) }

    Column(
        modifier = Modifier.fillMaxWidth(0.95f).wrapContentHeight().clip(shape = RoundedCornerShape(20.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainer),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        TaskDateTimeDetails(
            title = "Time",
            imageVector = ImageVector.vectorResource(R.drawable.time),
            interactionSource = interactionSource,
            selectedData = timeInfo,
        ) { showTimePicker = true }
        TaskDateTimeDetails(
            title = "Reminder",
            imageVector = ImageVector.vectorResource(R.drawable.alarm),
            selectedData = reminderInfo,
            interactionSource = interactionSource,
            passCoordinates = { coordinates -> dropdownMenuOffset = coordinates.positionInRoot() }
        ) { showReminderPicker = true }
        TaskDateTimeDetails(
            title = "Repeat",
            imageVector = ImageVector.vectorResource(R.drawable.repeat),
            selectedData = repeatInfo,
            interactionSource = interactionSource,
            passCoordinates = { coordinates -> dropdownMenuOffset = coordinates.positionInRoot() }
        ) { showRepeatPicker = true }
    }

    if (showTimePicker) {
        ShowTimePicker(onCanceled = { showTimePicker = false }, onConfirmed = { state ->
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, state.hour)
            cal.set(Calendar.MINUTE, state.minute)
            cal.isLenient = false
            showTimePicker = false
            onTimeSelected(cal.timeInMillis)

            timeInfo = formatTime(cal.timeInMillis)
        })
    }

    if (showReminderPicker) {
        ShowReminder(
            offset = with(LocalDensity.current) {
                DpOffset(
                    dropdownMenuOffset.x.toDp() + 200.dp,
                    dropdownMenuOffset.y.toDp()
                )
            },
            onDismiss = { showReminderPicker = false },
            onConfirmed = { reminder ->
                reminderInfo = reminder.title
                showReminderPicker = false
                onReminderSelected(reminder)
            }
        )
    }

    if (showRepeatPicker) {
        ShowRepeatPicker(
            offset = with(LocalDensity.current) {
                DpOffset(
                    dropdownMenuOffset.x.toDp() + 200.dp,
                    dropdownMenuOffset.y.toDp()
                )
            },
            onDismiss = { showRepeatPicker = false },
            onConfirmed = { repeat ->
                showRepeatPicker = false
                repeatInfo = repeat.title
                onRepeatSelected(repeat)
            }
        )
    }
}

@Composable
fun ShowRepeatPicker(
    offset: DpOffset,
    onDismiss: () -> Unit,
    onConfirmed: (reminder: Repeat) -> Unit,
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss,
        offset = offset,
        properties = PopupProperties()
    ) {
        Repeat.entries.forEach {
            DropdownMenuItem(
                text = { Text(it.title) },
                onClick = { onConfirmed(it) }
            )
        }
    }
}

@Composable
fun ShowReminder(
    onDismiss: () -> Unit,
    onConfirmed: (reminder: Reminder) -> Unit,
    offset: DpOffset
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss,
        offset = offset,
        properties = PopupProperties()
    ) {
        Reminder.entries.forEach {
            DropdownMenuItem(
                text = { Text(it.title) },
                onClick = { onConfirmed(it) }
            )
        }
    }
}


@Composable
fun TaskDateTimeDetails(
    imageVector: ImageVector,
    title: String,
    interactionSource: MutableInteractionSource,
    selectedData: String,
    passCoordinates: (coordinates: LayoutCoordinates) -> Unit = {},
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 20.dp)
            .onGloballyPositioned { coordinates -> passCoordinates(coordinates) }
            .clickable(indication = null, interactionSource = interactionSource, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.wrapContentSize().padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = imageVector,
                contentDescription = null
            )
            Text(title)
        }
        Text(selectedData, modifier = Modifier)
    }
}

@Composable
private fun MonthHeader(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("MonthHeader"),
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun Day(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // This is important for square-sizing!
            .testTag("MonthDay")
            .padding(6.dp)
            .clip(CircleShape)
            .background(color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            // Disable clicks on inDates/outDates
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
//                showRipple = !isSelected,
                onClick = { onClick(day) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        val textColor = when (day.position) {
            // Color.Unspecified will use the default text color from the current theme
            DayPosition.MonthDate -> if (isSelected) Color.White else Color.Unspecified
            DayPosition.InDate, DayPosition.OutDate -> Color.LightGray
        }
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            fontSize = 14.sp,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowTimePicker(onCanceled: () -> Unit, onConfirmed: (state: TimePickerState) -> Unit) {
    val timePickerState = rememberTimePickerState()
    TimePickerDialog(
        onCancel = onCanceled,
        onConfirm = { onConfirmed(timePickerState) },
        content = {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
            calendar.set(Calendar.MINUTE, timePickerState.minute)
            TimePicker(state = timePickerState)
        })
}

@Preview
@Composable
private fun Example1Preview() {
    DateTimeSelectorView(
        onCanceled = {},
        onDone = { _, _, _ -> }
    )
}