package com.project.lifeos.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import com.project.lifeos.R
import com.project.lifeos.data.Category
import com.project.lifeos.data.Duration
import com.project.lifeos.data.Task
import com.project.lifeos.ui.screen.addTask.AddTaskBottomSheetView
import com.project.lifeos.utils.formatTime
import com.project.lifeos.viewmodel.CreateGoalScreenViewModel

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
        imageVector = ImageVector.vectorResource(R.drawable.back),
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

    BasicTextField2(modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        value = goalTitle,
        onValueChange = { goalTitle = it },
        textStyle = TextStyle(
            fontSize = 27.sp,
            fontFamily = FontFamily.Default
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        decorator = { innerTextField ->
            if (goalTitle.isEmpty()) {
                Text(
                    "Give some cool title", style = TextStyle(
                        fontSize = 27.sp,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold
                    ), color = Color.LightGray
                )
            }
            innerTextField()
        }
    )
    HorizontalDivider(Modifier.height(1.dp).padding(top = 7.dp), color = Color.Black)

    Spacer(modifier = Modifier.height(35.dp))

    BasicTextField2(modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        value = goalDescription,
        onValueChange = { goalDescription = it },
        textStyle = TextStyle(
            fontSize = 15.sp,
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium
        ),
        decorator = { innerTextField ->
            if (goalDescription.isEmpty()) {
                Text(
                    "What do you want to achieve and why?",
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontFamily = FontFamily.Default,
                        letterSpacing = 0.7.sp,
                        fontWeight = FontWeight.Normal
                    ), color = Color.LightGray
                )
            }
            innerTextField()
        }
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

private fun getResourceId(duration: Duration): Int {
    return when (duration) {
        Duration.TWO_WEEKS -> R.drawable.two_weeks
        Duration.MONTH -> R.drawable.month
        Duration.THREE_MONTH -> R.drawable.three_month
        Duration.SIX_MONTH -> R.drawable.six_month
        Duration.YEAR -> R.drawable.year
    }
}

fun getCategoryResourceId(category: Category): Int {
    return when (category) {
        Category.PERSONAL -> R.drawable.personal
        Category.WORK -> R.drawable.work
        Category.STUDY -> R.drawable.study
        Category.SPORT -> R.drawable.sports
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
                Icon(imageVector = ImageVector.vectorResource(R.drawable.list), contentDescription = null)
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
                Icon(imageVector = ImageVector.vectorResource(R.drawable.list), contentDescription = null)
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

fun getButtonText(size: Int): String {
    return if (size == 0) "Create up to 4 tasks for achieving this goal"
    else if (size in 1..3) "Add one more task?"
    else "Nice done"
}

@Composable
fun AddTaskButton(viewModel: CreateGoalScreenViewModel, text: String, onDone: (task: Task) -> Unit) {
    var showModalBottomSheet by remember { mutableStateOf(false) }


    if (showModalBottomSheet) {
        AddTaskBottomSheetView(
            addTaskViewModel = null,
            duration = Duration.THREE_MONTH,
            onDone = { title, description, time, dates, checkItems, status, reminder, priority ->
                onDone(
                    viewModel.validateDataAndGetTask(
                        title,
                        description,
                        time,
                        dates,
                        checkItems,
                        status,
                        reminder,
                        priority
                    )
                )
                showModalBottomSheet = false
            },
            onDismiss = { showModalBottomSheet = false }
        )
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
            Icon(imageVector = ImageVector.vectorResource(R.drawable.list), contentDescription = null)
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
            Icon(imageVector = Icons.Default.Close, contentDescription = null,
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




