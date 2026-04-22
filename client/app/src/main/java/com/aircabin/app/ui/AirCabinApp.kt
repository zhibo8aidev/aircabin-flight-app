package com.aircabin.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.InsertChartOutlined
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aircabin.app.data.AirCabinRepository
import com.aircabin.app.data.ChatSummary
import com.aircabin.app.data.ConnectivityState
import com.aircabin.app.data.CurrentFlightUiModel
import com.aircabin.app.data.FlightSyncMode
import com.aircabin.app.data.GallerySummary
import com.aircabin.app.data.ImportDraft
import com.aircabin.app.data.LoadState
import com.aircabin.app.data.ProfileSummary
import com.aircabin.app.data.StatSummary
import com.aircabin.app.ui.theme.AccentDeep
import com.aircabin.app.ui.theme.AccentGold
import com.aircabin.app.ui.theme.AccentGoldMuted
import com.aircabin.app.ui.theme.AirCabinTheme
import com.aircabin.app.ui.theme.BackgroundBase
import com.aircabin.app.ui.theme.BackgroundGlow
import com.aircabin.app.ui.theme.BackgroundShade
import com.aircabin.app.ui.theme.OfflineTint
import com.aircabin.app.ui.theme.RiskTint
import com.aircabin.app.ui.theme.SurfacePrimary
import com.aircabin.app.ui.theme.SurfaceSecondary
import com.aircabin.app.ui.theme.TextMuted
import com.aircabin.app.ui.theme.TextPrimary
import com.aircabin.app.ui.theme.TextSecondary

private val repo = AirCabinRepository()

private enum class AppRoute(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val inBottomBar: Boolean = true,
) {
    Home("home", "首页", Icons.Outlined.Home),
    Import("import", "导入", Icons.Outlined.AddCircle, inBottomBar = false),
    Flight("flight", "航班", Icons.Outlined.Flight),
    Stats("stats", "统计", Icons.Outlined.InsertChartOutlined),
    Gallery("gallery", "相册", Icons.Outlined.PhotoLibrary),
    Chat("chat", "聊天室", Icons.Outlined.ChatBubbleOutline, inBottomBar = false),
    Profile("profile", "我的", Icons.Outlined.AccountCircle),
}

