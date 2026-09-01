package com.findit.app.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findit.app.domain.model.ItemStatus
import com.findit.app.ui.theme.*
import com.findit.app.ui.theme.OutfitFontFamily

data class StatusConfig(
    val background: Color,
    val textColor: Color,
    val dotColor: Color
)

@Composable
fun statusConfig(status: ItemStatus): StatusConfig = when (status) {
    ItemStatus.LOST -> StatusConfig(Red100, Red600, Red500)
    ItemStatus.FOUND -> StatusConfig(Green100, Green600, Green500)
    ItemStatus.PENDING -> StatusConfig(Amber100, Amber600, Amber500)
    ItemStatus.APPROVED -> StatusConfig(Blue100, Blue600, Blue500)
    ItemStatus.REJECTED -> StatusConfig(Gray100, Gray500, Gray400)
    ItemStatus.CLAIMED -> StatusConfig(Violet100, Violet600, Violet500)
    ItemStatus.RESOLVED -> StatusConfig(Emerald100, Emerald600, Emerald500)
}

enum class BadgeSize { XS, SM, MD }

@Composable
fun StatusBadge(
    status: ItemStatus,
    modifier: Modifier = Modifier,
    size: BadgeSize = BadgeSize.SM
) {
    val config = statusConfig(status)
    val (fontSize, horizontalPadding, verticalPadding) = when (size) {
        BadgeSize.XS -> Triple(10.sp, 8.dp, 2.dp)
        BadgeSize.SM -> Triple(12.sp, 10.dp, 4.dp)
        BadgeSize.MD -> Triple(14.sp, 12.dp, 6.dp)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(config.background)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(config.dotColor)
        )
        Text(
            text = status.name,
            color = config.textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            fontFamily = OutfitFontFamily
        )
    }
}
