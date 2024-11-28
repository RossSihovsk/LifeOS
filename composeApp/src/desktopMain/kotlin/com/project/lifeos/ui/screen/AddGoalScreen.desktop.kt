package com.project.lifeos.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import com.project.lifeos.data.Category
import com.project.lifeos.data.Duration
import com.project.lifeos.data.Task
import com.project.lifeos.di.AppModuleProvider
import com.project.lifeos.utils.formatTime
import com.project.lifeos.viewmodel.CreateGoalScreenViewModel
import java.time.Instant

private val logger = Logger.withTag("AddGoalScreenContent")
private const val GOAL_SCREEN_KEY = "com.project.lifeos.ui.screen.GoalScreen"

@Composable
actual fun AddGoalScreenContent(navigator: Navigator, viewModel: CreateGoalScreenViewModel) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 25.dp, horizontal = 20.dp)) {

        var goalTitle by remember { mutableStateOf("") }
        var goalDescription by remember { mutableStateOf("") }
        var goalDuration: Duration? by remember { mutableStateOf(null) }
        var goalCategory: Category? by remember { mutableStateOf(null) }
        var goalTasks: List<Task> by remember { mutableStateOf(mutableListOf()) }

        BackButton(navigator = navigator)
        CreateGoalHeader()
        Spacer(modifier = Modifier.height(50.dp))
        GoalTitleAndDescription { title, description ->
            goalTitle = title
            goalDescription = description
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            SelectDuration {
                goalDuration = it
            }

            SelectCategory {
                goalCategory = it
            }
        }

        Spacer(Modifier.height(20.dp))

        GoalTasksView(
            navigator,
            viewModel,
            goalTasks,
            onDone = { goalTasks = goalTasks.plus(it) },
            onDelete = { goalTasks = goalTasks.minus(it) },
            onSaveGoal = {
                viewModel.saveGoal(
                    title = goalTitle,
                    description = goalDescription,
                    duration = goalDuration ?: Duration.THREE_MONTH,
                    category = goalCategory ?: Category.PERSONAL,
                    tasks = goalTasks
                )
            }
        )
    }
}

@Composable
fun CreateGoalHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create your goal",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.W900,
            fontSize = 25.sp
        )
    }
}

