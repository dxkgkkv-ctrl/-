package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ImageLogoTabContent(viewModel: AppViewModel, isLogoCreator: Boolean) {
    if (isLogoCreator) {
        LogoCreatorView(viewModel)
    } else {
        ImageGeneratorView(viewModel)
    }
}

@Composable
fun ImageGeneratorView(viewModel: AppViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "تولید تصویر هوشمند کمشک",
            color = PrimaryCyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "توصیف خلاقانه خود را از تصویر مد نظر بنویسید تا هوش مصنوعی آن را ثانیه‌هایی بعد با خلاقیت تمام ترسیم کند.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 20.sp
        )

        // Text prompt
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("توصیف تصویر (پرامپت اصلی)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = viewModel.imagePrompt,
                    onValueChange = { viewModel.imagePrompt = it },
                    placeholder = { Text("مثال: یک فضانورد سوار بر اسب سفید در مریخ، سبک نقاشی دیجیتال سورئال، نورپردازی نئونی کبالت...", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("image_prompt_input")
                        .clip(RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CosmicSurfaceCard,
                        unfocusedContainerColor = CosmicSurfaceCard,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 4
                )

                // Prompt enhance chip
                Button(
                    onClick = {
                        viewModel.imagePrompt = "پرتره یک سیمرغ افسانه‌ای غرق در نورهای جادویی نئون با پرهای طلایی و فیروزه‌ای، پس‌زمینه کهکشانی، کیفیت جادویی پرتره ۴کا"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceCard),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = StarryGold, modifier = Modifier.size(14.dp))
                        Text("بهینه‌سازی خودکار پرامپت با هوش مصنوعی", color = PrimaryCyan, fontSize = 11.sp)
                    }
                }
            }
        }

        // Style selector
        Text("انتخاب هنر و سبک تصویر", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        val stylesList = listOf("عکاسی واقعی", "انیمه", "سه بعدی", "نقاشی رنگ روغن", "سبک کمیک")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(stylesList) { style ->
                val isSelected = viewModel.imageStyle == style
                val borderCol = if (isSelected) PrimaryCyan else CosmicBorder
                val bgCol = if (isSelected) CosmicSurfaceCard else CosmicSurface

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgCol)
                        .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                        .clickable { viewModel.imageStyle = style }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = style,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Aspect ratio selector
        Text("ابعاد تصویر خروجی", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val sizes = listOf("1:1" to "مربع هماهنگ", "16:9" to "منظره عریض", "9:16" to "پرتره موبایل")
            sizes.forEach { (ratio, label) ->
                val isSelected = viewModel.imageSize == ratio
                val borderCol = if (isSelected) PrimaryCyan else CosmicBorder

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CosmicSurface)
                        .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                        .clickable { viewModel.imageSize = ratio }
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Ratio visualizer box
                    Box(
                        modifier = Modifier
                            .size(
                                width = if (ratio == "16:9") 36.dp else if (ratio == "9:16") 20.dp else 28.dp,
                                height = if (ratio == "9:16") 36.dp else if (ratio == "16:9") 20.dp else 28.dp
                            )
                            .border(1.5.dp, if (isSelected) PrimaryCyan else TextMuted, RoundedCornerShape(3.dp))
                    )
                    Text(text = ratio, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = label, color = TextSecondary, fontSize = 10.sp)
                }
            }
        }

        // HD and Negative options
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("خروجی کیفیت Ultra HD", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("وضوح فوق‌العاده بالا به همراه رندر جزییات اضافه", color = TextSecondary, fontSize = 10.sp)
                    }
                    Switch(
                        checked = viewModel.imageQuality == "Ultra HD",
                        onCheckedChange = { viewModel.imageQuality = if (it) "Ultra HD" else "HD" },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PrimaryCyan,
                            checkedTrackColor = SecondaryTurquoise.copy(alpha = 0.5f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text("کلمات منفی (Negative Prompt)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = viewModel.negativePrompt,
                    onValueChange = { viewModel.negativePrompt = it },
                    placeholder = { Text("کلماتی که در تصویر نباید باشند: سایه زیاد، تاریک، چهره دوتایی، بد فرم...", color = TextMuted, fontSize = 11.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CosmicSurfaceCard,
                        unfocusedContainerColor = CosmicSurfaceCard,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 2
                )
            }
        }

        // Action generate button or loader
        if (viewModel.isGeneratingImage) {
            ImageLoadingProgress()
        } else {
            Button(
                onClick = { viewModel.generateAIImage() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_image_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(PrimaryCyan, SecondaryTurquoise))),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = CosmicBackground)
                        Text("تولید تصویر هوشمند هولوگرافیک", color = CosmicBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Procedural Generated Canvas Display
        AnimatedVisibility(
            visible = viewModel.generatedImageSeed != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(CosmicSurfaceHex), RoundedCornerShape(16.dp))
                    .border(2.dp, PrimaryCyan, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("تصویر رندر شده گالری کمشک", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "طراحی در گالری با موفقیت ذخیره شد.", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Save link", tint = PrimaryCyan)
                    }
                }

                // Custom dynamic canvas painting representing visual artistry based on seed
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F121C))
                ) {
                    val progressAnim = rememberInfiniteTransition().animateFloat(
                        initialValue = 0f, targetValue = 1f,
                        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse)
                    )
                    val accent = viewModel.generatedImageColorAccent

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val baseColor1 = when (accent) {
                            "neon_pink" -> Color(0xFFE91E63)
                            "cyber_neon" -> Color(0xFF00E5FF)
                            "amber_gold" -> Color(0xFFFFC107)
                            "azure_glow" -> Color(0xFF2979FF)
                            else -> Color(0xFF00E676)
                        }

                        val baseColor2 = when (accent) {
                            "neon_pink" -> Color(0xFF9C27B0)
                            "cyber_neon" -> Color(0xFF7C4DFF)
                            "amber_gold" -> Color(0xFFFF5722)
                            "azure_glow" -> Color(0xFF00E5FF)
                            else -> Color(0xFF00E5FF)
                        }

                        // Drawing cosmic beautiful canvas background elements
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(baseColor2.copy(alpha = 0.3f), Color.Transparent),
                                center = Offset(size.width * 0.5f, size.height * 0.5f),
                                radius = size.width * 0.7f
                            )
                        )

                        // Draw mountain silhouettes path
                        val mountPath = Path().apply {
                            moveTo(0f, size.height)
                            lineTo(size.width * 0.25f, size.height * 0.45f)
                            lineTo(size.width * 0.45f, size.height * 0.7f)
                            lineTo(size.width * 0.75f, size.height * 0.35f)
                            lineTo(size.width, size.height)
                            close()
                        }
                        drawPath(
                            mountPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(baseColor1.copy(alpha = 0.5f), Color(0xFF020408))
                            )
                        )

                        // Draw secondary transparent hills
                        val mountPath2 = Path().apply {
                            moveTo(0f, size.height)
                            lineTo(size.width * 0.15f, size.height * 0.65f)
                            lineTo(size.width * 0.55f, size.height * 0.4f)
                            lineTo(size.width * 0.85f, size.height * 0.55f)
                            lineTo(size.width, size.height)
                            close()
                        }
                        drawPath(
                            mountPath2,
                            brush = Brush.verticalGradient(
                                colors = listOf(baseColor2.copy(alpha = 0.3f), Color(0xFF03050B).copy(alpha = 0.7f))
                            )
                        )

                        // Drawing glowing planetary loops or stars
                        drawCircle(
                            color = baseColor1,
                            radius = 28f + progressAnim.value * 8f,
                            center = Offset(size.width * 0.75f, size.height * 0.25f)
                        )

                        // Planet shine ring
                        drawArc(
                            color = baseColor2,
                            startAngle = 120f,
                            sweepAngle = 180f,
                            useCenter = false,
                            topLeft = Offset(size.width * 0.75f - 40f, size.height * 0.25f - 40f),
                            size = Size(80f, 80f),
                            style = Stroke(width = 3f)
                        )

                        // Neon grid projection bottom lines
                        for (i in 0..10) {
                            val lineX = size.width * (i.toFloat() / 10f)
                            drawLine(
                                color = baseColor2.copy(alpha = 0.25f),
                                start = Offset(size.width * 0.5f, size.height * 0.75f),
                                end = Offset(lineX, size.height),
                                strokeWidth = 2f
                            )
                        }
                    }
                }

                Text(
                    text = "سبک انتخابی: ${viewModel.imageStyle} | وضوح: ${viewModel.imageSize} (${viewModel.imageQuality})",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun LogoCreatorView(viewModel: AppViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "برند و لوگوساز هوشمند کمشک",
            color = AccentPurple,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "نام برند کاربری و شعار تبلیغاتی خود را بنویسید. سپس نوع آیکون نمادین و پالت لوکس انتخابی را کلیک کنید تا لوگوی حرفه‌ای برداری رندر شود.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 20.sp
        )

        // Text inputs
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("نام تجاری برند", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = viewModel.logoText,
                    onValueChange = { viewModel.logoText = it },
                    placeholder = { Text("نام کمپانی شما (مثال: پارس بتون یا کمشک جی‌پی‌تی)...", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("logo_text_field")
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CosmicSurfaceCard,
                        unfocusedContainerColor = CosmicSurfaceCard,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text("شعار کوتاه سازمانی", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = viewModel.logoSlogan,
                    onValueChange = { viewModel.logoSlogan = it },
                    placeholder = { Text("شعار (مثال: ساختن آیندهٔ ساخت و ساز)...", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CosmicSurfaceCard,
                        unfocusedContainerColor = CosmicSurfaceCard,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 1
                )
            }
        }

        // Style select presets
        Text("سبک طراحی گرافیکی", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        val stylePresets = listOf("لاکچری", "مینیمال مدرن", "سلطنتی", "صنعتی آوانگارد")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(stylePresets) { preset ->
                val isSelected = viewModel.logoStylePreset == preset
                val borderCol = if (isSelected) AccentPurple else CosmicBorder
                val bgCol = if (isSelected) CosmicSurfaceCard else CosmicSurface

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgCol)
                        .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                        .clickable { viewModel.logoStylePreset = preset }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = preset,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Emblem visual options
        Text("نوع نشان تصویری (امبلم)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        val emblemsList = listOf("پر طلایی", "دایره اوربیتال", "سپر مدافع", "شش ضلعی تکنولوژی")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            emblemsList.forEach { emblem ->
                val isSelected = viewModel.logoEmblemType == emblem
                val borderCol = if (isSelected) AccentPurple else CosmicBorder

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CosmicSurface)
                        .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                        .clickable { viewModel.logoEmblemType = emblem }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = when(emblem) {
                            "پر طلایی" -> Icons.Default.Star
                            "دایره اوربیتال" -> Icons.Default.Refresh
                            "سپر مدافع" -> Icons.Default.Lock
                            else -> Icons.Default.Menu
                        },
                        contentDescription = null,
                        tint = if (isSelected) StarryGold else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = emblem,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Generate logo button
        if (viewModel.isGeneratingLogo) {
            ImageLoadingProgress()
        } else {
            Button(
                onClick = { viewModel.generateBusinessLogo() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_logo_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(AccentPurple, PrimaryCyan))),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Text("رندر فوری آیکون برداری", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Procedural Logo Draw display
        AnimatedVisibility(
            visible = viewModel.savedLogoSeed != 0L,
            enter = fadeIn() + expandVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillModifierWithGradientBorder(16f)
                    .fillMaxWidth()
                    .background(CosmicSurface, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "لوگوی اختصاصی رندر شده برند شما",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                // The procedural Logo Vector board
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0C0E16))
                        .border(1.dp, CosmicBorder, RoundedCornerShape(16.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeCol = if (viewModel.logoStylePreset == "سلطنتی") StarryGold else PrimaryCyan
                        val fillCol = AccentPurple

                        // Drawing Custom Emblem shapes based on selection
                        when (viewModel.logoEmblemType) {
                            "پر طلایی" -> {
                                // Draw glowing wings
                                val leftWing = Path().apply {
                                    moveTo(size.width * 0.45f, size.height * 0.2f)
                                    cubicTo(
                                        size.width * 0.2f, size.height * 0.1f,
                                        size.width * 0.1f, size.height * 0.5f,
                                        size.width * 0.4f, size.height * 0.7f
                                    )
                                    lineTo(size.width * 0.45f, size.height * 0.5f)
                                    close()
                                }
                                drawPath(leftWing, brush = Brush.linearGradient(listOf(strokeCol, fillCol)))

                                val rightWing = Path().apply {
                                    moveTo(size.width * 0.55f, size.height * 0.2f)
                                    cubicTo(
                                        size.width * 0.8f, size.height * 0.1f,
                                        size.width * 0.9f, size.height * 0.5f,
                                        size.width * 0.6f, size.height * 0.7f
                                    )
                                    lineTo(size.width * 0.55f, size.height * 0.5f)
                                    close()
                                }
                                drawPath(rightWing, brush = Brush.linearGradient(listOf(strokeCol, fillCol)))

                                drawCircle(
                                    color = StarryGold,
                                    radius = 16f,
                                    center = Offset(size.width * 0.5f, size.height * 0.4f)
                                )
                            }
                            "دایره اوربیتال" -> {
                                // Dynamic neon concentric rings
                                drawCircle(
                                    color = strokeCol,
                                    radius = 48f,
                                    center = Offset(size.width * 0.5f, size.height * 0.5f),
                                    style = Stroke(width = 4f)
                                )
                                drawCircle(
                                    color = fillCol,
                                    radius = 32f,
                                    center = Offset(size.width * 0.5f, size.height * 0.5f),
                                    style = Stroke(width = 2f)
                                )
                                drawCircle(
                                    color = StarryGold,
                                    radius = 10f,
                                    center = Offset(size.width * 0.35f, size.height * 0.35f)
                                )
                            }
                            "سپر مدافع" -> {
                                // Draw armor crest shield path
                                val shield = Path().apply {
                                    moveTo(size.width * 0.5f, size.height * 0.2f)
                                    lineTo(size.width * 0.8f, size.height * 0.25f)
                                    quadraticTo(
                                        size.width * 0.8f, size.height * 0.6f,
                                        size.width * 0.5f, size.height * 0.85f
                                    )
                                    quadraticTo(
                                        size.width * 0.2f, size.height * 0.6f,
                                        size.width * 0.2f, size.height * 0.25f
                                    )
                                    close()
                                }
                                drawPath(shield, brush = Brush.verticalGradient(listOf(strokeCol.copy(alpha = 0.5f), fillCol.copy(alpha = 0.5f))))
                                drawPath(shield, color = strokeCol, style = Stroke(width = 3f))
                            }
                            else -> {
                                // Hexagon technical network shape
                                val hexPath = Path().apply {
                                    moveTo(size.width * 0.5f, size.height * 0.2f)
                                    lineTo(size.width * 0.76f, size.height * 0.35f)
                                    lineTo(size.width * 0.76f, size.height * 0.65f)
                                    lineTo(size.width * 0.5f, size.height * 0.8f)
                                    lineTo(size.width * 0.24f, size.height * 0.65f)
                                    lineTo(size.width * 0.24f, size.height * 0.35f)
                                    close()
                                }
                                drawPath(hexPath, color = strokeCol, style = Stroke(width = 3.5f))
                            }
                        }
                    }
                }

                // Company brand outputs
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = viewModel.logoText, color = strokeColHex(viewModel.logoStylePreset), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    if (viewModel.logoSlogan.isNotEmpty()) {
                        Text(text = viewModel.logoSlogan, color = TextSecondary, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Action vectors export
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val formats = listOf("PNG", "SVG Vector", "PDF Mockup")
                    formats.forEach { form ->
                        Button(
                            onClick = { Toast.makeText(context, "فایل با فرمت $form دانلود شد.", Toast.LENGTH_SHORT).show() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceCard),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "$form خروجی", color = TextPrimary, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageLoadingProgress() {
    val infiniteTransition = rememberInfiniteTransition()
    val progressAnim by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Restart)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CosmicSurfaceCard, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LinearProgressIndicator(
            progress = { progressAnim / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp)),
            color = PrimaryCyan,
            trackColor = CosmicBorder
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("عملیات بارگذاری هوش مصنوعی...", color = TextSecondary, fontSize = 11.sp)
            Text("${progressAnim.toInt()}%", color = PrimaryCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

fun strokeColHex(preset: String): Color {
    return if (preset == "سلطنتی" || preset == "لاکچری") StarryGold else PrimaryCyan
}

const val CosmicSurfaceHex = 0xFFFFFFFF
