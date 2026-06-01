package com.example.data

data class FoodItem(
    val id: Int,
    val name: String,
    val price: Double,
    val rating: Float,
    val category: String, // e.g., "Pizza", "Burger", "Drinks", "Dessert", "Chicken", "Pasta"
    val imageUrl: String,
    val description: String,
    val deliveryTime: String,
    val isBestSeller: Boolean = false
)

object FoodData {
    val sampleItems = listOf(
        FoodItem(
            id = 1,
            name = "Pizza Margherita",
            price = 20.00,
            rating = 4.9f,
            category = "Pizza",
            imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=500&auto=format&fit=crop&q=80",
            description = "Authentic Neapolitan pizza topped with rich San Marzano tomato paste, fresh buffalo mozzarella, aromatic basil leaves, and a drizzle of extra virgin olive oil.",
            deliveryTime = "20-25 min",
            isBestSeller = true
        ),
        FoodItem(
            id = 2,
            name = "Smoked Truffle Burger",
            price = 26.00,
            rating = 4.8f,
            category = "Burger",
            imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500&auto=format&fit=crop&q=80",
            description = "Premium dry-aged beef patty grilled to juicy perfection, layers of melted Swiss cheese, caramelized onions, wild mushrooms, and luxurious white truffle aioli.",
            deliveryTime = "15-20 min",
            isBestSeller = true
        ),
        FoodItem(
            id = 3,
            name = "Crunchy Fried Chicken",
            price = 18.00,
            rating = 4.7f,
            category = "Chicken",
            imageUrl = "https://images.unsplash.com/photo-1562967914-608f82629710?w=500&auto=format&fit=crop&q=80",
            description = "Crispy golden fried chicken marinated in a secret butter-milk blend of 11 herbs and spices. Juicy on the inside, exceptionally crunchy on the outside.",
            deliveryTime = "25-30 min"
        ),
        FoodItem(
            id = 4,
            name = "Club Sandwich Deluxe",
            price = 15.00,
            rating = 4.6f,
            category = "Sandwich",
            imageUrl = "https://images.unsplash.com/photo-1509722747041-616f39b57569?w=500&auto=format&fit=crop&q=80",
            description = "Toasted sourdough stacked with slow-roasted turkey breast, hickory smoked bacon, crisp butter lettuce, ripe tomatoes, and seasoned herb mayonnaise.",
            deliveryTime = "15-18 min"
        ),
        FoodItem(
            id = 5,
            name = "Creamy Pesto Pasta",
            price = 22.00,
            rating = 4.9f,
            category = "Pasta",
            imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500&auto=format&fit=crop&q=80",
            description = "Al dente penne pasta tossed in a rich, velvety basil pesto cream sauce, roasted pine nuts, grated aged Parmigiano-Reggiano, and tender garlic-grilled chicken.",
            deliveryTime = "20-25 min"
        ),
        FoodItem(
            id = 6,
            name = "Iced Latte Arabica",
            price = 8.00,
            rating = 4.5f,
            category = "Drinks",
            imageUrl = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=500&auto=format&fit=crop&q=80",
            description = "Chilled organic single-origin Arabica espresso shot combined with freshly textured creamy milk, served over artisanal slow-melting ice spheres.",
            deliveryTime = "10-12 min"
        )
    )
}
