# SimpleLedger

SimpleLedger is a small, local-only Android expense tracker for personal daily bookkeeping.

## Goals

- Fast expense entry
- Local Room database only
- No login
- No network dependency
- No ads
- No cloud sync
- No `INTERNET` permission

## Tech Stack

- Kotlin
- Jetpack Compose
- Room
- ViewModel
- Flow / StateFlow
- DataStore
- Material 3
- Gradle Kotlin DSL

## Current Features

- Quick expense entry with PHP / CNY / USD
- Fixed category IDs stored in the database
- Transaction history with edit and delete
- Statistics by week, month, quarter, and year
- Per-currency statistics without mixing currencies
- Category pie chart drawn with Compose Canvas
- Expense trend line chart drawn with Compose Canvas
- Unit tests for money conversion, date ranges, aggregation, and trend zero filling

## Verification

This workspace does not currently include `gradlew`, and the local machine does not expose a `gradle` command. Android build and tests still need to be verified in an Android/Gradle environment:

```bash
./gradlew test
./gradlew assembleDebug
```

See `PROJECT_STATUS.md` for the current implementation status and device verification checklist.
