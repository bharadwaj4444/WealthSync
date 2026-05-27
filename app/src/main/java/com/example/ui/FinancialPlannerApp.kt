package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.FinancialGoal
import com.example.data.Holding
import com.example.data.MonthlySnapshot
import com.example.ui.theme.*
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialPlannerApp(viewModel: MainViewModel) {
    var activeTab by remember { mutableStateOf(0) }
    
    // Core database collections
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val holdings by viewModel.holdings.collectAsStateWithLifecycle()
    val snapshots by viewModel.snapshots.collectAsStateWithLifecycle()
    val targetRiskProfile by viewModel.targetRiskProfile.collectAsStateWithLifecycle()
    
    // UI Dialog flows
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAddHoldingDialog by remember { mutableStateOf(false) }
    var showAddSnapshotDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var userPromptText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountBalance,
                                contentDescription = "WealthSync Logo",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            text = "WealthSync",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.fetchFinancialPlanningAdvice("Optimize my portfolio allocation and audit active savings.") },
                        modifier = Modifier
                            .testTag("ai_audit_button")
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Spa,
                            contentDescription = "AI Audit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { showResetConfirmDialog = true },
                        modifier = Modifier
                            .testTag("clear_all_button")
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteForever,
                            contentDescription = "Clear All Data",
                            tint = CoralAlert,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .navigationBarsPadding()
                    .drawWithContent {
                        drawContent()
                        drawLine(
                            color = GeometricCardBorder,
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Filled.Flag, "Milestones") },
                    label = { Text("Milestones", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Filled.PieChart, "Portfolio") },
                    label = { Text("Portfolio", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Filled.TrendingUp, "Simulator") },
                    label = { Text("Retirement/Tax", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Icon(Icons.Filled.Analytics, "AI Advisor") },
                    label = { Text("AI Advisor", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        },
        floatingActionButton = {
            when (activeTab) {
                0 -> {
                    ExtendedFloatingActionButton(
                        onClick = { showAddGoalDialog = true },
                        icon = { Icon(Icons.Filled.Add, "Goal") },
                        text = { Text("New Goal") },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.testTag("add_goal_fab")
                    )
                }
                1 -> {
                    ExtendedFloatingActionButton(
                        onClick = { showAddHoldingDialog = true },
                        icon = { Icon(Icons.Filled.Add, "Asset") },
                        text = { Text("New Asset") },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.testTag("add_asset_fab")
                    )
                }
                3 -> {
                    ExtendedFloatingActionButton(
                        onClick = { showAddSnapshotDialog = true },
                        icon = { Icon(Icons.Filled.Add, "Snapshot") },
                        text = { Text("Log Snapshot") },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.testTag("add_snapshot_fab")
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (activeTab) {
                0 -> GoalsScreen(
                    goals = goals,
                    onUpdateSaved = { goal, updatedSaved -> viewModel.updateGoal(goal.copy(currentSaved = updatedSaved)) },
                    onDelete = { id -> viewModel.deleteGoal(id) },
                    onUpdateGoal = { updatedGoal -> viewModel.updateGoal(updatedGoal) }
                )
                1 -> PortfolioScreen(
                    holdings = holdings,
                    targetRiskProfile = targetRiskProfile,
                    onRiskProfileChange = { viewModel.setTargetRiskProfile(it) },
                    onDeleteHolding = { id -> viewModel.deleteHolding(id) },
                    onAddTemplateHolding = { assetClass, name, value -> viewModel.addHolding(assetClass, name, value) }
                )
                2 -> RetirementAndTaxScreen(
                    currentHoldings = holdings,
                    viewModel = viewModel,
                    onAddSuggestedAsset = { assetClass, name, value -> viewModel.addHolding(assetClass, name, value) }
                )
                3 -> AIAdvisorAndHistoryScreen(
                    viewModel = viewModel,
                    snapshots = snapshots,
                    onDeleteSnapshot = { id -> viewModel.deleteSnapshot(id) }
                )
            }

            // --- Dialog Overlays ---
            if (showAddGoalDialog) {
                AddGoalDialog(
                    onDismiss = { showAddGoalDialog = false },
                    onSave = { name, amt, cur, year, month, risk ->
                        viewModel.addGoal(name, amt, cur, year, month, risk)
                        showAddGoalDialog = false
                    }
                )
            }

            if (showAddHoldingDialog) {
                AddHoldingDialog(
                    onDismiss = { showAddHoldingDialog = false },
                    onSave = { category, name, valAmt ->
                        viewModel.addHolding(category, name, valAmt)
                        showAddHoldingDialog = false
                    }
                )
            }

            if (showAddSnapshotDialog) {
                AddSnapshotDialog(
                    onDismiss = { showAddSnapshotDialog = false },
                    onSave = { monYr, nw, rate ->
                        viewModel.addSnapshot(monYr, nw, rate)
                        showAddSnapshotDialog = false
                    }
                )
            }

            if (showResetConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showResetConfirmDialog = false },
                    title = { Text("Wipe All Wealth Data?", color = PlatinumWhite) },
                    text = { Text("This will permanently clear all milestones, portfolio holdings and historical entries from local memory. Do you want to proceed and start with a completely clean slate?", color = SilverSlate) },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.clearAllData()
                                showResetConfirmDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CoralAlert)
                        ) {
                            Text("Erase Everything", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showResetConfirmDialog = false }) {
                            Text("Cancel", color = PlatinumWhite)
                        }
                    }
                )
            }
        }
    }
}

