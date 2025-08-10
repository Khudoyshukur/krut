# Krut - Lightweight Kotlin Web Framework

Krut is a modern, lightweight web framework for Kotlin that provides a simple and intuitive API for building web applications. It supports both HTTP Server and Tomcat engines, offers middleware support, and includes built-in JSON serialization.

## Features

- 🚀 **Lightweight & Fast**: Minimal overhead with high performance
- 🔧 **Multiple Engine Support**: Choose between HTTP Server and Tomcat
- 🛡️ **Middleware Support**: Global and route-specific middleware
- 📦 **Built-in JSON Serialization**: Automatic request/response JSON handling
- 🎯 **Type-Safe Routing**: Path parameters with type safety
- ⚡ **Coroutine Support**: Built on Kotlin coroutines for async operations
- 🔌 **Easy to Use**: Simple, intuitive API design

## Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.khudoyshukur:krut-core:1.0.0")
}
```

## Quick Start

Here's a simple "Hello World" example:

```kotlin
import engine.EngineType
import util.respondJson

fun main() {
    val app = KrutApp()
    
    app.get("/hello") { request ->
        respondJson(
            status = 200,
            body = mapOf("message" to "Hello, World!")
        )
    }
    
    app.listen(
        port = 8080,
        host = "0.0.0.0",
        engineType = EngineType.TOMCAT
    )
}
```

## Basic Usage

### Creating an Application

```kotlin
val app = KrutApp()
```

### Defining Routes

Krut supports all HTTP methods:

```kotlin
// GET request
app.get("/users") { request ->
    // Handle GET request
}

// POST request
app.post("/users") { request ->
    // Handle POST request
}

// PUT request
app.put("/users/:id") { request ->
    // Handle PUT request
}

// DELETE request
app.delete("/users/:id") { request ->
    // Handle DELETE request
}
```

### Path Parameters

Use `:paramName` syntax for path parameters:

```kotlin
app.get("/users/:userId") { request ->
    val userId = request.pathParams["userId"] // Access path parameter
    // Handle request
}
```

### Starting the Server

```kotlin
app.listen(
    port = 8080,
    host = "0.0.0.0",
    engineType = EngineType.TOMCAT // or EngineType.HTTP_SERVER
)
```

## Request Handling

### Accessing Request Data

```kotlin
app.post("/users") { request ->
    // HTTP Method
    val method = request.method
    
    // Path
    val path = request.path
    
    // Headers
    val contentType = request.getHeader("Content-Type")
    
    // Query Parameters
    val page = request.queryParams["page"]
    
    // Path Parameters
    val userId = request.pathParams["userId"]
    
    // Request Body (JSON)
    val userData = request.body<User>()
}
```

### JSON Request Body

Automatically deserialize JSON request bodies:

```kotlin
data class User(
    val name: String,
    val email: String
)

app.post("/users") { request ->
    val user = request.body<User>() // Automatic JSON deserialization
    // Process user data
}
```

## Response Handling

### JSON Responses

Use the `respondJson` utility for JSON responses:

```kotlin
import util.respondJson

app.get("/users") { request ->
    val users = listOf(
        User(1, "John Doe", "john@example.com"),
        User(2, "Jane Smith", "jane@example.com")
    )
    
    respondJson(
        status = 200,
        body = users
    )
}
```

### Custom Responses

Create custom responses:

```kotlin
app.get("/plain-text") { request ->
    KrutResponse(
        status = 200,
        headers = mapOf("Content-Type" to "text/plain"),
        body = "Hello, World!".toByteArray()
    )
}
```

## Middleware

### Global Middleware

Apply middleware to all routes:

```kotlin
val loggingMiddleware: KrutMiddleware = { request, next ->
    println("Request: ${request.method} ${request.path}")
    val response = next(request)
    println("Response status: ${response.status}")
    response
}

val app = KrutApp(globalMiddlewares = listOf(loggingMiddleware))
```

### Route-Specific Middleware

Apply middleware to specific routes:

```kotlin
val authMiddleware: KrutMiddleware = { request, next ->
    val token = request.getHeader("Authorization")
    if (token != "valid-token") {
        respondJson(
            status = 401,
            body = mapOf("error" to "Unauthorized")
        )
    } else {
        next(request)
    }
}

