import model.MessageResponse
import model.Student
import model.StudentsList
import model.input.StudentInput
import repository.impl.StudentRepositoryImpl
import repository.StudentRepository

val repo: StudentRepository = StudentRepositoryImpl()

fun main() {
    val logHandler: KrutMiddleWare =  { req, next ->
        val resp = next(req)
        println("Executed ${req.method} ${req.path}, responded with ${resp.status}")

        resp
    }

    val app = KrutApp(globalMiddleWares = listOf(logHandler))

    app.get("/students") {
        val students = repo.getAllStudents()
        respondJson(data = StudentsList(students))
    }

    app.put("/students") { req ->
        val student = req.body<Student>()

        if (repo.updateStudent(student)) {
            respondJson200(data = MessageResponse(message = "Updated successfully"))
        } else {
            respondJson404(data = MessageResponse(message = "Not found"))
        }
    }

    app.delete("/students/:id") { req ->
        val studentId = req.pathParams["id"]!!.toLong()

        if (repo.deleteStudent(studentId)) {
            respondJson200(data = MessageResponse(message = "Deleted successfully"))
        } else {
            respondJson404(data = MessageResponse(message = "Not found"))
        }
    }

    app.post("/students") { req ->
        val input = req.body<StudentInput>()
        val student = repo.insertStudent(input)

        respondJson200(data = student)
    }

    app.listen(port = 8080)
}