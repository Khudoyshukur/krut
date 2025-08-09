package repository.impl

import model.Student
import model.StudentInput
import repository.StudentRepository

class InMemoryStudentRepository: StudentRepository {
    private val students = mutableListOf<Student>()
    private var idIncrement = 1L

    override suspend fun getAll(): List<Student> {
        return students
    }

    override suspend fun getStudentById(id: Long): Student? {
        return students.find { it.id == id }
    }

    override suspend fun removeStudent(id: Long): Boolean {
        return students.removeIf { it.id == id }
    }

    override suspend fun addStudent(input: StudentInput): Boolean {
        val newStudent = Student(
            id = idIncrement++,
            name = input.name,
            age = input.age
        )

        return students.add(newStudent)
    }

    override suspend fun updateStudent(student: Student): Boolean {
        var updated = false

        val newStudents = mutableListOf<Student>()
        students.forEach { oldStudent ->
            if (oldStudent.id == student.id) {
                updated = true
                newStudents.add(student)
            } else {
                newStudents.add(oldStudent)
            }
        }

        students.clear()
        students.addAll(newStudents)

        return updated
    }
}