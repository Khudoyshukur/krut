package utils

import KrutApp
import KrutMiddleWare

fun krut(
    globalMiddleWares: List<KrutMiddleWare> = emptyList(),
    block: KrutApp.() -> Unit
): KrutApp {
    return KrutApp().apply(block)
}