data class Address(
    val street: String,
    val city: String?,
    val postalCode: String?
)

data class Client(
    val id: Int,
    val name: String,
    val email: String?,
    val address: Address?,
    val extraData: Any?
)

val clientList = listOf(
    Client(
        id = 1,
        name = "Олена",
        email = "olena@example.com",
        address = Address("вул. Саксаганського, 10", "Київ", "01033"),
        extraData = "VIP-клієнт"
    ),
    Client(
        id = 2,
        name = "Богдан",
        email = null,
        address = Address("вул. Городоцька, 45", "Львів", null),
        extraData = 42
    ),
    Client(
        id = 3,
        name = "Марія",
        email = "maria@example.com",
        address = null,
        extraData = null
    ),
    Client(
        id = 4,
        name = "Дмитро",
        email = null,
        address = Address("вул. Соборна, 1", null, null),
        extraData = "Очікує дзвінка"
    )
)

fun getShippingLabel(client: Client): String {
    val address = client.address ?: return "Самовивіз: Клієнт ${client.name} не надав адреси"
    val city = address.city ?: "Місто не вказано"
    val postalCode = address.postalCode ?: "Індекс невідомий"
    return "${address.street}, $city, $postalCode"
}

fun printClientNote(client: Client) {
    val note = client.extraData as? String ?: "Додаткові примітки відсутні"
    println("Клієнт ${client.name} -> Примітка: $note")
}

fun getClientEmailOrThrow(client: Client): String {
    return client.email ?: throw IllegalArgumentException("Клієнт з ID ${client.id} не має електронної пошти!")
}

// !! доречно використовувати лише тоді, коли ви на 100% впевнені (з бізнес-логіки чи попередніх перевірок), що значення не може бути null, і готові свідомо ризикнути винятком як сигналом про порушений інваріант.
fun forceGetPostalCode(client: Client): String {
    return client.address!!.postalCode!!
}

fun main() {
    println("--- Завдання 1 ---")
    clientList.take(3).forEach { client ->
        println("Адреса ${client.name}: ${getShippingLabel(client)}")
    }

    println()
    println("--- Завдання 2 ---")
    clientList.forEach { printClientNote(it) }

    println()
    println("--- Завдання 3 ---")
    val emails = clientList.map { it.email }.filterNotNull()
    val shortestLength = emails.minOfOrNull { it.length }
    println("База email для розсилки: $emails")
    println("Довжина найкоротшого email: $shortestLength")

    println()
    println("--- Завдання 4 ---")
    try {
        getClientEmailOrThrow(clientList[1])
    } catch (e: IllegalArgumentException) {
        println("Перехоплено виняток: ${e.message}")
    }

    println()
    println("--- Завдання 5 ---")
    println("Індекс клієнта ${clientList[0].name}: ${forceGetPostalCode(clientList[0])}")
    try {
        forceGetPostalCode(clientList[1])
    } catch (e: NullPointerException) {
        println("Перехоплено очікуваний NPE: ${e.message}")
    }
}
