fun main() {
    val text = readLine()!!
    val counts = text.groupingBy { it }.eachCount()
    for ((char, count) in counts) {
        println("'$char' - $count")
    }
}
