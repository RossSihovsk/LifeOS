package com.project.lifeos.ui.view

import android.content.Context
import android.util.Log
import android.widget.Toast


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.project.lifeos.data.Duration
import com.project.lifeos.data.Repeat
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import java.time.LocalDate

/**
 * Alternative way to find the first fully visible month in the layout.
 *
 * @see [rememberFirstVisibleMonthAfterScroll]
 * @see [rememberFirstMostVisibleMonth]
 */
@Composable
fun rememberFirstCompletelyVisibleMonth(state: CalendarState): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    // Only take non-null values as null will be produced when the
    // list is mid-scroll as no index will be completely visible.
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo.completelyVisibleMonths.firstOrNull() }
            .filterNotNull()
            .collect { month -> visibleMonth.value = month }
    }
    return visibleMonth.value
}

/**
 * Returns the first visible month in a paged calendar **after** scrolling stops.
 *
 * @see [rememberFirstCompletelyVisibleMonth]
 * @see [rememberFirstMostVisibleMonth]
 */
@Composable
fun rememberFirstVisibleMonthAfterScroll(state: CalendarState): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { scrolling -> !scrolling }
            .collect { visibleMonth.value = state.firstVisibleMonth }
    }
    return visibleMonth.value
}

/**
 * Find the first month on the calendar visible up to the given [viewportPercent] size.
 *
 * @see [rememberFirstCompletelyVisibleMonth]
 * @see [rememberFirstVisibleMonthAfterScroll]
 */
@Composable
fun rememberFirstMostVisibleMonth(
    state: CalendarState,
    viewportPercent: Float = 50f,
): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo.firstMostVisibleMonth(viewportPercent) }
            .filterNotNull()
            .collect { month -> visibleMonth.value = month }
    }
    return visibleMonth.value
}

private val CalendarLayoutInfo.completelyVisibleMonths: List<CalendarMonth>
    get() {
        val visibleItemsInfo = this.visibleMonthsInfo.toMutableList()
        return if (visibleItemsInfo.isEmpty()) {
            emptyList()
        } else {
            val lastItem = visibleItemsInfo.last()
            val viewportSize = this.viewportEndOffset + this.viewportStartOffset
            if (lastItem.offset + lastItem.size > viewportSize) {
                visibleItemsInfo.removeLast()
            }
            val firstItem = visibleItemsInfo.firstOrNull()
            if (firstItem != null && firstItem.offset < this.viewportStartOffset) {
                visibleItemsInfo.removeFirst()
            }
            visibleItemsInfo.map { it.month }
        }
    }

private fun CalendarLayoutInfo.firstMostVisibleMonth(viewportPercent: Float = 50f): CalendarMonth? {
    return if (visibleMonthsInfo.isEmpty()) {
        null
    } else {
        val viewportSize = (viewportEndOffset + viewportStartOffset) * viewportPercent / 100f
        visibleMonthsInfo.firstOrNull { itemInfo ->
            if (itemInfo.offset < 0) {
                itemInfo.offset + itemInfo.size >= viewportSize
            } else {
                itemInfo.size - itemInfo.offset >= viewportSize
            }
        }?.month
    }
}


fun recalculateDays(firstDay: CalendarDay, repeat: Repeat, duration: Duration): List<CalendarDay> {
    Log.d("JustForTest", "recalculateDays")
    return when (repeat) {
        Repeat.NONE -> listOf(firstDay)
        Repeat.WEEKLY -> {
            val list = mutableListOf(firstDay)
            val endDate = calculateLastDate(firstDay.date, duration)
            var lastDay = CalendarDay(firstDay.date.plusWeeks(1), DayPosition.MonthDate)
            list.add(lastDay)
            while (lastDay.date.isBefore(endDate)) {
                lastDay = CalendarDay(lastDay.date.plusWeeks(1), lastDay.position)
                list.add(lastDay)
            }
            Log.d("JustForTest", "$list")
            list
        }

        Repeat.MONTHLY -> {
            val list = mutableListOf(firstDay)
            val endDate = calculateLastDate(firstDay.date, duration)
            var lastDay = CalendarDay(firstDay.date.plusMonths(1), DayPosition.MonthDate)
            list.add(lastDay)
            while (lastDay.date.isBefore(endDate)) {
                lastDay = CalendarDay(lastDay.date.plusMonths(1), lastDay.position)
                list.add(lastDay)
            }

            list
        }

        Repeat.CUSTOM -> listOf(firstDay)
    }
}

fun calculateLastDate(firstDay: LocalDate, duration: Duration): LocalDate {
    return when (duration) {
        Duration.TWO_WEEKS -> firstDay.plusWeeks(2)
        Duration.MONTH -> firstDay.plusMonths(1)
        Duration.THREE_MONTH -> firstDay.plusMonths(3)
        Duration.SIX_MONTH -> firstDay.plusMonths(6)
        Duration.YEAR -> firstDay.plusYears(1)
    }
}

fun makeToastMessage(text: String, context: Context) = Toast.makeText(context, text, Toast.LENGTH_LONG).show()

