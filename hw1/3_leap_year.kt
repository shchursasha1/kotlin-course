fun main() {
    val year = readLine()!!.trim().toInt()
    val isLeap = year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)
    println(if (isLeap) "YES" else "NO")
}
