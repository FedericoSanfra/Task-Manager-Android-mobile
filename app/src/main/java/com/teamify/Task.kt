package com.teamify

import org.w3c.dom.Comment
import java.util.Date

data class Task(
    val id: Long,
    var taskName: String, //supponiamo il nome primary key
    var taskDescription: String,
    var assignedMembers: List<Person>,
    var dueDate: Date,
    var creationDate: Date,
    var completion: Float,
    var category: String,
    var tag: List<String>,
    var recurring: Recurring,
    var status: Status,
    var effort : Int = 100,        //default value to be changed
  //  var comments : List<Comment> = emptyList(),
   // var history: List<TaskHistoryElement> = emptyList()
)




