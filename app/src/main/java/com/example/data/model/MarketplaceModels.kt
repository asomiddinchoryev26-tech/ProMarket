package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val nameEn: String,
    val nameUz: String,
    val nameRu: String,
    val descriptionEn: String,
    val descriptionUz: String,
    val descriptionRu: String,
    val category: String, // Electrical, Welding, Screwdrivers, Drills, Safety, Materials, Accessories
    val brand: String,
    val price: Double,
    val rating: Float,
    val reviewCount: Int,
    val imageUrl: String,
    val stockLevel: Int,
    val lowStockAlertLevel: Int = 15,
    val warehouseName: String, // Warehouse A (Tashkent), Warehouse B (Samarkand), Warehouse C (Bukhara)
    val voltage: String = "N/A",
    val material: String = "Composite Steel",
    val warrantyEn: String = "1 Year",
    val warrantyUz: String = "1 yil",
    val warrantyRu: String = "1 год",
    val featured: Boolean = false,
    val technicalSpecsEn: String = "",
    val technicalSpecsUz: String = "",
    val technicalSpecsRu: String = ""
) {
    fun getName(lang: String): String = when (lang) {
        "uz" -> nameUz
        "ru" -> nameRu
        else -> nameEn
    }

    fun getDescription(lang: String): String = when (lang) {
        "uz" -> descriptionUz
        "ru" -> descriptionRu
        else -> descriptionEn
    }

    fun getWarranty(lang: String): String = when (lang) {
        "uz" -> warrantyUz
        "ru" -> warrantyRu
        else -> warrantyEn
    }

    fun getTechnicalSpecs(lang: String): String = when (lang) {
        "uz" -> technicalSpecsUz
        "ru" -> technicalSpecsRu
        else -> technicalSpecsEn
    }
}

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val productId: String,
    val quantity: Int,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist_items")
data class WishlistItem(
    @PrimaryKey val productId: String,
    val likedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String = UUID.randomUUID().toString().take(8).uppercase(),
    val customerEmail: String,
    val productsJson: String, // JSON array of Product details and quantities
    val totalAmount: Double,
    val paymentMethod: String, // Payme, Click, Uzum, Visa, ApplePay, GooglePay
    val paymentStatus: String, // Pending, Paid, Refunded
    val deliveryRegion: String, // Tashkent, Samarkand, Bukhara, Navoi, Khorezm
    val deliveryAddress: String,
    val deliveryMethod: String, // Standard, Express
    val deliveryFee: Double,
    val deliveryStatus: String, // Ordered, Preparing, OutForDelivery, Delivered
    val orderDate: Long = System.currentTimeMillis(),
    val invoicePath: String = ""
)

@Entity(tableName = "audit_logs")
data class LogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String, // Stock, Payment, Telegram, System, Security
    val message: String,
    val payload: String = ""
)
