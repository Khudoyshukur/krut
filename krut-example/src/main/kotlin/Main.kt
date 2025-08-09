import model.Student
import util.respondJson

fun main() {
    val krutApp = KrutApp()

    krutApp.get("/students/") {
        val list = listOf(
            Student(
                id = 1,
                name = "Khudoyshukur",
                age = 25
            )
        )

        respondJson(
            status = 200,
            body = list
        )
    }

    krutApp.get("/students/:studentId/room/:roomId") {
        println("Student id is -> ${it.pathParams["studentId"]}, Room id is -> ${it.pathParams["roomId"]}")

        KrutResponse(
            status = 200,
            headers = mapOf(),
            body = "Hello from students abs GET".toByteArray()
        )
    }

    krutApp.listen(
        port = 8080,
        host = "0.0.0.0"
    )
}

// query param
// pathParams
// serialization types
//

// GET, POST, PUT, DELETE
// student management

// {baseUrl}/students GET
// {baseUrl}/students POST
// {baseUrl}/students/{studentId} PUT
// {baseUrl}/students/{studentId} DELETE

//{baseUrl}/students?age=18 GET
