fun main() {
    val krutApp = KrutApp()

    krutApp.listen(
        port = 8080,
        host = "0.0.0.0"
    )
}