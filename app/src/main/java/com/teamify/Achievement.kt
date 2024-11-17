package com.teamify
// AchievementViewModel.kt

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background

// AchievementComponent.kt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import java.util.Date


@Composable
fun PieChart(
    data: Map<String, Int>,
    radiusOuter: Dp = 140.dp,
    chartBarWidth: Dp = 35.dp,
    animDuration: Int = 1000,
    centerText: String
) {
    val totalSum = data.values.sum()
    val floatValue = mutableListOf<Float>()

    data.values.forEachIndexed { index, values ->
        floatValue.add(index, 360 * values.toFloat() / totalSum.toFloat())
    }

    val colors = listOf(
        Color(0xFF114B5F),
        Color(0xFF1A936F),
        Color(0xFF88D498),
        Color(0xFFC6DABF),
        Color(0xFFF3E9D2)

    )

    var animationPlayed by remember { mutableStateOf(false) }

    var lastValue = 0f

    val animateSize by animateFloatAsState(
        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 11f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(animateSize.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(radiusOuter * 2f)
                    .rotate(animateRotation)
            ) {
                floatValue.forEachIndexed { index, value ->
                    drawArc(
                        color = colors[index],
                        lastValue,
                        value,
                        useCenter = false,
                        style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                    )
                    lastValue += value
                }
                // Aggiungi il testo al centro del grafico
                val textWidth = size.width / 2
                val textHeight = size.height / 2
                drawContext.canvas.nativeCanvas.apply {
                    save()
                    rotate(90f, textWidth, textHeight)
                    drawText(
                        centerText,
                        textWidth,
                        textHeight,
                        android.graphics.Paint().apply {
                            color = Color.Black.toArgb()
                            textSize = 40f // Imposta la dimensione del testo come desiderato
                            textAlign = android.graphics.Paint.Align.CENTER // Imposta l'allineamento del testo al centro
                        }
                    )
                    restore()
                }
            }
        }

        DetailsPieChart(
            data = data,
            colors = colors
        )
    }
}


@Composable
fun DetailsPieChart(
    data: Map<String, Int>,
    colors: List<androidx.compose.ui.graphics.Color>
) {
    Column(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
    ) {
        data.values.forEachIndexed { index, value ->
            DetailsPieChartItem(
                data = Pair(data.keys.elementAt(index), value),
                color = colors[index]
            )
        }
    }
}

@Composable
fun DetailsPieChartItem(
    data: Pair<String, Int>,
    height: Dp = 20.dp,
    color: androidx.compose.ui.graphics.Color
) {
    Surface(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 40.dp),
        color = androidx.compose.ui.graphics.Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = color,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .size(height)
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = "${data.first}  (${data.second})",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }
        }
    }
}

fun calculateTopPerformers(tasks: List<Task>): List<Pair<Person, Int>> {
    return tasks.filter { it.status == Status.Completed }
        .flatMap { it.assignedMembers }
        .groupingBy { it }
        .eachCount()
        .entries
        .sortedByDescending { it.value }
        .take(3)
        .map { it.toPair() }
}

fun calculateTopEffortPerformers(tasks: List<Task>): List<Pair<Person, Int>> {
    return tasks.filter { it.status == Status.Completed }
        .flatMap { task -> task.assignedMembers.map { person -> person to task.effort } }
        .groupBy({ it.first }, { it.second })
        .mapValues { (_, efforts) -> efforts.sum() }
        .entries
        .sortedByDescending { it.value }
        .take(3)
        .map { it.toPair() }
}

@Composable
fun EffortDisplay(effort: Int) {
    val hours = effort / 60
    val minutes = effort % 60

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Outlined.Timer, contentDescription = "Effort icon")
        Text(text = "${hours}h ${minutes}m", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun TopPerformers(
    topPerformers: List<Pair<Person, Int>>,
    topEffortPerformers: List<Pair<Person, Int>>
) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        Text(text = "Top 3 Performers by Tasks Completed", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        topPerformers.forEach { (person, taskCount) ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)){
                Column(modifier = Modifier.weight(1f)){
                    Row{
                        if(person.imageFile!=null){
                            Image(bitmap = person.imageFile, contentDescription ="Image person file", modifier = Modifier.size(40.dp))
                        }
                        else{
                            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Default icon account", modifier = Modifier.size(40.dp))
                        }
                        Column(modifier = Modifier.padding(start = 8.dp)){
                            Text(text = "${person.firstName} ${person.lastName}", fontSize = 16.sp)
                            Text(text = person.email, fontSize = 12.sp)
                        }

                    }

                }
                Column(modifier = Modifier.weight(1f)){
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Icon(imageVector = Icons.Outlined.Star, contentDescription = "Task icon")
                        Text(text = "$taskCount tasks completed", fontWeight = FontWeight.SemiBold)
                    }

                }

            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Top 3 Performers by Effort", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        topEffortPerformers.forEach { (person, effort) ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)){
                Column(modifier = Modifier.weight(1f)){
                    Row{
                        if(person.imageFile!=null){
                            Image(bitmap = person.imageFile, contentDescription ="Image person file", modifier = Modifier.size(40.dp))
                        }
                        else{
                            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Default icon account", modifier = Modifier.size(40.dp))
                        }
                        Column(modifier = Modifier.padding(start = 8.dp)){
                            Text(text = "${person.firstName} ${person.lastName}", fontSize = 16.sp)
                            Text(text = person.email, fontSize = 12.sp)
                        }

                    }

                }

                Column(modifier = Modifier.weight(1f)){
                    EffortDisplay(effort = effort)
                }


            }

        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun AchievementsGui(team: Team) {

    val totalTasks = team.tasks.size
    val completedTasks = team.tasks.count { it.status == Status.Completed }
    val overdueTasks = team.tasks.count { it.status == Status.Overdue }
    val onHoldTasks = team.tasks.count { it.status == Status.OnHold }
    val pendingTasks = team.tasks.count { it.status == Status.Pending}
    val inProgressTasks = team.tasks.count { it.status == Status.InProgress }

    val topPerformers = calculateTopPerformers(team.tasks)
    val topEffortPerformers = calculateTopEffortPerformers(team.tasks)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    ) {
                        PieChart(
                            data = mapOf(
                                "Completed" to completedTasks,
                                "Overdue" to overdueTasks,
                                "On Hold" to onHoldTasks,
                                "Pending" to pendingTasks,
                                "In Progress" to inProgressTasks
                            ),
                            radiusOuter = 80.dp,
                            chartBarWidth = 50.dp,
                            animDuration = 1000,
                            centerText = "Task status"
                        )
                    }
                }
                item{
                    TopPerformers(
                        topPerformers = topPerformers,
                        topEffortPerformers = topEffortPerformers
                    )
                }


            }
        }