package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.data.model.Product
import com.example.ui.screens.*
import com.example.ui.theme.MarketplaceTheme
import com.example.viewmodel.MarketplaceViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MarketplaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDark by viewModel.isDarkMode.collectAsState()
            val language by viewModel.currentLanguage.collectAsState()

            MarketplaceTheme(darkTheme = isDark) {
                var currentTab by remember { mutableStateOf(0) } // 0: Home, 1: Search, 2: Cart, 3: Assistant, 4: Admin
                var selectedProductDetail by remember { mutableStateOf<Product?>(null) }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    topBar = {
                        Column {
                            // Sub-status bar to avoid notch overlapping
                            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                            MarketplaceTopAppBar(
                                currentLang = language,
                                onLangChange = { viewModel.setLanguage(it) },
                                isDark = isDark,
                                onThemeToggle = { viewModel.toggleTheme() }
                            )
                        }
                    },
                    bottomBar = {
                        MarketplaceBottomNavigationBar(
                            selectedTab = currentTab,
                            onTabSelected = { currentTab = it },
                            lang = language,
                            viewModel = viewModel
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "screen_transitions"
                        ) { tab ->
                            when (tab) {
                                0 -> LandingScreen(
                                    viewModel = viewModel,
                                    onNavigateToSearch = { currentTab = 1 },
                                    onProductClick = { selectedProductDetail = it },
                                    lang = language,
                                    isDark = isDark
                                )
                                1 -> SearchScreen(
                                    viewModel = viewModel,
                                    onProductClick = { selectedProductDetail = it },
                                    lang = language,
                                    isDark = isDark
                                )
                                2 -> CartScreen(
                                    viewModel = viewModel,
                                    lang = language,
                                    isDark = isDark
                                )
                                3 -> AssistantScreen(
                                    viewModel = viewModel,
                                    lang = language,
                                    isDark = isDark
                                )
                                4 -> ControlRoomScreen(
                                    viewModel = viewModel,
                                    lang = language,
                                    isDark = isDark
                                )
                            }
                        }

                        // Product Detail Dialog Card Overlay
                        selectedProductDetail?.let { p ->
                            ProductDetailDialog(
                                product = p,
                                onDismiss = { selectedProductDetail = null },
                                onAddToCart = {
                                    viewModel.addToCart(p.id)
                                    selectedProductDetail = null
                                },
                                lang = language,
                                isDark = isDark
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// CUSTOM TOP APP BAR WITH SYSTEM TOGGLES
// ==========================================
@Composable
fun MarketplaceTopAppBar(
    currentLang: String,
    onLangChange: (String) -> Unit,
    isDark: Boolean,
    onThemeToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App title
        Column {
            Text(
                text = "ProMarket",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Enterprise Node",
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
        }

        // Toggles Container
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Language Buttons Row
            Row(
                modifier = Modifier
                    .background(
                        color = Color.White.copy(alpha = if (isDark) 0.05f else 0.1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(2.dp)
            ) {
                listOf("en", "uz", "ru").forEach { l ->
                    val isSelected = (currentLang == l)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { onLangChange(l) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = l.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // Light / Dark Mode Toggle Button
            IconButton(
                onClick = onThemeToggle,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = Color.White.copy(alpha = if (isDark) 0.05f else 0.1f),
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Icon(
                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ==========================================
// COMPLIANT BOT NAVIGATION BAR (M3 INSPIRED)
// ==========================================
@Composable
fun MarketplaceBottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    lang: String,
    viewModel: MarketplaceViewModel
) {
    val cartProducts by viewModel.cartProducts.collectAsState()
    val itemsCount = remember(cartProducts) { cartProducts.sumOf { it.quantity } }

    NavigationBar(
        modifier = Modifier
            .navigationBarsPadding() // Protect against System Gesture pillars overlapping
            .height(65.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val menuItems = listOf(
            Triple("Home", Icons.Default.Home, 0),
            Triple("AI Search", Icons.Default.Search, 1),
            Triple("Cart", Icons.Default.ShoppingCart, 2),
            Triple("Copilot", Icons.Default.AutoAwesome, 3),
            Triple("Admin", Icons.Default.AdminPanelSettings, 4)
        )

        menuItems.forEach { (label, icon, tabIdx) ->
            val active = (selectedTab == tabIdx)
            NavigationBarItem(
                selected = active,
                onClick = { onTabSelected(tabIdx) },
                icon = {
                    Box {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (active) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        // Cart item count badge
                        if (tabIdx == 2 && itemsCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(Color.Red, CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 6.dp, y = (-6).dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$itemsCount",
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                label = {
                    Text(
                        text = if (tabIdx == 3) "Copilot" else if (tabIdx == 4) "Admin" else label,
                        fontSize = 10.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                        color = if (active) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}

// ==========================================
// PRODUCT DETAIL MODAL DIALOG COMPONENT
// ==========================================
@Composable
fun ProductDetailDialog(
    product: Product,
    onDismiss: () -> Unit,
    onAddToCart: () -> Unit,
    lang: String,
    isDark: Boolean
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("product_detail_modal_${product.id}"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = product.category,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close View", tint = Color.LightGray)
                    }
                }

                // Image Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.DarkGray)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(product.imageUrl),
                        contentDescription = product.getName(lang),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Title details
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = product.brand,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = product.getName(lang),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Description
                Text(
                    text = product.getDescription(lang),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )

                Divider(color = Color.White.copy(alpha = 0.08f))

                // Specifications Drawer
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = Loc.t("details", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                    listOf(
                        "Material / Formula" to product.material,
                        "Voltage System" to product.voltage,
                        "Technical Specs" to product.getTechnicalSpecs(lang),
                        Loc.t("warranty", lang) to product.getWarranty(lang)
                    ).forEach { (label, value) ->
                        if (value.isNotEmpty() && value != "N/A") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = label, fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = value,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Divider(color = Color.White.copy(alpha = 0.08f))

                // Bottom CTA Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = Loc.t("price", lang), fontSize = 10.sp, color = Color.Gray)
                        Text(
                            text = "$${product.price}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Button(
                        onClick = onAddToCart,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("detail_add_to_cart_button"),
                        enabled = product.stockLevel > 0
                    ) {
                        Icon(
                            imageVector = if (product.stockLevel > 0) Icons.Default.AddShoppingCart else Icons.Default.HourglassEmpty,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (product.stockLevel > 0) Loc.t("add_to_cart", lang) else "SOLD OUT",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
