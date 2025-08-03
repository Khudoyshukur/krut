package model.input

import kotlinx.serialization.Serializable

@Serializable
data class StudentInput(
    val name: String,
    val age: Int
)