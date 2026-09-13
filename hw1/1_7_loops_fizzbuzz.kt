fun main() {
    for (i in 1..100) {
        val result = when {
            i % 15 == 0 -> "fizzbuzz"
            i % 3 == 0 -> "fizz"
            i % 5 == 0 -> "buzz"
            else -> i.toString()
        }
        println(result)
    }
}
