fun main() {
    val month = readLine()!!.trim().toInt()
    val season = when (month) {
        12, 1, 2 -> "Winter"
        3, 4, 5 -> "Spring"
        6, 7, 8 -> "Summer"
        else -> "Autumn"
    }
    println(season)
}
