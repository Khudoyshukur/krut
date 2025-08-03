import model.MessageResponse
import model.Student
import model.StudentsList
import model.input.StudentInput
import repository.impl.StudentRepositoryImpl
import repository.StudentRepository

val repo: StudentRepository = StudentRepositoryImpl()

fun main() {
    val app = KrutApp()

    app.get("/students") {
        val students = repo.getAllStudents()
        respondJson(data = StudentsList(students))
    }

    app.put("/students") {
        val student = this.body<Student>()

        if (repo.updateStudent(student)) {
            respondJson200(data = MessageResponse(message = "Updated successfully"))
        } else {
            respondJson404(data = MessageResponse(message = "Not found"))
        }
    }

    app.delete("/students/:id") {
        val studentId = pathParams["id"]!!.toLong()

        if (repo.deleteStudent(studentId)) {
            respondJson200(data = MessageResponse(message = "Deleted successfully"))
        } else {
            respondJson404(data = MessageResponse(message = "Not found"))
        }
    }

    app.post("/students") {
        val input = body<StudentInput>()
        val student = repo.insertStudent(input)

        respondJson200(data = student)
    }

    app.listen(port = 8080)
}