package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ProfileAdminTabContent(viewModel: AppViewModel, isAdminPanel: Boolean) {
    if (isAdminPanel) {
        HiddenAdminPanelView(viewModel)
    } else {
        UserProfileView(viewModel)
    }
}

@Composable
fun UserProfileView(viewModel: AppViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val prof by viewModel.userProfile.collectAsState()
    val projects by viewModel.savedProjects.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upper Profile Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.5.dp, PrimaryCyan)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Glow avatar box
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(PrimaryCyan, AccentPurple)))
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(CosmicBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Text(
                    text = prof?.name ?: "کاربر گرامی",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = prof?.email ?: "m.rmhmdamyn@gmail.com",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                // Plan badges
                val isPremium = prof?.plan == "premium"
                if (isPremium) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(Brush.linearGradient(listOf(StarryGold, Color(0xFFFF8F00))))
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text("مشترک ویژه طلایی (Premium)", color = CosmicBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(CosmicSurfaceCard)
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text("طرح هدیه نقره‌ای (رایگان)", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                Divider(color = CosmicBorder, modifier = Modifier.padding(vertical = 4.dp))

                // Stats rows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("اعتبار باقی‌مانده", color = TextSecondary, fontSize = 10.sp)
                        Text("${prof?.points ?: 0} سکه", color = PrimaryCyan, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    VerticalDivider()
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("پروژه‌های ذخیره شده", color = TextSecondary, fontSize = 10.sp)
                        Text("${projects.size} طرح", color = PrimaryCyan, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    VerticalDivider()
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("مصرف فضای کلود", color = TextSecondary, fontSize = 10.sp)
                        Text("۲.۴ مگابایت", color = PrimaryCyan, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Promotional mock premium upgrade card
        if (prof?.plan != "premium") {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1711)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, StarryGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.upgradeToPremium() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, contentDescription = "Upgrade", tint = StarryGold, modifier = Modifier.size(32.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ارتقا به اشتراک غیرمحدود کمشک GPT", color = StarryGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("دسترسی به مدل Pro 3.1، تولید تصویر بدون محدودیت، داک خروجی اکسل و پی‌دی‌اف اختصاصی.", color = Color(0xFFE2E8F0), fontSize = 10.sp, lineHeight = 16.sp)
                    }
                    Icon(Icons.Default.Add, contentDescription = "Buy", tint = StarryGold, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Saved creation lists
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("آرشیو طرح‌ها و خروجی‌های شما", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("تعداد: ${projects.size}", color = TextSecondary, fontSize = 11.sp)
        }

        if (projects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(CosmicSurface, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
                    Text("هنوز پروژه‌ای خلق نکرده‌اید.", color = TextMuted, fontSize = 11.sp)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                projects.forEach { proj ->
                    SavedProjectItem(project = proj, viewModel = viewModel)
                }
            }
        }

        // Settings reset controls
        Button(
            onClick = {
                viewModel.resetUserProfile()
                Toast.makeText(context, "پروفایل به حالت اول برگشت.", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("بازنشانی تنظیمات پروفایل کاربر", color = StatusError, fontSize = 12.sp)
        }
    }
}

@Composable
fun HiddenAdminPanelView(viewModel: AppViewModel) {
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "پنل فوق‌محرمانه ادمین کمشک جیپیتی",
                color = StatusError,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(StatusError.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("SUPER_USER_ROOT", color = StatusError, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
            }
        }

        Text(
            text = "تنظیمات مدل فعال، پایش ترافیک، محدودسازی مصرف منابع، مدیریت فیلترینگ و مانیتورینگ بلادرنگ سخت‌افزاری را از این پنل کنترل کنید.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 20.sp
        )

        // Telemetry monitoring canvas charts
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("پایش بلادرنگ تاخیر شبکه (API Latency)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                // Drw real telemetry chart lines
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(CosmicSurfaceCircleBack, RoundedCornerShape(8.dp))
                        .border(1.dp, CosmicBorder, RoundedCornerShape(8.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val points = listOf(140f, 180f, 290f, 210f, 160f, 240f, 310f, 240f) // raw ms points
                        val graphPath = Path().apply {
                            val spacing = size.width / (points.size - 1)
                            moveTo(0f, size.height - (points[0] / 400f * size.height))
                            points.forEachIndexed { i, p ->
                                lineTo(i * spacing, size.height - (p / 400f * size.height))
                            }
                        }

                        drawPath(
                            graphPath,
                            color = StatusError,
                            style = Stroke(width = 3f)
                        )

                        // Draw baseline grid lines
                        drawLine(
                            color = CosmicBorder,
                            start = Offset(0f, size.height * 0.5f),
                            end = Offset(size.width, size.height * 0.5f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("سلامت سیستم: ${viewModel.adminSystemHealth}%", color = StatusSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("میانگین تاخیر: ${viewModel.adminLatencyFactor}ms", color = StatusWarning, fontSize = 11.sp)
                }
            }
        }

        // Active AI Engine Toggle
        Text("انتخاب هسته پردازشی اصلی", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val models = listOf("Gemini 3.5 Flash", "Gemini 3.1 Pro")
            models.forEach { mdl ->
                val isSelected = viewModel.adminActiveModel == mdl
                val borderCol = if (isSelected) StatusError else CosmicBorder
                val bgCol = if (isSelected) CosmicSurfaceCard else CosmicSurface

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgCol)
                        .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                        .clickable {
                            viewModel.adminActiveModel = mdl
                            viewModel.adminLatencyFactor = if (mdl == "Gemini 3.1 Pro") 420 else 240
                        }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = mdl, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (mdl == "Gemini 3.1 Pro") "هسته سنگین / کیفیت ممتاز" else "هسته سبک / سرعت فوق آوانگارد",
                            color = TextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        // Admin limits sliders
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CosmicBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("ترافیک مجاز هر کاربر به صورت روزانه", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Slider(
                    value = viewModel.adminDailyRequestLimit.toFloat(),
                    onValueChange = { viewModel.adminDailyRequestLimit = it.toInt() },
                    valueRange = 50f..500f,
                    colors = SliderDefaults.colors(
                        thumbColor = StatusError,
                        activeTrackColor = StatusError.copy(alpha = 0.5f)
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("حد مجاز: ${viewModel.adminDailyRequestLimit} درخواست در روز", color = TextPrimary, fontSize = 11.sp)
                    Text("کل کوئری‌های امروز: ${viewModel.adminQueriesCount}", color = PrimaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Divider(color = CosmicBorder, modifier = Modifier.padding(vertical = 4.dp))

                // Safe search toxicity controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("فیلترینگ محتوای نامناسب (SafeSearch)", color = TextPrimary, fontSize = 12.sp)
                    Switch(
                        checked = viewModel.adminIsSafeSearchOn,
                        onCheckedChange = { viewModel.adminIsSafeSearchOn = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = StatusError)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("سامانه تعدیل کلمات سمی و کلاهبرداری", color = TextPrimary, fontSize = 12.sp)
                    Switch(
                        checked = viewModel.adminIsToxicFilterOn,
                        onCheckedChange = { viewModel.adminIsToxicFilterOn = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = StatusError)
                    )
                }
            }
        }

        // System actions purge
        Button(
            onClick = {
                viewModel.resetWholeDatabase()
                Toast.makeText(context, "تمامی اطلاعات کش شده با موفقیت امحا شد.", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = StatusError.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, StatusError)
        ) {
            Text("امحای آرشیو کارهای محلی دیتابیس Room", color = StatusError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SavedProjectItem(project: com.example.data.SavedProject, viewModel: AppViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, CosmicBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val icon = when(project.type) {
                        "logo" -> Icons.Default.Star
                        "code" -> Icons.Default.PlayArrow
                        "document" -> Icons.Default.Add
                        else -> Icons.Default.Search
                    }
                    val brandColor = when(project.type) {
                        "logo" -> StarryGold
                        "code" -> PrimaryCyan
                        "document" -> AccentPurple
                        else -> SecondaryTurquoise
                    }

                    Icon(icon, contentDescription = null, tint = brandColor, modifier = Modifier.size(18.dp))
                    Text(text = project.title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(
                    onClick = { viewModel.deleteProject(project.id) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusError.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                }
            }

            Text(text = project.content, color = TextSecondary, fontSize = 11.sp)

            if (project.details.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CosmicBackground, RoundedCornerShape(8.dp))
                        .border(1.dp, CosmicBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = project.details,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        maxLines = 4
                    )
                }
            }
        }
    }
}

@Composable
fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(28.dp)
            .background(CosmicBorder)
    )
}
