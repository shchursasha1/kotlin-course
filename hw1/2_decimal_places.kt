fun main() {
    val x = readLine()!!.trim().toDouble()
    val n = readLine()!!.trim().toInt()
    println("%.${n}f".format(x))
}
