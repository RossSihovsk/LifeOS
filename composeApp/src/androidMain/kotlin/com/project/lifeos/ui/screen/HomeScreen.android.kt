package com.project.lifeos.ui.screen

import android.view.Surface
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import com.project.lifeos.di.AppModule
import com.project.lifeos.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
actual fun HomeScreenContent(
    viewModel: TaskViewModel,
    navigator: Navigator?
) {
    CalendarApp()

}

@Composable
fun Header(data: CalendarUiModel) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = data.selectedDate.date.month.name,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(4.dp)) // Add vertical spacing between month and "Today"
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
fun CalendarApp(modifier: Modifier = Modifier) {
    val dataSource = CalendarDataSource()
    // we use `mutableStateOf` and `remember` inside composable function to schedules recomposition
    var calendarUiModel by remember { mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today)) }

    Column(modifier = modifier.fillMaxSize()) {
        Header(data = calendarUiModel)
        Content(data = calendarUiModel, onDateClickListener = { date ->
            calendarUiModel = calendarUiModel.copy(
                selectedDate = date,
                visibleDates = calendarUiModel.visibleDates.map {
                    it.copy(
                        isSelected = it.date.isEqual(date.date)
                    )
                }
            )
        })
    }
}

@Composable
fun Content(data: CalendarUiModel, onDateClickListener: (CalendarUiModel.Date) -> Unit) {
    LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal =  10.dp, vertical = 5.dp), horizontalArrangement = Arrangement.SpaceAround) {
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
            .clickable { // making the element clickable, by adding 'clickable' modifier
                onDateClickListener(date)
            },
        colors = CardDefaults.cardColors(
            // background colors of the selected date
            // and the non-selected date are different
            containerColor = MaterialTheme.colorScheme.background
        ),
    ) {
        Column(
            modifier = Modifier
                .width(40.dp)
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

fun Modifier.roundedBackground(
    color: Color,
    isSelected: Boolean = false // Optional parameter for selection state
) = this.then(
    if (isSelected) {
        background(color, CircleShape)
    } else {
        background(Color.Transparent)
    }
)

data class CalendarUiModel(
    val selectedDate: Date, // the date selected by the User. by default is Today.
    val visibleDates: List<Date> // the dates shown on the screen
) {

    val startDate: Date = visibleDates.first() // the first of the visible dates
    val endDate: Date = visibleDates.last() // the last of the visible dates

    data class Date(
        val date: LocalDate,
        val isSelected: Boolean,
        val isToday: Boolean
    ) {
        val day: String = date.format(DateTimeFormatter.ofPattern("E")) // get the day by formatting the date
    }
}

@Composable
@Preview
fun HomeScreenPreview() {
    HomeScreenContent(viewModel = AppModule.taskViewModel)
}