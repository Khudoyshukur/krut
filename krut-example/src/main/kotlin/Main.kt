import engine.EngineType
import model.MessageResponse
import model.Student
import model.input.StudentInput
import repository.StudentRepository
import repository.impl.StudentRepositoryImpl
import utils.krut

val repo: StudentRepository = StudentRepositoryImpl()

fun main() {
    val app = krut {
        get("/students") {
            val students = repo.getAllStudents()
            respondJson200(data = students)
        }

        put("/students") { req ->
            val student = req.body<Student>()

            if (repo.updateStudent(student)) {
                respondJson200(data = MessageResponse(message = "Updated successfully"))
            } else {
                respondJson404(data = MessageResponse(message = "Not found"))
            }
        }

        delete("/students/:id") { req ->
            val studentId = req.pathParams["id"]!!.toLong()

            if (repo.deleteStudent(studentId)) {
                respondJson200(data = MessageResponse(message = "Deleted successfully"))
            } else {
                respondJson404(data = MessageResponse(message = "Not found"))
            }
        }

        post("/students") { req ->
            val input = req.body<StudentInput>()
            val student = repo.insertStudent(input)

            respondJson200(data = student)
        }
    }

    app.listen(port = 8080, engineType = EngineType.HTTP_SERVER)
}