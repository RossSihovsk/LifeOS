package com.project.lifeos.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.lifeos.ui.view.calendar.CalendarUiModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

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
