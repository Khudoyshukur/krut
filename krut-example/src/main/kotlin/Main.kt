fun main() {
    val krutApp = KrutApp()

    krutApp.get("/students") {
        KrutResponse(
            status = 200,
            headers = mapOf(),
            body = "Hello from students GET".toByteArray()
        )
    }

    krutApp.put("/students/:studentId") {
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

// GET, POST, PUT, DELETE
// student management

// {baseUrl}/students GET
// {baseUrl}/students POST
// {baseUrl}/students/{studentId} PUT
// {baseUrl}/students/{studentId} DELETE

//{baseUrl}/students?age=18 GET
