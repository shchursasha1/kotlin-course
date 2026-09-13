fun main() {
    var n = readLine()!!.trim().toLong()
    var reversed = 0L
    while (n > 0) {
        reversed = reversed * 10 + n % 10
        n /= 10
    }
    println(reversed)
}
