class Employee(val firstName: String, val lastName: String, val position: String)

fun main() {
    val emp1 = Employee("Іван", "Петренко", "Developer")
    val emp2 = Employee("Іван", "Петренко", "Developer")
    val emp3 = emp1

    println(emp1)

    println(emp1 === emp2)
    println(emp1 == emp2)
    println(emp1 === emp3)
}
