package com.arthas.simpleledger.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.arthas.simpleledger.model.CurrencyCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class SettingsRepository(private val context: Context) {
    private val defaultCurrencyKey = stringPreferencesKey("default_currency")

    val defaultCurrency: Flow<CurrencyCode> = context.dataStore.data.map { prefs ->
        prefs[defaultCurrencyKey]?.let { CurrencyCode.valueOf(it) } ?: CurrencyCode.PHP
    }

    suspend fun setDefaultCurrency(currency: CurrencyCode) {
        context.dataStore.edit { it[defaultCurrencyKey] = currency.name }
    }
}