@Composable
fun AirCabinApp() {
    AirCabinTheme {
        val navController = rememberNavController()
        val bottomItems = AppRoute.entries.filter { it.inBottomBar }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BackgroundGlow, BackgroundBase, BackgroundShade),
                    ),
                ),
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets.safeDrawing,
                bottomBar = {
                    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
                    NavigationBar(
                        containerColor = SurfacePrimary,
                        tonalElevation = 0.dp,
                    ) {
                        bottomItems.forEach { item ->
                            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AccentDeep,
                                    selectedTextColor = AccentDeep,
                                    indicatorColor = AccentGoldMuted,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                ),
                            )
                        }
                    }
                },
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = AppRoute.Home.route,
                    modifier = Modifier.padding(innerPadding),
                ) {
                    composable(AppRoute.Home.route) {
                        HomeScreen(
                            flightState = repo.flightState,
                            onImport = { navController.navigate(AppRoute.Import.route) },
                            onFlightDetail = { navController.navigate(AppRoute.Flight.route) },
                            onGallery = { navController.navigate(AppRoute.Gallery.route) },
                            onChat = { navController.navigate(AppRoute.Chat.route) },
                        )
                    }
                    composable(AppRoute.Import.route) {
                        ImportScreen(
                            draft = repo.importDraft,
                            onBack = { navController.popBackStack() },
                            onDraftChange = repo::updateDraft,
                            onConfirm = {
                                repo.confirmImport()
                                navController.navigate(AppRoute.Home.route) {
                                    popUpTo(AppRoute.Home.route) { inclusive = false }
                                }
                            },
                        )
                    }
                    composable(AppRoute.Flight.route) {
                        FlightScreen(
                            state = repo.flightState,
                            onBack = { navController.popBackStack() },
                            onModeChange = repo::useMode,
                            onChat = { navController.navigate(AppRoute.Chat.route) },
                            onStats = { navController.navigate(AppRoute.Stats.route) },
                        )
                    }
                    composable(AppRoute.Stats.route) {
                        StatsScreen(
                            state = repo.statsState,
                            flightState = repo.flightState,
                            onSimulateError = repo::simulateStatsError,
                            onRestore = repo::restoreStats,
                        )
                    }
                    composable(AppRoute.Gallery.route) {
                        GalleryScreen(state = repo.galleryState)
                    }
                    composable(AppRoute.Chat.route) {
                        ChatScreen(
                            state = repo.chatState,
                            flightState = repo.flightState,
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable(AppRoute.Profile.route) {
                        ProfileScreen(
                            state = repo.profileState,
                            onClear = repo::clearCurrentFlight,
                            onRestore = repo::restoreCurrentFlight,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(
    flightState: LoadState<CurrentFlightUiModel>,
    onImport: () -> Unit,
    onFlightDetail: () -> Unit,
    onGallery: () -> Unit,
    onChat: () -> Unit,
) {
    ScreenColumn {
        TopHeader(
            eyebrow = "Today · Apr 22",
            title = "云舱",
            trailing = {
                IconButton(onClick = {}) {
                    Icon(Icons.Outlined.Settings, contentDescription = "设置")
                }
            },
        )
        when (flightState) {
            LoadState.Loading -> StatusCard("正在准备当前航班", "加载本地缓存与飞行进度。")
            is LoadState.Empty -> EmptyStateCard(flightState.title, flightState.body, "导入登机信息", onImport)
            is LoadState.Error -> ErrorStateCard(flightState.title, flightState.body, "重试", onImport)
            is LoadState.Success -> FlightHeroCard(
                flight = flightState.value,
                onAction = onFlightDetail,
            )
        }
        ShortcutGrid(
            items = listOf(
                ShortcutItem("登机导入", "OCR / 手动", Icons.Outlined.AddCircle, onImport),
                ShortcutItem("本次飞行", "进入航班页", Icons.Outlined.Flight, onFlightDetail),
                ShortcutItem("相册整理", "仅在本机处理", Icons.Outlined.DeleteSweep, onGallery),
                ShortcutItem("同舱聊天", "网络增强", Icons.Outlined.ChatBubbleOutline, onChat),
            ),
        )
        SummaryCard()
    }
}

@Composable
private fun ImportScreen(
    draft: ImportDraft,
    onBack: () -> Unit,
    onDraftChange: (ImportDraft) -> Unit,
    onConfirm: () -> Unit,
) {
    var localDraft by rememberSaveable { mutableStateOf(draft) }
    ScreenColumn {
        TopHeader(
            eyebrow = "Flight Import",
            title = "导入登机信息",
            leading = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回")
                }
            },
            subtitle = "先确认关键字段，再生成当前航班卡片。",
        )
        SectionCard {
            Text("导入方式", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            ChipRow(labels = listOf("拍照识别", "截图识别", "手动录入"), selected = "手动录入")
            Spacer(Modifier.height(16.dp))
            Text("低置信字段已高亮，需要人工确认。", color = OfflineTint, style = MaterialTheme.typography.bodySmall)
        }
        SectionCard {
            Text("字段确认", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            DraftField("航班号", localDraft.flightNo) { localDraft = localDraft.copy(flightNo = it) }
            DraftField("出发机场", localDraft.departureCode) { localDraft = localDraft.copy(departureCode = it) }
            DraftField("到达机场", localDraft.arrivalCode) { localDraft = localDraft.copy(arrivalCode = it) }
            DraftField("出发城市", localDraft.departureCity) { localDraft = localDraft.copy(departureCity = it) }
            DraftField("到达城市", localDraft.arrivalCity) { localDraft = localDraft.copy(arrivalCity = it) }
            DraftField("座位号", localDraft.seatNo) { localDraft = localDraft.copy(seatNo = it) }
            DraftField("舱位", localDraft.cabin) { localDraft = localDraft.copy(cabin = it) }
        }
        Button(
            onClick = {
                onDraftChange(localDraft)
                onConfirm()
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
        ) {
            Text("确认导入并生成航班卡片")
        }
    }
}

@Composable
private fun FlightScreen(
    state: LoadState<CurrentFlightUiModel>,
    onBack: () -> Unit,
    onModeChange: (FlightSyncMode) -> Unit,
    onChat: () -> Unit,
    onStats: () -> Unit,
) {
    ScreenColumn {
        TopHeader(
            eyebrow = "Current Flight",
            title = "当前航班",
            leading = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回")
                }
            },
            trailing = {
                IconButton(onClick = {}) {
                    Icon(Icons.Outlined.MoreHoriz, contentDescription = "更多")
                }
            },
        )
        when (state) {
            LoadState.Loading -> StatusCard("正在恢复航班轨迹", "优先展示离线缓存。")
            is LoadState.Empty -> EmptyStateCard(state.title, state.body, "去导入", onBack)
            is LoadState.Error -> ErrorStateCard(state.title, state.body, "返回", onBack)
            is LoadState.Success -> FlightDetailContent(
                flight = state.value,
                onModeChange = onModeChange,
                onChat = onChat,
                onStats = onStats,
            )
        }
    }
}

@Composable
private fun FlightDetailContent(
    flight: CurrentFlightUiModel,
    onModeChange: (FlightSyncMode) -> Unit,
    onChat: () -> Unit,
    onStats: () -> Unit,
) {
    SectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ModeBadge(flight.mode)
                Text(
                    text = when (flight.connectivity) {
                        ConnectivityState.Online -> "机上网络可用"
                        ConnectivityState.Weak -> "弱网运行中"
                        ConnectivityState.Offline -> "离线延续最近修正"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
            ChipRow(
                labels = FlightSyncMode.entries.map { it.label },
                selected = flight.mode.label,
                onSelect = { label ->
                    FlightSyncMode.entries.firstOrNull { it.label == label }?.let(onModeChange)
                },
            )
        }
    }
    MapCard(flight)
    SectionCard {
        SectionTitle("飞行状态", chip = flight.phase.label)
        MetricsGrid(
            items = listOf(
                "速度" to flight.speed,
                "海拔" to flight.altitude,
                "剩余航程" to flight.distanceLeft,
                "到达" to flight.eta,
            ),
            columns = 2,
        )
    }
    SectionCard {
        SectionTitle("时间进度", chip = flight.mode.sourceChip)
        MetricsGrid(
            items = listOf(
                "总时长" to flight.total,
                "已飞" to flight.elapsed,
                "剩余" to flight.remaining,
            ),
            columns = 3,
        )
        Spacer(Modifier.height(14.dp))
        ProgressBar(flight.progress)
    }
    ActionRow("进入聊天室", onChat)
    ActionRow("查看飞行统计", onStats)
    ActionRow("登机信息已脱敏", {})
}

@Composable
private fun StatsScreen(
    state: LoadState<StatSummary>,
    flightState: LoadState<CurrentFlightUiModel>,
    onSimulateError: () -> Unit,
    onRestore: () -> Unit,
) {
    ScreenColumn {
        TopHeader(
            eyebrow = "Flight Archive",
            title = "飞行统计",
            trailing = {
                AssistChip(onClick = {}, label = { Text("离线回退") })
            },
        )
        ChipRow(labels = listOf("本次", "月度", "季度", "全年"), selected = "本次")
        when (state) {
            LoadState.Loading -> StatusCard("正在聚合统计数据", "优先显示本地缓存。")
            is LoadState.Empty -> EmptyStateCard(state.title, state.body, "恢复样例", onRestore)
            is LoadState.Error -> ErrorStateCard(state.title, state.body, "恢复缓存", onRestore)
            is LoadState.Success -> {
                SectionCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("总飞行时长", color = TextSecondary)
                            Text(state.value.totalHours, style = MaterialTheme.typography.displaySmall)
                            Text(state.value.cachedAt, color = TextMuted, style = MaterialTheme.typography.bodySmall)
                        }
                        AssistChip(onClick = onSimulateError, label = { Text("模拟错误态") })
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SmallMetricCard("飞行次数", state.value.totalFlights, "国内 31 / 国际 11", Modifier.weight(1f))
                    SmallMetricCard("总里程", state.value.totalDistance, "CA / MU", Modifier.weight(1f))
                }
                ChartSkeletonCard("年度趋势", state.value.cachedAt)
                ChartSkeletonCard("航司分布", "Offline cache")
                SectionCard(containerColor = AccentGoldMuted) {
                    Text("年度飞行报告", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("保留导出入口骨架，待接入实际图片导出与分享能力。", color = TextSecondary)
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = {}) {
                        Text("生成报告卡片")
                    }
                }
                if (flightState is LoadState.Success) {
                    SectionCard {
                        SectionTitle("本次飞行", chip = flightState.value.mode.label)
                        MetricsGrid(
                            items = listOf(
                                "已飞" to flightState.value.elapsed,
                                "剩余" to flightState.value.remaining,
                                "总时长" to flightState.value.total,
                            ),
                            columns = 3,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryScreen(state: LoadState<GallerySummary>) {
    ScreenColumn {
        TopHeader(
            eyebrow = "Local Media",
            title = "相册整理",
            trailing = {
                AssistChip(onClick = {}, label = { Text("仅在本机处理") })
            },
        )
        when (state) {
            LoadState.Loading -> StatusCard("正在扫描本地媒体", "首次扫描完成后可离线查看分类。")
            is LoadState.Empty -> EmptyStateCard(state.title, state.body, "等待权限", {})
            is LoadState.Error -> ErrorStateCard(state.title, state.body, "稍后重试", {})
            is LoadState.Success -> {
                SectionCard {
                    Text("候选空间", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(state.value.reclaimableSpace, style = MaterialTheme.typography.displaySmall)
                    Text("${state.value.candidateCount} 可审阅", color = TextSecondary)
                }
                ChipRow(labels = state.value.categories, selected = "截图")
                SectionCard {
                    SectionTitle("整理网格", chip = "3 列")
                    GalleryGridPlaceholder()
                }
                SectionCard(containerColor = SurfaceSecondary) {
                    Text("删除前预览与二次确认已预留。", color = TextSecondary)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = {}, modifier = Modifier.weight(1f).height(54.dp)) {
                            Text("预览候选")
                        }
                        Button(onClick = {}, modifier = Modifier.weight(1f).height(54.dp)) {
                            Text("批量删除")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatScreen(
    state: LoadState<ChatSummary>,
    flightState: LoadState<CurrentFlightUiModel>,
    onBack: () -> Unit,
) {
    ScreenColumn {
        TopHeader(
            eyebrow = "Cabin Room",
            title = "聊天室",
            leading = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回")
                }
            },
        )
        when (state) {
            LoadState.Loading -> StatusCard("正在连接房间", "弱网下保留草稿，离线不可进入。")
            is LoadState.Empty -> EmptyStateCard(state.title, state.body, "返回", onBack)
            is LoadState.Error -> ErrorStateCard(state.title, state.body, "返回", onBack)
            is LoadState.Success -> {
                val mode = (flightState as? LoadState.Success)?.value?.mode ?: FlightSyncMode.OfflineFallback
                SectionCard {
                    SectionTitle(state.value.roomLabel, chip = state.value.participants)
                    Spacer(Modifier.height(8.dp))
                    Text(state.value.roomNotice, color = TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    ModeBadge(mode)
                }
                SectionCard {
                    SectionTitle("话题", chip = "封闭房间")
                    ChipRow(labels = listOf("#拼车", "#转机", "#目的地建议"), selected = "#拼车")
                }
                SectionCard {
                    SectionTitle("消息列表", chip = "弱网提示")
                    ChatBubble("CloudSeat-21", "落地后一起拼车吗？", own = false)
                    ChatBubble("我", "可以，先看网络状态。", own = true)
                    Text(state.value.networkHint, color = OfflineTint, style = MaterialTheme.typography.bodySmall)
                }
                SectionCard {
                    OutlinedTextField(
                        value = "离线时保留草稿",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        label = { Text("输入区骨架") },
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(
    state: LoadState<ProfileSummary>,
    onClear: () -> Unit,
    onRestore: () -> Unit,
) {
    ScreenColumn {
        TopHeader(
            eyebrow = "Profile",
            title = "我的",
            trailing = {
                IconButton(onClick = {}) {
                    Icon(Icons.Outlined.Sync, contentDescription = "同步")
                }
            },
        )
        when (state) {
            LoadState.Loading -> StatusCard("正在读取档案", "优先展示本地记录。")
            is LoadState.Empty -> EmptyStateCard(state.title, state.body, "恢复", onRestore)
            is LoadState.Error -> ErrorStateCard(state.title, state.body, "恢复", onRestore)
            is LoadState.Success -> {
                SectionCard {
                    Text(state.value.travelerName, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(6.dp))
                    Text(state.value.yearlyFlights, color = TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    AssistChip(onClick = {}, label = { Text(state.value.preferenceTag) })
                }
                SectionCard {
                    SettingRow("飞行档案", "42 条")
                    Divider(color = Color.Transparent, thickness = 10.dp)
                    SettingRow("权限与隐私", "On")
                    Divider(color = Color.Transparent, thickness = 10.dp)
                    SettingRow("同步策略", "Wi-Fi only")
                }
                SectionCard {
                    Text("风险操作", style = MaterialTheme.typography.titleMedium, color = RiskTint)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = onRestore, modifier = Modifier.weight(1f).height(54.dp)) {
                            Text("恢复样例")
                        }
                        Button(onClick = onClear, modifier = Modifier.weight(1f).height(54.dp)) {
                            Text("清空当前航班")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenColumn(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 24.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        content = { content() },
    )
}

@Composable
private fun TopHeader(
    eyebrow: String,
    title: String,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.Top) {
            if (leading != null) {
                leading()
                Spacer(Modifier.size(4.dp))
            }
            Column {
                Text(eyebrow, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                Text(title, style = MaterialTheme.typography.displaySmall)
                if (subtitle != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        }
        if (trailing != null) trailing()
    }
}

@Composable
private fun FlightHeroCard(
    flight: CurrentFlightUiModel,
    onAction: () -> Unit,
) {
    SectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("${flight.record.airline} ${flight.record.flightNo}", color = TextSecondary)
            StatusChip(flight.mode.label, chipColor(flight.mode))
        }
        Spacer(Modifier.height(14.dp))
        Text(
            "${flight.record.departureCity} ${flight.record.departureCode} → ${flight.record.arrivalCity} ${flight.record.arrivalCode}",
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CaptionTag(flight.mode.caption)
            SourceChip(flight.mode.sourceChip)
        }
        Spacer(Modifier.height(16.dp))
        RouteStrip(progress = flight.progress, mode = flight.mode)
        Spacer(Modifier.height(18.dp))
        MetricsGrid(
            items = listOf(
                "阶段" to flight.phase.label,
                "剩余" to flight.remaining,
                "到达" to flight.eta,
            ),
            columns = 3,
        )
        Spacer(Modifier.height(18.dp))
        Button(onClick = onAction, modifier = Modifier.fillMaxWidth().height(54.dp)) {
            Text("查看航班详情")
        }
    }
}

@Composable
private fun SummaryCard() {
    SectionCard {
        Text("2026 摘要", color = TextSecondary)
        Spacer(Modifier.height(6.dp))
        Text("首屏只保留最有用的信息", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        MetricsGrid(items = listOf("飞行次数" to "42", "总时长" to "186h"), columns = 2)
        Spacer(Modifier.height(10.dp))
        Text("北京 ↔ 深圳为常见航线。历史档案下沉到详情列表。", color = TextSecondary)
    }
}

@Composable
private fun MapCard(flight: CurrentFlightUiModel) {
    SectionCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFF7F8FA), Color(0xFFEDEFF3)),
                    ),
                ),
                .padding(20.dp),
        ) {
            RouteStrip(
                progress = flight.progress,
                mode = flight.mode,
                modifier = Modifier.align(Alignment.Center),
            )
            Text(
                "Reference only",
                modifier = Modifier.align(Alignment.BottomEnd),
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
            )
        }
    }
}

@Composable
private fun RouteStrip(
    progress: Float,
    mode: FlightSyncMode,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(lineColor(mode).copy(alpha = 0.35f)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(12.dp)
                .clip(CircleShape)
                .background(TextPrimary),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(12.dp)
                .clip(CircleShape)
                .background(TextPrimary),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = (progress * 260).dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(AccentGold),
        )
    }
}

@Composable
private fun ShortcutGrid(items: List<ShortcutItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { item ->
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = SurfaceSecondary),
                        shape = RoundedCornerShape(24.dp),
                        onClick = item.onClick,
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(item.icon, contentDescription = item.title, tint = AccentDeep)
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            Text(item.caption, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    containerColor: Color = SurfacePrimary,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(30.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            content = content,
        )
    }
}

@Composable
private fun SectionTitle(title: String, chip: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        SourceChip(chip)
    }
}

@Composable
private fun StatusCard(title: String, body: String) {
    SectionCard {
        Icon(Icons.Outlined.GridView, contentDescription = null, tint = AccentDeep)
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Text(body, color = TextSecondary)
    }
}

@Composable
private fun EmptyStateCard(title: String, body: String, action: String, onClick: () -> Unit) {
    SectionCard {
        Icon(Icons.Outlined.AddCircle, contentDescription = null, tint = AccentDeep)
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Text(body, color = TextSecondary)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onClick) {
            Text(action)
        }
    }
}

@Composable
private fun ErrorStateCard(title: String, body: String, action: String, onClick: () -> Unit) {
    SectionCard {
        Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = RiskTint)
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Text(body, color = TextSecondary)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onClick) {
            Text(action)
        }
    }
}

@Composable
private fun DraftField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        label = { Text(label) },
        singleLine = true,
    )
}

@Composable
private fun ChipRow(
    labels: List<String>,
    selected: String,
    onSelect: ((String) -> Unit)? = null,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        labels.forEach { label ->
            AssistChip(
                onClick = { onSelect?.invoke(label) },
                label = { Text(label) },
                colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                    containerColor = if (label == selected) AccentGoldMuted else SurfaceSecondary,
                    labelColor = TextPrimary,
                ),
            )
        }
    }
}

@Composable
private fun ModeBadge(mode: FlightSyncMode) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        StatusChip(mode.label, chipColor(mode))
        CaptionTag(mode.caption)
        SourceChip(mode.sourceChip)
    }
}

@Composable
private fun StatusChip(label: String, background: Color) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(background)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(label, color = TextPrimary, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun CaptionTag(label: String) {
    Text(label, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun SourceChip(label: String) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(SurfaceSecondary)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(label, color = TextPrimary, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun MetricsGrid(
    items: List<Pair<String, String>>,
    columns: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        items.chunked(columns).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { (label, value) ->
                    Column(modifier = Modifier.weight(1f)) {
                        Text(label, color = TextMuted, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(4.dp))
                        Text(value, style = MaterialTheme.typography.titleMedium)
                    }
                }
                repeat(columns - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(CircleShape)
            .background(SurfaceSecondary),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(10.dp)
                .clip(CircleShape)
                .background(AccentGold),
        )
    }
}

@Composable
private fun ActionRow(label: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label)
            Icon(Icons.Outlined.MoreHoriz, contentDescription = null, tint = TextMuted)
        }
    }
}

@Composable
private fun SmallMetricCard(title: String, value: String, caption: String, modifier: Modifier = Modifier) {
    SectionCard(containerColor = SurfacePrimary) {
        Text(title, color = TextSecondary)
        Spacer(Modifier.height(8.dp))
        Text(value, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(6.dp))
        Text(caption, color = TextMuted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ChartSkeletonCard(title: String, chip: String) {
    SectionCard {
        SectionTitle(title, chip)
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            listOf(0.32f, 0.58f, 0.44f, 0.76f, 0.52f, 0.83f).forEach { ratio ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .fillMaxSize(ratio)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentGoldMuted),
                )
            }
        }
    }
}

@Composable
private fun GalleryGridPlaceholder() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(92.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (index == 1) AccentGoldMuted else SurfaceSecondary),
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(author: String, text: String, own: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (own) Alignment.End else Alignment.Start,
    ) {
        Text(author, color = TextMuted, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(4.dp))
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (own) AccentGoldMuted else SurfaceSecondary,
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun SettingRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title)
        Text(value, color = TextSecondary)
    }
}

private data class ShortcutItem(
    val title: String,
    val caption: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

private fun chipColor(mode: FlightSyncMode): Color = when (mode) {
    FlightSyncMode.Simulated -> AccentGoldMuted
    FlightSyncMode.LiveAdjusted -> Color(0x1A262626)
    FlightSyncMode.OfflineFallback -> Color(0x24B96A3C)
}

private fun lineColor(mode: FlightSyncMode): Color = when (mode) {
    FlightSyncMode.Simulated -> AccentGold
    FlightSyncMode.LiveAdjusted -> AccentDeep
    FlightSyncMode.OfflineFallback -> OfflineTint
}
