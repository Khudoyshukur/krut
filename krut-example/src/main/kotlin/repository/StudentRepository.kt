package repository

import model.Student
import model.input.StudentInput

interface StudentRepository {
    suspend fun getAllStudents(): List<Student>
    suspend fun insertStudent(input: StudentInput): Student
    suspend fun updateStudent(student: Student): Boolean
    suspend fun deleteStudent(id: Long): Boolean
}