import utils.tryOperation

enum class KrutMethod {
    GET, POST, PUT, DELETE;

    companion object {
        fun valueOfOrNull(method: String): KrutMethod? {
            return tryOperation { KrutMethod.valueOf(method.uppercase()) }
        }
    }
}