app.post("/protected", middlewares = listOf(authMiddleware)) { request ->
    // This route requires authentication
    respondJson(status = 200, body = mapOf("message" to "Protected data"))
}
```

### Middleware Chain

Middleware is executed in the order provided:

```kotlin
val corsMiddleware: KrutMiddleware = { request, next ->
    val response = next(request)
    KrutResponse(
        status = response.status,
        headers = response.headers + mapOf(
            "Access-Control-Allow-Origin" to "*",
            "Access-Control-Allow-Methods" to "GET, POST, PUT, DELETE"
        ),
        body = response.body
    )
}

val app = KrutApp(globalMiddlewares = listOf(loggingMiddleware, corsMiddleware))
```

## Engine Types

Krut supports two engine types:

### Tomcat Engine (Default)

```kotlin
app.listen(
    port = 8080,
    host = "0.0.0.0",
    engineType = EngineType.TOMCAT
)
```

### HTTP Server Engine

```kotlin
app.listen(
    port = 8080,
    host = "0.0.0.0",
    engineType = EngineType.HTTP_SERVER
)
```

## Complete Example

Here's a complete REST API example for managing students:

```kotlin
import engine.EngineType
import model.Student
import model.StudentInput
import model.MessageResponse
import util.respondJson

data class Student(
    val id: Long,
    val name: String,
    val age: Int
)

data class StudentInput(
    val name: String,
    val age: Int
)

data class MessageResponse(
    val message: String
)

fun main() {
    val students = mutableListOf<Student>()
    var nextId = 1L
    
    val app = KrutApp()
    
    // Get all students
    app.get("/students") { request ->
        respondJson(
            status = 200,
            body = students
        )
    }
    
    // Add a new student
    app.post("/students") { request ->
        val input = request.body<StudentInput>()
        val student = Student(nextId++, input.name, input.age)
        students.add(student)
        
        respondJson(
            status = 201,
            body = MessageResponse("Student added successfully")
        )
    }
    
    // Update a student
    app.put("/students/:id") { request ->
        val id = request.pathParams["id"]!!.toLong()
        val input = request.body<StudentInput>()
        
        val index = students.indexOfFirst { it.id == id }
        if (index == -1) {
            respondJson(
                status = 404,
                body = MessageResponse("Student not found")
            )
        } else {
            students[index] = Student(id, input.name, input.age)
            respondJson(
                status = 200,
                body = MessageResponse("Student updated successfully")
            )
        }
    }
    
    // Delete a student
    app.delete("/students/:id") { request ->
        val id = request.pathParams["id"]!!.toLong()
        val removed = students.removeIf { it.id == id }
        
        if (removed) {
            respondJson(
                status = 200,
                body = MessageResponse("Student deleted successfully")
            )
        } else {
            respondJson(
                status = 404,
                body = MessageResponse("Student not found")
            )
        }
    }
    
    app.listen(
        port = 8080,
        host = "0.0.0.0",
        engineType = EngineType.TOMCAT
    )
}
```

## API Reference

### KrutApp

- `KrutApp(globalMiddlewares: List<KrutMiddleware> = listOf())` - Create a new application
- `get(path: String, middlewares: List<KrutMiddleware> = listOf(), handler: KrutHandler)` - Register GET route
- `post(path: String, middlewares: List<KrutMiddleware> = listOf(), handler: KrutHandler)` - Register POST route
- `put(path: String, middlewares: List<KrutMiddleware> = listOf(), handler: KrutHandler)` - Register PUT route
- `delete(path: String, middlewares: List<KrutMiddleware> = listOf(), handler: KrutHandler)` - Register DELETE route
- `listen(port: Int, host: String, engineType: EngineType = EngineType.TOMCAT)` - Start the server

### KrutRequest

- `method: KrutMethod` - HTTP method
- `path: String` - Request path
- `queryParams: Map<String, String>` - Query parameters
- `pathParams: Map<String, String>` - Path parameters
- `body: InputStream` - Request body
- `getHeader(header: String): String?` - Get header value
- `body<T>(): T` - Deserialize JSON body

### KrutResponse

- `status: Int` - HTTP status code
- `headers: Map<String, String>` - Response headers
- `body: ByteArray` - Response body

### Utilities

- `respondJson(status: Int, headers: Map<String, String> = mapOf(), body: T): KrutResponse` - Create JSON response

## Requirements

- Kotlin 1.9+
- JVM 17+
- Kotlinx Serialization
- Kotlinx Coroutines

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. 