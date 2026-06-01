package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MockSmartphoneFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val maxWidthVal = maxWidth
        val isWideScreen = maxWidthVal > 500.dp

        if (isWideScreen) {
            // Render premium smartphone mockup shell (similar to Dribbble showcase specs)
            Box(
                modifier = Modifier
                    .width(420.dp)
                    .fillMaxHeight()
                    .padding(vertical = 16.dp)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(36.dp),
                        clip = false,
                        ambientColor = Color(0x7F1E2229),
                        spotColor = Color(0x7F1E2229)
                    )
                    .background(Color(0xFF0F0F11), RoundedCornerShape(36.dp))
                    .border(5.dp, Color(0xFF232529), RoundedCornerShape(36.dp))
                    .padding(8.dp) // Bezel width
            ) {
                // Phone Screen content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(28.dp))
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Simulated Mobile OS Status Bar inside mockup
                        SimulatedStatusBar()

                        // Core App Area
                        Box(modifier = Modifier.weight(1f)) {
                            content()
                        }

                        // Simulated bottom gesture pill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color.Gray)
                            )
                        }
                    }

                    // Simulated Camera Island (Dynamic Notch)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 6.dp)
                            .width(90.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(Color.Black)
                    ) {
                        // Tiny lens reflection reflection
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 12.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF131542))
                        )
                    }
                }
            }
        } else {
            // Full screen content on standard narrow device formats
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SimulatedStatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(26.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // Time display
        Text(
            text = "09:41",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Simulated Status Icons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.SignalCellular4Bar,
                contentDescription = "Signal strength",
                modifier = Modifier.size(11.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Filled.Wifi,
                contentDescription = "Wifi connection",
                modifier = Modifier.size(11.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Filled.BatteryFull,
                contentDescription = "Battery level",
                modifier = Modifier.size(11.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
