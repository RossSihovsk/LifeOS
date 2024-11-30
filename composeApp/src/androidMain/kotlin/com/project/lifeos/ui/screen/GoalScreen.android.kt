package com.project.lifeos.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import com.project.lifeos.R
import com.project.lifeos.data.Category
import com.project.lifeos.data.Goal
import com.project.lifeos.data.Task
import com.project.lifeos.utils.safePush
import com.project.lifeos.viewmodel.GoalScreenViewModel
import com.project.lifeos.viewmodel.GoalsUIState

@Composable
actual fun GoalScreenContent(navigator: Navigator, viewModel: GoalScreenViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 25.dp, start = 15.dp, end = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val uiState by viewModel.uiState.collectAsState()
        viewModel.init()

        GoalHeader()

        when (val state = uiState) {
            is GoalsUIState.GoalsFounded -> {
                GoalCard(state.goals, viewModel)
            }

            is GoalsUIState.NoGoals -> {
                NoGoalsView()
            }
        }

        AddNewGoalButton {
            navigator.safePush(AddGoalScreen())
        }
    }
}

@Composable
fun NoGoalsView() {
    Column(
        Modifier.height(height = 650.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.goals),
            contentDescription = null,
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "No goals yet \uD83E\uDD7A",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 25.sp
        )
    }
}

@Composable
fun GoalCard(goals: List<Goal>, viewModel: GoalScreenViewModel) {
    var showDeleteView by remember { mutableStateOf(false) }
    var goalToDelete: Goal? by remember { mutableStateOf(null) }

    LazyColumn(modifier = Modifier.height(height = 650.dp)) {
        items(items = goals) { goal ->
            GoalContent(goal, viewModel) {
                goalToDelete = it
                showDeleteView = true
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showDeleteView) {
        DeleteGoalDialog(
            dialogTitle = "Delete Goal",
            dialogText = "Do you want to delete goal \"${goalToDelete?.title}\"?",
            onDismissRequest = {
                showDeleteView = false
            },
            onDeleteCompletely = {
                viewModel.deleteGoal(goalToDelete)
                showDeleteView = false
            }
        )
    }
}


@Composable
fun AddNewGoalButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.background(Color.Transparent).wrapContentSize(),
            onClick = onClick,
            border = BorderStroke(1.dp, Color.Black),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Row(
                modifier = Modifier.wrapContentSize().padding(horizontal = 20.dp, vertical = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.align),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "Add new Goal",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GoalContent(goal: Goal, viewModel: GoalScreenViewModel, onDelete: (goal: Goal) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier.background(Color.Transparent)
                .width(380.dp)
                .height(120.dp)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { onDelete(goal) }
                ),
            border = BorderStroke(1.dp, Color.Black),
            shape = RoundedCornerShape(35.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = goal.category.color),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                GoalCardHeader(goal.title, goal.category)

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    GoalCardFooterElement("Duration: ${goal.duration.title}")
                    GoalCardFooterElement("${viewModel.tasksThisWeek(goal)} tasks this week")
                    GoalCardFooterElement("${viewModel.percentageDone(goal)}%")
                }
            }
        }
    }
}

@Composable
fun GoalCardHeader(title: String, category: Category) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 22.sp
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = category.title,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(getCategoryResourceId(category = category)),
                contentDescription = null,
                modifier = Modifier.size(23.dp)
            )
        }
    }
}

@Composable
fun GoalCardFooterElement(text: String) {
    Card(
        modifier = Modifier.background(Color.Transparent).wrapContentSize(),
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
        onClick = {}
    ) {
        Row(
            modifier = Modifier.wrapContentWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DeleteGoalDialog(
    onDismissRequest: () -> Unit,
    onDeleteCompletely: () -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Delete, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDeleteCompletely()
                }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun GoalHeader() {
    Text(
        text = "Your long term archiving",
        style = MaterialTheme.typography.displayLarge,
        fontWeight = FontWeight.SemiBold,
        fontSize = 25.sp
    )
}