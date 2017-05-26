package io.github.vyo.twig.serialiser

/**
 * Created by Manuel Weidmann <manuel.weidmann@7p-group.com> on 26.05.17.
 */
val simpleSerialiser = { any: Any ->

    when (any) {
        is Boolean,
        is Double,
        is Float,
        is Long,
        is Int,
        is Short,
        is Byte,
        is Char -> simplePrimitiveSerialiser(any)
        is Array<*> -> simpleArraySerialiser(any)
        is Collection<*> -> simpleCollectionSerialiser(any)
        else -> "\"${escapeSpecialChars(any.toString())}\""
    }

}

private fun simplePrimitiveSerialiser(any: Any): String {
    return "$any"
}

private fun simpleArraySerialiser(array: Array<*>): String {
    var string = "["

    for (element: Any? in array) {
        if (element is Any) {
            string += simpleSerialiser(element)
            string += ","
        }
    }

    string += "]"

    return string.replace(",]", "]")
}

private fun simpleCollectionSerialiser(collection: Collection<*>): String {
    var string = "["

    for (element: Any? in collection) {
        if (element is Any) {
            string += simpleSerialiser(element)
            string += ","
        }
    }

    string += "]"

    return string.replace(",]", "]")
}

private fun escapeSpecialChars(string: String): String {

    var escapedString: String = string
    escapedString = escapedString.replace("\\", "\\\\")
    escapedString = escapedString.replace("\n", "\\n")
    escapedString = escapedString.replace("\r", "\\r")
    escapedString = escapedString.replace("\b", "\\b")
    escapedString = escapedString.replace("\t", "\\t")
    escapedString = escapedString.replace("\"", "\\\"")

    return escapedString
}
