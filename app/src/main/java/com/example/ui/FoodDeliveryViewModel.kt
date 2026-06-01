package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FoodData
import com.example.data.FoodItem
import com.example.data.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Order(
    val id: String,
    val items: List<Pair<FoodItem, Int>>,
    val totalAmount: Double,
    val date: String,
    val status: String // "Preparing", "Out for Delivery", "Delivered"
)

data class FoodUiState(
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val activeTab: Int = 0, // 0 = Menu / Home, 1 = Orders, 2 = AI Suggestion
    val cartItems: Map<Int, Int> = emptyMap(), // itemId -> quantity
    val orders: List<Order> = emptyList(), // past and active orders
    val deliveryAddress: String = "121 Gourmet Boulevard, Cuisine District",
    val aiQuery: String = "",
    val aiResponse: String = "",
    val isAiLoading: Boolean = false,
    val favoriteIds: Set<Int> = emptySet(),
    val activeCategoryFilter: String = "All",
    val isEditingAddress: Boolean = false
)

class FoodDeliveryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FoodUiState())
    val uiState: StateFlow<FoodUiState> = _uiState.asStateFlow()

    init {
        // Pre-populate one historical completed order to make the Orders tab look realistic right away!
        val pastOrder = Order(
            id = "#FD-9042",
            items = listOf(
                FoodData.sampleItems[0] to 1, // Pizza
                FoodData.sampleItems[5] to 2  // 2 Coffees
            ),
            totalAmount = 36.00,
            date = "Today, 12:30 PM",
            status = "Delivered"
        )
        _uiState.update { it.copy(orders = listOf(pastOrder)) }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateActiveTab(tabIndex: Int) {
        _uiState.update { it.copy(activeTab = tabIndex) }
    }

    fun addToCart(itemId: Int) {
        _uiState.update { state ->
            val currentQty = state.cartItems[itemId] ?: 0
            val updated = state.cartItems.toMutableMap()
            updated[itemId] = currentQty + 1
            state.copy(cartItems = updated)
        }
    }

    fun removeFromCart(itemId: Int) {
        _uiState.update { state ->
            val currentQty = state.cartItems[itemId] ?: return@update state
            val updated = state.cartItems.toMutableMap()
            if (currentQty <= 1) {
                updated.remove(itemId)
            } else {
                updated[itemId] = currentQty - 1
            }
            state.copy(cartItems = updated)
        }
    }

    fun clearCart() {
        _uiState.update { it.copy(cartItems = emptyMap()) }
    }

    fun toggleFavorite(itemId: Int) {
        _uiState.update { state ->
            val favorites = state.favoriteIds.toMutableSet()
            if (favorites.contains(itemId)) {
                favorites.remove(itemId)
            } else {
                favorites.add(itemId)
            }
            state.copy(favoriteIds = favorites)
        }
    }

    fun setEditingAddress(editing: Boolean) {
        _uiState.update { it.copy(isEditingAddress = editing) }
    }

    fun updateDeliveryAddress(newAddress: String) {
        _uiState.update { it.copy(deliveryAddress = newAddress, isEditingAddress = false) }
    }

    fun updateAiQuery(query: String) {
        _uiState.update { it.copy(aiQuery = query) }
    }

    fun sendAiPrompt() {
        val queryToSend = _uiState.value.aiQuery.trim()
        if (queryToSend.isEmpty()) return

        _uiState.update { it.copy(isAiLoading = true, aiResponse = "") }

        viewModelScope.launch {
            try {
                val recommendation = GeminiService.getFoodRecommendation(queryToSend)
                _uiState.update { it.copy(aiResponse = recommendation, isAiLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        aiResponse = "Oops, I encountered an issue parsing the culinary recommendation. Try ordering our famous Italian Pizza Margherita directly!", 
                        isAiLoading = false
                    ) 
                }
            }
        }
    }

    fun checkout() {
        val state = _uiState.value
        if (state.cartItems.isEmpty()) return

        val orderItems = state.cartItems.mapNotNull { (itemId, qty) ->
            val item = FoodData.sampleItems.find { it.id == itemId }
            if (item != null) item to qty else null
        }

        val total = orderItems.sumOf { (item, qty) -> item.price * qty }
        val randomId = (1000..9999).random()

        val newOrder = Order(
            id = "#FD-$randomId",
            items = orderItems,
            totalAmount = total,
            date = "Just now",
            status = "Preparing"
        )

        _uiState.update { 
            it.copy(
                orders = listOf(newOrder) + it.orders,
                cartItems = emptyMap(),
                activeTab = 1 // Switch to Orders tab so they can see it preparing!
            )
        }
    }

    // Filter items based on selected category and search query
    fun getFilteredFoodItems(): List<FoodItem> {
        val state = _uiState.value
        return FoodData.sampleItems.filter { item ->
            val matchesCategory = state.selectedCategory == "All" || item.category.equals(state.selectedCategory, ignoreCase = true)
            val matchesSearch = item.name.lowercase().contains(state.searchQuery.lowercase()) ||
                                item.description.lowercase().contains(state.searchQuery.lowercase())
            matchesCategory && matchesSearch
        }
    }

    // Helper to get total number of items in the cart
    fun getCartCount(): Int {
        return _uiState.value.cartItems.values.sum()
    }

    // Helper to get cart total amount
    fun getCartTotal(): Double {
        return _uiState.value.cartItems.entries.sumOf { (itemId, qty) ->
            val item = FoodData.sampleItems.find { it.id == itemId }
            (item?.price ?: 0.0) * qty
        }
    }
}
