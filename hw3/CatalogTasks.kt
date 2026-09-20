data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val rating: Double,
    val inStock: Boolean
)

val products = listOf(
    Product(1, "Google Pixel 9", "Smartphones", 899.0, 4.8, true),
    Product(2, "iPhone 16 Pro", "Smartphones", 1199.0, 4.9, true),
    Product(3, "Galaxy A55", "Smartphones", 399.0, 4.3, false),
    Product(4, "MacBook Air M3", "Laptops", 1299.0, 4.9, true),
    Product(5, "ThinkPad X1 Carbon", "Laptops", 1499.0, 4.6, false),
    Product(6, "Dell XPS 13", "Laptops", 1150.0, 4.4, true),
    Product(7, "Sony WH-1000XM5", "Audio", 349.0, 4.7, true),
    Product(8, "AirPods Pro 2", "Audio", 249.0, 4.8, false),
    Product(9, "Pixel Buds Pro 2", "Audio", 229.0, 4.5, true)
)

fun List<Product>.filterAndTransform(
    predicate: (Product) -> Boolean,
    transform: (Product) -> String
): List<String> {
    return this.filter(predicate).map(transform)
}

fun main() {
    products
        .filter { it.inStock && it.rating >= 4.7 && it.price < 1000.0 }
        .sortedByDescending { it.rating }
        .map { "Назва: ${it.name} | Рейтинг: ${it.rating} | Ціна: $${it.price}" }
        .forEach { println(it) }

    val expensiveLaptop = products.find { it.category == "Laptops" && it.price > 1200.0 }
    println(expensiveLaptop?.name ?: "Товар не знайдено")

    val hasExpensiveAudio = products.any { it.category == "Audio" && it.price > 300.0 }
    println(hasExpensiveAudio)

    val allSmartphonesHighRated = products.filter { it.category == "Smartphones" }.all { it.rating > 4.0 }
    println(allSmartphonesHighRated)

    val (available, outOfStock) = products.partition { it.inStock }
    println("У наявності: ${available.size}")
    println("Немає в наявності: ${outOfStock.size}")

    val byCategory = products.groupBy { it.category }

    byCategory.forEach { (category, items) ->
        val leader = items.maxByOrNull { it.price }
        println("Категорія $category -> Найдорожчий: ${leader?.name} ($${leader?.price})")
    }

    val promoMessages = products.filterAndTransform({ it.price < 300.0 }) {
        "Акційна ціна на ${it.name}: лише $${it.price}!"
    }
    promoMessages.forEach { println(it) }

    val inStockProducts = products.filter { it.inStock }
    val totalInStockPrice = inStockProducts.fold(0.0) { acc, product -> acc + product.price }
    val averageInStockPrice = totalInStockPrice / inStockProducts.size

    println("Загальна вартість товарів у наявності: $totalInStockPrice")
    println("Середня вартість товарів у наявності: $averageInStockPrice")
}
