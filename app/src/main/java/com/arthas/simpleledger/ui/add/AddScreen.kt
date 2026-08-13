package com.arthas.simpleledger.ui.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arthas.simpleledger.model.Category
import com.arthas.simpleledger.model.CurrencyCode
import com.arthas.simpleledger.ui.ChoiceChips

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun AddScreen(viewModel: AddViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.uiState.collectAsState()
    val amountFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        amountFocusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = state.amount,
            onValueChange = viewModel::setAmount,
            modifier = Modifier.fillMaxWidth().focusRequester(amountFocusRequester),
            label = { Text("金额") },
            prefix = { Text(state.currency.symbol) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        ChoiceChips(
            values = CurrencyCode.entries,
            selected = state.currency,
            label = { it.name },
            onSelect = viewModel::setCurrency
        )
        Text("分类")
        ChoiceChips(
            values = Category.entries,
            selected = state.category,
            label = { it.label },
            onSelect = viewModel::setCategory
        )
        OutlinedTextField(
            value = state.note,
            onValueChange = viewModel::setNote,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("备注，可不填") },
            singleLine = true
        )
        Spacer(Modifier.height(4.dp))
        Button(
            onClick = viewModel::save,
            enabled = state.canSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("保存")
        }
        state.savedMessage?.let { Text(it) }
    }
}
