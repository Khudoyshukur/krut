package repository.impl

import model.Student
import model.input.StudentInput
import repository.StudentRepository

class StudentRepositoryImpl : StudentRepository {
    private var fakeId = 1L
    private var students = mutableListOf<Student>()

    override suspend fun getAllStudents(): List<Student> {
        return students
    }

    override suspend fun insertStudent(input: StudentInput): Student {
        val student = Student.from(id = fakeId++, input = input)
        students.add(student)

        return student
    }

    override suspend fun updateStudent(student: Student): Boolean {
        var found = false

        students = students.map { currStudent ->
            if (currStudent.id == student.id) student.also { found = true } else currStudent
        }.toMutableList()

        return found
    }

    override suspend fun deleteStudent(id: Long): Boolean {
        return students.removeIf { it.id == id }
    }
}