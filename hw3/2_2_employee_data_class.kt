data class Employee(val firstName: String, val lastName: String, val position: String) {
    var bonus: Int = 0
}

fun main() {
    val emp1 = Employee("Іван", "Петренко", "Developer")
    val emp2 = Employee("Іван", "Петренко", "Developer")
    val emp3 = emp1

    println(emp1)

    println(emp1 === emp2)
    println(emp1 == emp2)
    println(emp1 === emp3)

    val (name, surname, pos) = emp1
    println("$name $surname $pos")

    val promoted = emp1.copy(position = "Senior Developer")
    println(promoted)

    emp1.bonus = 100
    val emp4 = emp1.copy()
    emp4.bonus = 200
    println(emp1 == emp4)
}
