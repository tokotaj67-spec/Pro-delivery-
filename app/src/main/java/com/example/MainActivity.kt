package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FoodDeliveryViewModel
import com.example.ui.components.MockSmartphoneFrame
import com.example.ui.screens.AiSuggestionScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: FoodDeliveryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Adaptive premium design frame wrapper (portrait phone aspect inside desktop view, adaptive full screen on mobile)
                MockSmartphoneFrame {
                    AppMainLayout(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppMainLayout(viewModel: FoodDeliveryViewModel) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val showSnackbar: (String) -> Unit = { message ->
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // Nav Item 1: Menu
                NavigationBarItem(
                    selected = state.activeTab == 0,
                    onClick = { 
                        focusManager.clearFocus()
                        viewModel.updateActiveTab(0) 
                    },
                    label = { Text("Menu", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.RestaurantMenu,
                            contentDescription = "Menu Items"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    )
                )

                // Nav Item 2: Orders & Bag
                NavigationBarItem(
                    selected = state.activeTab == 1,
                    onClick = { 
                        focusManager.clearFocus()
                        viewModel.updateActiveTab(1) 
                    },
                    label = { Text("Orders", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = {
                        Box {
                            Icon(
                                imageVector = Icons.Filled.ShoppingBag,
                                contentDescription = "Active Orders"
                            )
                            val cartItemCount = viewModel.getCartCount()
                            if (cartItemCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 8.dp, y = (-4).dp)
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$cartItemCount",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    )
                )

                // Nav Item 3: AI chef Suggestions
                NavigationBarItem(
                    selected = state.activeTab == 2,
                    onClick = { 
                        focusManager.clearFocus()
                        viewModel.updateActiveTab(2) 
                    },
                    label = { Text("AI Suggestion", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "AI Suggestions"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    )
                )
            }
        },
        floatingActionButton = {
            // Floating cart button shown on Home Screen only when cart contains items
            val cartItemCount = viewModel.getCartCount()
            AnimatedVisibility(
                visible = state.activeTab == 0 && cartItemCount > 0,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { viewModel.updateActiveTab(1) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ShoppingBag,
                            contentDescription = "Floating Cart Button"
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$cartItemCount",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        // Render screens nicely with elegant Crossfade screen transitions under 350ms
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = state.activeTab,
                animationSpec = tween(300),
                label = "ScreenCrossfade"
            ) { activeTab ->
                when (activeTab) {
                    0 -> HomeScreen(
                        viewModel = viewModel,
                        state = state,
                        onSnackShow = showSnackbar
                    )
                    1 -> OrdersScreen(
                        viewModel = viewModel,
                        state = state,
                        onSnackShow = showSnackbar
                    )
                    2 -> AiSuggestionScreen(
                        viewModel = viewModel,
                        state = state,
                        onSnackShow = showSnackbar
                    )
                }
            }
        }
    }
}