// ==================== 1. Milestones Screen ====================
@Composable
fun GoalsScreen(
    goals: List<FinancialGoal>,
    onUpdateSaved: (FinancialGoal, Double) -> Unit,
    onDelete: (Int) -> Unit,
    onUpdateGoal: (FinancialGoal) -> Unit
) {
    var editingGoal by remember { mutableStateOf<FinancialGoal?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = "Financial Milestones",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    text = "Track targets and fund your dynamic milestones sequentially.",
                    color = SilverSlate,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (goals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Flag,
                            contentDescription = "Empty",
                            tint = SilverSlate.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No financial milestones compiled yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = SilverSlate
                        )
                        Text(
                            text = "Tap 'New Goal' below to initialize one.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SilverSlate.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        } else {
            items(goals) { goal ->
                var isEditingValue by remember { mutableStateOf(false) }
                var inputtedValueStr by remember { mutableStateOf(goal.currentSaved.toString()) }
                var isGuideExpanded by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("goal_card_${goal.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = goal.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PlatinumWhite
                                )
                                Text(
                                    text = "Target Year: ${goal.targetYear} • Profile: ${goal.riskProfile}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SilverSlate
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { editingGoal = goal },
                                    modifier = Modifier.testTag("edit_goal_icon_${goal.id}")
                                ) {
                                    Icon(Icons.Filled.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { onDelete(goal.id) }) {
                                    Icon(Icons.Filled.Delete, "Delete", tint = CoralAlert)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = String.format("₹%,.0f Saved", goal.currentSaved),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = String.format("₹%,.0f Target", goal.targetAmount),
                                style = MaterialTheme.typography.bodySmall,
                                color = SilverSlate
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { goal.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.background
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = String.format("%.1f%% Completed", goal.progress * 100f),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (goal.progress >= 1f) EmeraldNeon else SilverSlate
                            )

                            if (!isEditingValue) {
                                Text(
                                    text = "Update Balance",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable { isEditingValue = true }
                                        .padding(4.dp)
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = inputtedValueStr,
                                        onValueChange = { inputtedValueStr = it },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.width(110.dp).height(48.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        textStyle = MaterialTheme.typography.bodySmall
                                    )
                                    IconButton(
                                        onClick = {
                                            val nv = inputtedValueStr.toDoubleOrNull()
                                            if (nv != null && nv >= 0) {
                                                onUpdateSaved(goal, nv)
                                                isEditingValue = false
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Filled.Check, "Save", tint = EmeraldNeon)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Expanding Guide Chevron
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isGuideExpanded = !isGuideExpanded }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(
                                    imageVector = if (isGuideExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "Expand Guide",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Achievement Guide & Strategy Switches",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (isGuideExpanded) {
                            Spacer(modifier = Modifier.height(8.dp))

                            val yearsRemaining = (goal.targetYear - 2026).coerceAtLeast(1)
                            val remainingAmount = (goal.targetAmount - goal.currentSaved).coerceAtLeast(0.0)
                            val monthsRemaining = yearsRemaining * 12

                            // SIP vs Savings calculations
                            val monthlySavingsNeeded = remainingAmount / monthsRemaining
                            // Est. 12% p.a. -> r = 0.01 per month
                            val r = 0.01
                            val denom = (1.0 + r).pow(monthsRemaining.toDouble()) - 1.0
                            val monthlySIPNeeded = if (denom > 0) (remainingAmount * r) / denom else monthlySavingsNeeded

                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "How to reach ₹${String.format("%,.0f", remainingAmount)} in $yearsRemaining year${if (yearsRemaining > 1) "s" else ""}:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                                .padding(6.dp)
                                        ) {
                                            Text("Mutual Fund SIP Target", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                            Text("₹${String.format("%,.0f", monthlySIPNeeded)} /mo", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                            Text("Est. 12% return p.a.", style = MaterialTheme.typography.labelSmall, color = SilverSlate)
                                        }
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                                .padding(6.dp)
                                        ) {
                                            Text("FD/RD Savings Target", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                                            Text("₹${String.format("%,.0f", monthlySavingsNeeded)} /mo", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                            Text("Est. 6% return p.a.", style = MaterialTheme.typography.labelSmall, color = SilverSlate)
                                        }
                                    }

                                    // Switching Advice Decision Box
                                    val (strategyText, dynamicActionText, indicatorColor) = when {
                                        yearsRemaining >= 5 -> Triple(
                                            "AGGRESSIVE ROTATION SIGNAL",
                                            "Invest 80% in Equity Mutual Funds (e.g., Nifty 50 Index / Flexicap direct funds) and 20% in Debt funds. Switch completely to savings/liquid FDs when tenure drops under 2 years to lock safe returns.",
                                            EmeraldNeon
                                        )
                                        yearsRemaining in 3..4 -> Triple(
                                            "BALANCED HYBRID SIGNAL",
                                            "Deploy in Balanced Advantage or Aggressive Hybrid Mutual Funds. Maintain moderate growth and switch automatically to pure Fixed Deposits when only 1 year remains.",
                                            GoldAccent
                                        )
                                        else -> Triple(
                                            "SAFE CAPITAL PRESERVATION",
                                            "Switch 100% to Recurring Deposits (RD), secure bank Fixed Deposits, or Liquid/Arbitrage Mutual Funds. Equity investments are too volatile for timelines under 3 years.",
                                            CoralAlert
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, indicatorColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                            .background(indicatorColor.copy(alpha = 0.05f))
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Box(modifier = Modifier.size(8.dp).background(indicatorColor, RoundedCornerShape(50.dp)))
                                                Text(
                                                    text = strategyText,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                                    color = indicatorColor
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = dynamicActionText,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    // Balanced Multi-Asset growth strategy
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "All-Weather Balanced MF Allocation (Constant Portfolio Growth)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldAccent
                                    )

                                    // Let's draw horizontal bar showing distributions
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                        ) {
                                            Box(modifier = Modifier.weight(0.40f).fillMaxHeight().background(EmeraldNeon))
                                            Box(modifier = Modifier.weight(0.30f).fillMaxHeight().background(MaterialTheme.colorScheme.primary))
                                            Box(modifier = Modifier.weight(0.20f).fillMaxHeight().background(GoldAccent))
                                            Box(modifier = Modifier.weight(0.10f).fillMaxHeight().background(CoralAlert))
                                        }

                                        // Legend / pills for the balanced investment distribution
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Box(modifier = Modifier.size(6.dp).background(EmeraldNeon, RoundedCornerShape(50.dp)))
                                                Text("Multi-Asset (40%)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = PlatinumWhite)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Box(modifier = Modifier.size(6.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50.dp)))
                                                Text("Nifty Index (30%)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = PlatinumWhite)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Box(modifier = Modifier.size(6.dp).background(GoldAccent, RoundedCornerShape(50.dp)))
                                                Text("Short-Debt (20%)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = PlatinumWhite)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Box(modifier = Modifier.size(6.dp).background(CoralAlert, RoundedCornerShape(50.dp)))
                                                Text("Gold Hedging (10%)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = PlatinumWhite)
                                            }
                                        }

                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(
                                                    text = "🎯 Balanced Mutual Fund Suggestions:",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = PlatinumWhite
                                                )
                                                Text(
                                                    text = "• ICICI Prudential Multi-Asset Allocation Fund (40%): Dynamically shifts holdings automatically among equity, debt, and gold based on valuation, maintaining positive growth irrespective of volatile market situations.\n" +
                                                           "• Nippon India Large Cap / SBI Nifty 50 Index Fund (30%): Delivers stable Indian equity expansion coupled with low expenses.\n" +
                                                           "• Aditya Birla Sun Life Short Term Debt Fund (20%): Cushions equity drawdowns during corrections while yielding regular predictable returns.\n" +
                                                           "• HDFC Gold Fund / Sovereign Gold Bonds (10%): Traditional shield that appreciates during stock bear runs.",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = SilverSlate,
                                                    lineHeight = 16.sp
                                                )

                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "🔄 Strict Entry and Exit Strategy Guidelines:",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = PlatinumWhite
                                                )
                                                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Text("📈", style = MaterialTheme.typography.bodySmall)
                                                    Text(
                                                        text = "ENTRY STRATEGY (Rupee Cost Averaging): Deploy funds via a Systematic Investment Plan (SIP) on a fixed monthly date. For lump sums, avoid direct equity entry; instead, invest in SBI Liquid Fund and configure a weekly Systematic Transfer Plan (STP) into your indices.",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = SilverSlate
                                                    )
                                                }
                                                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Text("📉", style = MaterialTheme.typography.bodySmall)
                                                    Text(
                                                        text = "EXIT STRATEGY (Capital Locking): Exactly 2 years prior to your milestone year (in YYYY: ${goal.targetYear - 2}), register a Systematic Withdrawal Plan (SWP) or systematic switches to transfer accumulated gains into ultra-secure Fixed Deposits or Liquid category funds.",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = SilverSlate
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Checklist action tasks
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("Recommended Checklist & Tasks:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SilverSlate)
                                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("•", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                            Text("Configure an auto-debit SIP in premium Direct Mutual Funds or set up recurring FD deposits.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("•", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                            Text("Avoid direct stock trading; prioritize broad Nifty 50 Index funds for steady wealth expansion.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                        if (goal.riskProfile == "Aggressive" || yearsRemaining >= 5) {
                                            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text("•", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                                Text("Leverage ELSS Mutual Funds to claim Section 80C tax benefits (save up to ₹46,800 in taxes).", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (editingGoal != null) {
            EditGoalDialog(
                goal = editingGoal!!,
                onDismiss = { editingGoal = null },
                onSave = { updatedGoal ->
                    onUpdateGoal(updatedGoal)
                    editingGoal = null
                }
            )
        }
    }
}

// ==================== 2. Portfolio Tracker & Rebalancing Screen ====================
@Composable
fun PortfolioScreen(
    holdings: List<Holding>,
    targetRiskProfile: String,
    onRiskProfileChange: (String) -> Unit,
    onDeleteHolding: (Int) -> Unit,
    onAddTemplateHolding: (String, String, Double) -> Unit
) {
    val totalInvested = holdings.sumOf { it.value }

    // Grouping & Calculating Allocations
    val groupSum = holdings.groupBy { it.assetClass }.mapValues { it.value.sumOf { h -> h.value } }
    val equityActual = if (totalInvested > 0) ((groupSum["Equity"] ?: 0.0) / totalInvested) * 100.0 else 0.0
    val debtActual = if (totalInvested > 0) ((groupSum["Debt"] ?: 0.0) / totalInvested) * 100.0 else 0.0
    val goldActual = if (totalInvested > 0) ((groupSum["Gold"] ?: 0.0) / totalInvested) * 100.0 else 0.0
    val cashActual = if (totalInvested > 0) ((groupSum["Cash"] ?: 0.0) / totalInvested) * 100.0 else 0.0

    // Recommended Target Splits
    // Conservative: Eq:20%, Debt:60%, Gold:10%, Cash:10%
    // Moderate: Eq:50%, Debt:35%, Gold:10%, Cash:5%
    // Aggressive: Eq:80%, Debt:10%, Gold:5%, Cash:5%
    val targetEquity = when (targetRiskProfile) {
        "Conservative" -> 20.0
        "Moderate" -> 50.0
        else -> 80.0
    }
    val targetDebt = when (targetRiskProfile) {
        "Conservative" -> 60.0
        "Moderate" -> 35.0
        else -> 10.0
    }
    val targetGold = when (targetRiskProfile) {
        "Conservative" -> 10.0
        "Moderate" -> 10.0
        else -> 5.0
    }
    val targetCash = when (targetRiskProfile) {
        "Conservative" -> 10.0
        "Moderate" -> 5.0
        else -> 5.0
    }

    // Identify deviations > 10%
    val isEquityMisaligned = kotlin.math.abs(equityActual - targetEquity) > 10.0
    val isDebtMisaligned = kotlin.math.abs(debtActual - targetDebt) > 10.0
    val isGoldMisaligned = kotlin.math.abs(goldActual - targetGold) > 10.0
    val isCashMisaligned = kotlin.math.abs(cashActual - targetCash) > 10.0
    val needsRebalancing = isEquityMisaligned || isDebtMisaligned || isGoldMisaligned || isCashMisaligned

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Total Portfolio Sum Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TOTAL NET WORTH",
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format("₹%,.2f", totalInvested),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Trend Indicator Badge
                    Row(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(100.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TrendingUp,
                            contentDescription = "Trending Up",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+2.4% vs Last Month",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Horizontal pill row to change profile
                    Text(
                        text = "ACTIVE TARGET ALLOCATION PROFILE",
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.08f), RoundedCornerShape(100.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Conservative", "Moderate", "Aggressive").forEach { profile ->
                            val active = targetRiskProfile == profile
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(if (active) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { onRiskProfileChange(profile) }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = profile,
                                    color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Real-time asset pie chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Asset Class Allocation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PlatinumWhite
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AllocationPieChart(holdings = holdings, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        // Section: Automated Rebalancing Alert Flag
        item {
            AnimatedVisibility(
                visible = needsRebalancing,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Balance,
                                    contentDescription = "Balance icon",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "REBALANCE",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "Portfolio drift exceeds 10% tolerance style",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Your current holdings have drifted away from your target '$targetRiskProfile' profile by more than 10%. Action is recommended to align with your risk preferences.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Show instruction details
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (isEquityMisaligned) {
                                RebalanceTipRow(
                                    name = "Equity",
                                    current = equityActual,
                                    target = targetEquity,
                                    totalInvested = totalInvested
                                )
                            }
                            if (isDebtMisaligned) {
                                RebalanceTipRow(
                                    name = "Debt",
                                    current = debtActual,
                                    target = targetDebt,
                                    totalInvested = totalInvested
                                )
                            }
                            if (isGoldMisaligned) {
                                RebalanceTipRow(
                                    name = "Gold",
                                    current = goldActual,
                                    target = targetGold,
                                    totalInvested = totalInvested
                                )
                            }
                            if (isCashMisaligned) {
                                RebalanceTipRow(
                                    name = "Cash",
                                    current = cashActual,
                                    target = targetCash,
                                    totalInvested = totalInvested
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Asset Listings and interactive deletion
        item {
            Text(
                text = "Holdings Portfolio Breakdown",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PlatinumWhite,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (holdings.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No assets configured.", color = SilverSlate)
                }
            }
        } else {
            items(holdings) { asset ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = asset.assetName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PlatinumWhite
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(getCategoryColor(asset.assetClass), shape = RoundedCornerShape(100.dp))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = asset.assetClass,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SilverSlate
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text(
                                text = String.format("₹%,.2f", asset.value),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldNeon
                            )
                            val pct = if (totalInvested > 0) (asset.value / totalInvested) * 100f else 0f
                            Text(
                                text = String.format("%.1f%% of portfolio", pct),
                                style = MaterialTheme.typography.labelSmall,
                                color = SilverSlate
                            )
                        }

                        IconButton(onClick = { onDeleteHolding(asset.id) }) {
                            Icon(Icons.Filled.Delete, "Remove Asset", tint = CoralAlert)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RebalanceTipRow(name: String, current: Double, target: Double, totalInvested: Double) {
    val deltaPercent = target - current
    val deltaINR = (deltaPercent / 100.0) * totalInvested
    val action = if (deltaPercent > 0) "Buy" else "Sell"
    val actionColor = if (deltaPercent > 0) EmeraldNeon else CoralAlert

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Class drift for $name : ${String.format("%.1f%%", current)} (Target ${String.format("%.1f%%", target)})",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = PlatinumWhite
            )
            Text(
                text = "${if (deltaPercent > 0) "Underallocated" else "Overallocated"} by ${String.format("%.1f%%", kotlin.math.abs(deltaPercent))}",
                style = MaterialTheme.typography.labelSmall,
                color = SilverSlate
            )
        }

        Box(
            modifier = Modifier
                .border(1.dp, actionColor.copy(alpha = 0.5f), RoundedCornerShape(50.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "$action ₹${String.format("%,.0f", kotlin.math.abs(deltaINR))}",
                color = actionColor,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold)
            )
        }
    }
}

// ==================== 3. Decisions, Deposits & Retirement Simulator ====================
@Composable
fun RetirementAndTaxScreen(
    currentHoldings: List<Holding>,
    viewModel: MainViewModel,
    onAddSuggestedAsset: (String, String, Double) -> Unit
) {
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val suggestionsLoading by viewModel.suggestionsLoading.collectAsStateWithLifecycle()
    val suggestionsError by viewModel.suggestionsError.collectAsStateWithLifecycle()
    val riskProfile by viewModel.targetRiskProfile.collectAsStateWithLifecycle()
    val todayDateStr = remember {
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    }
    val mfSearchQuery by viewModel.mfSearchQuery.collectAsStateWithLifecycle()
    val mfSearchResults by viewModel.mfSearchResults.collectAsStateWithLifecycle()
    val mfSearchLoading by viewModel.mfSearchLoading.collectAsStateWithLifecycle()
    val mfSearchError by viewModel.mfSearchError.collectAsStateWithLifecycle()
    val individualNavs by viewModel.individualNavs.collectAsStateWithLifecycle()
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    // Retirment projection states
    var currentAge by remember { mutableStateOf(30f) }
    var retirementAge by remember { mutableStateOf(60f) }
    var initialNestEgg by remember { mutableStateOf(500000f) }
    var regularSavingsMonthly by remember { mutableStateOf(10000f) }
    var roiRateExpected by remember { mutableStateOf(10.5f) }

    // Computes compounding nest egg trajectory
    val compoundingYears = (retirementAge - currentAge).coerceAtLeast(0f).toInt()
    var runningTotal = initialNestEgg.toDouble()
    val rate = roiRateExpected / 100.0
    for (i in 1..compoundingYears) {
        runningTotal = (runningTotal + (regularSavingsMonthly * 12)) * (1 + rate)
    }

    // Adjusting for Indian inflation impact (deflates by 5.0% inflation average in India)
    val inflationRate = 0.05
    val inflationAdjustedNet = runningTotal / (1 + inflationRate).pow(compoundingYears.toDouble())

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Retirement Simulator",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = PlatinumWhite
                )
                Text(
                    text = "Interactive projections of compound Indian wealth goals in action.",
                    color = SilverSlate,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Compound interest summary
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateMedium),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Estimated Net Worth at Retirement",
                        color = SilverSlate,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = String.format("₹%,.2f", runningTotal),
                        color = EmeraldNeon,
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format("Inflation-Adjusted equivalent (at 5%% inflation): ₹%,.0f", inflationAdjustedNet),
                        color = GoldAccent,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Active Projection Sliders
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Customize Projections Parameters",
                        style = MaterialTheme.typography.titleMedium,
                        color = PlatinumWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Slider 1: Ages
                    Text("Current Age: ${currentAge.toInt()} | Retire at: ${retirementAge.toInt()}", color = PlatinumWhite, style = MaterialTheme.typography.bodySmall)
                    RangeSlider(
                        value = currentAge..retirementAge,
                        onValueChange = { range ->
                            currentAge = range.start.coerceAtLeast(20f)
                            retirementAge = range.endInclusive.coerceAtMost(85f)
                        },
                        valueRange = 20f..85f,
                        colors = SliderDefaults.colors(
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            thumbColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Slider 2: Current capital
                    Text(String.format("Starting Capital: ₹%,.0f", initialNestEgg), color = PlatinumWhite, style = MaterialTheme.typography.bodySmall)
                    Slider(
                        value = initialNestEgg,
                        onValueChange = { initialNestEgg = it },
                        valueRange = 0f..10000000f,
                        steps = 100,
                        colors = SliderDefaults.colors(
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            thumbColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Slider 3: Monthly Savings
                    Text(String.format("Monthly Savings additions (SIP/RD): ₹%,.0f", regularSavingsMonthly), color = PlatinumWhite, style = MaterialTheme.typography.bodySmall)
                    Slider(
                        value = regularSavingsMonthly,
                        onValueChange = { regularSavingsMonthly = it },
                        valueRange = 0f..250000f,
                        steps = 100,
                        colors = SliderDefaults.colors(
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            thumbColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Slider 4: Interest rate expected
                    Text(String.format("Projected Market Return (ROI): %.1f%%", roiRateExpected), color = PlatinumWhite, style = MaterialTheme.typography.bodySmall)
                    Slider(
                        value = roiRateExpected,
                        onValueChange = { roiRateExpected = it },
                        valueRange = 4f..15f,
                        colors = SliderDefaults.colors(
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            thumbColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // --- 📊 LIVE ALGORITHMIC MUTUAL FUND SUGGESTIONS (api.mfapi.in AMFI Feed) ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("live_mf_suggestions_dashboard"),
                colors = CardDefaults.cardColors(containerColor = SlateMedium),
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Live Algorithmic Suggestions",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = GoldAccent
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Filled.Lock, "Algorithm", tint = SilverSlate, modifier = Modifier.size(12.dp))
                                Text(
                                    text = "Model: $riskProfile Profile Optimization",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PlatinumWhite
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.refreshAlgorithmicSuggestions(riskProfile) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.height(36.dp).testTag("refresh_suggestions_btn")
                        ) {
                            Text("Recalculate 🔄", fontSize = 10.sp, color = ObsidianDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Real-time performance ranking models compiled using the live Association of Mutual Funds in India (AMFI) api.mfapi.in feed. The recommendations algorithm automatically dynamically downloads, parses, and ranks historical NAV curves to optimize maximum 3-Year CAGR matching your custom target portfolio risk profile ($riskProfile).",
                        style = MaterialTheme.typography.bodySmall,
                        color = SilverSlate
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (suggestionsLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                CircularProgressIndicator(color = GoldAccent, modifier = Modifier.size(24.dp))
                                Text("Recalculating NAV returns...", style = MaterialTheme.typography.bodySmall, color = SilverSlate)
                            }
                        }
                    } else {
                        if (suggestionsError != null) {
                            Text(
                                text = suggestionsError ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = CoralAlert,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            suggestions.forEach { suggestion ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    border = BorderStroke(1.dp, GeometricCardBorder),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("suggestion_card_${suggestion.schemeCode}")
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = suggestion.fundName,
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = PlatinumWhite
                                                )
                                                Text(
                                                    text = "Category: ${suggestion.category} • AMC: ${suggestion.amcName}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                    color = SilverSlate
                                                )
                                            }

                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = when (suggestion.riskLabel) {
                                                        "Low Risk" -> EmeraldNeon.copy(alpha = 0.15f)
                                                        "Moderate Risk" -> GoldAccent.copy(alpha = 0.15f)
                                                        else -> CoralAlert.copy(alpha = 0.15f)
                                                    }
                                                ),
                                                border = BorderStroke(
                                                    1.dp,
                                                    when (suggestion.riskLabel) {
                                                        "Low Risk" -> EmeraldNeon.copy(alpha = 0.5f)
                                                        "Moderate Risk" -> GoldAccent.copy(alpha = 0.5f)
                                                        else -> CoralAlert.copy(alpha = 0.5f)
                                                    }
                                                ),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = suggestion.riskLabel,
                                                    color = when (suggestion.riskLabel) {
                                                        "Low Risk" -> EmeraldNeon
                                                        "Moderate Risk" -> GoldAccent
                                                        else -> CoralAlert
                                                    },
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Show statistical calculations
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("Latest Live NAV", style = MaterialTheme.typography.labelSmall, color = SilverSlate)
                                                Text(
                                                    text = String.format("₹%,.2f", suggestion.latestNav),
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = PlatinumWhite
                                                )
                                                if (suggestion.latestNavDate.isNotEmpty()) {
                                                    Text(
                                                        text = suggestion.latestNavDate,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                                        color = SilverSlate
                                                    )
                                                }
                                            }

                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("1-Year Yield", style = MaterialTheme.typography.labelSmall, color = SilverSlate)
                                                Text(
                                                    text = String.format("%.2f%%", suggestion.oneYearReturn),
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = if (suggestion.oneYearReturn >= 0) EmeraldNeon else CoralAlert
                                                )
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("3-Year CAGR", style = MaterialTheme.typography.labelSmall, color = SilverSlate)
                                                Text(
                                                    text = String.format("%.2f%%", suggestion.threeYearCagr),
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = if (suggestion.threeYearCagr >= 0) EmeraldNeon else CoralAlert
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.clickable {
                                                    val url = when {
                                                        suggestion.fundName.lowercase().contains("sbi") -> "https://www.sbimf.com"
                                                        suggestion.fundName.lowercase().contains("hdfc") -> "https://www.hdfcfund.com"
                                                        suggestion.fundName.lowercase().contains("nippon") -> "https://www.nipponindiamf.com"
                                                        suggestion.fundName.lowercase().contains("icici") -> "https://www.icicipruamc.com"
                                                        suggestion.fundName.lowercase().contains("parag parikh") || suggestion.fundName.lowercase().contains("ppfas") -> "https://amc.ppfas.com"
                                                        suggestion.fundName.lowercase().contains("quant") -> "https://www.quantmutual.com"
                                                        suggestion.fundName.lowercase().contains("uti") -> "https://www.utimf.com"
                                                        else -> "https://www.amfiindia.com"
                                                    }
                                                    uriHandler.openUri(url)
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.OpenInNew,
                                                    contentDescription = "Invest Direct",
                                                    tint = GoldAccent,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = "AMC Web Investment Portal",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = GoldAccent,
                                                        fontWeight = FontWeight.SemiBold,
                                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            }

                                            Button(
                                                onClick = {
                                                    // Convert to a direct portfolio hold allocation suggestions helper
                                                    onAddSuggestedAsset("Mutual Fund", suggestion.fundName, suggestion.minInvestment)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp).testTag("suggest_buy_btn_${suggestion.schemeCode}")
                                            ) {
                                                Text("Add Allocation ₹${String.format("%.0f", suggestion.minInvestment)}", fontSize = 9.sp, color = ObsidianDark, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Real Data Deposit recommendations lists
        item {
            Text(
                text = "Indian High-Yield Savings & Investments",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = PlatinumWhite,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // --- 🔍 LIVE NSE/AMFI MUTUAL FUND EXPLORER SECTION (NO SIMULATIONS) ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .testTag("live_mf_search_card"),
                colors = CardDefaults.cardColors(containerColor = SlateMedium),
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Live NSE/AMFI Scheme Explorer",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldAccent
                    )
                    Text(
                        text = "Query direct, non-simulated real schemes from the Indian Mutual Fund Association feed. Retrieve accurate live NAV updates on demand.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SilverSlate,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    var queryText by remember { mutableStateOf(mfSearchQuery) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = queryText,
                            onValueChange = {
                                queryText = it
                                // Auto search in real-time if query has at least 3 chars
                                if (it.trim().length >= 3) {
                                    viewModel.searchMutualFunds(it)
                                }
                            },
                            placeholder = { Text("Search Scheme e.g., SBI, HDFC, ABSL...", color = SilverSlate, fontSize = 12.sp) },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = PlatinumWhite, fontSize = 12.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = PlatinumWhite,
                                unfocusedTextColor = PlatinumWhite,
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = SilverSlate.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("live_mf_query_input")
                        )

                        Button(
                            onClick = { viewModel.searchMutualFunds(queryText) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("live_mf_search_btn")
                        ) {
                            Text("Search", fontSize = 11.sp, color = ObsidianDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (mfSearchLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = GoldAccent, modifier = Modifier.size(24.dp))
                        }
                    } else if (mfSearchError != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mfSearchError ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (mfSearchError!!.startsWith("Enter")) SilverSlate else CoralAlert
                            )
                        }
                    } else if (mfSearchResults.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            mfSearchResults.take(8).forEach { item ->
                                val navLoaded = individualNavs[item.schemeCode]
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                                    border = BorderStroke(1.dp, GeometricCardBorder.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("live_match_item_${item.schemeCode}")
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = item.schemeName,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = PlatinumWhite
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Code: ${item.schemeCode}",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                                color = SilverSlate
                                            )

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                if (navLoaded != null) {
                                                    Text(
                                                        text = "NAV: ₹$navLoaded",
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = MaterialTheme.colorScheme.primary,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            fontSize = 11.sp
                                                        )
                                                    )
                                                } else {
                                                    // Request NAV action
                                                    Text(
                                                        text = "Load Live NAV",
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = GoldAccent,
                                                            fontWeight = FontWeight.Bold,
                                                            textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                                                            fontSize = 10.sp
                                                        ),
                                                        modifier = Modifier
                                                            .clickable { viewModel.fetchLatestNavForScheme(item.schemeCode) }
                                                            .testTag("load_nav_btn_${item.schemeCode}")
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier
                                                    .clickable { uriHandler.openUri(item.amcUrl) }
                                                    .testTag("live_link_btn_${item.schemeCode}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.OpenInNew,
                                                    contentDescription = "AMC Website LINK",
                                                    tint = GoldAccent,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = "Official AMC Website",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = GoldAccent,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                                                    )
                                                )
                                            }

                                            Button(
                                                onClick = {
                                                    onAddSuggestedAsset("Equity", item.schemeName, 10000.0)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(26.dp).testTag("live_invest_btn_${item.schemeCode}")
                                            ) {
                                                Text("Invest ₹10k", fontSize = 9.sp, color = SilverSlate)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Provide standard AMC query (e.g., 'SBI', 'HDF', 'Axis') to search live schemes",
                                style = MaterialTheme.typography.bodySmall,
                                color = SilverSlate,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        val depositSuggestions = listOf(
            MFRecommendation("Equity", "Parag Parikh Flexi Cap Fund", 21.4, "https://amc.ppfas.com", "Acclaimed global multi-cap equity compounder with high tech and consumer exposure."),
            MFRecommendation("Equity", "HDFC Mid-Cap Opportunities Fund", 24.5, "https://www.hdfcfund.com", "Highly resilient mid-cap corporate strategy focused on high-growth Indian sectors."),
            MFRecommendation("Debt", "SBI Magnum Constant Maturity Fund", 7.8, "https://www.sbimf.com", "Sovereign guaranteed long duration gilt fund shielding capital from typical equity volatility."),
            MFRecommendation("Debt", "Nippon India Liquid Fund Strategy", 6.8, "https://www.nipponindiamf.com", "Instant liquidity and steady marginal overnight compounding yields for idle corporate/cash reserves."),
            MFRecommendation("Hybrid", "ICICI Prudential Asset Allocator Fund", 14.8, "https://www.icicipruamc.com", "Dynamic allocation mutual fund that auto-hedges capital between equity and secure debt indexes.")
        )

        items(depositSuggestions) { sug ->
            Card(
                modifier = Modifier.fillMaxWidth().testTag("real_time_suggestion_card_${sug.name.replace(" ", "_")}"),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = sug.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PlatinumWhite
                            )
                            Text(
                                text = "Class: ${sug.assetClass} • Annualized Return Trend",
                                style = MaterialTheme.typography.bodySmall,
                                color = SilverSlate
                            )
                        }

                        Text(
                            text = "${sug.yield}% p.a.",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = sug.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = SilverSlate,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clickable { uriHandler.openUri(sug.url) }
                                .testTag("real_time_link_${sug.name.replace(" ", "_")}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.OpenInNew,
                                contentDescription = "Open Website",
                                tint = GoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Fund Website",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                                )
                            )
                        }

                        Button(
                            onClick = {
                                onAddSuggestedAsset(sug.assetClass, sug.name, 10000.0)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Add holding", fontSize = 11.sp, color = SilverSlate)
                        }
                    }
                }
            }
        }

        // Tax-efficiency section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ReceiptLong,
                                contentDescription = "Tax Strategy Icon",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "INDIAN TAX STRATEGY",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = "Preserve wealth under Indian Income Tax Act",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    TaxTipPill(
                        title = "Claim Section 80C Deductions",
                        desc = "Invest up to ₹1.5 Lakhs per financial year in ELSS Mutual Funds, PPF, or National Savings Certificate (NSC) to lower taxable salary income."
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TaxTipPill(
                        title = "Maximize NPS under Section 80CCD(1B)",
                        desc = "Contribute an additional ₹50,000 directly to National Pension Scheme (NPS) for deductions beyond Section 80C."
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TaxTipPill(
                        title = "Capital Gains Tax Strategy",
                        desc = "Hold equity mutual funds for 1+ years to leverage LTCG (12.5% tax on gains above ₹1.25L), rather than paying 20% on STCG."
                    )
                }
            }
        }
    }
}

@Composable
fun TaxTipPill(title: String, desc: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = PlatinumWhite)
        Spacer(modifier = Modifier.height(2.dp))
        Text(desc, style = MaterialTheme.typography.bodySmall, color = SilverSlate)
    }
}

// ==================== 4. AI Advisor & Performance History ====================
@Composable
fun AIAdvisorAndHistoryScreen(
    viewModel: MainViewModel,
    snapshots: List<MonthlySnapshot>,
    onDeleteSnapshot: (Int) -> Unit
) {
    val aiAdvice by viewModel.aiAdvice.collectAsStateWithLifecycle()
    val adviceLoading by viewModel.adviceLoading.collectAsStateWithLifecycle()
    var noteInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Financial AI Advisor",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = PlatinumWhite
                )
                Text(
                    text = "Personalized tax strategies, retirement projects and monthly audits.",
                    color = SilverSlate,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Gemini advice card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_advice_card"),
                colors = CardDefaults.cardColors(containerColor = SlateMedium),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "COGNITIVE WEALTH AUDIT",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (adviceLoading) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Assembling market files and formulating advice...", color = SilverSlate, style = MaterialTheme.typography.bodySmall)
                        }
                    } else if (aiAdvice.isEmpty()) {
                        Text(
                            text = "Welcome to Gemini Financial! Ask any custom question below (e.g. 'How can I save taxable gains on equity?') or click 'Build Fiduciary Report' to analyze milestone safety.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SilverSlate
                        )
                    } else if (aiAdvice == "API_KEY_MISSING") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CoralAlert.copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, CoralAlert)
                        ) {
                            Text(
                                text = "Gemini API Key is missing. Please add your GEMINI_API_KEY inside the Secrets panel of Google AI Studio.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CoralAlert,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    } else {
                        // Display Advice Text beautifully
                        Text(
                            text = aiAdvice,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                            color = PlatinumWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input controls
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        placeholder = { Text("Ask a custom question to the wealth advisor...") },
                        modifier = Modifier.fillMaxWidth().testTag("ai_custom_input"),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SilverSlate.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.fetchFinancialPlanningAdvice(noteInput)
                                noteInput = ""
                            },
                            enabled = !adviceLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f).testTag("ask_ai_button")
                        ) {
                            Text("Consult AI Advisor", color = ObsidianDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section: Historical Net Worth Snapshot
        item {
            Text(
                text = "Performance History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = PlatinumWhite,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Net Worth MoM Growth trajectory",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = PlatinumWhite
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NetWorthLineChart(
                        snapshots = snapshots,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }
            }
        }

        // Historial listing list
        items(snapshots) { snap ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = snap.monthYear,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = PlatinumWhite
                        )
                        Text(
                            text = "Savings: ${snap.savingsRate}% of Income",
                            style = MaterialTheme.typography.bodySmall,
                            color = SilverSlate
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = String.format("₹%,.0f", snap.netWorth),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        IconButton(onClick = { onDeleteSnapshot(snap.id) }) {
                            Icon(Icons.Filled.Delete, "Remove snapshot", tint = CoralAlert)
                        }
                    }
                }
            }
        }
    }
}

// ==================== Custom Charts Components ====================

@Composable
fun AllocationPieChart(holdings: List<Holding>, modifier: Modifier = Modifier) {
    val totals = holdings.groupBy { it.assetClass }.mapValues { entry -> entry.value.sumOf { it.value } }
    val totalSum = totals.values.sum()
    if (totalSum == 0.0) {
        Box(modifier = modifier.height(100.dp), contentAlignment = Alignment.Center) {
            Text("No Assets Registered", color = SilverSlate.copy(alpha = 0.5f))
        }
        return
    }

    val slices = totals.map { (category, value) ->
        val fraction = (value / totalSum).toFloat()
        category to fraction
    }.sortedByDescending { it.second }

    val colors = listOf(
        EmeraldNeon,
        SageDarkSecondary,
        GoldAccent,
        Color(0xFFFF5252),
        Color(0xFF29B6F6)
    )

    Row(
        modifier = modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Canvas(modifier = Modifier.size(100.dp).testTag("allocation_pie_canvas")) {
            var startAngle = -90f
            slices.forEachIndexed { index, (_, fraction) ->
                val sweepAngle = fraction * 360f
                val color = colors[index % colors.size]
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )
                startAngle += sweepAngle
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            slices.forEachIndexed { index, (category, fraction) ->
                val color = colors[index % colors.size]
                val amt = totals[category] ?: 0.0
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(color, shape = RoundedCornerShape(100.dp))
                    )
                    Column {
                        Text(
                            text = "$category (${String.format("%.1f%%", fraction * 100f)})",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlatinumWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = String.format("₹%,.0f", amt),
                            style = MaterialTheme.typography.labelSmall,
                            color = SilverSlate
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NetWorthLineChart(snapshots: List<MonthlySnapshot>, modifier: Modifier = Modifier) {
    if (snapshots.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("No snapshot records logged.", color = SilverSlate.copy(alpha = 0.5f))
        }
        return
    }

    val maxVal = snapshots.maxOfOrNull { it.netWorth } ?: 1.0
    val minVal = snapshots.minOfOrNull { it.netWorth } ?: 0.0
    val range = (maxVal - minVal).coerceAtLeast(1000.0)

    val strokeColor = EmeraldNeon
    val bgColor = ObsidianDark

    Canvas(modifier = modifier.testTag("net_worth_chart_canvas")) {
        val width = size.width
        val height = size.height

        val points = snapshots.mapIndexed { index, snapshot ->
            val x = if (snapshots.size > 1) {
                (index.toFloat() / (snapshots.size - 1)) * (width - 60f) + 30f
            } else {
                width / 2f
            }
            val valDiff = (snapshot.netWorth - minVal)
            val yFactor = if (range > 0) (valDiff / range).toFloat() else 0.5f
            val y = height - (yFactor * (height - 40f) + 20f)
            x to y
        }

        // Draw connections path
        val path = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points[0].first, points[0].second)
                for (i in 1 until points.size) {
                    lineTo(points[i].first, points[i].second)
                }
            }
        }

        // Fill background gradients
        if (points.isNotEmpty()) {
            val fillPath = Path().apply {
                moveTo(points[0].first, points[0].second)
                for (i in 1 until points.size) {
                    lineTo(points[i].first, points[i].second)
                }
                lineTo(points.last().first, height)
                lineTo(points.first().first, height)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        strokeColor.copy(alpha = 0.25f),
                        Color.Transparent
                    )
                )
            )
        }

        // Stroke line
        drawPath(
            path = path,
            color = strokeColor,
            style = Stroke(width = 4f)
        )

        // Draw dot circles and label tags
        points.forEach { (x, y) ->
            drawCircle(color = bgColor, radius = 8f, center = Offset(x, y))
            drawCircle(color = strokeColor, radius = 5f, center = Offset(x, y))
        }
    }
}

@Composable
fun getCategoryColor(cat: String): Color {
    return when (cat) {
        "Equity" -> EmeraldNeon
        "Debt" -> SageDarkSecondary
        "Gold" -> GoldAccent
        else -> Color(0xFF29B6F6)
    }
}

// ==================== Overlay Dialogs ====================

@Composable
fun AddGoalDialog(onDismiss: () -> Unit, onSave: (String, Double, Double, Int, Int, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var targetStr by remember { mutableStateOf("") }
    var curStr by remember { mutableStateOf("") }
    var yearStr by remember { mutableStateOf("2028") }
    var riskProfile by remember { mutableStateOf("Moderate") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assemble New Milestone Target") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Milestone Goal Name (e.g., Home Downpayment)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_goal_name")
                )
                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Overall target amount (₹)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_goal_target")
                )
                OutlinedTextField(
                    value = curStr,
                    onValueChange = { curStr = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Initially saved balance (₹)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_goal_saved")
                )
                OutlinedTextField(
                    value = yearStr,
                    onValueChange = { yearStr = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Target Year") },
                    modifier = Modifier.fillMaxWidth().testTag("input_goal_year")
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text("Assigned Risk Profile", style = MaterialTheme.typography.bodySmall, color = SilverSlate)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Conservative", "Moderate", "Aggressive").forEach { prof ->
                        val selected = riskProfile == prof
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    riskProfile = prof
                                    yearStr = when (prof) {
                                        "Conservative" -> "2028"
                                        "Moderate" -> "2031"
                                        "Aggressive" -> "2038"
                                        else -> "2031"
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                prof,
                                color = if (selected) ObsidianDark else PlatinumWhite,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val targ = targetStr.toDoubleOrNull() ?: 0.0
                    val initVal = curStr.toDoubleOrNull() ?: 0.0
                    val yr = yearStr.toIntOrNull() ?: 2028
                    if (name.isNotBlank() && targ > 0) {
                        onSave(name, targ, initVal, yr, 12, riskProfile)
                    }
                }
            ) {
                Text("Compile Target")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abort") }
        }
    )
}

@Composable
fun EditGoalDialog(
    goal: FinancialGoal,
    onDismiss: () -> Unit,
    onSave: (FinancialGoal) -> Unit
) {
    var name by remember { mutableStateOf(goal.name) }
    var targetStr by remember { mutableStateOf(goal.targetAmount.toString()) }
    var curStr by remember { mutableStateOf(goal.currentSaved.toString()) }
    var yearStr by remember { mutableStateOf(goal.targetYear.toString()) }
    var riskProfile by remember { mutableStateOf(goal.riskProfile) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Milestone Target") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Milestone Goal Name (e.g., Home Downpayment)") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_goal_name")
                )
                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Overall target amount (₹)") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_goal_target")
                )
                OutlinedTextField(
                    value = curStr,
                    onValueChange = { curStr = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Initially saved balance (₹)") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_goal_saved")
                )
                OutlinedTextField(
                    value = yearStr,
                    onValueChange = { yearStr = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Target Year") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_goal_year")
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text("Assigned Risk Profile", style = MaterialTheme.typography.bodySmall, color = SilverSlate)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Conservative", "Moderate", "Aggressive").forEach { prof ->
                        val selected = riskProfile == prof
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    riskProfile = prof
                                    yearStr = when (prof) {
                                        "Conservative" -> "2028"
                                        "Moderate" -> "2031"
                                        "Aggressive" -> "2038"
                                        else -> "2031"
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                prof,
                                color = if (selected) ObsidianDark else PlatinumWhite,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val targ = targetStr.toDoubleOrNull() ?: goal.targetAmount
                    val initVal = curStr.toDoubleOrNull() ?: goal.currentSaved
                    val yr = yearStr.toIntOrNull() ?: goal.targetYear
                    if (name.isNotBlank() && targ > 0) {
                        onSave(goal.copy(
                            name = name,
                            targetAmount = targ,
                            currentSaved = initVal,
                            targetYear = yr,
                            riskProfile = riskProfile
                        ))
                    }
                }
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abort") }
        }
    )
}

@Composable
fun AddHoldingDialog(onDismiss: () -> Unit, onSave: (String, String, Double) -> Unit) {
    var category by remember { mutableStateOf("Equity") }
    var name by remember { mutableStateOf("") }
    var valueStr by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register Portfolio Holding") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Asset Class Type", style = MaterialTheme.typography.bodySmall, color = SilverSlate)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Equity", "Debt", "Gold", "Cash").forEach { cat ->
                        val selected = category == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { category = cat }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                cat,
                                color = if (selected) ObsidianDark else PlatinumWhite,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Asset Name (e.g. Nippon Large Cap Mutual Fund)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_asset_name")
                )
                OutlinedTextField(
                    value = valueStr,
                    onValueChange = { valueStr = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Current value balance (₹)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_asset_value")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = valueStr.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && amt > 0) {
                        onSave(category, name, amt)
                    }
                }
            ) {
                Text("Register")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abort") }
        }
    )
}

@Composable
fun AddSnapshotDialog(onDismiss: () -> Unit, onSave: (String, Double, Double) -> Unit) {
    var monthYear by remember { mutableStateOf("2026-06") }
    var netWorthStr by remember { mutableStateOf("") }
    var savingsRateStr by remember { mutableStateOf("25.0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Monthly snapshot summary") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = monthYear,
                    onValueChange = { monthYear = it },
                    label = { Text("Month (YYYY-MM)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_snap_month")
                )
                OutlinedTextField(
                    value = netWorthStr,
                    onValueChange = { netWorthStr = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Total Net Worth value (₹)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_snap_nw")
                )
                OutlinedTextField(
                    value = savingsRateStr,
                    onValueChange = { savingsRateStr = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Savings Rate index (%)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_snap_rate")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val nw = netWorthStr.toDoubleOrNull() ?: 0.0
                    val sr = savingsRateStr.toDoubleOrNull() ?: 0.0
                    if (monthYear.isNotBlank() && nw > 0) {
                        onSave(monthYear, nw, sr)
                    }
                }
            ) {
                Text("Save snapshot")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abort") }
        }
    )
}

// --- Auxiliary suggestion classes ---
data class MFRecommendation(
    val assetClass: String,
    val name: String,
    val yield: Double,
    val url: String,
    val description: String
)
