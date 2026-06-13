package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun EditorCodingTabContent(viewModel: AppViewModel, isCodingAssistant: Boolean) {
    if (isCodingAssistant) {
        CodingAssistantView(viewModel)
    } else {
        PhotoEditorView(viewModel)
    }
}

@Composable
fun PhotoEditorView(viewModel: AppViewModel) {
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
            text = "ویرایشگر عکس هوشمند کمشک",
            color = PrimaryCyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "پس‌زمینه عکس خود را حذف کنید، وضوح چهره را ارتقا دهید، نور را اصلاح کنید و یا سبک جدید اضافه کنید. از اسلایدر مقایسه برای مشاهده تأثیر هوش مصنوعی استفاده نمایید.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 20.sp
        )

        // Select mock sample photos
        Text("انتخاب عکس جهت پردازش", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        val photosList = listOf(
            "طبیعت مه‌آلود کوهستان" to Color(0xFF2C3E50),
            "پرتره رترو استودیو" to Color(0xFFE67E22),
            "خیابان نئونی توکیو" to Color(0xFF9B59B6),
            "پروفایل پرسنلی شرکتی" to Color(0xFF34495E)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(photosList.size) { index ->
                val (title, sampleColor) = photosList[index]
                val isSelected = viewModel.selectedPhotoIndex == index
                val borderCol = if (isSelected) PrimaryCyan else CosmicBorder

                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CosmicSurface)
                        .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                        .clickable {
                            viewModel.selectedPhotoIndex = index
                            viewModel.editorComparisonSlider = 0.5f // Reset slider
                        }
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(sampleColor)
                        )
                        Text(text = title, color = TextPrimary, fontSize = 11.sp, maxLines = 1)
                    }
                }
            }
        }

        // Tools selector
        Text("انتخاب متد هوش مصنوعی", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        val editTools = listOf("حذف پس‌زمینه", "کاهش نویز و بازسازی", "اصلاح رنگ خودکار", "انتقال بردهای هنری")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            editTools.forEach { tool ->
                val isSelected = viewModel.selectedEditorTool == tool
                val borderCol = if (isSelected) PrimaryCyan else CosmicBorder
                val bgCol = if (isSelected) CosmicSurfaceCard else CosmicSurface

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgCol)
                        .border(1.dp, borderCol, RoundedCornerShape(10.dp))
                        .clickable { viewModel.selectedEditorTool = tool }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = tool, color = if (isSelected) TextPrimary else TextSecondary, fontSize = 11.sp)
                }
            }
        }

        // Action edit trigger button
        if (viewModel.isEditingPhoto) {
            CardLoader()
        } else {
            Button(
                onClick = { viewModel.executePhotoEditTool() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "اعمال زنده ادیتور هوشمند", color = CosmicBackground, fontWeight = FontWeight.Bold)
            }
        }

        // Draggable before/after comparison layout
        Text("مقایسه زنده خروجی (قبل / بعد)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CosmicBorder, RoundedCornerShape(16.dp))
        ) {
            val widthPx = remember { mutableStateOf(300f) }

            // Base Photo Rendering with sliders
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F111A))
            ) {
                // Drawing dynamic content inside canvas to represent the selected photo visual effects
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val newSlider = (viewModel.editorComparisonSlider + (dragAmount.x / size.width)).coerceIn(0f, 1f)
                                viewModel.editorComparisonSlider = newSlider
                            }
                        }
                ) {
                    val splitX = size.width * (1.0f - viewModel.editorComparisonSlider) // Map due to RTL slider layout
                    val sampleBgColor = photosList[viewModel.selectedPhotoIndex].second

                    // Drawing "BEFORE" side (Gray, Blurry, Darkened or has grid patterns)
                    // Let's filter the canvas drawing
                    drawRect(
                        brush = Brush.verticalGradient(listOf(sampleBgColor.copy(alpha = 0.5f), Color.Black)),
                        topLeft = Offset(0f, 0f),
                        size = androidx.compose.ui.geometry.Size(splitX, size.height)
                    )

                    // Draw raw mountain shape for "Before"
                    val peak1 = Path().apply {
                        moveTo(0f, size.height)
                        lineTo(size.width * 0.3f, size.height * 0.4f)
                        lineTo(splitX, size.height * 0.8f)
                        lineTo(splitX, size.height)
                        close()
                    }
                    drawPath(peak1, color = Color.Gray.copy(alpha = 0.6f))

                    // Draw grid/noise over before
                    for (row in 0..10) {
                        drawLine(
                            color = Color.White.copy(alpha = 0.04f),
                            start = Offset(0f, size.height * (row / 10f)),
                            end = Offset(splitX, size.height * (row / 10f)),
                            strokeWidth = 1f
                        )
                    }

                    // Drawing "AFTER" side (Sharp, high contrast, radiant glowing vectors)
                    drawRect(
                        brush = Brush.verticalGradient(listOf(sampleBgColor, Color(0xFF03050B))),
                        topLeft = Offset(splitX, 0f),
                        size = androidx.compose.ui.geometry.Size(size.width - splitX, size.height)
                    )

                    // Draw sharp mountain path for "After"
                    val peak2 = Path().apply {
                        moveTo(splitX, size.height)
                        lineTo(size.width * 0.3f, size.height * 0.4f)
                        lineTo(size.width * 0.7f, size.height * 0.3f)
                        lineTo(size.width, size.height)
                        close()
                    }
                    drawPath(
                        peak2,
                        brush = Brush.verticalGradient(listOf(PrimaryCyan, Color(0xFF0C101A)))
                    )

                    // Glow particle effects
                    drawCircle(
                        color = SecondaryTurquoise,
                        radius = 20f,
                        center = Offset(size.width * 0.7f, size.height * 0.3f)
                    )

                    // Slit line
                    drawLine(
                        color = PrimaryCyan,
                        start = Offset(splitX, 0f),
                        end = Offset(splitX, size.height),
                        strokeWidth = 4f
                    )
                }

                // Small circular drag badge aligned over the central split
                Box(
                    modifier = Modifier
                        .offset(
                            x = 28.dp + (270.dp * (1.0f - viewModel.editorComparisonSlider)), // Basic mapping estimate
                            y = 110.dp
                        )
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PrimaryCyan)
                        .border(2.dp, TextPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = CosmicBackground, modifier = Modifier.size(16.dp))
                }

                // Small badge headers
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("قبل ادیت (خام)", color = TextSecondary, fontSize = 9.sp)
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(PrimaryCyan.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("بهینه‌شده با کمشک", color = CosmicBackground, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Text(
            text = "جهت مقایسه، انگشت خود را روی تصویر بالا به سمت چپ یا راست بکشید.",
            color = TextSecondary,
            fontSize = 11.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun CodingAssistantView(viewModel: AppViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
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
            text = "دستیار برنامه‌نویسی کمشک جیپیتی",
            color = PrimaryCyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "برنامه‌نویس فوق‌العاده هوشمند برای طراحی انواع پروژه‌ها، اپلیکیشن‌ها، کدنویسی موبایل، دیباگ خطاهای کامپایلر و بازنویسی کد برقی.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 20.sp
        )

        // Select Language and mode
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Language selector
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("زبان برنامه‌نویسی", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                val languages = listOf("Kotlin", "Python", "Javascript", "HTML/CSS", "SQL")
                var expandedLang by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CosmicSurface)
                        .border(1.dp, CosmicBorder, RoundedCornerShape(10.dp))
                        .clickable { expandedLang = true }
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = viewModel.codingLanguage, color = TextPrimary, fontSize = 13.sp)
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                    }
                }

                DropdownMenu(
                    expanded = expandedLang,
                    onDismissRequest = { expandedLang = false },
                    modifier = Modifier.background(CosmicSurface)
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang, color = TextPrimary) },
                            onClick = {
                                viewModel.codingLanguage = lang
                                expandedLang = false
                            }
                        )
                    }
                }
            }

            // Mode/Action selector
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("نوع فعالیت کدنویسی", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                val actions = listOf("تولید کد", "توضیح دادن کد", "رفع باگ و دیباگ", "بهینه‌سازی کارایی")
                var expandedAct by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CosmicSurface)
                        .border(1.dp, CosmicBorder, RoundedCornerShape(10.dp))
                        .clickable { expandedAct = true }
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = viewModel.codingAction, color = TextPrimary, fontSize = 12.sp)
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                    }
                }

                DropdownMenu(
                    expanded = expandedAct,
                    onDismissRequest = { expandedAct = false },
                    modifier = Modifier.background(CosmicSurface)
                ) {
                    actions.forEach { act ->
                        DropdownMenuItem(
                            text = { Text(act, color = TextPrimary) },
                            onClick = {
                                viewModel.codingAction = act
                                expandedAct = false
                            }
                        )
                    }
                }
            }
        }

        // Text prompt target
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("درخواست پروژه یا تکه کد برنامه‌نویسی", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = viewModel.codeInputPrompt,
                    onValueChange = { viewModel.codeInputPrompt = it },
                    placeholder = { Text("مثال: ساخت لندینگ پیج ریسپانسیو دندان‌پزشکی با جلوه شیشه‌ای و انیمیشن...", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("code_prompt_field")
                        .clip(RoundedCornerShape(10.dp)),
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
            }
        }

        // Run/Generate code action button
        if (viewModel.isCodingLoading) {
            CardLoader()
        } else {
            Button(
                onClick = { viewModel.runCodingTask() },
                modifier = Modifier
                    .fillModifierWithGradientBorder(12f)
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("code_generate_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = PrimaryCyan)
                    Text("تولید و عیب‌یابی کدهای هوشمند", color = PrimaryCyan, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Custom Preloaded Quick Projects selectors
        Text("شروع سریع با پروژه‌های آماده", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        val templateProjects = listOf(
            Triple("کلاینت گت دیتابیس", "یک فانکشن بنویس که بتواند رکوردهای جدول کاربران را از SQLite واکشی کند.", "Kotlin"),
            Triple("صحفه فرود ریسپانسیو", "یک لندینگ پیج زیبا به زبان HTML و CSS با تم تیره و مدرن طراحی کن.", "HTML/CSS"),
            Triple("بات تلگرام اتوماتیک", "یک ربات خبرخوان تلگرام بنویس که خبرها را از فایل rss بخواند.", "Python")
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(templateProjects) { (title, prompt, lang) ->
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CosmicSurface)
                        .border(1.dp, CosmicBorder, RoundedCornerShape(12.dp))
                        .clickable { viewModel.loadPredefinedCode(title, prompt, lang) }
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = title, color = PrimaryCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = lang, color = TextSecondary, fontSize = 10.sp)
                        Text(text = prompt, color = TextMuted, fontSize = 9.sp, maxLines = 2)
                    }
                }
            }
        }

        // Monospace IDE Console Output
        Text("کنسول خروجی ویرایشگر کدهای کمشک", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F111A)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(StatusError))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(StatusWarning))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(StatusSuccess))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${viewModel.codingLanguage} Editor Console", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(viewModel.generatedCodeResponse))
                            Toast.makeText(context, "کدهای تولید شده در حافظه کپی شد.", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Copy code", tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Monospace text
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    Column {
                        // Lines number rendering block
                        val lines = viewModel.generatedCodeResponse.split("\n")
                        lines.forEachIndexed { idx, line ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = String.format("%02d  ", idx + 1),
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = line,
                                    color = getHighlightedCodeColor(line),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Custom simple syntax highlighting colors
fun getHighlightedCodeColor(line: String): Color {
    val trimmed = line.trim()
    return when {
        trimmed.startsWith("//") || trimmed.startsWith("#") || trimmed.startsWith("/*") -> Color(0xFF6A9955) // Green Comments
        trimmed.startsWith("import") || trimmed.startsWith("package") || trimmed.startsWith("from") -> Color(0xFFC586C0) // Import Purple
        trimmed.contains("fun ") || trimmed.contains("def ") || trimmed.contains("class ") || trimmed.contains("const ") -> Color(0xFF569CD6) // blue declarations
        trimmed.contains("\"") || trimmed.contains("'") -> Color(0xFFD69D85) // Orange Strings
        else -> Color(0xFFD4D4D4)
    }
}

@Composable
fun CardLoader() {
    val infiniteTransition = rememberInfiniteTransition()
    val widthFactor by infiniteTransition.animateFloat(
        initialValue = 0.1f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicSurfaceCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(widthFactor)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(PrimaryCyan)
            )
            Text("درحال پردازش گرافیک هوشمند...", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
