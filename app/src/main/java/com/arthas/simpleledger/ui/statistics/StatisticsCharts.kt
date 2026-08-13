package com.arthas.simpleledger.ui.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.arthas.simpleledger.data.CategoryExpenseSummary
import com.arthas.simpleledger.model.CurrencyCode
import com.arthas.simpleledger.util.MoneyFormatter
import com.arthas.simpleledger.util.TrendPoint

private val ChartColors = listOf(
    Color(0xFF2F80ED),
    Color(0xFF27AE60),
    Color(0xFFF2994A),
    Color(0xFFEB5757),
    Color(0xFF9B51E0),
    Color(0xFF00A8A8),
    Color(0xFF8A6F3D),
    Color(0xFF4F4F4F),
    Color(0xFF56CCF2),
    Color(0xFFBB6BD9),
    Color(0xFF6FCF97)
)

@Composable
fun CategoryPieChart(
    rows: List<CategoryExpenseSummary>,
    totalMinor: Long,
    currency: CurrencyCode,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("分类支出占比")
        if (totalMinor <= 0L || rows.isEmpty()) {
            Text("当前范围暂无 ${currency.name} 支出")
            return@Column
        }
        Canvas(modifier = Modifier.fillMaxWidth().height(220.dp)) {
            val diameter = size.minDimension * 0.78f
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            var startAngle = -90f
            rows.forEachIndexed { index, row ->
                val sweep = row.amountMinor.toFloat() / totalMinor.toFloat() * 360f
                drawArc(
                    color = ChartColors[index % ChartColors.size],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = topLeft,
                    size = Size(diameter, diameter)
                )
                startAngle += sweep
            }
        }
        rows.forEachIndexed { index, row ->
            val percent = row.amountMinor.toDouble() * 100.0 / totalMinor.toDouble()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${row.category.label} ${"%.1f".format(percent)}%")
                Text(MoneyFormatter.formatMinor(row.amountMinor, currency, false))
            }
        }
    }
}

@Composable
fun ExpenseLineChart(
    points: List<TrendPoint>,
    currency: CurrencyCode,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title)
        if (points.isEmpty()) {
            Text("当前范围暂无 ${currency.name} 支出")
            return@Column
        }
        val maxAmount = points.maxOfOrNull { it.amountMinor } ?: 0L
        Canvas(modifier = Modifier.fillMaxWidth().height(220.dp)) {
            val left = 44.dp.toPx()
            val right = 12.dp.toPx()
            val top = 16.dp.toPx()
            val bottom = 34.dp.toPx()
            val chartWidth = size.width - left - right
            val chartHeight = size.height - top - bottom
            val axisColor = Color(0xFF9E9E9E)
            val lineColor = Color(0xFF2F80ED)

            drawLine(axisColor, Offset(left, top), Offset(left, top + chartHeight), strokeWidth = 1.dp.toPx())
            drawLine(axisColor, Offset(left, top + chartHeight), Offset(left + chartWidth, top + chartHeight), strokeWidth = 1.dp.toPx())

            val yMax = maxAmount.coerceAtLeast(1L).toFloat()
            val coordinates = points.mapIndexed { index, point ->
                val x = if (points.size == 1) left else left + chartWidth * index / (points.size - 1)
                val y = top + chartHeight - (point.amountMinor.toFloat() / yMax * chartHeight)
                Offset(x, y)
            }

            if (coordinates.size > 1) {
                val path = Path().apply {
                    moveTo(coordinates.first().x, coordinates.first().y)
                    coordinates.drop(1).forEach { lineTo(it.x, it.y) }
                }
                drawPath(path, lineColor, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round))
            }
            coordinates.forEach { point ->
                drawCircle(lineColor, radius = 3.dp.toPx(), center = point)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(points.first().label)
            Text("Max ${MoneyFormatter.formatMinor(maxAmount, currency, false)}")
            Text(points.last().label)
        }
    }
}
