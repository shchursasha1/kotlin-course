import kotlin.random.Random

fun generateRandomArray(size: Int, maxValue: Int): IntArray? {
    if (size <= 0 || maxValue <= 0) return null
    return IntArray(size) { Random.nextInt(0, maxValue + 1) }
}

fun main() {
    generateRandomArray(size = 10, maxValue = 50)
        ?.apply {
            for (i in indices) {
                this[i] = if (this[i] % 2 != 0) this[i] * 2 else this[i] / 2
            }
        }
        ?.also { array -> println("Модифікований масив: ${array.contentToString()}") }
        ?.let { array -> array.maxOrNull() }
        ?.let { max -> println("Максимальне значення масиву: $max") }
        ?: println("Помилка вхідних даних")
}
