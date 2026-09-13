import java.math.BigInteger

fun main() {
    val n = readLine()!!.trim().toInt()
    var result = BigInteger.ONE
    for (i in 1..n) {
        result *= BigInteger.valueOf(i.toLong())
    }
    println(result)
}
