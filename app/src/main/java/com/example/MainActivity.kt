package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Ensure RTL Layout direction for Farsi context
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        WorkspaceRootView(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WorkspaceRootView(modifier: Modifier = Modifier) {
    val viewModel: AppViewModel = viewModel()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicBackground)
    ) {
        // Tablet Side Navigation Rail
        if (isTablet) {
            NavigationRail(
                containerColor = CosmicSurface,
                modifier = Modifier.fillMaxHeight(),
                header = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(PrimaryCyan, AccentPurple)))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(CosmicBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("ک", color = PrimaryCyan, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            ) {
                // Main Rail Navigation items
                val menuItems = getNavigationItems()
                menuItems.forEach { (tab, label, icon) ->
                    val isSelected = viewModel.currentTab == tab
                    NavigationRailItem(
                        selected = isSelected,
                        onClick = { viewModel.switchTab(tab) },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, fontSize = 10.sp) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = CosmicBackground,
                            selectedTextColor = PrimaryCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = PrimaryCyan
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Lock item to enter the secret Admin Panel on click
                val isSecretActive = viewModel.currentTab == AppTab.ADMIN
                NavigationRailItem(
                    selected = isSecretActive,
                    onClick = {
                        viewModel.switchTab(AppTab.ADMIN)
                        Toast.makeText(context, "وارد پنل فوق‌محرمانه ادمین شدید.", Toast.LENGTH_SHORT).show()
                    },
                    icon = { Icon(Icons.Default.Lock, contentDescription = "Secret Admin", tint = StatusError) },
                    label = { Text("ادمین", color = StatusError, fontSize = 10.sp) }
                )
            }
        }

        // Main content screen holder
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            // Elegant Professional Polish Header
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .testTag("app_header_title")
                            .combinedClickable(
                                onClick = {
                                    Toast.makeText(context, "کمشک جیپیتی؛ هوش مصنوعی برای انجام همه کارها", Toast.LENGTH_SHORT).show()
                                },
                                onLongClick = {
                                    viewModel.switchTab(AppTab.ADMIN)
                                    Toast.makeText(context, "🔑 قفل پنل محرمانه مدیریت باز شد!", Toast.LENGTH_SHORT).show()
                                }
                            )
                    ) {
                        // Spinning/Styled brand box
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                                .background(PrimaryCyan)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        Column {
                            Text(
                                text = "کمشک جیپیتی",
                                color = TextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                lineHeight = 18.sp
                            )
                            Text(
                                text = "Kameshk GPT",
                                color = PrimaryCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    // Quick Action button depending on view (e.g. Clean DB, Upgrade plan)
                    val prof by viewModel.userProfile.collectAsState()
                    val isPremium = prof?.plan == "premium"

                    if (isPremium) {
                        IconButton(onClick = { Toast.makeText(context, "شما عضو طلایی ممتاز هستید.", Toast.LENGTH_SHORT).show() }) {
                            Icon(Icons.Default.Star, contentDescription = "VIP", tint = StarryGold)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.upgradeToPremium() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryCyan,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("ارتقا به طلایی", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // User text bubble avatar as in HTML
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryCyan.copy(alpha = 0.12f))
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ک",
                            color = PrimaryCyan,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CosmicSurface,
                    titleContentColor = TextPrimary
                )
            )

            // Dynamic view selector based on the active state
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (viewModel.currentTab) {
                    AppTab.CHAT -> ChatTabContent(viewModel)
                    AppTab.IMAGE -> ImageLogoTabContent(viewModel, isLogoCreator = false)
                    AppTab.LOGO -> ImageLogoTabContent(viewModel, isLogoCreator = true)
                    AppTab.EDITOR -> EditorCodingTabContent(viewModel, isCodingAssistant = false)
                    AppTab.CODING -> EditorCodingTabContent(viewModel, isCodingAssistant = true)
                    AppTab.DOCUMENT -> DocContentTabContent(viewModel, isContentValue = false)
                    AppTab.CONTENT -> DocContentTabContent(viewModel, isContentValue = true)
                    AppTab.PROFILE -> ProfileAdminTabContent(viewModel, isAdminPanel = false)
                    AppTab.ADMIN -> ProfileAdminTabContent(viewModel, isAdminPanel = true)
                }
            }

            // Normal mobile device screen navigation
            if (!isTablet) {
                NavigationBar(
                    containerColor = CosmicSurface,
                    tonalElevation = 8.dp
                ) {
                    val menuItems = getNavigationItems()
                    menuItems.forEach { (tab, label, icon) ->
                        val isSelected = viewModel.currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.switchTab(tab) },
                            icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp)) },
                            label = { Text(label, fontSize = 9.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CosmicBackground,
                                selectedTextColor = PrimaryCyan,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = PrimaryCyan
                            ),
                            modifier = Modifier.testTag("nav_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }
    }
}

// Helper structures containing all modern tabs configuration
fun getNavigationItems() = listOf(
    Triple(AppTab.CHAT, "گفتگو", Icons.Default.Search),
    Triple(AppTab.IMAGE, "نگارگر", Icons.Default.Add),
    Triple(AppTab.LOGO, "لوگو ساز", Icons.Default.Star),
    Triple(AppTab.EDITOR, "ادیتور", Icons.Default.Edit),
    Triple(AppTab.CODING, "کدنویس", Icons.Default.PlayArrow),
    Triple(AppTab.DOCUMENT, "اسناد", Icons.Default.Add),
    Triple(AppTab.CONTENT, "محتوا", Icons.Default.Share),
    Triple(AppTab.PROFILE, "پروفایل", Icons.Default.Person)
)

@Composable
fun DocContentTabContent(viewModel: AppViewModel, isContentValue: Boolean) {
    com.example.ui.DocContentTabContent(viewModel, isContentValue)
}
