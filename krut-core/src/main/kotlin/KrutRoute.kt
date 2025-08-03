
data class KrutRoute(
    val method: KrutMethod,
    val path: String,
    val pathRegex: Regex,
    val paramNames: List<String>,
    val handler: KrutHandler,
)

internal fun compilePath(template: String): Pair<Regex, List<String>> {
    val paramNames = mutableListOf<String>()
    val pattern = template.split("/").joinToString("/") {
        if (it.startsWith(":")) {
            paramNames += it.drop(1)
            "([^/]+)"
        } else it
    }
    return Regex("^$pattern$") to paramNames
}