@Composable
fun BackButton(navigator: Navigator) {
    Icon(
        imageVector = Icons.Rounded.Backup,
        contentDescription = null,
        modifier = Modifier.clickable {
            navigator.popUntil { it.key == GOAL_SCREEN_KEY }
        }.size(40.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GoalTitleAndDescription(onDone: (title: String, description: String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var goalTitle by remember { mutableStateOf("") }
    var goalDescription by remember { mutableStateOf("") }

    TextField(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        value = goalTitle,
        onValueChange = { goalTitle = it },
        textStyle = TextStyle(
            fontSize = 27.sp,
            fontFamily = FontFamily.Default
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),

        )
    HorizontalDivider(Modifier.height(1.dp).padding(top = 7.dp), color = Color.Black)

    Spacer(modifier = Modifier.height(35.dp))

    TextField(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        value = goalDescription,
        onValueChange = { goalDescription = it },
        textStyle = TextStyle(
            fontSize = 15.sp,
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium
        ),
    )
    HorizontalDivider(Modifier.height(1.dp).padding(top = 7.dp), color = Color.Black)

    if (goalTitle.isNotEmpty() && goalDescription.isNotEmpty()) {
        onDone(goalTitle, goalDescription)
    }
}

@Composable
fun SelectCategory(onDone: (category: Category) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var category: Category? by remember { mutableStateOf(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable { showDialog = true }
    ) {
        Image(
            painter = painterResource(getCategoryResourceId(category = category ?: Category.PERSONAL)),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = category?.title ?: "Chose category",
            letterSpacing = 0.7.sp,
            fontWeight = FontWeight.Bold
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = {}) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier.wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val firstPart = listOf(Category.PERSONAL, Category.STUDY)
                    val secondPart = listOf(Category.WORK, Category.SPORT)

                    CardWrapper(roundedCorners = 10.dp) {
                        Text("Now select your goal category", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    CardWrapper(roundedCorners = 10.dp) {
                        Text(
                            "This one affects nothing, just helps you to improve all your life's categories",
                            modifier = Modifier
                                .fillMaxWidth(0.8f) // Adjust the fraction based on the screen width
                                .padding(2.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                    }

                    Spacer(Modifier.height(15.dp))

                    CategoryRowView(firstPart) {
                        category = it
                        onDone(it)
                        showDialog = false
                    }

                    Spacer(Modifier.height(15.dp))
                    CategoryRowView(secondPart) {
                        category = it
                        onDone(it)
                        showDialog = false
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}

@Composable
fun SelectDuration(onDone: (duration: Duration) -> Unit) {

    var showDialog by remember { mutableStateOf(false) }
    var duration: Duration? by remember { mutableStateOf(null) }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable { showDialog = true }
    ) {
        Image(
            painter = painterResource(getResourceId(duration = duration ?: Duration.TWO_WEEKS)),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = duration?.title ?: "Chose duration",
            letterSpacing = 0.7.sp,
            fontWeight = FontWeight.Bold
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = {}) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val firstPart = listOf(Duration.TWO_WEEKS, Duration.MONTH)
                    val secondPart = listOf(Duration.SIX_MONTH, Duration.THREE_MONTH)
                    val thirdPart = listOf(Duration.YEAR)

                    CardWrapper(roundedCorners = 10.dp) {
                        Text("Now select your goal duration", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    CardWrapper(roundedCorners = 10.dp) {
                        Text(
                            "This would effect the duration of tasks that you'll have to create to achieve your goal",
                            modifier = Modifier
                                .fillMaxWidth(0.8f) // Adjust the fraction based on the screen width
                                .padding(2.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                    }

                    Spacer(Modifier.height(15.dp))

                    DurationRowView(firstPart) {
                        duration = it
                        onDone(it)
                        showDialog = false
                    }

                    Spacer(Modifier.height(15.dp))
                    DurationRowView(thirdPart) {
                        duration = it
                        onDone(it)
                        showDialog = false
                    }

                    Spacer(Modifier.height(15.dp))
                    DurationRowView(secondPart) {
                        duration = it
                        onDone(it)
                        showDialog = false
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryRowView(list: List<Category>, onClick: (category: Category) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        list.forEach {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.clickable { onClick(it) }
            ) {
                Image(
                    painter = painterResource(getCategoryResourceId(category = it)),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp)
                )
                Text(
                    text = it.title,
                    letterSpacing = 0.7.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DurationRowView(list: List<Duration>, onClick: (duration: Duration) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        list.forEach {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.clickable { onClick(it) }
            ) {
                Image(
                    painter = painterResource(getResourceId(duration = it)),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp)
                )
                Text(
                    text = it.title,
                    letterSpacing = 0.7.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun getResourceId(duration: Duration): String {
    return when (duration) {
        Duration.TWO_WEEKS -> "two_weeks.png"
        Duration.MONTH -> "month.png"
        Duration.THREE_MONTH -> "three_month.png"
        Duration.SIX_MONTH -> "six_month.png"
        Duration.YEAR -> "year.png"
    }
}

fun getCategoryResourceId(category: Category): String {
    return when (category) {
        Category.PERSONAL -> "personal.png"
        Category.WORK -> "work.png"
        Category.STUDY -> "study.png"
        Category.SPORT -> "sports.png"
    }
}


@Composable
fun GoalTasksView(
    navigator: Navigator,
    viewModel: CreateGoalScreenViewModel,
    goalTasks: List<Task>,
    onDone: (task: Task) -> Unit,
    onDelete: (task: Task) -> Unit,
    onSaveGoal: () -> Unit
) {

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        if (goalTasks.isEmpty()) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Add your first task",
                fontWeight = FontWeight.Thin,
                fontSize = 20.sp
            )
        }
    }

    goalTasks.forEach { task ->
        Row {
            DisplayTask(task, onDelete)
        }

    }

    Spacer(Modifier.height(5.dp))
    if (goalTasks.isEmpty()) {
        AddTaskButton(viewModel, "Create up to 4 tasks for achieving this goal", onDone)
    } else if (goalTasks.size in 1..3) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AddTaskButton(viewModel, "Add one more task", onDone)

            CardWrapper(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                roundedCorners = 20.dp,
                onClick = {
                    onSaveGoal()
                    navigator.popUntil { it.key == GOAL_SCREEN_KEY }
                }
            ) {
                Text(
                    text = "Save Goal",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(imageVector = Icons.Rounded.List, contentDescription = null)
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CardWrapper(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                roundedCorners = 20.dp,
                onClick = {
                    onSaveGoal()
                }
            ) {
                Text(
                    text = "Save Goal",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(imageVector = Icons.Rounded.List, contentDescription = null)
            }
        }
    }
}

@Composable
fun DisplayTask(task: Task, onDelete: (task: Task) -> Unit) {
    Column {
        TaskView(task, onDelete)
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(Modifier.height(1.dp), color = Color.Black)
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskButton(viewModel: CreateGoalScreenViewModel, text: String, onDone: (task: Task) -> Unit) {
    var showModalBottomSheet by remember { mutableStateOf(false) }
    val showDatePicker = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    if (showModalBottomSheet) {

        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        Dialog(
            onDismissRequest = { showDatePicker.value = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),

            ) {
            Column(Modifier.background(color = Color.White).widthIn(max = 1000.dp).heightIn(max = 640.dp)) {
                Column(Modifier.heightIn(max = 600.dp)) {
                    AddTaskScreenContent(
                        null,
                        logger
                    ) { title, description, time, dates, checkItems, status, reminder, priority ->
                        val task = Task(
                            title = title,
                            description = description,
                            time = time,
                            dates = dates,
                            checkItems = checkItems,
                            status = status,
                            reminder = reminder,
                            priority = priority
                        )
                        onDone(task)
                        showModalBottomSheet = false
                    }
                }
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CardWrapper(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            roundedCorners = 20.dp,
            onClick = { showModalBottomSheet = true }
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(imageVector = Icons.Rounded.List, contentDescription = null)
        }
    }
}

@Composable
fun CardWrapper(
    containerColor: Color = Color.Transparent,
    roundedCorners: Dp,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.background(Color.Transparent).wrapContentSize(),
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(roundedCorners),
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.wrapContentWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Composable
fun TaskView(task: Task, onDelete: (task: Task) -> Unit) {
    Column(
        modifier = Modifier
            .wrapContentSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Close, contentDescription = null,
                modifier = Modifier.height(25.dp).clickable { onDelete(task) })
            Spacer(modifier = Modifier.width(13.dp))
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
        Spacer(modifier = Modifier.height(4.dp))
        TaskCardDescription(description = task.description)
        Spacer(modifier = Modifier.height(6.dp))
        AdditionalInfoCard(task = task)
    }
}

