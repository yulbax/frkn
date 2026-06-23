package io.github.yulbax.frkn.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun CountryFlag(country: String) {
    AsyncImage(
        model = "https://flagcdn.com/w80/${country.lowercase()}.png",
        contentDescription = country,
        modifier = Modifier
            .width(36.dp)
            .height(26.dp)
            .clip(RoundedCornerShape(3.dp))
    )
}
