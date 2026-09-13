import kotlin.math.sqrt

class Complex(val re: Double, val im: Double)

fun abs(x: Int): Int = if (x >= 0) x else -x

fun abs(x: Double): Double = if (x >= 0.0) x else -x

fun abs(z: Complex): Double = sqrt(z.re * z.re + z.im * z.im)

fun main() {
    println(abs(-5))
    println(abs(-3.14))
    println(abs(Complex(3.0, 4.0)))
}
