fun main() {
    val numbers = IntArray(100) { (-100..100).random() }

    val result = numbers.toList()
        .filter { it > 0 }
        .filter { it % 5 == 0 }
        .map { it * it }
        .sortedDescending()

    println("Максимум: ${result.maxOrNull()}")
    println("Мінімум: ${result.minOrNull()}")

    val strings = result.map { it.toString() }
    println(strings.joinToString(" "))
}
