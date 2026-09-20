fun multiplyForEach(a: Int, b: Int): Int {
    var product = 0
    (1..b).forEach { product += a }
    return product
}

fun multiplyRepeat(a: Int, b: Int): Int {
    var product = 0
    repeat(b) { product += a }
    return product
}

fun main() {
    val (a, b) = readLine()!!.trim().split(" ").map { it.toInt() }
    println(multiplyForEach(a, b))
    println(multiplyRepeat(a, b))
}
