fun main() {
    val (a, b) = readLine()!!.trim().split(" ").map { it.toInt() }
    var product = 0
    for (i in 1..b) {
        product += a
    }
    println(product)
}
