interface Messenger {
    fun sendMessage(message: String)
    fun receiveMessage(): String
}

class BasicMessenger : Messenger {
    override fun sendMessage(message: String) {
        println("Sending message: $message")
    }

    override fun receiveMessage(): String {
        return "You've got a new message!"
    }
}

class SmartMessenger(private val messenger: Messenger) : Messenger by messenger {
    override fun sendMessage(message: String) {
        println("Sending a smart message: $message")
        messenger.sendMessage("[smart] $message")
    }
}

fun main() {
    val basicMessenger = BasicMessenger()
    val smartMessenger = SmartMessenger(basicMessenger)

    basicMessenger.sendMessage("Hello!")
    println(smartMessenger.receiveMessage())
    smartMessenger.sendMessage("Hello from SmartMessenger!")
}
