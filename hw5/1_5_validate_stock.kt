fun validateStock(requested: Int?, available: Int?): Int {
    val req = requested ?: return -1
    val avail = available ?: return -1
    if (req < 0) return -1
    if (req > avail) return -1
    return req
}

fun main() {
    println(validateStock(5, 10))
    println(validateStock(null, 10))
    println(validateStock(-2, 10))
}
