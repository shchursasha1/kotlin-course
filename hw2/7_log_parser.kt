fun main() {
    val rawLogs = listOf(
        "2026-09-01 INFO: User logged in",
        "2026-09-01 ERROR: 500 Internal Server Error",
        "2026-09-01 WARN: High memory usage",
        "2026-09-01 ERROR: 404 Not Found",
        "2026-09-01 INFO: Payment processed"
    )

    var infoCount = 0
    var warnCount = 0
    var errorCount = 0
    val errors = mutableListOf<String>()

    for (log in rawLogs) {
        when {
            log.contains("ERROR") -> {
                errorCount++
                errors.add(log)
            }
            log.contains("WARN") -> warnCount++
            log.contains("INFO") -> infoCount++
        }
    }

    println(
        """
        Звіт по логах сервера
        ----------------------
        INFO: $infoCount
        WARN: $warnCount
        ERROR: $errorCount

        Помилки:
        ${errors.joinToString("\n")}
        """.trimIndent()
    )
}
