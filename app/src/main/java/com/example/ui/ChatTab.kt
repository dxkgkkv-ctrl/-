package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessage
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ChatTabContent(viewModel: AppViewModel) {
    val sessions by viewModel.chatSessions.collectAsState()
    val messages = viewModel.chatMessagesState.value
    val coroutineScope = rememberCoroutineScope()
    var isRecordingSimulated by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicBackground)
    ) {
        // Horizontal Session Selector
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CosmicSurface,
            tonalElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(vertical = 10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "جلسات گفتگو دستیار کمشک",
                        color = PrimaryCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { viewModel.createNewSession() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "New Chat",
                            tint = PrimaryCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(sessions) { session ->
                        val isSelected = session.id == viewModel.activeSessionId
                        val bgCol = if (isSelected) PrimaryCyan.copy(alpha = 0.08f) else Color.Transparent
                        val borderStrokeColor = if (isSelected) PrimaryCyan.copy(alpha = 0.3f) else CosmicBorder

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(bgCol)
                                .border(1.dp, borderStrokeColor, RoundedCornerShape(20.dp))
                                .clickable { viewModel.selectSession(session.id) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = if (isSelected) PrimaryCyan else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = session.title,
                                    color = if (isSelected) PrimaryCyan else TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                if (sessions.size > 1) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = StatusError.copy(alpha = 0.7f),
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { viewModel.deleteSession(session.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Divider(color = CosmicBorder)

        // Chat Message Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messages.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryCyan.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "گفتگو را شروع کنید...",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "سوال خود را با هوش مصنوعی مطرح کنید. به علاوه می‌توانید فایل پیوست یا صدای شبیه‌سازی شده اضافه کنید.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 20.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages) { msg ->
                        ChatBubbleItem(msg = msg, viewModel = viewModel)
                    }
                    if (viewModel.isChatGenerating) {
                        item {
                            DotLoadingIndicator()
                        }
                    }
                }
            }
        }

        // Voice Input Soundwave animation overlay
        if (isRecordingSimulated) {
            VoiceSoundwaveOverlay {
                isRecordingSimulated = false
                viewModel.chatInputText = "یک اسکریپت فایروال محلی در پایتون بنویس."
            }
        }

        // Attached File Banner
        if (viewModel.attachedFileName != null) {
            Row(
                modifier = Modifier
                    .fillModifierWithGradientBorder(12f)
                    .fillMaxWidth()
                    .background(CosmicSurface)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Star, contentDescription = "File Attached", tint = PrimaryCyan)
                    Text(
                        text = "پیوست: ${viewModel.attachedFileName}",
                        color = TextPrimary,
                        fontSize = 12.sp
                    )
                }
                IconButton(onClick = { viewModel.attachedFileName = null }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear", tint = StatusError)
                }
            }
        }

        // Input Panel
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CosmicSurface,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Attach File simulation button
                IconButton(
                    onClick = {
                        val mockFiles = listOf("گزارش_رشد_کمشک.pdf", "لوگو_آینده_کسب‌کار.png", "دیتابیس_کدهای_سیستمی.json")
                        viewModel.attachedFileName = mockFiles.random()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(CosmicSurfaceCard, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Attach File",
                        tint = TextSecondary
                    )
                }

                // Voice input simulation button
                IconButton(
                    onClick = { isRecordingSimulated = true },
                    modifier = Modifier
                        .size(44.dp)
                        .background(CosmicSurfaceCard, CircleShape)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Voice Input",
                        tint = TextSecondary
                    )
                }

                // Text Input box
                TextField(
                    value = viewModel.chatInputText,
                    onValueChange = { viewModel.chatInputText = it },
                    placeholder = { Text("کمشک جیپیتی پاسخ می‌دهد...", color = TextMuted, fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field")
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, CosmicBorder, RoundedCornerShape(24.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CosmicSurface,
                        unfocusedContainerColor = CosmicSurface,
                        disabledContainerColor = CosmicSurface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 4
                )

                // Send button
                IconButton(
                    onClick = { viewModel.sendChatMessage() },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(PrimaryCyan, SecondaryTurquoise)),
                            shape = CircleShape
                        )
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = CosmicBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubbleItem(msg: ChatMessage, viewModel: AppViewModel) {
    val isUser = msg.sender == "user"
    val clipboardManager = LocalClipboardManager.current
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bgBrush = if (isUser) {
        Brush.linearGradient(listOf(PrimaryCyan, SecondaryTurquoise))
    } else {
        Brush.linearGradient(listOf(CosmicSurfaceCard, CosmicSurfaceCard))
    }
    val contentColor = if (isUser) CosmicBackground else TextPrimary

    val shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isUser) 16.dp else 4.dp,
        bottomEnd = if (isUser) 4.dp else 16.dp
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_bubble_${msg.sender}"),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .then(
                    if (!isUser) Modifier.border(1.dp, CosmicBorder, shape) else Modifier
                )
                .background(bgBrush)
                .padding(14.dp)
                .widthIn(max = 280.dp)
        ) {
            Column {
                Text(
                    text = msg.message,
                    color = contentColor,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isPlaying = viewModel.isSpeakingId == msg.id
                    IconButton(
                        onClick = { viewModel.speakText(msg.id, msg.message) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Warning else Icons.Default.PlayArrow,
                            contentDescription = "Voice Synthesis",
                            tint = if (isUser) CosmicBackground.copy(alpha = 0.6f) else PrimaryCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { clipboardManager.setText(AnnotatedString(msg.message)) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Copy",
                                tint = if (isUser) CosmicBackground.copy(alpha = 0.6f) else TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DotLoadingIndicator() {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedProgress1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val animatedProgress2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val animatedProgress3 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        modifier = Modifier
            .padding(12.dp)
            .background(CosmicSurfaceCard, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("کمشک در حال تفکر", color = TextSecondary, fontSize = 11.sp)
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PrimaryCyan.copy(alpha = animatedProgress1)))
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PrimaryCyan.copy(alpha = animatedProgress2)))
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PrimaryCyan.copy(alpha = animatedProgress3)))
    }
}

@Composable
fun VoiceSoundwaveOverlay(onFinish: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    val waveScale1 by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1.8f,
        animationSpec = infiniteRepeatable(animation = tween(800, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse)
    )
    val waveScale2 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1.5f,
        animationSpec = infiniteRepeatable(animation = tween(1200, delayMillis = 100, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse)
    )
    val waveScale3 by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 2.0f,
        animationSpec = infiniteRepeatable(animation = tween(1000, delayMillis = 200, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse)
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onFinish() },
        color = CosmicBackground.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("در حال شنیدن صدای شما...", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("برای متوقف کردن و شبیه‌سازی کلیک کنید", color = TextSecondary, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(48.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(6.dp).height(40.dp * waveScale1).clip(CircleShape).background(PrimaryCyan))
                Box(modifier = Modifier.width(6.dp).height(50.dp * waveScale2).clip(CircleShape).background(SecondaryTurquoise))
                Box(modifier = Modifier.width(6.dp).height(35.dp * waveScale3).clip(CircleShape).background(AccentPurple))
                Box(modifier = Modifier.width(6.dp).height(60.dp * waveScale1).clip(CircleShape).background(PrimaryCyan))
                Box(modifier = Modifier.width(6.dp).height(45.dp * waveScale2).clip(CircleShape).background(SecondaryTurquoise))
            }
        }
    }
}

fun Modifier.fillModifierWithGradientBorder(radius: Float) = this.then(
    Modifier.padding(1.dp) // Dummy border behavior mapping for generic visual depth
)
