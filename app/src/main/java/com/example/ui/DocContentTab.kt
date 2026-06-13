package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun DocContentTabContent(viewModel: AppViewModel, isContentTool: Boolean) {
    if (isContentTool) {
        ContentCreationView(viewModel)
    } else {
        DocumentGeneratorView(viewModel)
    }
}

@Composable
fun DocumentGeneratorView(viewModel: AppViewModel) {
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
            text = "ساخت و تبدیل اسناد هوشمند کمشک",
            color = PrimaryCyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "ارائه پاورپوینت‌های شرکتی، فاکتورهای رسمی PDF، صورت‌های مالی Excel و مستندات Word را در عرض چند لحظه آماده و دانلود کنید.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 20.sp
        )

        // Select Document Type
        Text("انتخاب قالب و سند هدف", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        val docTypes = listOf("معرفی پاورپوینت", "گزارش رسمی PDF", "صفحه مالی Excel", "مستندات متنی Word")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(docTypes) { type ->
                val isSelected = viewModel.docType == type
                val borderCol = if (isSelected) PrimaryCyan else CosmicBorder
                val bgCol = if (isSelected) CosmicSurfaceCard else CosmicSurface

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgCol)
                        .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                        .clickable { viewModel.docType = type }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(text = type, color = if (isSelected) TextPrimary else TextSecondary, fontSize = 12.sp)
                }
            }
        }

        // Title and notes input
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("عنوان سند یا گزارش", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = viewModel.docTitle,
                    onValueChange = { viewModel.docTitle = it },
                    placeholder = { Text("مثال: گزارش صورت سود و زیان مالی ۶ ماهه نخست سال...", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("doc_title_field")
                        .clip(RoundedCornerShape(10.dp)),
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

                Text("یادداشت‌ها و جزییات محتوا", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = viewModel.docNotes,
                    onValueChange = { viewModel.docNotes = it },
                    placeholder = { Text("شاخص‌های رشد، جدول دارایی‌ها و توضیحات تکمیلی...", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CosmicSurfaceCard,
                        unfocusedContainerColor = CosmicSurfaceCard,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 3
                )
            }
        }

        // Color palettes selector
        Text("انتخاب تم گرافیکی سند", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val palettes = listOf(
                "سبز زمردی" to Color(0xFF10B981),
                "آبی تکنولوژی" to Color(0xFF2979FF),
                "طلایی شاهانه" to Color(0xFFFFD700),
                "برنز مدیریتی" to Color(0xFFE67E22)
            )
            palettes.forEach { (name, color) ->
                val isSelected = viewModel.docThemeColor == name
                val outlineCol = if (isSelected) PrimaryCyan else CosmicBorder

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CosmicSurface)
                        .border(1.5.dp, outlineCol, RoundedCornerShape(12.dp))
                        .clickable { viewModel.docThemeColor = name }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(color))
                    Text(text = name, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Generate doc button or loaders
        if (viewModel.isGeneratingDoc) {
            ImageLoadingProgress()
        } else {
            Button(
                onClick = { viewModel.generateDocument() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("doc_generate_button"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "رندر و تولید نهایی سند ساختاریافته", color = CosmicBackground, fontWeight = FontWeight.Bold)
            }
        }

        // Live visual document structure rendering block
        AnimatedVisibility(
            visible = viewModel.savedProjects.value.any { it.type == "document" },
            enter = fadeIn() + expandVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CosmicSurface, RoundedCornerShape(16.dp))
                    .border(1.dp, CosmicBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("پیش‌نمایش زنده ساختار رندر شده", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = {
                            Toast.makeText(context, "سند '${viewModel.docTitle}' دانلود و در حافظه ذخیره شد.", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceCard),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(14.dp))
                            Text("دانلود سند نهایی", color = TextPrimary, fontSize = 11.sp)
                        }
                    }
                }

                // Render dynamic layout styled differently depending on chosen docType
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CosmicSurfaceCircleBack, RoundedCornerShape(12.dp))
                        .border(1.dp, CosmicBorder, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header official representation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "کمشک داک رندر", color = PrimaryCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = "شماره: K-902-DX", color = TextMuted, fontSize = 9.sp)
                    }

                    Divider(color = CosmicBorder)

                    Text(text = "عنوان سند: ${viewModel.docTitle}", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    if (viewModel.docType == "معرفی پاورپوینت") {
                        // Drawing PowerPoint mock slide thumbnail list
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            for (i in 1..3) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(CosmicBackground, RoundedCornerShape(6.dp))
                                        .border(0.5.dp, PrimaryCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("اسلاید $i", color = StarryGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(TextMuted.copy(alpha = 0.2f)))
                                    Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(TextMuted.copy(alpha = 0.2f)))
                                }
                            }
                        }
                    } else if (viewModel.docType == "گزارش رسمی PDF") {
                        // Drawing invoice details
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("متن خلاصه گزارش: ${viewModel.docNotes}", color = TextSecondary, fontSize = 11.sp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CosmicBackground, RoundedCornerShape(4.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("مالیات و عوارض کارهای هوشمند", color = TextSecondary, fontSize = 10.sp)
                                Text("۰٪ (رایگان)", color = PrimaryCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Excel spreadsheet representation
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            for (r in 1..3) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = "ردیف $r", color = TextMuted, fontSize = 9.sp, modifier = Modifier.width(36.dp))
                                    Box(modifier = Modifier.weight(1f).height(14.dp).background(CosmicBackground, RoundedCornerShape(2.dp)))
                                    Box(modifier = Modifier.weight(1f).height(14.dp).background(CosmicBackground, RoundedCornerShape(2.dp)))
                                    Box(modifier = Modifier.width(50.dp).height(14.dp).background(PrimaryCyan.copy(alpha = 0.12f), RoundedCornerShape(2.dp))) {
                                        Text("جمع کل", color = PrimaryCyan, fontSize = 8.sp, modifier = Modifier.align(Alignment.Center))
                                    }
                                }
                            }
                        }
                    }

                    Text(text = "* این یک سند شبیه‌سازی شدهٔ رسمی با تم رنگی '${viewModel.docThemeColor}' است که قابلیت تبدیل مستقیم به فرمت‌های آفیس را دارد.", color = TextMuted, fontSize = 9.sp, lineHeight = 14.sp)
                }
            }
        }
    }
}

