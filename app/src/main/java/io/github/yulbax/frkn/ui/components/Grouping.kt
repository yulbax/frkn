package io.github.yulbax.frkn.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

private val GROUP_CORNER_BIG = 20.dp
private val GROUP_CORNER_SMALL = 6.dp

fun groupItemShape(index: Int, count: Int): Shape {
    val top = if (index == 0) GROUP_CORNER_BIG else GROUP_CORNER_SMALL
    val bottom = if (index == count - 1) GROUP_CORNER_BIG else GROUP_CORNER_SMALL
    return RoundedCornerShape(topStart = top, topEnd = top, bottomStart = bottom, bottomEnd = bottom)
}

fun groupRowShape(index: Int, count: Int): Shape {
    val start = if (index == 0) GROUP_CORNER_BIG else GROUP_CORNER_SMALL
    val end = if (index == count - 1) GROUP_CORNER_BIG else GROUP_CORNER_SMALL
    return RoundedCornerShape(topStart = start, bottomStart = start, topEnd = end, bottomEnd = end)
}
