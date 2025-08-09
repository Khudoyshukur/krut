
data class KrutRoute(
    val method: KrutMethod,
    val path: String,
    val pathRegex: Regex,
    val pathNames: List<String>,
    val handler: KrutHandler,
)

fun getRegexAndPathNames(path: String): Pair<Regex, List<String>> {
    // students/:studentId/room/:roomId
    // students/([^/]+)/room/([^/]+)
    // [studentId, roomId]

    val parts = path.split("/")
    val names = mutableListOf<String>()
    val regexBuilder = StringBuilder("")
    regexBuilder.append("^")

    // students/:studentId/room/:roomId
    // students/:studentId/room/:roomId
    // [students, :studentId, room, :roomId]

    parts.forEachIndexed { index, part ->
        if (part.startsWith(':')) {
            names.add(part.removePrefix(":"))
            regexBuilder.append("([^/]+)")
        } else {
            regexBuilder.append(part)
        }

        if (index != parts.lastIndex) {
            regexBuilder.append("/")
        }
    }

    regexBuilder.append("$")

    return Pair(
        Regex(pattern = regexBuilder.toString()),
        names
    )
}