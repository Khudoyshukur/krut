package repository

import model.Student
import model.StudentInput

interface StudentRepository {
    suspend fun getAll(): List<Student>
    suspend fun getStudentById(id: Long): Student?
    suspend fun removeStudent(id: Long): Boolean
    suspend fun addStudent(input: StudentInput): Boolean
    suspend fun updateStudent(student: Student): Boolean
}