package repository.impl

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import model.Student
import model.StudentInput
import repository.StudentRepository
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader

class FileStudentRepository: StudentRepository {
    private val file = File("krut-example/src/main/kotlin/repository/impl/students.json")

    private val students = mutableListOf<Student>()
    private var idIncrement = 1L

    init {
        if (file.exists().not()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }

        try {
            val json = FileReader(file).readText()
            val students = Json.decodeFromString<List<Student>>(json)

            this.students.addAll(students)
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun updateFile() {
        FileOutputStream(file).use {
            it.write(
                Json.encodeToString(serializer(), this.students).toByteArray()
            )
        }
    }

    override suspend fun getAll(): List<Student> {
        return students
    }

    override suspend fun getStudentById(id: Long): Student? {
        return students.find { it.id == id }
    }

    override suspend fun removeStudent(id: Long): Boolean {
        return students.removeIf { it.id == id }.also {
            if (it) updateFile()
        }
    }

    override suspend fun addStudent(input: StudentInput): Boolean {
        val newStudent = Student(
            id = idIncrement++,
            name = input.name,
            age = input.age
        )

        return students.add(newStudent).also {
            if (it) updateFile()
        }
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

        return updated.also {
            if (it) updateFile()
        }
    }
}