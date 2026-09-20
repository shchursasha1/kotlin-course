fun stringFilter(strings: Collection<String>, predicate: (String) -> Boolean): Collection<String> {
    return strings.filter(predicate)
}

fun main() {
    val words = listOf("abc123", "hello", "test5", "world")
    val withDigits = stringFilter(words) { word -> word.any { it.isDigit() } }
    println(withDigits)
}
