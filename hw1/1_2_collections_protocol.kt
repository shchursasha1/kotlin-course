fun main() {
    val SUPPORTED = setOf("HTTP", "HTTPS", "FTP")
    val requested = "smtp"
    val isSupported = SUPPORTED.contains(requested.uppercase())
    println("Support for $requested: $isSupported")
}
