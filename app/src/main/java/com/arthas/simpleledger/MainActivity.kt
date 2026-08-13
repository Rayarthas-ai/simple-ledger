package com.arthas.simpleledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arthas.simpleledger.ui.AppViewModelFactory
import com.arthas.simpleledger.ui.add.AddScreen
import com.arthas.simpleledger.ui.add.AddViewModel
import com.arthas.simpleledger.ui.history.HistoryScreen
import com.arthas.simpleledger.ui.history.HistoryViewModel
import com.arthas.simpleledger.ui.settings.SettingsScreen
import com.arthas.simpleledger.ui.settings.SettingsViewModel
import com.arthas.simpleledger.ui.statistics.StatisticsScreen
import com.arthas.simpleledger.ui.statistics.StatisticsViewModel

enum class AppTab(val label: String) {
    ADD("记账"),
    HISTORY("流水"),
    STATISTICS("统计"),
    SETTINGS("设置")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as SimpleLedgerApp
        val factory = AppViewModelFactory(app.transactionRepository, app.settingsRepository)
        val addVm by viewModels<AddViewModel> { factory }
        val historyVm by viewModels<HistoryViewModel> { factory }
        val statisticsVm by viewModels<StatisticsViewModel> { factory }
        val settingsVm by viewModels<SettingsViewModel> { factory }

        setContent {
            MaterialTheme {
                var tab by rememberSaveable { mutableStateOf(AppTab.ADD) }
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            AppTab.entries.forEach {
                                NavigationBarItem(
                                    selected = tab == it,
                                    onClick = { tab = it },
                                    label = { Text(it.label) },
                                    icon = { Text(it.label.first().toString()) }
                                )
                            }
                        }
                    }
                ) { padding ->
                    val modifier = Modifier.padding(padding)
                    when (tab) {
                        AppTab.ADD -> AddScreen(addVm, modifier)
                        AppTab.HISTORY -> HistoryScreen(historyVm, modifier)
                        AppTab.STATISTICS -> StatisticsScreen(statisticsVm, modifier)
                        AppTab.SETTINGS -> SettingsScreen(settingsVm, modifier)
                    }
                }
            }
        }
    }
}
