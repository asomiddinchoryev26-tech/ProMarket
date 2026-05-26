package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import com.example.data.local.MarketplaceDatabase
import com.example.data.model.*
import com.example.data.repository.MarketplaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

data class CartProduct(
    val product: Product,
    val quantity: Int
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String, // "user", "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MarketplaceRepository

    init {
        val db = MarketplaceDatabase.getDatabase(application)
        repository = MarketplaceRepository(db)
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    // --- Localization & Style state ---
    private val _currentLanguage = MutableStateFlow("en") // "en", "uz", "ru"
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // --- Core data lists ---
    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistItems: StateFlow<List<WishlistItem>> = repository.wishlistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<LogEntry>> = repository.auditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Joined shopping cart items
    val cartProducts: StateFlow<List<CartProduct>> = combine(products, cartItems) { prodList, cartList ->
        cartList.mapNotNull { item ->
            val p = prodList.find { it.id == item.productId }
            p?.let { CartProduct(it, item.quantity) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Shopping Actions ---
    fun toggleLike(productId: String) {
        viewModelScope.launch {
            repository.toggleWishlist(productId)
            val name = products.value.find { it.id == productId }?.nameEn ?: ""
            val liked = repository.isLiked(productId)
            repository.logActivity("System", "${if (liked) "Liked" else "Unliked"} product: $name", "")
        }
    }

    fun addToCart(productId: String, qty: Int = 1) {
        viewModelScope.launch {
            val existing = cartItems.value.find { it.productId == productId }
            if (existing != null) {
                repository.insertCartItem(CartItem(productId, existing.quantity + qty))
            } else {
                repository.insertCartItem(CartItem(productId, qty))
            }
            val name = products.value.find { it.id == productId }?.nameEn ?: ""
            repository.logActivity("System", "Added $qty x '$name' to cart", "")
        }
    }

    fun updateCartQuantity(productId: String, qty: Int) {
        viewModelScope.launch {
            repository.updateCartQty(productId, qty)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.deleteCartItem(productId)
        }
    }

    // --- Search & Filters State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedBrand = MutableStateFlow<String?>(null)
    val selectedBrand: StateFlow<String?> = _selectedBrand.asStateFlow()

    private val _priceRangeMax = MutableStateFlow(500.0)
    val priceRangeMax: StateFlow<Double> = _priceRangeMax.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun selectBrand(brand: String?) {
        _selectedBrand.value = brand
    }

    fun setPriceMax(max: Double) {
        _priceRangeMax.value = max
    }

    // Filtered products list
    val filteredProducts: StateFlow<List<Product>> = combine(
        products, _searchQuery, _selectedCategory, _selectedBrand, _priceRangeMax
    ) { prodList, query, cat, brand, maxPrice ->
        prodList.filter { p ->
            val matchesQuery = query.isEmpty() || p.nameEn.contains(query, ignoreCase = true) ||
                    p.nameUz.contains(query, ignoreCase = true) || p.nameRu.contains(query, ignoreCase = true) ||
                    p.brand.contains(query, ignoreCase = true) || p.category.contains(query, ignoreCase = true)
            val matchesCategory = cat == null || p.category.equals(cat, ignoreCase = true)
            val matchesBrand = brand == null || p.brand.equals(brand, ignoreCase = true)
            val matchesPrice = p.price <= maxPrice
            matchesQuery && matchesCategory && matchesBrand && matchesPrice
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- AI Smart Recommendations & AI Semantic Search ---
    private val _aiRecommendationText = MutableStateFlow<String?>(null)
    val aiRecommendationText: StateFlow<String?> = _aiRecommendationText.asStateFlow()

    private val _isSearchingAi = MutableStateFlow(false)
    val isSearchingAi: StateFlow<Boolean> = _isSearchingAi.asStateFlow()

    fun performAiSemanticSearch(queryStr: String) {
        if (queryStr.isBlank()) return
        _isSearchingAi.value = true
        _searchQuery.value = queryStr
        _aiRecommendationText.value = null

        viewModelScope.launch {
            repository.logActivity("System", "Triggered AI Semantic Search", "Prompt: $queryStr")
            val systemPrompt = """
                You are high-performance ProMarket Hardware AI.
                Analyze the user's construction/hardware scenario and suggest which of our catalog items fits perfectly.
                We have: DeWalt Heavy-Duty Brushless Drill ($249.99), Milton Arc Core Welder ($479.50), Bosch Magnetic Screwdriver ($34.99), Makita cordless jigsaw ($189), Schneider smart circuit IoT breaker ($119.99), Titan Carbon Helmet & Harness ($74.95), Uzum galvanized anchors 100-pack ($44.90), Cemex Ultra-Crete rapid cement ($11.99).
                Explain in a friendly, enterprise startup way (under 3 or 4 concise bullet suggestions) which tool aligns and why. Highlight any electrical voltages or safety metrics in bold. Speak in the active language: ${currentLanguage.value}.
            """.trimIndent()

            val aiAnswer = GeminiClient.askGemini(
                prompt = "User scenario: $queryStr. Find best matching project tools from the database.",
                systemPrompt = systemPrompt
            )
            _aiRecommendationText.value = aiAnswer
            _isSearchingAi.value = false
        }
    }

    fun clearAiSearch() {
        _aiRecommendationText.value = null
    }

    // --- Checkout & Geography State ---
    private val _selectedRegion = MutableStateFlow("Tashkent")
    val selectedRegion: StateFlow<String> = _selectedRegion.asStateFlow()

    private val _expressDelivery = MutableStateFlow(false)
    val expressDelivery: StateFlow<Boolean> = _expressDelivery.asStateFlow()

    fun selectRegion(region: String) {
        _selectedRegion.value = region
    }

    fun toggleExpressDelivery() {
        _expressDelivery.value = !_expressDelivery.value
    }

    fun getDeliveryFee(): Double {
        val base = when (_selectedRegion.value) {
            "Tashkent" -> 4.99
            "Samarkand" -> 9.99
            "Bukhara" -> 12.99
            "Navoi" -> 14.99
            "Khorezm" -> 18.00
            else -> 8.00
        }
        return if (_expressDelivery.value) base + 8.00 else base
    }

    fun checkoutCart(email: String, address: String, paymentMethod: String): String? {
        val cartList = cartProducts.value
        if (cartList.isEmpty()) return null
        if (email.isBlank() || address.isBlank()) return "Please supply an email and delivery address."

        val totalAmount = cartList.sumOf { it.product.price * it.quantity }
        val fee = getDeliveryFee()
        val productsSimplifiedJson = cartList.joinToString(prefix = "[", postfix = "]") {
            "\"${it.product.nameEn} (x${it.quantity})\""
        }

        val order = OrderEntity(
            customerEmail = email,
            productsJson = productsSimplifiedJson,
            totalAmount = totalAmount + fee,
            paymentMethod = paymentMethod,
            paymentStatus = "Paid", // Demo auto-paid
            deliveryRegion = _selectedRegion.value,
            deliveryAddress = address,
            deliveryMethod = if (_expressDelivery.value) "Express" else "Standard",
            deliveryFee = fee,
            deliveryStatus = "Ordered"
        )

        viewModelScope.launch {
            repository.createOrder(order)
            // Adjust stocks
            for (item in cartList) {
                val newStock = (item.product.stockLevel - item.quantity).coerceAtLeast(0)
                repository.updateProductStock(item.product.id, newStock)
            }
            // Clear cart
            repository.clearCart()
        }
        return null // success
    }

    fun generateMockOrder() {
        viewModelScope.launch {
            val orderId = UUID.randomUUID().toString().take(6).uppercase()
            val order = OrderEntity(
                orderId = orderId,
                customerEmail = "builder_tashkent@enterprise.co",
                productsJson = "[\"DeWalt XR Heavy-Duty Max Brushless Drill (x1)\", \"Cemex Ultra-Crete Rapid Cement (x10)\"]",
                totalAmount = 374.88,
                paymentMethod = "Click",
                paymentStatus = "Paid",
                deliveryRegion = "Tashkent",
                deliveryAddress = "Navoi Street 24, Tashkent",
                deliveryMethod = "Express",
                deliveryFee = 12.99,
                deliveryStatus = "Ordered"
            )
            repository.createOrder(order)
        }
    }

    // --- Interactive AI Assistant Bot Screen ---
    private val _aiMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage(
            sender = "ai",
            text = "👋 Hello! I am your ProMarket Assistant. Ask me anything about drilling torque, welding currents, structural concrete anchors, regional delivery fees, or help comparing industrial gear!"
        )
    ))
    val aiMessages: StateFlow<List<ChatMessage>> = _aiMessages.asStateFlow()

    private val _isAiTyping = MutableStateFlow(false)
    val isAiTyping: StateFlow<Boolean> = _isAiTyping.asStateFlow()

    fun sendMessageToAssistant(messageText: String) {
        if (messageText.isBlank()) return
        val userMsg = ChatMessage(sender = "user", text = messageText)
        _aiMessages.value = _aiMessages.value + userMsg
        _isAiTyping.value = true

        viewModelScope.launch {
            val systemPrompt = """
                You are the premium ProMarket AI Chatbot for full-scale building materials and construction devices.
                We have DeWalt Drills (20V), Milton Welder (220V), Bosch Screwdrivers (magnetic), Makita saws, Schneider circuit breakers, Titan safety sets, Uzum concrete anchors, and Cemex cement in our catalog.
                Answer builders, construction workers, and engineers with technical authority. Keep responses concise, styled beautifully (markdown bold highlights), and direct.
                Speak in the language: ${currentLanguage.value}.
            """.trimIndent()

            val answerText = GeminiClient.askGemini(messageText, systemPrompt)
            _aiMessages.value = _aiMessages.value + ChatMessage(sender = "ai", text = answerText)
            _isAiTyping.value = false
        }
    }

    fun clearChat() {
        _aiMessages.value = listOf(
            ChatMessage(
                sender = "ai",
                text = "Chat history cleared. How can I help with your industrial equipment needs today?"
            )
        )
    }

    // --- Admin & Enterprise Simulation ---
    private val _currentEmployeeRole = MutableStateFlow("Super Admin") // Super Admin, Manager, Operator, Warehouse Staff, Delivery Staff
    val currentEmployeeRole: StateFlow<String> = _currentEmployeeRole.asStateFlow()

    fun changeEmployeeRole(role: String) {
        _currentEmployeeRole.value = role
        viewModelScope.launch {
            repository.logActivity("Security", "User switched operating portal role to: $role", "Authorized transition")
        }
    }

    fun adjustInventoryStock(productId: String, delta: Int) {
        viewModelScope.launch {
            val prod = products.value.find { it.id == productId }
            if (prod != null) {
                val nextStock = (prod.stockLevel + delta).coerceAtLeast(0)
                repository.updateProductStock(productId, nextStock)
            }
        }
    }

    fun changeOrderStatus(orderId: String, nextStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, nextStatus)
        }
    }

    fun broadcastMockTelegramAlert(message: String) {
        viewModelScope.launch {
            repository.logActivity("Telegram", "📢 [Telegram Broadcast] $message", "Admin Broadcast Node")
        }
    }

    fun injectMockAlert(category: String, message: String) {
        viewModelScope.launch {
            repository.logActivity(category, message, "Mock Payload Triggered")
        }
    }
}
