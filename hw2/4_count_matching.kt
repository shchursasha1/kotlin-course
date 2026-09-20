fun countMatching(strings: Collection<String>, predicate: (String) -> Boolean): Int {
    return strings.count(predicate)
}

fun main() {
    val words = listOf("abc123", "hello", "test5", "world")
    val hasDigit: (String) -> Boolean = { word -> word.any { it.isDigit() } }
    println(countMatching(words, hasDigit))
}
