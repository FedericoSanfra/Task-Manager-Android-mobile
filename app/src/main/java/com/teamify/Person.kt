package com.teamify

import androidx.compose.ui.graphics.ImageBitmap

data class Person(

val id: Long,
val firstName: String,
val lastName: String,
val email: String,
var isChecked: Boolean,
val imageFile: ImageBitmap?

) {
    fun doesMatchSearchQuery(query: String) : Boolean{
        val matchingCombinations = listOf(
            "$firstName$lastName",
            "$firstName $lastName",
            "${firstName.first()} ${lastName.first()}"
        )
        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}
