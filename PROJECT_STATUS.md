# SimpleLedger Project Status

## 当前完成内容

- Phase 1：已创建单 `app` module Android 项目骨架。
- Phase 2：已实现 Room 数据层：Entity、DAO、Database、Repository。
- Phase 3：已实现快速记账页面：金额、币种、分类、备注、保存。
- Phase 4：已实现流水页面：倒序列表、币种/分类轻筛选、点击编辑、删除。
- Phase 5：已实现统计页面：周/月/季度/年，按 `[start, end)` 时间范围过滤。
- Phase 6：已实现设置页面：默认币种保存到 DataStore。
- Phase 7：已加入金额转换与时间范围 Unit Test。
- 2026-08-12：补齐 `AppViewModelFactory.kt`，修复 0 字节导致的编译阻塞点。
- 2026-08-12：快速记账页金额输入框已增加启动后自动聚焦，并尝试弹出数字键盘。
- 2026-08-13：统计页增加币种选择，图表与统计只显示当前选定币种。
- 2026-08-13：统计页增加分类支出占比饼图，使用 Compose Canvas 自绘，未引入第三方图表库。
- 2026-08-13：统计页增加支出趋势折线图；周/月/季度按日展示，年视图按月展示。
- 2026-08-13：DAO 增加统计 aggregation query：按分类汇总、按日汇总、按月汇总。
- 2026-08-13：增加日期补零逻辑，确保无消费日期/月份也显示 0。
- 2026-08-13：新增统计相关 Unit Test：分类汇总、多币种隔离、日期补零、月末、闰年、季度边界、年视图 12 个月。

## 当前目录结构

```text
app/
  src/main/java/com/arthas/simpleledger/
    data/
      AppDatabase.kt
      Converters.kt
      SettingsRepository.kt
      SummaryRows.kt
      TransactionDao.kt
      TransactionEntity.kt
      TransactionRepository.kt
    model/
      Category.kt
      CurrencyCode.kt
      TransactionType.kt
    ui/
      add/
      history/
      statistics/
        StatisticsCharts.kt
        StatisticsScreen.kt
        StatisticsViewModel.kt
      settings/
      AppViewModelFactory.kt
      CommonUi.kt
    util/
      DateRangeUtil.kt
      MoneyFormatter.kt
      StatisticsAggregationUtil.kt
      TrendSummaryUtil.kt
    MainActivity.kt
    SimpleLedgerApp.kt
  src/test/java/com/arthas/simpleledger/util/
    DateRangeUtilTest.kt
    MoneyFormatterTest.kt
    StatisticsAggregationUtilTest.kt
    TrendSummaryUtilTest.kt
gradle/
  libs.versions.toml
```

## 数据库

- Room database：`simple_ledger.db`
- version：1
- 表：`transactions`
- 金额字段：`amountMinor: Long`，不使用 Float/Double。
- 币种保存原始币种：PHP / CNY / USD，不做汇率换算。
- 分类保存稳定 ID：FOOD、SNACK、RENT、WIFI、ELECTRICITY、WATER、TRANSPORT、DAILY、MEDICAL、GAME、OTHER。
- 统计查询由 Room / SQLite 完成 `SUM` / `GROUP BY`，避免全量读取历史交易后再做图表统计。

## 已实现功能

- 纯本地，无登录、无网络、无广告。
- Manifest 未申请 INTERNET / 定位 / 通讯录 / 相机等权限。
- 快速记账保存后清空金额和备注，保留当前币种和分类，显示“已保存”反馈。
- 流水页支持查看、编辑、删除。
- 统计按选定币种单独汇总，严禁 PHP/CNY/USD 直接相加。
- 分类饼图过滤当前时间范围、当前币种、EXPENSE、非 0 金额分类。
- 趋势折线图支持周/月/季度按天补零，年视图按月固定 12 点补零。
- 设置页支持默认币种持久化。

## 静态检查

- 无 INTERNET 权限。
- 未发现 Firebase / Retrofit / 广告 SDK / Analytics / WorkManager / Hilt。
- 未新增第三方图表库。
- 未发现 0 字节源码文件。
- 未发现明显 TODO / FIXME。
- NOT VERIFIED：当前环境无法执行 Android 编译，因此 unused import / KSP / Room 查询映射仍需 Gradle 验证。

## 真机验收清单

- NOT VERIFIED：App 是否成功编译。
- NOT VERIFIED：`assembleDebug` 是否成功。
- NOT VERIFIED：Unit Test 是否全部通过。
- NOT VERIFIED：真机首次启动。
- NOT VERIFIED：金额输入键盘是否自动弹出。
- NOT VERIFIED：保存交易。
- NOT VERIFIED：修改交易。
- NOT VERIFIED：删除交易。
- NOT VERIFIED：PHP/CNY/USD 切换。
- NOT VERIFIED：周统计。
- NOT VERIFIED：月统计。
- NOT VERIFIED：季度统计。
- NOT VERIFIED：年统计。
- NOT VERIFIED：饼图显示。
- NOT VERIFIED：折线图显示。
- NOT VERIFIED：无数据状态。
- NOT VERIFIED：横竖屏是否出现明显布局问题。

## 待执行命令

当前机器没有 `gradle` 命令，也没有 `gradlew` wrapper，尚未能执行 Android build 或 unit test。需要在有 Android SDK / Gradle 的环境中运行：

```bash
./gradlew test
./gradlew assembleDebug
```

## 已知问题

- 当前仓库未包含 Gradle Wrapper；需要在具备 Gradle 的环境中补充 wrapper 或直接用系统 Gradle 构建。
- 饼图和折线图为第一版静态图表，无点击高亮、缩放、拖拽或动画。
