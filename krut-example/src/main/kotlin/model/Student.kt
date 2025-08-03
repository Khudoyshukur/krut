package model

import kotlinx.serialization.Serializable
import model.input.StudentInput

@Serializable
data class Student(
    val id: Long,
    val name: String,
    val age: Int
) {
    companion object {
        fun from(id: Long, input: StudentInput): Student {
            return Student(
                id = id,
                name = input.name,
                age = input.age
            )
        }
    }
}