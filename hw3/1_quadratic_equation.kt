import kotlin.math.sqrt
import kotlin.random.Random

sealed interface Roots {
    data class TwoRoots(val x1: Double, val x2: Double) : Roots
    data class OneRoot(val x: Double) : Roots
    data object NoRealRoots : Roots
}

class QuadraticEquation(a: Double, var b: Double, var c: Double) {

    init {
        require(a != 0.0) { "Коефіцієнт 'a' не може бути нулем!" }
    }

    var a: Double = a
        set(value) {
            require(value != 0.0) { "Коефіцієнт 'a' не може бути нулем!" }
            field = value
        }

    constructor(a: Int, b: Int, c: Int) : this(a.toDouble(), b.toDouble(), c.toDouble())

    constructor(b: Double, c: Double) : this(1.0, b, c)

    val discriminant: Double
        get() = b * b - 4 * a * c

    fun solve(): List<Double> = when {
        discriminant > 0 -> {
            val sqrtD = sqrt(discriminant)
            listOf((-b + sqrtD) / (2 * a), (-b - sqrtD) / (2 * a))
        }
        discriminant == 0.0 -> listOf(-b / (2 * a))
        else -> emptyList()
    }

    fun solveRoots(): Roots = when {
        discriminant > 0 -> {
            val sqrtD = sqrt(discriminant)
            Roots.TwoRoots((-b + sqrtD) / (2 * a), (-b - sqrtD) / (2 * a))
        }
        discriminant == 0.0 -> Roots.OneRoot(-b / (2 * a))
        else -> Roots.NoRealRoots
    }

    override fun toString(): String {
        val aTerm = when (a) {
            1.0 -> "x^2"
            -1.0 -> "-x^2"
            else -> "${a}x^2"
        }
        val bTerm = when {
            b == 0.0 -> ""
            b > 0 -> " + ${b}x"
            else -> " - ${-b}x"
        }
        val cTerm = when {
            c == 0.0 -> ""
            c > 0 -> " + $c"
            else -> " - ${-c}"
        }
        return "$aTerm$bTerm$cTerm = 0"
    }
}

fun main() {
    val eq1 = QuadraticEquation(2.0, -4.0, 2.0)
    println(eq1)
    println(eq1.discriminant)
    println(eq1.solve())

    val eq2 = QuadraticEquation(1, 5, -6)
    println(eq2)
    println(eq2.solve())

    val eq3 = QuadraticEquation(5.0, 6.0)
    println(eq3)
    println(eq3.solve())

    eq1.a = 1.0
    eq1.b = 5.0
    eq1.c = -6.0
    println(eq1)
    println(eq1.discriminant)

    when (val roots = eq2.solveRoots()) {
        is Roots.TwoRoots -> println("Два корені: ${roots.x1}, ${roots.x2}")
        is Roots.OneRoot -> println("Один корінь: ${roots.x}")
        Roots.NoRealRoots -> println("Дійсних коренів немає")
    }

    val equations = List(100) {
        var randomA = Random.nextInt(-50, 51)
        if (randomA == 0) randomA = 1
        val randomB = Random.nextInt(-50, 51)
        val randomC = Random.nextInt(-50, 51)
        QuadraticEquation(randomA, randomB, randomC)
    }

    val twoRootsEquations = equations.filter { it.discriminant > 0 }

    twoRootsEquations.forEach { equation ->
        println("$equation -> корені: ${equation.solve()}")
    }

    println("Кількість рівнянь із двома коренями: ${twoRootsEquations.size}")
}
