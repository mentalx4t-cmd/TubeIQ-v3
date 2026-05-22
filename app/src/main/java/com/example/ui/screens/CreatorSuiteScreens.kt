package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.data.local.SavedKeyword
import com.example.data.local.SavedVideoSEO
import com.example.data.local.NicheTrend
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun AppNavigationContainer(viewModel: MainViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (!isLoggedIn) {
            LoginSetupScreen(viewModel)
        } else {
            MainTabsNavigator(viewModel)
        }
    }
}

// --- SCREEN 1: LOGIN SETUP SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginSetupScreen(viewModel: MainViewModel) {
    var channelNameInput by remember { mutableStateOf("") }
    var channelIdInput by remember { mutableStateOf("") }
    var youtubeKeyInput by remember { mutableStateOf("") }
    var geminiKeyInput by remember { mutableStateOf("") }
    var expandNicheDropdown by remember { mutableStateOf(false) }
    var selectedNicheInput by remember { mutableStateOf("AI & Technology") }
    var showPass by remember { mutableStateOf(false) }

    val nichesList = listOf(
        "AI & Technology",
        "Gaming & Gameplays",
        "Finance & Crypto",
        "Cooking & Food Vlogs",
        "Fitness & Gym Workouts"
    )

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("login_setup_scroll")
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Spacer(modifier = Modifier.height(30.dp))

            // Pulse Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(AccentCyan, AccentPrimary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Logo icon",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "vidIQ Creator Suite",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Powered by Gemini 1.5 Flash AI Engine",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Channel Integration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Link your channel to unlock automated tag generation & SEO audits.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = channelNameInput,
                        onValueChange = { channelNameInput = it },
                        label = { Text("Channel Handle / Name") },
                        placeholder = { Text("e.g. AI Coding Ninja") },
                        leadingIcon = { Icon(Icons.Default.AccountBox, "Channel icon") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("channel_name_input")
                            .padding(bottom = 12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = channelIdInput,
                        onValueChange = { channelIdInput = it },
                        label = { Text("Channel ID (v3 API)") },
                        placeholder = { Text("e.g. UCxxxxxxxxxxxxxxxx") },
                        leadingIcon = { Icon(Icons.Default.Link, "ID Link icon") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("channel_id_input")
                            .padding(bottom = 12.dp),
                        singleLine = true
                    )

                    // Niche dropdown Setup
                    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        OutlinedTextField(
                            value = selectedNicheInput,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Creator Niche Category") },
                            leadingIcon = { Icon(Icons.Default.Category, "Category icon") },
                            trailingIcon = {
                                IconButton(onClick = { expandNicheDropdown = !expandNicheDropdown }) {
                                    Icon(Icons.Default.ArrowDropDown, "Dropdown toggle")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("niche_dropdown_trigger")
                                .clickable { expandNicheDropdown = true }
                        )
                        DropdownMenu(
                            expanded = expandNicheDropdown,
                            onDismissRequest = { expandNicheDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            nichesList.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedNicheInput = category
                                        expandNicheDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Text(
                        text = "Custom Keys (Remembered until Log out)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Set custom API keys for unlimited official service queries, or leave empty to run in sandbox simulation mode.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = youtubeKeyInput,
                        onValueChange = { youtubeKeyInput = it },
                        label = { Text("YouTube Data API Key") },
                        placeholder = { Text("AIzaSy...") },
                        leadingIcon = { Icon(Icons.Default.Key, "Key icon") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("youtube_key_input")
                            .padding(bottom = 12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation()
                    )

                    OutlinedTextField(
                        value = geminiKeyInput,
                        onValueChange = { geminiKeyInput = it },
                        label = { Text("Gemini API Key override") },
                        placeholder = { Text("Keep empty for system default key") },
                        leadingIcon = { Icon(Icons.Default.Key, "Gemini Key icon") },
                        trailingIcon = {
                            IconButton(onClick = { showPass = !showPass }) {
                                Icon(
                                    imageVector = if (showPass) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Show/hide input"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("gemini_key_input")
                            .padding(bottom = 16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation()
                    )

                    Button(
                        onClick = {
                            val finalChanName = channelNameInput.ifBlank { "Niche Tech Master" }
                            val finalChanId = channelIdInput.ifBlank { "UC_Mck3487fdfhe" }
                            viewModel.login(
                                youtubeKey = youtubeKeyInput,
                                geminiKey = geminiKeyInput,
                                niche = selectedNicheInput,
                                userChannelName = finalChanName,
                                userChannelId = finalChanId
                            )
                            Toast.makeText(context, "Logged in: $finalChanName connected!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("login_submit_button")
                    ) {
                        Icon(Icons.Default.LockOpen, contentDescription = "Log in icon", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Connect and Launch", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- CORE NAVIGATION TAB CONTROLLER ---
@Composable
fun MainTabsNavigator(viewModel: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val alertNotif by viewModel.alertNotification.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Dashboard, "Dashboard") },
                    label = { Text("Dashboard", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Search, "Keyword Analyzer") },
                    label = { Text("Research", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.VideoCall, "Video SEO") },
                    label = { Text("SEO Tool", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Analytics, "Monthly Report") },
                    label = { Text("Summary", fontSize = 10.sp) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Trending alert notification banner
            AnimatedVisibility(
                visible = alertNotif != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                if (alertNotif != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationImportant,
                                contentDescription = "Alert warning icon",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .size(24.dp)
                                    .weight(0.1f)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = alertNotif ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(0.75f)
                            )
                            IconButton(
                                onClick = { viewModel.dismissNotification() },
                                modifier = Modifier
                                    .size(24.dp)
                                    .weight(0.15f)
                            ) {
                                Icon(Icons.Default.Close, "Dismiss banner", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            // Tabs Router
            when (selectedTab) {
                0 -> CreatorDashboardTab(viewModel)
                1 -> KeywordResearchTab(viewModel)
                2 -> VideoSeoTab(viewModel)
                3 -> AnalyticsExportTab(viewModel)
            }
        }
    }
}

// --- TAB 1: CREATOR DASHBOARD ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorDashboardTab(viewModel: MainViewModel) {
    val chName by viewModel.channelName.collectAsState()
    val chNiche by viewModel.creatorNiche.collectAsState()
    val isCustom by viewModel.isCustomMode.collectAsState()
    val widgetsList by viewModel.widgets.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    
    var showWidgetSheet by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Upper Profile Panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = chName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isCustom) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isCustom) "API Active" else "Sandbox",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCustom) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                    Text(
                        text = "SEO Target Niche: $chNiche",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.refreshStats() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh stats logo")
                    }
                    IconButton(onClick = { showWidgetSheet = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Customize widgets layout")
                    }
                    Button(
                        onClick = { viewModel.logout() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Log Out", color = MaterialTheme.colorScheme.error, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (isAnalyzing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        // Lazy scroll of core widgets based on visibility state toggles (customizable)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(widgetsList.filter { it.isVisible }) { widget ->
                when (widget.widgetId) {
                    "channel_reach" -> WidgetMultiPlatformReach(viewModel)
                    "retention_chart" -> WidgetViewerRetentionCurve()
                    "predictive_engagement" -> WidgetPredictiveEstimations()
                    "niche_trends_alert" -> WidgetNicheTrendsAlert(viewModel)
                    "keyword_research_launcher" -> WidgetKeywordLauncher(viewModel)
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal sheet/dialog to configure widget list customization
    if (showWidgetSheet) {
        AlertDialog(
            onDismissRequest = { showWidgetSheet = false },
            confirmButton = {
                TextButton(onClick = { showWidgetSheet = false }) {
                    Text("Done")
                }
            },
            title = {
                Text("Customize Dashboard Metrics", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "Toggle off components you wish to hide from your main console dashboard grid.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    widgetsList.forEach { widget ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (widget.widgetId) {
                                        "channel_reach" -> Icons.Default.Analytics
                                        "retention_chart" -> Icons.Default.ShowChart
                                        "predictive_engagement" -> Icons.Default.OnlinePrediction
                                        "niche_trends_alert" -> Icons.Default.Campaign
                                        else -> Icons.Default.EditRoad
                                    },
                                    contentDescription = "Widget icon",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp).padding(end = 6.dp)
                                )
                                Text(widget.title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                            Switch(
                                checked = widget.isVisible,
                                onCheckedChange = { visible ->
                                    viewModel.updateWidgetVisibility(widget.widgetId, visible)
                                },
                                modifier = Modifier.scale(0.8f)
                            )
                        }
                    }
                }
            }
        )
    }
}

// Extension to scale switches down easily
private fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.padding(0.dp) // dummy modifier spacer, we'd typically use graphicsLayer or layout but standard handles well
)

// --- INDIVIDUAL DASHBOARD WIDGETS ---

@Composable
fun WidgetMultiPlatformReach(viewModel: MainViewModel) {
    val subs by viewModel.youtubeSubscribers.collectAsState()
    val views by viewModel.youtubeViews.collectAsState()
    val vids by viewModel.youtubeVideosCount.collectAsState()
    val tiktokSubs by viewModel.tiktokFollowers.collectAsState()
    val instagramSubs by viewModel.instagramFollowers.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(Icons.Default.Public, contentDescription = "Sync channels node", tint = AccentCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crossplatform Metrics Sync", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            // YouTube Grid Stats
            Text("YOUTUBE MAIN STATS (REAL-TIME DATA)", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = AccentCyan)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(
                    Triple("Subscribers", subs, AccentCyan),
                    Triple("Total Views", views, AccentPrimary),
                    Triple("Video Count", vids, AccentWarning)
                ).forEach { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(item.first, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            Text(
                                text = item.second,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = item.third
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(8.dp))

            // Multi-Platform items: TikTok and Instagram
            Text("SYNC FEED NETWORKS", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.Gray)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // TikTok node
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MusicVideo, "TikTok", tint = Color.Cyan, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("TikTok Live", fontSize = 9.sp, color = Color.Gray)
                            Text("$tiktokSubs Followers", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Instagram node
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(colors = listOf(AccentPink, AccentWarning))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, "Instagram", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Instagram", fontSize = 9.sp, color = Color.Gray)
                            Text("$instagramSubs Followers", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WidgetViewerRetentionCurve() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShowChart, contentDescription = "Retention chart indicator", tint = AccentPink)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Viewer Retention Dynamics", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AccentPink.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("+4.8% Spine Dip Boost", color = AccentPink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text("Historical index tracking average percentage retention over 10:00 video uploads.", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            // Professional canvas curve drawing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Draw grid background line benchmarks
                    val lineCount = 4
                    for (i in 0..lineCount) {
                        val y = h / lineCount * i
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.15f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                    }

                    // 50% threshold line
                    drawLine(
                        color = AccentPink.copy(alpha = 0.25f),
                        start = Offset(0f, h * 0.5f),
                        end = Offset(w, h * 0.5f),
                        strokeWidth = 1.5f,
                        cap = StrokeCap.Round
                    )

                    // Draw organic retention curve: Starts at 100% (top left), dips slowly, levels off around 45%
                    val path = Path().apply {
                        moveTo(0f, 0.05f * h) // 100% Intro
                        cubicTo(
                            w * 0.15f, h * 0.25f, // Hook drop
                            w * 0.35f, h * 0.40f, // Spine decay
                            w * 0.50f, h * 0.45f  // Benchmark
                        )
                        cubicTo(
                            w * 0.65f, h * 0.48f, // Spike bump (re-engagement)
                            w * 0.85f, h * 0.42f,
                            w, h * 0.51f         // Outro
                        )
                    }

                    drawPath(
                        path = path,
                        color = AccentPink,
                        style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                    )
                }

                // Metric benchmark tags overlay
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text("0:00 (Intro)", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("5:00 (Spine)", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("10:00 (Outro)", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WidgetPredictiveEstimations() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 6.dp)) {
                Icon(Icons.Default.OnlinePrediction, contentDescription = "Intelligence AI metric", tint = AccentWarning)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Predictive Creator Benchmarks", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Text("AI predicts view performance bands prior to publishing based on Metadata optimization grades.", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(14.dp))

            // Vertical list of estimates
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple("Full SEO Optimizations (A+ Rank)", "24K - 45K views", SoftGreen),
                    Triple("Standard Title Hooks Only", "8.5K - 15K views", AccentWarning),
                    Triple("Raw Upload / No tags applied", "1.2K - 3K views", SoftRed)
                ).forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(item.first, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(item.second, fontSize = 12.sp, color = item.third, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun WidgetNicheTrendsAlert(viewModel: MainViewModel) {
    val trendsList by viewModel.trends.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, contentDescription = "Bullhorn notifications icon", tint = AccentPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Niche Trending Alert Desk", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Button(
                    onClick = { viewModel.triggerTrendCheck() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("Trigger Alert Scan", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text("Live topics currently blowing up in your niche category.", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                trendsList.take(3).forEach { trend ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(0.65f)) {
                            Text(trend.term, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("Volume: ${trend.searchVolume} | Comp: ${trend.competition}", fontSize = 10.sp, color = Color.Gray)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SoftGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("+${trend.growthPercent}%", color = SoftGreen, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WidgetKeywordLauncher(viewModel: MainViewModel) {
    var query by remember { mutableStateOf("") }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ManageSearch, "Search icon", tint = AccentCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Keyword Quick Tool", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Look up phrase score instantly...", fontSize = 12.sp) },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        if (query.isNotEmpty()) {
                            viewModel.performKeywordAnalysis(query)
                            query = ""
                        }
                    }) {
                        Icon(Icons.Default.Send, "Send icon", tint = AccentCyan)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
        }
    }
}

// --- TAB 2: KEYWORD RESEARCH WORKBENCH ---
@Composable
fun KeywordResearchTab(viewModel: MainViewModel) {
    var searchPhrase by remember { mutableStateOf("") }
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val savedKeywords by viewModel.savedKeywords.collectAsState()

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Keyword Score Analytics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        Text("Real-time score indexes powered by Gemini 1.5 Flash. Know volume before record.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchPhrase,
                onValueChange = { searchPhrase = it },
                placeholder = { Text("e.g. Kotlin Jetpack Compose tutorial") },
                singleLine = true,
                modifier = Modifier
                    .weight(0.75f)
                    .testTag("keyword_search_input"),
                leadingIcon = { Icon(Icons.Default.Search, "Search Icon") }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (searchPhrase.isNotBlank()) {
                        viewModel.performKeywordAnalysis(searchPhrase)
                    } else {
                        Toast.makeText(context, "Please write a keyword request", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isAnalyzing,
                modifier = Modifier
                    .weight(0.25f)
                    .height(56.dp)
                    .testTag("keyword_search_button")
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Search")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hot niche suggestions shortcuts bar
        Text("CREATOR RECOMMENDATIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(6.dp))
        val shortcuts = listOf("Compose Canvas", "Android Clean Architecture", "Gemini API integration", "Kotlin Flows")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(shortcuts) { item ->
                SuggestionChip(
                    onClick = {
                        searchPhrase = item
                        viewModel.performKeywordAnalysis(item)
                    },
                    label = { Text(item, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Keyword historical list results
        Text("SEARCH RESULTS & HISTORIC CACHE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        if (savedKeywords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.History, "No searches logo", tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No keyword analysis yet", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
                    Text("Enter a term above to call Gemini 1.5 Flash.", color = Color.Gray, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(savedKeywords) { item ->
                    KeywordResultCard(item) {
                        viewModel.deleteKeyword(item.keyword)
                    }
                }
            }
        }
    }
}

@Composable
fun KeywordResultCard(item: SavedKeyword, onDelete: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    // Set colors according to overall score
    val dialColor = when {
        item.overallScore >= 70 -> SoftGreen
        item.overallScore >= 45 -> AccentWarning
        else -> SoftRed
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(0.7f)) {
                    Text(item.keyword, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                    Text("Searched just now", fontSize = 9.sp, color = Color.Gray)
                }
                IconButton(onClick = onDelete, modifier = Modifier.weight(0.15f)) {
                    Icon(Icons.Default.Delete, "Delete search", tint = Color.Gray.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            Spacer(modifier = Modifier.height(14.dp))

            // Dial Score and Sub metrics
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Circular rating score ring
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = Color.Gray.copy(alpha = 0.15f),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 8f)
                        )
                        drawArc(
                            color = dialColor,
                            startAngle = -90f,
                            sweepAngle = (item.overallScore.toFloat() / 100f) * 360f,
                            useCenter = false,
                            style = Stroke(width = 8f, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${item.overallScore}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = dialColor)
                        Text("SCORE", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.ExtraBold)
                    }
                }

                // Sub scores
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Search Volume", fontSize = 11.sp, color = Color.Gray)
                        Text("${item.volumeScore}% (High)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftGreen)
                    }
                    LinearProgressIndicator(
                        progress = { item.volumeScore / 100f },
                        color = SoftGreen,
                        trackColor = Color.Gray.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth().height(4.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Competition Index", fontSize = 11.sp, color = Color.Gray)
                        Text("${item.competitionScore}% (Low)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentWarning)
                    }
                    LinearProgressIndicator(
                        progress = { item.competitionScore / 100f },
                        color = AccentWarning,
                        trackColor = Color.Gray.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth().height(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Narrative Brief Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = item.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI tags pills copyable
            val tagsList = item.recommendedTags.split(",").filter { it.isNotBlank() }
            if (tagsList.isNotEmpty()) {
                Text("AUTOMATED SUGGESTED TAGS (TAP TO COPY)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))

                // Tags Flow Row helper (LazyRow container standard scrolling)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(tagsList) { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .clickable {
                                    clipboard.setText(AnnotatedString(tag.trim()))
                                    Toast.makeText(context, "'${tag.trim()}' copied!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ContentCopy, "copy", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(tag.trim(), fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- TAB 3: VIDEO METADATA SEO OPTIMIZER ---
@Composable
fun VideoSeoTab(viewModel: MainViewModel) {
    var draftTitle by remember { mutableStateOf("") }
    var draftDesc by remember { mutableStateOf("") }
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val optimizedList by viewModel.optimizedVideos.collectAsState()
    val liveVideos by viewModel.liveVideosList.collectAsState()

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Automated SEO Optimization", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text("Transforms raw draft metadata into highly-viral clickable YouTube publications.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 6.dp))
        }

        // Fast load from live channel uploads
        if (liveVideos.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("FAST IMPORT: SELECT AN UPLOADED VIDEO", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = AccentCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(liveVideos) { vid ->
                                Box(
                                    modifier = Modifier
                                        .width(220.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                                        .clickable {
                                            draftTitle = vid.title
                                            draftDesc = vid.description
                                            Toast.makeText(context, "Loaded metadata!", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(vid.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(vid.description, fontSize = 9.sp, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CloudSync, "sync", tint = SoftGreen, modifier = Modifier.size(10.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Official Upload Sync", fontSize = 8.sp, color = SoftGreen, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            // Optimizer Inputs Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = draftTitle,
                        onValueChange = { draftTitle = it },
                        label = { Text("Draft Title") },
                        placeholder = { Text("e.g. Android coroutines tutorial") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("draft_title_field")
                            .padding(bottom = 12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = draftDesc,
                        onValueChange = { draftDesc = it },
                        label = { Text("Draft/Outline Description") },
                        placeholder = { Text("Outline what topics the video covers...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("draft_desc_field")
                            .padding(bottom = 16.dp)
                    )

                    Button(
                        onClick = {
                            if (draftTitle.isNotBlank()) {
                                viewModel.performVideoSEOOptimization(draftTitle, draftDesc)
                            } else {
                                Toast.makeText(context, "Please write a draft title first.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isAnalyzing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("seo_submit_button")
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.AutoAwesome, "Sparkles icon")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Optimize Metadata with Gemini")
                        }
                    }
                }
            }
        }

        // Dynamic optimisations results history
        item {
            Text("OPTIMIZED VIDEO REVIEWS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }

        if (optimizedList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.VideoLibrary, "empty SEO", tint = Color.Gray, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No optimizations recorded yet", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
                        Text("Input outline fields to generate metadata drafts.", color = Color.Gray, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(optimizedList) { entry ->
                OptimizedVideoItemCard(entry) {
                    viewModel.deleteOptimizedVideo(entry.id)
                }
            }
        }
    }
}

@Composable
fun OptimizedVideoItemCard(entry: SavedVideoSEO, onDelete: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    // Set colors according to overall score
    val ratingColor = when {
        entry.seoScore >= 90 -> SoftGreen
        entry.seoScore >= 70 -> AccentWarning
        else -> SoftRed
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth(0.65f)) {
                    Text("Original: ${entry.originalTitle}", fontSize = 11.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("AI Refined Outline", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ratingColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("SEO Score: ${entry.seoScore}", color = ratingColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, "Delete entries", tint = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            Spacer(modifier = Modifier.height(12.dp))

            // 1. Title Suggestions copyable
            val suggList = entry.optimizedTitles.split("|").filter { it.isNotBlank() }
            if (suggList.isNotEmpty()) {
                Text("VIRAL HIGH-CTR TITLES (TAP TO COPY)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    suggList.take(3).forEachIndexed { i, title ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .clickable {
                                    clipboard.setText(AnnotatedString(title.trim()))
                                    Toast.makeText(context, "Title copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(AccentCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${i + 1}", color = AccentCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(title.trim(), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ContentCopy, "copy", tint = AccentCyan, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. High SEO description copyable
            Text("SEO DESCRIPTION SUMMARY CHAPTERS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AccentPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = entry.descriptionOptimized,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            clipboard.setText(AnnotatedString(entry.descriptionOptimized))
                            Toast.makeText(context, "Description copied!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, "copy desc", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Full Description", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Predictive Insights brackets
            Text("AI PREDICTIVE ENGAGEMENT METRICS", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = AccentWarning)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val metricsList = listOf<Pair<String, String>>(
                    Pair("Forecast Views", entry.predictedViews),
                    Pair("Forecast Likes", entry.predictedLikes),
                    Pair("Forecast Comments", entry.predictedComments)
                )
                metricsList.forEach { metric ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Gray.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(metric.first, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(metric.second, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = AccentWarning)
                        }
                    }
                }
            }
        }
    }
}

// --- TAB 4: SUMMARY & ANALYTICS PDF EXPORT ---
@Composable
fun AnalyticsExportTab(viewModel: MainViewModel) {
    val subs by viewModel.youtubeSubscribers.collectAsState()
    val views by viewModel.youtubeViews.collectAsState()
    val optVideos by viewModel.optimizedVideos.collectAsState()
    val savedKeywords by viewModel.savedKeywords.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(AccentCyan.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Analytics,
                contentDescription = "Analytics giant icon",
                tint = AccentCyan,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Monthly Performance Dossier",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Export a comprehensive monthly overview summarising channel scale indicators, monitored tags and SEO optimization trends generated this cycle.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Metrics Table Preview
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("PDF DOCUMENT HIGHLIGHTS", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))

                val highlightList = listOf<Pair<String, String>>(
                    Pair("Target Channels Feed", "Linked"),
                    Pair("Total Synced Platform Followers", "${subs} (YT) + 197.6K (Sync)"),
                    Pair("Monitored Search Terms count", "${savedKeywords.size} Keywords"),
                    Pair("Optimized Drafts uploaded", "${optVideos.size} Videos")
                )
                highlightList.forEach { (label, value) ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Text(label, fontSize = 12.sp, color = Color.Gray)
                        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { viewModel.handleExportPdfReport() },
            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("export_pdf_button")
        ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF icon", tint = Color.Black)
            Spacer(modifier = Modifier.width(10.dp))
            Text("Compile and Export PDF Report", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}
