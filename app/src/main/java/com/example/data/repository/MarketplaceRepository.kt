package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID

class MarketplaceRepository(private val db: MarketplaceDatabase) {
    private val productDao = db.productDao()
    private val cartDao = db.cartDao()
    private val wishlistDao = db.wishlistDao()
    private val orderDao = db.orderDao()
    private val auditLogDao = db.auditLogDao()

    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val cartItems: Flow<List<CartItem>> = cartDao.getCartItems()
    val wishlistItems: Flow<List<WishlistItem>> = wishlistDao.getWishlistItems()
    val allOrders: Flow<List<OrderEntity>> = orderDao.getOrders()
    val auditLogs: Flow<List<LogEntry>> = auditLogDao.getLogs()

    suspend fun getProductById(id: String): Product? = withContext(Dispatchers.IO) {
        productDao.getProductById(id)
    }

    suspend fun insertProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
        logActivity("Stock", "Added/Updated product: ${product.nameEn}", "ID: ${product.id}")
    }

    suspend fun updateProductStock(id: String, stock: Int) = withContext(Dispatchers.IO) {
        productDao.updateProductStock(id, stock)
        val prod = productDao.getProductById(id)
        if (prod != null) {
            logActivity("Stock", "Stock level adjusted for '${prod.nameEn}'", "New Stock: $stock")
            if (stock <= prod.lowStockAlertLevel) {
                logActivity("Telegram", "⚠️ [Telegram Alert] Warehouse Stock Low for '${prod.nameEn}' (Only $stock left in ${prod.warehouseName}!)", "Low Stock Notification")
            }
        }
    }

    suspend fun insertCartItem(item: CartItem) = withContext(Dispatchers.IO) {
        cartDao.insertCartItem(item)
    }

    suspend fun updateCartQty(productId: String, qty: Int) = withContext(Dispatchers.IO) {
        if (qty <= 0) {
            cartDao.deleteCartItem(productId)
        } else {
            cartDao.updateCartItemQty(productId, qty)
        }
    }

    suspend fun deleteCartItem(productId: String) = withContext(Dispatchers.IO) {
        cartDao.deleteCartItem(productId)
    }

    suspend fun clearCart() = withContext(Dispatchers.IO) {
        cartDao.clearCart()
    }

    suspend fun toggleWishlist(productId: String) = withContext(Dispatchers.IO) {
        if (wishlistDao.isLiked(productId)) {
            wishlistDao.deleteWishlist(productId)
        } else {
            wishlistDao.insertWishlist(WishlistItem(productId))
        }
    }

    suspend fun isLiked(productId: String): Boolean = withContext(Dispatchers.IO) {
        wishlistDao.isLiked(productId)
    }

    suspend fun createOrder(order: OrderEntity) = withContext(Dispatchers.IO) {
        orderDao.insertOrder(order)
        logActivity("Payment", "New Order ${order.orderId} processed (${order.paymentMethod})", "Total: $${String.format("%.2f", order.totalAmount)}")
        logActivity("Telegram", "🤖 [Telegram Order Notification] Order #${order.orderId} was paid via ${order.paymentMethod}! Regional Delivery: ${order.deliveryRegion}", "Telegram Bot Integration")
    }

    suspend fun updateOrderStatus(orderId: String, status: String) = withContext(Dispatchers.IO) {
        orderDao.updateOrderStatus(orderId, status)
        logActivity("System", "Order $orderId status updated to $status", "")
        logActivity("Telegram", "🚚 [Telegram Delivery Update] Order #${orderId} changed status to: $status", "Realtime Status Notification")
    }

    suspend fun updatePaymentStatus(orderId: String, status: String) = withContext(Dispatchers.IO) {
        orderDao.updatePaymentStatus(orderId, status)
        logActivity("Payment", "Order $orderId payment marked as $status", "")
    }

    suspend fun logActivity(category: String, message: String, payload: String = "") = withContext(Dispatchers.IO) {
        auditLogDao.insertLog(LogEntry(category = category, message = message, payload = payload))
    }

    suspend fun clearLogs() = withContext(Dispatchers.IO) {
        auditLogDao.clearLogs()
    }

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val currentSize = productDao.getAllProducts().first().size
        if (currentSize == 0) {
            val initialList = listOf(
                Product(
                    id = "drill_dewalt_20v",
                    nameEn = "DeWalt XR Heavy-Duty Max Brushless Drill",
                    nameUz = "DeWalt XR og'ir yukga mo'ljallangan cho'tkasiz drel",
                    nameRu = "Бесщеточная дрель DeWalt XR повышенной мощности",
                    descriptionEn = "Futuristic 20V brushless smart drill with 3-speed transmission, ergonomic LED tactical light, and precise digital clutch. Ideal for steel structural fabrication.",
                    descriptionUz = "Uch tezlikli uzatmaga ega bo'lgan kelajak dreli 20V, tunda ishlash uchun taktik LED yorug'lik va har qanday po'lat karkasda burg'ulash kuchi.",
                    descriptionRu = "Высокотехнологичная бесщеточная дрель 20В с трехскоростной регулировкой, тактическим светодиодным прожектором и электронным контроллером.",
                    category = "Drills",
                    brand = "DeWalt",
                    price = 249.99,
                    rating = 4.8f,
                    reviewCount = 142,
                    imageUrl = "https://images.unsplash.com/photo-1504148455328-c376907d081c?auto=format&fit=crop&w=600&q=80", // Drill stock photo
                    stockLevel = 45,
                    warehouseName = "Warehouse A (Tashkent Central)",
                    voltage = "20V Max",
                    material = "Tungsten Carbide Reinforced Polymer",
                    warrantyEn = "3 Years Premium Warranty",
                    warrantyUz = "3 yillik kafolat xizmati",
                    warrantyRu = "3 года официальной гарантии",
                    featured = true,
                    technicalSpecsEn = "Max torque: 95Nm • No-load speed: 0-2250 RPM • Dual Chuck system.",
                    technicalSpecsUz = "Maksimal aylanma kuch: 95Nm • Bo'sh aylanish: 0-2250 ayl/daqiqa • Dual Chuck tizimi.",
                    technicalSpecsRu = "Крутящий момент: 95 Нм • Скорость: 0-2250 об/мин • Двухкулачковый патрон."
                ),
                Product(
                    id = "welder_milton_220v",
                    nameEn = "Milton Enterprise Arc Core Welder",
                    nameUz = "Milton sanoat darajasidagi elektr duga payvandlash uskunasi",
                    nameRu = "Промышленный сварочный инвертор Milton Arc Core",
                    descriptionEn = "Intelligent IGBT inverter welder supporting MMA & TIG gasless welding. Automatically balances thermal flow. Equipped with smart OLED HUD screen.",
                    descriptionUz = "IGBT aqlli payvandlash uskunasi. MMA va TIG payvandlash rejimlarini qo'llab-quvvatlaydi. OLED displeyi orqali tok kuchini raqamli boshqarish.",
                    descriptionRu = "Интеллектуальный сварочный инвертор IGBT, поддерживающий режимы MMA и TIG. Оборудован ярким OLED-экраном для настройки силы тока.",
                    category = "Welding",
                    brand = "Milton",
                    price = 479.50,
                    rating = 4.9f,
                    reviewCount = 84,
                    imageUrl = "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=600&q=80",
                    stockLevel = 12,
                    warehouseName = "Warehouse B (Samarkand Hub)",
                    voltage = "220V AC",
                    material = "High-Tensile Aerospace Alloy",
                    warrantyEn = "5 Years Enterprise Warranty",
                    warrantyUz = "5 yillik zavod kafolati",
                    warrantyRu = "5 лет производственной гарантии",
                    featured = true,
                    technicalSpecsEn = "Current range: 20A - 250A • Duty cycle: 85% at maximum load • IP23 Weatherproof.",
                    technicalSpecsUz = "Tok diapazoni: 20A - 250A • Foydalanish koeffitsiyenti: 85% yuk ostida • IP23 xavfsizlik standarti.",
                    technicalSpecsRu = "Диапазон тока: 20А - 250А • Рабочий цикл: 85% • Класс защиты IP23."
                ),
                Product(
                    id = "screwdriver_bosch_mag",
                    nameEn = "Bosch Pro-Grip Magnetic Screwdriver Set",
                    nameUz = "Bosch Pro-Grip magnitli otvertkalar jamlanmasi",
                    nameRu = "Набор магнитных отверток Bosch Pro-Grip",
                    descriptionEn = "Dual-component anti-slip grip screwdrivers with precision-forged magnetic tips made of S2 tool steel. Anti-roll handles.",
                    descriptionUz = "S2 asbobsozlik po'latidan tayyorlangan va sirpanmaydigan qoplamaga ega bo'lgan 12 bo'g'imli magnit dastalari mavjud professional otvertkalar to'plami.",
                    descriptionRu = "Профессиональный набор из 12 магнитных отверток из премиальной инструментальной стали S2. Обрезиненные эргономичные рукоятки.",
                    category = "Screwdrivers",
                    brand = "Bosch",
                    price = 34.99,
                    rating = 4.7f,
                    reviewCount = 312,
                    imageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=600&q=80",
                    stockLevel = 180,
                    warehouseName = "Warehouse A (Tashkent Central)",
                    voltage = "N/A",
                    material = "S2 Industrial Tool Steel",
                    warrantyEn = "1 Year Replacement Warranty",
                    warrantyUz = "1 yillik almashtirib berish kafolati",
                    warrantyRu = "1 год гарантии на замену комплекта",
                    featured = false,
                    technicalSpecsEn = "Content: 6 Phillips, 6 Slotted tips • Dual Torque handling • ESD safe handles.",
                    technicalSpecsUz = "Tarkibi: 6 dona krestli, 6 dona tekis • Maksimal burish kuchi muvofiqligi • ESD antistatik himoya.",
                    technicalSpecsRu = "В комплекте: 6 крестовых, 6 шлицевых отверток • Защита от статического разряда ESD."
                ),
                Product(
                    id = "jigsaw_makita_18v",
                    nameEn = "Makita Smart Li-Ion Cordless Jigsaw",
                    nameUz = "Makita akkumulyatorli dasta arra uskunasi",
                    nameRu = "Аккумуляторный сетевой электролобзик Makita",
                    descriptionEn = "High stroke frequency T-shank cordless jigsaw. Includes brushless motor feedback that tracks speed based on timber density automatically.",
                    descriptionUz = "Akkumulyatorli elektron dasta arra. Burg'ulash yuzasining qattiqligiga qarab kesish tezligini avtomatik moslashtiruvchi datchikka ega.",
                    descriptionRu = "Аккумуляторный лобзик со встроенным датчиком сопротивления, регулирующим частоту хода пилки в зависимости от плотности заготовки.",
                    category = "Tools",
                    brand = "Makita",
                    price = 189.00,
                    rating = 4.6f,
                    reviewCount = 59,
                    imageUrl = "https://images.unsplash.com/photo-1504148455328-c376907d081c?auto=format&fit=crop&w=600&q=80",
                    stockLevel = 22,
                    warehouseName = "Warehouse C (Bukhara Logistics)",
                    voltage = "18V DC",
                    material = "Polycarbonate Frame with Magnesium Shoe",
                    warrantyEn = "2 Years Makita Care",
                    warrantyUz = "2 yillik Makita rasmiy kafolati",
                    warrantyRu = "2 года официальной дилерской гарантии",
                    featured = true,
                    technicalSpecsEn = "Stroke length: 26mm • 4 orbital settings • No load speed: 800 - 3500 SPM.",
                    technicalSpecsUz = "Kesish qadami: 26 mm • 4 xil orbital rejim • Harakat tezligi: 800-3500 SPM.",
                    technicalSpecsRu = "Длина хода: 26 мм • 4 режима маятникового хода • Скорость полотна: 800-3500 ход/мин."
                ),
                Product(
                    id = "breaker_schneider_230v",
                    nameEn = "Schneider Enterprise IoT Smart Breaker Node",
                    nameUz = "Schneider IoT aqlli elektr avtomat tarmog'i",
                    nameRu = "Умный сетевой автоматический выключатель Schneider IoT",
                    descriptionEn = "IoT integrated 230V circuit breaker that communicates with smartphone over WiFi to track active power consumption, leaks, and voltage surges.",
                    descriptionUz = "IoT boshqaruv tizimiga ega 230V elektr tarmog'i himoya avtomati. Smartfon ilovasi orqali sarflangan elektr, qisqa tutashuv va ortiqcha kuchlanishni kuzatish tizimi.",
                    descriptionRu = "Интеллектуальный автоматический выключатель на 230В с поддержкой IoT. Отслеживает в реальном времени энергопотребление и имеет защиту от скачков напряжения.",
                    category = "Electrical",
                    brand = "Schneider",
                    price = 119.99,
                    rating = 4.8f,
                    reviewCount = 94,
                    imageUrl = "https://images.unsplash.com/photo-1558244661-d248897f7bc4?auto=format&fit=crop&w=600&q=80",
                    stockLevel = 65,
                    warehouseName = "Warehouse A (Tashkent Central)",
                    voltage = "230V AC",
                    material = "Self-Extinguishing Flame Retardant Resin",
                    warrantyEn = "3 Years Schneider Trust",
                    warrantyUz = "3 yillik Schneider rasmiy kafolati",
                    warrantyRu = "3 года авторизованной гарантии Schneider Electric",
                    featured = false,
                    technicalSpecsEn = "Max Load: 63A • WiFi 2.4GHz Smart Comm link • Response time under 2ms.",
                    technicalSpecsUz = "Maksimal yuklama: 63A • WiFi 2.4GHz datchik bog'lanishi • O'chish vaqti 2 millisaniyadan kam.",
                    technicalSpecsRu = "Номинальный ток: 63А • Сеть WiFi 2.4ГГц • Время расцепления менее 2 мс."
                ),
                Product(
                    id = "helmet_titan_safe",
                    nameEn = "Titan Carbon Steel Pro Helmet & Harness Kit",
                    nameUz = "Titan uglerod po'latli xavfsizlik dubulg'asi va kamar jamlanmasi",
                    nameRu = "Защитный комплект Titan Pro (Каска и страховочная привязь)",
                    descriptionEn = "Ultra lightweight composite carbon steel reinforced hard safety hat with custom internal cooling vents combined with dynamic anti-shatter rescue harness hook.",
                    descriptionUz = "Uglerod po'lat bilan mustahkamlangan yengil plastik dubulg'a va yiqilishdan himoya qiluvchi datchikli bel kamari va tirkama to'plami.",
                    descriptionRu = "Усиленная защитная каска с подвесной системой охлаждения в комплекте со страховочной привязью для высотных работ со встроенным амортизатором.",
                    category = "Safety",
                    brand = "Titan",
                    price = 74.95,
                    rating = 4.5f,
                    reviewCount = 110,
                    imageUrl = "https://images.unsplash.com/photo-1590402449133-79d509908802?auto=format&fit=crop&w=600&q=80",
                    stockLevel = 0, // Out of stock to demonstrate inventory alert!
                    warehouseName = "Warehouse B (Samarkand Hub)",
                    voltage = "N/A",
                    material = "Lexan Polycarbonate & Carbon Reinforcements",
                    warrantyEn = "2 Years Structural Integrity Guarantee",
                    warrantyUz = "2 yillik butunlik va mustahkamlik kafolati",
                    warrantyRu = "2 года гарантии прочности конструкции",
                    featured = false,
                    technicalSpecsEn = "Impact classification: ANSI Type I Class C • Multi-point adjust harness.",
                    technicalSpecsUz = "Urilish tasnifi: ANSI Type I Class C • Har tomonlama sozlanuvchi bel qulfi.",
                    technicalSpecsRu = "Сертификация ударопрочности: ANSI Класс I Тип С • Регулируемые застежки."
                ),
                Product(
                    id = "anchors_uzum_100x",
                    nameEn = "Uzum Heavy Galvanized Anchors (100-Pack)",
                    nameUz = "Uzum mustahkam ruxlangan karkas langarlar jamlanmasi (100 dona)",
                    nameRu = "Набор анкерных болтов Uzum Heavy (100 шт)",
                    descriptionEn = "Hot-dip galvanized heavy metal concrete expansion anchor bolts (M12 size, 120mm length). Excellent load carrying capability under seismic stress.",
                    descriptionUz = "Beton yuzalar uchun quruq ruxlash usulida qoplangan M12 o'lchamli og'ir tirgak langarlari (100 dona). Seysmik tebranishlarga juda chidamli.",
                    descriptionRu = "Высокопрочные оцинкованные клиновые анкеры для бетона (размер М12, длина 120 мм) для ответственного монтажа и защиты от вибрационных нагрузок.",
                    category = "Accessories",
                    brand = "Uzum Pro",
                    price = 44.90,
                    rating = 4.9f,
                    reviewCount = 339,
                    imageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=600&q=80",
                    stockLevel = 340,
                    warehouseName = "Warehouse C (Bukhara Logistics)",
                    voltage = "N/A",
                    material = "Grade 8.8 Galvanized Structural Iron",
                    warrantyEn = "Lifetime Quality Bond",
                    warrantyUz = "Butun umrlik yaroqlilik kafolati",
                    warrantyRu = "Пожизненное заводское качество",
                    featured = false,
                    technicalSpecsEn = "Tensile strength: 800 MPa • Ultimate Shear Load: 45kN • Seismic approved.",
                    technicalSpecsUz = "Maksimal cho'zilish chidamliligi: 800 MPa • Kesish kuchi yuklamasi: 45kN • Seysmik tasdiqlangan.",
                    technicalSpecsRu = "Предел прочности на разрыв: 800 МПа • Срезывающая нагрузка: 45 кН."
                ),
                Product(
                    id = "cement_cemex_ultra",
                    nameEn = "Cemex Ultra-Crete Rapid Cement (25kg)",
                    nameUz = "Cemex Ultra-Crete tez qotuvchi yuqori sifatli gips/sement (25 kg)",
                    nameRu = "Высокомарочный экспресс-цемент Cemex Ultra-Crete (25 кг)",
                    descriptionEn = "Premium swift-setting architectural portland cement. Perfect for high tensile foundation castings and instant post placements.",
                    descriptionUz = "M700 markali juda tez qotadigan va yorilib ketmaydigan portland sementi. Uy poydevorlari va temir karkaslarni darhol quyish uchun eng to'g'ri tanlov.",
                    descriptionRu = "Высокоинтенсивный быстротвердеющий портландцемент марки М700 для устройства ответственных бетонных фундаментов и опорных строительных узлов.",
                    category = "Materials",
                    brand = "Cemex",
                    price = 11.99,
                    rating = 4.7f,
                    reviewCount = 512,
                    imageUrl = "https://images.unsplash.com/photo-1589939705384-5185137a7f0f?auto=format&fit=crop&w=600&q=80",
                    stockLevel = 500,
                    warehouseName = "Warehouse A (Tashkent Central)",
                    voltage = "N/A",
                    material = "Advanced Portland Silicate compound",
                    warrantyEn = "N/A (Standard Bag Seal Integrity)",
                    warrantyUz = "Qop butunligi bo'yicha kafolati",
                    warrantyRu = "Гарантия сохранности герметичности упаковки",
                    featured = false,
                    technicalSpecsEn = "Cure time: Light traffic in 4 hours • Compression strength: 65 MPa.",
                    technicalSpecsUz = "Qotish muddati: 4 soat ichida yengil foydalanish • Siqilish kuchi chidamliligi: 65 MPa.",
                    technicalSpecsRu = "Время затвердевания: 4 часа • Прочность на сжатие: 65 МПа."
                )
            )
            productDao.insertProducts(initialList)
            logActivity("System", "Initial hardware marketplace database seeded successfully.", "${initialList.size} items added")
        }
    }
}
