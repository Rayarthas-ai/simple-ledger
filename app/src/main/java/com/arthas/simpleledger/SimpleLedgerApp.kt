package com.arthas.simpleledger

import android.app.Application
import com.arthas.simpleledger.data.AppDatabase
import com.arthas.simpleledger.data.SettingsRepository
import com.arthas.simpleledger.data.TransactionRepository

class SimpleLedgerApp : Application() {
    val transactionRepository by lazy { TransactionRepository(AppDatabase.get(this).transactionDao()) }
    val settingsRepository by lazy { SettingsRepository(this) }
}
