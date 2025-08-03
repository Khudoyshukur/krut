package model

import kotlinx.serialization.Serializable

@Serializable
data class StudentsList(
    val data: List<Student>
)