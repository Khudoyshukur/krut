import model.Student
import model.StudentInput
import model.MessageResponse
import repository.StudentRepository
import repository.impl.FileStudentRepository
import repository.impl.InMemoryStudentRepository
import util.respondJson

// http request body (json body)

// student management app

// get all students
// add student
// remove student
// update student

fun main() {
    val repo: StudentRepository = FileStudentRepository()
    val krutApp = KrutApp()

    krutApp.get("/students") {
        respondJson(
            status = 200,
            body = repo.getAll()
        )
    }

    krutApp.post("/students") {
        val input = it.body<StudentInput>()
        repo.addStudent(input)

        respondJson(
            status = 200,
            body = MessageResponse("Student added successfully")
        )
    }

    krutApp.put("/students") {
        val updatedStudent = it.body<Student>()
        if (repo.getStudentById(updatedStudent.id) == null) {
            respondJson(
                status = 404,
                body = MessageResponse("Student not found")
            )
        } else {
            repo.updateStudent(updatedStudent)
            respondJson(
                status = 200,
                body = MessageResponse("Student updated successfully")
            )
        }
    }

    krutApp.delete("/students/:studentId") {
        val studentId = it.pathParams["studentId"]!!.toLong()
        if (repo.getStudentById(studentId) == null) {
            respondJson(
                status = 404,
                body = MessageResponse("Student not found")
            )
        } else {
            repo.removeStudent(studentId)

            respondJson(
                status = 200,
                body = MessageResponse("Student deleted successfully")
            )
        }
    }

    krutApp.listen(
        port = 8080,
        host = "0.0.0.0"
    )
}
