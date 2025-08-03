package repository.impl

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import model.Student
import model.input.StudentInput
import repository.StudentRepository
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStream

class StudentRepositoryImpl : StudentRepository {
    private var fakeId = 1L
    private var students = mutableListOf<Student>()
    private val studentsFile = File("krut-example/src/main/kotlin/repository/impl/students.json")

    init {
        try {
            if (studentsFile.exists().not()) {
                studentsFile.createNewFile()
            }

            val reader = FileReader(studentsFile)
            val storedStudents = Json.decodeFromString<List<Student>>(reader.readText())
            students.addAll(storedStudents)
        }
        catch (e: Exception) { }
        finally {
            fakeId = students.maxByOrNull { it.id }?.id?.plus(1) ?: 1L
        }
    }

    override suspend fun getAllStudents(): List<Student> {
        return students
    }

    override suspend fun insertStudent(input: StudentInput): Student {
        val student = Student.from(id = fakeId++, input = input)
        students.add(student)

        return student.also { updateStudentsFile() }
    }

    override suspend fun updateStudent(student: Student): Boolean {
        var found = false

        students = students.map { currStudent ->
            if (currStudent.id == student.id) student.also { found = true } else currStudent
        }.toMutableList()

        return found.also { if (it) updateStudentsFile() }
    }

    override suspend fun deleteStudent(id: Long): Boolean {
        return students.removeIf { it.id == id }.also { updateStudentsFile() }
    }

    private fun updateStudentsFile() {
        val writer = FileWriter(studentsFile)
        writer.write(Json.encodeToString(serializer(),  students))
        writer.close()
    }
}