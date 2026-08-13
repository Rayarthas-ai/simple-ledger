package com.arthas.simpleledger.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arthas.simpleledger.data.TransactionEntity
import com.arthas.simpleledger.model.Category
import com.arthas.simpleledger.model.CurrencyCode
import com.arthas.simpleledger.ui.ChoiceChips
import com.arthas.simpleledger.ui.TextChip
import com.arthas.simpleledger.util.MoneyFormatter
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(viewModel: HistoryViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.uiState.collectAsState()
    var selected by remember { mutableStateOf<TransactionEntity?>(null) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextChip("全部币种") { viewModel.setCurrency(null) }
            CurrencyCode.entries.forEach { currency -> TextChip(currency.name) { viewModel.setCurrency(currency) } }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextChip("全部分类") { viewModel.setCategory(null) }
            Category.entries.take(5).forEach { category -> TextChip(category.label) { viewModel.setCategory(category) } }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.transactions, key = { it.id }) { item ->
                TransactionRow(item, onClick = { selected = item })
            }
        }
    }

    selected?.let { item ->
        var amount by remember(item.id) { mutableStateOf(MoneyFormatter.formatMinor(item.amountMinor, item.currency, false).replace(",", "")) }
        var currency by remember(item.id) { mutableStateOf(item.currency) }
        var category by remember(item.id) { mutableStateOf(item.category) }
        var note by remember(item.id) { mutableStateOf(item.note.orEmpty()) }
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text("编辑记录") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("""\d{0,9}(\.\d{0,2})?"""))) amount = it },
                        label = { Text("金额") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    ChoiceChips(CurrencyCode.entries, currency, { it.name }, { currency = it })
                    ChoiceChips(Category.entries, category, { it.label }, { category = it })
                    OutlinedTextField(value = note, onValueChange = { note = it.take(80) }, label = { Text("备注") })
                    Text(formatDate(item.timestamp))
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        viewModel.delete(item)
                        selected = null
                    }) { Text("删除") }
                    Button(onClick = {
                        val minor = MoneyFormatter.parseToMinor(amount) ?: return@Button
                        viewModel.update(item.copy(amountMinor = minor, currency = currency, category = category, note = note.ifBlank { null }))
                        selected = null
                    }) { Text("保存") }
                }
            },
            dismissButton = {
                TextButton(onClick = { selected = null }) { Text("关闭") }
            }
        )
    }
}

@Composable
private fun TransactionRow(item: TransactionEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(item.category.label)
            Text(formatDate(item.timestamp))
        }
        Text(MoneyFormatter.formatMinor(item.amountMinor, item.currency))
    }
}

private fun formatDate(timestamp: Long): String =
    Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ISO_LOCAL_DATE)
