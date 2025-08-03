package utils

internal fun <T: Any> tryOperation(block: () -> T): T? {
    return try {
        return block.invoke()
    } catch (throwable: Throwable) {
        null
    }
}