package com.arthas.simpleledger.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arthas.simpleledger.model.CurrencyCode
import com.arthas.simpleledger.ui.ChoiceChips

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val defaultCurrency by viewModel.defaultCurrency.collectAsState()
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("默认币种")
        ChoiceChips(
            values = CurrencyCode.entries,
            selected = defaultCurrency,
            label = { it.name },
            onSelect = viewModel::setDefaultCurrency
        )
        Text("导出数据：第一版暂未实现，后续可加 CSV / JSON。")
    }
}
