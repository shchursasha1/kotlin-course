fun repeatN(n: Int, action: () -> Unit) {
    repeat(n) { action() }
}

fun main() {
    repeatN(5) { println("Hello") }
}