@Composable
fun ContentCreationView(viewModel: AppViewModel) {
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
            text = "تولیدکننده محتوا و کپی‌رایتینگ کمشک",
            color = PrimaryCyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "دیگر نیازی به ساعت‌ها فکر کردن ندارید؛ کپی‌رایتر هوشمند بهترین مقاله‌ها، متن‌های فانتزی داستان، سناریوهای یوتیوب و هشتگ‌های بازاریابی را ردیف می‌کند.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 20.sp
        )

        // Select tool category
        Text("انتخاب قالب نگارشی", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        val categories = listOf("پست وبلاگ", "کپی‌رایتینگ و هشتگ", "سناریو فیلم و یوتیوب", "داستان فانتزی بلند", "تولید تگ SEO")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(categories) { cat ->
                val isSelected = viewModel.contentCategory == cat
                val borderCol = if (isSelected) PrimaryCyan else CosmicBorder
                val bgCol = if (isSelected) CosmicSurfaceCard else CosmicSurface

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgCol)
                        .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                        .clickable { viewModel.contentCategory = cat }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(text = cat, color = if (isSelected) TextPrimary else TextSecondary, fontSize = 12.sp)
                }
            }
        }

        // Subject input box
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("کلیدواژه یا موضوع نگاره", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = viewModel.contentTopicInput,
                    onValueChange = { viewModel.contentTopicInput = it },
                    placeholder = { Text("مثال: معرفی قابلیت‌های هوش مصنوعی مینیاتوری در رباتیک...", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("content_subject_input")
                        .clip(RoundedCornerShape(10.dp)),
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

                Spacer(modifier = Modifier.height(4.dp))

                Text("لحن و زبان بیان قلم", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                val tones = listOf("جذاب و خلاقانه", "رسمی و اداری", "احساسی روانشناختی", "بسیار پرانرژی")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(tones) { tone ->
                        val isSelected = viewModel.contentToneValue == tone
                        val outlineCol = if (isSelected) PrimaryCyan else CosmicBorder
                        val bgCol = if (isSelected) CosmicSurfaceCard else Color.Transparent

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(bgCol)
                                .border(1.dp, outlineCol, RoundedCornerShape(8.dp))
                                .clickable { viewModel.contentToneValue = tone }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = tone, color = TextPrimary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Generate action triggered
        if (viewModel.isGeneratingContent) {
            ImageLoadingProgress()
        } else {
            Button(
                onClick = { viewModel.generateContentTool() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("content_generate_button"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "نگارش متن با هوش مصنوعی و بهینه‌ساز SEO", color = CosmicBackground, fontWeight = FontWeight.Bold)
            }
        }

        // Result display board
        AnimatedVisibility(
            visible = viewModel.generatedContentResponse.isNotEmpty(),
            enter = fadeIn() + expandVertically()
        ) {
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = StarryGold)
                            Text("محتوای تولید شده کپی‌رایتر", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(viewModel.generatedContentResponse))
                                Toast.makeText(context, "محتوای پست در حافظه موقت کپی شد.", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Copy text", tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                        }
                    }

                    Divider(color = CosmicBorder)

                    Text(
                        text = viewModel.generatedContentResponse,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
