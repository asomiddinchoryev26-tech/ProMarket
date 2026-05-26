package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import com.example.ui.theme.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.data.model.OrderEntity
import com.example.data.model.Product
import com.example.viewmodel.CartProduct
import com.example.viewmodel.ChatMessage
import com.example.viewmodel.MarketplaceViewModel

// --- Shared Glassmorphic Background Modifier ---
@Composable
fun Modifier.glassCard1(backgroundColor: Color = MaterialTheme.colorScheme.surface, isDark: Boolean = true): Modifier {
    return this
        .background(
            color = backgroundColor.copy(alpha = if (isDark) 0.65f else 0.85f),
            shape = RoundedCornerShape(16.dp)
        )
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = if (isDark) listOf(
                    Color.White.copy(alpha = 0.15f),
                    Color.White.copy(alpha = 0.03f)
                ) else listOf(
                    Color.Black.copy(alpha = 0.08f),
                    Color.Black.copy(alpha = 0.02f)
                )
            ),
            shape = RoundedCornerShape(16.dp)
        )
}

// ==========================================
// 1. LANDING SCREEN (AESTHETIC HERO CARD)
// ==========================================
@Composable
fun LandingScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToSearch: () -> Unit,
    onProductClick: (Product) -> Unit,
    lang: String,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val featuredList = remember(products) { products.filter { it.featured } }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
    ) {
        // --- 1. Cinematic Hero Header Banner ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF8C00).copy(alpha = 0.25f),
                                    Color.Transparent
                                ),
                                center = Offset(size.width * 0.8f, size.height * 0.2f),
                                radius = size.width
                            )
                            drawRect(brush)
                        }
                        .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                        .padding(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF00FFCC), CircleShape)
                            )
                            Text(
                                text = "ENTERPRISE PRO GATEWAY • 2026",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                        }

                        Text(
                            text = Loc.t("hero_title", lang),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 36.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = Loc.t("hero_subtitle", lang),
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onNavigateToSearch,
                            modifier = Modifier.testTag("hero_explore_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = Loc.t("buy_now", lang), color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- 2. Live Startup Statistics Counter Room ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(
                    Triple("12,400+", Loc.t("stats_happy", lang), Icons.Default.Engineering),
                    Triple("45,000", Loc.t("stats_delivered", lang), Icons.Default.LocalShipping),
                    Triple("99.98%", Loc.t("stats_uptime", lang), Icons.Default.CloudQueue)
                ).forEach { (value, label, icon) ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(105.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = value,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 3. Featured Products Grid Row ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Loc.t("featured", lang),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Explore More",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(featuredList) { prod ->
                        ProductCardMini(
                            product = prod,
                            onClick = { onProductClick(prod) },
                            onAddClick = { viewModel.addToCart(prod.id) },
                            lang = lang,
                            isDark = isDark
                        )
                    }
                }
            }
        }

        // --- 4. Interactive Testimonials & FAQ Accordions ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = Loc.t("reviews", lang),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Review Card
                val testimonials = listOf(
                    Triple("Rustam Karimov", "Tashkent Construction CEO", "The Milton welder and Uzum heavy anchors kept our building project 100% on schedule in Tashkent. Absolute game changer."),
                    Triple("Sofia Nikolaeva", "Lead Electrical Inspector", "Schneider smart breakers are highly compliant. This platform's simulated and real dispatch networks are outstanding.")
                )

                testimonials.forEach { (author, role, text) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .drawBehind {
                                val borderY = size.height - 1f
                                drawLine(
                                    color = Color.White.copy(alpha = 0.08f),
                                    start = Offset(0f, borderY),
                                    end = Offset(size.width, borderY),
                                    strokeWidth = 1f
                                )
                            }
                            .padding(bottom = 8.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(5) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFCC00), modifier = Modifier.size(12.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"$text\"",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$author • $role",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // FAQ Accordions
                Text(
                    text = Loc.t("faq_title", lang),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                FAQItem(
                    question = if (lang == "uz") "Etkazib berish qancha vaqt oladi?" else if (lang == "ru") "Сколько времени занимает доставка?" else "How fast is delivery dispatch?",
                    answer = if (lang == "uz") "Toshkent shahriga 4 soat ichida (Tezkor), viloyatlarga esa 24 soat ichida yetkaziladi." else if (lang == "ru") "Доставка по Ташкенту — в течение 4 часов (Экспресс), в регионы — в течение суток." else "Tashkent deliveries arrive in 4 hours via Express. Regional warehouses dispatch within 24 hours.",
                    isDark = isDark
                )
            }
        }

        // --- 5. Branding Footer Node ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "ProMarket Enterprise Cloud Nodes",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
                Text(
                    text = Loc.t("footer_rights", lang),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ProductCardMini(
    product: Product,
    onClick: () -> Unit,
    onAddClick: () -> Unit,
    lang: String,
    isDark: Boolean
) {
    Card(
        modifier = Modifier
            .width(185.dp)
            .height(295.dp)
            .clickable(onClick = onClick)
            .testTag("product_mini_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.DarkGray)
            ) {
                // Image with Unsplash URL or similar placeholder
                Image(
                    painter = rememberAsyncImagePainter(product.imageUrl),
                    contentDescription = product.getName(lang),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Category tag
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = product.category,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Voltage indicators
                if (product.voltage != "N/A") {
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Text(
                            text = product.voltage,
                            color = Color.Black,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.brand,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = product.getName(lang),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp,
                modifier = Modifier.height(34.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFCC00), modifier = Modifier.size(12.dp))
                Text(
                    text = "${product.rating}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
                Text(
                    text = "(${product.reviewCount})",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${String.format("%.2f", product.price)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                IconButton(
                    onClick = {
                        onAddClick()
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .testTag("add_mini_${product.id}"),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun FAQItem(
    question: String,
    answer: String,
    isDark: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .background(
                color = Color.White.copy(alpha = 0.03f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(0.9f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = answer,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ==========================================
// 2. SEARCH & DYNAMIC FILTERING & AI RECOMS
// ==========================================
@Composable
fun SearchScreen(
    viewModel: MarketplaceViewModel,
    onProductClick: (Product) -> Unit,
    lang: String,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredList by viewModel.filteredProducts.collectAsState()
    val aiRecommendation by viewModel.aiRecommendationText.collectAsState()
    val isSearchingAi by viewModel.isSearchingAi.collectAsState()

    val selectedCat by viewModel.selectedCategory.collectAsState()
    val selectedBrand by viewModel.selectedBrand.collectAsState()
    val maxPrice by viewModel.priceRangeMax.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Top Search bar container ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text(text = Loc.t("search_placeholder", lang), fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_field_input"),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
                )

                // AI Assist trigger button (Calling Gemini API client)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.performAiSemanticSearch(searchQuery)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_search_button"),
                        enabled = !isSearchingAi
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSearchingAi) "AI Loading..." else Loc.t("ai_search_btn", lang),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    if (aiRecommendation != null || searchQuery.isNotEmpty() || selectedCat != null || selectedBrand != null) {
                        Button(
                            onClick = {
                                viewModel.updateSearchQuery("")
                                viewModel.selectCategory(null)
                                viewModel.selectBrand(null)
                                viewModel.setPriceMax(500.0)
                                viewModel.clearAiSearch()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.FilterAltOff, contentDescription = "Reset Filters", modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Text(
                    text = Loc.t("ai_expl_tip", lang),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    lineHeight = 13.sp
                )
            }
        }

        // --- 2. Live Gemini Response card Drawer ---
        AnimatedVisibility(visible = isSearchingAi || aiRecommendation != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_suggestion_box"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                        .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                            Text(
                                text = "ProMarket Smart Recommendation",
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 13.sp
                            )
                        }
                        IconButton(onClick = { viewModel.clearAiSearch() }) {
                            Icon(Icons.Default.Close, contentDescription = "Close Advice", tint = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isSearchingAi) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = MaterialTheme.colorScheme.tertiary, strokeWidth = 2.dp)
                            Text("Gemini is analyzing catalog inventory specifications...", fontSize = 11.sp, color = Color.Gray)
                        }
                    } else {
                        Text(
                            text = aiRecommendation ?: "",
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }

        // --- 3. Traditional Filter Chips (Categories & Brands) ---
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Categories Horizontal row
            val categories = listOf("Drills", "Welding", "Screwdrivers", "Tools", "Electrical", "Safety", "Materials", "Accessories")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = (selectedCat == null),
                        onClick = { viewModel.selectCategory(null) },
                        label = { Text("All", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
                items(categories) { cat ->
                    FilterChip(
                        selected = (selectedCat == cat),
                        onClick = { viewModel.selectCategory(cat) },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            // Price range slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Max Price: $${maxPrice.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
                Slider(
                    value = maxPrice.toFloat(),
                    onValueChange = { viewModel.setPriceMax(it.toDouble()) },
                    valueRange = 10f..500f,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        // --- 4. Main Results Vertical Grid ---
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Construction, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                    Text(
                        text = "No tools aligned with this query.",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Try reducing filters or asking Gemini directly to find alternatives.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_grid_list"),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredList) { prod ->
                    ProductCardGridItem(
                        product = prod,
                        onClick = { onProductClick(prod) },
                        onAddClick = { viewModel.addToCart(prod.id) },
                        lang = lang,
                        isDark = isDark
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCardGridItem(
    product: Product,
    onClick: () -> Unit,
    onAddClick: () -> Unit,
    lang: String,
    isDark: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(295.dp)
            .clickable(onClick = onClick)
            .testTag("product_grid_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.DarkGray)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(product.imageUrl),
                    contentDescription = product.getName(lang),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Out of Stock Overlay icon
                if (product.stockLevel == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.65f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "OUT OF STOCK",
                            color = Color.Red,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${product.brand} • ${product.category}",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = product.getName(lang),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp,
                modifier = Modifier.height(34.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.Warehouse, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(11.dp))
                Text(
                    text = "Stock: ${product.stockLevel}",
                    fontSize = 10.sp,
                    color = if (product.stockLevel <= product.lowStockAlertLevel) Color(0xFFFFCC00) else Color.Gray
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${String.format("%.2f", product.price)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            if (product.stockLevel == 0) Color.Gray else MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                        .testTag("add_grid_${product.id}"),
                    enabled = product.stockLevel > 0,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ==========================================
// 3. SHOPPING CART, EXPEDITED SHIP & ORDER
// ==========================================
@Composable
fun CartScreen(
    viewModel: MarketplaceViewModel,
    lang: String,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val cartProducts by viewModel.cartProducts.collectAsState()
    val region by viewModel.selectedRegion.collectAsState()
    val express by viewModel.expressDelivery.collectAsState()

    var customerEmail by remember { mutableStateOf("client@enterprise.uz") }
    var destinationAddress by remember { mutableStateOf("Amir Temur Street 42, Tashkent") }
    var activePayment by remember { mutableStateOf("Payme") } // Payme, Click, Uzum, Visa

    val keyboardController = LocalSoftwareKeyboardController.current

    var showInvoiceDialog by remember { mutableStateOf<OrderEntity?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Invoice completion Dialog
        item {
            showInvoiceDialog?.let { order ->
                Dialog(onDismissRequest = { showInvoiceDialog = null }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .testTag("invoice_modal"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF00FFCC))
                                    Text("PAYMENT REFRESHED", color = Color(0xFF00FFCC), fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                }
                                IconButton(onClick = { showInvoiceDialog = null }) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.LightGray)
                                }
                            }

                            Text("Official Digital PDF Slip", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)

                            Text(
                                text = "Order ID: #${order.orderId}\n" +
                                        "Client Email: ${order.customerEmail}\n" +
                                        "Shipping Region: ${order.deliveryRegion}\n" +
                                        "Deliver to: ${order.deliveryAddress}\n" +
                                        "Delivery Method: ${order.deliveryMethod} Dispatch",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.LightGray,
                                lineHeight = 16.sp
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Text("Purchased Inventory items:", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text(
                                    text = order.productsJson,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text("Total Paid:", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = "$${String.format("%.2f", order.totalAmount)}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Button(
                                onClick = { showInvoiceDialog = null },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Done", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // --- 1. Cart Items Rows ---
        if (cartProducts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                        Text(
                            text = Loc.t("empty_cart", lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        } else {
            items(cartProducts) { cProd ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cart_item_${cProd.product.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(cProd.product.imageUrl),
                            contentDescription = cProd.product.getName(lang),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(75.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = cProd.product.getName(lang),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "$${cProd.product.price} x ${cProd.quantity}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Quantity mod buttons
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.updateCartQuantity(cProd.product.id, cProd.quantity - 1) },
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(12.dp))
                            }

                            Text(text = "${cProd.quantity}", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                            IconButton(
                                onClick = { viewModel.updateCartQuantity(cProd.product.id, cProd.quantity + 1) },
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Customer Credentials Container ---
        if (cartProducts.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = Loc.t("cart_title", lang),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        OutlinedTextField(
                            value = customerEmail,
                            onValueChange = { customerEmail = it },
                            label = { Text(Loc.t("customer_email", lang), fontSize = 11.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_input"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = destinationAddress,
                            onValueChange = { destinationAddress = it },
                            label = { Text(Loc.t("delivery_addr", lang), fontSize = 11.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("address_input"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Regional delivery picker
                        val regionsList = listOf("Tashkent", "Samarkand", "Bukhara", "Navoi", "Khorezm")
                        Column {
                            Text(text = Loc.t("select_region", lang), fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(regionsList) { r ->
                                    val matched = (region == r)
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (matched) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.05f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { viewModel.selectRegion(r) }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = r,
                                            color = if (matched) Color.Black else MaterialTheme.colorScheme.onBackground,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Express dispatch toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = Loc.t("express_del", lang), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Switch(
                                checked = express,
                                onCheckedChange = { viewModel.toggleExpressDelivery() },
                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }

            // --- 3. Gateway Payment Providers ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = Loc.t("pay_method", lang), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        val paymentOptions = listOf("Payme", "Click", "Uzum Bank", "Visa")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            paymentOptions.forEach { opt ->
                                val active = (activePayment == opt)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            width = 1.dp,
                                            color = if (active) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .background(
                                            color = if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { activePayment = opt }
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = opt,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- 4. Checkout bill panel ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val subtotal = cartProducts.sumOf { it.product.price * it.quantity }
                        val fee = viewModel.getDeliveryFee()
                        val grandTotal = subtotal + fee

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = Loc.t("subtotal", lang), fontSize = 12.sp, color = Color.Gray)
                            Text(text = "$${String.format("%.2f", subtotal)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = Loc.t("delivery_fee", lang) + "($region)", fontSize = 12.sp, color = Color.Gray)
                            Text(text = "$${String.format("%.2f", fee)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                        }

                        Divider(modifier = Modifier.padding(vertical = 4.dp), color = Color.White.copy(alpha = 0.08f))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                            Text(text = Loc.t("total", lang), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "$${String.format("%.2f", grandTotal)}",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        errorMessage?.let { msg ->
                            Text(text = msg, color = Color.Red, fontSize = 11.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Button(
                            onClick = {
                                keyboardController?.hide()
                                val error = viewModel.checkoutCart(customerEmail, destinationAddress, activePayment)
                                if (error != null) {
                                    errorMessage = error
                                } else {
                                    errorMessage = null
                                    // Seed a pseudo invoice
                                    showInvoiceDialog = OrderEntity(
                                        customerEmail = customerEmail,
                                        productsJson = cartProducts.joinToString { "${it.product.nameEn} (x${it.quantity})" },
                                        totalAmount = grandTotal,
                                        paymentMethod = activePayment,
                                        paymentStatus = "Paid",
                                        deliveryRegion = region,
                                        deliveryAddress = destinationAddress,
                                        deliveryMethod = if (express) "Express Air" else "Standard Cargo",
                                        deliveryFee = fee,
                                        deliveryStatus = "Ordered"
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("checkout_commit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = Loc.t("place_order", lang), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. CHAT BOT TERMINAL (RESPONSIVE THREAD)
// ==========================================
@Composable
fun AssistantScreen(
    viewModel: MarketplaceViewModel,
    lang: String,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.aiMessages.collectAsState()
    val isTyping by viewModel.isAiTyping.collectAsState()
    var inputStr by remember { mutableStateOf("") }

    val controller = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    // Keep scrolled down on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Chat Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(text = Loc.t("asst_welcome", lang), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = Loc.t("asst_tag", lang), fontSize = 10.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                IconButton(onClick = { viewModel.clearChat() }) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Clear Chat", tint = Color.LightGray)
                }
            }
        }

        // Message Thread View
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Recommendation Preset Query Chips
            item {
                val questions = listOf(
                    "Recommend a drill for tungsten steel frame.",
                    "Explain Schneider smart IoT circuit specs.",
                    "What's the cure rate of Cemex cement M700?",
                    "Do you deliver to Khorezm Hub?"
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(questions) { q ->
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                                .clickable { viewModel.sendMessageToAssistant(q) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = q, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(messages) { msg ->
                val fromAi = (msg.sender == "ai")
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (fromAi) Alignment.CenterStart else Alignment.CenterEnd
                ) {
                    Card(
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .testTag("chat_bubble_${msg.sender}"),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (fromAi) 4.dp else 16.dp,
                            bottomEnd = if (fromAi) 16.dp else 4.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (fromAi) {
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                            } else {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = if (fromAi) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f) else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = msg.text,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            if (isTyping) {
                item {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(12.dp), color = MaterialTheme.colorScheme.tertiary, strokeWidth = 1.5.dp)
                        Text(text = "ProMarket Copilot is reviewing inventory levels...", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }

        // Input Tray
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputStr,
                    onValueChange = { inputStr = it },
                    placeholder = { Text("Ask about tool torque or materials...", fontSize = 11.sp, color = Color.Gray) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("assistant_input_text"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (inputStr.isNotBlank()) {
                            viewModel.sendMessageToAssistant(inputStr)
                            inputStr = ""
                            controller?.hide()
                        }
                    })
                )

                IconButton(
                    onClick = {
                        if (inputStr.isNotBlank()) {
                            viewModel.sendMessageToAssistant(inputStr)
                            inputStr = ""
                            controller?.hide()
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .testTag("assistant_send_button"),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send Prompt", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun OrderProgressBar(currentStatus: String, isDark: Boolean) {
    val statuses = listOf("Ordered", "Preparing", "OutForDelivery", "Delivered")
    val displayNames = mapOf(
        "Ordered" to "Ordered",
        "Preparing" to "Preparing",
        "OutForDelivery" to "Shipped",
        "Delivered" to "Delivered"
    )
    val currentIndex = statuses.indexOf(currentStatus).coerceAtLeast(0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Track base
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
            )

            // Track progress filled line
            val progressPercentage = when (currentIndex) {
                0 -> 0.0f
                1 -> 0.33f
                2 -> 0.66f
                else -> 1.0f
            }

            // Highlight track progress
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressPercentage)
                    .height(4.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
            )

            // Four nodes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                statuses.forEachIndexed { idx, status ->
                    val isCompleted = idx <= currentIndex
                    val isCurrent = idx == currentIndex

                    val backgroundColor = if (isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        if (isDark) Color(0xFF2C2C2C) else Color(0xFFE0E0E0)
                    }

                    val borderColor = if (isCurrent) {
                        Color.White
                    } else {
                        Color.Transparent
                    }

                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(backgroundColor, CircleShape)
                            .border(width = 1.5.dp, color = borderColor, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(10.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(Color.Gray.copy(alpha = 0.6f), CircleShape)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Step labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            statuses.forEachIndexed { idx, status ->
                val isCurrent = idx == currentIndex
                val isCompleted = idx <= currentIndex
                val label = displayNames[status] ?: status
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isCurrent -> MaterialTheme.colorScheme.primary
                        isCompleted -> MaterialTheme.colorScheme.onBackground
                        else -> Color.Gray.copy(alpha = 0.5f)
                    },
                    modifier = Modifier.width(64.dp),
                    textAlign = when (idx) {
                        0 -> TextAlign.Start
                        statuses.size - 1 -> TextAlign.End
                        else -> TextAlign.Center
                    }
                )
            }
        }
    }
}

// ==========================================
// 5. ADMIN COMMAND HUB & CORPORATE REALTIME CHARTS
// ==========================================
@Composable
fun ControlRoomScreen(
    viewModel: MarketplaceViewModel,
    lang: String,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val activeRole by viewModel.currentEmployeeRole.collectAsState()
    val productsList by viewModel.products.collectAsState()
    val ordersList by viewModel.orders.collectAsState()
    val logsList by viewModel.auditLogs.collectAsState()

    var broadcastTxt by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // --- 1. Authorized Role selectors ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(text = Loc.t("role_control", lang), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    val roles = listOf("Super Admin", "Manager", "Warehouse Staff", "Delivery Staff")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(roles) { roleItem ->
                            val matched = (activeRole == roleItem)
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (matched) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.05f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.changeEmployeeRole(roleItem) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = roleItem,
                                    color = if (matched) Color.Black else MaterialTheme.colorScheme.onBackground,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Dynamic Portal Contents by Role authority ---
        when (activeRole) {
            "Super Admin" -> {
                // Realtime Sales canvas Charts
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("financial_analytics_chart"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                                .padding(16.dp)
                        ) {
                            Text(text = Loc.t("analytics_overview", lang), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Canvas drawing glowing corporate revenue bars
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                            ) {
                                val barWidth = 35.dp.toPx()
                                val cornerRadius = 4.dp.toPx()
                                val spacing = 20.dp.toPx()
                                val heights = listOf(110.dp.toPx(), 75.dp.toPx(), 120.dp.toPx(), 90.dp.toPx(), 45.dp.toPx())
                                val labels = listOf("Jan", "Feb", "Mar", "Apr", "May")

                                heights.forEachIndexed { idx, heightVal ->
                                    val leftX = idx * (barWidth + spacing) + spacing
                                    val topY = size.height - heightVal

                                    // Gradient bar
                                    drawRoundRect(
                                        brush = Brush.verticalGradient(
                                            listOf(Color(0xFFFF8C00), Color(0xFFFF8C00).copy(alpha = 0.2f))
                                        ),
                                        topLeft = Offset(leftX, topY),
                                        size = Size(barWidth, heightVal),
                                        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                listOf("Jan ($45k)", "Feb ($30k)", "Mar ($55k)", "Apr ($40k)", "May ($65k)").forEach { item ->
                                    Text(text = item, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            "Manager" -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = Loc.t("broadcast_telegram", lang), fontSize = 13.sp, fontWeight = FontWeight.Bold)

                            OutlinedTextField(
                                value = broadcastTxt,
                                onValueChange = { broadcastTxt = it },
                                placeholder = { Text("Low Stock/Critical Order alerts to Telegram network...", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("telegram_broadcast_field")
                            )

                            Button(
                                onClick = {
                                    if (broadcastTxt.isNotBlank()) {
                                        viewModel.broadcastMockTelegramAlert(broadcastTxt)
                                        broadcastTxt = ""
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Send Telegram Notification Node", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            "Warehouse Staff" -> {
                item {
                    Text(text = Loc.t("warehouse_title", lang), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                items(productsList) { prod ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(0.7f)) {
                                Text(text = prod.getName(lang), fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text(text = "Hub: ${prod.warehouseName} | Current Stock: ${prod.stockLevel}", fontSize = 10.sp, color = Color.Gray)
                            }

                            Row(
                                modifier = Modifier.weight(0.3f),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { viewModel.adjustInventoryStock(prod.id, -1) },
                                    modifier = Modifier.size(28.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(12.dp))
                                }

                                Text(text = "${prod.stockLevel}", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                                IconButton(
                                    onClick = { viewModel.adjustInventoryStock(prod.id, 1) },
                                    modifier = Modifier.size(28.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                }
            }

            "Delivery Staff" -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = Loc.t("delivery_tracker", lang), fontSize = 13.sp, fontWeight = FontWeight.Bold)

                            // Simulated coordinates lines
                            Canvas(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                                val stroke = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f))
                                // Path
                                drawLine(Color.Gray, Offset(20f, 40f), Offset(150f, 20f), strokeWidth = stroke.width, pathEffect = stroke.pathEffect)
                                drawLine(Color.Gray, Offset(150f, 20f), Offset(280f, 45f), strokeWidth = stroke.width, pathEffect = stroke.pathEffect)

                                // Nodes
                                drawCircle(Color(0xFFFF8C00), radius = 10f, center = Offset(20f, 40f))
                                drawCircle(Color(0xFF00FFFF), radius = 10f, center = Offset(150f, 20f))
                                drawCircle(Color(0xFFFF8C00), radius = 10f, center = Offset(280f, 45f))
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tashkent Departure node", fontSize = 9.sp, color = Color.Gray)
                                Text("Samarkand Hub Transit", fontSize = 9.sp, color = Color(0xFF00FFFF))
                                Text("Bukhara Delivery point", fontSize = 9.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        // --- Central Order Management & Live Stepper Node ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("order_management_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = if (lang == "uz") "Buyurtmalarni Boshqarish Tizimi" else if (lang == "ru") "Управление Заказами" else "Order Management Control Panel",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        // Shortcut to insert a mock order if list empty
                        Button(
                            onClick = { viewModel.generateMockOrder() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier
                                .height(26.dp)
                                .testTag("btn_add_mock_order"),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (lang == "uz") "Simulyatsiya" else if (lang == "ru") "Симулировать" else "Mock Order",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (ordersList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(12.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.List, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                                Text(
                                    text = if (lang == "uz") "Faol buyurtmalar mavjud emas." else if (lang == "ru") "Нет активных заказов." else "No active client orders found.",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = if (lang == "uz") "Xarid savatidan buyurtma bering yoki yuqoridagi 'Simulyatsiya' tugmasini bosing." else if (lang == "ru") "Оформите заказ в корзине или нажмите 'Симулировать'." else "Check out some items from Cart or click 'Mock Order' to generate instantly.",
                                    fontSize = 9.sp,
                                    color = Color.Gray.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            ordersList.forEach { order ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("order_item_${order.orderId}")
                                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isDark) Color.White.copy(alpha = 0.03f) else Color.Black.copy(alpha = 0.02f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Order header info
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column {
                                                Text(
                                                    text = "ID: #${order.orderId}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = order.customerEmail,
                                                    fontSize = 10.sp,
                                                    color = Color.Gray
                                                )
                                            }

                                            Text(
                                                text = "$${String.format("%.2f", order.totalAmount)}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        }

                                        // Products listed
                                        Text(
                                            text = "Items: ${order.productsJson.replace("[", "").replace("]", "").replace("\"", "")}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                            lineHeight = 13.sp
                                        )

                                        Text(
                                            text = "Address: ${order.deliveryAddress} (${order.deliveryRegion}) | ${order.deliveryMethod}",
                                            fontSize = 9.sp,
                                            color = Color.Gray
                                        )

                                        // Divider for the progress bar
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(Color.White.copy(alpha = 0.05f))
                                        )

                                        // --- Live Status Progress Bar ---
                                        OrderProgressBar(currentStatus = order.deliveryStatus, isDark = isDark)

                                        // Control actions row to update status
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val currentStatus = order.deliveryStatus
                                            val nextStatus = when (currentStatus) {
                                                "Ordered" -> "Preparing"
                                                "Preparing" -> "OutForDelivery"
                                                "OutForDelivery" -> "Delivered"
                                                else -> null
                                            }

                                            val btnLabel = when (currentStatus) {
                                                "Ordered" -> if (lang == "uz") "Tayyorlashni boshlash" else if (lang == "ru") "Начать сборку" else "Assemble"
                                                "Preparing" -> if (lang == "uz") "Yo'lga chiqarish" else if (lang == "ru") "Передать доставке" else "Ship"
                                                "OutForDelivery" -> if (lang == "uz") "Taqdim etildi" else if (lang == "ru") "Доставлено" else "Complete"
                                                else -> null
                                            }

                                            if (nextStatus != null && btnLabel != null) {
                                                Button(
                                                    onClick = { viewModel.changeOrderStatus(order.orderId, nextStatus) },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = MaterialTheme.colorScheme.primary,
                                                        contentColor = Color.Black
                                                    ),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                    modifier = Modifier
                                                        .height(28.dp)
                                                        .testTag("btn_advance_status_${order.orderId}"),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(10.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(text = btnLabel, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            } else {
                                                // Reset status helper
                                                TextButton(
                                                    onClick = { viewModel.changeOrderStatus(order.orderId, "Ordered") },
                                                    modifier = Modifier
                                                        .height(28.dp)
                                                        .testTag("btn_reset_order_${order.orderId}"),
                                                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray),
                                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                                ) {
                                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(10.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = if (lang == "uz") "Qaytadan" else if (lang == "ru") "Сбросить" else "Reset Flow",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 3. Shared Audit Logs Terminal Log Section ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassCard1(MaterialTheme.colorScheme.surface, isDark)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = Loc.t("logs_terminal", lang), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color.Black, RoundedCornerShape(8.dp))
                            .verticalScroll(rememberScrollState())
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (logsList.isEmpty()) {
                            Text("No telemetry logs compiled yet.", color = Color.Gray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        } else {
                            logsList.forEach { log ->
                                val color = when (log.category) {
                                    "Telecom", "Telegram" -> Color(0xFF00E5FF)
                                    "Stock" -> Color(0xFFFFCC00)
                                    "Payment" -> Color(0xFF00FF88)
                                    "Security" -> Color(0xFFFF3B30)
                                    else -> Color.White
                                }
                                Text(
                                    text = "[${log.category}] ${log.message} ${log.payload}",
                                    color = color,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
