package com.teamify

import androidx.compose.ui.graphics.ImageBitmap
import java.util.Date

data class Team(
    val id: Long,
    val name: String,
    val teamImage: ImageBitmap?,  //OR  val imageResId: Int (if we upload from drawable)
    val description: String,
    val category: String,
    val roles: MutableMap<Long, Role>, // Maps person ID to their role in the team
    val tasks: MutableList<Task>,
    val messages: MutableList<Message>,
    val creationDate: Date
)

